package org.yourappdev.homeinterior.ui.UiUtils

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import homeinterior.composeapp.generated.resources.Res
import homeinterior.composeapp.generated.resources.close
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource

@Composable
fun DeleteConfirmationDialog(
    title:String,
    onConfirm: () -> Unit = {},
    onCancel: () -> Unit = {}
) {
    Surface(
        shape = MaterialTheme.shapes.medium,
        color = Color.White,
        tonalElevation = 6.dp
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Delete",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 18.sp,
                    modifier = Modifier.weight(1f).padding(start = 20.dp),
                    textAlign = TextAlign.Center
                )
                Box(modifier = Modifier.size(30.dp).clip(CircleShape).clickable {
                    onCancel()
                }, contentAlignment = Alignment.Center) {
                    Image(
                        painter = painterResource(Res.drawable.close),
                        contentDescription = "Close",
                        colorFilter = ColorFilter.tint(color = Color(0xFF8C8989)),
                        modifier = Modifier.size(23.dp)
                    )
                }
            }
            Divider(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp, bottom = 16.dp)
            )
            Text(
                text = title,
                fontWeight = FontWeight.SemiBold,
                fontSize = 14.sp,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Start
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 24.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier.weight(1f).background(Color.Transparent, RoundedCornerShape(50))
                        .border(1.dp, Color(0xffE1DDDD), shape = RoundedCornerShape(50))
                        .clip(RoundedCornerShape(50)).clickable(enabled = true, onClick = {

                        }), contentAlignment = Alignment.Center
                ) {
                    Text(
                        "Cancel",
                        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                        textAlign = TextAlign.Center, color = Color(0xff8C8989),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Button(
                    onClick = {

                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xffDC3545)),
                    shape = RoundedCornerShape(50),
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        "Delete", color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}