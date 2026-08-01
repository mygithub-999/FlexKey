package com.example.engine

import android.content.Context
import android.media.AudioManager
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.view.KeyEvent
import com.example.data.model.KeyModel
import kotlin.math.exp
import kotlin.math.hypot
import kotlin.math.pow

data class PointF(val x: Float, val y: Float, val time: Long = System.currentTimeMillis())

class SpatialModel {
    private val standardDev = 40.0f // pixel standard deviation for key touch

    fun calculateKeyProbability(touchX: Float, touchY: Float, keyCenterX: Float, keyCenterY: Float): Double {
        val distanceSquare = (touchX - keyCenterX).pow(2) + (touchY - keyCenterY).pow(2)
        return exp(-distanceSquare / (2 * standardDev * standardDev)).toDouble()
    }

    fun findClosestKey(touchX: Float, touchY: Float, keysWithBounds: List<Pair<KeyModel, List<Float>>>): KeyModel? {
        var closestKey: KeyModel? = null
        var minDistance = Float.MAX_VALUE

        for ((key, bounds) in keysWithBounds) {
            // bounds: [left, top, right, bottom]
            val centerX = (bounds[0] + bounds[2]) / 2f
            val centerY = (bounds[1] + bounds[3]) / 2f
            val distance = hypot(touchX - centerX, touchY - centerY)
            if (distance < minDistance) {
                minDistance = distance
                closestKey = key
            }
        }
        return closestKey
    }
}

class GlideTypingEngine {

    private val touchPoints = mutableListOf<PointF>()

    fun onTouchDown(x: Float, y: Float) {
        touchPoints.clear()
        touchPoints.add(PointF(x, y))
    }

    fun onTouchMove(x: Float, y: Float) {
        val last = touchPoints.lastOrNull()
        if (last == null || hypot(x - last.x, y - last.y) > 12f) {
            touchPoints.add(PointF(x, y))
        }
    }

    fun onTouchUp(x: Float, y: Float, keyBoundsMap: List<Pair<KeyModel, List<Float>>>): String? {
        touchPoints.add(PointF(x, y))
        if (touchPoints.size < 4) {
            touchPoints.clear()
            return null
        }

        // Map path trajectory points to touched keys
        val spatialModel = SpatialModel()
        val keySequence = mutableListOf<Char>()

        for (point in touchPoints) {
            val key = spatialModel.findClosestKey(point.x, point.y, keyBoundsMap)
            if (key != null && key.label.length == 1 && key.label.first().isLetter()) {
                val char = key.label.lowercase().first()
                if (keySequence.lastOrNull() != char) {
                    keySequence.add(char)
                }
            }
        }

        touchPoints.clear()

        if (keySequence.isEmpty()) return null
        return keySequence.joinToString("")
    }

    fun getActivePath(): List<PointF> = touchPoints.toList()
}

class HapticAndSoundFeedback(private val context: Context) {

    private val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as? AudioManager
    private val vibrator: Vibrator? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
        vibratorManager?.defaultVibrator
    } else {
        @Suppress("DEPRECATION")
        context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
    }

    var isHapticEnabled: Boolean = true
    var isSoundEnabled: Boolean = true

    fun performKeyFeedback(keyType: com.example.data.model.KeyType = com.example.data.model.KeyType.CHARACTER) {
        if (isSoundEnabled) {
            val soundFx = when (keyType) {
                com.example.data.model.KeyType.DELETE -> AudioManager.FX_KEYPRESS_DELETE
                com.example.data.model.KeyType.ENTER -> AudioManager.FX_KEYPRESS_RETURN
                com.example.data.model.KeyType.SPACE -> AudioManager.FX_KEYPRESS_SPACEBAR
                else -> AudioManager.FX_KEYPRESS_STANDARD
            }
            audioManager?.playSoundEffect(soundFx, 0.5f)
        }

        if (isHapticEnabled && vibrator?.hasVibrator() == true) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                vibrator.vibrate(VibrationEffect.createPredefined(VibrationEffect.EFFECT_CLICK))
            } else {
                @Suppress("DEPRECATION")
                vibrator.vibrate(15L)
            }
        }
    }
}
