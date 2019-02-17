package frc.team2606.lib.drivers;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;

/**
 * TalonSRX wrapper that skips duplicate set commands
 */
public class LazyTalonSRX extends TalonSRX {
    protected double lastSet = Double.NaN;
    protected ControlMode lastControlMode = null;

    public LazyTalonSRX(int deviceID) {
        super(deviceID);
    }

    public double getLastSet() {
        return lastSet;
    }

    public void set(ControlMode nextMode, double nextSet) {
        if (nextSet != lastSet || nextMode != lastControlMode) {
            lastSet = nextSet;
            lastControlMode = nextMode;
            super.set(nextMode, nextSet);
        }
    }
}