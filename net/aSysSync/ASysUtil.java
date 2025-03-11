package net.aSysSync;

public class ASysUtil {
    public static void threadSleep(int milliseconds) {
        try {
            Thread.sleep(milliseconds);

        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
