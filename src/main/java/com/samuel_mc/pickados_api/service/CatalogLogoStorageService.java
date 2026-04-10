package com.samuel_mc.pickados_api.service;

import com.samuel_mc.pickados_api.config.R2Properties;
import com.samuel_mc.pickados_api.dto.catalog.PresignCatalogLogoResponseDTO;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

import java.time.Duration;
import java.util.Map;
import java.util.UUID;

@Service
public class CatalogLogoStorageService {

    private static final String KEY_PREFIX = "catalog-logos";

    private static final Map<String, String> EXT_BY_CONTENT_TYPE = Map.of(
            "image/jpeg", "jpg",
            "image/png", "png",
            "image/webp", "webp");

    private final R2Properties r2Properties;
    private final S3Presigner presigner;

    public CatalogLogoStorageService(R2Properties r2Properties, ObjectProvider<S3Presigner> presignerProvider) {
        this.r2Properties = r2Properties;
        this.presigner = presignerProvider.getIfAvailable();
    }

    public PresignCatalogLogoResponseDTO presignPut(String catalogType, Long entityId, String contentType) {
        if (!r2Properties.isEnabled() || presigner == null) {
            throw new IllegalStateException(
                    "Almacenamiento R2 no está configurado. Activa r2.enabled y las credenciales en application.properties.");
        }
        String ext = EXT_BY_CONTENT_TYPE.get(contentType);
        if (ext == null) {
            throw new IllegalArgumentException("Tipo de contenido no permitido para logo");
        }

        String objectKey = KEY_PREFIX + "/" + catalogType + "/" + entityId + "/" + UUID.randomUUID() + "." + ext;

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
        return new PresignCatalogLogoResponseDTO(uploadUrl, objectKey);
    }

    public boolean isOwnedByCatalogEntity(String catalogType, Long entityId, String objectKey) {
        if (objectKey == null || objectKey.isBlank() || objectKey.length() > 512) {
            return false;
        }
        if (objectKey.contains("..") || objectKey.startsWith("/")) {
            return false;
        }
        String prefix = KEY_PREFIX + "/" + catalogType + "/" + entityId + "/";
        return objectKey.startsWith(prefix);
    }

    public String resolvePublicUrl(String stored) {
        if (stored == null || stored.isBlank()) {
            return null;
        }
        if (stored.startsWith("http://") || stored.startsWith("https://")) {
            return stored;
        }
        String base = r2Properties.getPublicBaseUrl();
        if (base == null || base.isBlank()) {
            return null;
        }
        return base.replaceAll("/$", "") + "/" + stored;
    }
}
