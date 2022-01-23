package dev.lightdream.databasemanager.dto;

import dev.lightdream.libs.fasterxml.annotation.JsonIgnore;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;

@SuppressWarnings("CanBeFinal")
@NoArgsConstructor
public class SQLConfig extends DriverConfig {
    public String driver = "MYSQL";
    public String host = "162.55.135.18";
    public String database = "new_panel";
    public String username = "root";
    @SuppressWarnings("SpellCheckingInspection")
    public String password = "5xbqT7JUaxam7heFeWH577M4QPTTYCTx";
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
