package com.example.ui.components

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.example.data.database.ClipboardEntity
import com.example.data.model.Candidate
import com.example.data.model.KeyModel
import com.example.data.model.KeyboardLayoutType
import com.example.data.model.KeyboardLayouts
import com.example.data.model.KeyboardTheme
import com.example.data.model.ShiftState

enum class KeyboardSubView {
    GRID,
    EMOJI,
    CLIPBOARD,
    AI_ASSISTANT
}

@Composable
fun KeyboardCanvas(
    activeLayout: KeyboardLayoutType,
    shiftState: ShiftState,
    activeTheme: KeyboardTheme,
    candidates: List<Candidate>,
    isIncognito: Boolean,
    clipboardItems: List<ClipboardEntity>,
    currentInputText: String,
    enterKeyLabel: String = "↵",
    onKeyClick: (KeyModel) -> Unit,
    onKeyLongClick: ((KeyModel) -> Unit)? = null,
    onCandidateClick: (Candidate) -> Unit,
    onPinClipToggle: (ClipboardEntity) -> Unit,
    onDeleteClip: (Long) -> Unit,
    onClearUnpinnedClips: () -> Unit,
    onInsertText: (String) -> Unit,
    onOpenSettings: () -> Unit,
    modifier: Modifier = Modifier
) {
    var subView by remember { mutableStateOf(KeyboardSubView.GRID) }

    val rows = remember(activeLayout, shiftState, enterKeyLabel) {
        KeyboardLayouts.getRows(activeLayout, shiftState, enterKeyLabel)
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .height(280.dp)
            .background(activeTheme.backgroundColor)
            .testTag("keyboard_canvas_root")
    ) {
        // Top Suggestion / Action Strip
        SuggestionStrip(
            candidates = candidates,
            isIncognito = isIncognito,
            activeTheme = activeTheme,
            onCandidateClick = onCandidateClick,
            onClipboardClick = { subView = KeyboardSubView.CLIPBOARD },
            onEmojiClick = { subView = KeyboardSubView.EMOJI },
            onThemeClick = { subView = KeyboardSubView.GRID },
            onAiAssistClick = { subView = KeyboardSubView.AI_ASSISTANT },
            onSettingsClick = onOpenSettings
        )

        // Main Keyboard Panel
        Crossfade(
            targetState = subView,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            label = "keyboardSubViewCrossfade"
        ) { currentView ->
            when (currentView) {
                KeyboardSubView.GRID -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 4.dp, vertical = 2.dp),
                        verticalArrangement = Arrangement.SpaceEvenly
                    ) {
                        rows.forEach { rowKeys ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .weight(1f),
                                horizontalArrangement = Arrangement.SpaceEvenly,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                rowKeys.forEach { keyModel ->
                                    KeyButton(
                                        keyModel = keyModel,
                                        activeTheme = activeTheme,
                                        onKeyClick = { clickedKey ->
                                            if (clickedKey.keyCode == -3) {
                                                subView = KeyboardSubView.EMOJI
                                            } else if (clickedKey.keyCode == -4) {
                                                onOpenSettings()
                                            } else {
                                                onKeyClick(clickedKey)
                                            }
                                        },
                                        onKeyLongClick = onKeyLongClick,
                                        modifier = Modifier.weight(keyModel.weight)
                                    )
                                }
                            }
                        }
                    }
                }

                KeyboardSubView.EMOJI -> {
                    EmojiPickerView(
                        activeTheme = activeTheme,
                        onEmojiSelected = { emoji ->
                            onInsertText(emoji)
                        },
                        onClose = { subView = KeyboardSubView.GRID }
                    )
                }

                KeyboardSubView.CLIPBOARD -> {
                    ClipboardVaultView(
                        clips = clipboardItems,
                        activeTheme = activeTheme,
                        onClipClick = { clipContent ->
                            onInsertText(clipContent)
                            subView = KeyboardSubView.GRID
                        },
                        onPinToggle = onPinClipToggle,
                        onDeleteClip = onDeleteClip,
                        onClearUnpinned = onClearUnpinnedClips,
                        onClose = { subView = KeyboardSubView.GRID }
                    )
                }

                KeyboardSubView.AI_ASSISTANT -> {
                    AiSmartAssistantBar(
                        activeTheme = activeTheme,
                        currentText = currentInputText,
                        onApplyText = { text ->
                            onInsertText(text)
                            subView = KeyboardSubView.GRID
                        },
                        onClose = { subView = KeyboardSubView.GRID }
                    )
                }
            }
        }
    }
}
