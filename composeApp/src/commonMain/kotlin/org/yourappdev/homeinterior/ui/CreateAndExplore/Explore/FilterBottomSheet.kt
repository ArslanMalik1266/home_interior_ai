package org.yourappdev.homeinterior.ui.CreateAndExplore.Explore

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import homeinterior.composeapp.generated.resources.Res
import homeinterior.composeapp.generated.resources.keyboard_arrow_down_24px
import homeinterior.composeapp.generated.resources.keyboard_arrow_up_24px
import org.jetbrains.compose.resources.painterResource
import org.yourappdev.homeinterior.ui.CreateAndExplore.FilterSection
import org.yourappdev.homeinterior.ui.CreateAndExplore.FilterState
import org.yourappdev.homeinterior.ui.Generate.UiScreens.ColorPalette

@Composable
fun FilterBottomSheetContent(
    filterState: FilterState,
    filterCount: Int,
    expandedRoomType: Boolean,
    expandedStyle: Boolean,
    expandedColor: Boolean,
    expandedFormat: Boolean,
    expandedPrice: Boolean,
    availableRoomTypes: List<String>,
    availableStyles: List<String>,
    availableColors: List<ColorPalette>,
    onFilterStateChange: (FilterState) -> Unit,
    onToggleSection: (FilterSection) -> Unit,
    onApplyFilters: () -> Unit,
    onCancel: () -> Unit,
    onClearAll: () -> Unit
) {
    val primaryGreen = Color(0xFFA3B18A)
    val darkText = Color(0xFF2C2C2C)
    val mediumText = Color(0xFF323232)
    val lightText = Color(0xFF4D4D4D)
    val borderGray = Color(0xFF7D7A7A)
    val dividerGray = Color(0xFFBBBBBB)
    val cancelTextColor = Color(0xFF8C8989)
    val cancelBorderColor = Color(0xFFE1DDDD)
    val whiteText = Color(0xFFFEF7F7)

    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(horizontal = 21.dp),
    ) {
        stickyHeader {
            Row(
                modifier = Modifier.fillMaxWidth().background(Color.White)
                    .padding(horizontal = 10.dp, vertical = 20.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Filters",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = darkText
                    )
                    Text(
                        text = " ($filterCount)",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = primaryGreen
                    )
                }
                Text(
                    text = "Clear all",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Normal,
                    color = primaryGreen,
                    modifier = Modifier.clickable { onClearAll() }
                )
            }
        }

        item {
            ExpandableFilterSection(
                title = "Type of Room",
                expanded = expandedRoomType,

                onExpandChange = { onToggleSection(FilterSection.ROOM_TYPE) },
                dividerColor = dividerGray,
                titleColor = mediumText
            ) {
                RoomTypeOptions(
                    selectedOptions = filterState.selectedRoomTypes,
                    availableOptions = availableRoomTypes,
                    onOptionsSelected = { onFilterStateChange(filterState.copy(selectedRoomTypes = it)) },
                    primaryGreen = primaryGreen,
                    borderGray = borderGray,
                    lightText = lightText
                )
            }
        }

        item {
            ExpandableFilterSection(
                title = "Style",
                expanded = expandedStyle,
                onExpandChange = { onToggleSection(FilterSection.STYLE) },
                dividerColor = dividerGray,
                titleColor = mediumText
            ) {
                StyleOptions(
                    selectedOptions = filterState.selectedStyles,
                    availableStyles = availableStyles,
                    onOptionsSelected = { onFilterStateChange(filterState.copy(selectedStyles = it)) },
                    primaryGreen = primaryGreen,
                    borderGray = borderGray,
                    lightText = lightText
                )
            }
        }

        item {
            ExpandableFilterSection(
                title = "Colour",
                expanded = expandedColor,
                onExpandChange = { onToggleSection(FilterSection.COLOR) },
                dividerColor = dividerGray,
                titleColor = mediumText
            ) {
                ColorOptions(
                    selectedPaletteIds = filterState.selectedColors, // Changed parameter name
                    availablePalettes = availableColors, // Pass dynamic data
                    onPalettesSelected = { onFilterStateChange(filterState.copy(selectedColors = it)) },
                    primaryGreen = primaryGreen
                )
            }
        }

        item {
            ExpandableFilterSection(
                title = "By Format",
                expanded = expandedFormat,
                onExpandChange = { onToggleSection(FilterSection.FORMAT) },
                dividerColor = dividerGray,
                titleColor = mediumText
            ) {
                FormatOptions(
                    selectedOptions = filterState.selectedFormats,
                    onOptionsSelected = { onFilterStateChange(filterState.copy(selectedFormats = it)) },
                    primaryGreen = primaryGreen,
                    borderGray = borderGray,
                    lightText = lightText
                )
            }
        }

        item {
            ExpandableFilterSection(
                title = "Price",
                expanded = expandedPrice,
                onExpandChange = { onToggleSection(FilterSection.PRICE) },
                dividerColor = dividerGray,
                titleColor = mediumText
            ) {
                PriceOptions(
                    selectedOptions = filterState.selectedPrices,
                    onOptionsSelected = { onFilterStateChange(filterState.copy(selectedPrices = it)) },
                    primaryGreen = primaryGreen,
                    borderGray = borderGray,
                    lightText = lightText
                )
            }
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth().padding(top = 10.dp),
                horizontalArrangement = Arrangement.End
            ) {
                Button(
                    onClick = onApplyFilters,
                    modifier = Modifier.padding(end = 10.dp),
                    shape = RoundedCornerShape(7.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = primaryGreen),
                ) {
                    Text(
                        text = "Apply Filters",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = whiteText,
                        letterSpacing = (-0.5).sp,
                        modifier = Modifier.padding(vertical = 3.dp, horizontal = 4.dp)
                    )
                }

                OutlinedButton(
                    onClick = onCancel,
                    shape = RoundedCornerShape(7.dp),
                    colors = ButtonDefaults.outlinedButtonColors(containerColor = Color.White),
                    border = BorderStroke(1.dp, cancelBorderColor),
                ) {
                    Text(
                        text = "Cancel",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = cancelTextColor,
                        letterSpacing = (-0.5).sp,
                        modifier = Modifier.padding(vertical = 3.dp, horizontal = 4.dp)
                    )
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@Composable
fun ExpandableFilterSection(
    title: String,
    expanded: Boolean,
    onExpandChange: (Boolean) -> Unit,
    dividerColor: Color,
    titleColor: Color,
    content: @Composable () -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(6.dp))
                .clickable { onExpandChange(!expanded) },
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = title,
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
                color = titleColor,
                modifier = Modifier.padding(start = 10.dp, top = 10.dp, bottom = 10.dp)
            )

            Image(
                painter = painterResource(if (expanded) Res.drawable.keyboard_arrow_up_24px else Res.drawable.keyboard_arrow_down_24px),
                contentDescription = null,
                modifier = Modifier
            )
        }

        AnimatedVisibility(
            visible = expanded,
            enter = expandVertically(
                animationSpec = tween(
                    durationMillis = 300,
                    easing = FastOutSlowInEasing
                )
            ) + fadeIn(
                animationSpec = tween(
                    durationMillis = 300
                )
            ),
            exit = shrinkVertically(
                animationSpec = tween(
                    durationMillis = 300,
                    easing = FastOutSlowInEasing
                )
            ) + fadeOut(
                animationSpec = tween(
                    durationMillis = 300
                )
            )
        ) {
            Column {
                Spacer(modifier = Modifier.height(5.dp))
                content()
            }
        }
    }
}

