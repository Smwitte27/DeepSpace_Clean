package frc.team2606.lib.drivers;

import java.util.ArrayList;
import java.util.function.DoubleSupplier;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;

import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.command.Subsystem;
import frc.team2606.lib.util.Util;

public class TalonSRXChecker {
    public static class CheckerConfig {
        public double currentFloor = 5;
        public double RPMFloor = 2000;

        public double currentEpsilon = 5.0;
        public double RPMEpsilon = 500;
        public DoubleSupplier RPMSupplier = null;

        public double runTimeSec = 4.0;
        public double waitTimeSec = 2.0;
        public double runOutputPercentage = 0.5;
    }

    public static class TalonSRXConfig {
        public String name;
        public TalonSRX talon;

        public TalonSRXConfig(String Name, TalonSRX Talon) {
            name = Name;
            talon = Talon;
        }
    }

    private static class StoredTalonSRXConfiguration {
        public ControlMode mode;
        public double setValue;
    }

    public static boolean CheckTalons(Subsystem subsystem, ArrayList<TalonSRXConfig> talonsToCheck, CheckerConfig checkerConfig) {
        boolean failure = false;
        System.out.println("////////////////////////////////////////////////");
        System.out.println("Checking subsystem " + subsystem.getClass() + " for " + talonsToCheck.size() + " talons.");

        ArrayList<Double> currents = new ArrayList<>();
        ArrayList<Double> rpms = new ArrayList<>();
        ArrayList<StoredTalonSRXConfiguration> storedConfigurations = new ArrayList<>();

        /* Store previous configurations*/
        for (TalonSRXConfig config : talonsToCheck) {
            LazyTalonSRX talon = LazyTalonSRX.class.cast(config.talon);

            StoredTalonSRXConfiguration configuration = new StoredTalonSRXConfiguration();
            configuration.mode = talon.getControlMode();
            configuration.setValue = talon.getLastSet();

            // Store
            storedConfigurations.add(configuration);
            // Disable
            talon.set(ControlMode.PercentOutput, 0.0);
        }
        for (TalonSRXConfig config : talonsToCheck) {
            System.out.println("Checking: " + config.name);

            config.talon.set(ControlMode.PercentOutput, checkerConfig.runOutputPercentage);
            Timer.delay(checkerConfig.runTimeSec);

            /* Get Talon information */

            double current = config.talon.getOutputCurrent();
            currents.add(current);
            System.out.print("Current: " + current);

            double rpm = Double.NaN;
            if (checkerConfig.RPMSupplier != null) {
                rpm = checkerConfig.RPMSupplier.getAsDouble();
                rpms.add(rpm);
                System.out.print("RPM: " + rpm);
            }
            System.out.println('\n');

            config.talon.set(ControlMode.PercentOutput, 0.0);

            /* Check Talons */

            //Current
            if (current < checkerConfig.currentFloor) {
                System.out.println(
                        config.name + " has failed current floor check vs " + checkerConfig.currentFloor + "!!");
                failure = true;
            }

            //RPM
            if (checkerConfig.RPMSupplier != null) {
                if (rpm < checkerConfig.RPMFloor) {
                    System.out.println(config.name + " has failed rpm floor check vs " + checkerConfig.RPMFloor + "!!");
                    failure = true;
                }
            }
            Timer.delay(checkerConfig.waitTimeSec);
        }
        /* Now run aggregate checks (Compare all to average) */

        //Current
        if (currents.size() > 0) {
            Double average = currents.stream().mapToDouble(val -> val).average().getAsDouble();

            if (!Util.allCloseTo(currents, average, checkerConfig.currentEpsilon)) {
                System.out.println("Currents varied!!!!!!!!!!!");
                failure = true;
            }
        }

        //RPM
        if (rpms.size() > 0) {
            Double average = rpms.stream().mapToDouble(val -> val).average().getAsDouble();

            if (!Util.allCloseTo(rpms, average, checkerConfig.RPMEpsilon)) {
                System.out.println("RPMs varied!!!!!!!!");
                failure = true;
            }
        }

        // Restore Talon configurations
        for (int i = 0; i < talonsToCheck.size(); ++i) {
            talonsToCheck.get(i).talon.set(storedConfigurations.get(i).mode, storedConfigurations.get(i).setValue);
        }
        return !failure;
    }
}