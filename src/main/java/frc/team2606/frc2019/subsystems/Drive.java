package frc.team2606.frc2019.subsystems;

import com.ctre.phoenix.ErrorCode;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.StatusFrameEnhanced;
import com.ctre.phoenix.motorcontrol.VelocityMeasPeriod;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.ctre.phoenix.motorcontrol.can.VictorSPX;

import edu.wpi.first.wpilibj.DriverStation;
import frc.team2606.frc2019.Constants;
import frc.team2606.frc2019.loops.Loop;
import frc.team2606.lib.util.CSVWriter;
import frc.team2606.lib.util.DriveSignal;

public class Drive extends Subsystem {

    private static Drive driveInstance = new Drive();
    private DriveControlState driveControlState;
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
                    System.out.println("Unexpected drive control state: " + mDriveControlState);
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

}