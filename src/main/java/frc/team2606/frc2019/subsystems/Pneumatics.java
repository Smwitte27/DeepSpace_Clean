package frc.team2606.frc2019.subsystems;

import edu.wpi.first.wpilibj.Compressor;
import frc.team2606.frc2019.loops.ILooper;
import frc.team2606.frc2019.loops.Loop;
import frc.team2606.frc2019.statemachines.IntakeStateMachine;
import frc.team2606.frc2019.statemachines.SuperstructureStateMachine;

public class Pneumatics extends Subsystem {
    private static Pneumatics pneumaticsInstance = new Pneumatics();
    private Superstructure superstructure;
    private Intake intake;
    private Compressor compressor;

    private boolean isDuringAuto = false;

    private Pneumatics() {
        compressor = new Compressor();
        compressor.start();

        superstructure = Superstructure.getInstance();
        intake = Intake.getInstance();
    }

    public static Pneumatics getInstance() {
        return pneumaticsInstance;
    }

    @Override
    public boolean checkSystem() {
        return false;
    }

    @Override
    public void outputTelemetry() {

    }

    @Override
    public void stop() {

    }

    private void startCompressor() {
        compressor.start();
    }

    private void stopCompressor() {
        compressor.stop();
    }

    public synchronized void setIsDuringAuto(boolean duringAuto) {
        isDuringAuto = duringAuto;
        if (isDuringAuto)
            stopCompressor();
    }

    public synchronized boolean isDuringAuto() {
        return isDuringAuto;
    }
    

    @Override
    public void registerEnabledLoops(ILooper enabledLooper) {
        enabledLooper.register(new Loop() {
            @Override
            public void onStart(double timestamp) {

            }

            @Override
            public void onLoop(double timestamp) {
                synchronized (Pneumatics.this) {
                    boolean isArmMoving = superstructure
                            .getSuperStructureState() == SuperstructureStateMachine.SystemState.MOVING_TO_POSITION;
                    boolean isIntaking = intake.getWantedAction() == IntakeStateMachine.WantedAction.WANT_BALL
                            && !intake.hasBall();
                    if (isArmMoving || isDuringAuto || isIntaking) {
                        stopCompressor();
                    } else {
                        startCompressor();
                    }
                }
            }

            @Override
            public void onStop(double timestamp) {

            }
        });
    }
}