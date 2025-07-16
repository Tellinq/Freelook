package com.github.chromaticforge.freelook.client

import net.minecraft.client.network.ClientPlayerEntity

object CameraStateTracker {
    private val pitchMap = mutableMapOf<ClientPlayerEntity, Float>()
    private val yawMap = mutableMapOf<ClientPlayerEntity, Float>()

    fun getCameraPitch(entity: ClientPlayerEntity?): Float = pitchMap[entity] ?: 0f
    fun getCameraYaw(entity: ClientPlayerEntity?): Float = yawMap[entity] ?: 0f

    fun setCameraPitch(entity: ClientPlayerEntity?, value: Float) {
        pitchMap[entity as ClientPlayerEntity] = value
    }

    fun setCameraYaw(entity: ClientPlayerEntity?, value: Float) {
        yawMap[entity as ClientPlayerEntity] = value
    }
}
