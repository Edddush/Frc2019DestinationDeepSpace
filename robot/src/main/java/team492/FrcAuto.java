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
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
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

import common.CmdPidDrive;
import common.CmdTimedDrive;
import frclib.FrcChoiceMenu;
import frclib.FrcJoystick;
import hallib.HalDashboard;
import trclib.TrcRobot;
import trclib.TrcRobot.RunMode;
import trclib.TrcTaskMgr;

public class FrcAuto implements TrcRobot.RobotMode
{
    private static final String moduleName = "FrcAuto";
    private static final boolean DO_UPDATES = false;

    public static enum AutoStrategy
    {
        X_TIMED_DRIVE, Y_TIMED_DRIVE, X_DISTANCE_DRIVE, Y_DISTANCE_DRIVE, TURN_DEGREES, DO_NOTHING
    } // enum AutoStrategy

    private Robot robot;

    //
    // Menus.
    //
    private FrcChoiceMenu<AutoStrategy> autoStrategyMenu;
    private AutoStrategy autoStrategy;
    private double delay;
    private boolean manualOverride = false;

    private TrcRobot.RobotCommand autoCommand;

    public FrcAuto(Robot robot)
    {
        this.robot = robot;
        //
        // Create Autonomous Mode specific menus.
        //
        autoStrategyMenu = new FrcChoiceMenu<>("Auto/AutoStrategies");
        //
        // Populate Autonomous Mode menus.
        //
        autoStrategyMenu.addChoice("X Timed Drive", AutoStrategy.X_TIMED_DRIVE);
        autoStrategyMenu.addChoice("Y Timed Drive", AutoStrategy.Y_TIMED_DRIVE);
        autoStrategyMenu.addChoice("X Distance Drive", AutoStrategy.X_DISTANCE_DRIVE);
        autoStrategyMenu.addChoice("Y Distance Drive", AutoStrategy.Y_DISTANCE_DRIVE);
        autoStrategyMenu.addChoice("Turn Degrees", AutoStrategy.TURN_DEGREES);
        autoStrategyMenu.addChoice("Do Nothing", AutoStrategy.DO_NOTHING, false, true);
    } // FrcAuto

    //
    // Implements TrcRobot.RunMode.
    //

    @Override
    public void startMode(RunMode prevMode, RunMode nextMode)
    {
        final String funcName = moduleName + ".startMode";

        robot.getGameInfo();
        robot.globalTracer
            .traceInfo(funcName, "%s_%s%03d (%s%d) [FMSConnected=%b] msg=%s", robot.eventName, robot.matchType,
                robot.matchNumber, robot.alliance.toString(), robot.location, robot.ds.isFMSAttached(),
                robot.gameSpecificMessage);

        //
        // Configure joysticks.
        //
        robot.leftDriveStick.setButtonHandler(this::leftDriveStickButtonEvent);
        robot.leftDriveStick.setYInverted(true);

        robot.rightDriveStick.setButtonHandler(this::rightDriveStickButtonEvent);
        robot.rightDriveStick.setYInverted(true);

        robot.operatorStick.setButtonHandler(this::operatorStickButtonEvent);
        robot.operatorStick.setYInverted(false);

        robot.encoderXPidCtrl.setOutputLimit(RobotInfo.DRIVE_MAX_XPID_POWER);
        robot.encoderYPidCtrl.setOutputLimit(RobotInfo.DRIVE_MAX_YPID_POWER);

        //
        // Retrieve menu choice values.
        //
        autoStrategy = autoStrategyMenu.getCurrentChoiceObject();
        delay = HalDashboard.getNumber("Auto/Delay", 0.0);
        switch (autoStrategy)
        {
            case X_TIMED_DRIVE:
                autoCommand = new CmdTimedDrive(robot, delay, robot.driveTime, robot.drivePower, 0.0, 0.0);
                break;

            case Y_TIMED_DRIVE:
                autoCommand = new CmdTimedDrive(robot, delay, robot.driveTime, 0.0, robot.drivePower, 0.0);
                break;

            case X_DISTANCE_DRIVE:
                autoCommand = new CmdPidDrive(robot, robot.pidDrive, delay, robot.driveDistance, 0.0, 0.0,
                    robot.drivePowerLimit, false);
                break;

            case Y_DISTANCE_DRIVE:
                autoCommand = new CmdPidDrive(robot, robot.pidDrive, delay, 0.0, robot.driveDistance, 0.0,
                    robot.drivePowerLimit, false);
                break;

            case TURN_DEGREES:
                autoCommand = new CmdPidDrive(robot, robot.pidDrive, delay, 0.0, 0.0, robot.turnDegrees,
                    robot.drivePowerLimit, false);
                break;

            case DO_NOTHING:
                autoCommand = null;
                break;
        }
    } // startMode

