package dev.lightdream.databasemanager.dto;

import dev.lightdream.databasemanager.DatabaseMain;

@SuppressWarnings("CanBeFinal")
public class SQLConfig {

    public String driverName = "SQLITE";
    public String host = "";
    public String database = "";
    public String username = "";
    public String password = "";
    public int port = 3306;
    public boolean useSSL = false;

    public SQLConfig() {

    }

    public DriverConfig.Driver driver(DatabaseMain main) {
        switch (driverName) {
            case "MYSQL":
                return main.getDriverConfig().MYSQL;
            case "MARIADB":
                return main.getDriverConfig().MARIADB;
            case "POSTGRESQL":
                return main.getDriverConfig().POSTGRESQL;
            case "SQLSERVER":
                return main.getDriverConfig().SQLSERVER;
            case "H2":
                return main.getDriverConfig().H2;
            case "SQLITE":
                return main.getDriverConfig().SQLITE;
        }
        return null;
    }


}
