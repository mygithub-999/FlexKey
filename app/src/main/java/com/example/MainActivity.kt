package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.example.data.database.AppDatabase
import com.example.data.database.ClipboardEntity
import com.example.data.database.UserDictionaryEntity
import com.example.data.model.KeyboardTheme
import com.example.ui.screens.ClipboardScreen
import com.example.ui.screens.DictionaryScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.screens.ThemeSelectorScreen
import com.example.ui.theme.MyApplicationTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

import android.view.WindowManager

enum class Screen {
    HOME,
    THEMES,
    CLIPBOARD,
    DICTIONARY,
    SETTINGS
}

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_SECURE,
            WindowManager.LayoutParams.FLAG_SECURE
        )
        enableEdgeToEdge()

        val database = AppDatabase.getInstance(this)
        val clipboardDao = database.clipboardDao()
        val userDictDao = database.userDictionaryDao()

        setContent {
            MyApplicationTheme {
                val coroutineScope = rememberCoroutineScope()
                var currentScreen by remember { mutableStateOf(Screen.HOME) }
                var activeTheme by remember { mutableStateOf(KeyboardTheme.MATERIAL_YOU) }

                val clipboardItems by clipboardDao.getAllClips().collectAsState(initial = emptyList())
                val dictionaryItems by userDictDao.getAllWords().collectAsState(initial = emptyList())

                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Crossfade(
                        targetState = currentScreen,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding),
                        label = "screenCrossfade"
                    ) { screen ->
                        when (screen) {
                            Screen.HOME -> {
                                HomeScreen(
                                    activeTheme = activeTheme,
                                    clipboardItems = clipboardItems,
                                    onThemeChange = { activeTheme = it },
                                    onNavigateToThemes = { currentScreen = Screen.THEMES },
                                    onNavigateToClipboard = { currentScreen = Screen.CLIPBOARD },
                                    onNavigateToDictionary = { currentScreen = Screen.DICTIONARY },
                                    onNavigateToSettings = { currentScreen = Screen.SETTINGS }
                                )
                            }

                            Screen.THEMES -> {
                                ThemeSelectorScreen(
                                    activeTheme = activeTheme,
                                    onThemeSelect = { selectedTheme ->
                                        activeTheme = selectedTheme
                                    },
                                    onBack = { currentScreen = Screen.HOME }
                                )
                            }

                            Screen.CLIPBOARD -> {
                                ClipboardScreen(
                                    clips = clipboardItems,
                                    onPinToggle = { clip ->
                                        coroutineScope.launch(Dispatchers.IO) {
                                            clipboardDao.updateClip(clip.copy(isPinned = !clip.isPinned))
                                        }
                                    },
                                    onDeleteClip = { id ->
                                        coroutineScope.launch(Dispatchers.IO) {
                                            clipboardDao.deleteClip(id)
                                        }
                                    },
                                    onClearUnpinned = {
                                        coroutineScope.launch(Dispatchers.IO) {
                                            clipboardDao.clearUnpinnedClips()
                                        }
                                    },
                                    onAddClip = { content ->
                                        coroutineScope.launch(Dispatchers.IO) {
                                            clipboardDao.insertClip(ClipboardEntity(content = content))
                                        }
                                    },
                                    onBack = { currentScreen = Screen.HOME }
                                )
                            }

                            Screen.DICTIONARY -> {
                                DictionaryScreen(
                                    words = dictionaryItems,
                                    onAddWord = { word, shortcut ->
                                        coroutineScope.launch(Dispatchers.IO) {
                                            userDictDao.insertWord(
                                                UserDictionaryEntity(
                                                    word = word,
                                                    shortcut = shortcut
                                                )
                                            )
                                        }
                                    },
                                    onDeleteWord = { id ->
                                        coroutineScope.launch(Dispatchers.IO) {
                                            userDictDao.deleteWord(id)
                                        }
                                    },
                                    onBack = { currentScreen = Screen.HOME }
                                )
                            }

                            Screen.SETTINGS -> {
                                SettingsScreen(
                                    onBack = { currentScreen = Screen.HOME }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
