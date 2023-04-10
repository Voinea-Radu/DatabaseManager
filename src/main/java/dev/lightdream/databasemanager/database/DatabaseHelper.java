package dev.lightdream.databasemanager.database;

import com.google.gson.Gson;
import dev.lightdream.databasemanager.DatabaseMain;
import dev.lightdream.lambda.lambda.ReturnArgLambdaExecutor;
import dev.lightdream.logger.Logger;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class DatabaseHelper {

    private static DatabaseMain main;
    private static final String lineSeparator = ";line_separator;";
    private static final HashMap<Class<?>, ReturnArgLambdaExecutor<?, Object>> serializeMap = new HashMap<>();
    private static final HashMap<Class<?>, ReturnArgLambdaExecutor<?, Object>> deserializeMap = new HashMap<>();

    @Setter
    @Getter
    private static Gson gson = new Gson();

    public static void init(DatabaseMain main){
        DatabaseHelper.main = main;
    }

    public static String formatQueryArgument(Object object) {
        if (object == null) {
            return "NULL";
        }
        Class<?> clazz = object.getClass();

        if (serializeMap.get(clazz) != null) {
            return serializeMap.get(clazz).execute(object).toString();
        } else {
            return gson.toJson(gson.toJson(object));
        }
    }

    public static Object getObject(Class<?> clazz, Object object) {
        if (object == null) {
            return null;
        }

        if (deserializeMap.get(clazz) != null) {
            return deserializeMap.get(clazz).execute(object);
        } else {
            return gson.fromJson(gson.toJson(object), clazz);
        }
    }


    @Deprecated
    public static ArrayList<?> deserializeList(Object object) {
        if (object == null) {
            return null;
        }

        try {
            if (object.toString()
                    .equals("[]")) {
                return new ArrayList<>();
            }
            String[] datas = object.toString()
                    .split(lineSeparator);
            Class<?> clazz = Class.forName(datas[0]);
            ArrayList<Object> lst = new ArrayList<>();
            for (String data : Arrays.asList(datas)
                    .subList(1, datas.length)) {
                lst.add(getObject(clazz, data));
            }
            return lst;
        } catch (Exception e) {
            Logger.error("Malformed data for " + object);
            e.printStackTrace();
            return null;
        }
    }

    @Deprecated
    public static String serializeList(Object object) {
        @SuppressWarnings("unchecked") List<Object> lst = (List<Object>) object;
        StringBuilder o1 = new StringBuilder();
        lst.forEach(entry -> o1.append(formatQueryArgument(entry).replace("\"", ""))
                .append(lineSeparator));
        o1.append(lineSeparator);
        if (o1.toString()
                .equals(lineSeparator)) {
            return "\"[]\"";
        }
        StringBuilder output = new StringBuilder(lst.get(0)
                .getClass()
                .toString()
                .replace("class ", "")).append(lineSeparator)
                .append(o1);
        return ("\"" + output.append("\"")).replace(lineSeparator + lineSeparator, "");
    }

    public static <R> void registerSDPair(Class<R> clazz, ReturnArgLambdaExecutor<?, R> serialize, ReturnArgLambdaExecutor<R, Object> deserialize) {
        serializeMap.put(clazz, (ReturnArgLambdaExecutor<?, Object>) serialize);
        deserializeMap.put(clazz, deserialize);
    }

}
