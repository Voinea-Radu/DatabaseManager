package dev.lightdream.databasemanager.dto;

import dev.lightdream.databasemanager.DatabaseMain;

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

    public String parse(DatabaseMain main){
        return parse(main.getSqlConfig().driver(main));
    }

    public String parse(DriverConfig.Driver driver){
        return type == OrderBy.OrderByType.ASCENDANT ?
                driver.orderAsc.replace("%order%", field) :
                driver.orderDesc.replace("%order%", field);
    }


}

