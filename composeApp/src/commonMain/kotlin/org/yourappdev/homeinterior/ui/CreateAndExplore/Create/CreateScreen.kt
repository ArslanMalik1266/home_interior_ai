package org.yourappdev.homeinterior.ui.CreateAndExplore.Create

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import coil3.compose.LocalPlatformContext
import coil3.request.ImageRequest
import coil3.request.crossfade
import homeinterior.composeapp.generated.resources.Res
import homeinterior.composeapp.generated.resources.add_2_24px
import homeinterior.composeapp.generated.resources.createpageimage
import homeinterior.composeapp.generated.resources.premiumicon
import homeinterior.composeapp.generated.resources.roomplaceholder
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.viewmodel.koinViewModel
import org.yourappdev.homeinterior.data.local.entities.RecentGeneratedEntity
import org.yourappdev.homeinterior.domain.model.RoomUi
import org.yourappdev.homeinterior.ui.CreateAndExplore.RoomEvent
import org.yourappdev.homeinterior.ui.CreateAndExplore.RoomsViewModel
import org.yourappdev.homeinterior.ui.theme.black_color
import org.yourappdev.homeinterior.ui.theme.green_btn
import org.yourappdev.homeinterior.ui.theme.white_color
import org.yourappdev.homeinterior.utils.getImageModel

@Composable
fun CreateScreen(
    viewModel: RoomsViewModel = koinViewModel(),
    onPremiumClick: () -> Unit = {},
    onAddPhotoClick: () -> Unit = {},
    onRoomClick: (RoomUi) -> Unit = {},
    onShowResults: () -> Unit,
    onSeeAllClick: () -> Unit
) {
    val state by viewModel.state.collectAsState()
    val dbImages by viewModel.dbGeneratedImages.collectAsState()

    LaunchedEffect(dbImages) {
        println("🟣 UI_CREATE: dbImages count = ${dbImages.size}")
    }

    // ✅ Bundles of entities
    val generatedBundles = dbImages.chunked(1).take(10)

    LaunchedEffect(generatedBundles) {
        println("🟣 UI_CREATE: bundles count = ${generatedBundles.size}")
    }

    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(white_color)
            .windowInsetsPadding(WindowInsets.statusBars),
        verticalArrangement = Arrangement.spacedBy(32.dp)
    ) {
        Header(onClick = onPremiumClick)
        EmptyStateCard({ onAddPhotoClick() })
        Column(
            modifier = Modifier.verticalScroll(scrollState),
            verticalArrangement = Arrangement.spacedBy(32.dp)
        ) {
            TrendingSection(
                rooms = state.trendingRooms,
                onRoomClick = onRoomClick
            )
            RecentFilesSection(
                generatedBundles = generatedBundles,
                onBundleClick = { bundle ->
                    viewModel.onRoomEvent(RoomEvent.ShowSelectedBundle(bundle))
                    onShowResults()
                },
                onSeeAllClick = onSeeAllClick
            )
        }
    }
}

@Composable
fun Header(onClick: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(start = 24.dp, end = 20.dp),
        verticalAlignment = Alignment.Top
    ) {
        Text(
            text = "Interior AI",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF2C2C2C),
            modifier = Modifier.weight(1f),
            textAlign = TextAlign.Start
        )
        Box(
            modifier = Modifier.size(30.dp).clip(CircleShape).clickable { onClick() },
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(Res.drawable.premiumicon),
                contentDescription = "",
                modifier = Modifier.size(21.dp)
            )
        }
    }
}

@Composable
private fun EmptyStateCard(onClick: () -> Unit) {
    Box(modifier = Modifier.padding(start = 24.dp, end = 24.dp)) {
        Image(painter = painterResource(Res.drawable.createpageimage), contentDescription = null)
        Surface(
            onClick = { onClick() },
            color = green_btn,
            shape = RoundedCornerShape(20),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 16.dp)
                .height(28.dp),
        ) {
            Row(
                modifier = Modifier
                    .padding(horizontal = 16.dp, vertical = 4.dp)
                    .fillMaxHeight(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(Res.drawable.add_2_24px),
                    contentDescription = "Add photo",
                    colorFilter = ColorFilter.tint(color = white_color),
                    modifier = Modifier.size(12.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Add photo",
                    color = white_color,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                )
            }
        }
    }
}

@Composable
private fun TrendingSection(rooms: List<RoomUi>, onRoomClick: (RoomUi) -> Unit) {
    Column {
        Text(
            text = "Trending",
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold,
            color = black_color,
            modifier = Modifier.padding(start = 24.dp)
        )
        Spacer(modifier = Modifier.height(8.dp))
        AnimatedContent(
            targetState = rooms.isEmpty(),
            transitionSpec = { fadeIn() togetherWith fadeOut() }
        ) { state ->
            if (state) {
                TrendingGridShimmer()
            } else {
                TrendingGrid(rooms = rooms, onRoomClick = onRoomClick)
            }
        }
    }
}

@Composable
private fun TrendingGrid(rooms: List<RoomUi>, onRoomClick: (RoomUi) -> Unit) {
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(start = 24.dp, end = 24.dp),
        modifier = Modifier.then(
            if (rooms.size > 1) Modifier.height(260.dp)
            else Modifier.wrapContentHeight()
        )
    ) {
        items(rooms.chunked(2)) { columnItems ->
            val columnIndex = rooms.chunked(2).indexOf(columnItems)
            val isAlternate = columnIndex % 2 == 1
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                columnItems.forEachIndexed { index, room ->
                    val height = when {
                        isAlternate && index == 1 -> 95.dp
                        isAlternate && index == 0 -> 156.dp
                        else -> 126.dp
                    }
                    RoomCategoryCard(room = room, height = height, onClick = { onRoomClick(room) })
                }
            }
        }
    }
}

