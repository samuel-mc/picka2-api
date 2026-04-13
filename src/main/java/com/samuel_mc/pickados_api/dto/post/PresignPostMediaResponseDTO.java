package com.samuel_mc.pickados_api.dto.post;

public class PresignPostMediaResponseDTO {
    private String uploadUrl;
    private String objectKey;

    public PresignPostMediaResponseDTO() {
    }

    public PresignPostMediaResponseDTO(String uploadUrl, String objectKey) {
        this.uploadUrl = uploadUrl;
        this.objectKey = objectKey;
    }

    public String getUploadUrl() {
        return uploadUrl;
    }

    public void setUploadUrl(String uploadUrl) {
        this.uploadUrl = uploadUrl;
    }

    public String getObjectKey() {
        return objectKey;
    }

    public void setObjectKey(String objectKey) {
        this.objectKey = objectKey;
    }
}
