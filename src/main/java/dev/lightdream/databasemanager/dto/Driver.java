package dev.lightdream.databasemanager.dto;

import dev.lightdream.databasemanager.annotations.database.DatabaseTable;
import dev.lightdream.databasemanager.utils.DatabaseProcessor;
import dev.lightdream.databasemanager.utils.ListUtils;
import dev.lightdream.databasemanager.utils.StringUtils;
import dev.lightdream.messagebuilder.MessageBuilder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;

@NoArgsConstructor
public class Driver {
    // Data Structure
    public HashMap<Class<?>, String> dataTypes;
    // Queries
    private MessageBuilder select;
    private MessageBuilder insert;
    private MessageBuilder createTable;
    private MessageBuilder delete;
    // Keywords
    private @Getter MessageBuilder autoIncrement;
    private MessageBuilder orderDescendant;
    private MessageBuilder orderAscendant;
    private MessageBuilder limit;

    public Driver(MessageBuilder select, MessageBuilder insert, MessageBuilder createTable, MessageBuilder delete,
                  MessageBuilder autoIncrement, MessageBuilder orderDescendant, MessageBuilder orderAscendant,
                  MessageBuilder limit, HashMap<Class<?>, String> dataTypes) {
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

    /**
     * @param table     The table to select from
     * @param condition The condition to select
     * @param order     The order to select. NULL for no order
     * @param limit     The limit to select. -1 for no limit
     * @return The query
     */
    public String select(String table, String condition, OrderBy order, int limit) {
        return select("*", table, condition, order, limit);
    }

    /**
     * @param table The table to select from
     * @return The query
     */
    public String select(String table) {
        return select(table, "1", null, -1);
    }

    /**
     * @param fields    The fields to select
     * @param table     The table to select from
     * @param condition The condition to select
     * @param order     The order to select. NULL for no order
     * @param limit     The limit to select. 0 for no limit
     * @return The query
     */
    public String select(@NotNull String fields, @NotNull String table, @NotNull String condition,
                         @Nullable OrderBy order, int limit) {
        return this.select
                .parse("fields", fields)
                .parse("table", table)
                .parse("condition", condition)
                .parse("order", order(order))
                .parse("limit", limit(limit))
                .parse();
    }

    /**
     * @param limit The limit to select
     * @return The query
     */
    public String limit(int limit) {
        if (limit <= 0) {
            return "";
        }

        return this.limit
                .parse("limit", limit)
                .parse();
    }

    /**
     * @param order The order to select. Null for no order
     * @return The query
     */
    public String order(@Nullable OrderBy order) {
        if (order == null) {
            return "";
        }

        MessageBuilder type = order.type == OrderBy.OrderByType.ASCENDANT ?
                this.orderAscendant :
                this.orderDescendant;

        return type
                .parse("order", order.field)
                .parse();
    }

    /**
     * @param table   The table to create
     * @param columns The columns to create
     * @param keys    The keys to create
     * @return The query
     */
    public String createTable(String table, String columns, String keys) {
        return this.createTable
                .parse("table", table)
                .parse("columns", columns)
                .parse("keys", keys)
                .parse();
    }

    /**
     * @param processor The database processor
     * @param table     The table to create
     * @param clazz     The class to create
     * @return The query
     */
    public String createTable(DatabaseProcessor processor, DatabaseTable table, Class<? extends IDatabaseEntry> clazz) {
        String name = table.name();
        String columns = StringUtils.listToString(processor.getFieldsWithProperty(clazz));
        String keys = "";


        return createTable(name, columns, keys);
    }

    /**
     * @param table   The table to insert
     * @param columns The columns to insert
     * @param values  The values to insert
     * @param update  The update to insert
     * @param key     The key to insert
     * @return The query
     */
    public String insert(String table, String columns, String values, String update, String key) {
        return this.insert
                .parse("table", table)
                .parse("columns", columns)
                .parse("values", values)
                .parse("update", update)
                .parse("key", key)
                .parse();
    }

    public PreparedQuery insert(DatabaseProcessor processor, IDatabaseEntry entry, DatabaseTable table) {
        String tableName = table.name();

        List<String> columns = processor.getFields(entry.getClass());
        List<Object> values = processor.getValues(entry);
        List<String> updates = ListUtils.mergeLists(
                columns,
                ListUtils.createQuestionList(columns.size()),
                "="
        );

        String columnsString = StringUtils.listToString(columns);
        String valuesString = StringUtils.listToString(ListUtils.createQuestionList(columns.size()));
        String update = StringUtils.listToString(updates);
        String key = processor.getKey(entry.getClass());

        // INSERT INTO table (columns) VALUES (?, ?, ?) ON CONFLICT(key) DO UPDATE SET update

        List<Object> argumentValue = ListUtils.concatenateLists(values, values);

        return new PreparedQuery(
                insert(tableName, columnsString, valuesString, update, key),
                argumentValue
        );
    }

    public String delete(String table, String condition) {
        return this.delete
                .parse("table", table)
                .parse("condition", condition)
                .parse();
    }
}