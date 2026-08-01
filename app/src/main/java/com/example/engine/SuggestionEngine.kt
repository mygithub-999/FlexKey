package com.example.engine

import com.example.data.database.UserDictionaryEntity
import com.example.data.model.Candidate
import com.example.data.model.CandidateSource
import kotlin.math.abs
import kotlin.math.min

class TrieNode {
    val children = mutableMapOf<Char, TrieNode>()
    var isWord = false
    var frequency = 0
}

class TrieDictionary {
    private val root = TrieNode()

    fun insert(word: String, frequency: Int = 1) {
        var current = root
        for (char in word.lowercase()) {
            current = current.children.getOrPut(char) { TrieNode() }
        }
        current.isWord = true
        current.frequency = frequency
    }

    fun getPrefixMatches(prefix: String, limit: Int = 5): List<Pair<String, Int>> {
        var current = root
        val lowerPrefix = prefix.lowercase()
        for (char in lowerPrefix) {
            current = current.children[char] ?: return emptyList()
        }

        val results = mutableListOf<Pair<String, Int>>()
        dfsCollect(current, StringBuilder(lowerPrefix), results, limit * 3)
        return results.sortedByDescending { it.second }.take(limit)
    }

    private fun dfsCollect(node: TrieNode, currentWord: StringBuilder, results: MutableList<Pair<String, Int>>, maxCount: Int) {
        if (results.size >= maxCount) return
        if (node.isWord) {
            results.add(Pair(currentWord.toString(), node.frequency))
        }
        for ((char, child) in node.children) {
            currentWord.append(char)
            dfsCollect(child, currentWord, results, maxCount)
            currentWord.deleteCharAt(currentWord.length - 1)
        }
    }

    fun findFuzzyMatches(query: String, maxDistance: Int = 1): List<String> {
        val lowerQuery = query.lowercase()
        val results = mutableListOf<String>()
        dfsFuzzy(root, "", lowerQuery, results, maxDistance)
        return results.take(3)
    }

    private fun dfsFuzzy(node: TrieNode, current: String, query: String, results: MutableList<String>, maxDist: Int) {
        if (node.isWord && levenshteinDistance(current, query) <= maxDist && current.isNotEmpty()) {
            results.add(current)
        }
        if (current.length > query.length + maxDist) return
        for ((char, child) in node.children) {
            dfsFuzzy(child, current + char, query, results, maxDist)
        }
    }

    private fun levenshteinDistance(s1: String, s2: String): Int {
        val dp = Array(s1.length + 1) { IntArray(s2.length + 1) }
        for (i in 0..s1.length) dp[i][0] = i
        for (j in 0..s2.length) dp[0][j] = j
        for (i in 1..s1.length) {
            for (j in 1..s2.length) {
                val cost = if (s1[i - 1] == s2[j - 1]) 0 else 1
                dp[i][j] = min(min(dp[i - 1][j] + 1, dp[i][j - 1] + 1), dp[i - 1][j - 1] + cost)
            }
        }
        return dp[s1.length][s2.length]
    }
}

class SuggestionEngine {

    private val dictionary = TrieDictionary()
    private val userShortcuts = mutableMapOf<String, String>()

    init {
        val defaultWords = listOf(
            "the" to 1000, "be" to 950, "to" to 900, "of" to 850, "and" to 800, "a" to 750,
            "in" to 700, "that" to 650, "have" to 600, "it" to 550, "for" to 500, "not" to 480,
            "on" to 460, "with" to 440, "he" to 420, "as" to 400, "you" to 390, "do" to 380,
            "at" to 370, "this" to 360, "but" to 350, "his" to 340, "by" to 330, "from" to 320,
            "they" to 310, "we" to 300, "say" to 290, "her" to 280, "she" to 270, "or" to 260,
            "an" to 250, "will" to 240, "my" to 230, "one" to 220, "all" to 210, "would" to 200,
            "there" to 190, "their" to 180, "what" to 170, "so" to 160, "up" to 150, "out" to 140,
            "keyboard" to 500, "flexkey" to 1000, "application" to 300, "message" to 400,
            "android" to 450, "developer" to 250, "welcome" to 200, "thanks" to 350,
            "awesome" to 180, "great" to 220, "tomorrow" to 190, "yesterday" to 170,
            
            "hola" to 500, "gracias" to 450, "por" to 400, "favor" to 380,
            "bonjour" to 500, "merci" to 450, "salut" to 400,
            "hallo" to 500, "danke" to 450, "bitte" to 400
        )

        defaultWords.forEach { (word, freq) ->
            dictionary.insert(word, freq)
        }

        userShortcuts["omw"] = "On my way!"
        userShortcuts["brb"] = "Be right back!"
        userShortcuts["ty"] = "Thank you so much!"
        userShortcuts["np"] = "No problem at all!"
        userShortcuts["btw"] = "by the way"
        userShortcuts["fyi"] = "for your information"
    }

    fun loadUserDictionary(entities: List<UserDictionaryEntity>) {
        entities.forEach { entity ->
            dictionary.insert(entity.word, entity.frequency * 10)
            if (!entity.shortcut.isNullOrEmpty()) {
                userShortcuts[entity.shortcut.lowercase()] = entity.word
            }
        }
    }

    fun addCustomShortcut(shortcut: String, expansion: String) {
        userShortcuts[shortcut.lowercase().trim()] = expansion.trim()
        dictionary.insert(expansion, 100)
    }

    fun generateSuggestions(
        query: String,
        isPasswordField: Boolean = false,
        maxCount: Int = 3
    ): List<Candidate> {
        if (query.isBlank() || isPasswordField) {
            return emptyList()
        }

        val cleanQuery = query.trim().lowercase()
        val candidates = mutableListOf<Candidate>()

        val shortcutExpansion = userShortcuts[cleanQuery]
        if (shortcutExpansion != null) {
            candidates.add(
                Candidate(
                    word = shortcutExpansion,
                    score = 2.0f,
                    source = CandidateSource.USER_SHORTCUT,
                    originalQuery = cleanQuery
                )
            )
        }

        val prefixMatches = dictionary.getPrefixMatches(cleanQuery, limit = maxCount)
        prefixMatches.forEach { (word, freq) ->
            if (word != cleanQuery && candidates.none { it.word.equals(word, ignoreCase = true) }) {
                val formattedWord = if (query.firstOrNull()?.isUpperCase() == true) {
                    word.replaceFirstChar { it.uppercase() }
                } else word

                candidates.add(
                    Candidate(
                        word = formattedWord,
                        score = freq.toFloat(),
                        source = CandidateSource.DICTIONARY,
                        originalQuery = cleanQuery
                    )
                )
            }
        }

        if (candidates.size < maxCount && cleanQuery.length >= 3) {
            val fuzzyMatches = dictionary.findFuzzyMatches(cleanQuery, maxDistance = 1)
            fuzzyMatches.forEach { word ->
                if (candidates.none { it.word.equals(word, ignoreCase = true) }) {
                    val formattedWord = if (query.firstOrNull()?.isUpperCase() == true) {
                        word.replaceFirstChar { it.uppercase() }
                    } else word

                    candidates.add(
                        Candidate(
                            word = formattedWord,
                            score = 0.8f,
                            source = CandidateSource.AUTOCORRECT,
                            originalQuery = cleanQuery
                        )
                    )
                }
            }
        }

        return candidates.distinctBy { it.word.lowercase() }.take(maxCount)
    }
}
