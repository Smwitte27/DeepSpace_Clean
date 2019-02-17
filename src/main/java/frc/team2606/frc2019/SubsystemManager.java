package frc.team2606.frc2019;

import java.util.ArrayList;
import java.util.List;

import frc.team2606.frc2019.loops.ILooper;
import frc.team2606.frc2019.loops.Loop;
import frc.team2606.frc2019.loops.Looper;
import frc.team2606.frc2019.subsystems.Subsystem;

public class SubsystemManager implements ILooper {

    private final List<Subsystem> allSubsystems;
    private final List<Loop> loops = new ArrayList<>();

    public SubsystemManager(List<Subsystem> subsystems) {
        allSubsystems = subsystems;
    }

    public void outputToSmartDashboard() {
        allSubsystems.forEach((s) -> s.outputTelemetry());
    }

    public void writeToLog() {
        allSubsystems.forEach((s) -> s.writeToLog());
    }

    public void stop() {
        allSubsystems.forEach((s) -> s.stop());
    }

    private class EnabledLoop implements Loop {

        @Override
        public void onStart(double timestamp) {
            for (Loop l : loops) {
                l.onStart(timestamp);
            }
        }

        @Override
        public void onLoop(double timestamp) {
            for (Subsystem s : allSubsystems) {
                s.readPeriodicInputs();
            }
            for (Loop l : loops) {
                l.onLoop(timestamp);
            }
            for (Subsystem s : allSubsystems) {
                s.writePeriodicOutputs();
            }
        }

        @Override
        public void onStop(double timestamp) {
            for (Loop l : loops) {
                l.onStop(timestamp);
            }
        }
    }

    private class DisabledLoop implements Loop {

        @Override
        public void onStart(double timestamp) {

        }

        @Override
        public void onLoop(double timestamp) {
            for (Subsystem s : allSubsystems) {
                s.readPeriodicInputs();
            }
            for (Subsystem s : allSubsystems) {
                s.writePeriodicOutputs();
            }
        }

        @Override
        public void onStop(double timestamp) {

        }
    }

    public void registerEnabledLoops(Looper enabledLooper) {
        allSubsystems.forEach((s) -> s.registerEnabledLoops(this));
        enabledLooper.register(new EnabledLoop());
    }

    public void registerDisabledLoops(Looper disabledLooper) {
        disabledLooper.register(new DisabledLoop());
    }

    @Override
    public void register(Loop loop) {
        loops.add(loop);
    }

}