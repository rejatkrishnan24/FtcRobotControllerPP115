package org.firstinspires.ftc.teamcode;

import static org.checkerframework.checker.units.UnitsTools.s;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.DigitalChannel;

@TeleOp
public class MecanumTeleOp extends LinearOpMode {
    @Override
    public void runOpMode() throws InterruptedException {
        // Declare our motors
        // Make sure your ID's match your configuration
        DcMotor frontLeft = hardwareMap.dcMotor.get("fl");
        DcMotor backLeft = hardwareMap.dcMotor.get("bl");
        DcMotor frontRight = hardwareMap.dcMotor.get("fr");
        DcMotor backRight = hardwareMap.dcMotor.get("br");
        //DcMotor lift = hardwareMap.dcMotor.get("l");
        //DcMotor angle = hardwareMap.dcMotor.get("a");
        //DcMotor spin = hardwareMap.dcMotor.get("s");
        CRServo grip = hardwareMap.get(CRServo.class, "g");

        DigitalChannel digitalTouch;
        digitalTouch = hardwareMap.get(DigitalChannel.class, "sensor_digital");

        // set the digital channel to input.
        digitalTouch.setMode(DigitalChannel.Mode.INPUT);
        frontLeft.setDirection(DcMotorSimple.Direction.REVERSE);
        backLeft.setDirection(DcMotorSimple.Direction.REVERSE);
        //lift.setDirection(DcMotorSimple.Direction.REVERSE);


        float driveSpeed = 0.5f; //sets drive motor speeds (between 0 and 1)

        waitForStart();

        if (isStopRequested()) return;

        while (opModeIsActive()) {
            double y = -gamepad1.left_stick_y; // Remember, this is reversed!
            double x = gamepad1.left_stick_x * 1.1; // Counteract imperfect strafing
            double rx = gamepad1.right_stick_x;
            //double l = gamepad2.left_stick_y;
            //double a = gamepad2.right_stick_y;
            double denominator = Math.max(Math.abs(y) + Math.abs(x) + Math.abs(rx), 1);
            double frontLeftPower = (y + x + rx) / denominator;
            double backLeftPower = (y - x + rx) / denominator;
            double frontRightPower = (y - x - rx) / denominator;
            double backRightPower = (y + x - rx) / denominator;
            //double liftPower = l;
            //double anglePower = a;
            //double spinPower = s;

            frontLeft.setPower(frontLeftPower * driveSpeed);
            backLeft.setPower(backLeftPower * driveSpeed);
            frontRight.setPower(frontRightPower * driveSpeed);
            backRight.setPower(backRightPower * driveSpeed);
            //angle.setPower(anglePower / 2.5);
            //spin.setPower(spinPower/3);
            if (digitalTouch.getState() == false) {
                //lift.setPower(liftPower);
            }
            if (gamepad2.right_trigger > 0.2 && digitalTouch.getState() == false) {
                grip.setPower(gamepad2.right_trigger/2);
            } else if (gamepad2.left_trigger > 0.2) {
                grip.setPower(-gamepad2.left_trigger/2);
            } else {
                grip.setPower(0);

            }
        }
    }
}