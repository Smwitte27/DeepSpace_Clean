package frc.team2606.frc2019.controlModes;

public class ArcadeControl implements IControls {

    @Override
    public double getLeftThrottle() {
        return 0;
    }

    @Override
    public double getRightThrottle() {
        return 0;
    }

    @Override
    public double getArmThrottle() {
        return 0;
    }

    @Override
    public boolean getRunIntake() {
        return false;
    }

    @Override
    public boolean getReverseIntake() {
        return false;
    }

    @Override
    public boolean getEjectHatch() {
        return false;
    }

    @Override
    public boolean getBallHeights() {
        return false;
    }

    @Override
    public boolean getHatchHeights() {
        return false;
    }

    @Override
    public boolean getGoToShipCargo() {
        return false;
    }

    @Override
    public boolean getGoToLevelOne() {
        return false;
    }

    @Override
    public boolean getGoToLevelTwo() {
        return false;
    }

    @Override
    public boolean getGoToLevelThree() {
        return false;
    }

}