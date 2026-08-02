package com.example.data.model

import android.view.KeyEvent

object KeyboardLayouts {

    fun getRows(layoutType: KeyboardLayoutType, shiftState: ShiftState, enterLabel: String = "↵"): List<List<KeyModel>> {
        return when (layoutType) {
            KeyboardLayoutType.QWERTY -> getQwertyRows(shiftState, enterLabel)
            KeyboardLayoutType.QWERTZ -> getQwertzRows(shiftState, enterLabel)
            KeyboardLayoutType.AZERTY -> getAzertyRows(shiftState, enterLabel)
            KeyboardLayoutType.SYMBOLS_PAGE_1 -> getSymbolsPage1Rows(enterLabel)
            KeyboardLayoutType.SYMBOLS_PAGE_2 -> getSymbolsPage2Rows(enterLabel)
            KeyboardLayoutType.NUMERIC -> getNumericRows()
            KeyboardLayoutType.EMOJI -> getQwertyRows(shiftState, enterLabel)
        }
    }

    private fun getQwertyRows(shiftState: ShiftState, enterLabel: String = "↵"): List<List<KeyModel>> {
        val row1Letters = listOf("q", "w", "e", "r", "t", "y", "u", "i", "o", "p")
        val row1Digits = listOf("1", "2", "3", "4", "5", "6", "7", "8", "9", "0")
        val row2Letters = listOf("a", "s", "d", "f", "g", "h", "j", "k", "l")
        val row2Symbols = listOf("@", "#", "$", "%", "&", "-", "+", "(", ")")
        val row3Letters = listOf("z", "x", "c", "v", "b", "n", "m")

        val row1 = row1Letters.mapIndexed { idx, char ->
            val display = if (shiftState != ShiftState.OFF) char.uppercase() else char
            KeyModel(
                keyCode = display.first().code,
                label = display,
                secondaryLabel = row1Digits[idx]
            )
        }

        val row2 = row2Letters.mapIndexed { idx, char ->
            val display = if (shiftState != ShiftState.OFF) char.uppercase() else char
            KeyModel(
                keyCode = display.first().code,
                label = display,
                secondaryLabel = row2Symbols.getOrNull(idx)
            )
        }

        val row3 = mutableListOf<KeyModel>()
        val shiftLabel = when (shiftState) {
            ShiftState.OFF -> "⇧"
            ShiftState.SHIFTED_ONCE -> "⇪"
            ShiftState.CAPS_LOCKED -> "🔒"
        }
        row3.add(KeyModel(keyCode = -1, label = shiftLabel, keyType = KeyType.SHIFT, weight = 1.4f))

        row3Letters.forEach { char ->
            val display = if (shiftState != ShiftState.OFF) char.uppercase() else char
            row3.add(KeyModel(keyCode = display.first().code, label = display))
        }

        row3.add(KeyModel(keyCode = KeyEvent.KEYCODE_DEL, label = "⌫", keyType = KeyType.DELETE, weight = 1.4f))

        val row4 = listOf(
            KeyModel(keyCode = -2, label = "?123", keyType = KeyType.LAYOUT_SWITCH, weight = 1.3f),
            KeyModel(keyCode = -3, label = "😊", keyType = KeyType.EMOJI_SWITCH, weight = 1.0f),
            KeyModel(keyCode = -4, label = "⚙️", keyType = KeyType.SETTINGS_SWITCH, weight = 1.0f),
            KeyModel(keyCode = ' '.code, label = "Space", keyType = KeyType.SPACE, weight = 3.8f),
            KeyModel(keyCode = '.'.code, label = ".", keyType = KeyType.CHARACTER, weight = 1.0f),
            KeyModel(keyCode = KeyEvent.KEYCODE_ENTER, label = enterLabel, keyType = KeyType.ENTER, weight = 1.4f)
        )

        return listOf(row1, row2, row3, row4)
    }

