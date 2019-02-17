package frc.team2606.lib.util;

/**
 * Drivetrain command routing or blocking user motor controls
 */
public class DriveSignal {
    protected double leftDrive;
    protected double rightDrive;
    protected boolean brakeMode;

    /**
     * Defaults breakMode to false without input, passes to extended constructor
     * @param left
     * @param right
     * @see <code> DriveSignal(double left, double right, boolean brakemode)
     */
    public DriveSignal(double left, double right) {
        this(left, right, false);
    }

    /**
     * Sets left and right motor values and brake boolean
     * @param left
     * @param right
     * @param breakmode
     */
    public DriveSignal(double left, double right, boolean brakemode) {
        leftDrive = left;
        rightDrive = right;
        brakeMode = brakemode;
    }

    public static DriveSignal NEUTRAL = new DriveSignal(0, 0);
    public static DriveSignal BRAKE = new DriveSignal(0, 0, true);

    public double getLeft() {
        return leftDrive;
    }

    public double getRight() {
        return rightDrive;
    }
    
    public boolean getBrakeMode() {
        return brakeMode;
    }

    public String toString() {
        return ("L: " + leftDrive + "R: " + rightDrive + (brakeMode ? ", BRAKE" : ""));
    }
}