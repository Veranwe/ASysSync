package net.aSysSync;

import net.boxes.BoxMap;

public class ASysSync {

    public static final BoxMap<String, ASysThread> threads = new BoxMap<>();

    static {
        new ASysThread("Main", Thread.currentThread());
    }
}
