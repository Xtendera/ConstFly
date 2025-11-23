package com.ashrayshah.constfly.modules;

import com.ashrayshah.constfly.ConstFly;
import meteordevelopment.meteorclient.events.entity.player.PlayerMoveEvent;
import meteordevelopment.meteorclient.mixininterface.IVec3d;
import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;
import meteordevelopment.orbit.EventPriority;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public class ElytraFlyPlus extends Module {
    public ElytraFlyPlus() {
        super(ConstFly.CATEGORY, "Const Fly", "Efly for Const");
    }

    private final SettingGroup sgGeneral = settings.getDefaultGroup();
    private final SettingGroup sgSpeed = settings.createGroup("Speed");

    //--------------------General--------------------//
    private final Setting<Boolean> stopWater = sgGeneral.add(new BoolSetting.Builder()
        .name("Stop Water")
        .description("Doesn't modify movement while in water.")
        .defaultValue(true)
        .build()
    );

    private final Setting<Boolean> stopLava = sgGeneral.add(new BoolSetting.Builder()
        .name("Stop Lava")
        .description("Doesn't modify movement while in lava.")
        .defaultValue(true)
        .build()
    );

    //--------------------Constantiam Speed--------------------//
    private final Setting<Double> constSpeed = sgSpeed.add(new DoubleSetting.Builder()
        .name("Const Speed")
        .description("Maximum speed for constantiam mode.")
        .defaultValue(1)
        .min(0)
        .sliderRange(0, 5)
        .build()
    );

    private final Setting<Double> constAcceleration = sgSpeed.add(new DoubleSetting.Builder()
        .name("Const Acceleration")
        .description("Maximum speed for constantiam mode.")
        .defaultValue(1)
        .min(0)
        .sliderRange(0, 5)
        .build()
    );

    private final Setting<Boolean> constStop = sgSpeed.add(new BoolSetting.Builder()
        .name("Const Stop")
        .description("Stops movement when no input.")
        .defaultValue(true)
        .build()
    );

    private double velocity;
    private int activeFor;

    @EventHandler(priority = EventPriority.HIGHEST)
    private void onMove(PlayerMoveEvent event) {
        if (!active()) return;

        activeFor++;
        if (activeFor < 5) return;

        constantiamTick(event);
    }

    private void constantiamTick(PlayerMoveEvent event) {
        Vec3d motion = getMotion(mc.player.getVelocity());
        if (motion != null) {
            ((IVec3d) event.movement).meteor$set(motion.getX(), motion.getY(), motion.getZ());
            event.movement = motion;
        }
    }

    private Vec3d getMotion(Vec3d velocity) {
        if (mc.player.input.getMovementInput().y == 0) {
            if (constStop.get()) return new Vec3d(0, 0, 0);
            return null;
        }

        boolean forward = mc.player.input.getMovementInput().y > 0;

        double yaw = Math.toRadians(mc.player.getYaw() + (forward ? 90 : -90));

        double x = Math.cos(yaw);
        double z = Math.sin(yaw);
        double maxAcc = calcAcceleration(velocity.x, velocity.z, x, z);
        double delta = Math.clamp(MathHelper.getLerpProgress(velocity.horizontalLength(), 0, 0.5), 0, 1);

        double acc = Math.min(maxAcc, constAcceleration.get() / 20 * (0.1 + delta * 0.9));
        return new Vec3d(velocity.getX() + x * acc, velocity.getY(), velocity.getZ() + z * acc);
    }

    private double calcAcceleration(double vx, double vz, double x, double z) {
        double xz = x * x + z * z;
        return (Math.sqrt(xz * constSpeed.get() * constSpeed.get() - x * x * vz * vz - z * z * vx * vx + 2 * x * z * vx * vz) - x * vx - z * vz) / xz;
    }

    public boolean active() {
        if (stopWater.get() && mc.player.isTouchingWater()) {
            activeFor = 0;
            return false;
        }
        if (stopLava.get() && mc.player.isInLava()) {
            activeFor = 0;
            return false;
        }
        return mc.player.isGliding();
    }
}
