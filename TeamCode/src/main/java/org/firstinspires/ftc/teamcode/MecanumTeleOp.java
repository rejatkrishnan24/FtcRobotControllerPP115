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
        CRServo spin1 = hardwareMap.get(CRServo.class, "spin1");
        CRServo spin2 = hardwareMap.get(CRServo.class, "spin2");

        frontLeft.setDirection(DcMotorSimple.Direction.REVERSE);

        waitForStart();

        if (isStopRequested()) return;

        while (opModeIsActive()) {
            double vertical = -gamepad1.left_stick_x; // Remember, this is reversed!
            double horizontal = gamepad1.left_stick_y; // Counteract imperfect strafing
            double pivot = gamepad1.right_stick_x;

            double frontLeftPower = (pivot - vertical + horizontal);
            double backLeftPower = (pivot - vertical - horizontal);
            double frontRightPower = (-pivot + vertical - horizontal);
            double backRightPower = (pivot + vertical - horizontal);

            frontLeft.setPower(frontLeftPower/2.5);
            backLeft.setPower(backLeftPower/2.5);
            frontRight.setPower(frontRightPower/2.5);
            backRight.setPower(backRightPower/2.5);

            if (gamepad1.right_trigger > 0.1) {
                spin1.setPower(gamepad1.right_trigger);
                spin2.setPower(-gamepad1.right_trigger);
            } else if (gamepad1.left_trigger > 0.1) {
                spin1.setPower(-gamepad1.left_trigger);
                spin2.setPower(gamepad1.left_trigger);
            } else {
                spin1.setPower(0);
                spin1.setPower(0);
            }
        }
    }
}