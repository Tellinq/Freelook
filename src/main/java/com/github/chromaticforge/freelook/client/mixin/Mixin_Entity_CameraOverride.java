package com.github.chromaticforge.freelook.client.mixin;

import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import com.github.chromaticforge.freelook.client.CameraOverriddenEntity;
import com.github.chromaticforge.freelook.client.FreeLookConfig;
import com.github.chromaticforge.freelook.client.FreeLookController;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Entity.class)
public class Mixin_Entity_CameraOverride implements CameraOverriddenEntity {
    @Unique
    private float cameraPitch;

    @Unique
    private float cameraYaw;

    @Inject(method = "changeLookDirection", at = @At("HEAD"), cancellable = true)
    public void changeCameraLookDirection(
            //#if MC >= 1.16.5
            double yaw, double pitch,
            //#elseif MC <= 1.12.2
            //$$ float yaw, float pitch,
            //#endif
            CallbackInfo ci) {
        if (FreeLookController.isFreeLooking && (Object) this instanceof ClientPlayerEntity) {
            float pitchDelta = (float) (pitch * 0.15);
            float yawDelta = (float) (yaw * 0.15);

            if (FreeLookConfig.Pitch.enabled) {
                this.cameraPitch = updateCameraValue(this.cameraPitch,
                        // Normal pitch is inverted on <=1.12.2. We must also reflect it here
                        //#if MC <= 1.12.2
                        //$$ -pitchDelta,
                        //#else
                        pitchDelta,
                        //#endif
                        FreeLookConfig.Pitch.invert, FreeLookConfig.Pitch.lock, -90.0f, 90.0f);
            }

            if (FreeLookConfig.Yaw.enabled) {
                this.cameraYaw = updateCameraValue(this.cameraYaw, yawDelta,
                        FreeLookConfig.Yaw.invert, FreeLookConfig.Yaw.lock, -90.0f, 90.0f);
            }
            //#if MC <= 1.12.2
            //$$ OmniClient.getInstance().renderGlobal.setDisplayListEntitiesDirty();
            //#endif

            ci.cancel();
        }
    }

    @Unique
    private float updateCameraValue(float currentValue, float delta, boolean invert, boolean lock, float min, float max) {
        float adjustedDelta = invert ? -delta : delta;
        return lock ? MathHelper.clamp(currentValue + adjustedDelta, min, max) : currentValue + adjustedDelta;
    }


    @Override
    @Unique
    public float freelook$getCameraPitch() {
        return this.cameraPitch;
    }

    @Override
    @Unique
    public float freelook$getCameraYaw() {
        return this.cameraYaw;
    }

    @Override
    @Unique
    public void freelook$setCameraPitch(float pitch) {
        this.cameraPitch = pitch;
    }

    @Override
    @Unique
    public void freelook$setCameraYaw(float yaw) {
        this.cameraYaw = yaw;
    }
}
