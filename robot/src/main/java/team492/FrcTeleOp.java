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

import frclib.FrcJoystick;
import trclib.TrcRobot;
import trclib.TrcRobot.RunMode;

public class FrcTeleOp implements TrcRobot.RobotMode
{
    private enum DriveMode
    {
        MECANUM_MODE, ARCADE_MODE, TANK_MODE
    } // enum DriveMode

    protected Robot robot;

    private DriveMode driveMode = DriveMode.TANK_MODE;

    public FrcTeleOp(Robot robot)
    {
        this.robot = robot;
    } // FrcTeleOp

    //
    // Implements TrcRobot.RunMode interface.
    //

    @Override
    public void startMode(RunMode prevMode, RunMode nextMode)
    {
        //
        // Configure joysticks.
        //
        robot.leftDriveStick.setButtonHandler(this::leftDriveStickButtonEvent);
        robot.leftDriveStick.setYInverted(true);

        robot.rightDriveStick.setButtonHandler(this::rightDriveStickButtonEvent);
        robot.rightDriveStick.setYInverted(true);

        robot.operatorStick.setButtonHandler(this::operatorStickButtonEvent);
        robot.operatorStick.setYInverted(false);
    } // startMode

    @Override
    public void stopMode(RunMode prevMode, RunMode nextMode)
    {
    } // stopMode

    @Override
    public void runPeriodic(double elapsedTime)
    {
        double leftDriveX = robot.leftDriveStick.getXWithDeadband(true);
        double leftDriveY = robot.leftDriveStick.getYWithDeadband(true);
        double rightDriveY = robot.rightDriveStick.getYWithDeadband(true);
        //
        // DriveBase operation.
        //
        switch (driveMode)
        {
            case TANK_MODE:
                double leftPower = leftDriveY;
                double rightPower = rightDriveY;
                if (Robot.USE_RAW)
                {
                    robot.lfRawWheel.set(leftPower);
                    robot.lrRawWheel.set(leftPower);
                    robot.rfRawWheel.set(rightPower);
                    robot.rrRawWheel.set(rightPower);
                }
                else
                {
                    robot.driveBase.tankDrive(leftPower, rightPower);
                }
                break;

            case ARCADE_MODE:
                double drivePower = rightDriveY;
                double turnPower = robot.rightDriveStick.getTwistWithDeadband(true);
                if (robot.USE_RAW)
                {
                    leftPower = drivePower + turnPower;
                    rightPower = drivePower - turnPower;

                    robot.lfRawWheel.set(leftPower);
                    robot.lrRawWheel.set(leftPower);
                    robot.rfRawWheel.set(rightPower);
                    robot.rrRawWheel.set(rightPower);
                }
                else
                {
                    robot.driveBase.arcadeDrive(drivePower, turnPower);
                }
                break;

            case MECANUM_MODE:
                double x = leftDriveX;
                double y = rightDriveY;
                double rot = robot.rightDriveStick.getTwistWithDeadband(true);
                robot.driveBase.holonomicDrive(x, y, rot);
                break;
        }
    } // runPeriodic

    @Override
    public void runContinuous(double elapsedTime)
    {
    } // runContinuous

    //
    // Implements TrcJoystick.ButtonHandler.
    //

    public void leftDriveStickButtonEvent(int button, boolean pressed)
    {
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

} // class FrcTeleOp
