package frc.team2606.frc2019.controlModes;

public interface IControls {
    
    double getLeftThrottle();

    double getRightThrottle();

    double getArmThrottle();

    boolean getRunIntake();

    boolean getReverseIntake();

    boolean getEjectHatch();


    //Height Selections
    boolean getBallHeights();

    boolean getHatchHeights();

    boolean getGoToShipCargo();

    boolean getGoToLevelOne();

    boolean getGoToLevelTwo();

    boolean getGoToLevelThree();



    
}