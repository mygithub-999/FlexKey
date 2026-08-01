package com.example.ui.screens

import android.content.Context
import android.content.Intent
import android.provider.Settings
import android.view.inputmethod.InputMethodManager
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Keyboard
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.outlined.Circle
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.database.ClipboardEntity
import com.example.data.model.Candidate
import com.example.data.model.KeyModel
import com.example.data.model.KeyType
import com.example.data.model.KeyboardLayoutType
import com.example.data.model.KeyboardTheme
import com.example.data.model.ShiftState
import com.example.engine.SuggestionEngine
import com.example.ui.components.KeyboardCanvas

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    activeTheme: KeyboardTheme,
    clipboardItems: List<ClipboardEntity>,
    onThemeChange: (KeyboardTheme) -> Unit,
    onNavigateToThemes: () -> Unit,
    onNavigateToClipboard: () -> Unit,
    onNavigateToDictionary: () -> Unit,
    onNavigateToSettings: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var isKeyboardEnabled by remember { mutableStateOf(checkIsKeyboardEnabled(context)) }
    var isKeyboardSelected by remember { mutableStateOf(checkIsKeyboardSelected(context)) }

    var selectedInputTypeIndex by remember { mutableIntStateOf(0) }
    var testInputText by remember { mutableStateOf("Testing FlexKey Keyboard ") }
    var showSimulatorKeyboard by remember { mutableStateOf(true) }

    var activeLayout by remember { mutableStateOf(KeyboardLayoutType.QWERTY) }
    var shiftState by remember { mutableStateOf(ShiftState.OFF) }

    val suggestionEngine = remember { SuggestionEngine() }
    var candidates by remember { mutableStateOf<List<Candidate>>(emptyList()) }

    val inputTypes = listOf("Text", "Password", "Email", "Number")

    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                isKeyboardEnabled = checkIsKeyboardEnabled(context)
                isKeyboardSelected = checkIsKeyboardSelected(context)
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    // Update real-time word suggestions as user types in playground
    LaunchedEffect(testInputText, selectedInputTypeIndex) {
        val lastWord = testInputText.split(Regex("\\s+")).lastOrNull() ?: ""
        candidates = suggestionEngine.generateSuggestions(
            query = lastWord,
            isPasswordField = selectedInputTypeIndex == 1
        )
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Hero Title Card
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(18.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Keyboard,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier
                            .height(42.dp)
                            .width(42.dp)
                    )
                    Spacer(modifier = Modifier.width(14.dp))
                    Column {
                        Text(
                            text = "FlexKey Keyboard",
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Text(
                            text = "Open-Source High-Performance Android IME Engine",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                        )
                    }
                }
            }

            // Onboarding Setup Wizard Card
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                shape = RoundedCornerShape(18.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "IME Setup Wizard",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    // Step 1: Enable Keyboard in System Settings
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(MaterialTheme.colorScheme.surface)
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = if (isKeyboardEnabled) Icons.Default.CheckCircle else Icons.Outlined.Circle,
                            contentDescription = null,
                            tint = if (isKeyboardEnabled) Color(0xFF10B981) else MaterialTheme.colorScheme.outline
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text("1. Enable FlexKey in System", fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                            Text("Grant Input Method permission", fontSize = 11.sp, color = MaterialTheme.colorScheme.outline)
                        }
                        Button(
                            onClick = {
                                context.startActivity(Intent(Settings.ACTION_INPUT_METHOD_SETTINGS))
                                isKeyboardEnabled = checkIsKeyboardEnabled(context)
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.testTag("step_enable_ime")
                        ) {
                            Text("Enable", fontSize = 12.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Step 2: Select Keyboard as Default
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(MaterialTheme.colorScheme.surface)
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = if (isKeyboardSelected) Icons.Default.CheckCircle else Icons.Outlined.Circle,
                            contentDescription = null,
                            tint = if (isKeyboardSelected) Color(0xFF10B981) else MaterialTheme.colorScheme.outline
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text("2. Switch Default Keyboard", fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                            Text("Set FlexKey as active IME", fontSize = 11.sp, color = MaterialTheme.colorScheme.outline)
                        }
                        Button(
                            onClick = {
                                val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
                                imm?.showInputMethodPicker()
                                isKeyboardSelected = checkIsKeyboardSelected(context)
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.testTag("step_select_ime")
                        ) {
                            Text("Select", fontSize = 12.sp)
                        }
                    }
                }
            }

            // Interactive Playground & Diagnostic Inspector
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(18.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(18.dp))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "IME Testing Playground",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                            )
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Security,
                                contentDescription = null,
                                tint = if (selectedInputTypeIndex == 1) Color(0xFFEF4444) else Color(0xFF10B981),
                                modifier = Modifier.height(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (selectedInputTypeIndex == 1) "Incognito Guard" else "Normal Field",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium,
                                color = if (selectedInputTypeIndex == 1) Color(0xFFEF4444) else Color(0xFF10B981)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Input Type Selector Tabs
                    ScrollableTabRow(
                        selectedTabIndex = selectedInputTypeIndex,
                        edgePadding = 0.dp,
                        containerColor = MaterialTheme.colorScheme.surfaceVariant,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .height(36.dp)
                    ) {
                        inputTypes.forEachIndexed { idx, label ->
                            Tab(
                                selected = selectedInputTypeIndex == idx,
                                onClick = { selectedInputTypeIndex = idx },
                                text = { Text(label, fontSize = 12.sp) }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Interactive Input Field
                    OutlinedTextField(
                        value = testInputText,
                        onValueChange = { testInputText = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { showSimulatorKeyboard = true }
                            .testTag("playground_input_field"),
                        label = { Text("Tap keys on the soft keyboard below to type") },
                        visualTransformation = if (selectedInputTypeIndex == 1) PasswordVisualTransformation() else VisualTransformation.None,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                            unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                        ),
                        shape = RoundedCornerShape(12.dp)
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Live Diagnostic Inspector Panel
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(MaterialTheme.colorScheme.inverseSurface)
                            .padding(10.dp)
                    ) {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Code,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.inverseOnSurface,
                                    modifier = Modifier.height(14.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "IME Engine Inspector Diagnostic",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.inverseOnSurface,
                                    fontFamily = FontFamily.Monospace
                                )
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "InputType: ${inputTypes[selectedInputTypeIndex]} | Active Theme: ${activeTheme.title}\nShift State: $shiftState | Incognito Guard: ${selectedInputTypeIndex == 1}",
                                fontSize = 10.sp,
                                color = MaterialTheme.colorScheme.inverseOnSurface.copy(alpha = 0.8f),
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }
                }
            }

            // Quick Navigation Hub
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .clickable { onNavigateToThemes() },
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(Icons.Default.Palette, contentDescription = null, tint = MaterialTheme.colorScheme.onSecondaryContainer)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("Themes", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }

                Card(
                    modifier = Modifier
                        .weight(1f)
                        .clickable { onNavigateToClipboard() },
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(Icons.Default.ContentCopy, contentDescription = null, tint = MaterialTheme.colorScheme.onTertiaryContainer)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("Clipboard Vault", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }

                Card(
                    modifier = Modifier
                        .weight(1f)
                        .clickable { onNavigateToDictionary() },
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(Icons.Default.Psychology, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("Shortcuts", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Keyboard Dock Controller Strip
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .clickable { showSimulatorKeyboard = !showSimulatorKeyboard }
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Keyboard,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.height(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (showSimulatorKeyboard) "Interactive Soft Keyboard (Active)" else "Show Soft Keyboard Simulator",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Icon(
                imageVector = if (showSimulatorKeyboard) Icons.Default.KeyboardArrowDown else Icons.Default.KeyboardArrowUp,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        // On-Screen Interactive Soft Keyboard Canvas
        AnimatedVisibility(
            visible = showSimulatorKeyboard,
            enter = fadeIn() + expandVertically(),
            exit = fadeOut() + shrinkVertically()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(activeTheme.backgroundColor)
            ) {
                KeyboardCanvas(
                    activeLayout = activeLayout,
                    shiftState = shiftState,
                    activeTheme = activeTheme,
                    candidates = candidates,
                    isIncognito = selectedInputTypeIndex == 1,
                    clipboardItems = clipboardItems,
                    currentInputText = testInputText,
                    onKeyClick = { keyModel ->
                        when (keyModel.keyType) {
                            KeyType.CHARACTER -> {
                                testInputText += keyModel.label
                                if (shiftState == ShiftState.SHIFTED_ONCE) shiftState = ShiftState.OFF
                            }
                            KeyType.SHIFT -> {
                                shiftState = when (shiftState) {
                                    ShiftState.OFF -> ShiftState.SHIFTED_ONCE
                                    ShiftState.SHIFTED_ONCE -> ShiftState.CAPS_LOCKED
                                    ShiftState.CAPS_LOCKED -> ShiftState.OFF
                                }
                            }
                            KeyType.DELETE -> {
                                if (testInputText.isNotEmpty()) {
                                    testInputText = testInputText.dropLast(1)
                                }
                            }
                            KeyType.SPACE -> {
                                testInputText += " "
                            }
                            KeyType.ENTER -> {
                                testInputText += "\n"
                            }
                            KeyType.LAYOUT_SWITCH -> {
                                activeLayout = when (activeLayout) {
                                    KeyboardLayoutType.QWERTY -> KeyboardLayoutType.SYMBOLS_PAGE_1
                                    KeyboardLayoutType.SYMBOLS_PAGE_1 -> KeyboardLayoutType.SYMBOLS_PAGE_2
                                    else -> KeyboardLayoutType.QWERTY
                                }
                            }
                            else -> {}
                        }
                    },
                    onCandidateClick = { candidate ->
                        val lastWord = testInputText.split(Regex("\\s+")).lastOrNull() ?: ""
                        if (lastWord.isNotEmpty() && testInputText.endsWith(lastWord)) {
                            testInputText = testInputText.dropLast(lastWord.length)
                        }
                        testInputText += "${candidate.word} "
                    },
                    onPinClipToggle = {},
                    onDeleteClip = {},
                    onClearUnpinnedClips = {},
                    onInsertText = { text -> testInputText += text },
                    onOpenSettings = onNavigateToSettings
                )
            }
        }
    }
}

private fun checkIsKeyboardEnabled(context: Context): Boolean {
    val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
    val enabledMethods = imm?.enabledInputMethodList ?: emptyList()
    return enabledMethods.any { it.packageName == context.packageName }
}

private fun checkIsKeyboardSelected(context: Context): Boolean {
    val selectedIme = Settings.Secure.getString(context.contentResolver, Settings.Secure.DEFAULT_INPUT_METHOD)
    return selectedIme != null && selectedIme.contains(context.packageName)
}
