package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.data.local.PostEntity
import com.example.ui.theme.*

@Composable
fun XLogo(modifier: Modifier = Modifier, tint: Color = Color.White) {
    Canvas(modifier = modifier.size(48.dp)) {
        val w = size.width
        val h = size.height
        
        // Draw modern minimalist X design
        val path = Path().apply {
            moveTo(w * 0.15f, h * 0.15f)
            lineTo(w * 0.85f, h * 0.85f)
        }
        val path2 = Path().apply {
            moveTo(w * 0.85f, h * 0.15f)
            lineTo(w * 0.48f, h * 0.48f)
            moveTo(w * 0.38f, h * 0.58f)
            lineTo(w * 0.15f, h * 0.85f)
        }
        
        drawPath(
            path = path,
            color = tint,
            style = Stroke(width = w * 0.12f, cap = StrokeCap.Round)
        )
        drawPath(
            path = path2,
            color = tint,
            style = Stroke(width = w * 0.08f, cap = StrokeCap.Round)
        )
    }
}

@Composable
fun VerificationBadge(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .padding(start = 4.dp)
            .size(16.dp)
            .background(VerifiedBlue, CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Default.Check,
            contentDescription = "Verified User",
            tint = Color.White,
            modifier = Modifier.size(11.dp)
        )
    }
}

