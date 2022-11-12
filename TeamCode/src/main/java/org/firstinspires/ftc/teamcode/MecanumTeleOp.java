package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;


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
        DcMotor lift = hardwareMap.dcMotor.get("l");
        CRServo spin1 = hardwareMap.get(CRServo.class, "spin1");
        CRServo spin2 = hardwareMap.get(CRServo.class, "spin2");

        frontLeft.setDirection(DcMotorSimple.Direction.REVERSE);
        backLeft.setDirection(DcMotorSimple.Direction.REVERSE);


        waitForStart();

        if (isStopRequested()) return;

        while (opModeIsActive()) {
            double y = -gamepad1.left_stick_y; // Remember, this is reversed!
            double x = gamepad1.left_stick_x * 1.1; // Counteract imperfect strafing
            double rx = gamepad1.right_stick_x;
            double l = gamepad2.left_stick_y;
            double denominator = Math.max(Math.abs(y) + Math.abs(x) + Math.abs(rx), 1);
            double frontLeftPower = (y + x + rx) / denominator;
            double backLeftPower = (y - x + rx)/ denominator;
            double frontRightPower = (y - x - rx)/ denominator;
            double backRightPower = (y + x - rx)/ denominator;
            double liftPower = l;

            frontLeft.setPower(frontLeftPower/1.5);
            backLeft.setPower(backLeftPower/1.5);
            frontRight.setPower(frontRightPower/1.5);
            backRight.setPower(backRightPower/1.5);
            lift.setPower(liftPower/2);

            if (gamepad2.right_trigger > 0.2) {
                spin1.setPower(gamepad2.right_trigger);
                spin2.setPower(-gamepad2.right_trigger);
            } else if (gamepad2.left_trigger > 0.2) {
                spin1.setPower(-gamepad2.left_trigger);
                spin2.setPower(gamepad2.left_trigger);
            } else {
                spin1.setPower(0);
                spin1.setPower(0);


            }
        }
    }
}