    private fun getQwertzRows(shiftState: ShiftState, enterLabel: String = "↵"): List<List<KeyModel>> {
        val row1Letters = listOf("q", "w", "e", "r", "t", "z", "u", "i", "o", "p")
        val row2Letters = listOf("a", "s", "d", "f", "g", "h", "j", "k", "l")
        val row3Letters = listOf("y", "x", "c", "v", "b", "n", "m")

        val row1 = row1Letters.map { char ->
            val display = if (shiftState != ShiftState.OFF) char.uppercase() else char
            KeyModel(keyCode = display.first().code, label = display)
        }
        val row2 = row2Letters.map { char ->
            val display = if (shiftState != ShiftState.OFF) char.uppercase() else char
            KeyModel(keyCode = display.first().code, label = display)
        }
        val row3 = mutableListOf<KeyModel>()
        row3.add(KeyModel(keyCode = -1, label = if (shiftState != ShiftState.OFF) "⇪" else "⇧", keyType = KeyType.SHIFT, weight = 1.4f))
        row3Letters.forEach { char ->
            val display = if (shiftState != ShiftState.OFF) char.uppercase() else char
            row3.add(KeyModel(keyCode = display.first().code, label = display))
        }
        row3.add(KeyModel(keyCode = KeyEvent.KEYCODE_DEL, label = "⌫", keyType = KeyType.DELETE, weight = 1.4f))

        val row4 = listOf(
            KeyModel(keyCode = -2, label = "?123", keyType = KeyType.LAYOUT_SWITCH, weight = 1.3f),
            KeyModel(keyCode = ' '.code, label = "Space", keyType = KeyType.SPACE, weight = 4.5f),
            KeyModel(keyCode = '.'.code, label = ".", keyType = KeyType.CHARACTER, weight = 1.0f),
            KeyModel(keyCode = KeyEvent.KEYCODE_ENTER, label = enterLabel, keyType = KeyType.ENTER, weight = 1.4f)
        )
        return listOf(row1, row2, row3, row4)
    }

    private fun getAzertyRows(shiftState: ShiftState, enterLabel: String = "↵"): List<List<KeyModel>> {
        val row1Letters = listOf("a", "z", "e", "r", "t", "y", "u", "i", "o", "p")
        val row2Letters = listOf("q", "s", "d", "f", "g", "h", "j", "k", "l", "m")
        val row3Letters = listOf("w", "x", "c", "v", "b", "n")

        val row1 = row1Letters.map { char ->
            val display = if (shiftState != ShiftState.OFF) char.uppercase() else char
            KeyModel(keyCode = display.first().code, label = display)
        }
        val row2 = row2Letters.map { char ->
            val display = if (shiftState != ShiftState.OFF) char.uppercase() else char
            KeyModel(keyCode = display.first().code, label = display)
        }
        val row3 = mutableListOf<KeyModel>()
        row3.add(KeyModel(keyCode = -1, label = if (shiftState != ShiftState.OFF) "⇪" else "⇧", keyType = KeyType.SHIFT, weight = 1.4f))
        row3Letters.forEach { char ->
            val display = if (shiftState != ShiftState.OFF) char.uppercase() else char
            row3.add(KeyModel(keyCode = display.first().code, label = display))
        }
        row3.add(KeyModel(keyCode = KeyEvent.KEYCODE_DEL, label = "⌫", keyType = KeyType.DELETE, weight = 1.4f))

        val row4 = listOf(
            KeyModel(keyCode = -2, label = "?123", keyType = KeyType.LAYOUT_SWITCH, weight = 1.3f),
            KeyModel(keyCode = ' '.code, label = "Espace", keyType = KeyType.SPACE, weight = 4.5f),
            KeyModel(keyCode = '.'.code, label = ".", keyType = KeyType.CHARACTER, weight = 1.0f),
            KeyModel(keyCode = KeyEvent.KEYCODE_ENTER, label = enterLabel, keyType = KeyType.ENTER, weight = 1.4f)
        )
        return listOf(row1, row2, row3, row4)
    }

