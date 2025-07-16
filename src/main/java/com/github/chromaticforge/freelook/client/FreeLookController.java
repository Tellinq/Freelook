package com.github.chromaticforge.freelook.client;

import dev.deftu.omnicore.client.OmniClient;
import dev.deftu.omnicore.client.OmniClientPlayer;
import org.polyfrost.polyui.animate.Animation;
import org.polyfrost.polyui.animate.Easing;

public class FreeLookController {
    public static boolean isFreeLooking = false;
    private static int lastPerspective;
    private static long pressStartTime;
    private static long lastUpdateTime;
    private static Animation timer = new Easing.Elastic(Easing.Type.Out, 650L, 0.0f, 1.0f);

    public static void handlePressAndHold(boolean pressed) {
        if (pressed && pressStartTime == 0) {
            pressStartTime = System.currentTimeMillis();
            toggleFreeLooking();
        } else {
            long pressDuration = System.currentTimeMillis() - pressStartTime;
            pressStartTime = 0;

            if (pressDuration > FreeLookConfig.Activation.holdThreshold) {
                stopFreeLooking();
            }
        }
    }

    public static void toggleFreeLooking() {
        if (isFreeLooking) {
            stopFreeLooking();
        } else {
            startFreeLooking();
        }
    }

    public static void startFreeLooking() {
        int currentPerspective = PerspectiveManager.getCurrentPerspective();
        if (currentPerspective != lastPerspective) {
            lastPerspective = currentPerspective;
        }

        int perspective = FreeLookConfig.INSTANCE.perspectiveMode;


        switch (FreeLookConfig.INSTANCE.changePerspective) {
            case 0:
                break;
            case 1:
                if (lastPerspective == 0) {
                    PerspectiveManager.setPerspective(perspective);
                }
                break;
            case 2:
                if (lastPerspective != 0) {
                    PerspectiveManager.setPerspective(perspective);
                }
                break;
            case 3:
                PerspectiveManager.setPerspective(perspective);
                break;
        }

        if (FreeLookConfig.INSTANCE.smoothCamera) {
            timer.reset();
            lastUpdateTime = System.currentTimeMillis();
        }

        if (OmniClient.getInstance().player instanceof CameraOverriddenEntity) {
            CameraOverriddenEntity ov = (CameraOverriddenEntity) OmniClient.getInstance().player;
            ov.freelook$setCameraYaw(OmniClientPlayer.getYaw());
            ov.freelook$setCameraPitch(OmniClientPlayer.getPitch());
        }

        isFreeLooking = true;
    }

    public static void stopFreeLooking() {
        isFreeLooking = false;
        PerspectiveManager.setPerspective(lastPerspective);
        timer.finishNow();
        //#if MC <= 1.12.2
        //$$ OmniClient.getInstance().renderGlobal.setDisplayListEntitiesDirty();
        //#endif
    }

    public static float applySmoothScale(float z) {
        if (!isFreeLooking || timer.isFinished() || !FreeLookConfig.INSTANCE.smoothCamera) {
            return z;
        }

        long currentTime = System.currentTimeMillis();
        long delta = currentTime - lastUpdateTime;
        lastUpdateTime = currentTime;
        timer.update(delta);
        float transitionProgress = timer.getValue();
        float scale = 0.125f + transitionProgress * (1.0f - 0.125f);
        return z * scale;
    }
}
