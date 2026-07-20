package net.aSysSync;

import java.util.ArrayList;
import java.util.HashMap;

public class ASysThread {

    private final Thread thread;

    private final ArrayList<Runnable> taskList = new ArrayList<>();
    private final HashMap<String, Runnable> loopList = new HashMap<>();

    private volatile boolean active = true;
    private final boolean keep;

    public ASysThread(String threadName) {
        this(threadName, false);
    }
    public ASysThread(String threadName, boolean keep) {
        this.thread = this.process();
        this.thread.start();

        this.keep = keep;

        ASysSync.namedThreads.put(threadName, this);
        ASysSync.threads.add(this);
    }
    public ASysThread() {
        this(false);
    }
    public ASysThread(boolean keep) {
        this.thread = this.process();
        this.thread.start();

        this.keep = keep;

        ASysSync.threads.add(this);
    }

    public synchronized void run(Runnable task) { this.taskList.add(task); }
    public synchronized void loop(String taskName, Runnable task) { this.loopList.put(taskName, task); }
    public synchronized void stopLoop(String taskName) { this.loopList.remove(taskName); }

    public synchronized void mergeInto(ASysThread thread) { this.mergeInto(thread, false); }
    public synchronized void mergeInto(ASysThread thread, boolean forceStop) {
        this.taskList.forEach(thread::run);
        this.loopList.forEach(thread::loop);

        this.stop();
        if (forceStop) this.stop();
    }
    public synchronized void stop() {
        if (this.active) {
            this.active = false;
        } else {
            this.thread.interrupt();
        }
    }

    private Thread process() {
        return new Thread(() -> {
            while (active) {
                while (!taskList.isEmpty()) {
                    this.taskList.removeFirst().run();
                }
                this.loopList.forEach((key, value) -> value.run());
                autoStop();
            }
        });
    }
    private void autoStop() {
        if (keep) return;
        if (this.taskList.isEmpty() && this.loopList.isEmpty()) this.stop();
    }
}
