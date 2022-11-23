package org.firstinspires.ftc.teamcode;

import static org.checkerframework.checker.units.UnitsTools.s;

import com.qualcomm.hardware.rev.RevBlinkinLedDriver;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.DigitalChannel;

@TeleOp
class mecanumteleop extends LinearOpMode {
    @Override
    public void runOpMode() throws InterruptedException {
        // Declare our motors
        // Make sure your ID's match your configuration
        DcMotor frontLeft = hardwareMap.dcMotor.get("fl");
        DcMotor backLeft = hardwareMap.dcMotor.get("bl");
        DcMotor frontRight = hardwareMap.dcMotor.get("fr");
        DcMotor backRight = hardwareMap.dcMotor.get("br");
        DcMotor lift = hardwareMap.dcMotor.get("l");
        DcMotor angle = hardwareMap.dcMotor.get("a");
        //DcMotor spin = hardwareMap.dcMotor.get("s");
        CRServo grip1 = hardwareMap.get(CRServo.class, "g1");
        CRServo grip2 = hardwareMap.get(CRServo.class, "g2");
        RevBlinkinLedDriver blinkinLedDriver = hardwareMap.get(RevBlinkinLedDriver.class, "LED");
        DigitalChannel digitalTouch;
        digitalTouch = hardwareMap.get(DigitalChannel.class, "sensor_digital");
        lift.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        lift.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        while (lift.getCurrentPosition() != 0) {
            idle();
        }
        lift.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        lift.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        int groundPosition = 0;
        int lowPosition = 100;
        int mediumPosition = 200;
        int highPosition = 300;
        waitForStart();

        // set the digital channel to input.
        digitalTouch.setMode(DigitalChannel.Mode.INPUT);
        frontLeft.setDirection(DcMotorSimple.Direction.REVERSE);
        backLeft.setDirection(DcMotorSimple.Direction.REVERSE);
        lift.setDirection(DcMotorSimple.Direction.REVERSE);

        lift.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        lift.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        float driveSpeed = 0.5f; //sets drive motor speeds (between 0 and 1)

        waitForStart();

        if (isStopRequested()) return;

        while (opModeIsActive()) {
            double y = -gamepad1.left_stick_y; // Remember, this is reversed!
            double x = gamepad1.left_stick_x * 1.1; // Counteract imperfect strafing
            double rx = gamepad1.right_stick_x;
            double denominator = Math.max(Math.abs(y) + Math.abs(x) + Math.abs(rx), 1);
            double frontLeftPower = (y + x + rx) / denominator;
            double backLeftPower = (y - x + rx) / denominator;
            double frontRightPower = (y - x - rx) / denominator;
            double backRightPower = (y + x - rx) / denominator;
            double spinPower = s;

            frontLeft.setPower(frontLeftPower * driveSpeed);
            backLeft.setPower(backLeftPower * driveSpeed);
            frontRight.setPower(frontRightPower * driveSpeed);
            backRight.setPower(backRightPower * driveSpeed);
            grip1.setPower(spinPower / 2);
            grip2.setPower(spinPower / 2);
        }

        if (gamepad2.left_stick_y > 0) {
            angle.setPower(-gamepad2.right_stick_y/2);
        } else {
            angle.setPower(0);

            //a = ground position
            //x = low position
            //y = medium position
            //b = high position
            if (gamepad2.b) {
                lift.setPower(1);
                lift.setTargetPosition(highPosition);
            } else if (gamepad2.a) {
                lift.setPower(0.3);
                lift.setTargetPosition(groundPosition);
            } else if (gamepad2.x) {
                lift.setPower(0.5);
                lift.setTargetPosition(lowPosition);
            } else if (gamepad2.y) {
                lift.setPower(0.7);
                lift.setTargetPosition(mediumPosition);
            }
            if (lift.isBusy()) {
                lift.setPower(0.5);
            } else {
                lift.setPower(0);


                //using the touch sensor to determine whether servo should be allowed to spin or not
                if (gamepad2.right_trigger > 0.2 && digitalTouch.getState() == false) {
                    grip1.setPower(gamepad2.right_trigger / 2);
                    grip2.setPower(-gamepad2.right_trigger / 2);
                    blinkinLedDriver.setPattern(RevBlinkinLedDriver.BlinkinPattern.DARK_RED);
                } else if (gamepad2.left_trigger > 0.2) {
                    blinkinLedDriver.setPattern(RevBlinkinLedDriver.BlinkinPattern.DARK_GREEN);
                    grip1.setPower(-gamepad2.left_trigger / 2);
                    grip2.setPower(gamepad2.right_trigger / 2);
                } else {
                    grip1.setPower(0);
                    grip2.setPower(0);
                    blinkinLedDriver.setPattern(RevBlinkinLedDriver.BlinkinPattern.RAINBOW_RAINBOW_PALETTE);
                }
            }
        }
    }
}
