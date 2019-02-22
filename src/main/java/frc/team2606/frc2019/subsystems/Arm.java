package frc.team2606.frc2019.subsystems;

import static org.junit.Assume.assumeNoException;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.DemandType;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.StatusFrameEnhanced;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;

import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.team2606.frc2019.Constants;
import frc.team2606.lib.drivers.FactoryMotorController;
import frc.team2606.lib.util.Util;

public class Arm extends Subsystem {

    private static final int ticksPerMotorRotataion = 1000;
    private static final int ticksPerInch = 1000;

    private boolean zeroedSensors = false;

    private static Arm armInstance = new Arm();
    private TalonSRX linearActuator;
    private PeriodicIO periodicIO;
    private ArmControlState armControlState = ArmControlState.OPEN_LOOP;

    private Arm() {
        periodicIO = new PeriodicIO();
        linearActuator = FactoryMotorController.createDefaultTalon(3);

        linearActuator.enableVoltageCompensation(true);
        linearActuator.setStatusFramePeriod(StatusFrameEnhanced.Status_2_Feedback0, 10, 20);
        linearActuator.setStatusFramePeriod(StatusFrameEnhanced.Status_10_MotionMagic, 10, 20);
        linearActuator.overrideSoftLimitsEnable(true);

        linearActuator.configSelectedFeedbackSensor(FeedbackDevice.Analog, 0, Constants.LONG_CAN_TIMEOUT);
        linearActuator.configForwardSoftLimitEnable(true, Constants.LONG_CAN_TIMEOUT);
        linearActuator.configForwardSoftLimitThreshold(Constants.ARM_UPPER_LIMIT, Constants.LONG_CAN_TIMEOUT);
        linearActuator.configReverseSoftLimitEnable(true, Constants.LONG_CAN_TIMEOUT);
        linearActuator.configReverseSoftLimitThreshold(Constants.ARM_LOWER_LIMIT, Constants.LONG_CAN_TIMEOUT);
        linearActuator.configOpenloopRamp(Constants.ARM_RAMP_RATE, Constants.LONG_CAN_TIMEOUT);
        linearActuator.configClosedloopRamp(Constants.ARM_RAMP_RATE, Constants.LONG_CAN_TIMEOUT);

        // PID Config
        linearActuator.config_kP(Constants.PID_CONTROL, Constants.ARM_PID_P, Constants.LONG_CAN_TIMEOUT);
        linearActuator.config_kI(Constants.PID_CONTROL, Constants.ARM_PID_I, Constants.LONG_CAN_TIMEOUT);
        linearActuator.config_kD(Constants.PID_CONTROL, Constants.ARM_PID_D, Constants.LONG_CAN_TIMEOUT);

        linearActuator.selectProfileSlot(0, 0);

        linearActuator.set(ControlMode.PercentOutput, 0);
        setNeutralMode(NeutralMode.Brake);
    }

    public static Arm getInstance() {
        return armInstance;
    }

    //Neutral (No) Control
    public void setNeutralMode(NeutralMode neutralMode) {
        linearActuator.setNeutralMode(neutralMode);
    }

    //Open Loop Control
    public void setOpenLoop(double percentage) {
        armControlState = ArmControlState.OPEN_LOOP;
        periodicIO.Actuator_Demand = percentage;
    }

    // Motion Magin Control
    public synchronized void setMotionMagicPosition(double height) {
        double POTPosition = height * ticksPerInch;
        setClosedLoopPOTPosition(POTPosition);
    }

    // PID Control
    public void setPositionPID(double height) {
        double POTPosition = height * ticksPerInch;
        if (armControlState != ArmControlState.POSITION_PID) {
            armControlState = ArmControlState.POSITION_PID;
            linearActuator.selectProfileSlot(Constants.PID_CONTROL, 0);
        }
        periodicIO.Actuator_Demand = POTPosition;
    }

