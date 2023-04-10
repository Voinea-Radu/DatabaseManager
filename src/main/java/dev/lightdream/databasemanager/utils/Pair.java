package dev.lightdream.databasemanager.utils;

import lombok.Getter;

public class Pair<P1, P2> {

    private @Getter P1 first;
    private @Getter P2 second;

    public Pair(P1 first, P2 second) {
        this.first = first;
        this.second = second;
    }

}
