package com.example.data.repository

import com.example.data.local.SocialDao
import com.example.data.local.UserEntity
import com.example.data.local.PostEntity
import com.example.data.local.LikeEntity
import com.example.data.local.FollowEntity
import com.example.data.local.DirectMessageEntity
import com.example.data.local.NotificationEntity
import kotlinx.coroutines.flow.Flow

class SocialRepository(private val socialDao: SocialDao) {

    // --- Users ---
    val allUsers: Flow<List<UserEntity>> = socialDao.getAllUsersFlow()

    suspend fun registerUser(user: UserEntity): Boolean {
        val existing = socialDao.getUserByUsername(user.username)
        if (existing != null) return false // Username already taken
        socialDao.insertUser(user)
        return true
    }

    suspend fun loginUser(username: String, passwordHash: String): UserEntity? {
        val user = socialDao.getUserByUsername(username)
        if (user != null && user.passwordHash == passwordHash) {
            return user
        }
        return null
    }

    suspend fun getUserByUsername(username: String): UserEntity? {
        return socialDao.getUserByUsername(username)
    }

    fun getUserByUsernameFlow(username: String): Flow<UserEntity?> {
        return socialDao.getUserByUsernameFlow(username)
    }

    suspend fun updateUser(user: UserEntity) {
        socialDao.updateUser(user)
    }

    suspend fun searchUsers(query: String): List<UserEntity> {
        return socialDao.searchUsers(query)
    }


    // --- Posts & Feed ---
    val feedPosts: Flow<List<PostEntity>> = socialDao.getFeedPostsFlow()

    fun getPostsByUserId(userId: String): Flow<List<PostEntity>> {
        return socialDao.getPostsByUserIdFlow(userId)
    }

    fun searchPosts(query: String): Flow<List<PostEntity>> {
        return socialDao.searchPostsFlow(query)
    }

    suspend fun createPost(
        userId: String,
        authorName: String,
        authorAvatar: String,
        content: String,
        mediaUrls: String = ""
    ): Long {
        val post = PostEntity(
            userId = userId,
            authorName = authorName,
            authorAvatar = authorAvatar,
            content = content,
            mediaUrls = mediaUrls
        )
        return socialDao.insertPost(post)
    }

    suspend fun deletePost(postId: Long) {
        socialDao.deletePostById(postId)
    }

    suspend fun togglePinPost(postId: Long) {
        val post = socialDao.getPostById(postId) ?: return
        val updated = post.copy(isPinned = !post.isPinned)
        socialDao.updatePost(updated)
    }

    suspend fun updatePostContent(postId: Long, newContent: String) {
        val post = socialDao.getPostById(postId) ?: return
        val updated = post.copy(content = newContent)
        socialDao.updatePost(updated)
    }


    // --- Post Interactions (Likes, Reposts, Comments) ---
    suspend fun toggleLike(username: String, postId: Long): Boolean {
        val isLiked = socialDao.isPostLikedByUser(username, postId) > 0
        val post = socialDao.getPostById(postId) ?: return false
        
        if (isLiked) {
            socialDao.deleteLike(username, postId)
            val updated = post.copy(likesCount = (post.likesCount - 1).coerceAtLeast(0))
            socialDao.updatePost(updated)
            return false // Post is now unliked
        } else {
            socialDao.insertLike(LikeEntity(username = username, postId = postId))
            val updated = post.copy(likesCount = post.likesCount + 1)
            socialDao.updatePost(updated)
            
            // Create a notification for the post author (if it's not the same user)
            if (post.userId != username) {
                val liker = socialDao.getUserByUsername(username)
                socialDao.insertNotification(
                    NotificationEntity(
                        type = "LIKE",
                        senderId = username,
                        senderName = liker?.fullName ?: username,
                        senderAvatar = liker?.avatarUrl ?: "",
                        receiverId = post.userId,
                        postId = postId
                    )
                )
            }
            return true // Post is now liked
        }
    }

    suspend fun isPostLiked(username: String, postId: Long): Boolean {
        return socialDao.isPostLikedByUser(username, postId) > 0
    }

