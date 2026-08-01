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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.KeyboardTheme

@Composable
fun AiSmartAssistantBar(
    activeTheme: KeyboardTheme,
    currentText: String,
    onApplyText: (String) -> Unit,
    onClose: () -> Unit,
    modifier: Modifier = Modifier
) {
    var generatedText by remember { mutableStateOf("") }

    val quickActions = listOf(
        "✨ Rephrase Professionally" to {
            generatedText = if (currentText.isNotBlank()) "Dear Recipient, $currentText. Best regards," else "Thank you for reaching out regarding this matter."
        },
        "📝 Fix Grammar" to {
            generatedText = if (currentText.isNotBlank()) currentText.trim().replaceFirstChar { it.uppercase() } + "." else "Everything looks grammatically correct."
        },
        "😊 Make Friendly" to {
            generatedText = if (currentText.isNotBlank()) "$currentText! Hope you're having a great day! 😊" else "Hey there! Hope you have an awesome day! 😊"
        },
        "🚀 Short Reply" to {
            generatedText = "Sounds good, let's proceed!"
        },
        "💡 Smart Email Intro" to {
            generatedText = "Hope this message finds you well."
        }
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(activeTheme.backgroundColor)
            .padding(8.dp)
    ) {
        // Top Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onClose, modifier = Modifier.testTag("ai_close")) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back",
                    tint = activeTheme.keyTextColor
                )
            }
            Spacer(modifier = Modifier.width(4.dp))
            Icon(
                imageVector = Icons.Default.AutoAwesome,
                contentDescription = null,
                tint = activeTheme.accentColor,
                modifier = Modifier.height(18.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = "AI Smart Writing Assistant",
                color = activeTheme.keyTextColor,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold
            )
        }

        // Quick Preset Actions Row
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            items(quickActions) { (label, action) ->
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .background(activeTheme.keyColor)
                        .clickable { action() }
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = label,
                        color = activeTheme.keyTextColor,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }

        // Output Result Card
        Card(
            colors = CardDefaults.cardColors(containerColor = activeTheme.candidateBgColor),
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(vertical = 4.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(10.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = generatedText.ifEmpty { if (currentText.isNotBlank()) "Current Text: \"$currentText\"\n\nSelect a smart rewrite mode above or type a custom prompt!" else "Select an AI action above to rephrase or generate text." },
                    color = activeTheme.keyTextColor,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Normal
                )

                if (generatedText.isNotEmpty()) {
                    Button(
                        onClick = {
                            onApplyText(generatedText)
                            onClose()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = activeTheme.accentColor),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(38.dp)
                            .testTag("apply_ai_text")
                    ) {
                        Text("Insert AI Generated Text", color = activeTheme.backgroundColor, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
