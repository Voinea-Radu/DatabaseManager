package dev.lightdream.databasemanager.config;

import lombok.NoArgsConstructor;

import java.util.HashMap;

@SuppressWarnings("CanBeFinal")
@NoArgsConstructor
public class SQLConfig {

    public DriverType driverType = DriverType.SQLITE;

    public String host = "127.0.0.1";
    public String database = "database";

    public String username = "user";
    public String password = "passwd";

    public String args = "?useSSL=false&autoReconnect=true";
    public HashMap<String, String> hibernateOptions = new HashMap<>(){{
        put("hibernate.hbm2ddl.auto", "update");
    }};

    public int sessionTimeout = 10;

    public enum DriverType {
        MYSQL,
        SQLITE
    }


}
