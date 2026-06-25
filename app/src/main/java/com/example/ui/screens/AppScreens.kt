package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.data.local.NotificationEntity
import com.example.data.local.PostEntity
import com.example.data.local.UserEntity
import com.example.ui.components.*
import com.example.ui.theme.*
import com.example.ui.viewmodel.SocialViewModel
import kotlinx.coroutines.launch

// ==========================================
// 1. AUTH SCREEN (REGISTER / LOGIN)
// ==========================================
@Composable
fun AuthScreen(
    viewModel: SocialViewModel,
    onAuthSuccess: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isLogin by remember { mutableStateOf(true) }
    var username by remember { mutableStateOf("") }
    var fullName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var bio by remember { mutableStateOf("") }
    var message by remember { mutableStateOf("") }
    var isError by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(DarkBackground, DarkSurfaceVariant)
                )
            )
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
        ) {
            XLogo(
                modifier = Modifier
                    .size(64.dp)
                    .padding(bottom = 16.dp)
            )

            Text(
                text = "NTd_303 Social",
                fontSize = 28.sp,
                fontWeight = FontWeight.ExtraBold,
                color = TextPrimary,
                letterSpacing = 1.sp
            )

            Text(
                text = if (isLogin) "See what's happening right now." else "Create your profile to join the network.",
                fontSize = 14.sp,
                color = TextSecondary,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 8.dp, bottom = 24.dp)
            )

            Card(
                colors = CardDefaults.cardColors(containerColor = DarkSurface),
                shape = RoundedCornerShape(24.dp),
                border = BorderStroke(1.dp, DarkOutline),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = if (isLogin) "Sign In" else "Create Account",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    // Username field (Needed in both)
                    OutlinedTextField(
                        value = username,
                        onValueChange = { username = it },
                        label = { Text("Username") },
                        placeholder = { Text("e.g. john_doe") },
                        leadingIcon = { Icon(Icons.Default.AlternateEmail, contentDescription = null) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("username_input"),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary,
                            focusedBorderColor = TwitterBlue,
                            unfocusedBorderColor = DarkOutline
                        ),
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    if (!isLogin) {
                        // Full Name
                        OutlinedTextField(
                            value = fullName,
                            onValueChange = { fullName = it },
                            label = { Text("Full Name") },
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary,
                                focusedBorderColor = TwitterBlue,
                                unfocusedBorderColor = DarkOutline
                            ),
                            singleLine = true
                        )
                        Spacer(modifier = Modifier.height(12.dp))

                        // Email
                        OutlinedTextField(
                            value = email,
                            onValueChange = { email = it },
                            label = { Text("Email Address") },
                            leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary,
                                focusedBorderColor = TwitterBlue,
                                unfocusedBorderColor = DarkOutline
                            ),
                            singleLine = true
                        )
                        Spacer(modifier = Modifier.height(12.dp))

                        // Bio
                        OutlinedTextField(
                            value = bio,
                            onValueChange = { bio = it },
                            label = { Text("Bio (Optional)") },
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary,
                                focusedBorderColor = TwitterBlue,
                                unfocusedBorderColor = DarkOutline
                            ),
                            maxLines = 3
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                    }

                    // Password field (Needed in both)
                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text("Password") },
                        visualTransformation = PasswordVisualTransformation(),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("password_input"),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary,
                            focusedBorderColor = TwitterBlue,
                            unfocusedBorderColor = DarkOutline
                        ),
                        singleLine = true
                    )

                    if (message.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = message,
                            color = if (isError) DarkError else GreenRepost,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Center
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    Button(
                        onClick = {
                            if (isLogin) {
                                viewModel.login(username, password) { success, msg ->
                                    message = msg
                                    isError = !success
                                    if (success) onAuthSuccess()
                                }
                            } else {
                                viewModel.register(username, fullName, email, password, bio) { success, msg ->
                                    message = msg
                                    isError = !success
                                    if (success) onAuthSuccess()
                                }
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = TwitterBlue),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("auth_submit_button"),
                        shape = RoundedCornerShape(24.dp)
                    ) {
                        Text(
                            text = if (isLogin) "Log In" else "Create Account",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = Color.White
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (isLogin) "Don't have an account?" else "Already have an account?",
                            color = TextSecondary,
                            fontSize = 13.sp
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (isLogin) "Sign Up" else "Log In",
                            color = TwitterBlue,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.clickable {
                                isLogin = !isLogin
                                message = ""
                            }
                        )
                    }
                }
            }
        }
    }
}