    private fun getSymbolsPage1Rows(enterLabel: String = "↵"): List<List<KeyModel>> {
        val row1 = listOf("1", "2", "3", "4", "5", "6", "7", "8", "9", "0").map {
            KeyModel(keyCode = it.first().code, label = it)
        }
        val row2 = listOf("@", "#", "$", "_", "&", "-", "+", "(", ")", "/").map {
            KeyModel(keyCode = it.first().code, label = it)
        }
        val row3 = mutableListOf<KeyModel>()
        row3.add(KeyModel(keyCode = -5, label = "1/2", keyType = KeyType.ACTION, weight = 1.4f))
        listOf("*", "\"", "'", ":", ";", "!", "?").forEach {
            row3.add(KeyModel(keyCode = it.first().code, label = it))
        }
        row3.add(KeyModel(keyCode = KeyEvent.KEYCODE_DEL, label = "⌫", keyType = KeyType.DELETE, weight = 1.4f))

        val row4 = listOf(
            KeyModel(keyCode = -6, label = "ABC", keyType = KeyType.LAYOUT_SWITCH, weight = 1.5f),
            KeyModel(keyCode = ','.code, label = ",", keyType = KeyType.CHARACTER, weight = 1.0f),
            KeyModel(keyCode = ' '.code, label = "Space", keyType = KeyType.SPACE, weight = 3.5f),
            KeyModel(keyCode = '.'.code, label = ".", keyType = KeyType.CHARACTER, weight = 1.0f),
            KeyModel(keyCode = KeyEvent.KEYCODE_ENTER, label = enterLabel, keyType = KeyType.ENTER, weight = 1.5f)
        )
        return listOf(row1, row2, row3, row4)
    }

    private fun getSymbolsPage2Rows(enterLabel: String = "↵"): List<List<KeyModel>> {
        val row1 = listOf("~", "`", "|", "•", "√", "π", "÷", "×", "¶", "∆").map {
            KeyModel(keyCode = it.first().code, label = it)
        }
        val row2 = listOf("£", "¥", "€", "¢", "^", "°", "=", "{", "}", "\\").map {
            KeyModel(keyCode = it.first().code, label = it)
        }
        val row3 = mutableListOf<KeyModel>()
        row3.add(KeyModel(keyCode = -5, label = "2/2", keyType = KeyType.ACTION, weight = 1.4f))
        listOf("%", "<", ">", "[", "]", "™", "©").forEach {
            row3.add(KeyModel(keyCode = it.first().code, label = it))
        }
        row3.add(KeyModel(keyCode = KeyEvent.KEYCODE_DEL, label = "⌫", keyType = KeyType.DELETE, weight = 1.4f))

        val row4 = listOf(
            KeyModel(keyCode = -6, label = "ABC", keyType = KeyType.LAYOUT_SWITCH, weight = 1.5f),
            KeyModel(keyCode = ','.code, label = ",", keyType = KeyType.CHARACTER, weight = 1.0f),
            KeyModel(keyCode = ' '.code, label = "Space", keyType = KeyType.SPACE, weight = 3.5f),
            KeyModel(keyCode = '.'.code, label = ".", keyType = KeyType.CHARACTER, weight = 1.0f),
            KeyModel(keyCode = KeyEvent.KEYCODE_ENTER, label = enterLabel, keyType = KeyType.ENTER, weight = 1.5f)
        )
        return listOf(row1, row2, row3, row4)
    }

    private fun getNumericRows(): List<List<KeyModel>> {
        val row1 = listOf("1", "2", "3").map { KeyModel(keyCode = it.first().code, label = it) }
        val row2 = listOf("4", "5", "6").map { KeyModel(keyCode = it.first().code, label = it) }
        val row3 = listOf("7", "8", "9").map { KeyModel(keyCode = it.first().code, label = it) }
        val row4 = listOf(
            KeyModel(keyCode = '.'.code, label = "."),
            KeyModel(keyCode = '0'.code, label = "0"),
            KeyModel(keyCode = KeyEvent.KEYCODE_DEL, label = "⌫", keyType = KeyType.DELETE)
        )
        return listOf(row1, row2, row3, row4)
    }
}
