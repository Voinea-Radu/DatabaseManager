package dev.lightdream.databasemanager.dto;

public class OrderBy {

    public OrderByType type;
    public String field;

    public OrderBy(OrderByType type, String field) {
        this.type = type;
        this.field = field;
    }

    @SuppressWarnings("unused")
    public static OrderBy ASCENDANT(String field) {
        return new OrderBy(OrderByType.ASCENDANT, field);
    }

    @SuppressWarnings("unused")
    public static OrderBy DESCENDENT(String field) {
        return new OrderBy(OrderByType.DESCENDENT, field);
    }

    public enum OrderByType {
        ASCENDANT, DESCENDENT
    }


}

