package com.example.engine

import com.example.data.model.ShiftState

class ShiftStateMachine {

    var state: ShiftState = ShiftState.OFF
        private set

    private var lastShiftTapTimestamp: Long = 0L
    private val DOUBLE_TAP_TIMEOUT_MS = 350L

    fun onShiftTap(): ShiftState {
        val now = System.currentTimeMillis()
        val timeSinceLastTap = now - lastShiftTapTimestamp
        lastShiftTapTimestamp = now

        state = when {
            state == ShiftState.CAPS_LOCKED -> ShiftState.OFF
            state == ShiftState.SHIFTED_ONCE -> {
                if (timeSinceLastTap < DOUBLE_TAP_TIMEOUT_MS) {
                    ShiftState.CAPS_LOCKED
                } else {
                    ShiftState.OFF
                }
            }
            state == ShiftState.OFF -> {
                if (timeSinceLastTap < DOUBLE_TAP_TIMEOUT_MS) {
                    ShiftState.CAPS_LOCKED
                } else {
                    ShiftState.SHIFTED_ONCE
                }
            }
            else -> ShiftState.OFF
        }
        return state
    }

    fun onCharacterTyped(): ShiftState {
        if (state == ShiftState.SHIFTED_ONCE) {
            state = ShiftState.OFF
        }
        return state
    }

    fun setShift(newState: ShiftState) {
        state = newState
    }
}
