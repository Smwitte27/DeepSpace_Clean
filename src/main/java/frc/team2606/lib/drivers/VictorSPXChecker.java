package frc.team2606.lib.drivers;

import java.util.ArrayList;
import java.util.function.DoubleSupplier;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.VictorSPX;

import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.command.Subsystem;
import frc.team2606.lib.util.Util;

public class VictorSPXChecker {
    public static class CheckerConfig {
        public double RPMFloor = 2000;

        public double RPMEpsilon = 500;
        public DoubleSupplier RPMSupplier = null;

        public double runTimeSec = 4.0;
        public double waitTimeSec = 2.0;
        public double runOutputPercentage = 0.5;
    }

    public static class VictorSPXConfig {
        public String name;
        public VictorSPX victor;

        public VictorSPXConfig(String Name, VictorSPX Victor) {
            name = Name;
            victor = Victor;
        }
    }

    private static class StoredVictorSPXConfiguration {
        public ControlMode mode;
        public double setValue;
    }

    public static boolean CheckVictors(Subsystem subsystem, ArrayList<VictorSPXConfig> victorsToCheck,
            CheckerConfig checkerConfig) {
        boolean failure = false;
        System.out.println("////////////////////////////////////////////////");
        System.out.println("Checking subsystem " + subsystem.getClass() + " for " + victorsToCheck.size() + " victors.");

        ArrayList<Double> rpms = new ArrayList<>();
        ArrayList<StoredVictorSPXConfiguration> storedConfigurations = new ArrayList<>();

        /* Store previous configurations */
        for (VictorSPXConfig config : victorsToCheck) {
            LazyVictorSPX victor = LazyVictorSPX.class.cast(config.victor);

            StoredVictorSPXConfiguration configuration = new StoredVictorSPXConfiguration();
            configuration.mode = victor.getControlMode();
            configuration.setValue = victor.getLastSet();

            // Store
            storedConfigurations.add(configuration);
            // Disable
            victor.set(ControlMode.PercentOutput, 0.0);
        }
        for (VictorSPXConfig config : victorsToCheck) {
            System.out.println("Checking: " + config.name);

            config.victor.set(ControlMode.PercentOutput, checkerConfig.runOutputPercentage);
            Timer.delay(checkerConfig.runTimeSec);

            /* Get Victor information */

            double rpm = Double.NaN;
            if (checkerConfig.RPMSupplier != null) {
                rpm = checkerConfig.RPMSupplier.getAsDouble();
                rpms.add(rpm);
                System.out.print("RPM: " + rpm);
            }
            System.out.println('\n');

            config.victor.set(ControlMode.PercentOutput, 0.0);

            /* Check Victors */

            // RPM
            if (checkerConfig.RPMSupplier != null) {
                if (rpm < checkerConfig.RPMFloor) {
                    System.out.println(config.name + " has failed rpm floor check vs " + checkerConfig.RPMFloor + "!!");
                    failure = true;
                }
            }
            Timer.delay(checkerConfig.waitTimeSec);
        }
        /* Now run aggregate checks (Compare all to average) */

        // RPM
        if (rpms.size() > 0) {
            Double average = rpms.stream().mapToDouble(val -> val).average().getAsDouble();

            if (!Util.allCloseTo(rpms, average, checkerConfig.RPMEpsilon)) {
                System.out.println("RPMs varied!!!!!!!!");
                failure = true;
            }
        }

        // Restore Victor configurations
        for (int i = 0; i < victorsToCheck.size(); ++i) {
            victorsToCheck.get(i).victor.set(storedConfigurations.get(i).mode, storedConfigurations.get(i).setValue);
        }
        return !failure;
    }
}