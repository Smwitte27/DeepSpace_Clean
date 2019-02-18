/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.team2606.frc2019;

import java.util.Arrays;

import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.team2606.frc2019.controlModes.Controls;
import frc.team2606.frc2019.loops.Looper;
import frc.team2606.frc2019.subsystems.Arm;
import frc.team2606.frc2019.subsystems.Drive;
import frc.team2606.frc2019.subsystems.Intake;
import frc.team2606.frc2019.subsystems.Superstructure;
import frc.team2606.lib.util.CrashTracker;
import frc.team2606.lib.util.DriveSignal;

/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the TimedRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the build.gradle file in the
 * project.
 */
public class Robot extends TimedRobot {
  private Looper enabledLooper = new Looper();
  private Looper disabledLooper = new Looper();
  private Controls controls = new Controls();

  private final SubsystemManager subsystemManager = new SubsystemManager(
      Arrays.asList(
      Drive.getInstance()//,
      //Arm.getInstance(),
      //Intake.getInstance(),
      //Superstructure.getInstance()
    )
  );

  private Drive drive = Drive.getInstance();
  private Arm arm = Arm.getInstance();
  //private Intake intake = Intake.getInstance();
  //private Superstructure superstrucure = Superstructure.getInstance();
  /**
   * This function is run when the robot is first started up and should be used
   * for any initialization code.
   */
  @Override
  public void robotInit() {
    try {
      CrashTracker.logRobotInit();


      subsystemManager.registerEnabledLoops(enabledLooper);
      subsystemManager.registerDisabledLoops(disabledLooper);

    } catch (Throwable t) {
      CrashTracker.logThrowableCrash(t);
      throw t;
    }
  }

  @Override
  public void disabledPeriodic() {
    SmartDashboard.putString("Match Cycle", "DISABLED");

    try {
      outputToSmartDashboard();
    } catch (Throwable t) {
      CrashTracker.logThrowableCrash(t);
        throw t;
    }
  }

  @Override
  public void autonomousInit() {
    SmartDashboard.putString("Match Cycle", "AUTONOMOUS");

    try {

    } catch (Throwable t) {
      CrashTracker.logThrowableCrash(t);
      throw t;
    }
  }

  @Override
  public void autonomousPeriodic() {
    SmartDashboard.putString("Match Cycle", "AUTONOMOUS");

    outputToSmartDashboard();
    try {

    } catch (Throwable t) {
      CrashTracker.logThrowableCrash(t);
      throw t;
    }
  }

  @Override
  public void teleopInit() {
    SmartDashboard.putString("Match Cycle", "TELEOP");

    try {
      CrashTracker.logTeleopInit();

      disabledLooper.stop();
      enabledLooper.start();

      drive.setOpenLoop(new DriveSignal(0.05, 0.05));

    } catch (Throwable t) {
      CrashTracker.logThrowableCrash(t);
      throw t;
    }
  }

  @Override
  public void teleopPeriodic() {
    SmartDashboard.putString("Match Cycle", "TELEOP");

    double lThrottle = controls.getLeftThrottle();
    double rThrottle = controls.getRightThrottle();
    double armThrottle = controls.getArmThrottle();
    //double armDown = OI.getArmDown();
    //boolean runIntake = OI.isRunIntake();
    //boolean ejectBall = OI.isEjectBall();
    SmartDashboard.putNumber("Left Throttle", lThrottle);
    SmartDashboard.putNumber("Right Throttle", rThrottle);
    SmartDashboard.putNumber("Arm Throttle", armThrottle);
    //SmartDashboard.putNumber("Arm Down", armDown);
    //SmartDashboard.putBoolean("Itake Running", runIntake);
    //SmartDashboard.putBoolean("Itake Ejecting", ejectBall);   

    try {

      drive.setOpenLoop(new DriveSignal(lThrottle, rThrottle));

      arm.setOpenLoop(armThrottle);

      // Intake/Eject
      boolean hasBall = false;
      boolean runIntake = controls.getRunIntake();
      boolean ReverseIntake = controls.getReverseIntake();
      boolean ejectHatch = controls.getEjectHatch();


      //Arm Positions

      // Hatch Scoring
      boolean goHighHatch = controls.getHatchHeights() && controls.getGoToLevelThree();
      boolean goMiddleHatch = controls.getHatchHeights() && controls.getGoToLevelTwo();
      boolean goLowHatch = controls.getHatchHeights() && controls.getGoToLevelOne();

      // Ball Scoring
      boolean goHighBall = controls.getBallHeights() && controls.getGoToLevelThree();
      boolean goMiddleBall = controls.getBallHeights() && controls.getGoToLevelTwo();
      boolean goLowBall = controls.getBallHeights() && controls.getGoToLevelOne();
      boolean goCargoBall = controls.getBallHeights() && controls.getGoToShipCargo();

      outputToSmartDashboard();
    } catch (Throwable t) {
      CrashTracker.logThrowableCrash(t);
      throw t;
    }
  }

  @Override
  public void testInit() {
    SmartDashboard.putString("Match Cycle", "TEST");

    try {
      System.out.println("Starting check systems.");

      disabledLooper.stop();
      enabledLooper.stop();

      // drive.checkSystem();
      // arm.checkSystem();
      // intake.checkSystem();

    } catch (Throwable t) {
      CrashTracker.logThrowableCrash(t);
      throw t;
    }
  }

  @Override
  public void testPeriodic() {
    SmartDashboard.putString("Match Cycle", "TEST");
  }

  public void outputToSmartDashboard() {
    Drive.getInstance().outputTelemetry();
    //Arm.getInstance().outputTelemetry();
    //Intake.getInstance().outputTelemetry();
    //Superstructure.getInstance().outputTelemetry();
    enabledLooper.outToSnartDashboard();
  }

}
