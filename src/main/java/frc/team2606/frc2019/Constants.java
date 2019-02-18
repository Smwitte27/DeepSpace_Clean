package frc.team2606.frc2019;

public class Constants {

    public static final double LooperDt = .01;

    /* Mechanical Configuration */

    //Drive
    public static final int LEFT_MASTER = 1;
    public static final int LEFT_SLAVE = 1;
    public static final int RIGHT_MASTER = 2;
    public static final int RIGHT_SLAVE = 2;

    //Superstructure
    public static final int ARM_ACTUATOR = 3;
    public static final int INTAKE_MOTOR = 4;

    //Solinoids
    public static final int HATCH_EJECT_SOLINOID = 1;

    //Controler
    public static final int CONTROLLER_PORT = 0;

    //Control Modes
    public static final boolean TANK_DRIVE = true;
    public static final boolean ARCADE_DRIVE = false;

    // CAN Stuff
    public static final int CAN_TIMEOUT = 10; // use for on the fly updates
    public static final int LONG_CAN_TIMEOUT = 100; // use for constructors
    public static final double DRIVE_RAMP_RATE = 0;
    public static final int PID_CONTROL = 2;



    /* ROBOT CONSTRAINTS/LIMITS */

    //Arm
    public static final int ARM_UPPER_LIMIT = 1000; //in "ticks"
    public static final int ARM_LOWER_LIMIT = 1000; // in "ticks"
    public static final double ARM_RAMP_RATE = .1;
    public static final double ARM_PID_P = 0;
    public static final double ARM_PID_I = 0;
    public static final double ARM_PID_D = 0;

    //Intake
    public static final double INTAKE_CURRENT_THRESHOLD = 5.5;



    /* ROBOT PHYSICAL CONSTANTS */
        
    //Wheels
    public static final double WHEEL_DIAMETER = 6;
    public static final double WHEEL_RADIUS = 3;

    //Robot Dimensions
    public static final double BUMPER_LENGTH = 38.25;
    public static final double BUMPER_WIDTH = 33.75;

    public static final double CENTER_TO_FRONT_BUMPER_DISTANCE = BUMPER_LENGTH / 2;
    public static final double CENTER_TO_REAR_BUMPER_DISTANCE = BUMPER_LENGTH / 2;
    public static final double CENTER_TO_SIDE_BUMPER_DISTANCE = BUMPER_WIDTH / 2;

    /*
    // Path Constants
    public static final double PATH_KX = 4.0; // units/s per unit of error
    public static final double PATH_LOOKAHEAD_TIME = 0.4; // seconds to look ahead along the path for steering
    public static final double PATH_MIN_LOOKAHEAD_DISTANCE = 24.0; // inches
    */


}