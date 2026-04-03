package com.webscare.interiorismai.ui.Files

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import homeinterior.composeapp.generated.resources.Res
import homeinterior.composeapp.generated.resources.emptyimage
import homeinterior.composeapp.generated.resources.roomplaceholder
import org.jetbrains.compose.resources.painterResource
import com.webscare.interiorismai.data.local.entities.RecentGeneratedEntity
import com.webscare.interiorismai.ui.CreateAndExplore.RoomUiState
import com.webscare.interiorismai.utils.getImageModel

@Composable
fun RecentContent(
    state: RoomUiState,
    generatedBundles: List<RecentGeneratedEntity>,
    isFetching: Boolean = false,
    tasksProgress: Map<String, Float> = emptyMap(),
    onBundleClick: (RecentGeneratedEntity) -> Unit
) {
    if (generatedBundles.isEmpty() && !isFetching) {
        // Empty State
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(Res.drawable.emptyimage),
                contentDescription = null,
                modifier = Modifier.size(140.dp)
            )
        }
    } else {
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            contentPadding = PaddingValues(horizontal = 24.dp, vertical = 24.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            // RecentContent.kt ke andar loading item ka design:
            if (isFetching) {
                items(state.activeTasksCount) { index ->
                    // Har loader apni progress map se uthayega
                    val taskList = tasksProgress.values.toList()
                    val currentProgress = taskList.getOrNull(index) ?: 0f

                    val animatedProgress by animateFloatAsState(
                        targetValue = currentProgress,
                        animationSpec = tween(900)
                    )

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(145.dp)
                            .clip(RoundedCornerShape(11.dp))
                            .background(Color(0xFFE8E8E8)),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            CircularProgressIndicator(
                                progress = { animatedProgress },
                                color = Color(0xFF222222),
                                modifier = Modifier.size(40.dp)
                            )
                            Text(text = "${(animatedProgress * 100).toInt()}%")
                        }
                    }
                }
            }
            items(generatedBundles) { bundle ->
                println("DEBUG: Bundle received = $bundle")
                val coverImage = bundle.localPaths.firstOrNull() ?: bundle.imageUrls.firstOrNull()
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(145.dp) // Drafts jaisa size
                        .clip(RoundedCornerShape(11.dp))
                        .background(Color(0xFFF5F5F5))
                        .clickable {
                            onBundleClick(bundle)
                        }
                ) {
                        AsyncImage(
                            model = getImageModel(coverImage ?: "") ?: coverImage,
                            contentDescription = "Generated Interior",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop,
                            placeholder = painterResource(Res.drawable.roomplaceholder),
                            error = painterResource(Res.drawable.roomplaceholder)
                        )

                        Surface(
                            color = Color.Black.copy(alpha = 0.6f),
                            shape = RoundedCornerShape(4.dp),
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .padding(8.dp)
                        ) {
                            Text(
                                text = "${bundle.localPaths.size} Pics",
                                color = Color.White,
                                fontSize = 10.sp,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }

                }
            }
        }
    }
}
