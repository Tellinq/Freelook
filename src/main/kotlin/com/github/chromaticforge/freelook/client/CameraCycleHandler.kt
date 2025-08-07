package com.github.chromaticforge.freelook.client

import com.github.chromaticforge.freelook.client.config.FreelookConfig
import dev.deftu.omnicore.client.OmniPerspective

object CameraCycleHandler {
    var hasCycledFreelook = false

    @JvmStatic
    fun shouldOverrideCameraCycle(): Boolean {
        when (FreelookConfig.onCycleChange) {
            1 -> FreelookController.perspectiveToggled = false
            2 -> if (FreelookController.perspectiveToggled && !hasCycledFreelook) {
                return false
            }
        }

        if (FreelookConfig.addToCameraCycle) {
            if (hasCycledFreelook) {
                hasCycledFreelook = false
                FreelookController.stop()
            } else if (
                OmniPerspective.rawCurrentPerspective ==
                OmniPerspective.ALL.size - 1
            ) {
                FreelookController.start()
                hasCycledFreelook = true
                return false
            }
        }

        return true
    }
}
