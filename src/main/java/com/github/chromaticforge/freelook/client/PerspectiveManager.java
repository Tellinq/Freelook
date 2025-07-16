package com.github.chromaticforge.freelook.client;

import dev.deftu.omnicore.client.OmniClient;
//#if MC >= 1.16.5
import net.minecraft.client.option.Perspective;
//#endif

public class PerspectiveManager {
    public static void setPerspective(int perspective) {
        //#if MC >= 1.16.5
        OmniClient.getInstance().options.setPerspective(Perspective.values()[perspective]);
        //#elseif MC <= 1.12.2
        //$$ OmniClient.getInstance().gameSettings.thirdPersonView = perspective;
        //#endif
    }

    public static int getCurrentPerspective() {
        //#if MC >= 1.16.5
        Perspective perspective = OmniClient.getInstance().options.getPerspective();
        return perspective.ordinal();
        //#else
        //$$ return OmniClient.getInstance().gameSettings.thirdPersonView;
        //#endif
    }

    /**
     * <= 1.12.2 will max at 2. Technically it could be expanded by other mods but tracking that might be difficult.
     * <br>
     * For modern MC perspective enum, this is completely possible to expand and track so long as this is being used.
     * @return The maximum perspective index
     */
    public static int getMaximumPerspectiveIndex() {
        //#if MC >= 1.16.5
        return Perspective.values().length - 1;
        //#else
        //$$ return 2;
        //#endif
    }
}
