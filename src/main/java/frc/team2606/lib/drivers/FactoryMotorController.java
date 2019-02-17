package frc.team2606.lib.drivers;

import com.ctre.phoenix.ParamEnum;
import com.ctre.phoenix.motorcontrol.ControlFrame;
import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.LimitSwitchNormal;
import com.ctre.phoenix.motorcontrol.LimitSwitchSource;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.StatusFrame;
import com.ctre.phoenix.motorcontrol.StatusFrameEnhanced;
import com.ctre.phoenix.motorcontrol.VelocityMeasPeriod;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.ctre.phoenix.motorcontrol.can.VictorSPX;

public class FactoryMotorController {

    private final static int timeoutMs = 100;

    public static class Configuration {

        public NeutralMode NEUTRAL_MODE = NeutralMode.Coast;
        // This is factory default.
        public double NEUTRAL_DEADBAND = 0.04;

        public boolean ENABLE_CURRENT_LIMIT = false;
        public boolean ENABLE_SOFT_LIMIT = false;
        public boolean ENABLE_LIMIT_SWITCH = false;
        public int FORWARD_SOFT_LIMIT = 0;
        public int REVERSE_SOFT_LIMIT = 0;

        public boolean INVERTED = false;
        public boolean SENSOR_PHASE = false;

        public int CONTROL_FRAME_PERIOD_MS = 5;
        public int MOTION_CONTROL_FRAME_PERIOD_MS = 100;
        public int GENERAL_STATUS_FRAME_RATE_MS = 5;
        public int FEEDBACK_STATUS_FRAME_RATE_MS = 100;
        public int QUAD_ENCODER_STATUS_FRAME_RATE_MS = 100;
        public int ANALOG_TEMP_VBAT_STATUS_FRAME_RATE_MS = 100;
        public int PULSE_WIDTH_STATUS_FRAME_RATE_MS = 100;

        public VelocityMeasPeriod VELOCITY_MEASUREMENT_PERIOD = VelocityMeasPeriod.Period_100Ms;
        public int VELOCITY_MEASUREMENT_ROLLING_AVERAGE_WINDOW = 64;

        public double OPEN_LOOP_RAMP_RATE = 1.0;
        public double CLOSED_LOOP_RAMP_RATE = 1.0;
    }

    private static final Configuration defaultConfiguration = new Configuration();
    private static final Configuration slaveConfiguration = new Configuration();

    static {
        slaveConfiguration.CONTROL_FRAME_PERIOD_MS = 100;
        slaveConfiguration.MOTION_CONTROL_FRAME_PERIOD_MS = 1000;
        slaveConfiguration.GENERAL_STATUS_FRAME_RATE_MS = 1000;
        slaveConfiguration.FEEDBACK_STATUS_FRAME_RATE_MS = 1000;
        slaveConfiguration.QUAD_ENCODER_STATUS_FRAME_RATE_MS = 1000;
        slaveConfiguration.ANALOG_TEMP_VBAT_STATUS_FRAME_RATE_MS = 1000;
        slaveConfiguration.PULSE_WIDTH_STATUS_FRAME_RATE_MS = 1000;
    }

