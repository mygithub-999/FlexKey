package com.example.service

import android.content.ClipboardManager
import android.content.Context
import android.inputmethodservice.InputMethodService
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.setViewTreeLifecycleOwner
import androidx.lifecycle.setViewTreeViewModelStoreOwner
import androidx.savedstate.SavedStateRegistry
import androidx.savedstate.SavedStateRegistryController
import androidx.savedstate.SavedStateRegistryOwner
import androidx.savedstate.setViewTreeSavedStateRegistryOwner
import com.example.data.database.AppDatabase
import com.example.data.database.ClipboardEntity
import com.example.data.engine.InputConnectionManager
import com.example.data.model.Candidate
import com.example.data.model.KeyModel
import com.example.data.model.KeyType
import com.example.data.model.KeyboardLayoutType
import com.example.data.model.KeyboardTheme
import com.example.data.model.ShiftState
import com.example.engine.HapticAndSoundFeedback
import com.example.engine.ShiftStateMachine
import com.example.engine.SuggestionEngine
import com.example.ui.components.KeyboardCanvas
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class FlexKeyboardService : InputMethodService(), LifecycleOwner, ViewModelStoreOwner, SavedStateRegistryOwner {

    private val lifecycleRegistry = LifecycleRegistry(this)
    private val store = ViewModelStore()
    private val savedStateRegistryController = SavedStateRegistryController.create(this)

    override val lifecycle: Lifecycle get() = lifecycleRegistry
    override val viewModelStore: ViewModelStore get() = store
    override val savedStateRegistry: SavedStateRegistry get() = savedStateRegistryController.savedStateRegistry

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    private val inputManager = InputConnectionManager()
    private val shiftStateMachine = ShiftStateMachine()
    private val suggestionEngine = SuggestionEngine()
    private lateinit var feedback: HapticAndSoundFeedback
    private lateinit var database: AppDatabase

    private var activeLayout by mutableStateOf(KeyboardLayoutType.QWERTY)
    private var shiftState by mutableStateOf(ShiftState.OFF)
    private var activeTheme by mutableStateOf(KeyboardTheme.MATERIAL_YOU)
    private var isIncognito by mutableStateOf(false)
    private var candidates by mutableStateOf<List<Candidate>>(emptyList())
    private var currentTextBeforeCursor by mutableStateOf("")

    override fun onCreate() {
        super.onCreate()
        savedStateRegistryController.performAttach()
        savedStateRegistryController.performRestore(null)
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_CREATE)
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_START)
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_RESUME)

        feedback = HapticAndSoundFeedback(this)
        database = AppDatabase.getInstance(this)

        serviceScope.launch {
            database.userDictionaryDao().getAllWords().collect { words ->
                suggestionEngine.loadUserDictionary(words)
            }
        }
    }

    override fun onCreateInputView(): View {
        window?.window?.decorView?.let { decorView ->
            decorView.setViewTreeLifecycleOwner(this)
            decorView.setViewTreeViewModelStoreOwner(this)
            decorView.setViewTreeSavedStateRegistryOwner(this)
        }

        val composeView = ComposeView(this).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnLifecycleDestroyed(this@FlexKeyboardService))
            setViewTreeLifecycleOwner(this@FlexKeyboardService)
            setViewTreeViewModelStoreOwner(this@FlexKeyboardService)
            setViewTreeSavedStateRegistryOwner(this@FlexKeyboardService)
        }

        val clipboardFlow = database.clipboardDao().getAllClips().stateIn(
            scope = serviceScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

        composeView.setContent {
            val clipboardItems by clipboardFlow.collectAsState()

            KeyboardCanvas(
                activeLayout = activeLayout,
                shiftState = shiftState,
                activeTheme = activeTheme,
                candidates = candidates,
                isIncognito = isIncognito,
                clipboardItems = clipboardItems,
                currentInputText = currentTextBeforeCursor,
                onKeyClick = { keyModel -> handleKeyClick(keyModel) },
                onCandidateClick = { candidate -> handleCandidateClick(candidate) },
                onPinClipToggle = { clip ->
                    serviceScope.launch(Dispatchers.IO) {
                        database.clipboardDao().updateClip(clip.copy(isPinned = !clip.isPinned))
                    }
                },
                onDeleteClip = { clipId ->
                    serviceScope.launch(Dispatchers.IO) {
                        database.clipboardDao().deleteClip(clipId)
                    }
                },
                onClearUnpinnedClips = {
                    serviceScope.launch(Dispatchers.IO) {
                        database.clipboardDao().clearUnpinnedClips()
                    }
                },
                onInsertText = { text ->
                    inputManager.safeCommitText(text)
                    updateSuggestions()
                },
                onOpenSettings = {
                    val intent = packageManager.getLaunchIntentForPackage(packageName)
                    if (intent != null) {
                        intent.addFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK)
                        startActivity(intent)
                    }
                }
            )
        }

        return composeView
    }

    override fun onEvaluateInputViewShown(): Boolean {
        super.onEvaluateInputViewShown()
        return true
    }

    override fun onEvaluateFullscreenMode(): Boolean {
        return false
    }

    override fun onStartInputView(info: EditorInfo?, restarting: Boolean) {
        super.onStartInputView(info, restarting)
        if (lifecycleRegistry.currentState != Lifecycle.State.RESUMED) {
            lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_START)
            lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_RESUME)
        }

        inputManager.activeInputConnection = currentInputConnection
        inputManager.activeEditorInfo = info

        isIncognito = inputManager.isPasswordField()

        if (inputManager.isAutoCapitalizeNeeded()) {
            shiftStateMachine.setShift(ShiftState.SHIFTED_ONCE)
            shiftState = ShiftState.SHIFTED_ONCE
        } else {
            shiftStateMachine.setShift(ShiftState.OFF)
            shiftState = ShiftState.OFF
        }

        captureClipboard()
        updateSuggestions()
    }

    override fun onFinishInputView(finishingInput: Boolean) {
        super.onFinishInputView(finishingInput)
        inputManager.activeInputConnection = null
        inputManager.activeEditorInfo = null
    }

    private fun handleKeyClick(keyModel: KeyModel) {
        try {
            feedback.performKeyFeedback(keyModel.keyType)

            when (keyModel.keyType) {
                KeyType.CHARACTER -> {
                    val textToCommit = keyModel.label
                    inputManager.safeCommitText(textToCommit)
                    shiftState = shiftStateMachine.onCharacterTyped()
                }
                KeyType.SHIFT -> {
                    shiftState = shiftStateMachine.onShiftTap()
                }
                KeyType.DELETE -> {
                    inputManager.safeDeleteSurroundingText(1, 0)
                }
                KeyType.ENTER -> {
                    inputManager.handleEnterKey()
                }
                KeyType.SPACE -> {
                    inputManager.safeCommitText(" ")
                    if (inputManager.isAutoCapitalizeNeeded()) {
                        shiftStateMachine.setShift(ShiftState.SHIFTED_ONCE)
                        shiftState = ShiftState.SHIFTED_ONCE
                    }
                }
                KeyType.LAYOUT_SWITCH -> {
                    activeLayout = when (activeLayout) {
                        KeyboardLayoutType.QWERTY, KeyboardLayoutType.QWERTZ, KeyboardLayoutType.AZERTY -> KeyboardLayoutType.SYMBOLS_PAGE_1
                        KeyboardLayoutType.SYMBOLS_PAGE_1 -> KeyboardLayoutType.SYMBOLS_PAGE_2
                        KeyboardLayoutType.SYMBOLS_PAGE_2 -> KeyboardLayoutType.QWERTY
                        else -> KeyboardLayoutType.QWERTY
                    }
                }
                KeyType.ACTION -> {
                    if (keyModel.label == "1/2") activeLayout = KeyboardLayoutType.SYMBOLS_PAGE_2
                    else if (keyModel.label == "2/2") activeLayout = KeyboardLayoutType.SYMBOLS_PAGE_1
                }
                else -> {}
            }

            updateSuggestions()
        } catch (e: Throwable) {
            // Prevent crashes
        }
    }

    private fun handleCandidateClick(candidate: Candidate) {
        try {
            feedback.performKeyFeedback(KeyType.CHARACTER)
            val currentWord = inputManager.getCurrentWordUnderCursor()
            if (currentWord.isNotEmpty()) {
                inputManager.safeDeleteSurroundingText(currentWord.length, 0)
            }
            inputManager.safeCommitText("${candidate.word} ")
            updateSuggestions()
        } catch (e: Throwable) {
            // Prevent crashes
        }
    }

    private fun updateSuggestions() {
        try {
            currentTextBeforeCursor = inputManager.safeGetTextBeforeCursor(30)
            val currentWord = inputManager.getCurrentWordUnderCursor()
            candidates = suggestionEngine.generateSuggestions(
                query = currentWord,
                isPasswordField = isIncognito
            )
        } catch (e: Throwable) {
            candidates = emptyList()
        }
    }

    private fun captureClipboard() {
        try {
            val clipManager = getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager
            val primaryClip = clipManager?.primaryClip
            if (primaryClip != null && primaryClip.itemCount > 0) {
                val clipText = primaryClip.getItemAt(0).text?.toString()
                if (!clipText.isNullOrEmpty() && clipText.length < 500 && !isIncognito) {
                    serviceScope.launch(Dispatchers.IO) {
                        database.clipboardDao().insertClip(ClipboardEntity(content = clipText))
                    }
                }
            }
        } catch (e: Throwable) {
            // Ignore clipboard access restrictions
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_PAUSE)
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_STOP)
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_DESTROY)
        serviceScope.cancel()
        store.clear()
    }
}
