package dev.lightdream.databasemanager.dto;

import dev.lightdream.libs.fasterxml.annotation.JsonIgnore;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;

@SuppressWarnings("CanBeFinal")
@NoArgsConstructor
public class SQLConfig {
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
        DriverConfig.Driver driver = new DriverConfig.Driver();
        return (DriverConfig.Driver) DriverConfig.Driver.class.getField(this.driver).get(driver);
    }


}