    // Closed Loop Control
    public void setClosedLoopPOTPosition(double POTPosition) {
        if (armControlState != ArmControlState.MOTION_MAGIC) {
            armControlState = ArmControlState.MOTION_MAGIC;
            linearActuator.selectProfileSlot(0, 0);
        }
        periodicIO.Actuator_Demand = POTPosition;
    }

    //Check for end of Motion Magic Trajectory
    public synchronized boolean hasFinishedTrajectory() {
        return armControlState == ArmControlState.MOTION_MAGIC
                && Util.epsilonEquals(periodicIO.active_trajectory_position, periodicIO.Actuator_Demand, 5);
    }

    public synchronized double getRPM() {
        return periodicIO.velocity_ticks_per_100ms * 10.0 / ticksPerMotorRotataion * 60.0;
    }

    public synchronized double getHeight() {
        return periodicIO.position_ticks / ticksPerInch;
    }

    @Override
    public boolean checkSystem() {
        return false;
    }

    @Override
    public void stop() {
        setOpenLoop(0.0);
    }

    public synchronized void zeroSensors() {
        linearActuator.setSelectedSensorPosition(0, 0, 10);
        zeroedSensors = true;
    }

    public synchronized boolean areZeroed() {
        return zeroedSensors;
    }

    @Override
    public void readPeriodicInputs() {
        final double timer = Timer.getFPGATimestamp();
        periodicIO.position_ticks = linearActuator.getSelectedSensorPosition(0);
        periodicIO.velocity_ticks_per_100ms = linearActuator.getSelectedSensorVelocity(0);

        if (linearActuator.getControlMode() == ControlMode.MotionMagic) {
            // Add motionMagic inputs here
        } else {

        }

    }

    @Override
    public synchronized void writePeriodicOutputs() {
        if (armControlState == ArmControlState.MOTION_MAGIC) {
            linearActuator.set(ControlMode.MotionMagic, periodicIO.Actuator_Demand, DemandType.ArbitraryFeedForward,
                    periodicIO.feedforward);
        } else if (armControlState == ArmControlState.POSITION_PID) {
            linearActuator.set(ControlMode.Position, periodicIO.Actuator_Demand, DemandType.ArbitraryFeedForward,
                    periodicIO.feedforward);
        } else {
            linearActuator.set(ControlMode.PercentOutput, periodicIO.Actuator_Demand, DemandType.ArbitraryFeedForward,
                    periodicIO.feedforward);
        }
    }

    @Override
    public void outputTelemetry() {
        SmartDashboard.putNumber("Arm Output %", periodicIO.output_percent);
        SmartDashboard.putNumber("Arm RPM", getRPM());
        SmartDashboard.putNumber("Arm Current", linearActuator.getOutputCurrent());
        SmartDashboard.putNumber("Arm Height", getHeight());
        SmartDashboard.putBoolean("Arm Limit", periodicIO.limit_switch);
        SmartDashboard.putNumber("Arm Ticks", periodicIO.position_ticks);

        SmartDashboard.putNumber("Arm Last Expected Trajectory", periodicIO.Actuator_Demand);
        SmartDashboard.putNumber("Arm Current Trajectory Point", periodicIO.active_trajectory_position);
        SmartDashboard.putNumber("Arm Traj Vel", periodicIO.active_trajectory_velocity);
        SmartDashboard.putNumber("Arm Traj Accel", periodicIO.active_trajectory_accel_g);
        SmartDashboard.putBoolean("Arm Has Sent Trajectory", hasFinishedTrajectory());
    }

    public static class PeriodicIO {
        // INPUTS
        public int position_ticks;
        public int velocity_ticks_per_100ms;
        public double active_trajectory_accel_g;
        public int active_trajectory_velocity;
        public int active_trajectory_position;
        public double output_percent;
        public boolean limit_switch;
        public double feedforward;

        // OUTPUTS
        public double Actuator_Demand;
    }

    public enum ArmControlState {
        OPEN_LOOP, MOTION_MAGIC, POSITION_PID
    }
}