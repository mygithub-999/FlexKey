package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ClearAll
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material.icons.outlined.PushPin
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.database.ClipboardEntity
import com.example.data.model.KeyboardTheme

@Composable
fun ClipboardVaultView(
    clips: List<ClipboardEntity>,
    activeTheme: KeyboardTheme,
    onClipClick: (String) -> Unit,
    onPinToggle: (ClipboardEntity) -> Unit,
    onDeleteClip: (Long) -> Unit,
    onClearUnpinned: () -> Unit,
    onClose: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(activeTheme.backgroundColor)
            .padding(8.dp)
    ) {
        // Header Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onClose, modifier = Modifier.testTag("clip_close")) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = activeTheme.keyTextColor
                    )
                }
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "Clipboard History Vault",
                    color = activeTheme.keyTextColor,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            if (clips.any { !it.isPinned }) {
                Button(
                    onClick = onClearUnpinned,
                    colors = ButtonDefaults.buttonColors(containerColor = activeTheme.keyColor),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.height(34.dp).testTag("clear_unpinned")
                ) {
                    Icon(Icons.Default.ClearAll, contentDescription = null, tint = activeTheme.keyTextColor, modifier = Modifier.height(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Clear All", fontSize = 11.sp, color = activeTheme.keyTextColor)
                }
            }
        }

        if (clips.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No copied text in Clipboard Vault yet.\nCopy text anywhere to see it here!",
                    color = activeTheme.keySecondaryTextColor,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(16.dp)
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                items(clips, key = { it.id }) { clip ->
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = if (clip.isPinned) activeTheme.keyColor else activeTheme.candidateBgColor
                        ),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onClipClick(clip.content) }
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 10.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = clip.content,
                                    color = activeTheme.keyTextColor,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Normal,
                                    maxLines = 3,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                IconButton(
                                    onClick = { onPinToggle(clip) },
                                    modifier = Modifier.height(32.dp).width(32.dp)
                                ) {
                                    Icon(
                                        imageVector = if (clip.isPinned) Icons.Default.PushPin else Icons.Outlined.PushPin,
                                        contentDescription = "Pin Clip",
                                        tint = if (clip.isPinned) activeTheme.accentColor else activeTheme.keySecondaryTextColor,
                                        modifier = Modifier.height(18.dp)
                                    )
                                }

                                IconButton(
                                    onClick = { onDeleteClip(clip.id) },
                                    modifier = Modifier.height(32.dp).width(32.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Delete,
                                        contentDescription = "Delete Clip",
                                        tint = activeTheme.keySecondaryTextColor,
                                        modifier = Modifier.height(18.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
