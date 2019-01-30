package team492;

import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMaxLowLevel;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.drive.MecanumDrive;

public class SparkMaxTest extends TimedRobot
{
    private Joystick leftJoystick, rightJoystick;
    private MecanumDrive drive;

    @Override
    public void robotInit()
    {
        leftJoystick = new Joystick(1);
        rightJoystick = new Joystick(2);

        CANSparkMax leftFrontWheel = new CANSparkMax(3, CANSparkMaxLowLevel.MotorType.kBrushless);
        CANSparkMax rightFrontWheel = new CANSparkMax(4, CANSparkMaxLowLevel.MotorType.kBrushless);
        CANSparkMax leftRearWheel = new CANSparkMax(5, CANSparkMaxLowLevel.MotorType.kBrushless);
        CANSparkMax rightRearWheel = new CANSparkMax(6, CANSparkMaxLowLevel.MotorType.kBrushless);

        drive = new MecanumDrive(leftFrontWheel, leftRearWheel, rightFrontWheel, rightRearWheel);
    }

    @Override
    public void teleopPeriodic()
    {
        double x = deadband(leftJoystick.getX());
        double y = deadband(rightJoystick.getY());
        double rot = deadband(rightJoystick.getTwist());
        drive.driveCartesian(y, x, rot);
    }

    private double deadband(double value)
    {
        return Math.abs(value) <= 0.05 ? 0.0 : value;
    }
}
