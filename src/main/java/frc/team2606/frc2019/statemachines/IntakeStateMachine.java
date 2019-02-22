package frc.team2606.frc2019.statemachines;

import frc.team2606.frc2019.states.IntakeState;

public class IntakeStateMachine {

    public enum WantedAction {
        WANT_BALL, WANT_MANUAL
    }

    private enum SystemState {
        OPEN_LOOP, HOLDING_BALL
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

    public IntakeState update(double timeStamp, WantedAction wantedAction, IntakeState currentState) {
        synchronized (IntakeStateMachine.this) {
            SystemState newState;
            double durration = timeStamp - currentStateTime;

            // State Transitions
            switch (systemState) {
            case OPEN_LOOP:
                newState = handleOpenLooopTrasition(wantedAction);
            case HOLDING_BALL:
                newState = handleHoldingTransition(wantedAction);
            default:
                newState = systemState;
            }

            if (newState != systemState) {
                // print changed?
                systemState = newState;
                currentStateTime = timeStamp;
            }

            // State Outputs
            switch (systemState) {
            case OPEN_LOOP:
                getOpenLoopCommanded(commandedState);
            case HOLDING_BALL:
                getHoldingCommanded(currentState, commandedState);
            default:
                getOpenLoopCommanded(commandedState);
            }
        }
        return commandedState;
    }

    // Transitions
    private synchronized SystemState handleOpenLooopTrasition(WantedAction wantedAction) {
        switch (wantedAction) {
        case WANT_BALL:
            return SystemState.HOLDING_BALL;
        default:
            return SystemState.OPEN_LOOP;
        }
    }

    private synchronized SystemState handleHoldingTransition(WantedAction wantedAction) {
        switch (wantedAction) {
        case WANT_MANUAL:
            return SystemState.OPEN_LOOP;
        default:
            return SystemState.HOLDING_BALL;
        }
    }

    // Commands
    private synchronized void getOpenLoopCommanded(IntakeState commandedState) {
        commandedState.setPower(wantedPower);
    }

    private synchronized void getHoldingCommanded(IntakeState currentState, IntakeState commandedState) {
        commandedState.setPower(1); // variable this to intake speed

        boolean hasBall = currentState.hasBall();

        if (hasBall) {
            commandedState.setPower(1); // variable this to small holding value
        } else {
            commandedState.setPower(1); // variable this to intake speed
        }
    }
}