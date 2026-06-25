package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface SocialDao {

    // --- User Operations ---
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserEntity)

    @Update
    suspend fun updateUser(user: UserEntity)

    @Query("SELECT * FROM users WHERE username = :username LIMIT 1")
    suspend fun getUserByUsername(username: String): UserEntity?

    @Query("SELECT * FROM users WHERE username = :username LIMIT 1")
    fun getUserByUsernameFlow(username: String): Flow<UserEntity?>

    @Query("SELECT * FROM users")
    fun getAllUsersFlow(): Flow<List<UserEntity>>

    @Query("SELECT * FROM users WHERE username LIKE '%' || :query || '%' OR fullName LIKE '%' || :query || '%'")
    suspend fun searchUsers(query: String): List<UserEntity>


    // --- Post Operations ---
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPost(post: PostEntity): Long

    @Update
    suspend fun updatePost(post: PostEntity)

    @Query("DELETE FROM posts WHERE postId = :postId")
    suspend fun deletePostById(postId: Long)

    @Query("SELECT * FROM posts ORDER BY isPinned DESC, timestamp DESC")
    fun getFeedPostsFlow(): Flow<List<PostEntity>>

    @Query("SELECT * FROM posts WHERE userId = :userId ORDER BY isPinned DESC, timestamp DESC")
    fun getPostsByUserIdFlow(userId: String): Flow<List<PostEntity>>

    @Query("SELECT * FROM posts WHERE content LIKE '%' || :query || '%' ORDER BY timestamp DESC")
    fun searchPostsFlow(query: String): Flow<List<PostEntity>>

    @Query("SELECT * FROM posts WHERE postId = :postId LIMIT 1")
    suspend fun getPostById(postId: Long): PostEntity?


    // --- Like Operations ---
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLike(like: LikeEntity)

    @Query("DELETE FROM likes WHERE username = :username AND postId = :postId")
    suspend fun deleteLike(username: String, postId: Long)

    @Query("SELECT COUNT(*) FROM likes WHERE username = :username AND postId = :postId")
    suspend fun isPostLikedByUser(username: String, postId: Long): Int


    // --- Follow Operations ---
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFollow(follow: FollowEntity)

    @Query("DELETE FROM follows WHERE followerUsername = :follower AND followingUsername = :following")
    suspend fun deleteFollow(follower: String, following: String)

    @Query("SELECT COUNT(*) FROM follows WHERE followerUsername = :follower AND followingUsername = :following")
    suspend fun isFollowing(follower: String, following: String): Int


    // --- Message Operations ---
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(message: DirectMessageEntity)

    @Query("""
        SELECT * FROM direct_messages 
        WHERE (senderId = :user1 AND receiverId = :user2) 
           OR (senderId = :user2 AND receiverId = :user1) 
        ORDER BY timestamp ASC
    """)
    fun getChatMessagesFlow(user1: String, user2: String): Flow<List<DirectMessageEntity>>

    @Query("""
        SELECT * FROM direct_messages 
        WHERE senderId = :userId OR receiverId = :userId 
        ORDER BY timestamp DESC
    """)
    fun getAllUserMessagesFlow(userId: String): Flow<List<DirectMessageEntity>>


    // --- Notification Operations ---
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotification(notification: NotificationEntity)

    @Query("SELECT * FROM notifications WHERE receiverId = :receiverId ORDER BY timestamp DESC")
    fun getNotificationsFlow(receiverId: String): Flow<List<NotificationEntity>>

    @Query("UPDATE notifications SET isRead = 1 WHERE receiverId = :receiverId")
    suspend fun markAllNotificationsAsRead(receiverId: String)
}
