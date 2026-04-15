package com.samuel_mc.pickados_api.dto.notification;

import java.util.ArrayList;
import java.util.List;

public class NotificationListResponseDTO {
    private long unreadCount;
    private List<NotificationItemResponseDTO> items = new ArrayList<>();

    public long getUnreadCount() {
        return unreadCount;
    }

    public void setUnreadCount(long unreadCount) {
        this.unreadCount = unreadCount;
    }

    public List<NotificationItemResponseDTO> getItems() {
        return items;
    }

    public void setItems(List<NotificationItemResponseDTO> items) {
        this.items = items;
    }
}
