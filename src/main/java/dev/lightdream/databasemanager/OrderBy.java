package dev.lightdream.databasemanager;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class OrderBy {

    public OrderByType type;
    public String field;

    public static OrderBy ASCENDANT(String field) {
        return new OrderBy(OrderByType.ASCENDANT, field);
    }

    public static OrderBy DESCENDENT(String field) {
        return new OrderBy(OrderByType.DESCENDENT, field);
    }

    public enum OrderByType {
        ASCENDANT, DESCENDENT
    }


}