@Composable
fun PostCard(
    post: PostEntity,
    currentUsername: String,
    onUserClick: (String) -> Unit,
    onLikeClick: () -> Unit,
    onRepostClick: () -> Unit,
    onCommentClick: (String) -> Unit,
    onDeleteClick: (() -> Unit)? = null,
    onPinClick: (() -> Unit)? = null,
    isLiked: Boolean = false,
    modifier: Modifier = Modifier
) {
    var showCommentDialog by remember { mutableStateOf(false) }
    var commentText by remember { mutableStateOf("") }
    
    val likeColor by animateColorAsState(
        targetValue = if (isLiked) RedLike else TextSecondary,
        animationSpec = spring(),
        label = "LikeColor"
    )
    val likeScale by animateFloatAsState(
        targetValue = if (isLiked) 1.2f else 1.0f,
        animationSpec = spring(),
        label = "LikeScale"
    )

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp, horizontal = 8.dp)
            .testTag("post_card_${post.postId}"),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(24.dp),
        border = CardBorder()
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            // Repost Tag
            if (post.isRepost) {
                Row(
                    modifier = Modifier.padding(bottom = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Repeat,
                        contentDescription = "Repost",
                        tint = GreenRepost,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "${post.originalAuthorName ?: "User"} reposted",
                        color = TextSecondary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            // Pinned Post Tag
            if (post.isPinned) {
                Row(
                    modifier = Modifier.padding(bottom = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.PushPin,
                        contentDescription = "Pinned",
                        tint = TwitterBlue,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Pinned Post",
                        color = TwitterBlue,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Row(modifier = Modifier.fillMaxWidth()) {
                // Avatar
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(post.authorAvatar.ifEmpty { "https://images.unsplash.com/photo-1534528741775-53994a69daeb?auto=format&fit=crop&w=150&q=80" })
                        .crossfade(true)
                        .build(),
                    contentDescription = "Author Avatar",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(46.dp)
                        .clip(CircleShape)
                        .background(DarkSurfaceVariant)
                        .clickable { onUserClick(post.userId) }
                )

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    // Header info
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f, fill = false)
                        ) {
                            Text(
                                text = post.authorName,
                                color = TextPrimary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.clickable { onUserClick(post.userId) }
                            )
                            // Display verification checkmarks dynamically
                            if (post.userId == "admin" || post.userId == "elonmusk" || post.userId == "nasa_space" || post.userId == "sarah_tech") {
                                VerificationBadge()
                            }
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "@${post.userId}",
                                color = TextSecondary,
                                fontSize = 13.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }

                        // Options Menu (Delete & Pin triggers)
                        Box {
                            var showMenu by remember { mutableStateOf(false) }
                            if (onDeleteClick != null || onPinClick != null) {
                                IconButton(
                                    onClick = { showMenu = true },
                                    modifier = Modifier.size(24.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.MoreVert,
                                        contentDescription = "Post Options",
                                        tint = TextSecondary
                                    )
                                }
                                DropdownMenu(
                                    expanded = showMenu,
                                    onDismissRequest = { showMenu = false },
                                    modifier = Modifier.background(DarkSurfaceVariant)
                                ) {
                                    if (onPinClick != null && post.userId == currentUsername) {
                                        DropdownMenuItem(
                                            text = { Text(if (post.isPinned) "Unpin Post" else "Pin to Profile", color = TextPrimary) },
                                            onClick = {
                                                onPinClick()
                                                showMenu = false
                                            }
                                        )
                                    }
                                    if (onDeleteClick != null && (post.userId == currentUsername || currentUsername == "admin")) {
                                        DropdownMenuItem(
                                            text = { Text("Delete Post", color = RedLike) },
                                            onClick = {
                                                onDeleteClick()
                                                showMenu = false
                                            }
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Content
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = post.content,
                        color = TextPrimary,
                        fontSize = 15.sp,
                        lineHeight = 20.sp
                    )

                    // Media Attachments
                    if (post.mediaUrls.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        AsyncImage(
                            model = ImageRequest.Builder(LocalContext.current)
                                .data(post.mediaUrls)
                                .crossfade(true)
                                .build(),
                            contentDescription = "Post Image Attachment",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .background(DarkSurfaceVariant)
                        )
                    }

                    // Action bar (Like, Comment, Repost, Bookmark)
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Comments
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.clickable { showCommentDialog = true }
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.ChatBubbleOutline,
                                contentDescription = "Comment",
                                tint = TextSecondary,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = post.commentsCount.toString(),
                                color = TextSecondary,
                                fontSize = 12.sp
                            )
                        }

                        // Reposts
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.clickable { onRepostClick() }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Repeat,
                                contentDescription = "Repost",
                                tint = if (post.repostsCount > 0) GreenRepost else TextSecondary,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = post.repostsCount.toString(),
                                color = if (post.repostsCount > 0) GreenRepost else TextSecondary,
                                fontSize = 12.sp
                            )
                        }

                        // Likes
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.clickable { onLikeClick() }
                        ) {
                            Icon(
                                imageVector = if (isLiked) Icons.Default.Favorite else Icons.Outlined.FavoriteBorder,
                                contentDescription = "Like",
                                tint = likeColor,
                                modifier = Modifier
                                    .size(18.dp)
                                    // simple dynamic pop effect on click
                                    .size((18 * likeScale).dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = post.likesCount.toString(),
                                color = likeColor,
                                fontSize = 12.sp
                            )
                        }

                        // Bookmark
                        IconButton(
                            onClick = { },
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.BookmarkBorder,
                                contentDescription = "Bookmark",
                                tint = TextSecondary,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }
        }
    }

    // Modal dialog to add comments
    if (showCommentDialog) {
        AlertDialog(
            onDismissRequest = { showCommentDialog = false },
            title = { Text("Comment on Post", color = TextPrimary, fontWeight = FontWeight.Bold) },
            text = {
                OutlinedTextField(
                    value = commentText,
                    onValueChange = { commentText = it },
                    placeholder = { Text("Write your comment...", color = TextSecondary) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        focusedBorderColor = TwitterBlue,
                        unfocusedBorderColor = DarkOutline
                    )
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (commentText.isNotBlank()) {
                            onCommentClick(commentText)
                            commentText = ""
                            showCommentDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = TwitterBlue)
                ) {
                    Text("Reply", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { showCommentDialog = false }) {
                    Text("Cancel", color = TextSecondary)
                }
            },
            containerColor = DarkSurface
        )
    }
}

@Composable
fun CardBorder(): BorderStroke {
    return BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
}

@Composable
fun MessageBubble(
    text: String,
    isMe: Boolean,
    timestamp: Long,
    modifier: Modifier = Modifier
) {
    val bubbleShape = if (isMe) {
        RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp, bottomStart = 16.dp, bottomEnd = 2.dp)
    } else {
        RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp, bottomStart = 2.dp, bottomEnd = 16.dp)
    }

    val bubbleBackground = if (isMe) {
        TwitterBlue
    } else {
        DarkSurfaceVariant
    }

    val textColor = if (isMe) Color.White else TextPrimary

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp, horizontal = 12.dp),
        horizontalArrangement = if (isMe) Arrangement.End else Arrangement.Start
    ) {
        Column(
            horizontalAlignment = if (isMe) Alignment.End else Alignment.Start,
            modifier = Modifier.widthIn(max = 280.dp)
        ) {
            Box(
                modifier = Modifier
                    .background(bubbleBackground, bubbleShape)
                    .padding(horizontal = 14.dp, vertical = 10.dp)
            ) {
                Text(
                    text = text,
                    color = textColor,
                    fontSize = 15.sp,
                    lineHeight = 20.sp
                )
            }
            Spacer(modifier = Modifier.height(2.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "9:37 AM", // Simulating formatted timestamp
                    fontSize = 11.sp,
                    color = TextSecondary
                )
                if (isMe) {
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Read",
                        tint = TwitterBlue,
                        modifier = Modifier.size(12.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun AnalyticsMiniChart(
    points: List<Float>,
    modifier: Modifier = Modifier,
    lineBrush: Brush = Brush.horizontalGradient(listOf(TwitterBlue, VerifiedBlue))
) {
    Canvas(modifier = modifier) {
        val width = size.width
        val height = size.height
        if (points.size < 2) return@Canvas

        val maxVal = points.maxOrNull() ?: 1f
        val minVal = points.minOrNull() ?: 0f
        val range = (maxVal - minVal).coerceAtLeast(1f)

        val path = Path()
        val spacing = width / (points.size - 1)

        points.forEachIndexed { idx, point ->
            val x = idx * spacing
            val y = height - ((point - minVal) / range) * (height * 0.8f) - (height * 0.1f)
            if (idx == 0) {
                path.moveTo(x, y)
            } else {
                path.lineTo(x, y)
            }
        }

        drawPath(
            path = path,
            brush = lineBrush,
            style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
        )

        // Draw shadow under line
        val fillPath = Path().apply {
            addPath(path)
            lineTo(width, height)
            lineTo(0f, height)
            close()
        }
        drawPath(
            path = fillPath,
            brush = Brush.verticalGradient(
                colors = listOf(TwitterBlue.copy(alpha = 0.2f), Color.Transparent),
                startY = 0f,
                endY = height
            )
        )
    }
}
