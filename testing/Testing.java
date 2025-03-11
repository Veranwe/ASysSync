package testing;

import net.aSysSync.ASysSync;
import net.aSysSync.ASysThread;
import net.aSysSync.ASysUtil;

public class Testing {
    public static void main(String[] args) {
        new ASysThread("Test-1");
        new ASysThread("Test-2");
        new ASysThread("Test-3");

        ASysSync.threads.get("Test-1").run(() -> System.out.println("Hello World!"));
        ASysSync.threads.get("Test-2").loop("Await_Hello", () -> {
            ASysUtil.threadSleep(1000);
            System.out.println("Also Hello!");
        });

        ASysUtil.threadSleep(1000);

        ASysSync.threads.get("Test-1").stop();

        ASysUtil.threadSleep(2000);

        ASysSync.threads.get("Test-2").mergeInto(ASysSync.threads.get("Test-3"));

        ASysUtil.threadSleep(2000);

        ASysSync.threads.get("Test-3").stop();
    }
}
