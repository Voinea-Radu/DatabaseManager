package dev.lightdream.databasemanager.dto;

import dev.lightdream.databasemanager.database.DatabaseManager;

public class QueryConstrains {

    private String query = "";

    @SuppressWarnings({"unused", "StringConcatenationInLoop"})
    public QueryConstrains or(QueryConstrains... queries) {
        if (queries.length == 1) {
            return queries[0];
        }
        if (queries.length > 1) {

            for (int i = 0; i < queries.length - 1; i++) {
                query += queries[i].getFinalQuery() + " OR ";
            }
            query += queries[queries.length - 1].getFinalQuery();
        }


        return this;
    }

    @SuppressWarnings({"unused", "StringConcatenationInLoop"})
    public QueryConstrains and(QueryConstrains... queries) {
        if (queries.length == 1) {
            return queries[0];
        }
        if (queries.length > 1) {

            for (int i = 0; i < queries.length - 1; i++) {
                query += queries[i].getFinalQuery() + " AND ";
            }
            query += queries[queries.length - 1].getFinalQuery();
        }

        return this;
    }

    @SuppressWarnings("unused")
    public QueryConstrains equals(String field, Object value) {
        return set(field + "=" + DatabaseManager.formatQueryArgument(value));
    }

    public QueryConstrains set(String query){
        if(this.query==""){
            this.query = query;
            return this;
        }
        return new QueryConstrains().and(
                this,
                new QueryConstrains().set(query)
        );
    }

    @SuppressWarnings("unused")
    public QueryConstrains bigger(String field, double value) {
        return set(field + ">" + value);
    }

    @SuppressWarnings("unused")
    public QueryConstrains biggerOrEquals(String field, double value) {
        return set(field + ">=" + value);
    }

    @SuppressWarnings("unused")
    public QueryConstrains smaller(String field, double value) {
        return set(field + "<" + value);
    }

    @SuppressWarnings("unused")
    public QueryConstrains smallerOrEquals(String field, double value) {
        return set(field + "<=" + value);
    }

    @SuppressWarnings("unused")
    public QueryConstrains contains(String field, String value) {
        return set(field + " LIKE '%" + value + "%'");
    }

    public String getFinalQuery() {
        return query;
    }


}
