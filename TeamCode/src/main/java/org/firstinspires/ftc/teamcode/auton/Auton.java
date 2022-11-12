/*
 * Copyright (c) 2021 OpenFTC Team
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
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package org.firstinspires.ftc.teamcode.auton;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotor.RunMode;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.openftc.apriltag.AprilTagDetection;
import org.openftc.easyopencv.OpenCvCamera;
import org.openftc.easyopencv.OpenCvCameraFactory;
import org.openftc.easyopencv.OpenCvCameraRotation;

import java.util.ArrayList;

@Autonomous

    public class Auton extends LinearOpMode {

        private DcMotor leftRear = null;
        private DcMotor rightRear = null;
        private DcMotor leftFront = null;
        private DcMotor rightFront = null;


        OpenCvCamera camera;
        AprilTagDetectionPipeline aprilTagDetectionPipeline;

        static final double FEET_PER_METER = 3.28084;

        // Lens intrinsics
        // UNITS ARE PIXELS
        // NOTE: this calibration is for the C920 webcam at 800x448.
        // You will need to do your own calibration for other configurations!
        double fx = 578.272;
        double fy = 578.272;
        double cx = 402.145;
        double cy = 221.506;

        // UNITS ARE METERS
        double tagsize = 0.166;

        // Tag ID 1,2,3 from the 36h11 family
        int LEFT = 1;
        int MIDDLE = 2;
        int RIGHT = 3;

        AprilTagDetection tagOfInterest = null;

    static final double     COUNTS_PER_MOTOR_REV    = 1120 ;    // eg: TETRIX Motor Encoder
    static final double     DRIVE_GEAR_REDUCTION    = 1.0 ;     // No External Gearing.
    static final double     WHEEL_DIAMETER_INCHES   = 2.0 ;     // For figuring circumference
    static final double     COUNTS_PER_INCH         = (COUNTS_PER_MOTOR_REV * DRIVE_GEAR_REDUCTION) / (WHEEL_DIAMETER_INCHES * 3.1415);
    static final double     DRIVE_SPEED             = 0.6;
    static final double     TURN_SPEED              = 0.5;
        @Override
        public void runOpMode() {


            int cameraMonitorViewId = hardwareMap.appContext.getResources().getIdentifier("cameraMonitorViewId", "id", hardwareMap.appContext.getPackageName());
            camera = OpenCvCameraFactory.getInstance().createWebcam(hardwareMap.get(WebcamName.class, "Webcam 1"), cameraMonitorViewId);
            aprilTagDetectionPipeline = new AprilTagDetectionPipeline(tagsize, fx, fy, cx, cy);

            camera.setPipeline(aprilTagDetectionPipeline);
            camera.openCameraDeviceAsync(new OpenCvCamera.AsyncCameraOpenListener() {
                @Override
                public void onOpened() {
                    camera.startStreaming(800, 448, OpenCvCameraRotation.UPRIGHT);
                }

                @Override
                public void onError(int errorCode) {

                }
            });

            telemetry.setMsTransmissionInterval(50);

            leftFront = hardwareMap.get(DcMotor.class, "fl");
            rightFront = hardwareMap.get(DcMotor.class, "fr");
            leftRear = hardwareMap.get(DcMotor.class, "bl");
            rightRear = hardwareMap.get(DcMotor.class, "br");


            // Most robots need the motor on one side to be reversed to drive forward
            // Reverse the motor that runs backwards when connected directly to the battery
            leftFront.setDirection(DcMotor.Direction.REVERSE);

            leftFront.setMode(RunMode.RUN_USING_ENCODER);
            leftRear.setMode(RunMode.RUN_USING_ENCODER);
            rightFront.setMode(RunMode.RUN_USING_ENCODER);
            rightRear.setMode(RunMode.RUN_USING_ENCODER);

            leftFront.setMode(RunMode.STOP_AND_RESET_ENCODER);
            leftRear.setMode(RunMode.STOP_AND_RESET_ENCODER);
            rightRear.setMode(RunMode.STOP_AND_RESET_ENCODER);
            rightFront.setMode(RunMode.STOP_AND_RESET_ENCODER);

            telemetry.addData("Starting at",  "%7d :%7d",
                    leftFront.getCurrentPosition(),
                    rightFront.getCurrentPosition());
                    rightRear.getCurrentPosition();
                    leftRear.getCurrentPosition();
            telemetry.update();

           leftRear.setTargetPosition(0);
           leftFront.setTargetPosition(0);
           rightRear.setTargetPosition(0);
           rightFront.setTargetPosition(0);

           rightRear.setMode(RunMode.RUN_TO_POSITION);
           rightFront.setMode(RunMode.RUN_TO_POSITION);
           leftRear.setMode(RunMode.RUN_TO_POSITION);
           leftFront.setMode(RunMode.RUN_TO_POSITION);


            /*
             * The INIT-loop:
             * This REPLACES waitForStart!
             */
            while (!isStarted() && !isStopRequested()) {
                ArrayList<AprilTagDetection> currentDetections = aprilTagDetectionPipeline.getLatestDetections();

                if (currentDetections.size() != 0) {
                    boolean tagFound = false;

                    for (AprilTagDetection tag : currentDetections) {
                        if (tag.id == LEFT || tag.id == MIDDLE || tag.id == RIGHT) {
                            tagOfInterest = tag;
                            tagFound = true;
                            break;
                        }
                    }

                    if (tagFound) {
                        telemetry.addLine("Tag of interest is in sight!\n\nLocation data:");
                        tagToTelemetry(tagOfInterest);
                    } else {
                        telemetry.addLine("Don't see tag of interest :(");

                        if (tagOfInterest == null) {
                            telemetry.addLine("(The tag has never been seen)");
                        } else {
                            telemetry.addLine("\nBut we HAVE seen the tag before; last seen at:");
                            tagToTelemetry(tagOfInterest);
                        }
                    }

                } else {
                    telemetry.addLine("Don't see tag of interest :(");

                    if (tagOfInterest == null) {
                        telemetry.addLine("(The tag has never been seen)");
                    } else {
                        telemetry.addLine("\nBut we HAVE seen the tag before; last seen at:");
                        tagToTelemetry(tagOfInterest);
                    }

                }

                telemetry.update();
                sleep(20);
            }

            /* Update the telemetry */
            if (tagOfInterest != null) {
                telemetry.addLine("Tag snapshot:\n");
                tagToTelemetry(tagOfInterest);
                telemetry.update();
            } else {
                telemetry.addLine("No tag snapshot available, it was never sighted during the init loop :(");
                telemetry.update();
            }

            /* Actually do something useful */
            if (tagOfInterest == null || tagOfInterest.id == LEFT) {
                leftRear.setPower(0.3);
                rightFront.setPower(0.3);
                leftFront.setPower(0.3);
                rightRear.setPower(0.3);
                sleep(2250);
                leftRear.setPower(0.0);
                rightFront.setPower(0.0);
                leftFront.setPower(0.0);
                rightRear.setPower(0.0);
                sleep(250);
                leftRear.setPower(0.55);
                rightFront.setPower(0.55);
                leftFront.setPower(-0.15);
                rightRear.setPower(-0.15);
                sleep(4000);
                leftRear.setPower(0.0);
                rightFront.setPower(0.0);
                leftFront.setPower(0.0);
                rightRear.setPower(0.0);



            } else if (tagOfInterest.id == MIDDLE) {
                leftRear.setPower(0.3);
                rightFront.setPower(0.3);
                leftFront.setPower(0.3);
                rightRear.setPower(0.3);
                sleep(2250);
                leftRear.setPower(0.0);
                rightFront.setPower(0.0);
                leftFront.setPower(0.0);
                rightRear.setPower(0.0);
            } else {
                leftRear.setPower(0.3);
                rightFront.setPower(0.3);
                leftFront.setPower(0.3);
                rightRear.setPower(0.3);
                sleep(2250);
                leftRear.setPower(0.0);
                rightFront.setPower(0.0);
                leftFront.setPower(0.0);
                rightRear.setPower(0.0);
                sleep(250);
                leftRear.setPower(-0.55);
                rightFront.setPower(-0.55);
                leftFront.setPower(0.15);
                rightRear.setPower(0.15);
                sleep(3000);
                leftRear.setPower(0.0);
                rightFront.setPower(0.0);
                leftFront.setPower(0.0);
                rightRear.setPower(0.0);
            }



            while (opModeIsActive()) {
                sleep(20);
            }
        }

        void tagToTelemetry(AprilTagDetection detection) {
            telemetry.addLine(String.format("\nDetected tag ID=%d", detection.id));
            telemetry.addLine(String.format("Translation X: %.2f feet", detection.pose.x * FEET_PER_METER));
            telemetry.addLine(String.format("Translation Y: %.2f feet", detection.pose.y * FEET_PER_METER));
            telemetry.addLine(String.format("Translation Z: %.2f feet", detection.pose.z * FEET_PER_METER));
            telemetry.addLine(String.format("Rotation Yaw: %.2f degrees", Math.toDegrees(detection.pose.yaw)));
            telemetry.addLine(String.format("Rotation Pitch: %.2f degrees", Math.toDegrees(detection.pose.pitch)));
            telemetry.addLine(String.format("Rotation Roll: %.2f degrees", Math.toDegrees(detection.pose.roll)));
        }



    }
