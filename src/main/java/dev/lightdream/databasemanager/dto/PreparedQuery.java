package dev.lightdream.databasemanager.dto;

import lombok.AllArgsConstructor;

import java.util.List;

@AllArgsConstructor
public class PreparedQuery {

    public String query;
    public List<Object> values;

}
