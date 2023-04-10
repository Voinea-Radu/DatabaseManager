package dev.lightdream.databasemanager.utils;

import java.util.ArrayList;
import java.util.List;

public class ListUtils {

    public static List<String> mergeLists(List<String> l1, List<String> l2, String mergeChar) {
        List<String> output = new ArrayList<>();

        if (l1.size() != l2.size()) {
            return new ArrayList<>();
        }

        for (int i = 0; i < l1.size(); i++) {
            output.add(l1.get(i) + mergeChar + l2.get(i));
        }

        return output;
    }

    public static List<String> createQuestionList(int size) {
        List<String> output = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            output.add("?");
        }
        return output;
    }

    public static <T> List<T> concatenateLists(List<T>... lists) {
        List<T> output = new ArrayList<>();
        for (List<T> list : lists) {
            output.addAll(list);
        }
        return output;
    }

}
