package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
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

        frontRight.setDirection(DcMotorSimple.Direction.REVERSE);
        frontLeft.setDirection(DcMotorSimple.Direction.REVERSE);
        backRight.setDirection(DcMotorSimple.Direction.REVERSE);

        waitForStart();

        if (isStopRequested()) return;

        while (opModeIsActive()) {
            double vertical = -gamepad1.left_stick_y; // Remember, this is reversed!
            double horizontal = gamepad1.left_stick_x; // Counteract imperfect strafing
            double pivot = gamepad1.right_stick_x;

            double frontLeftPower = (pivot + vertical + horizontal);
            double backLeftPower = (pivot + vertical - horizontal);
            double frontRightPower = (-pivot + vertical - horizontal);
            double backRightPower = (-pivot + vertical + horizontal);

            frontLeft.setPower(frontLeftPower/2.5);
            backLeft.setPower(backLeftPower/2.5);
            frontRight.setPower(frontRightPower/2.5);
            backRight.setPower(backRightPower/2.5);
        }
    }
}