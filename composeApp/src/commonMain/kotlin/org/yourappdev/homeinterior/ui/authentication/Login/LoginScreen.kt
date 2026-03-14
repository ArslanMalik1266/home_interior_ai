package org.yourappdev.homeinterior.ui.authentication.Login

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import homeinterior.composeapp.generated.resources.Res
import homeinterior.composeapp.generated.resources.emailicon
import homeinterior.composeapp.generated.resources.google
import homeinterior.composeapp.generated.resources.hide_
import homeinterior.composeapp.generated.resources.passicon
import homeinterior.composeapp.generated.resources.show_1_
import kotlinx.coroutines.flow.SharedFlow
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel
import org.yourappdev.homeinterior.data.remote.util.ResultState
import org.yourappdev.homeinterior.navigation.Routes
import org.yourappdev.homeinterior.ui.authentication.AuthViewModel
import org.yourappdev.homeinterior.ui.authentication.register.RegisterEvent
import org.yourappdev.homeinterior.ui.authentication.register.RegisterState
import org.yourappdev.homeinterior.ui.UiUtils.BackIconButton
import org.yourappdev.homeinterior.ui.UiUtils.ButtonWithIcon
import org.yourappdev.homeinterior.ui.UiUtils.ClickableText
import org.yourappdev.homeinterior.ui.UiUtils.CustomSnackbar
import org.yourappdev.homeinterior.ui.UiUtils.ProgressLoading
import org.yourappdev.homeinterior.ui.UiUtils.rememberCustomSnackbarState
import org.yourappdev.homeinterior.ui.common.base.CommonUiEvent
import org.yourappdev.homeinterior.ui.theme.app_color
import org.yourappdev.homeinterior.ui.theme.buttonBack
import org.yourappdev.homeinterior.ui.theme.green_border
import org.yourappdev.homeinterior.ui.theme.green_btn
import org.yourappdev.homeinterior.ui.theme.grey_border
import org.yourappdev.homeinterior.ui.theme.smallText
import org.yourappdev.homeinterior.ui.theme.white_color

@Preview(showBackground = true)
@Composable
fun LoginRoot(authViewModel: AuthViewModel = koinViewModel(), navController: NavHostController, onBackClick: (() -> Unit)? = null) {
    val state by authViewModel.state.collectAsState()
    LoginScreen(navController, authViewModel.uiEvent, state, authViewModel::onRegisterFormEvent,onBackClick)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    navController: NavHostController,
    uiEvent: SharedFlow<CommonUiEvent>,
    state: RegisterState,
    onLoginEvent: (event: RegisterEvent) -> Unit,
    onBackClick: (() -> Unit)? = null
) {
    val snackBarState = rememberCustomSnackbarState()
    LaunchedEffect(Unit) {
        uiEvent.collect { event ->
            println("DEBUG_UI_EVENT: Received Event -> $event")
            when (event) {
                is CommonUiEvent.ShowError -> {
                    snackBarState.showError(event.message)
                }

                CommonUiEvent.NavigateToSuccess -> {
                    navController.navigate(Routes.Verification) {
                    }
                }

                is CommonUiEvent.ShowSuccess -> {
                    snackBarState.showSuccess(event.message)
                }
            }
        }
    }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(white_color)
            .statusBarsPadding()
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(28.dp)
        ) {
            BackIconButton {
                if (onBackClick != null) {
                    onBackClick()
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Login",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = app_color
            )
            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Sign in to explore AI room makeovers, save your favorite styles, and transform your living space.",
                fontSize = 14.sp,
                color = smallText,
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Email field
            Text(
                text = "Email",
                fontSize = 14.sp,
                color = smallText,
            )
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = state.email,
                onValueChange = { onLoginEvent(RegisterEvent.EmailUpdate(it)) },
                modifier = Modifier.fillMaxWidth(),
                placeholder = {
                    Text(
                        "Ex. abc@example.com",
                        color = grey_border
                    )
                },
                leadingIcon = {
                    Image(
                        painter = painterResource(Res.drawable.emailicon),
                        contentDescription = null,
                        colorFilter = ColorFilter.tint(color = LocalContentColor.current)
                    )
                },
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedBorderColor = grey_border,
                    focusedBorderColor = green_border,
                    focusedLeadingIconColor = green_border,
                    unfocusedLeadingIconColor = grey_border
                ),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                singleLine = true
            )
            Spacer(modifier = Modifier.height(24.dp))

            // Login button
            Button(
                onClick = {
                    println("DEBUG_LOGIN_BTN: Button Clicked! Email = ${state.email}")
                    onLoginEvent(RegisterEvent.Login)
                },
                enabled = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = green_btn
                )
            ) {
                Text(
                    text = "Login",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            HorizontalDivider(
                thickness = 1.dp,
                color = Color(0xffD2CECE),
                modifier = Modifier.fillMaxWidth().padding(vertical = 20.dp, horizontal = 10.dp)
            )

        }
        if (state.loginResponse is ResultState.Loading) {
            ProgressLoading()
        }
        CustomSnackbar(
            state = snackBarState,
            duration = 3000L
        )
    }
}
