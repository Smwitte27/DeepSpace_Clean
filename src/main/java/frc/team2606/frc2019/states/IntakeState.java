package frc.team2606.frc2019.states;

public class IntakeState {
    public enum MotorState {
        IDLE, RUNNING, EJECTING, HOLDING
    }
    
    public MotorState intakeState = MotorState.IDLE;
    public double intakeMotor = 0;

    public boolean ballSensorTriggered = false; //current draw sensing

    public void setPower(double power) {
        intakeMotor = power;
    }

    public boolean hasBall() {
        return ballSensorTriggered;
    }
 }