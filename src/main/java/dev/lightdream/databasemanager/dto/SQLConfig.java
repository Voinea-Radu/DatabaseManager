package dev.lightdream.databasemanager.dto;

import dev.lightdream.libs.fasterxml.annotation.JsonIgnore;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;

@SuppressWarnings("CanBeFinal")
@NoArgsConstructor
public class SQLConfig extends DriverConfig {
    public String driver = "SQLITE";
    public String host = "";
    public String database = "";
    public String username = "";
    public String password = "";
    public int port = 3306;
    public boolean useSSL = false;

    @SneakyThrows
    @JsonIgnore
    public DriverConfig.Driver getDriver() {
        switch (driver) {
            case "MYSQL":
                return MYSQL;
            case "MARIADB":
                return MARIADB;
            case "POSTGRESQL":
                return POSTGRESQL;
            case "SQLSERVER":
                return SQLSERVER;
            case "H2":
                return H2;
            case "SQLITE":
                return SQLITE;
        }
        return null;
    }


}
