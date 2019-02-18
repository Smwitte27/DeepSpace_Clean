package frc.team2606.frc2019.statemachines;

public class SuperstructureStateMachine {
    public enum WantedAction {
        IDLE, GO_TO_POSITION, WANT_MANUAL
    }
    
    public enum SystemState {
        HOLDING_POSITION, MOVING_TO_POSITION, MANUAL
    }
    
    private SystemState systemState = SystemState.HOLDING_POSITION;

    public void resetManual() {
    }
    
    public synchronized void setUpwardsSubcommandEnable(boolean enabled) {
    }

    public synchronized SystemState getSystemState() {
        return systemState;
    }
}