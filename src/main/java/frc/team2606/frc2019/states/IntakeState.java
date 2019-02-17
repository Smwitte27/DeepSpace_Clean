package frc.team2606.frc2019.states;

public class IntakeState {
    public enum IntakeStates {
        IDLE, RUNNING, EJECTING, HOLDING
    }
    
    public IntakeStates intakeState = IntakeStates.IDLE;
    public double intakeMotor = 0;

    public boolean ballSensorTriggered = false;
}