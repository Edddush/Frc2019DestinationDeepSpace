package team492;

import com.revrobotics.CANDigitalInput;
import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMaxLowLevel;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.TimedRobot;
import frclib.FrcCANSparkMax;

public class SparkMaxTest extends TimedRobot
{
    private Joystick leftJoystick, rightJoystick;
    private FrcCANSparkMax sparkMax;
    private CANSparkMax spark;

    @Override
    public void robotInit()
    {
        leftJoystick = new Joystick(0);
        rightJoystick = new Joystick(1);
//        spark = new CANSparkMax(4, CANSparkMaxLowLevel.MotorType.kBrushless);

        sparkMax = new FrcCANSparkMax("SparkMAX", 4, true);
        sparkMax.motor.getForwardLimitSwitch(CANDigitalInput.LimitSwitchPolarity.kNormallyClosed).enableLimitSwitch(false);
        sparkMax.motor.getReverseLimitSwitch(CANDigitalInput.LimitSwitchPolarity.kNormallyClosed).enableLimitSwitch(false);
    }

    @Override
    public void teleopPeriodic()
    {
        double power = deadband(rightJoystick.getY());
        System.out.printf("Running motor %d with power: %.2f\n", sparkMax.getDeviceID(), power);
        //spark.set(power);
        sparkMax.set(power);
    }

    private double deadband(double value)
    {
        return Math.abs(value) <= 0.1 ? 0.0 : value;
    }
}
