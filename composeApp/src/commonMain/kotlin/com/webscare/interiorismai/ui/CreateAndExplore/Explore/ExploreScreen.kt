package com.webscare.interiorismai.ui.CreateAndExplore.Explore

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import homeinterior.composeapp.generated.resources.Res
import homeinterior.composeapp.generated.resources.filter
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.DrawableResource
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.Dp
import coil3.compose.AsyncImage
import coil3.compose.LocalPlatformContext
import coil3.request.ImageRequest
import coil3.request.crossfade
import homeinterior.composeapp.generated.resources.roomplaceholder
import org.koin.compose.viewmodel.koinViewModel
import com.webscare.interiorismai.data.remote.util.ResultState
import com.webscare.interiorismai.domain.model.RoomUi
import com.webscare.interiorismai.ui.CreateAndExplore.Create.shimmerLoading
import com.webscare.interiorismai.ui.CreateAndExplore.RoomEvent
import com.webscare.interiorismai.ui.CreateAndExplore.RoomsViewModel
import com.webscare.interiorismai.ui.theme.fieldBack


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExploreScreen(viewModel: RoomsViewModel = koinViewModel(), onRoomClick: (RoomUi) -> Unit = {}) {
    val state by viewModel.state.collectAsState()
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Sticky Header
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .statusBarsPadding()
            ) {
                Text(
                    text = "Explore",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(start = 24.dp, bottom = 16.dp)
                )

                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    modifier = Modifier.padding(start = 24.dp, end = 24.dp, bottom = 16.dp)
                ) {
                    BasicTextField(
                        value = state.searchQuery,
                        onValueChange = {
                            viewModel.onRoomEvent(RoomEvent.OnSearchQueryChange(it))
                        },
                        textStyle = TextStyle(
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Normal,
                            letterSpacing = 0.16.sp,
                            color = Color.Black
                        ),
                        singleLine = true,
                        modifier = Modifier
                            .height(50.dp)
                            .weight(1f)
                            .background(fieldBack, RoundedCornerShape(10.dp))
                    ) { innerTextField ->
                        TextFieldDefaults.DecorationBox(
                            value = state.searchQuery,
                            innerTextField = innerTextField,
                            placeholder = { Text("Search here...") },
                            singleLine = true,
                            enabled = true,
                            interactionSource = remember { MutableInteractionSource() },
                            colors = TextFieldDefaults.colors(
                                unfocusedContainerColor = Color.Transparent,
                                focusedContainerColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent,
                                focusedIndicatorColor = Color.Transparent
                            ),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 0.dp),
                            visualTransformation = VisualTransformation.None
                        )
                    }

                    Box(modifier = Modifier.height(50.dp).aspectRatio(1f)) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(fieldBack, RoundedCornerShape(10.dp))
                                .clip(RoundedCornerShape(10.dp))
                                .clickable(enabled = true, onClick = {
                                    viewModel.onRoomEvent(RoomEvent.OnFilterClick)
                                }),
                            contentAlignment = Alignment.Center
                        ) {
                            Image(
                                painter = painterResource(Res.drawable.filter),
                                contentDescription = null
                            )
                        }

                        if (state.filterCount > 0) {
                            Box(
                                modifier = Modifier
                                    .size(18.dp)
                                    .align(Alignment.TopEnd)
                                    .offset(x = 4.dp, y = (-4).dp)
                                    .background(Color(0xFFA3B18A), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = state.filterCount.toString(),
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    lineHeight = 10.sp,
                                )
                            }
                        }
                    }
                }
            }

            // Vertical Staggered Grid
            AnimatedContent(
                targetState = state.getRoomsResponse to state.filteredRooms.isEmpty(),
                transitionSpec = {
                    fadeIn() togetherWith fadeOut()
                }
            ) { (loading, isEmpty) ->
                when {
                    loading is ResultState.Loading -> ExploreGridShimmer()
                    isEmpty -> EmptyRoomsMessage()
                    else -> ExploreGrid(
                        rooms = state.filteredRooms,
                        onRoomClick = onRoomClick
                    )
                }
            }
        }

        if (state.showFilterSheet) {
            ModalBottomSheet(
                onDismissRequest = {
                    viewModel.onRoomEvent(RoomEvent.OnDismissFilterSheet)
                },
                sheetState = sheetState,
                containerColor = Color.Transparent,
                dragHandle = null,
                modifier = Modifier.statusBarsPadding()
            ) {
                FilterBottomSheetContent(
                    filterState = state.tempFilterState,
                    filterCount = state.tempFilterCount,
                    expandedRoomType = state.expandedRoomType,
                    expandedStyle = state.expandedStyle,
                    expandedColor = state.expandedColor,
                    expandedFormat = state.expandedFormat,
                    expandedPrice = state.expandedPrice,
                    onFilterStateChange = {
                        viewModel.onRoomEvent(RoomEvent.OnTempFilterChange(it))
                    },
                    onToggleSection = { section ->
                        viewModel.onRoomEvent(RoomEvent.OnToggleFilterSection(section))
                    },
                    onApplyFilters = {
                        viewModel.onRoomEvent(RoomEvent.OnApplyFilters)
                    },
                    onCancel = {
                        viewModel.onRoomEvent(RoomEvent.OnDismissFilterSheet)
                    },
                    onClearAll = {
                        viewModel.onRoomEvent(RoomEvent.OnClearFilters)
                    },
                    availableRoomTypes = state.availableRoomTypes,
                    availableStyles = state.availableStylesString,
                    availableColors = state.availableColors
                )
            }
        }
    }
}