// ==========================================
// 2. HOME SCREEN (FEED & POSTING)
// ==========================================
@Composable
fun BentoGridDashboard(
    onTrendingClick: (String) -> Unit,
    onViewChatClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // First Row: 2 column bento cells
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Left block: Trending
            Card(
                modifier = Modifier
                    .weight(1f)
                    .height(115.dp)
                    .clickable { onTrendingClick("#NTd303") },
                colors = CardDefaults.cardColors(containerColor = DarkSurfaceVariant),
                shape = RoundedCornerShape(24.dp),
                border = BorderStroke(1.dp, DarkOutline)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(14.dp),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "TRENDING",
                        color = TwitterBlue,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.5.sp
                    )
                    Column {
                        Text(
                            text = "#NTd303",
                            color = TextPrimary,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Black
                        )
                        Text(
                            text = "15.4k Reposts",
                            color = TextSecondary,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            // Right block: System Uptime
            Card(
                modifier = Modifier
                    .weight(1f)
                    .height(115.dp),
                colors = CardDefaults.cardColors(containerColor = DarkSurface),
                shape = RoundedCornerShape(24.dp),
                border = BorderStroke(1.dp, DarkOutline)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(14.dp)
                ) {
                    // Pulsing indicators in top-right
                    Row(
                        modifier = Modifier.align(Alignment.TopEnd),
                        horizontalArrangement = Arrangement.spacedBy(3.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .background(Color(0xFF22C55E), CircleShape)
                        )
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .background(Color(0xFF22C55E).copy(alpha = 0.3f), CircleShape)
                        )
                    }

                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "99.9%",
                            color = TwitterBlue,
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Light
                        )
                        Text(
                            text = "SYSTEM UPTIME",
                            color = TextSecondary,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp,
                            modifier = Modifier.padding(top = 2.dp)
                        )
                    }
                }
            }
        }

        // Second Row: Wide gradient message banner
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight(),
            shape = RoundedCornerShape(24.dp),
            border = BorderStroke(1.dp, DarkOutline)
        ) {
            Row(
                modifier = Modifier
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(BentoBlueGradientStart, BentoBlueGradientEnd)
                        )
                    )
                    .padding(16.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = "Active Messages",
                        color = Color(0xFFD0E4FF),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                    // Overlapping Avatar cluster
                    Row(
                        horizontalArrangement = Arrangement.spacedBy((-8).dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        val avatars = listOf(
                            "https://images.unsplash.com/photo-1534528741775-53994a69daeb?auto=format&fit=crop&w=100&q=80",
                            "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?auto=format&fit=crop&w=100&q=80",
                            "https://images.unsplash.com/photo-1494790108377-be9c29b29330?auto=format&fit=crop&w=100&q=80"
                        )
                        avatars.forEach { url ->
                            AsyncImage(
                                model = url,
                                contentDescription = null,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .size(28.dp)
                                    .clip(CircleShape)
                                    .background(DarkSurfaceVariant)
                                    .border(1.5.dp, DarkSurface, CircleShape)
                            )
                        }
                    }
                }

                Button(
                    onClick = onViewChatClick,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = BentoButtonBg,
                        contentColor = BentoButtonText
                    ),
                    shape = RoundedCornerShape(16.dp),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 6.dp),
                    modifier = Modifier.height(36.dp)
                ) {
                    Text(
                        text = "View Chat",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
fun HomeScreen(
    viewModel: SocialViewModel,
    onUserClick: (String) -> Unit,
    modifier: Modifier = Modifier,
    onNavigateToRoute: (String) -> Unit = {}
) {
    val posts by viewModel.feedPosts.collectAsStateWithLifecycle()
    val currentUser by viewModel.currentUser.collectAsStateWithLifecycle()

    var postContent by remember { mutableStateOf("") }
    var mediaUrlInput by remember { mutableStateOf("") }
    var showMediaField by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBackground)
    ) {
        // Compose section (Togglable or standard card)
        Card(
            colors = CardDefaults.cardColors(containerColor = DarkSurface),
            shape = RoundedCornerShape(24.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 8.dp),
            border = BorderStroke(1.dp, color = DarkOutline)
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(modifier = Modifier.fillMaxWidth()) {
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(currentUser?.avatarUrl ?: "https://images.unsplash.com/photo-1534528741775-53994a69daeb?auto=format&fit=crop&w=150&q=80")
                            .crossfade(true)
                            .build(),
                        contentDescription = "My Avatar",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(DarkSurfaceVariant)
                            .clickable { onUserClick(currentUser?.username ?: "") }
                    )

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        TextField(
                            value = postContent,
                            onValueChange = { postContent = it },
                            placeholder = { Text("What's happening?", color = TextSecondary) },
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = Color.Transparent,
                                unfocusedContainerColor = Color.Transparent,
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary,
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )

                        if (showMediaField) {
                            Spacer(modifier = Modifier.height(8.dp))
                            OutlinedTextField(
                                value = mediaUrlInput,
                                onValueChange = { mediaUrlInput = it },
                                placeholder = { Text("Enter Image URL...", color = TextSecondary) },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth(),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedTextColor = TextPrimary,
                                    unfocusedTextColor = TextPrimary,
                                    focusedBorderColor = TwitterBlue,
                                    unfocusedBorderColor = DarkOutline
                                )
                            )
                        }
                    }
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row {
                        IconButton(onClick = { showMediaField = !showMediaField }) {
                            Icon(
                                imageVector = Icons.Outlined.Image,
                                contentDescription = "Add Image Link",
                                tint = TwitterBlue
                            )
                        }
                        IconButton(onClick = {}) {
                            Icon(
                                imageVector = Icons.Outlined.SentimentSatisfied,
                                contentDescription = "Add Emoji",
                                tint = TwitterBlue
                            )
                        }
                    }

                    Button(
                        onClick = {
                            if (postContent.isNotBlank()) {
                                viewModel.submitPost(postContent, mediaUrlInput)
                                postContent = ""
                                mediaUrlInput = ""
                                showMediaField = false
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = TwitterBlue),
                        shape = RoundedCornerShape(18.dp),
                        modifier = Modifier
                            .height(36.dp)
                            .testTag("submit_tweet_button"),
                        enabled = postContent.isNotBlank()
                    ) {
                        Text("Post", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    }
                }
            }
        }

        // Live Feed Scroll (Includes Bento widgets)
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .testTag("feed_lazy_column")
        ) {
            item {
                BentoGridDashboard(
                    onTrendingClick = { hashtag ->
                        viewModel.updateSearchQuery(hashtag)
                        onNavigateToRoute("explore")
                    },
                    onViewChatClick = {
                        onNavigateToRoute("messages")
                    }
                )
            }

            if (posts.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 40.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = Icons.Outlined.Feed,
                                contentDescription = "Empty Feed",
                                tint = TextSecondary,
                                modifier = Modifier.size(64.dp)
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Text("No posts available.", color = TextPrimary, fontWeight = FontWeight.Bold)
                            Text("Follow other users or compose your first post above!", color = TextSecondary, fontSize = 13.sp)
                        }
                    }
                }
            } else {
                items(posts, key = { it.postId }) { post ->
                    var isPostLiked by remember { mutableStateOf(false) }

                    // Reactive Like Status check
                    LaunchedEffect(post.postId, currentUser?.username) {
                        currentUser?.let { me ->
                            isPostLiked = viewModel.currentUser.value?.username?.let { myUser ->
                                // Direct lookup via viewModel or state check
                                false // handled in repo, let's keep localized variable but synced
                            } ?: false
                        }
                    }

                    PostCard(
                        post = post,
                        currentUsername = currentUser?.username ?: "",
                        onUserClick = onUserClick,
                        onLikeClick = { viewModel.likePost(post.postId) },
                        onRepostClick = { viewModel.repostPost(post.postId) },
                        onCommentClick = { text -> viewModel.replyToPost(post.postId, text) },
                        onDeleteClick = { viewModel.deletePost(post.postId) },
                        onPinClick = { viewModel.togglePin(post.postId) },
                        isLiked = isPostLiked
                    )
                }
            }
        }
    }
}


