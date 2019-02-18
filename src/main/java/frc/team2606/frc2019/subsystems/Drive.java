package frc.team2606.frc2019.subsystems;

import com.ctre.phoenix.ErrorCode;
import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.DemandType;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.StatusFrameEnhanced;
import com.ctre.phoenix.motorcontrol.VelocityMeasPeriod;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.ctre.phoenix.motorcontrol.can.VictorSPX;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.team2606.frc2019.Constants;
import frc.team2606.frc2019.loops.Loop;
import frc.team2606.lib.drivers.FactoryMotorController;
import frc.team2606.lib.util.CSVWriter;
import frc.team2606.lib.util.DriveSignal;

public class Drive extends Subsystem {

    private static Drive driveInstance = new Drive();
    private DriveControlState driveControlState;
    private boolean isBrakeMode;
    private PeriodicIO periodicIO;
    private CSVWriter<PeriodicIO> CSVWriter = null;

    // Hardware
    private final TalonSRX leftMaster, rightMaster;
    private final VictorSPX leftSlave, rightSlave;

    private final Loop loop = new Loop() {

        public void onStart(double timestamp) {
            synchronized (Drive.this) {
                setOpenLoop(new DriveSignal(0.05, 0.05));
                setBrakeMode(false);
                startLogging();
            }
        }

        public void onLoop(double timestamp) {
            synchronized (Drive.this) {
                switch (driveControlState) {
                case OPEN_LOOP:
                    break;
                case PATH_FOLLOWING:
                    updatePathFollower();
                    break;
                default:
                    System.out.println("Unexpected drive control state: " + driveControlState);
                    break;
                }
            }
        }

        public void onStop(double timestamp) {
            stop();
            stopLogging();
        }
    };

    private void configureMaster(TalonSRX talon, boolean left) {
        talon.setStatusFramePeriod(StatusFrameEnhanced.Status_2_Feedback0, 5, 100);
        final ErrorCode sensorPresent = talon.configSelectedFeedbackSensor(FeedbackDevice.CTRE_MagEncoder_Relative, 0,
                100); // primary closed-loop, 100 ms timeout
        if (sensorPresent != ErrorCode.OK) {
            DriverStation.reportError("Could not detect " + (left ? "left" : "right") + " encoder: " + sensorPresent,
                    false);
        }
        talon.setInverted(!left);
        talon.setSensorPhase(true);
        talon.enableVoltageCompensation(true);
        talon.configVoltageCompSaturation(12.0, Constants.LONG_CAN_TIMEOUT);
        talon.configVelocityMeasurementPeriod(VelocityMeasPeriod.Period_50Ms, Constants.LONG_CAN_TIMEOUT);
        talon.configVelocityMeasurementWindow(1, Constants.LONG_CAN_TIMEOUT);
        talon.configClosedloopRamp(Constants.DRIVE_RAMP_RATE, Constants.LONG_CAN_TIMEOUT);
        talon.configNeutralDeadband(0.04, 0);
    }

    private Drive() {
        periodicIO = new PeriodicIO();

        leftMaster = FactoryMotorController.createDefaultTalon(Constants.LEFT_MASTER);
        configureMaster(leftMaster, true);

        rightMaster = FactoryMotorController.createDefaultTalon(Constants.RIGHT_MASTER);
        configureMaster(rightMaster, false);

        leftSlave = FactoryMotorController.createPermanentSlaveVictor(Constants.LEFT_SLAVE, Constants.LEFT_MASTER);
        leftSlave.setInverted(false);

        rightSlave =  FactoryMotorController.createPermanentSlaveVictor(Constants.RIGHT_SLAVE, Constants.RIGHT_MASTER);
        rightSlave.setInverted(true);

        setOpenLoop(DriveSignal.NEUTRAL);

        // Force CAN Message
        isBrakeMode = true;
        setBrakeMode(false);
    }

    public static Drive getInstance() {
        return driveInstance;
    }

    private static double rotationsToInches(double rotations) {
        return rotations * (Constants.WHEEL_DIAMETER * Math.PI);
    }

    private static double rpmToInchesPerSecond(double rpm) {
        return rotationsToInches(rpm) / 60;
    }

    private static double inchesToRotations(double inches) {
        return inches / (Constants.WHEEL_DIAMETER * Math.PI);
    }

    private static double inchesPerSecondToRpm(double inches_per_second) {
        return inchesToRotations(inches_per_second) * 60;
    }

    private static double radiansPerSecondToTicksPer100ms(double rad_s) {
        return rad_s / (Math.PI * 2.0) * 4096.0 / 10.0;
    }

    public synchronized void setOpenLoop(DriveSignal signal) {
        if (driveControlState != DriveControlState.OPEN_LOOP) {
            setBrakeMode(false);

            System.out.println("Switching to open loop");
            System.out.println(signal);
            driveControlState = DriveControlState.OPEN_LOOP;
            leftMaster.configNeutralDeadband(0.04, 0);
            rightMaster.configNeutralDeadband(0.04, 0);
        }
        periodicIO.left_demand = signal.getLeft();
        periodicIO.right_demand = signal.getRight();
        periodicIO.left_feedforward = 0.0;
        periodicIO.right_feedforward = 0.0;
    }

    public synchronized void setVelocity(DriveSignal signal, DriveSignal feedForward) {
        //TODO Implement this
    }

    public boolean isBrakeMode() {
        return isBrakeMode;
    }