    @Override
    public void stopMode(RunMode prevMode, RunMode nextMode)
    {
        TrcTaskMgr.getInstance().printTaskPerformanceMetrics(robot.globalTracer);
    } // stopMode

    @Override
    public void runPeriodic(double elapsedTime)
    {
        if (DO_UPDATES)
        {
            robot.updateDashboard(RunMode.AUTO_MODE);
            robot.announceSafety();
        }

        if (manualOverride)
        {
            double x = robot.leftDriveStick.getXWithDeadband(true);
            double y = robot.rightDriveStick.getYWithDeadband(true);
            double rot = robot.rightDriveStick.getTwistWithDeadband(true);
            robot.driveBase.holonomicDrive(x, y, rot);
        }
    } // runPeriodic

    @Override
    public void runContinuous(double elapsedTime)
    {
        if (autoCommand != null)
        {
            autoCommand.cmdPeriodic(elapsedTime);

            if (robot.pidDrive.isActive())
            {
                robot.encoderXPidCtrl.printPidInfo(robot.globalTracer, elapsedTime, robot.battery);
                robot.encoderYPidCtrl.printPidInfo(robot.globalTracer, elapsedTime, robot.battery);
                robot.gyroTurnPidCtrl.printPidInfo(robot.globalTracer, elapsedTime, robot.battery);
            }
        }
    } // runContinuous

    //
    // Implements TrcJoystick.ButtonHandler.
    //

    public void leftDriveStickButtonEvent(int button, boolean pressed)
    {
        robot.dashboard.displayPrintf(8, " LeftDriveStick: button=0x%04x %s", button, pressed ? "pressed" : "released");

        switch (button)
        {
            case FrcJoystick.LOGITECH_TRIGGER:
                break;

            case FrcJoystick.LOGITECH_BUTTON2:
                break;

            case FrcJoystick.LOGITECH_BUTTON3:
                break;

            case FrcJoystick.LOGITECH_BUTTON4:
                break;

            case FrcJoystick.LOGITECH_BUTTON5:
                break;

            case FrcJoystick.LOGITECH_BUTTON6:
                break;

            case FrcJoystick.LOGITECH_BUTTON7:
                break;

            case FrcJoystick.LOGITECH_BUTTON8:
                break;

            case FrcJoystick.LOGITECH_BUTTON9:
                break;

            case FrcJoystick.LOGITECH_BUTTON10:
                break;

            case FrcJoystick.LOGITECH_BUTTON11:
                break;

            case FrcJoystick.LOGITECH_BUTTON12:
                break;
        }
    } // leftDriveStickButtonEvent

    public void rightDriveStickButtonEvent(int button, boolean pressed)
    {
        robot.dashboard.displayPrintf(8, "RightDriveStick: button=0x%04x %s", button, pressed ? "pressed" : "released");

        switch (button)
        {
            case FrcJoystick.SIDEWINDER_TRIGGER:
                break;

            case FrcJoystick.SIDEWINDER_BUTTON2:
                break;

            case FrcJoystick.SIDEWINDER_BUTTON3:
                break;

            case FrcJoystick.SIDEWINDER_BUTTON4:
                break;

            case FrcJoystick.SIDEWINDER_BUTTON5:
                break;

            case FrcJoystick.SIDEWINDER_BUTTON6:
                break;

            case FrcJoystick.SIDEWINDER_BUTTON7:
                break;

            case FrcJoystick.SIDEWINDER_BUTTON8:
                break;

            case FrcJoystick.SIDEWINDER_BUTTON9:
                break;
        }
    } // rightDriveStickButtonEvent

    public void operatorStickButtonEvent(int button, boolean pressed)
    {
        robot.dashboard.displayPrintf(8, "  OperatorStick: button=0x%04x %s", button, pressed ? "pressed" : "released");

        switch (button)
        {
            case FrcJoystick.LOGITECH_TRIGGER:
                break;

            case FrcJoystick.LOGITECH_BUTTON2:
                break;

            case FrcJoystick.LOGITECH_BUTTON3:
                break;

            case FrcJoystick.LOGITECH_BUTTON4:
                break;

            case FrcJoystick.LOGITECH_BUTTON5:
                break;

            case FrcJoystick.LOGITECH_BUTTON6:
                break;

            case FrcJoystick.LOGITECH_BUTTON7:
                break;

            case FrcJoystick.LOGITECH_BUTTON8:
                break;

            case FrcJoystick.LOGITECH_BUTTON9:
                break;

            case FrcJoystick.LOGITECH_BUTTON10:
                break;

            case FrcJoystick.LOGITECH_BUTTON11:
                break;

            case FrcJoystick.LOGITECH_BUTTON12:
                break;
        }
    } // operatorStickButtonEvent

} // class FrcAuto