// ==========================================
// 3. EXPLORE SCREEN (SEARCH & TRENDING)
// ==========================================
@Composable
fun ExploreScreen(
    viewModel: SocialViewModel,
    onUserClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val searchedPosts by viewModel.searchedPosts.collectAsStateWithLifecycle()
    val searchedUsers by viewModel.searchedUsers.collectAsStateWithLifecycle()
    val currentUser by viewModel.currentUser.collectAsStateWithLifecycle()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBackground)
            .padding(12.dp)
    ) {
        // Search Bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { viewModel.updateSearchQuery(it) },
            placeholder = { Text("Search hashtags, users or keywords...", color = TextSecondary) },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search", tint = TextSecondary) },
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    IconButton(onClick = { viewModel.updateSearchQuery("") }) {
                        Icon(Icons.Default.Clear, contentDescription = "Clear", tint = TextSecondary)
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp)
                .testTag("explore_search_bar"),
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = TextPrimary,
                unfocusedTextColor = TextPrimary,
                focusedBorderColor = TwitterBlue,
                unfocusedBorderColor = DarkOutline
            ),
            singleLine = true,
            shape = RoundedCornerShape(24.dp)
        )

        if (searchQuery.isEmpty()) {
            // Display Trending Topics
            Text(
                text = "What's happening",
                fontSize = 18.sp,
                fontWeight = FontWeight.ExtraBold,
                color = TextPrimary,
                modifier = Modifier.padding(vertical = 8.dp)
            )

            Card(
                colors = CardDefaults.cardColors(containerColor = DarkSurface),
                shape = RoundedCornerShape(24.dp),
                border = CardBorder(),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    viewModel.trendingHashtags.forEachIndexed { idx, (hashtag, postsCount) ->
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { viewModel.updateSearchQuery(hashtag) }
                                .padding(vertical = 10.dp)
                        ) {
                            Text(
                                text = "Trending " + (idx + 1),
                                color = TextSecondary,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = hashtag,
                                color = TextPrimary,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = postsCount,
                                color = TextSecondary,
                                fontSize = 12.sp
                            )
                        }
                        if (idx < viewModel.trendingHashtags.size - 1) {
                            HorizontalDivider(color = DarkOutline)
                        }
                    }
                }
            }
        } else {
            // Search Results Panel
            if (searchedUsers.isNotEmpty()) {
                Text(
                    text = "Profiles",
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    modifier = Modifier.padding(vertical = 8.dp)
                )

                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 160.dp)
                ) {
                    items(searchedUsers) { user ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onUserClick(user.username) }
                                .padding(vertical = 8.dp, horizontal = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            AsyncImage(
                                model = user.avatarUrl,
                                contentDescription = null,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(DarkSurfaceVariant)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(user.fullName, color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                    if (user.username == "admin" || user.username == "elonmusk" || user.username == "sarah_tech") {
                                        VerificationBadge()
                                    }
                                }
                                Text("@${user.username}", color = TextSecondary, fontSize = 12.sp)
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
            }

            Text(
                text = "Matching Posts",
                color = TextPrimary,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                modifier = Modifier.padding(vertical = 8.dp)
            )

            if (searchedPosts.isEmpty()) {
                Text(
                    text = "No matching posts found.",
                    color = TextSecondary,
                    fontSize = 14.sp,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
            } else {
                LazyColumn(modifier = Modifier.fillMaxWidth()) {
                    items(searchedPosts) { post ->
                        PostCard(
                            post = post,
                            currentUsername = currentUser?.username ?: "",
                            onUserClick = onUserClick,
                            onLikeClick = { viewModel.likePost(post.postId) },
                            onRepostClick = { viewModel.repostPost(post.postId) },
                            onCommentClick = { text -> viewModel.replyToPost(post.postId, text) }
                        )
                    }
                }
            }
        }
    }
}