@Composable
fun RadioOption(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
    primaryGreen: Color,
    borderGray: Color,
    lightText: Color
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(6.dp))
            .clickable(onClick = onClick),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            modifier = Modifier
                .padding(start = 5.dp, top = 5.dp, bottom = 5.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(12.dp)
                    .border(0.7.dp, borderGray, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                if (selected) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .background(primaryGreen, CircleShape)
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            Text(
                text = text,
                fontSize = 14.sp,
                fontWeight = FontWeight.Normal,
                color = lightText,
            )
        }
    }
}

@Composable
fun RoomTypeOptions(
    selectedOptions: Set<String>,
    availableOptions: List<String>, // Add this parameter
    onOptionsSelected: (Set<String>) -> Unit,
    primaryGreen: Color,
    borderGray: Color,
    lightText: Color
) {
    val allOptions = listOf("All") + availableOptions
    val optionsWithoutAll = allOptions.filter { it != "All" }


    FlowRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 5.dp, end = 5.dp, bottom = 18.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(1.dp),
        maxItemsInEachRow = 2
    ) {
        allOptions.forEach { option ->
            Box(modifier = Modifier.weight(1f)) {
                RadioOption(
                    text = option,
                    selected = selectedOptions.contains(option),
                    onClick = {
                        val newSelection = when {
                            option == "All" -> {
                                if (selectedOptions.contains("All")) {
                                    val firstOptionAfterAll = allOptions.getOrNull(1)
                                    if (firstOptionAfterAll != null) {
                                        selectedOptions - "All" - firstOptionAfterAll
                                    } else {
                                        selectedOptions - "All"
                                    }
                                } else {
                                    allOptions.toSet()
                                }
                            }

                            selectedOptions.contains("All") && option != "All" -> {
                                (allOptions.toSet() - "All" - option).ifEmpty { emptySet() }
                            }

                            selectedOptions.contains(option) -> {
                                (selectedOptions - option).ifEmpty { emptySet() }
                            }

                            else -> {
                                val updated = selectedOptions + option
                                if (updated.size == optionsWithoutAll.size) updated + "All" else updated
                            }
                        }
                        onOptionsSelected(newSelection)
                    },
                    primaryGreen = primaryGreen,
                    borderGray = borderGray,
                    lightText = lightText
                )
            }
        }
    }
}

