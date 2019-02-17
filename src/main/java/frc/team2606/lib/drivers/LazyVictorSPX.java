package frc.team2606.lib.drivers;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.VictorSPX;;

/**
 * VictorSPX wrapper that skips duplicate set commands
 */
public class LazyVictorSPX extends VictorSPX {
    protected double lastSet = Double.NaN;
    protected ControlMode lastControlMode = null;

    public LazyVictorSPX(int deviceID) {
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