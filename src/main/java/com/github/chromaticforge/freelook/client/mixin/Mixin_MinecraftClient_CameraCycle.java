package com.github.chromaticforge.freelook.client.mixin;


import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.GameOptions;
//#if MC >= 1.16.5
import net.minecraft.client.option.Perspective;
import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
//#endif
//#if MC <= 1.12.2
//$$ import org.objectweb.asm.Opcodes;
//$$ import org.spongepowered.asm.mixin.injection.Redirect;
//$$ import org.objectweb.asm.Opcodes;
//#endif
import com.github.chromaticforge.freelook.client.FreeLookConfig;
import com.github.chromaticforge.freelook.client.FreeLookController;
import com.github.chromaticforge.freelook.client.PerspectiveManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;


/**
 * An example Mixin using SpongePowered's Mixin library
 *
 * @see Mixin
 * @see Inject
 */
@Mixin(MinecraftClient.class)
public class Mixin_MinecraftClient_CameraCycle {

    @Unique
    private boolean hasCycledFreelook = false;


    //#if MC >= 1.16.5
    @WrapWithCondition(
            method = "handleInputEvents",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/option/GameOptions;setPerspective(Lnet/minecraft/client/option/Perspective;)V"
            )
    )
    private boolean overrideCameraCycle(GameOptions instance, Perspective cameraType) {
        return shouldOverrideCameraCycle();
    }
    //#else
    //$$ @Redirect(
    //$$         method = "runTick",
    //$$         at = @At(
    //$$                 value = "FIELD",
    //$$                 target = "Lnet/minecraft/client/settings/GameSettings;thirdPersonView:I",
    //$$                 opcode = Opcodes.PUTFIELD,
    //$$                 ordinal = 0
    //$$         )
    //$$ )
    //$$ private void overrideCameraCycle(GameSettings instance, int cameraType) {
    //$$     if (shouldOverrideCameraCycle()) {
    //$$         instance.thirdPersonView = cameraType;
    //$$     }
    //$$ }
    //#endif


    @Unique
    private boolean shouldOverrideCameraCycle() {
        if (FreeLookConfig.INSTANCE.onCycleChange == 1) {
            FreeLookController.isFreeLooking = false;
        }

        if (FreeLookConfig.INSTANCE.onCycleChange == 2 && FreeLookController.isFreeLooking && !hasCycledFreelook) {
            return false;
        }

        if (FreeLookConfig.INSTANCE.addToCameraCycle) {
            if (hasCycledFreelook) {
                hasCycledFreelook = false;
                FreeLookController.stopFreeLooking();
            } else if (PerspectiveManager.getCurrentPerspective() == PerspectiveManager.getMaximumPerspectiveIndex()) {
                FreeLookController.startFreeLooking();
                hasCycledFreelook = true;

                return false;
            }
        }

        return true;
    }
}
