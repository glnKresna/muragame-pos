package com.muragame.pos.model;

public enum Category {
    RAMEN,
    MINUMAN,
    SNACK;

    public static Category fromString(String category) {
        if (category == null) return RAMEN;
        switch (category.toLowerCase()) {
            case "ramen": return RAMEN;
            case "minuman": return MINUMAN;
            case "snack": return SNACK;
            default: return RAMEN;
        }
    }

    @Override
    public String toString() {
        return name().toLowerCase();
    }

    public String getDisplayName() {
        return name().substring(0, 1).toUpperCase() + name().substring(1).toLowerCase();
    }
}
