package com.example.data.engine

import android.os.Build
import android.text.InputType
import android.text.TextUtils
import android.view.KeyEvent
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputConnection

class InputConnectionManager {

    var activeInputConnection: InputConnection? = null
    var activeEditorInfo: EditorInfo? = null

    fun safeCommitText(text: CharSequence, newCursorPosition: Int = 1): Boolean {
        return try {
            activeInputConnection?.commitText(text, newCursorPosition) ?: false
        } catch (e: Exception) {
            false
        }
    }

    fun safeDeleteSurroundingText(beforeLength: Int = 1, afterLength: Int = 0): Boolean {
        return try {
            val ic = activeInputConnection ?: return false
            val selectedText = ic.getSelectedText(0)
            if (!TextUtils.isEmpty(selectedText)) {
                ic.commitText("", 1)
            } else {
                ic.deleteSurroundingText(beforeLength, afterLength)
            }
            true
        } catch (e: Exception) {
            false
        }
    }

    fun safeGetTextBeforeCursor(n: Int = 100): String {
        return try {
            activeInputConnection?.getTextBeforeCursor(n, 0)?.toString() ?: ""
        } catch (e: Exception) {
            ""
        }
    }

    fun safeGetTextAfterCursor(n: Int = 100): String {
        return try {
            activeInputConnection?.getTextAfterCursor(n, 0)?.toString() ?: ""
        } catch (e: Exception) {
            ""
        }
    }

    fun safeSendKeyEvent(keyCode: Int): Boolean {
        return try {
            val ic = activeInputConnection ?: return false
            val down = KeyEvent(KeyEvent.ACTION_DOWN, keyCode)
            val up = KeyEvent(KeyEvent.ACTION_UP, keyCode)
            ic.sendKeyEvent(down) && ic.sendKeyEvent(up)
        } catch (e: Exception) {
            false
        }
    }

    fun safePerformAction(actionId: Int): Boolean {
        return try {
            activeInputConnection?.performEditorAction(actionId) ?: false
        } catch (e: Exception) {
            false
        }
    }

    fun safePerformContextMenuAction(id: Int): Boolean {
        return try {
            activeInputConnection?.performContextMenuAction(id) ?: false
        } catch (e: Exception) {
            false
        }
    }

    fun handleEnterKey(): Boolean {
        val ic = activeInputConnection ?: return false
        val info = activeEditorInfo
        val imeOptions = info?.imeOptions ?: 0
        val inputType = info?.inputType ?: 0

        val actionMasked = imeOptions and EditorInfo.IME_MASK_ACTION
        val actionId = if (info?.actionId != null && info.actionId != 0) info.actionId else actionMasked

        val isMultiLine = (inputType and InputType.TYPE_TEXT_FLAG_MULTI_LINE) != 0
        val noEnterAction = (imeOptions and EditorInfo.IME_FLAG_NO_ENTER_ACTION) != 0

        if (!isMultiLine && !noEnterAction && actionId != EditorInfo.IME_ACTION_UNSPECIFIED && actionId != EditorInfo.IME_ACTION_NONE) {
            val handled = try { ic.performEditorAction(actionId) } catch (e: Exception) { false }
            if (handled) return true
        }

        return try {
            if (isMultiLine || actionId == EditorInfo.IME_ACTION_UNSPECIFIED || actionId == EditorInfo.IME_ACTION_NONE) {
                val committed = ic.commitText("\n", 1)
                if (!committed) {
                    safeSendKeyEvent(KeyEvent.KEYCODE_ENTER)
                } else {
                    true
                }
            } else {
                safeSendKeyEvent(KeyEvent.KEYCODE_ENTER)
            }
        } catch (e: Exception) {
            safeSendKeyEvent(KeyEvent.KEYCODE_ENTER)
        }
    }

    fun isPasswordField(): Boolean {
        val info = activeEditorInfo ?: return false
        val inputType = info.inputType
        val variation = inputType and InputType.TYPE_MASK_VARIATION
        return variation == InputType.TYPE_TEXT_VARIATION_PASSWORD ||
                variation == InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD ||
                variation == InputType.TYPE_TEXT_VARIATION_WEB_PASSWORD ||
                (inputType and InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS) != 0
    }

    fun isAutoCapitalizeNeeded(): Boolean {
        val info = activeEditorInfo ?: return false
        val inputType = info.inputType
        if ((inputType and InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS) != 0) return true

        val textBefore = safeGetTextBeforeCursor(2)
        if ((inputType and InputType.TYPE_TEXT_FLAG_CAP_SENTENCES) != 0) {
            if (textBefore.isEmpty() || textBefore.endsWith(". ") || textBefore.endsWith("? ") || textBefore.endsWith("! ") || textBefore.endsWith("\n")) {
                return true
            }
        }
        return false
    }

    fun getCurrentWordUnderCursor(): String {
        val before = safeGetTextBeforeCursor(30)
        if (before.isEmpty()) return ""
        val lastWord = before.split(Regex("\\s+")).lastOrNull() ?: ""
        return lastWord.filter { it.isLetterOrDigit() }
    }
}