    public synchronized void setBrakeMode(boolean on) {
        if (isBrakeMode != on) {
            isBrakeMode = on;
            NeutralMode mode = on ? NeutralMode.Brake : NeutralMode.Coast;
            leftMaster.setNeutralMode(mode);
            leftSlave.setNeutralMode(mode);

            rightMaster.setNeutralMode(mode);
            rightSlave.setNeutralMode(mode);
        }
    }

    public synchronized void resetEncoders() {
        leftMaster.setSelectedSensorPosition(0, 0, 0);
        rightMaster.setSelectedSensorPosition(0, 0, 0);
        periodicIO = new PeriodicIO();
    }

    public double getLeftEncoderRotations() {
        return 0;
    }

    public double getRightEncoderRotations() {
        return 0;
    }

    public double getLeftEncoderDistance() {
        return rotationsToInches(getLeftEncoderRotations());
    }

    public double getRightEncoderDistance() {
        return rotationsToInches(getRightEncoderRotations());
    }

    public double getLeftVelocicyNativeUnits() {
        return 0;
    }

    public double getRightVelocityNativeUnits() {
        return 0;
    }

    public double getLeftLinearVelocity() {
        return rotationsToInches(getLeftVelocicyNativeUnits()); // TODO Finish calculation
    }

    public double getRightLinearVelocity() {
        return rotationsToInches(getRightVelocityNativeUnits()); // TODO Finish calculation
    }

    public double getLinearVelocity() {
        return (getLeftLinearVelocity() + getRightLinearVelocity() / 2);
    }

    private void updatePathFollower() {
        if (driveControlState == DriveControlState.PATH_FOLLOWING) {
            final double now = Timer.getFPGATimestamp();
        }
    }


    @Override
    public boolean checkSystem() {
        return false;
    }

    @Override
    public synchronized void stop() {
    setOpenLoop(DriveSignal.NEUTRAL);
    }

    public synchronized void startLogging() {
        if (CSVWriter == null) {
            CSVWriter = new CSVWriter<>("C:/Code/Logs/Drive-Logs.csv", PeriodicIO.class);
        }
    }

    public synchronized void stopLogging() {
        if (CSVWriter != null) {
            CSVWriter.flush();
            CSVWriter = null;
        }
    }


    @Override
    public synchronized void readPeriodicInputs() {
        double prevLeftTicks = periodicIO.left_position_ticks;
        double prevRightTicks = periodicIO.right_position_ticks;
        periodicIO.left_position_ticks = leftMaster.getSelectedSensorPosition(0);
        periodicIO.right_position_ticks = rightMaster.getSelectedSensorPosition(0);
        periodicIO.left_velocity_ticks_per_100ms = leftMaster.getSelectedSensorVelocity(0);
        periodicIO.right_velocity_ticks_per_100ms = rightMaster.getSelectedSensorVelocity(0);

        double deltaLeftTicks = ((periodicIO.left_position_ticks - prevLeftTicks) / 4096.0) * Math.PI;
        if (deltaLeftTicks > 0.0) {
            periodicIO.left_distance += deltaLeftTicks * Constants.WHEEL_DIAMETER;
        } else {
            periodicIO.left_distance += deltaLeftTicks * Constants.WHEEL_DIAMETER;
        }

        double deltaRightTicks = ((periodicIO.right_position_ticks - prevRightTicks) / 4096.0) * Math.PI;
        if (deltaRightTicks > 0.0) {
            periodicIO.right_distance += deltaRightTicks * Constants.WHEEL_DIAMETER;
        } else {
            periodicIO.right_distance += deltaRightTicks * Constants.WHEEL_DIAMETER;
        }

        if (CSVWriter != null) {
            CSVWriter.add(periodicIO);
        }
    }
    

    @Override
    public synchronized void writePeriodicOutputs() {
        if (driveControlState == DriveControlState.OPEN_LOOP) {
            leftMaster.set(ControlMode.PercentOutput, periodicIO.left_demand, DemandType.ArbitraryFeedForward, 0.0);
            rightMaster.set(ControlMode.PercentOutput, periodicIO.right_demand, DemandType.ArbitraryFeedForward, 0.0);
        }
    }

    @Override
    public void outputTelemetry() {
        SmartDashboard.putNumber("Right Drive Distance", periodicIO.right_distance);
        SmartDashboard.putNumber("Right Drive Ticks", periodicIO.right_position_ticks);
        SmartDashboard.putNumber("Left Drive Ticks", periodicIO.left_position_ticks);
        SmartDashboard.putNumber("Left Drive Distance", periodicIO.left_distance);
        SmartDashboard.putNumber("Right Linear Velocity", getRightLinearVelocity());
        SmartDashboard.putNumber("Left Linear Velocity", getLeftLinearVelocity());

        if (CSVWriter != null) {
            CSVWriter.write();
        }
    }


    public static class PeriodicIO {
        // INPUTS
        public int left_position_ticks;
        public int right_position_ticks;
        public double left_distance;
        public double right_distance;
        public int left_velocity_ticks_per_100ms;
        public int right_velocity_ticks_per_100ms;

        // OUTPUTS
        public double left_demand;
        public double right_demand;
        public double left_accel;
        public double right_accel;
        public double left_feedforward;
        public double right_feedforward;
    }

    public enum DriveControlState {
        OPEN_LOOP,
        PATH_FOLLOWING
    }

}