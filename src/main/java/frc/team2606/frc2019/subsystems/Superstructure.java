package frc.team2606.frc2019.subsystems;

import frc.team2606.frc2019.loops.ILooper;
import frc.team2606.frc2019.loops.Loop;
import frc.team2606.frc2019.statemachines.SuperstructureStateMachine;

public class Superstructure extends Subsystem {

    static Superstructure structureInstance = new Superstructure();
    private Arm arm = Arm.getInstance();
    private Intake intake = Intake.getInstance();
    private SuperstructureStateMachine stateMachine = new SuperstructureStateMachine();

    public synchronized static Superstructure getInstance() {
        return structureInstance;
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

    public synchronized SuperstructureStateMachine.SystemState getSuperStructureState() {
        return stateMachine.getSystemState();
    }

    @Override
    public void registerEnabledLoops(ILooper enabledLooper) {
        enabledLooper.register(new Loop() {

            @Override
            public void onStart(double timestamp) {
                stateMachine.resetManual();
                stateMachine.setUpwardsSubcommandEnable(!Pneumatics.getInstance().isDuringAuto());
            }

            @Override
            public void onLoop(double timestamp) {
                synchronized (Superstructure.this) {
                }
            }

            @Override
            public void onStop(double timestamp) {

            }
        });
    }

}