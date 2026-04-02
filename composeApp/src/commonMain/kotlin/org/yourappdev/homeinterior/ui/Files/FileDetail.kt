package org.yourappdev.homeinterior.ui.Files

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import homeinterior.composeapp.generated.resources.Res
import homeinterior.composeapp.generated.resources.close
import homeinterior.composeapp.generated.resources.compare
import homeinterior.composeapp.generated.resources.deletenew
import homeinterior.composeapp.generated.resources.redo
import homeinterior.composeapp.generated.resources.save
import homeinterior.composeapp.generated.resources.share
import homeinterior.composeapp.generated.resources.sofa
import homeinterior.composeapp.generated.resources.sofa_3
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import org.yourappdev.homeinterior.data.local.entities.RecentGeneratedEntity
import org.yourappdev.homeinterior.ui.CreateAndExplore.RoomsViewModel
import org.yourappdev.homeinterior.ui.UiUtils.CloseIconButton
import org.yourappdev.homeinterior.ui.UiUtils.DeleteConfirmationDialog
import org.yourappdev.homeinterior.utils.getImageModel
import org.yourappdev.homeinterior.utils.saveImageToGallery
import org.yourappdev.homeinterior.utils.shareImage
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalMaterial3Api::class, ExperimentalTime::class)
@Composable
fun CreateEditScreen( imageUrl: ByteArray = byteArrayOf(),
                      imageUrlString: String = "",
                      onClick: () -> Unit,
                      onRedo: () -> Unit = {},
                      viewModel: RoomsViewModel? = null,
                      entity: RecentGeneratedEntity? = null,
                      selectedIndex: Int = 0,
                      isTrending: Boolean = false ) {
    val scope = rememberCoroutineScope()

    var saveSuccess by remember { mutableStateOf<Boolean?>(null) }
    LaunchedEffect(saveSuccess) {
        if (saveSuccess != null) {
            kotlinx.coroutines.delay(2000)
            saveSuccess = null
        }
    }
    val backgroundColor = Color(0xFFFFFFFF)
    val darkText = Color(0xFF2C2C2C)
    val grayText = Color(0xFF9B9B9B)
    val buttonBackground = Color(0xFFF9F9F9)
    val borderColor = Color(0xFFA3B18A)
    val imageBorder = Color(0xFFEAEAEA)

    val bottomSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var showDelete by remember {
        mutableStateOf(false)
    }
    val gradientColors = listOf(
        Color(0xFFC5EBB2),
        Color(0xFFDFF2C2),
        Color(0xFFC1DFB5),
        Color(0xFFD2F7BD)
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
        )
        {
            TopBar(
                darkText = darkText,
                gradientColors = gradientColors
            ) {
                onClick()
            }

            Spacer(modifier = Modifier.height(24.dp))

            ImageSection(
                imageUrl = imageUrl,
                imageUrlString = imageUrlString,
                isTrending = isTrending,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .then(
                        if (isTrending) Modifier.weight(1f) else Modifier
                    ),
                imageBorder = imageBorder
            )

            Spacer(modifier = Modifier.height(24.dp))

            if (!isTrending) {
                Spacer(modifier = Modifier.height(24.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                        ActionButton(
                            icon = Res.drawable.redo,
                            label = "Redo",
                            backgroundColor = buttonBackground,
                            textColor = darkText,
                            iconTint = grayText,
                            modifier = Modifier.weight(1f)
                        ) {
                            if (viewModel != null && entity != null) {
                                scope.launch {
                                    viewModel.redoGeneration(
                                        entity = entity,
                                        indexToReplace = selectedIndex,
                                        onResult = { onRedo() })
                                }
                            }
                        }

                        ActionButton(
                            icon = Res.drawable.save,
                            label = "Save",
                            backgroundColor = buttonBackground,
                            textColor = darkText,
                            iconTint = grayText,
                            modifier = Modifier.weight(1f)
                        ) {
                            scope.launch {
                                val success = saveImageToGallery(
                                    imageBytes = if (imageUrl.isNotEmpty()) imageUrl else null,
                                    imageUrl = if (imageUrlString.isNotEmpty()) imageUrlString else null,
                                    fileName = "interior_${
                                        Clock.System.now().toEpochMilliseconds()
                                    }.jpg"
                                )
                                saveSuccess = success
                                println(if (success) "✅ Saved!" else "❌ Save failed")
                            }

                        }


                        ActionButton(
                            icon = Res.drawable.deletenew,
                            label = "Delete",
                            backgroundColor = buttonBackground,
                            textColor = darkText,
                            iconTint = grayText,
                            modifier = Modifier.weight(1f)
                        ) {
                            showDelete = true
                        }

                        ActionButton(
                            icon = Res.drawable.share,
                            label = "Share",
                            backgroundColor = Color.White,
                            textColor = darkText,
                            iconTint = grayText,
                            hasBorder = true,
                            borderColor = borderColor,
                            modifier = Modifier.weight(1f)
                        ) {
                            scope.launch {
                                shareImage(
                                    imageBytes = if (imageUrl.isNotEmpty()) imageUrl else null,
                                    imageUrl = if (imageUrlString.isNotEmpty()) imageUrlString else null,
                                    fileName = "interior_design.jpg"
                                )
                            }

                        }


                }
            }

        }
        saveSuccess?.let { success ->
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .padding(horizontal = 24.dp, vertical = 8.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(
                        if (success) Color(0xFF4CAF50) else Color(0xFFDC3545)
                    )
                    .padding(12.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (success) "✅ Image saved to gallery!" else "❌ Save failed!",
                    color = Color.White,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
        if (showDelete) {
            ModalBottomSheet(
                onDismissRequest = { showDelete = false },
                sheetState = bottomSheetState,
                containerColor = Color.White, // Use White for visibility while testing
                dragHandle = { BottomSheetDefaults.DragHandle() }, // Optional: adding drag handle
                modifier = Modifier.statusBarsPadding()
            ) {
                DeleteConfirmationDialog(
                    title = "Are you sure you want to delete this image?",
                    onConfirm = {
                        println("🟡 PROCESS: Starting Delete for ID: ${entity?.id}")
                        showDelete = false // Foran sheet band karein

                        scope.launch {
                            try {
                                // Null safety check
                                if (viewModel != null && entity != null) {
                                    viewModel.deleteImageFromBundle(
                                        entity = entity,
                                        imageIndex = selectedIndex,
                                        onDeleted = { onClick() }
                                    )
                                } else {
                                    println("❌ ERROR: ViewModel or Entity is NULL")
                                }
                            } catch (e: Exception) {
                                println("❌ ERROR: Coroutine Failed: ${e.message}")
                            }
                        }
                    },
                    onCancel = {
                        println("🔵 PROCESS: User cancelled delete")
                        showDelete = false
                    }
                )
            }
        }
    }
}

@Composable
fun TopBar(
    darkText: Color,
    gradientColors: List<Color>,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start,
            modifier = Modifier.weight(1f)
        ) {
            CloseIconButton(iconSize = 20.dp) {
                onClick()
            }
            Spacer(modifier = Modifier.width(8.dp))

            Text(
                text = "Create",
                fontSize = 20.sp,
                fontWeight = FontWeight.SemiBold,
                color = darkText
            )
        }

        ProBadge(gradientColors)
    }
}

