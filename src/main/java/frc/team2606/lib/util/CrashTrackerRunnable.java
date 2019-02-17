package frc.team2606.lib.util;

/**
 * Class that runs a crash tracking system that catches crash throwables
 */
public abstract class CrashTrackerRunnable implements Runnable {

    @Override
    public final void run() {
        try {
            runCrahTracked();
        } catch (Throwable t) {
            throw t;
        }
    }

    public abstract void runCrahTracked();
}