// ==========================================
// 4. MESSAGES SCREEN (DM CHATS)
// ==========================================
@Composable
fun MessagesScreen(
    viewModel: SocialViewModel,
    onUserClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val allUsers by viewModel.allUsers.collectAsStateWithLifecycle()
    val activeChatPartner by viewModel.activeChatPartner.collectAsStateWithLifecycle()
    val chatMessages by viewModel.chatMessages.collectAsStateWithLifecycle()
    val currentUser by viewModel.currentUser.collectAsStateWithLifecycle()

    var textInput by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBackground)
    ) {
        if (activeChatPartner == null) {
            // Direct Messages inbox view
            Text(
                text = "Direct Messages",
                fontSize = 20.sp,
                fontWeight = FontWeight.ExtraBold,
                color = TextPrimary,
                modifier = Modifier.padding(16.dp)
            )

            val selectableUsers = allUsers.filter { it.username != currentUser?.username }

            if (selectableUsers.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No other users found on the platform.", color = TextSecondary)
                }
            } else {
                LazyColumn(modifier = Modifier.fillMaxWidth()) {
                    items(selectableUsers) { user ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { viewModel.selectChatPartner(user) }
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            AsyncImage(
                                model = user.avatarUrl,
                                contentDescription = null,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(CircleShape)
                                    .background(DarkSurfaceVariant)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = user.fullName,
                                        color = TextPrimary,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 15.sp
                                    )
                                    if (user.username == "admin" || user.username == "elonmusk" || user.username == "nasa_space") {
                                        VerificationBadge()
                                    }
                                }
                                Text(
                                    text = "@${user.username}",
                                    color = TextSecondary,
                                    fontSize = 13.sp
                                )
                            }
                            Icon(
                                imageVector = Icons.Default.ChatBubble,
                                contentDescription = "Open Chat",
                                tint = TwitterBlue,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        HorizontalDivider(color = DarkOutline, modifier = Modifier.padding(horizontal = 16.dp))
                    }
                }
            }
        } else {
            // Active chat room view
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(DarkSurface)
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { viewModel.selectChatPartner(null as UserEntity?) }) {
                    Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back", tint = TwitterBlue)
                }
                Spacer(modifier = Modifier.width(8.dp))
                AsyncImage(
                    model = activeChatPartner!!.avatarUrl,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .clickable { onUserClick(activeChatPartner!!.username) }
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = activeChatPartner!!.fullName,
                            color = TextPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            modifier = Modifier.clickable { onUserClick(activeChatPartner!!.username) }
                        )
                        if (activeChatPartner!!.username == "admin" || activeChatPartner!!.username == "elonmusk" || activeChatPartner!!.username == "nasa_space") {
                            VerificationBadge()
                        }
                    }
                    Text("@${activeChatPartner!!.username}", color = TextSecondary, fontSize = 12.sp)
                }
            }

            // Chat history stream
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(vertical = 8.dp),
                reverseLayout = false
            ) {
                items(chatMessages) { message ->
                    val isMe = message.senderId == currentUser?.username
                    MessageBubble(
                        text = message.content,
                        isMe = isMe,
                        timestamp = message.timestamp
                    )
                }
            }

            // Input Send box
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(DarkSurface)
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = textInput,
                    onValueChange = { textInput = it },
                    placeholder = { Text("Start a message...", color = TextSecondary) },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("chat_input_text"),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        focusedBorderColor = TwitterBlue,
                        unfocusedBorderColor = DarkOutline
                    ),
                    maxLines = 3,
                    shape = RoundedCornerShape(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                IconButton(
                    onClick = {
                        if (textInput.isNotBlank()) {
                            viewModel.sendChatMessage(textInput)
                            textInput = ""
                        }
                    },
                    modifier = Modifier
                        .background(TwitterBlue, CircleShape)
                        .size(40.dp)
                ) {
                    Icon(imageVector = Icons.Default.Send, contentDescription = "Send", tint = Color.White, modifier = Modifier.size(18.dp))
                }
            }
        }
    }
}


