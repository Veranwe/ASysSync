package net.aSysSync;

import net.boxes.BoxList;
import net.boxes.BoxMap;

public class ASysThread {

    private final String threadName;
    private final Thread thread;

    private final BoxList<Runnable> taskList = new BoxList<>();
    private final BoxMap<String, Runnable> loopList = new BoxMap<>();

    private volatile boolean active = true;

    public ASysThread(String threadName) {
        this.threadName = threadName;
        this.thread = this.process();
        this.thread.start();

        ASysSync.threads.put(threadName, this);
    }

    public synchronized void run(Runnable task) { this.taskList.add(task); }
    public synchronized void loop(String taskName, Runnable task) { this.loopList.put(taskName, task); }
    public synchronized void stopLoop(String taskName) { this.loopList.remove(taskName); }

    public synchronized void mergeInto(ASysThread thread) { this.mergeInto(thread, false); }
    public synchronized void mergeInto(ASysThread thread, boolean forceStop) {
        while (!taskList.isEmpty()) {
            thread.run(taskList.removeFirst());
        }
        BoxList<BoxMap.BoxEntry<String, Runnable>> loopEntries = loopList.getEntries();
        while (!loopEntries.isEmpty()) {
            BoxMap.BoxEntry<String, Runnable> entry = loopEntries.removeFirst();

            thread.loop(entry.key, entry.value);
        }

        this.stop();
        if (forceStop) this.stop();
    }
    public synchronized void stop() {
        if (this.active) {
            this.active = false;
            ASysSync.threads.remove(this.threadName);
        } else {
            thread.interrupt();
        }
    }

    private Thread process() {
        return new Thread(() -> {
            while (active) {
                while (!taskList.isEmpty()) {
                    taskList.removeFirst().run();
                }

                for (Object task : loopList.entries.objects) {
                    ((BoxMap.BoxEntry<String, Runnable>) task).value.run();
                }
            }
        });
    }
}
