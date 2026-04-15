package com.samuel_mc.pickados_api.repository.projection;

import java.time.LocalDateTime;

public interface PostTimelineProjection {
    Long getPostId();
    LocalDateTime getEventAt();
    Long getRepostId();
    Long getRepostUserId();
    LocalDateTime getRepostCreatedAt();
}