@Composable
fun StyleOptions(
    selectedOptions: Set<String>,
    availableStyles: List<String>,
    onOptionsSelected: (Set<String>) -> Unit,
    primaryGreen: Color,
    borderGray: Color,
    lightText: Color
) {
    val allOptions = listOf("All") + availableStyles
    val optionsWithoutAll = allOptions.filter { it != "All" }

    FlowRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 5.dp, end = 5.dp, bottom = 18.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(1.dp),
        maxItemsInEachRow = 2
    ) {
        allOptions.forEach { option ->
            Box(modifier = Modifier.weight(1f)) {
                RadioOption(
                    text = option,
                    selected = selectedOptions.contains(option),
                    onClick = {
                        val newSelection = when {
                            option == "All" -> {
                                if (selectedOptions.contains("All")) {
                                    val firstOptionAfterAll = allOptions.getOrNull(1)
                                    if (firstOptionAfterAll != null) {
                                        selectedOptions - "All" - firstOptionAfterAll
                                    } else {
                                        selectedOptions - "All"
                                    }
                                } else {
                                    allOptions.toSet()
                                }
                            }

                            selectedOptions.contains("All") && option != "All" -> {
                                (allOptions.toSet() - "All" - option).ifEmpty { emptySet() }
                            }

                            selectedOptions.contains(option) -> {
                                (selectedOptions - option).ifEmpty { emptySet() }
                            }

                            else -> {
                                val updated = selectedOptions + option
                                if (updated.size == optionsWithoutAll.size) updated + "All" else updated
                            }
                        }
                        onOptionsSelected(newSelection)
                    },
                    primaryGreen = primaryGreen,
                    borderGray = borderGray,
                    lightText = lightText
                )
            }
        }
    }
}

