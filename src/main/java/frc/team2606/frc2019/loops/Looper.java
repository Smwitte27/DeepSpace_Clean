package frc.team2606.frc2019.loops;


import java.util.ArrayList;
import java.util.List;

import edu.wpi.first.wpilibj.Notifier;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.team2606.frc2019.Constants;
import frc.team2606.frc2019.subsystems.VisionClient;
import frc.team2606.lib.util.CrashTrackerRunnable;

public class Looper implements ILooper {
    public final double looperPeriod = Constants.LooperDt;

    private boolean running;
    private final Notifier notifier;
    private final Notifier visionThread;
    private final List<Loop> loops;
    private final Object runningLock = new Object();
    private double timestamp = 0;
    private double Dt = 0;

    private final CrashTrackerRunnable runnable = new CrashTrackerRunnable() {
    
        @Override
        public void runCrahTracked() {
            synchronized (runningLock) {
                if (running) {
                    double now = Timer.getFPGATimestamp();

                    for (Loop loop : loops) {
                        loop.onLoop(now);
                    }

                    Dt = now - timestamp;
                    timestamp = now;
                }
            }  
        }
    };

    private final VisionClient client = new VisionClient() {

    };

    public Looper() {
        notifier = new Notifier(runnable);
        visionThread = new Notifier(client);
        running = false;
        loops = new ArrayList<>();
    }

    @Override
    public synchronized void register(Loop loop) {
        synchronized (runningLock) {
            loops.add(loop);
        }
    }
    
    public synchronized void start() {
        if (running == false) {
            System.out.println("Starting Loops");
            notifier.startPeriodic(looperPeriod);
            visionThread.startPeriodic(looperPeriod);
            synchronized (runningLock) {
                timestamp = Timer.getFPGATimestamp();
                running = true;

                for (Loop loop : loops) {
                    System.out.println("Starting " + loop);
                    loop.onStart(timestamp);
                }
            }
        }
    }

    public synchronized void stop() {
        if (running == true) {
            System.out.println("Stopping Loops");
            notifier.stop();
            visionThread.stop();
            synchronized (runningLock) {
                timestamp = Timer.getFPGATimestamp();
                running = false;

                for (Loop loop : loops) {
                    System.out.println("Stopping " + loop);
                    loop.onStop(timestamp);
                }
            }
        }
    }

    public void outToSnartDashboard() {
        SmartDashboard.putNumber("Looper Dt", Dt);
    }

}