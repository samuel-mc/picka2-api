package com.samuel_mc.pickados_api.service.post;

import com.samuel_mc.pickados_api.config.R2Properties;
import com.samuel_mc.pickados_api.dto.post.PostMediaUploadResponseDTO;
import com.samuel_mc.pickados_api.dto.post.PresignPostMediaResponseDTO;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

import java.time.Duration;
import java.util.Map;
import java.util.UUID;

@Service
public class PostMediaStorageService {

    public static final String KEY_PREFIX = "post-media";

    private static final Map<String, String> EXT_BY_CONTENT_TYPE = Map.of(
            "image/jpeg", "jpg",
            "image/png", "png",
            "image/webp", "webp");

    private final R2Properties r2Properties;
    private final S3Presigner presigner;

    public PostMediaStorageService(R2Properties r2Properties, ObjectProvider<S3Presigner> presignerProvider) {
        this.r2Properties = r2Properties;
        this.presigner = presignerProvider.getIfAvailable();
    }

    public PresignPostMediaResponseDTO presignPut(long userId, String contentType) {
        if (!r2Properties.isEnabled() || presigner == null) {
            throw new IllegalStateException(
                    "Almacenamiento R2 no está configurado. Activa r2.enabled y las credenciales en application.properties.");
        }
        String ext = EXT_BY_CONTENT_TYPE.get(contentType);
        if (ext == null) {
            throw new IllegalArgumentException("Tipo de contenido no permitido para posts");
        }
        String objectKey = KEY_PREFIX + "/" + userId + "/" + UUID.randomUUID() + "." + ext;

        PutObjectRequest put = PutObjectRequest.builder()
                .bucket(r2Properties.getBucket())
                .key(objectKey)
                .contentType(contentType)
                .build();

        PutObjectPresignRequest presignRequest = PutObjectPresignRequest.builder()
                .signatureDuration(Duration.ofMinutes(15))
                .putObjectRequest(put)
                .build();

        String uploadUrl = presigner.presignPutObject(presignRequest).url().toString();
        return new PresignPostMediaResponseDTO(uploadUrl, objectKey);
    }

    public boolean isKeyOwnedByUser(long userId, String objectKey) {
        if (objectKey == null || objectKey.isBlank() || objectKey.length() > 1024) {
            return false;
        }
        if (objectKey.contains("..") || objectKey.startsWith("/")) {
            return false;
        }
        String prefix = KEY_PREFIX + "/" + userId + "/";
        return objectKey.startsWith(prefix);
    }

    public PostMediaUploadResponseDTO completeUpload(long userId, String objectKey) {
        if (!isKeyOwnedByUser(userId, objectKey)) {
            throw new IllegalArgumentException("Clave de objeto no válida para este usuario");
        }
        return new PostMediaUploadResponseDTO(objectKey, resolvePublicUrl(objectKey));
    }

    public String resolvePublicUrl(String storedValue) {
        if (storedValue == null || storedValue.isBlank()) {
            return null;
        }
        if (storedValue.startsWith("http://") || storedValue.startsWith("https://")) {
            return storedValue;
        }
        String base = r2Properties.getPublicBaseUrl();
        if (base == null || base.isBlank()) {
            return storedValue;
        }
        return base.replaceAll("/$", "") + "/" + storedValue;
    }
}