@Composable
fun ColorOptions(
    selectedPaletteIds: Set<Int>, // Changed to palette IDs
    availablePalettes: List<ColorPalette>, // Add this parameter
    onPalettesSelected: (Set<Int>) -> Unit, // Changed to IDs
    primaryGreen: Color
) {


    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 10.dp, end = 10.dp, bottom = 18.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        availablePalettes.forEach { palette ->
            val isSelected = selectedPaletteIds.contains(palette.id)

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(
                        width = if (isSelected) 2.dp else 1.dp,
                        color = if (isSelected) primaryGreen else Color(0xFFE5E5E5),
                        shape = RoundedCornerShape(8.dp)
                    )
                    .clip(RoundedCornerShape(8.dp))
                    .clickable {
                        val newSelection = if (isSelected) {
                            selectedPaletteIds - palette.id
                        } else {
                            selectedPaletteIds + palette.id
                        }
                        onPalettesSelected(newSelection)
                    }
                    .padding(12.dp)
            ) {
                Text(
                    text = "Palette ${palette.id}",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF4D4D4D),
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    palette.colors.forEach { color ->
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .border(
                                    width = 1.dp,
                                    color = Color(0xFFD0D0D0),
                                    shape = CircleShape
                                )
                                .padding(2.dp)
                                .background(color, CircleShape)
                        )
                    }
                }
            }
        }
    }
}


@Composable
fun FormatOptions(
    selectedOptions: Set<String>,
    onOptionsSelected: (Set<String>) -> Unit,
    primaryGreen: Color,
    borderGray: Color,
    lightText: Color
) {
    val allOptions = listOf("All", "JPEG", "PNG", "PDF")
    val optionsWithoutAll = allOptions.filter { it != "All" }

    FlowRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 5.dp, end = 5.dp, bottom = 18.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(1.dp),
        maxItemsInEachRow = 2
    ) {
        allOptions.forEach { option ->
            Box(modifier = Modifier.weight(1f)) {
                RadioOption(
                    text = option,
                    selected = selectedOptions.contains(option),
                    onClick = {
                        val newSelection = when {
                            option == "All" -> {
                                if (selectedOptions.contains("All")) {
                                    val firstOptionAfterAll = allOptions.getOrNull(1)
                                    if (firstOptionAfterAll != null) {
                                        selectedOptions - "All" - firstOptionAfterAll
                                    } else {
                                        selectedOptions - "All"
                                    }
                                } else {
                                    allOptions.toSet()
                                }
                            }

                            selectedOptions.contains("All") && option != "All" -> {
                                (allOptions.toSet() - "All" - option).ifEmpty { emptySet() }
                            }

                            selectedOptions.contains(option) -> {
                                (selectedOptions - option).ifEmpty { emptySet() }
                            }

                            else -> {
                                val updated = selectedOptions + option
                                if (updated.size == optionsWithoutAll.size) updated + "All" else updated
                            }
                        }
                        onOptionsSelected(newSelection)
                    },
                    primaryGreen = primaryGreen,
                    borderGray = borderGray,
                    lightText = lightText
                )
            }
        }
    }
}

@Composable
fun PriceOptions(
    selectedOptions: Set<String>,
    onOptionsSelected: (Set<String>) -> Unit,
    primaryGreen: Color,
    borderGray: Color,
    lightText: Color
) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(start = 5.dp, end = 5.dp, bottom = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Box(modifier = Modifier.weight(1f)) {
            RadioOption(
                text = "Free",
                selected = selectedOptions.contains("Free"),
                onClick = {
                    val newSelection = if (selectedOptions.contains("Free")) {
                        selectedOptions - "Free"
                    } else {
                        selectedOptions + "Free"
                    }
                    onOptionsSelected(newSelection)
                },
                primaryGreen = primaryGreen,
                borderGray = borderGray,
                lightText = lightText
            )
        }
        Box(modifier = Modifier.weight(1f)) {
            RadioOption(
                text = "Premium",
                selected = selectedOptions.contains("Premium"),
                onClick = {
                    val newSelection = if (selectedOptions.contains("Premium")) {
                        selectedOptions - "Premium"
                    } else {
                        selectedOptions + "Premium"
                    }
                    onOptionsSelected(newSelection)
                },
                primaryGreen = primaryGreen,
                borderGray = borderGray,
                lightText = lightText
            )
        }
    }
}