// ==========================================
// 5. NOTIFICATIONS SCREEN
// ==========================================
@Composable
fun NotificationsScreen(
    viewModel: SocialViewModel,
    onUserClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val notifications by viewModel.notifications.collectAsStateWithLifecycle()
    val currentUser by viewModel.currentUser.collectAsStateWithLifecycle()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBackground)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Notifications",
                fontSize = 20.sp,
                fontWeight = FontWeight.ExtraBold,
                color = TextPrimary
            )
            TextButton(onClick = { viewModel.clearNotifications() }) {
                Text("Mark all read", color = TwitterBlue, fontWeight = FontWeight.Bold)
            }
        }

        if (notifications.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Outlined.Notifications,
                        contentDescription = "Empty",
                        tint = TextSecondary,
                        modifier = Modifier.size(64.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text("Nothing to see here — yet", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    Text("Likes, comments, and mentions on your posts will appear here.", color = TextSecondary, fontSize = 13.sp)
                }
            }
        } else {
            LazyColumn(modifier = Modifier.fillMaxWidth()) {
                items(notifications) { notification ->
                    val icon = when (notification.type) {
                        "LIKE" -> Icons.Default.Favorite to RedLike
                        "COMMENT" -> Icons.Default.ChatBubble to TwitterBlue
                        "REPOST" -> Icons.Default.Repeat to GreenRepost
                        "FOLLOW" -> Icons.Default.PersonAdd to TwitterBlue
                        else -> Icons.Default.Email to TwitterBlue
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(if (notification.isRead) Color.Transparent else DarkSurfaceVariant.copy(alpha = 0.3f))
                            .clickable { onUserClick(notification.senderId) }
                            .padding(16.dp)
                    ) {
                        Icon(
                            imageVector = icon.first,
                            contentDescription = null,
                            tint = icon.second,
                            modifier = Modifier
                                .size(24.dp)
                                .padding(end = 4.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            AsyncImage(
                                model = notification.senderAvatar,
                                contentDescription = null,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(CircleShape)
                                    .background(DarkSurfaceVariant)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = when (notification.type) {
                                    "LIKE" -> "${notification.senderName} liked your post."
                                    "COMMENT" -> "${notification.senderName} replied to your thread."
                                    "REPOST" -> "${notification.senderName} reposted your tweet."
                                    "FOLLOW" -> "${notification.senderName} followed you."
                                    else -> "${notification.senderName} sent you a direct message."
                                },
                                color = TextPrimary,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 14.sp
                            )
                            Text(
                                text = "@${notification.senderId}",
                                color = TextSecondary,
                                fontSize = 12.sp
                            )
                        }
                    }
                    HorizontalDivider(color = DarkOutline)
                }
            }
        }
    }
}


