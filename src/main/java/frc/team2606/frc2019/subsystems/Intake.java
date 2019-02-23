package frc.team2606.frc2019.subsystems;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;

import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.team2606.frc2019.Constants;
import frc.team2606.frc2019.loops.ILooper;
import frc.team2606.frc2019.loops.Loop;
import frc.team2606.frc2019.statemachines.IntakeStateMachine;
import frc.team2606.frc2019.states.IntakeState;
import frc.team2606.lib.drivers.FactoryMotorController;
import jdk.jfr.Threshold;

public class Intake extends Subsystem {

    private final static boolean ejectHatch = false;

    private static Intake intakeInstance = new Intake();
    private final Solenoid hatchEject;
    private final TalonSRX intakeMotor;
    private IntakeStateMachine.WantedAction wantedAction = IntakeStateMachine.WantedAction.WANT_MANUAL;
    private IntakeStateMachine stateMachine = new IntakeStateMachine();
    private IntakeState.MotorState motorState;
    private IntakeState currentState = new IntakeState();

    private Intake() {
        hatchEject = new Solenoid(Constants.HATCH_EJECT_SOLINOID);

        intakeMotor = FactoryMotorController.createDefaultTalon(Constants.INTAKE_MOTOR);
        intakeMotor.set(ControlMode.PercentOutput, 0);
        intakeMotor.setInverted(true);
        intakeMotor.configVoltageCompSaturation(12.0, Constants.LONG_CAN_TIMEOUT);
        intakeMotor.enableVoltageCompensation(true);
    }

    public synchronized static Intake getInstance() {
        return intakeInstance;
    }

    public void registerEnabledLoops(ILooper enabledLooper) {
        Loop loop = new Loop() {

            @Override
            public void onStart(double timestamp) {
                if (hasBall()) {
                    wantedAction = IntakeStateMachine.WantedAction.WANT_BALL;
                } else {
                    wantedAction = IntakeStateMachine.WantedAction.WANT_MANUAL;
                }
            }

            @Override
            public void onLoop(double timestamp) {
                synchronized (Intake.this) {
                    IntakeState newState = stateMachine.update(Timer.getFPGATimestamp(), wantedAction, getState());
                }
            }

            @Override
            public void onStop(double timestamp) {
                // kill actions
                wantedAction = IntakeStateMachine.WantedAction.WANT_MANUAL;
                stop();
            }
        };
        enabledLooper.register(loop);
    }

    private IntakeState getState() {
        currentState.ballSensorTriggered = hasBall();
        return currentState;
    }

    public void setMotor(IntakeState.MotorState state) {
        if (motorState == state) {
            return;
        }
        motorState = state;
        switch (motorState) {
        case RUNNING:
            intakeMotor.set(ControlMode.PercentOutput, 0);
        case EJECTING:
            intakeMotor.set(ControlMode.PercentOutput, -0);
        case HOLDING:
            intakeMotor.set(ControlMode.PercentOutput, 0);
        default:
            intakeMotor.set(ControlMode.PercentOutput, 0);
            intakeMotor.setNeutralMode(NeutralMode.Coast);
        }
    }

    public synchronized boolean hasBall() {
        return (intakeMotor.getOutputCurrent() > Constants.INTAKE_CURRENT_THRESHOLD);
    }

    public IntakeState.MotorState getMotorState() {
        return motorState;
    }

    public synchronized void setState() {

    }

    public synchronized void setPower(double power) {

    }

    public synchronized void release(double power) {
        setState();
        setPower(power);
    }

    public synchronized void runIntake() {

    }

    public synchronized void holdBall() {

    }

    public synchronized void releaseBall() {

    }

    public IntakeStateMachine.WantedAction getWantedAction() {
        return wantedAction;
    }

    @Override
    public boolean checkSystem() {
        return false;
    }

    @Override
    public void stop() {

    }

    @Override
    public void outputTelemetry() {
        SmartDashboard.putBoolean("Ball", hasBall());
    }

}