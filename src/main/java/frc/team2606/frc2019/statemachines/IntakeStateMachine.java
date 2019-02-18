package frc.team2606.frc2019.statemachines;

import frc.team2606.frc2019.states.IntakeState;

public class IntakeStateMachine {

    public enum WantedAction {
        WANT_BALL, WANT_MANUAL
    }
    
    private enum SystemState {
        OPEN_LOOP, KEEPING_BALL
    }
    
    private SystemState systemState = SystemState.OPEN_LOOP;
    private IntakeState commandedState = new IntakeState();
    private double currentStateTime = 0;

    private IntakeState.MotorState wantedMotorState = IntakeState.MotorState.IDLE;
    private double wantedPower = 0;

    public synchronized void setWantedMotorState(final IntakeState.MotorState motorState) {
        wantedMotorState = motorState;
    }

    public synchronized void setWantedPower(double power) {
        wantedPower = power;
    }

    public IntakeState update() {
        return commandedState;
    }
}