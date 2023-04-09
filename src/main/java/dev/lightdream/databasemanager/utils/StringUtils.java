package dev.lightdream.databasemanager.utils;

import java.util.List;

public class StringUtils {

    public static String listToString(List<String> list) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < list.size(); i++) {
            String string = list.get(i);
            builder.append(string);

            if (i != list.size() - 1) {
                builder.append(", ");
            }
        }
        return builder.toString().replace(" , ", ", ");
    }

}
