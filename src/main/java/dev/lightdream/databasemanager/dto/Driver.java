package dev.lightdream.databasemanager.dto;

import dev.lightdream.messagebuilder.MessageBuilder;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;

public class Driver {
    // Data Structure
    public HashMap<Class<?>, String> dataTypes;
    // Queries
    private String select;
    private String insert;
    private String createTable;
    private String delete;
    // Keywords
    private @Getter String autoIncrement;
    private String orderDescendant;
    private String orderAscendant;
    private String limit;

    public Driver() {
    }


    public Driver(String select, String insert, String createTable, String delete,
                  HashMap<Class<?>, String> dataTypes,
                  String autoIncrement, String orderDescendant, String orderAscendant, String limit) {
        this.select = select;
        this.insert = insert;
        this.createTable = createTable;
        this.delete = delete;

        this.dataTypes = dataTypes;

        this.autoIncrement = autoIncrement;
        this.orderDescendant = orderDescendant;
        this.orderAscendant = orderAscendant;
        this.limit = limit;
    }

    public Driver(Driver driver) {
        this.select = driver.select;
        this.insert = driver.insert;
        this.createTable = driver.createTable;
        this.delete = driver.delete;

        this.dataTypes = driver.dataTypes;

        this.autoIncrement = driver.autoIncrement;
        this.orderDescendant = driver.orderDescendant;
        this.orderAscendant = driver.orderAscendant;
        this.limit = driver.limit;
    }

    public String select(String table, String condition, OrderBy order, int limit) {
        return select("*", table, condition, order, limit);
    }

    public String select(String table) {
        return select(table, "1", null, -1);
    }

    /**
     * @param fields    The fields to select
     * @param table     The table to select from
     * @param condition The condition to select
     * @param order     The order to select. NULL for no order
     * @param limit     The limit to select. -1 for no limit
     * @return The query
     */
    public String select(@NotNull String fields, @NotNull String table, @NotNull String condition,
                         @Nullable OrderBy order, int limit) {
        String orderString = "";
        String limitString = "";
        if (order != null) {
            orderString = order(order);
        }
        if (limit != -1) {
            limitString = limit(limit);
        }

        return new MessageBuilder(select)
                .parse("fields", fields)
                .parse("table", table)
                .parse("condition", condition)
                .parse("order", orderString)
                .parse("limit", limitString)
                .parse();
    }

    public String limit(int limit) {
        return new MessageBuilder(limit)
                .parse("limit", limit)
                .parse();
    }

    public String order(OrderBy order) {
        String type = order.type == OrderBy.OrderByType.ASCENDANT ? orderAscendant : orderDescendant;

        return new MessageBuilder(type).parse("order", order.field).parse();
    }

    public String createTable(String table, String columns, String keys) {
        return new MessageBuilder(createTable)
                .parse("table", table)
                .parse("columns", columns)
                .parse("keys", keys)
                .parse();
    }

    public String insert(String table, String columns, String values, String update) {
        //
        return new MessageBuilder(insert)
                .parse("table", table)
                .parse("columns", columns)
                .parse("values", values)
                .parse("update", update)
                .parse();
    }

    public String delete(String table, String condition) {
        return new MessageBuilder(delete)
                .parse("table", table)
                .parse("condition", condition)
                .parse();
    }
}