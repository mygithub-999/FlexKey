package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.EmojiEmotions
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Candidate
import com.example.data.model.CandidateSource
import com.example.data.model.KeyboardTheme

@Composable
fun SuggestionStrip(
    candidates: List<Candidate>,
    isIncognito: Boolean,
    activeTheme: KeyboardTheme,
    onCandidateClick: (Candidate) -> Unit,
    onClipboardClick: () -> Unit,
    onEmojiClick: () -> Unit,
    onThemeClick: () -> Unit,
    onAiAssistClick: () -> Unit,
    onSettingsClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(46.dp)
            .background(activeTheme.candidateBgColor)
            .padding(horizontal = 6.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Quick Action Tools Row
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            IconButton(
                onClick = onClipboardClick,
                modifier = Modifier
                    .testTag("action_clipboard")
                    .height(36.dp)
                    .width(36.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.ContentCopy,
                    contentDescription = "Clipboard History Vault",
                    tint = activeTheme.candidateTextColor,
                    modifier = Modifier.height(20.dp)
                )
            }

            IconButton(
                onClick = onEmojiClick,
                modifier = Modifier
                    .testTag("action_emoji")
                    .height(36.dp)
                    .width(36.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.EmojiEmotions,
                    contentDescription = "Emoji Picker",
                    tint = activeTheme.candidateTextColor,
                    modifier = Modifier.height(20.dp)
                )
            }

            IconButton(
                onClick = onThemeClick,
                modifier = Modifier
                    .testTag("action_theme")
                    .height(36.dp)
                    .width(36.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Palette,
                    contentDescription = "Theme Selector",
                    tint = activeTheme.candidateTextColor,
                    modifier = Modifier.height(20.dp)
                )
            }

            IconButton(
                onClick = onAiAssistClick,
                modifier = Modifier
                    .testTag("action_ai")
                    .height(36.dp)
                    .width(36.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.AutoAwesome,
                    contentDescription = "AI Smart Rewrite",
                    tint = activeTheme.accentColor,
                    modifier = Modifier.height(20.dp)
                )
            }
        }

        Box(
            modifier = Modifier
                .width(1.dp)
                .fillMaxHeight(0.6f)
                .background(activeTheme.keySecondaryTextColor.copy(alpha = 0.3f))
                .padding(horizontal = 4.dp)
        )

        Spacer(modifier = Modifier.width(4.dp))

        // Center Candidates or Incognito Guard Notice
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight(),
            contentAlignment = Alignment.Center
        ) {
            if (isIncognito) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(activeTheme.keyColor)
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Security,
                        contentDescription = "Incognito Mode Active",
                        tint = activeTheme.accentColor,
                        modifier = Modifier
                            .height(16.dp)
                            .padding(end = 6.dp)
                    )
                    Text(
                        text = "Incognito Password Guard Active",
                        color = activeTheme.keyTextColor,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            } else if (candidates.isEmpty()) {
                Text(
                    text = "FlexKey Engine",
                    color = activeTheme.keySecondaryTextColor.copy(alpha = 0.6f),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Normal
                )
            } else {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    candidates.forEach { candidate ->
                        val isAutoCorrect = candidate.source == CandidateSource.AUTOCORRECT || candidate.source == CandidateSource.USER_SHORTCUT
                        val fontStyle = if (isAutoCorrect) FontWeight.Bold else FontWeight.Medium

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight()
                                .padding(horizontal = 2.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isAutoCorrect) activeTheme.keyColor else activeTheme.candidateBgColor)
                                .clickable { onCandidateClick(candidate) },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = candidate.word,
                                color = activeTheme.candidateTextColor,
                                fontSize = 14.sp,
                                fontWeight = fontStyle,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }
        }

        IconButton(
            onClick = onSettingsClick,
            modifier = Modifier
                .testTag("action_settings")
                .height(36.dp)
                .width(36.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Settings,
                contentDescription = "Keyboard Settings",
                tint = activeTheme.candidateTextColor,
                modifier = Modifier.height(20.dp)
            )
        }
    }
}
