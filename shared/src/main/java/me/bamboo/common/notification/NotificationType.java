package me.bamboo.common.notification;

public enum NotificationType {
    SEARCH_PREFERENCE_HIT("EMAIL");

    private String type;
    
    NotificationType(String type) {
        this.type = type;
    }
}