@Composable
fun ProBadge(gradientColors: List<Color>) {
    Box(
        modifier = Modifier
            .height(32.dp)
            .clip(RoundedCornerShape(21.dp))
            .background(
                brush = Brush.linearGradient(
                    colors = gradientColors
                )
            )
            .padding(horizontal = 17.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "PRO",
            fontSize = 15.sp,
            fontWeight = FontWeight.ExtraBold,
            color = Color.Black
        )
    }
}

@Composable
fun ImageSection(
    imageUrl: ByteArray = byteArrayOf(),
    imageUrlString: String = "",
    modifier: Modifier = Modifier,
    imageBorder: Color,
    isTrending: Boolean = false
) {
    LaunchedEffect(Unit) {
        println("🟢 ImageSection - imageUrl bytes: ${imageUrl.size}")
        println("🟢 ImageSection - imageUrlString: '$imageUrlString'")
        println("🟢 ImageSection - model: ${if (imageUrl.isNotEmpty()) "ByteArray" else getImageModel(imageUrlString) ?: imageUrlString}")

    }
    var image by remember { mutableStateOf(Res.drawable.sofa_3) }
    var isPressed by remember { mutableStateOf(false) }
    Box(
        modifier = modifier
            .then(if (!isTrending) Modifier.aspectRatio(392f / 620f) else Modifier)
            .clip(RoundedCornerShape(9.dp))
            .border(1.dp, imageBorder, RoundedCornerShape(9.dp))
            .background(Color(0xFFF5F5F5))
    ) {
        AsyncImage(
            model = if (imageUrl.isNotEmpty()) imageUrl
            else if (imageUrlString.isNotEmpty()) getImageModel(imageUrlString) ?: imageUrlString
            else null,
            contentDescription = "Editing Image",
            contentScale = ContentScale.Fit,
            modifier = Modifier.fillMaxSize()
        )

        Box(
            modifier = Modifier
                .size(42.dp)
                .align(Alignment.BottomStart)
                .offset(x = 16.dp, y = (-16).dp)
                .clip(RoundedCornerShape(41.dp))
                .background(Color(0xFFFCFCFC).copy(alpha = 0.9f))
                .padding(7.dp)
                .pointerInput(Unit) {
                    detectTapGestures(
                        onPress = {
                            isPressed = true
                            tryAwaitRelease()
                            isPressed = false
                        }
                    )
                },
            contentAlignment = Alignment.Center
        ) {
            if (!isTrending) {
                Icon(
                    painter = painterResource(Res.drawable.compare),
                    contentDescription = "Layers",
                    tint = Color(0xFFADAAAA),
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}


@Composable
fun ActionButton(
    icon: DrawableResource,
    label: String,
    backgroundColor: Color,
    textColor: Color,
    iconTint: Color,
    modifier: Modifier = Modifier,
    hasBorder: Boolean = false,
    borderColor: Color = Color.Transparent,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .height(72.dp)
            .shadow(
                elevation = if (hasBorder) 6.dp else 0.dp,
                shape = RoundedCornerShape(8.dp),
                ambientColor = Color.Black.copy(alpha = 0.23f),
                spotColor = Color.Black.copy(alpha = 0.23f)
            )
            .clip(RoundedCornerShape(8.dp))
            .background(backgroundColor)
            .then(
                if (hasBorder) {
                    Modifier.border(1.dp, borderColor, RoundedCornerShape(8.dp))
                } else {
                    Modifier
                }
            ).clickable(enabled = true, onClick = {
                onClick()
            })
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(horizontal = 10.dp, vertical = 7.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Image(
                painter = painterResource(icon),
                contentDescription = label,
                colorFilter = ColorFilter.tint(color = iconTint),
                modifier = Modifier.size(24.dp)
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = label,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                color = textColor
            )
        }
    }

}

