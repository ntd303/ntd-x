package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey val username: String, // Treat username/handle as the primary unique ID
    val fullName: String,
    val email: String,
    val passwordHash: String,
    val bio: String,
    val avatarUrl: String,
    val coverUrl: String,
    val isVerified: Boolean = false,
    val isModerated: Boolean = false,
    val followersCount: Int = 0,
    val followingCount: Int = 0,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "posts")
data class PostEntity(
    @PrimaryKey(autoGenerate = true) val postId: Long = 0,
    val userId: String, // References username in users table
    val authorName: String,
    val authorAvatar: String,
    val content: String,
    val mediaUrls: String = "", // Comma-separated image URLs or resource identifiers
    val timestamp: Long = System.currentTimeMillis(),
    val likesCount: Int = 0,
    val commentsCount: Int = 0,
    val repostsCount: Int = 0,
    val isPinned: Boolean = false,
    val isRepost: Boolean = false,
    val originalPostId: Long? = null,
    val originalAuthorName: String? = null
)

@Entity(tableName = "likes")
data class LikeEntity(
    @PrimaryKey(autoGenerate = true) val likeId: Long = 0,
    val username: String,
    val postId: Long
)

@Entity(tableName = "follows")
data class FollowEntity(
    @PrimaryKey(autoGenerate = true) val followId: Long = 0,
    val followerUsername: String,
    val followingUsername: String
)

@Entity(tableName = "direct_messages")
data class DirectMessageEntity(
    @PrimaryKey(autoGenerate = true) val messageId: Long = 0,
    val senderId: String,
    val receiverId: String,
    val content: String,
    val timestamp: Long = System.currentTimeMillis(),
    val isRead: Boolean = false
)

@Entity(tableName = "notifications")
data class NotificationEntity(
    @PrimaryKey(autoGenerate = true) val notificationId: Long = 0,
    val type: String, // "LIKE", "COMMENT", "FOLLOW", "REPOST"
    val senderId: String,
    val senderName: String,
    val senderAvatar: String,
    val receiverId: String,
    val postId: Long? = null,
    val timestamp: Long = System.currentTimeMillis(),
    val isRead: Boolean = false
)