@Composable
private fun EmptyRoomsMessage() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "No rooms found",
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            color = Color.Gray
        )
    }
}


@Composable
private fun ExploreGrid(
    rooms: List<RoomUi>,
    onRoomClick: (RoomUi) -> Unit
) {
    LazyVerticalStaggeredGrid(
        columns = StaggeredGridCells.Fixed(2),
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 24.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalItemSpacing = 12.dp
    ) {
        items(rooms) { room ->
            RoomImageCard(
                room = room,
                onClick = { onRoomClick(room) }
            )
        }
    }
}

@Composable
private fun RoomImageCard(
    room: RoomUi,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(room.cardHeight.dp)
            .clip(RoundedCornerShape(12.dp))
            .clickable { onClick() }
    ) {
        AsyncImage(
            model = ImageRequest.Builder(LocalPlatformContext.current)
                .data(room.imageUrl)
                .crossfade(true)
                .build(),
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
                        1.0f to Color.Black.copy(alpha = 0.6f)
                    )
                )
        )

        Text(
            text = room.roomType,
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color.White,
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(12.dp)
        )

        if (room.colors.isNotEmpty()) {
            OverlappingColorRow(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(top = 8.dp, end = 8.dp),
                colors = room.colors
            )
        }
    }
}

@Composable
fun ImageCard(imageRes: DrawableResource, height: Dp, colors: List<Color>) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(height)
            .clip(RoundedCornerShape(12.dp))
    ) {
        Image(
            painter = painterResource(imageRes),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        OverlappingColorRow(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(bottom = 8.dp, start = 8.dp),
            colors = colors
        )
    }
}

@Composable
private fun ExploreGridShimmer() {
    val dummyItems = List(12) { it }

    LazyVerticalStaggeredGrid(
        columns = StaggeredGridCells.Fixed(2),
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalItemSpacing = 12.dp
    ) {
        items(dummyItems) { index ->
            val height = remember(index) {
                listOf(150, 180, 210, 240, 280).random().dp
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(height)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFFE8E8E8))
                    .shimmerLoading()
            )
        }
    }
}

@Composable
fun OverlappingColorRow(modifier: Modifier = Modifier, colors: List<Color>) {
    Box(
        modifier = modifier
            .size(
                width = (20.dp * colors.size) - (7.5.dp * (colors.size - 1)),
                height = 20.dp
            )
    ) {
        colors.forEachIndexed { index, color ->
            Box(
                modifier = Modifier
                    .offset(x = (index * 12.5).dp)
                    .size(20.dp)
                    .background(color, shape = CircleShape)
                    .border(1.dp, Color.White, CircleShape)
            )
        }
    }
}