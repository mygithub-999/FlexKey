package com.example.ui.components

import android.view.MotionEvent
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.pointerInteropFilter
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.KeyModel
import com.example.data.model.KeyType
import com.example.data.model.KeyboardTheme

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun KeyButton(
    keyModel: KeyModel,
    activeTheme: KeyboardTheme,
    onKeyClick: (KeyModel) -> Unit,
    onKeyLongClick: ((KeyModel) -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    var isPressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(targetValue = if (isPressed) 0.92f else 1.0f, label = "keyScale")

    val isSpecialKey = keyModel.keyType != KeyType.CHARACTER && keyModel.keyType != KeyType.SPACE
    val backgroundColor = when {
        isPressed -> activeTheme.keyPressedColor
        isSpecialKey -> activeTheme.keyColor.copy(alpha = 0.85f)
        keyModel.keyType == KeyType.SPACE -> activeTheme.keyColor
        else -> activeTheme.keyColor
    }

    val textColor = when {
        keyModel.keyType == KeyType.ENTER || keyModel.keyType == KeyType.SHIFT && keyModel.label != "⇧" -> activeTheme.accentColor
        else -> activeTheme.keyTextColor
    }

    Box(
        modifier = modifier
            .fillMaxHeight()
            .padding(horizontal = 2.dp, vertical = 3.dp)
            .scale(scale)
            .shadow(
                elevation = if (isPressed) 1.dp else 2.dp,
                shape = RoundedCornerShape(8.dp)
            )
            .clip(RoundedCornerShape(8.dp))
            .background(backgroundColor)
            .testTag("key_${keyModel.label}")
            .pointerInput(keyModel) {
                detectTapGestures(
                    onPress = {
                        isPressed = true
                        tryAwaitRelease()
                        isPressed = false
                    },
                    onTap = {
                        onKeyClick(keyModel)
                    },
                    onLongPress = {
                        if (onKeyLongClick != null) {
                            onKeyLongClick(keyModel)
                        } else if (keyModel.secondaryLabel != null) {
                            onKeyClick(
                                KeyModel(
                                    keyCode = keyModel.secondaryLabel.first().code,
                                    label = keyModel.secondaryLabel
                                )
                            )
                        } else {
                            onKeyClick(keyModel)
                        }
                    }
                )
            },
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxSize()
        ) {
            // Secondary Hint Label (e.g. number on top right)
            if (keyModel.secondaryLabel != null && keyModel.keyType == KeyType.CHARACTER) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(end = 4.dp, top = 2.dp),
                    contentAlignment = Alignment.TopEnd
                ) {
                    Text(
                        text = keyModel.secondaryLabel,
                        color = activeTheme.keySecondaryTextColor,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = keyModel.label,
                    color = textColor,
                    fontSize = when {
                        keyModel.label.length > 2 -> 12.sp
                        keyModel.label.length == 2 -> 14.sp
                        else -> 18.sp
                    },
                    fontWeight = if (isSpecialKey) FontWeight.Bold else FontWeight.Medium
                )
            }
        }
    }
}
