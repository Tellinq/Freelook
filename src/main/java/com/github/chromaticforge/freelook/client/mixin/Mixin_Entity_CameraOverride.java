package com.github.chromaticforge.freelook.client.mixin;

import com.github.chromaticforge.freelook.client.FreelookController;
import com.github.chromaticforge.freelook.client.config.FreelookConfig;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import com.github.chromaticforge.freelook.client.CameraOverriddenEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

//#if MC <= 1.12.2
//$$ import dev.deftu.omnicore.client.OmniClient;
//#endif

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
        if (FreelookController.isFreeLooking && (Object) this instanceof ClientPlayerEntity) {
            float pitchDelta = (float) (pitch * 0.15);
            float yawDelta = (float) (yaw * 0.15);

            if (FreelookConfig.Pitch.INSTANCE.getEnabled()) {
                this.cameraPitch = FreelookController.INSTANCE.updateCameraValue(this.cameraPitch,
                        // Normal pitch is inverted on <=1.12.2. We must also reflect it here
                        //#if MC <= 1.12.2
                        //$$ -pitchDelta,
                        //#else
                        pitchDelta,
                        //#endif
                        FreelookConfig.Pitch.INSTANCE.getInvert(), FreelookConfig.Pitch.INSTANCE.getLock(), -90.0f, 90.0f);
            }

            if (FreelookConfig.Yaw.INSTANCE.getEnabled()) {
                this.cameraYaw = FreelookController.INSTANCE.updateCameraValue(this.cameraYaw, yawDelta,
                        FreelookConfig.Yaw.INSTANCE.getInvert(), FreelookConfig.Yaw.INSTANCE.getLock(), -90.0f, 90.0f);
            }
            //#if MC <= 1.12.2
            //$$ OmniClient.getInstance().renderGlobal.setDisplayListEntitiesDirty();
            //#endif

            ci.cancel();
        }
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
