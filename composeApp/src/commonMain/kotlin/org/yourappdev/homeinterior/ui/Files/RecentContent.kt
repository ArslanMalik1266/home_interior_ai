package org.yourappdev.homeinterior.ui.Files

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.Image
import coil3.compose.AsyncImage
import homeinterior.composeapp.generated.resources.Res
import homeinterior.composeapp.generated.resources.emptyimage
import homeinterior.composeapp.generated.resources.roomplaceholder
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import org.yourappdev.homeinterior.data.local.entities.RecentGeneratedEntity

@Composable
fun RecentContent(
    generatedImages: List<RecentGeneratedEntity>,
    onBundleClick: (List<RecentGeneratedEntity>) -> Unit,
    onImageClick: (RecentGeneratedEntity) -> Unit
) {
    val bundles = generatedImages.chunked(1)
    if (bundles.isEmpty()) {
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
            items(bundles) { bundle ->
                println("DEBUG: Bundle received = $bundle")
                val urlList = bundle.map { it.imageUrl }
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(145.dp) // Drafts jaisa size
                        .clip(RoundedCornerShape(11.dp))
                        .background(Color(0xFFF5F5F5))
                        .clickable {
                            onImageClick(bundle[0])
                        }
                ) {
                    if (bundle.isNotEmpty()) {
                        println("DEBUG: Loading image = ${bundle[0]}")
                        AsyncImage(
                            model = bundle[0].localPath ?: bundle[0].imageUrl,
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
                                text = "${bundle.size} Pics",
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
}