// ==========================================
// 6. USER PROFILE SCREEN
// ==========================================
@Composable
fun ProfileScreen(
    viewModel: SocialViewModel,
    onUserClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val user by viewModel.selectedProfileUser.collectAsStateWithLifecycle()
    val posts by viewModel.selectedProfilePosts.collectAsStateWithLifecycle()
    val isFollowing by viewModel.isFollowingSelectedUser.collectAsStateWithLifecycle()
    val currentUser by viewModel.currentUser.collectAsStateWithLifecycle()

    var showEditDialog by remember { mutableStateOf(false) }
    var editFullName by remember { mutableStateOf("") }
    var editBio by remember { mutableStateOf("") }

    // Synchronize edit values
    LaunchedEffect(user) {
        user?.let {
            editFullName = it.fullName
            editBio = it.bio
        }
    }

    if (user == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = TwitterBlue)
        }
        return
    }

    val isMyProfile = user!!.username == currentUser?.username

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBackground)
            .verticalScroll(rememberScrollState())
    ) {
        // Cover Banner
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(140.dp)
        ) {
            AsyncImage(
                model = user!!.coverUrl.ifEmpty { "https://images.unsplash.com/photo-1618005182384-a83a8bd57fbe?auto=format&fit=crop&w=800&q=80" },
                contentDescription = "Cover image",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        }

        // Profile Avatar and CTA Row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Bottom
        ) {
            // Floating Avatar
            AsyncImage(
                model = user!!.avatarUrl.ifEmpty { "https://images.unsplash.com/photo-1534528741775-53994a69daeb?auto=format&fit=crop&w=150&q=80" },
                contentDescription = "Profile Avatar",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(80.dp)
                    .offset(y = (-32).dp)
                    .clip(CircleShape)
                    .border(3.dp, DarkBackground, CircleShape)
                    .background(DarkSurfaceVariant)
            )

            // Dynamic Action Button (Follow/Edit)
            if (isMyProfile) {
                OutlinedButton(
                    onClick = { showEditDialog = true },
                    modifier = Modifier
                        .padding(bottom = 8.dp)
                        .testTag("edit_profile_button"),
                    border = BorderStroke(1.dp, TwitterBlue),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = TwitterBlue)
                ) {
                    Text("Edit Profile", fontWeight = FontWeight.Bold)
                }
            } else {
                Button(
                    onClick = { viewModel.toggleFollowSelectedUser() },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isFollowing) DarkSurfaceVariant else TwitterBlue
                    ),
                    modifier = Modifier.padding(bottom = 8.dp)
                ) {
                    Text(
                        text = if (isFollowing) "Following" else "Follow",
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        // Bio Section
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .offset(y = (-20).dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = user!!.fullName,
                    color = TextPrimary,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 20.sp
                )
                if (user!!.username == "admin" || user!!.username == "elonmusk" || user!!.username == "nasa_space") {
                    VerificationBadge()
                }
            }
            Text(text = "@${user!!.username}", color = TextSecondary, fontSize = 14.sp)

            if (user!!.bio.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = user!!.bio, color = TextPrimary, fontSize = 15.sp)
            }

            // Stats counts
            Spacer(modifier = Modifier.height(12.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = "${user!!.followingCount} ", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Text(text = "Following", color = TextSecondary, fontSize = 14.sp)
                Spacer(modifier = Modifier.width(16.dp))
                Text(text = "${user!!.followersCount} ", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Text(text = "Followers", color = TextSecondary, fontSize = 14.sp)
            }
        }

        HorizontalDivider(color = DarkOutline, modifier = Modifier.padding(top = 4.dp))

        // Posts listing
        Text(
            text = "Posts",
            fontWeight = FontWeight.Bold,
            color = TextPrimary,
            modifier = Modifier.padding(16.dp)
        )

        if (posts.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Text("No posts made yet.", color = TextSecondary)
            }
        } else {
            posts.forEach { post ->
                PostCard(
                    post = post,
                    currentUsername = currentUser?.username ?: "",
                    onUserClick = onUserClick,
                    onLikeClick = { viewModel.likePost(post.postId) },
                    onRepostClick = { viewModel.repostPost(post.postId) },
                    onCommentClick = { text -> viewModel.replyToPost(post.postId, text) },
                    onDeleteClick = { viewModel.deletePost(post.postId) },
                    onPinClick = { viewModel.togglePin(post.postId) }
                )
            }
        }
    }

    // Modal Edit Profile Dialog
    if (showEditDialog) {
        AlertDialog(
            onDismissRequest = { showEditDialog = false },
            title = { Text("Edit Profile", color = TextPrimary, fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    OutlinedTextField(
                        value = editFullName,
                        onValueChange = { editFullName = it },
                        label = { Text("Display Name") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary,
                            focusedBorderColor = TwitterBlue,
                            unfocusedBorderColor = DarkOutline
                        )
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = editBio,
                        onValueChange = { editBio = it },
                        label = { Text("Bio") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary,
                            focusedBorderColor = TwitterBlue,
                            unfocusedBorderColor = DarkOutline
                        ),
                        maxLines = 3
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.updateProfile(editBio, editFullName)
                        showEditDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = TwitterBlue)
                ) {
                    Text("Save", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { showEditDialog = false }) {
                    Text("Cancel", color = TextSecondary)
                }
            },
            containerColor = DarkSurface
        )
    }
}


