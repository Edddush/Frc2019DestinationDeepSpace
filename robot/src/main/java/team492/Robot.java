/*
 * Copyright (c) 2019 Titan Robotics Club (http://www.titanrobotics.com)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package team492;

import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;

import edu.wpi.first.wpilibj.SPI;
import frclib.FrcAHRSGyro;
import frclib.FrcCANSparkMax;
import frclib.FrcJoystick;
import frclib.FrcRobotBase;
import trclib.TrcMecanumDriveBase;
import trclib.TrcPidController;
import trclib.TrcPidDrive;
import trclib.TrcRobot.RunMode;

/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the TrcRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the manifest file in the
 * resource directory.
 */
public class Robot extends FrcRobotBase
{
    public static final String programName = "DestinationDeepSpace";
    public static final boolean USE_RAW = false;

    //
    // Inputs.
    //
    public FrcJoystick leftDriveStick = null;
    public FrcJoystick rightDriveStick = null;
    public FrcJoystick operatorStick = null;
    //
    // Raw wheels
    //
    public CANSparkMax lfRawWheel = null;
    public CANSparkMax lrRawWheel = null;
    public CANSparkMax rfRawWheel = null;
    public CANSparkMax rrRawWheel = null;

    public FrcAHRSGyro gyro = null;
    //
    // DriveBase subsystem.
    //
    public FrcCANSparkMax leftFrontWheel = null;
    public FrcCANSparkMax leftRearWheel = null;
    public FrcCANSparkMax rightFrontWheel = null;
    public FrcCANSparkMax rightRearWheel = null;
    public TrcMecanumDriveBase driveBase = null;

    private TrcPidController encoderXPidCtrl, encoderYPidCtrl, gyroTurnPidCtrl;
    private TrcPidDrive pidDrive;

    /**
     * Constructor.
     */
    public Robot()
    {
        super(programName);
    }   //Robot

    /**
     * This function is run when the robot is first started up and should be used for any initialization code.
     */
    @Override
    public void robotInit()
    {
        //
        // Inputs.
        //
        leftDriveStick = new FrcJoystick("leftDriveStick", RobotInfo.JSPORT_LEFT_DRIVESTICK);
        rightDriveStick = new FrcJoystick("rightDriveStick", RobotInfo.JSPORT_RIGHT_DRIVESTICK);
        operatorStick = new FrcJoystick("operatorStick", RobotInfo.JSPORT_OPERATORSTICK);
        //
        // Raw Wheels
        //
        if (USE_RAW)
        {
            lfRawWheel = new CANSparkMax(RobotInfo.CANID_LEFTFRONTWHEEL, MotorType.kBrushless);
            lrRawWheel = new CANSparkMax(RobotInfo.CANID_LEFTREARWHEEL, MotorType.kBrushless);
            rfRawWheel = new CANSparkMax(RobotInfo.CANID_RIGHTFRONTWHEEL, MotorType.kBrushless);
            rrRawWheel = new CANSparkMax(RobotInfo.CANID_RIGHTREARWHEEL, MotorType.kBrushless);
        }
        else
        {
            //
            // DriveBase subsystem.
            //
            leftFrontWheel = new FrcCANSparkMax("LeftFrontWheel", RobotInfo.CANID_LEFTFRONTWHEEL, true);
            leftRearWheel = new FrcCANSparkMax("LeftRearWheel", RobotInfo.CANID_LEFTREARWHEEL, true);
            rightFrontWheel = new FrcCANSparkMax("RightFrontWheel", RobotInfo.CANID_RIGHTFRONTWHEEL, true);
            rightRearWheel = new FrcCANSparkMax("RightRearWheel", RobotInfo.CANID_RIGHTREARWHEEL, true);

            gyro = new FrcAHRSGyro("Gyro", SPI.Port.kMXP);

            //
            // Initialize each drive motor controller.
            //
            leftFrontWheel.setInverted(false);
            leftRearWheel.setInverted(false);
            rightFrontWheel.setInverted(true);
            rightRearWheel.setInverted(true);

            leftFrontWheel.setPositionSensorInverted(false);
            leftRearWheel.setPositionSensorInverted(false);
            rightFrontWheel.setPositionSensorInverted(false);
            rightRearWheel.setPositionSensorInverted(false);

            //
            // Initialize DriveBase subsystem.
            //
            driveBase = new TrcMecanumDriveBase(leftFrontWheel, leftRearWheel, rightFrontWheel, rightRearWheel, gyro);
            driveBase.setPositionScales(0.001, 0.001, 0.01);
            driveBase.setOdometryEnabled(true);

            encoderXPidCtrl = new TrcPidController(
                "encoderXPidCtrl",
                new TrcPidController.PidCoefficients(
                    RobotInfo.ENCODER_X_KP, RobotInfo.ENCODER_X_KI, RobotInfo.ENCODER_X_KD, RobotInfo.ENCODER_X_KF),
                RobotInfo.ENCODER_X_TOLERANCE,
                driveBase::getXPosition);
            encoderYPidCtrl = new TrcPidController(
                "encoderYPidCtrl",
                new TrcPidController.PidCoefficients(
                    RobotInfo.ENCODER_Y_KP, RobotInfo.ENCODER_Y_KI, RobotInfo.ENCODER_Y_KD, RobotInfo.ENCODER_Y_KF),
                RobotInfo.ENCODER_Y_TOLERANCE,
                driveBase::getYPosition);
            gyroTurnPidCtrl = new TrcPidController(
                "gyroTurnPidCtrl",
                new TrcPidController.PidCoefficients(
                    RobotInfo.GYRO_TURN_KP, RobotInfo.GYRO_TURN_KI, RobotInfo.GYRO_TURN_KD, RobotInfo.GYRO_TURN_KF),
                RobotInfo.GYRO_TURN_TOLERANCE,
                driveBase::getHeading);
            gyroTurnPidCtrl.setAbsoluteSetPoint(true);
            pidDrive = new TrcPidDrive("pidDrive", driveBase, encoderXPidCtrl, encoderYPidCtrl, gyroTurnPidCtrl);
            pidDrive.setStallTimeout(RobotInfo.DRIVE_STALL_TIMEOUT);
            pidDrive.setMsgTracer(globalTracer);

            encoderXPidCtrl.setOutputLimit(RobotInfo.DRIVE_MAX_XPID_POWER);
            encoderYPidCtrl.setOutputLimit(RobotInfo.DRIVE_MAX_YPID_POWER);
            gyroTurnPidCtrl.setOutputLimit(RobotInfo.DRIVE_MAX_TURNPID_POWER);

        }
        //
        // Create Robot Modes.
        //
        setupRobotModes(new FrcTeleOp(this), null, null, null);
    }   //robotInit

    @Override
    public void robotStartMode(RunMode prevMode, RunMode nextMode)
    {
    }   //robotStartMode

    @Override
    public void robotStopMode(RunMode prevMode, RunMode nextMode)
    {
    }   //robotStopMode

}   //class Robot
