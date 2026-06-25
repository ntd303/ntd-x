package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.local.NotificationEntity
import com.example.data.local.PostEntity
import com.example.data.local.UserEntity
import com.example.data.repository.SocialRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class SocialViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: SocialRepository
    
    // --- Current Active User Session ---
    private val _currentUser = MutableStateFlow<UserEntity?>(null)
    val currentUser: StateFlow<UserEntity?> = _currentUser.asStateFlow()

    // --- Feed State ---
    val feedPosts: StateFlow<List<PostEntity>>

    // --- Search / Explore State ---
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _searchedUsers = MutableStateFlow<List<UserEntity>>(emptyList())
    val searchedUsers: StateFlow<List<UserEntity>> = _searchedUsers.asStateFlow()

    val searchedPosts: StateFlow<List<PostEntity>>

    // --- Messaging / DM State ---
    private val _activeChatPartner = MutableStateFlow<UserEntity?>(null)
    val activeChatPartner: StateFlow<UserEntity?> = _activeChatPartner.asStateFlow()

    val chatMessages: StateFlow<List<com.example.data.local.DirectMessageEntity>> = _activeChatPartner
        .flatMapLatest { partner ->
            val myUsername = _currentUser.value?.username ?: ""
            if (partner != null && myUsername.isNotEmpty()) {
                repository.getChatMessages(myUsername, partner.username)
            } else {
                flowOf(emptyList())
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val recentChats: StateFlow<List<com.example.data.local.DirectMessageEntity>> = _currentUser
        .flatMapLatest { user ->
            if (user != null) {
                repository.getAllUserMessages(user.username)
            } else {
                flowOf(emptyList())
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // --- Notifications State ---
    val notifications: StateFlow<List<NotificationEntity>> = _currentUser
        .flatMapLatest { user ->
            if (user != null) {
                repository.getNotifications(user.username)
            } else {
                flowOf(emptyList())
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // --- Profile Target State ---
    private val _selectedProfileUsername = MutableStateFlow<String>("")
    val selectedProfileUsername: StateFlow<String> = _selectedProfileUsername.asStateFlow()

    val selectedProfileUser: StateFlow<UserEntity?> = _selectedProfileUsername
        .flatMapLatest { username ->
            if (username.isNotEmpty()) {
                repository.getUserByUsernameFlow(username)
            } else {
                flowOf(null)
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val selectedProfilePosts: StateFlow<List<PostEntity>> = _selectedProfileUsername
        .flatMapLatest { username ->
            if (username.isNotEmpty()) {
                repository.getPostsByUserId(username)
            } else {
                flowOf(emptyList())
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _isFollowingSelectedUser = MutableStateFlow(false)
    val isFollowingSelectedUser: StateFlow<Boolean> = _isFollowingSelectedUser.asStateFlow()

    // --- Admin Dashboard State ---
    val allUsers: StateFlow<List<UserEntity>>

    // --- Trending Topics (Static Simulation list but dynamic filters) ---
    val trendingHashtags = listOf(
        "#NTd303Social" to "124.5K posts",
        "#JetpackCompose" to "84.2K posts",
        "#KotlinMultiplatform" to "43.1K posts",
        "#RoomDatabase" to "29.4K posts",
        "#AndroidDev" to "112.1K posts"
    )

    init {
        val database = AppDatabase.getDatabase(application)
        repository = SocialRepository(database.socialDao())

        feedPosts = repository.feedPosts
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

        searchedPosts = _searchQuery
            .debounce(300)
            .flatMapLatest { query ->
                if (query.length >= 2) {
                    repository.searchPosts(query)
                } else {
                    repository.feedPosts
                }
            }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

        allUsers = repository.allUsers
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

        // Seed default database values if empty
        seedDatabaseIfEmpty()
    }

    private fun seedDatabaseIfEmpty() {
        viewModelScope.launch {
            val users = repository.searchUsers("")
            if (users.isEmpty()) {
                // Seed default users
                val adminUser = UserEntity(
                    username = "admin",
                    fullName = "System Administrator",
                    email = "admin@ntd303.social",
                    passwordHash = "admin123",
                    bio = "Primary platform architect & content moderation executive for NTd_303 Social. Security, scale, speed. 🛡️",
                    avatarUrl = "https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?auto=format&fit=crop&w=150&q=80",
                    coverUrl = "https://images.unsplash.com/photo-1618005182384-a83a8bd57fbe?auto=format&fit=crop&w=800&q=80",
                    isVerified = true
                )
                repository.registerUser(adminUser)

                val elon = UserEntity(
                    username = "elonmusk",
                    fullName = "Elon Musk",
                    email = "elon@tesla.com",
                    passwordHash = "elon123",
                    bio = "Mars & Earth, Engineering, Design, AI, Rockets. Let that sink in! 🚀",
                    avatarUrl = "https://images.unsplash.com/photo-1570295999919-56ceb5ecca61?auto=format&fit=crop&w=150&q=80",
                    coverUrl = "https://images.unsplash.com/photo-1451187580459-43490279c0fa?auto=format&fit=crop&w=800&q=80",
                    isVerified = true,
                    followersCount = 420000,
                    followingCount = 143
                )
                repository.registerUser(elon)

                val techInsider = UserEntity(
                    username = "sarah_tech",
                    fullName = "Sarah Chen",
                    email = "sarah@techinsider.com",
                    passwordHash = "sarah123",
                    bio = "Staff Reviewer @TechInsider. Exploring Jetpack Compose, Room DB & Android edge-to-edge aesthetics. 📱💻",
                    avatarUrl = "https://images.unsplash.com/photo-1494790108377-be9c29b29330?auto=format&fit=crop&w=150&q=80",
                    coverUrl = "https://images.unsplash.com/photo-1550751827-4bd374c3f58b?auto=format&fit=crop&w=800&q=80",
                    isVerified = true,
                    followersCount = 54100,
                    followingCount = 432
                )
                repository.registerUser(techInsider)

                val nasa = UserEntity(
                    username = "nasa_space",
                    fullName = "NASA Space",
                    email = "contact@nasa.gov",
                    passwordHash = "nasa123",
                    bio = "There is space for everyone. Exploring the secrets of the universe for the benefit of all. 🚀🌌☄️",
                    avatarUrl = "https://images.unsplash.com/photo-1506703719100-a0f3a48c0f86?auto=format&fit=crop&w=150&q=80",
                    coverUrl = "https://images.unsplash.com/photo-1446776811953-b23d57bd21aa?auto=format&fit=crop&w=800&q=80",
                    isVerified = true,
                    followersCount = 982000,
                    followingCount = 29
                )
                repository.registerUser(nasa)

                // Seed posts
                repository.createPost(
                    userId = "admin",
                    authorName = "System Administrator",
                    authorAvatar = "https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?auto=format&fit=crop&w=150&q=80",
                    content = "Welcome to NTd_303 Social! This platform is built entirely using Kotlin, Jetpack Compose, and Room Database. Check out the beautiful custom transitions, fully persistent direct messaging, notifications, and the live Admin Panel dashboard!"
                )

                val post2 = repository.createPost(
                    userId = "sarah_tech",
                    authorName = "Sarah Chen",
                    authorAvatar = "https://images.unsplash.com/photo-1494790108377-be9c29b29330?auto=format&fit=crop&w=150&q=80",
                    content = "Developing on Android feels magical with Jetpack Compose. Clean state representation, responsive edge-to-edge layouts, and incredibly smooth spring animations. Loving this clean design! #AndroidDev #JetpackCompose",
                    mediaUrls = "https://images.unsplash.com/photo-1555066931-4365d14bab8c?auto=format&fit=crop&w=800&q=80"
                )

                repository.createPost(
                    userId = "nasa_space",
                    authorName = "NASA Space",
                    authorAvatar = "https://images.unsplash.com/photo-1506703719100-a0f3a48c0f86?auto=format&fit=crop&w=150&q=80",
                    content = "Behold the beauty of deep space! Captured by our satellite, this distant nebula shows active star formation. The universe is full of mysteries waiting to be solved. 🌌✨🚀 #NASA #Space",
                    mediaUrls = "https://images.unsplash.com/photo-1462331940025-496dfbfc7564?auto=format&fit=crop&w=800&q=80"
                )

                repository.createPost(
                    userId = "elonmusk",
                    authorName = "Elon Musk",
                    authorAvatar = "https://images.unsplash.com/photo-1570295999919-56ceb5ecca61?auto=format&fit=crop&w=150&q=80",
                    content = "Next gen rocket boosters are lookin' good. Next test flight scheduled for tomorrow! We are going to Mars sooner than you think. 🚀🌌🔥 #NTd303Social #Mars"
                )

                // Trigger a starter notification & messaging history
                repository.toggleFollow("sarah_tech", "admin")
                repository.toggleLike("sarah_tech", post2)
                repository.sendMessage("sarah_tech", "admin", "Hey Admin! Amazing social media build. High performance, zero lag!")
                repository.sendMessage("admin", "sarah_tech", "Thanks Sarah! The persistent Room DB engine and fluid Compose list views make it blazing fast. 🔥")
            }
        }
    }


    // --- Auth Actions ---
    fun register(username: String, fullName: String, email: String, pass: String, bio: String, onResult: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            if (username.isBlank() || fullName.isBlank() || email.isBlank() || pass.isBlank()) {
                onResult(false, "Please fill in all mandatory fields.")
                return@launch
            }
            val newUser = UserEntity(
                username = username.trim().lowercase(),
                fullName = fullName.trim(),
                email = email.trim(),
                passwordHash = pass,
                bio = bio.trim(),
                avatarUrl = "https://images.unsplash.com/photo-1534528741775-53994a69daeb?auto=format&fit=crop&w=150&q=80",
                coverUrl = "https://images.unsplash.com/photo-1579546929518-9e396f3cc809?auto=format&fit=crop&w=800&q=80"
            )
            val success = repository.registerUser(newUser)
            if (success) {
                _currentUser.value = newUser
                onResult(true, "Registration successful!")
            } else {
                onResult(false, "Username '@$username' is already taken.")
            }
        }
    }

    fun login(username: String, pass: String, onResult: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            if (username.isBlank() || pass.isBlank()) {
                onResult(false, "Username and password cannot be empty.")
                return@launch
            }
            val user = repository.loginUser(username.trim().lowercase(), pass)
            if (user != null) {
                if (user.isModerated) {
                    onResult(false, "Your account has been restricted by administrators.")
                } else {
                    _currentUser.value = user
                    onResult(true, "Login successful!")
                }
            } else {
                onResult(false, "Invalid username or password.")
            }
        }
    }

    fun logout() {
        _currentUser.value = null
    }


    // --- Post Actions ---
    fun submitPost(content: String, mediaUrls: String) {
        viewModelScope.launch {
            val user = _currentUser.value ?: return@launch
            repository.createPost(
                userId = user.username,
                authorName = user.fullName,
                authorAvatar = user.avatarUrl,
                content = content,
                mediaUrls = mediaUrls
            )
        }
    }

    fun deletePost(postId: Long) {
        viewModelScope.launch {
            repository.deletePost(postId)
        }
    }

    fun togglePin(postId: Long) {
        viewModelScope.launch {
            repository.togglePinPost(postId)
        }
    }

    fun editPost(postId: Long, newContent: String) {
        viewModelScope.launch {
            repository.updatePostContent(postId, newContent)
        }
    }


    // --- Post Interactions ---
    fun likePost(postId: Long) {
        viewModelScope.launch {
            val user = _currentUser.value ?: return@launch
            repository.toggleLike(user.username, postId)
        }
    }

    fun repostPost(postId: Long) {
        viewModelScope.launch {
            val user = _currentUser.value ?: return@launch
            repository.createRepost(user.username, postId)
        }
    }

    fun replyToPost(postId: Long, text: String) {
        viewModelScope.launch {
            val user = _currentUser.value ?: return@launch
            repository.addComment(user.username, postId, text)
        }
    }


    // --- Search Actions ---
    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
        viewModelScope.launch {
            _searchedUsers.value = repository.searchUsers(query)
        }
    }


    // --- Message Actions ---
    fun selectChatPartner(partner: UserEntity?) {
        _activeChatPartner.value = partner
    }

    fun sendChatMessage(content: String) {
        viewModelScope.launch {
            val me = _currentUser.value ?: return@launch
            val partner = _activeChatPartner.value ?: return@launch
            if (content.isBlank()) return@launch
            
            repository.sendMessage(me.username, partner.username, content)
            
            // Simulating typing and instant responses for mock bots
            simulateSmartBotResponse(partner, content)
        }
    }

    private fun simulateSmartBotResponse(partner: UserEntity, userMessage: String) {
        if (partner.username == "admin" || partner.username == "elonmusk" || partner.username == "sarah_tech" || partner.username == "nasa_space") {
            viewModelScope.launch {
                kotlinx.coroutines.delay(1500) // Delay to feel like natural typing
                val response = when (partner.username) {
                    "elonmusk" -> {
                        if (userMessage.contains("mars", true) || userMessage.contains("rocket", true)) {
                            "Mars is our priority! Full propulsion test flights coming up. Let's make humanity multiplanetary! 🚀✨"
                        } else {
                            "Interesting! Tesla and SpaceX are building some wild tech. What are your thoughts on neural nets?"
                        }
                    }
                    "sarah_tech" -> "Totally! Jetpack Compose represents a massive paradigm shift. Declarative UI makes development super fun and fluid."
                    "nasa_space" -> "Star formation is indeed an incredible spectacle. Stay tuned for more high-resolution updates from deep space!"
                    else -> "Thank you for the message. Your report and feedback have been logged securely. Stay secure! 🛡️"
                }
                repository.sendMessage(partner.username, _currentUser.value?.username ?: "", response)
            }
        }
    }


    // --- Follow / Profile Actions ---
    fun selectProfileUser(username: String) {
        _selectedProfileUsername.value = username
        viewModelScope.launch {
            val me = _currentUser.value?.username ?: ""
            if (me.isNotEmpty() && username.isNotEmpty()) {
                _isFollowingSelectedUser.value = repository.checkIsFollowing(me, username)
            }
        }
    }

    fun toggleFollowSelectedUser() {
        viewModelScope.launch {
            val me = _currentUser.value ?: return@launch
            val target = _selectedProfileUsername.value
            if (target.isEmpty() || target == me.username) return@launch
            
            val isFollowingNow = repository.toggleFollow(me.username, target)
            _isFollowingSelectedUser.value = isFollowingNow
            
            // Update local user model cache
            _currentUser.value = repository.getUserByUsername(me.username)
        }
    }

    fun updateProfile(bio: String, fullName: String) {
        viewModelScope.launch {
            val me = _currentUser.value ?: return@launch
            val updated = me.copy(bio = bio, fullName = fullName)
            repository.updateUser(updated)
            _currentUser.value = updated
            // If viewing own profile, trigger refresh
            if (_selectedProfileUsername.value == me.username) {
                _selectedProfileUsername.value = me.username
            }
        }
    }


    // --- Notification Actions ---
    fun clearNotifications() {
        viewModelScope.launch {
            val me = _currentUser.value ?: return@launch
            repository.markNotificationsAsRead(me.username)
        }
    }


    // --- Admin Operations ---
    fun toggleUserVerification(username: String) {
        viewModelScope.launch {
            val user = repository.getUserByUsername(username) ?: return@launch
            val updated = user.copy(isVerified = !user.isVerified)
            repository.updateUser(updated)
            // If current user is modified, update current user flow
            if (_currentUser.value?.username == username) {
                _currentUser.value = updated
            }
        }
    }

    fun toggleUserRestriction(username: String) {
        viewModelScope.launch {
            val user = repository.getUserByUsername(username) ?: return@launch
            val updated = user.copy(isModerated = !user.isModerated)
            repository.updateUser(updated)
            // If current user is restricted, log out or update
            if (_currentUser.value?.username == username) {
                _currentUser.value = null
            }
        }
    }
}