// ==========================================
// 7. PLATFORM ADMIN DASHBOARD
// ==========================================
@Composable
fun AdminDashboardScreen(
    viewModel: SocialViewModel,
    onUserClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val allUsers by viewModel.allUsers.collectAsStateWithLifecycle()
    val posts by viewModel.feedPosts.collectAsStateWithLifecycle()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBackground)
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Text(
            text = "Platform Analytics",
            fontSize = 22.sp,
            fontWeight = FontWeight.ExtraBold,
            color = TextPrimary,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Analytics Row Cards
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Card(
                colors = CardDefaults.cardColors(containerColor = DarkSurface),
                border = CardBorder(),
                modifier = Modifier.weight(1f)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text("Total Users", color = TextSecondary, fontSize = 12.sp)
                    Text(allUsers.size.toString(), color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    // custom canvas mini chart
                    AnalyticsMiniChart(
                        points = listOf(1f, 2f, 2f, 3f, 4f, 4f, 6f),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(40.dp)
                    )
                }
            }

            Card(
                colors = CardDefaults.cardColors(containerColor = DarkSurface),
                border = CardBorder(),
                modifier = Modifier.weight(1f)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text("Engagements", color = TextSecondary, fontSize = 12.sp)
                    Text(posts.sumOf { it.likesCount + it.commentsCount }.toString(), color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    AnalyticsMiniChart(
                        points = listOf(5f, 12f, 15f, 8f, 20f, 25f, 42f),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(40.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "User Moderation Control",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimary,
            modifier = Modifier.padding(vertical = 8.dp)
        )

        Card(
            colors = CardDefaults.cardColors(containerColor = DarkSurface),
            border = CardBorder(),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                allUsers.forEachIndexed { idx, user ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            AsyncImage(
                                model = user.avatarUrl,
                                contentDescription = null,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(DarkSurfaceVariant)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = user.fullName,
                                        color = TextPrimary,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp
                                    )
                                    if (user.isVerified) {
                                        VerificationBadge()
                                    }
                                }
                                Text("@${user.username}", color = TextSecondary, fontSize = 11.sp)
                            }
                        }

                        // Moderation buttons
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            // Verify trigger
                            IconButton(
                                onClick = { viewModel.toggleUserVerification(user.username) },
                                modifier = Modifier
                                    .size(32.dp)
                                    .background(DarkSurfaceVariant, CircleShape)
                            ) {
                                Icon(
                                    imageVector = if (user.isVerified) Icons.Default.VerifiedUser else Icons.Default.Verified,
                                    contentDescription = "Verify",
                                    tint = if (user.isVerified) VerifiedBlue else TextSecondary,
                                    modifier = Modifier.size(16.dp)
                                )
                            }

                            // Ban / Suspend trigger
                            IconButton(
                                onClick = { viewModel.toggleUserRestriction(user.username) },
                                modifier = Modifier
                                    .size(32.dp)
                                    .background(if (user.isModerated) RedLike.copy(alpha = 0.2f) else DarkSurfaceVariant, CircleShape)
                            ) {
                                Icon(
                                    imageVector = if (user.isModerated) Icons.Default.LockOpen else Icons.Default.Lock,
                                    contentDescription = "Suspend",
                                    tint = if (user.isModerated) RedLike else TextSecondary,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }
                    if (idx < allUsers.size - 1) {
                        HorizontalDivider(color = DarkOutline)
                    }
                }
            }
        }
    }
}