    public static TalonSRX createTalon(int id, Configuration config) {
        TalonSRX talon = new LazyTalonSRX(id);
        talon.set(ControlMode.PercentOutput, 0.0);

        talon.changeMotionControlFramePeriod(config.MOTION_CONTROL_FRAME_PERIOD_MS);
        talon.clearMotionProfileHasUnderrun(timeoutMs);
        talon.clearMotionProfileTrajectories();

        talon.clearStickyFaults(timeoutMs);

        talon.configForwardLimitSwitchSource(LimitSwitchSource.FeedbackConnector, LimitSwitchNormal.NormallyOpen,
                timeoutMs);
        talon.configReverseLimitSwitchSource(LimitSwitchSource.FeedbackConnector, LimitSwitchNormal.NormallyOpen,
                timeoutMs);
        talon.overrideLimitSwitchesEnable(config.ENABLE_LIMIT_SWITCH);

        // Turn off re-zeroing by default.
        talon.configSetParameter(ParamEnum.eClearPositionOnLimitF, 0, 0, 0, timeoutMs);
        talon.configSetParameter(ParamEnum.eClearPositionOnLimitR, 0, 0, 0, timeoutMs);

        talon.configNominalOutputForward(0, timeoutMs);
        talon.configNominalOutputReverse(0, timeoutMs);
        talon.configNeutralDeadband(config.NEUTRAL_DEADBAND, timeoutMs);

        talon.configPeakOutputForward(1.0, timeoutMs);
        talon.configPeakOutputReverse(-1.0, timeoutMs);

        talon.setNeutralMode(config.NEUTRAL_MODE);

        talon.configForwardSoftLimitThreshold(config.FORWARD_SOFT_LIMIT, timeoutMs);
        talon.configForwardSoftLimitEnable(config.ENABLE_SOFT_LIMIT, timeoutMs);

        talon.configReverseSoftLimitThreshold(config.REVERSE_SOFT_LIMIT, timeoutMs);
        talon.configReverseSoftLimitEnable(config.ENABLE_SOFT_LIMIT, timeoutMs);
        talon.overrideSoftLimitsEnable(config.ENABLE_SOFT_LIMIT);

        talon.setInverted(config.INVERTED);
        talon.setSensorPhase(config.SENSOR_PHASE);

        talon.selectProfileSlot(0, 0);

        talon.configVelocityMeasurementPeriod(config.VELOCITY_MEASUREMENT_PERIOD, timeoutMs);
        talon.configVelocityMeasurementWindow(config.VELOCITY_MEASUREMENT_ROLLING_AVERAGE_WINDOW, timeoutMs);

        talon.configOpenloopRamp(config.OPEN_LOOP_RAMP_RATE, timeoutMs);
        talon.configClosedloopRamp(config.CLOSED_LOOP_RAMP_RATE, timeoutMs);

        talon.configVoltageCompSaturation(0.0, timeoutMs);
        talon.configVoltageMeasurementFilter(32, timeoutMs);
        talon.enableVoltageCompensation(false);

        talon.enableCurrentLimit(config.ENABLE_CURRENT_LIMIT);

        talon.setStatusFramePeriod(StatusFrameEnhanced.Status_1_General, config.GENERAL_STATUS_FRAME_RATE_MS,
                timeoutMs);
        talon.setStatusFramePeriod(StatusFrameEnhanced.Status_2_Feedback0, config.FEEDBACK_STATUS_FRAME_RATE_MS,
                timeoutMs);

        talon.setStatusFramePeriod(StatusFrameEnhanced.Status_3_Quadrature, config.QUAD_ENCODER_STATUS_FRAME_RATE_MS,
                timeoutMs);
        talon.setStatusFramePeriod(StatusFrameEnhanced.Status_4_AinTempVbat,
                config.ANALOG_TEMP_VBAT_STATUS_FRAME_RATE_MS, timeoutMs);
        talon.setStatusFramePeriod(StatusFrameEnhanced.Status_8_PulseWidth, config.PULSE_WIDTH_STATUS_FRAME_RATE_MS,
                timeoutMs);

        talon.setControlFramePeriod(ControlFrame.Control_3_General, config.CONTROL_FRAME_PERIOD_MS);

        return talon;
    }

