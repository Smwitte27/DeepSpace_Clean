package frc.team2606.lib.drivers;

import com.ctre.phoenix.ErrorCode;

import edu.wpi.first.wpilibj.DriverStation;

public class MotorControllerUtil {

    public static void checkError(ErrorCode errorCode, String message) {
        if (errorCode != ErrorCode.OK) {
            DriverStation.reportError(message + errorCode, false);
        }
    }
}