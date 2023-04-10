package dev.lightdream.databasemanager.config;

import dev.lightdream.databasemanager.DatabaseMain;
import dev.lightdream.databasemanager.dto.Driver;

@SuppressWarnings("CanBeFinal")
public class SQLConfig {

    public String host = "";
    public String database = "";
    public String username = "";
    public String password = "";
    public int port = 3306;
    public boolean useSSL = false;


}
