package org.yourappdev.homeinterior.ui.Generate.UiScreens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridItemSpan
import androidx.compose.foundation.lazy.staggeredgrid.itemsIndexed
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import org.yourappdev.homeinterior.data.local.entities.RecentGeneratedEntity
import org.yourappdev.homeinterior.ui.CreateAndExplore.Create.shimmerLoading
import org.yourappdev.homeinterior.utils.getImageModel

@Composable
fun ResultScreen(
    generatedImages: List<RecentGeneratedEntity>,
    generatedImageUrls: List<String> = emptyList(),
    onCloseClick: () -> Unit = {},
    isFetchingImages: Boolean = false,
    etaSeconds: Int = 0,
    generatedCount: Int = 1,
    onImageClick: (Int) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
        ) {
            TopBar { onCloseClick() }

            if (isFetchingImages && generatedImages.isEmpty()) {
                // Koi image nahi aayi — sab shimmer dikhao
                ShimmerResultGrid(imageCount = generatedCount)
            } else {
                // Jo images aa gayi + baaki shimmer
                MixedResultGrid(
                    imageList = generatedImages,
                    totalCount = generatedCount,
                    isFetchingImages = isFetchingImages,
                    onImageClick = onImageClick
                )
            }
        }
    }
}

@Composable
private fun MixedResultGrid(
    imageList: List<RecentGeneratedEntity>,
    totalCount: Int,
    isFetchingImages: Boolean,
    onImageClick: (Int) -> Unit
) {
    LazyVerticalStaggeredGrid(
        columns = StaggeredGridCells.Fixed(2),
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp),
        contentPadding = PaddingValues(vertical = 24.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalItemSpacing = 16.dp
    ) {
        // Aayi hui images dikhao
        itemsIndexed(
            items = imageList,
            key = { index, _ -> "image_$index" },
            span = { index, _ ->
                if ((index + 1) % 3 == 0) StaggeredGridItemSpan.FullLine
                else StaggeredGridItemSpan.SingleLane
            }
        ) { index, entity ->
            println("🔵 ENTITY[$index]: localPath=${entity.localPath}, imageUrl=${entity.imageUrl}")
            val imageModel = getImageModel(entity.localPath) ?: entity.imageUrl.ifBlank { null }
            ImageCard(
                imageUrl = imageModel,
                isLarge = (index + 1) % 3 == 0,
                modifier = Modifier.clickable { onImageClick(index) }
            )
        }

        // Baaki ke shimmer boxes dikhao
        if (isFetchingImages) {
            val remainingCount = totalCount - imageList.size
            items(remainingCount, key = { "shimmer_$it" }) { index ->
                val globalIndex = imageList.size + index
                Box(
                    modifier = Modifier
                        .then(
                            if ((globalIndex + 1) % 3 == 0)
                                Modifier.fillMaxWidth().height(176.dp)
                            else
                                Modifier.aspectRatio(1f)
                        )
                        .clip(RoundedCornerShape(9.dp))
                        .background(Color(0xFFE8E8E8))
                        .shimmerLoading()
                )
            }
        }
    }
}

@Composable
private fun ShimmerResultGrid(imageCount: Int = 1) {
    LazyVerticalStaggeredGrid(
        columns = StaggeredGridCells.Fixed(2),
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp),
        contentPadding = PaddingValues(vertical = 24.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalItemSpacing = 16.dp
    ) {
        items(
            count = imageCount,
            span = { index ->
                if ((index + 1) % 3 == 0) StaggeredGridItemSpan.FullLine
                else StaggeredGridItemSpan.SingleLane
            }
        ) { index ->
            Box(
                modifier = Modifier
                    .then(
                        if ((index + 1) % 3 == 0)
                            Modifier.fillMaxWidth().height(176.dp)
                        else
                            Modifier.aspectRatio(1f)
                    )
                    .clip(RoundedCornerShape(9.dp))
                    .background(Color(0xFFE8E8E8))
                    .shimmerLoading()
            )
        }
    }
}

@Composable
private fun ImageCard(
    imageUrl: Any?,
    isLarge: Boolean,
    modifier: Modifier = Modifier
) {
    AsyncImage(
        model = imageUrl,
        contentDescription = "Room design",
        onError = { error ->
            println("❌ IMAGE_ERROR: ${error.result.throwable.message}")
            println("❌ IMAGE_URL: $imageUrl")
        },
        onSuccess = {
            println("✅ IMAGE_LOADED: $imageUrl")
        },
        modifier = modifier
            .then(
                if (isLarge) Modifier.fillMaxWidth().height(176.dp)
                else Modifier.aspectRatio(1f)
            )
            .clip(RoundedCornerShape(9.dp))
            .border(
                width = 1.dp,
                color = Color(0xFFCFCFCF),
                shape = RoundedCornerShape(9.dp)
            ),
        contentScale = ContentScale.Crop
    )
}
