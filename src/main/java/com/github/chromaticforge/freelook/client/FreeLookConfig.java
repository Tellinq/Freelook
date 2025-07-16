package com.github.chromaticforge.freelook.client;

import dev.deftu.omnicore.client.OmniKeyboard;
import org.polyfrost.oneconfig.api.config.v1.Config;
import org.polyfrost.oneconfig.api.config.v1.annotations.*;
import org.polyfrost.oneconfig.api.config.v1.annotations.Number;
import org.polyfrost.oneconfig.api.ui.v1.keybind.KeybindManager;
import org.polyfrost.polyui.input.KeyBinder;
import org.polyfrost.polyui.input.KeybindHelper;

public class FreeLookConfig extends Config {
    @RadioButton(
            title = "Change Perspective",
            description = "Which camera perspective to start",
            options = {"Never", "First Person Only", "Third Person Only", "Always"}
    )
    public int changePerspective = 1;

    @RadioButton(
            title = "Starting Perspective",
            description = "Which camera perspective to start if changed",
            options = {"First Person",  "Third Person (Back)", "Third Person (Front)"}
    )
    public int perspectiveMode = 1;


    @Accordion(
            title = "Activation"
    )
    public static class Activation {

        @RadioButton(
                title = "Activation Mode",
                description = "Whether you can hold, toggle or do both to activate",
                options = {"Hold",  "Quick Press", "Toggle"},
                subcategory = "Activation"
        )
        public static int pressMode = 1;

        @Number(
                title = "Hold threshold",
                description = "How long you can hold before FreeLook is disabled upon release",
                unit = "ms",
                min = 0,
                max = Float.MAX_VALUE,
                subcategory = "Activation"
        )
        public static long holdThreshold = 300;
    }

    public static class MovementConfig {
        @Include
        public static boolean enabled = true;

        @Switch(title = "Invert")
        public static boolean invert = false;
    }

    @Accordion(title = "Pitch Movement", index = 2)
    public static class Pitch extends MovementConfig {
        @Switch(title = "Lock")
        public static boolean lock = true;
    }

    @Accordion(title = "Yaw Movement", index = 2)
    public static class Yaw extends MovementConfig {
        @Switch(title = "Lock")
        public static boolean lock = false;
    }


    @Switch(title = "Add to Camera Cycle")
    public boolean addToCameraCycle = false;

    @RadioButton(
            title = "On Camera Cycle Change",
            options = {"Don't change FreeLook state", "Stop FreeLook", "Block Cycle Change"}
    )
    public int onCycleChange = 1;

    @Switch(title = "Smooth Camera")
    public boolean smoothCamera = false;

    @Keybind(title = "Freelook Keybind")
    public KeyBinder.Bind freelookbind = KeybindHelper.builder().keys(OmniKeyboard.KEY_F).doesZ((pressed) -> {
        switch (FreeLookConfig.Activation.pressMode) {
            case 1:
                FreeLookController.handlePressAndHold(pressed);
                break;
            case 2:
                if (pressed) {
                    FreeLookController.toggleFreeLooking();
                }
                break;
            default:
                if (pressed) {
                    FreeLookController.startFreeLooking();
                } else {
                    FreeLookController.stopFreeLooking();
                }
        }
        return pressed;
    }).build();

    public static final FreeLookConfig INSTANCE = new FreeLookConfig();

    public FreeLookConfig() {
        super("freelook.json", "FreeLook", Category.QOL);
        initializeConfig();
        KeybindManager.registerKeybind(freelookbind);
    }
}