    public static VictorSPX createVictor(int id, Configuration config) {
        VictorSPX victor = new LazyVictorSPX(id);
        victor.set(ControlMode.PercentOutput, 0.0);

        victor.changeMotionControlFramePeriod(config.MOTION_CONTROL_FRAME_PERIOD_MS);
        victor.clearMotionProfileHasUnderrun(timeoutMs);
        victor.clearMotionProfileTrajectories();

        victor.clearStickyFaults(timeoutMs);

        victor.configForwardLimitSwitchSource(LimitSwitchSource.FeedbackConnector, LimitSwitchNormal.NormallyOpen,
                timeoutMs);
        victor.overrideLimitSwitchesEnable(config.ENABLE_LIMIT_SWITCH);

        // Turn off re-zeroing by default.
        victor.configSetParameter(ParamEnum.eClearPositionOnLimitF, 0, 0, 0, timeoutMs);
        victor.configSetParameter(ParamEnum.eClearPositionOnLimitR, 0, 0, 0, timeoutMs);

        victor.configNominalOutputForward(0, timeoutMs);
        victor.configNominalOutputReverse(0, timeoutMs);
        victor.configNeutralDeadband(config.NEUTRAL_DEADBAND, timeoutMs);

        victor.configPeakOutputForward(1.0, timeoutMs);
        victor.configPeakOutputReverse(-1.0, timeoutMs);

        victor.setNeutralMode(config.NEUTRAL_MODE);

        victor.configForwardSoftLimitThreshold(config.FORWARD_SOFT_LIMIT, timeoutMs);
        victor.configForwardSoftLimitEnable(config.ENABLE_SOFT_LIMIT, timeoutMs);

        victor.configReverseSoftLimitThreshold(config.REVERSE_SOFT_LIMIT, timeoutMs);
        victor.configReverseSoftLimitEnable(config.ENABLE_SOFT_LIMIT, timeoutMs);
        victor.overrideSoftLimitsEnable(config.ENABLE_SOFT_LIMIT);

        victor.setInverted(config.INVERTED);
        victor.setSensorPhase(config.SENSOR_PHASE);

        victor.selectProfileSlot(0, 0);

        victor.configVelocityMeasurementPeriod(config.VELOCITY_MEASUREMENT_PERIOD, timeoutMs);
        victor.configVelocityMeasurementWindow(config.VELOCITY_MEASUREMENT_ROLLING_AVERAGE_WINDOW, timeoutMs);

        victor.configOpenloopRamp(config.OPEN_LOOP_RAMP_RATE, timeoutMs);
        victor.configClosedloopRamp(config.CLOSED_LOOP_RAMP_RATE, timeoutMs);

        victor.configVoltageCompSaturation(0.0, timeoutMs);
        victor.configVoltageMeasurementFilter(32, timeoutMs);
        victor.enableVoltageCompensation(false);


        victor.setStatusFramePeriod(StatusFrame.Status_1_General, config.GENERAL_STATUS_FRAME_RATE_MS, timeoutMs);
        victor.setStatusFramePeriod(StatusFrame.Status_2_Feedback0, config.FEEDBACK_STATUS_FRAME_RATE_MS, timeoutMs);

        victor.setStatusFramePeriod(StatusFrame.Status_4_AinTempVbat, config.ANALOG_TEMP_VBAT_STATUS_FRAME_RATE_MS, timeoutMs);

        victor.setControlFramePeriod(ControlFrame.Control_3_General, config.CONTROL_FRAME_PERIOD_MS);

        return victor;
    }
    // Create  a default TalonSRX
    public static TalonSRX createDefaultTalon(int id) {
        return createTalon(id, defaultConfiguration);
    }

    public static TalonSRX createPermanentSlaveTalon(int id, int master_id) {
        TalonSRX talon = new LazyTalonSRX(id);
        talon.set(ControlMode.Follower, master_id);
        return talon;
    }

    //Create a default VictorSPX
    public static VictorSPX createDefaultVictor(int id) {
        return createVictor(id, defaultConfiguration);
    }

    public static VictorSPX createPermanentSlaveVictor(int id, int master_id) {
        VictorSPX victor = new LazyVictorSPX(id);
        victor.set(ControlMode.Follower, master_id);
        return victor;
    }
}