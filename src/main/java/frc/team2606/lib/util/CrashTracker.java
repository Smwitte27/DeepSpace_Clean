package frc.team2606.lib.util;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;
import java.util.UUID;

/**
 * Tracks and logs Start-up and caught crash events
 */
public class CrashTracker {

    private static final UUID RUN_INSTANCE_UUID = UUID.randomUUID();

    private static void logMarker(String mark) {
        logMarker(mark, null);
    }

    /**
     * Prints a unique UUID, and the name and date of the operation
     * If there is an exception, it will print out the info of that after the description
     * @param mark
     * @param nullableException
     */
    private static void logMarker(String mark, Throwable nullableException) {
        try (PrintWriter writer = new PrintWriter(new FileWriter("/home/lvuser/crash_tracking.txt", true))) {
            // Prints log info
            writer.print(RUN_INSTANCE_UUID.toString());
            writer.print(". ");
            writer.print(mark);
            writer.print(", ");
            writer.print(new Date().toString());

            // Prints the stacktrace of the error (Throwable and its backtrace)
            if (nullableException != null) {
                writer.print(", ");
                nullableException.printStackTrace(writer);
            }

            writer.println();
            // Catches error of crash logger :P
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Diferent log markers throughout the program

    public static void logRobotStartup() {
        logMarker("Robot Startup");
    }

    public static void logRobotConstruction() {
        logMarker("Robot Construction");
    }

    public static void logRobotInit() {
        logMarker("Robot Init");
    }

    public static void logTeleopInit() {
        logMarker("Teleop Init");
    }

    public static void logAutoInit() {
        logMarker("Auto Init");
    }

    public static void logDisabledInit() {
        logMarker("Disabled Init");
    }

    //general Crash logger
    public static void logThrowableCrash(Throwable t) {
        logMarker(t, null);
    }

}