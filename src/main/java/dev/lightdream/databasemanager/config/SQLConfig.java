package dev.lightdream.databasemanager.config;

import lombok.NoArgsConstructor;

@SuppressWarnings("CanBeFinal")
@NoArgsConstructor
public class SQLConfig {

    public DriverType driverType = DriverType.SQLITE;

    public String host = "127.0.0.1";
    public String database = "database";

    public String username = "user";
    public String password = "passwd";

    public String args = "?useSSL=false&autoReconnect=true";

    public int sessionTimeout = 10;

    public enum DriverType {
        MYSQL,
        SQLITE
    }


}
