package com.example.ui.components

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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Backspace
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Keyboard
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.KeyboardTheme

data class EmojiData(
    val emoji: String,
    val name: String,
    val keywords: List<String> = emptyList()
)

@Composable
fun EmojiPickerView(
    activeTheme: KeyboardTheme,
    onEmojiSelected: (String) -> Unit,
    onClose: () -> Unit,
    modifier: Modifier = Modifier
) {
    var searchQuery by remember { mutableStateOf("") }
    var isKeyboardVisible by remember { mutableStateOf(false) }
    var selectedCategoryIndex by remember { mutableIntStateOf(0) }

    val categories: List<Pair<String, List<EmojiData>>> = remember {
        listOf(
            "😀" to listOf(
                EmojiData("😀", "grinning face", listOf("smile", "happy", "joy")),
                EmojiData("😃", "grinning face with big eyes", listOf("happy", "joy", "smile")),
                EmojiData("😄", "grinning face with smiling eyes", listOf("happy", "laugh", "smile")),
                EmojiData("😁", "beaming face", listOf("smile", "teeth", "grin")),
                EmojiData("😆", "grinning squinting face", listOf("laugh", "haha", "satisfied")),
                EmojiData("😅", "grinning face with sweat", listOf("sweat", "nervous", "relief")),
                EmojiData("😂", "face with tears of joy", listOf("lol", "funny", "laugh", "tears", "joy")),
                EmojiData("🤣", "rolling on the floor laughing", listOf("rofl", "lol", "laugh")),
                EmojiData("🥲", "smiling face with tear", listOf("sad", "happy", "tear", "grateful")),
                EmojiData("🥹", "face holding back tears", listOf("proud", "touched", "pleading")),
                EmojiData("☺️", "smiling face", listOf("blush", "smile")),
                EmojiData("😊", "smiling face with smiling eyes", listOf("happy", "blush")),
                EmojiData("😇", "smiling face with halo", listOf("angel", "innocent")),
                EmojiData("🙂", "slightly smiling face", listOf("smile", "okay")),
                EmojiData("🙃", "upside down face", listOf("silly", "sarcasm")),
                EmojiData("😉", "winking face", listOf("wink", "flirt")),
                EmojiData("😌", "relieved face", listOf("relieved", "calm")),
                EmojiData("😍", "smiling face with heart eyes", listOf("love", "heart", "crush")),
                EmojiData("🥰", "smiling face with hearts", listOf("love", "adore")),
                EmojiData("😘", "face blowing a kiss", listOf("kiss", "love", "mwah")),
                EmojiData("😋", "face savoring food", listOf("yum", "delicious", "hungry")),
                EmojiData("😛", "face with tongue", listOf("silly", "playful")),
                EmojiData("😜", "winking face with tongue", listOf("crazy", "joke")),
                EmojiData("🤪", "zany face", listOf("goofy", "wild", "crazy")),
                EmojiData("🤨", "face with raised eyebrow", listOf("skeptical", "suspicious")),
                EmojiData("🧐", "face with monocle", listOf("inspect", "curious")),
                EmojiData("🤓", "nerd face", listOf("smart", "geek", "glasses")),
                EmojiData("😎", "smiling face with sunglasses", listOf("cool", "sun", "glasses")),
                EmojiData("🥸", "disguised face", listOf("mask", "incognito")),
                EmojiData("🤩", "star struck", listOf("wow", "star", "excited")),
                EmojiData("🥳", "partying face", listOf("party", "celebrate", "birthday")),
                EmojiData("😏", "smirking face", listOf("smirk", "sly")),
                EmojiData("😒", "unamused face", listOf("bored", "whatever")),
                EmojiData("😞", "disappointed face", listOf("sad", "upset")),
                EmojiData("😔", "pensive face", listOf("sad", "sorry")),
                EmojiData("😟", "worried face", listOf("anxious", "nervous")),
                EmojiData("😕", "confused face", listOf("huh", "puzzled")),
                EmojiData("🥺", "pleading face", listOf("puppy eyes", "please")),
                EmojiData("😢", "crying face", listOf("cry", "sad", "tear")),
                EmojiData("😭", "loudly crying face", listOf("sob", "sad", "cry")),
                EmojiData("😤", "face with steam", listOf("angry", "mad", "triumph")),
                EmojiData("😠", "angry face", listOf("mad", "annoyed")),
                EmojiData("😡", "pouting face", listOf("furious", "mad", "rage")),
                EmojiData("🤯", "exploding head", listOf("mind blown", "shocked")),
                EmojiData("😳", "flushed face", listOf("blush", "embarrassed")),
                EmojiData("🥵", "hot face", listOf("heat", "sweat")),
                EmojiData("🥶", "cold face", listOf("freezing", "ice")),
                EmojiData("😱", "face screaming in fear", listOf("scared", "fear", "scream")),
                EmojiData("🤔", "thinking face", listOf("hmm", "think", "wonder")),
                EmojiData("🥱", "yawning face", listOf("tired", "sleepy")),
                EmojiData("😴", "sleeping face", listOf("zzz", "sleep", "night"))
            ),
            "🖐️" to listOf(
                EmojiData("👋", "waving hand", listOf("wave", "hello", "hi", "bye")),
                EmojiData("🤚", "raised back of hand", listOf("stop", "hand")),
                EmojiData("🖐️", "hand with fingers splayed", listOf("five", "hand")),
                EmojiData("✋", "raised hand", listOf("stop", "high five")),
                EmojiData("🖖", "vulcan salute", listOf("spock", "star trek")),
                EmojiData("👌", "ok hand", listOf("perfect", "okay", "fine")),
                EmojiData("🤌", "pinched fingers", listOf("italian", "chef")),
                EmojiData("🤏", "pinching hand", listOf("small", "little", "tiny")),
                EmojiData("✌️", "victory hand", listOf("peace", "two", "v")),
                EmojiData("🤞", "crossed fingers", listOf("luck", "hope")),
                EmojiData("🤟", "love you gesture", listOf("rock", "love")),
                EmojiData("🤘", "sign of the horns", listOf("rock on", "metal")),
                EmojiData("🤙", "call me hand", listOf("phone", "shaka")),
                EmojiData("👈", "backhand index pointing left", listOf("point", "left")),
                EmojiData("👉", "backhand index pointing right", listOf("point", "right")),
                EmojiData("👆", "backhand index pointing up", listOf("point", "up")),
                EmojiData("👇", "backhand index pointing down", listOf("point", "down")),
                EmojiData("👍", "thumbs up", listOf("like", "yes", "good", "approve")),
                EmojiData("👎", "thumbs down", listOf("dislike", "no", "bad")),
                EmojiData("✊", "raised fist", listOf("power", "fist")),
                EmojiData("👊", "oncoming fist", listOf("punch", "fist bump")),
                EmojiData("👏", "clapping hands", listOf("clap", "bravo", "applause")),
                EmojiData("🙌", "raising hands", listOf("hooray", "celebrate")),
                EmojiData("🫶", "heart hands", listOf("love", "heart")),
                EmojiData("🤝", "handshake", listOf("deal", "agreement", "hello")),
                EmojiData("🙏", "folded hands", listOf("pray", "please", "thanks", "thank you")),
                EmojiData("💪", "flexed biceps", listOf("strong", "muscle", "workout"))
            ),
            "🐶" to listOf(
                EmojiData("🐶", "dog face", listOf("puppy", "dog", "pet")),
                EmojiData("🐱", "cat face", listOf("kitty", "cat", "pet")),
                EmojiData("🐭", "mouse face", listOf("rat", "mouse")),
                EmojiData("🐹", "hamster face", listOf("pet")),
                EmojiData("🐰", "rabbit face", listOf("bunny", "rabbit")),
                EmojiData("🦊", "fox face", listOf("fox")),
                EmojiData("🐻", "bear face", listOf("bear", "teddy")),
                EmojiData("🐼", "panda face", listOf("panda")),
                EmojiData("🐨", "koala", listOf("koala")),
                EmojiData("🐯", "tiger face", listOf("tiger", "cat")),
                EmojiData("🦁", "lion face", listOf("lion", "king")),
                EmojiData("🐮", "cow face", listOf("cow", "milk")),
                EmojiData("🐷", "pig face", listOf("pig", "oink")),
                EmojiData("🐸", "frog face", listOf("frog", "toad")),
                EmojiData("🐵", "monkey face", listOf("monkey")),
                EmojiData("🙈", "see no evil monkey", listOf("monkey", "blind")),
                EmojiData("🙉", "hear no evil monkey", listOf("monkey", "deaf")),
                EmojiData("🙊", "speak no evil monkey", listOf("monkey", "quiet")),
                EmojiData("🐔", "chicken", listOf("rooster", "bird")),
                EmojiData("🐧", "penguin", listOf("bird", "ice")),
                EmojiData("🐦", "bird", listOf("fly")),
                EmojiData("🦄", "unicorn", listOf("magic", "horse")),
                EmojiData("🐝", "honeybee", listOf("bee", "bug", "honey")),
                EmojiData("🦋", "butterfly", listOf("bug", "wings"))
            ),
            "🍔" to listOf(
                EmojiData("🍏", "green apple", listOf("apple", "fruit")),
                EmojiData("🍎", "red apple", listOf("apple", "fruit")),
                EmojiData("🍊", "tangerine", listOf("orange", "fruit")),
                EmojiData("🍋", "lemon", listOf("sour", "citrus")),
                EmojiData("🍌", "banana", listOf("fruit")),
                EmojiData("🍉", "watermelon", listOf("fruit", "summer")),
                EmojiData("🍇", "grapes", listOf("fruit", "wine")),
                EmojiData("🍓", "strawberry", listOf("berry", "fruit")),
                EmojiData("🫐", "blueberries", listOf("berry")),
                EmojiData("🍒", "cherries", listOf("fruit")),
                EmojiData("🍑", "peach", listOf("fruit")),
                EmojiData("🍍", "pineapple", listOf("fruit")),
                EmojiData("🥑", "avocado", listOf("food")),
                EmojiData("🍕", "pizza", listOf("cheese", "food", "junk")),
                EmojiData("🍔", "hamburger", listOf("burger", "fast food", "meat")),
                EmojiData("🍟", "french fries", listOf("fries", "fast food")),
                EmojiData("🌭", "hot dog", listOf("food", "sausage")),
                EmojiData("🍿", "popcorn", listOf("movie", "snack")),
                EmojiData("🥓", "bacon", listOf("meat", "breakfast")),
                EmojiData("🍩", "doughnut", listOf("donut", "sweet")),
                EmojiData("🍦", "soft ice cream", listOf("icecream", "dessert")),
                EmojiData("🍰", "shortcake", listOf("cake", "birthday")),
                EmojiData("☕️", "hot beverage", listOf("coffee", "tea", "cafe")),
                EmojiData("🧃", "beverage box", listOf("juice", "drink")),
                EmojiData("🍺", "beer mug", listOf("beer", "drink", "alcohol"))
            ),
            "⚽️" to listOf(
                EmojiData("⚽️", "soccer ball", listOf("football", "sports", "game")),
                EmojiData("🏀", "basketball", listOf("hoops", "sports")),
                EmojiData("🏈", "american football", listOf("sports", "nfl")),
                EmojiData("⚾️", "baseball", listOf("sports")),
                EmojiData("🎾", "tennis", listOf("sports", "racket")),
                EmojiData("🏐", "volleyball", listOf("sports")),
                EmojiData("🏉", "rugby football", listOf("sports")),
                EmojiData("🏓", "ping pong", listOf("table tennis", "sports")),
                EmojiData("🏸", "badminton", listOf("sports")),
                EmojiData("🥊", "boxing glove", listOf("fight", "sports")),
                EmojiData("🛹", "skateboard", listOf("skate", "board")),
                EmojiData("🚴", "person biking", listOf("cycle", "bike"))
            ),
            "🚗" to listOf(
                EmojiData("🚗", "automobile", listOf("car", "drive", "vehicle")),
                EmojiData("🚕", "taxi", listOf("cab", "car")),
                EmojiData("🚙", "sport utility vehicle", listOf("car", "suv")),
                EmojiData("🚌", "bus", listOf("transit")),
                EmojiData("🏎️", "racing car", listOf("race", "fast")),
                EmojiData("🚓", "police car", listOf("cop", "emergency")),
                EmojiData("🚑", "ambulance", listOf("hospital", "emergency")),
                EmojiData("🚒", "fire engine", listOf("fire", "truck")),
                EmojiData("🛵", "motor scooter", listOf("scooter", "bike")),
                EmojiData("🏍️", "motorcycle", listOf("bike")),
                EmojiData("🚀", "rocket", listOf("space", "launch", "moon")),
                EmojiData("✈️", "airplane", listOf("flight", "travel", "fly"))
            ),
            "💡" to listOf(
                EmojiData("💡", "light bulb", listOf("idea", "lamp", "bright")),
                EmojiData("🔦", "flashlight", listOf("torch", "light")),
                EmojiData("📱", "mobile phone", listOf("smartphone", "call")),
                EmojiData("💻", "laptop", listOf("computer", "code", "work")),
                EmojiData("🎉", "party popper", listOf("celebrate", "tada", "congrats")),
                EmojiData("🔥", "fire", listOf("flame", "hot", "lit")),
                EmojiData("✨", "sparkles", listOf("magic", "clean", "star")),
                EmojiData("⭐", "star", listOf("favorite", "rating")),
                EmojiData("💎", "gem stone", listOf("diamond", "jewel"))
            ),
            "❤️" to listOf(
                EmojiData("❤️", "red heart", listOf("love", "heart", "like")),
                EmojiData("🧡", "orange heart", listOf("love", "heart")),
                EmojiData("💛", "yellow heart", listOf("love", "heart")),
                EmojiData("💚", "green heart", listOf("love", "heart")),
                EmojiData("💙", "blue heart", listOf("love", "heart")),
                EmojiData("💜", "purple heart", listOf("love", "heart")),
                EmojiData("🖤", "black heart", listOf("love", "heart", "dark")),
                EmojiData("🤍", "white heart", listOf("love", "heart")),
                EmojiData("🤎", "brown heart", listOf("love", "heart")),
                EmojiData("💔", "broken heart", listOf("heartbreak", "sad")),
                EmojiData("❤️‍🔥", "heart on fire", listOf("passionate", "love")),
                EmojiData("💯", "hundred points", listOf("100", "perfect", "score"))
            )
        )
    }

    val queryClean = searchQuery.trim().lowercase()

    val currentEmojis = if (queryClean.isNotBlank()) {
        val queryTokens = queryClean.split("\\s+".toRegex()).filter { it.isNotBlank() }
        categories.flatMap { it.second }
            .filter { item ->
                val nameLower = item.name.lowercase()
                queryTokens.all { token ->
                    item.emoji.contains(token) ||
                            nameLower.contains(token) ||
                            item.keywords.any { kw -> kw.lowercase().contains(token) }
                }
            }
            .distinctBy { it.emoji }
    } else {
        categories.getOrNull(selectedCategoryIndex)?.second ?: categories.first().second
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(activeTheme.backgroundColor)
            .padding(6.dp)
    ) {
        // Top Header - Interactive Search Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onClose, modifier = Modifier.testTag("emoji_close")) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back to Keyboard",
                    tint = activeTheme.keyTextColor
                )
            }

            // Search Bar Input Container
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(40.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(activeTheme.keyColor)
                    .border(
                        width = 1.dp,
                        color = if (isKeyboardVisible || searchQuery.isNotEmpty()) activeTheme.accentColor else activeTheme.keyColor,
                        shape = RoundedCornerShape(20.dp)
                    )
                    .clickable { isKeyboardVisible = true }
                    .padding(horizontal = 12.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search",
                        tint = activeTheme.keySecondaryTextColor,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))

                    BasicTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        singleLine = true,
                        cursorBrush = SolidColor(activeTheme.accentColor),
                        textStyle = TextStyle(
                            color = activeTheme.keyTextColor,
                            fontSize = 14.sp
                        ),
                        modifier = Modifier.weight(1f),
                        decorationBox = { innerTextField ->
                            Box(contentAlignment = Alignment.CenterStart) {
                                if (searchQuery.isEmpty()) {
                                    Text(
                                        text = "Search Emojis...",
                                        color = activeTheme.keySecondaryTextColor,
                                        fontSize = 14.sp,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                                innerTextField()
                            }
                        }
                    )

                    if (searchQuery.isNotEmpty()) {
                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .clip(CircleShape)
                                .clickable {
                                    searchQuery = ""
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Clear",
                                tint = activeTheme.keyTextColor,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(4.dp))

                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .clip(CircleShape)
                            .background(if (isKeyboardVisible) activeTheme.accentColor.copy(alpha = 0.2f) else Color.Transparent)
                            .clickable { isKeyboardVisible = !isKeyboardVisible },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Keyboard,
                            contentDescription = "Toggle Keyboard",
                            tint = if (isKeyboardVisible) activeTheme.accentColor else activeTheme.keySecondaryTextColor,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }

        // Category Tabs (shown when not searching)
        if (searchQuery.isBlank() && !isKeyboardVisible) {
            ScrollableTabRow(
                selectedTabIndex = selectedCategoryIndex,
                edgePadding = 4.dp,
                containerColor = activeTheme.backgroundColor,
                contentColor = activeTheme.accentColor,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(36.dp)
            ) {
                categories.forEachIndexed { idx, (icon, _) ->
                    Tab(
                        selected = selectedCategoryIndex == idx,
                        onClick = { selectedCategoryIndex = idx },
                        text = { Text(icon, fontSize = 16.sp) }
                    )
                }
            }
        }

        // Embedded Search Soft Keyboard
        if (isKeyboardVisible) {
            val row1 = listOf('q', 'w', 'e', 'r', 't', 'y', 'u', 'i', 'o', 'p')
            val row2 = listOf('a', 's', 'd', 'f', 'g', 'h', 'j', 'k', 'l')
            val row3 = listOf('z', 'x', 'c', 'v', 'b', 'n', 'm')

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(activeTheme.backgroundColor)
                    .padding(vertical = 4.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    row1.forEach { char ->
                        SearchKeyButton(
                            char = char.toString(),
                            activeTheme = activeTheme,
                            onClick = { searchQuery += char },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Spacer(modifier = Modifier.width(10.dp))
                    row2.forEach { char ->
                        SearchKeyButton(
                            char = char.toString(),
                            activeTheme = activeTheme,
                            onClick = { searchQuery += char },
                            modifier = Modifier.weight(1f)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    row3.forEach { char ->
                        SearchKeyButton(
                            char = char.toString(),
                            activeTheme = activeTheme,
                            onClick = { searchQuery += char },
                            modifier = Modifier.weight(1f)
                        )
                    }

                    // Backspace Key
                    Box(
                        modifier = Modifier
                            .weight(1.5f)
                            .height(34.dp)
                            .padding(horizontal = 2.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .background(activeTheme.keyPressedColor)
                            .clickable {
                                if (searchQuery.isNotEmpty()) {
                                    searchQuery = searchQuery.dropLast(1)
                                }
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Backspace,
                            contentDescription = "Backspace",
                            tint = activeTheme.keyTextColor,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }

                // Row 4: Space and Clear Controls
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 2.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Space Bar
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(34.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .background(activeTheme.keyColor)
                            .clickable { searchQuery += " " },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Space",
                            color = activeTheme.keySecondaryTextColor,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    // Hide Keyboard
                    Box(
                        modifier = Modifier
                            .width(60.dp)
                            .height(34.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .background(activeTheme.keyPressedColor)
                            .clickable { isKeyboardVisible = false },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Done",
                            color = activeTheme.accentColor,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        // Emoji Grid or Empty State
        if (currentEmojis.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No emojis found for \"$searchQuery\"",
                    color = activeTheme.keySecondaryTextColor,
                    fontSize = 13.sp
                )
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Adaptive(42.dp),
                modifier = Modifier
                    .weight(1f)
                    .padding(top = 4.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                items(currentEmojis, key = { it.emoji }) { emojiData ->
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .clickable { onEmojiSelected(emojiData.emoji) },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = emojiData.emoji, fontSize = 22.sp)
                    }
                }
            }
        }
    }
}

@Composable
private fun SearchKeyButton(
    char: String,
    activeTheme: KeyboardTheme,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .height(34.dp)
            .padding(horizontal = 2.dp)
            .clip(RoundedCornerShape(6.dp))
            .background(activeTheme.keyColor)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = char,
            color = activeTheme.keyTextColor,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium
        )
    }
}
