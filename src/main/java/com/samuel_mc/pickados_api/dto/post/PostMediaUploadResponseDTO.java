package com.samuel_mc.pickados_api.dto.post;

public class PostMediaUploadResponseDTO {
    private String objectKey;
    private String mediaUrl;

    public PostMediaUploadResponseDTO() {
    }

    public PostMediaUploadResponseDTO(String objectKey, String mediaUrl) {
        this.objectKey = objectKey;
        this.mediaUrl = mediaUrl;
    }

    public String getObjectKey() {
        return objectKey;
    }

    public void setObjectKey(String objectKey) {
        this.objectKey = objectKey;
    }

    public String getMediaUrl() {
        return mediaUrl;
    }

    public void setMediaUrl(String mediaUrl) {
        this.mediaUrl = mediaUrl;
    }
}
