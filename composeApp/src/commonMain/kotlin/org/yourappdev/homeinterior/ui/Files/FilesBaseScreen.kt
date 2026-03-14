package org.yourappdev.homeinterior.ui.Files


import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import homeinterior.composeapp.generated.resources.Res
import homeinterior.composeapp.generated.resources.sofa
import homeinterior.composeapp.generated.resources.sofa_2
import homeinterior.composeapp.generated.resources.sofa_3
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.viewmodel.koinViewModel
import org.yourappdev.homeinterior.navigation.Routes
import org.yourappdev.homeinterior.ui.CreateAndExplore.RoomEvent
import org.yourappdev.homeinterior.ui.CreateAndExplore.RoomsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilesScreen(
    viewModel: RoomsViewModel = koinViewModel(),
    navController: androidx.navigation.NavController,
    onImageClick: () -> Unit,
    onShowResults: () -> Unit
) {
    val state by viewModel.state.collectAsState()

    val tabs = listOf("Recent", "Drafts")
    val pagerState = rememberPagerState(pageCount = { tabs.size })
    var selectedTabIndex = remember {
        derivedStateOf {
            pagerState.currentPage
        }
    }
    val scope = rememberCoroutineScope()
    Scaffold(
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(start = 24.dp, end = 24.dp)
                    .statusBarsPadding()
            ) {

                Text(
                    text = "Files",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF2C2C2C)
                )

                Spacer(modifier = Modifier.height(24.dp))

                TabRow(
                    selectedTabIndex = selectedTabIndex.value,
                    containerColor = Color.White,
                    contentColor = Color(0xFF2C2C2C),
                    indicator = { tabPositions ->
                        Box(
                            modifier = Modifier
                                .tabIndicatorOffset(tabPositions[selectedTabIndex.value])
                                .height(2.dp)
                                .background(
                                    color = Color(0xFF99AD76),
                                    shape = RoundedCornerShape(4.dp)
                                )
                        )
                    },
                    divider = {
                        HorizontalDivider(
                            thickness = 2.dp,
                            color = Color(0xFFE6E4E4)
                        )
                    }
                ) {
                    tabs.forEachIndexed { index, title ->
                        Tab(
                            selected = selectedTabIndex.value == index,
                            onClick = {
                                scope.launch {
                                    pagerState.animateScrollToPage(index)
                                }
                            },
                            text = {
                                Text(
                                    text = title,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = if (selectedTabIndex.value == index) Color(0xFF2C2C2C) else Color(
                                        0xFF959595
                                    )
                                )
                            }
                        )
                    }
                }
            }
        },
        containerColor = Color.White
    ) { paddingValues ->
        HorizontalPager(
            state = pagerState,

            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) { page ->
            when (page) {

                0 -> {
                    // 1. Database se images collect karein
                    val dbImages by viewModel.dbGeneratedImages.collectAsState()

                    // 2. RecentContent ko pass karein
                    RecentContent(
                        generatedImages = dbImages,
                        onBundleClick = { bundleUrls ->
                            viewModel.onRoomEvent(RoomEvent.ShowSelectedBundle(bundleUrls))
                            onShowResults()
                        }
                    )
                }

                1 -> DraftsContent(
                    viewModel = viewModel,
                    onImageClick = { clickedDraft ->
                        viewModel.selectDraftImage(clickedDraft)
                        navController.navigate(Routes.AddScreen)
                    }
                )
            }
        }
    }
}