@Composable
private fun RoomCategoryCard(room: RoomUi, height: androidx.compose.ui.unit.Dp, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .width(126.dp)
            .height(height)
            .clip(RoundedCornerShape(8.782.dp))
            .background(Color(0xFFE8E8E8))
            .clickable { onClick() }
    ) {
        AsyncImage(
            model = ImageRequest.Builder(LocalPlatformContext.current)
                .data(room.imageUrl)
                .build(),
            onError = { error ->
                println("❌ Image Error: ${error.result.throwable}")
            },
            onSuccess = {
                println("✅ Image Loaded: ${room.imageUrl}")
            },
            placeholder = painterResource(Res.drawable.roomplaceholder),
            error = painterResource(Res.drawable.roomplaceholder),
            contentDescription = room.roomType,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp)
                .align(Alignment.BottomCenter)
                .background(
                    Brush.verticalGradient(
                        0.0f to Color.Transparent,
                        0.3f to Color.Black.copy(alpha = 0.1f),
                        0.6f to Color.Black.copy(alpha = 0.4f),
                        1.0f to Color.Black.copy(alpha = 0.6f)
                    )
                )
        )
        Text(
            text = room.roomType,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            modifier = Modifier.align(Alignment.BottomStart).padding(8.dp)
        )
    }
}

@Composable
private fun RecentFilesSection(
    generatedBundles: List<List<RecentGeneratedEntity>>,  // ✅ Correct type
    onBundleClick: (List<RecentGeneratedEntity>) -> Unit,  // ✅ Correct type
    onSeeAllClick: () -> Unit
) {
    Column(modifier = Modifier.padding(bottom = 30.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Recent Files",
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                color = black_color
            )
            if (generatedBundles.isNotEmpty()) {
                Text(
                    text = "See all",
                    fontSize = 13.sp,
                    color = Color.Gray,
                    modifier = Modifier.clickable { onSeeAllClick() }
                )
            }
        }
        Spacer(modifier = Modifier.height(12.dp))
        RecentFilesRow(
            generatedBundles = generatedBundles,  // ✅ Matches parameter name
            onBundleClick = onBundleClick          // ✅ Matches parameter name
        )
    }
}

@Composable
private fun RecentFilesRow(
    generatedBundles: List<List<RecentGeneratedEntity>>,  // ✅ Data parameter
    onBundleClick: (List<RecentGeneratedEntity>) -> Unit  // ✅ Callback parameter
) {
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(horizontal = 24.dp),
    ) {
        if (generatedBundles.isNotEmpty()) {
            items(generatedBundles) { bundle ->
                Box(
                    modifier = Modifier
                        .size(114.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFFE8E8E8))
                        .clickable { onBundleClick(bundle) }
                ) {
                    if (bundle.isNotEmpty()) {
                        val firstImage = bundle[0]
                        AsyncImage(
                            model = ImageRequest.Builder(LocalPlatformContext.current)
                                .data(getImageModel(firstImage.localPath) ?: firstImage.imageUrl)
                                .crossfade(true)
                                .build(),
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop,
                            placeholder = painterResource(Res.drawable.roomplaceholder),
                            error = painterResource(Res.drawable.roomplaceholder)
                        )
                    }
                }
            }
        } else {
            items(3) {
                Box(
                    modifier = Modifier
                        .size(114.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFFF5F5F5))
                        .shimmerLoading()
                )
            }
        }
    }
}

@Composable
private fun TrendingGridShimmer() {
    val dummyItems = List(12) { it }
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(start = 24.dp, end = 24.dp),
        modifier = Modifier.height(260.dp)
    ) {
        items(dummyItems.chunked(2)) { columnItems ->
            val columnIndex = dummyItems.chunked(2).indexOf(columnItems)
            val isAlternate = columnIndex % 2 == 1
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                columnItems.forEachIndexed { index, _ ->
                    val height = when {
                        isAlternate && index == 1 -> 95.dp
                        isAlternate && index == 0 -> 156.dp
                        else -> 126.dp
                    }
                    Box(
                        modifier = Modifier
                            .width(126.dp)
                            .height(height)
                            .clip(RoundedCornerShape(8.782.dp))
                            .background(Color(0xFFE8E8E8))
                            .shimmerLoading()
                    )
                }
            }
        }
    }
}

@Composable
fun Modifier.shimmerLoading(durationMillis: Int = 1000): Modifier {
    val transition = rememberInfiniteTransition(label = "")
    val translateAnimation by transition.animateFloat(
        initialValue = 0f,
        targetValue = 500f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = durationMillis, easing = LinearEasing),
            repeatMode = RepeatMode.Restart,
        ),
        label = "",
    )
    return drawBehind {
        drawRect(
            brush = Brush.linearGradient(
                colors = listOf(
                    Color.LightGray.copy(alpha = 0.2f),
                    Color.LightGray.copy(alpha = 1.0f),
                    Color.LightGray.copy(alpha = 0.2f),
                ),
                start = Offset(x = translateAnimation, y = translateAnimation),
                end = Offset(x = translateAnimation + 100f, y = translateAnimation + 100f),
            )
        )
    }
}