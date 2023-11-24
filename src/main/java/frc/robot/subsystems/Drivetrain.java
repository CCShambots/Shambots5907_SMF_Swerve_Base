package frc.robot.subsystems;

import edu.wpi.first.wpilibj2.command.InstantCommand;
import frc.robot.Constants;
import frc.robot.ShamLib.SMF.StateMachine;
import frc.robot.ShamLib.swerve.DriveCommand;
import frc.robot.ShamLib.swerve.SwerveDrive;

import java.util.function.DoubleSupplier;

import static frc.robot.Constants.Drivetrain.*;

public class Drivetrain extends StateMachine<Drivetrain.State> {
    private SwerveDrive swerveDrive;

    private DoubleSupplier xSupplier;
    private DoubleSupplier ySupplier;
    private DoubleSupplier thetaSupplier;

    public Drivetrain(DoubleSupplier xSupplier, DoubleSupplier ySupplier, DoubleSupplier thetaSupplier) {
        super("Drivetrain", State.Undetermined, State.class);

        this.xSupplier = xSupplier;
        this.ySupplier = ySupplier;
        this.thetaSupplier = thetaSupplier;

        this.swerveDrive = new SwerveDrive(
                PIGEON_CAN_ID,
                Modules.DRIVE_GAINS,
                Modules.TURN_GAINS,
                MAX_SWERVE_LIMITS.getMaxSpeed(),
                MAX_SWERVE_LIMITS.getMaxAcceleration(),
                Modules.MAX_TURN_SPEED,
                Modules.MAX_TURN_ACCELERATION,
                TELEOP_THETA_GAINS,
                AUTO_THETA_GAINS,
                TRANSLATION_GAINS,
                false, //Set to true for extra telemetry
                Modules.CAN_BUS,
                GYRO_CAN_BUS,
                Constants.CURRENT_LIMITS_CONFIGS,
                Modules.MODULE_1,
                Modules.MODULE_2,
                Modules.MODULE_3,
                Modules.MODULE_4
        );
    }

    private void registerStates() {
        registerStateCommand(State.TeleopFieldOriented, new DriveCommand(
                swerveDrive,
                xSupplier,
                ySupplier,
                thetaSupplier,
                Constants.Controller.DEADBAND,
                Constants.Controller.CONVERTER,
                true,
                this,
                MAX_SWERVE_LIMITS
        ));

        registerStateCommand(State.TeleopBotOriented, new DriveCommand(
                swerveDrive,
                xSupplier,
                ySupplier,
                thetaSupplier,
                Constants.Controller.DEADBAND,
                Constants.Controller.CONVERTER,
                true,
                this,
                MAX_SWERVE_LIMITS
        ));


        registerStateCommand(State.XShape, new InstantCommand(() ->
            swerveDrive.setModuleStates(X_SHAPE)
        ));
    }

    private void registerTransitions() {
        addOmniTransition(State.TeleopFieldOriented, () -> swerveDrive.setFieldRelative(true));
        addOmniTransition(State.TeleopBotOriented, () -> swerveDrive.setFieldRelative(false));
    }

    @Override
    protected void determineSelf() {
        setState(State.Idle);
    }

    public enum State {
        Undetermined,
        Idle,
        TeleopFieldOriented,
        TeleopBotOriented,
        XShape
    }
}