    suspend fun createRepost(username: String, originalPostId: Long): Long {
        val original = socialDao.getPostById(originalPostId) ?: return -1
        val reposter = socialDao.getUserByUsername(username) ?: return -1
        
        // Insert new post marking it as a Repost
        val repost = PostEntity(
            userId = username,
            authorName = reposter.fullName,
            authorAvatar = reposter.avatarUrl,
            content = original.content,
            mediaUrls = original.mediaUrls,
            isRepost = true,
            originalPostId = originalPostId,
            originalAuthorName = original.authorName
        )
        
        // Update original post repost counter
        socialDao.updatePost(original.copy(repostsCount = original.repostsCount + 1))
        
        val newPostId = socialDao.insertPost(repost)
        
        // Send a notification if original owner is different
        if (original.userId != username) {
            socialDao.insertNotification(
                NotificationEntity(
                    type = "REPOST",
                    senderId = username,
                    senderName = reposter.fullName,
                    senderAvatar = reposter.avatarUrl,
                    receiverId = original.userId,
                    postId = originalPostId
                )
            )
        }
        
        return newPostId
    }

    suspend fun addComment(username: String, postId: Long, commentText: String) {
        val post = socialDao.getPostById(postId) ?: return
        val commenter = socialDao.getUserByUsername(username) ?: return
        
        // Update original comment count
        socialDao.updatePost(post.copy(commentsCount = post.commentsCount + 1))
        
        // Create an implicit reply post
        val replyPost = PostEntity(
            userId = username,
            authorName = commenter.fullName,
            authorAvatar = commenter.avatarUrl,
            content = "Replying to @${post.userId}: $commentText",
            isRepost = false
        )
        socialDao.insertPost(replyPost)
        
        // Notify original poster
        if (post.userId != username) {
            socialDao.insertNotification(
                NotificationEntity(
                    type = "COMMENT",
                    senderId = username,
                    senderName = commenter.fullName,
                    senderAvatar = commenter.avatarUrl,
                    receiverId = post.userId,
                    postId = postId
                )
            )
        }
    }


    // --- Follow / Unfollow ---
    suspend fun toggleFollow(follower: String, following: String): Boolean {
        val isFollowing = socialDao.isFollowing(follower, following) > 0
        val fUser = socialDao.getUserByUsername(follower) ?: return false
        val tUser = socialDao.getUserByUsername(following) ?: return false
        
        if (isFollowing) {
            socialDao.deleteFollow(follower, following)
            socialDao.updateUser(fUser.copy(followingCount = (fUser.followingCount - 1).coerceAtLeast(0)))
            socialDao.updateUser(tUser.copy(followersCount = (tUser.followersCount - 1).coerceAtLeast(0)))
            return false // Now unfollowed
        } else {
            socialDao.insertFollow(FollowEntity(followerUsername = follower, followingUsername = following))
            socialDao.updateUser(fUser.copy(followingCount = fUser.followingCount + 1))
            socialDao.updateUser(tUser.copy(followersCount = tUser.followersCount + 1))
            
            // Create Notification
            socialDao.insertNotification(
                NotificationEntity(
                    type = "FOLLOW",
                    senderId = follower,
                    senderName = fUser.fullName,
                    senderAvatar = fUser.avatarUrl,
                    receiverId = following
                )
            )
            return true // Now followed
        }
    }

    suspend fun checkIsFollowing(follower: String, following: String): Boolean {
        return socialDao.isFollowing(follower, following) > 0
    }


    // --- Messaging ---
    fun getChatMessages(user1: String, user2: String): Flow<List<DirectMessageEntity>> {
        return socialDao.getChatMessagesFlow(user1, user2)
    }

    fun getAllUserMessages(userId: String): Flow<List<DirectMessageEntity>> {
        return socialDao.getAllUserMessagesFlow(userId)
    }

    suspend fun sendMessage(senderId: String, receiverId: String, content: String) {
        val sender = socialDao.getUserByUsername(senderId)
        val msg = DirectMessageEntity(
            senderId = senderId,
            receiverId = receiverId,
            content = content
        )
        socialDao.insertMessage(msg)
        
        // Also send a Message notification
        socialDao.insertNotification(
            NotificationEntity(
                type = "MESSAGE",
                senderId = senderId,
                senderName = sender?.fullName ?: senderId,
                senderAvatar = sender?.avatarUrl ?: "",
                receiverId = receiverId
            )
        )
    }


    // --- Notifications ---
    fun getNotifications(receiverId: String): Flow<List<NotificationEntity>> {
        return socialDao.getNotificationsFlow(receiverId)
    }

    suspend fun markNotificationsAsRead(receiverId: String) {
        socialDao.markAllNotificationsAsRead(receiverId)
    }
}
