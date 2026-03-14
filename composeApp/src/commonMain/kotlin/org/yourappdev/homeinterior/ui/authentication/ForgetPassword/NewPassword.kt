package org.yourappdev.homeinterior.ui.authentication.ForgetPassword

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
import homeinterior.composeapp.generated.resources.Res
import homeinterior.composeapp.generated.resources.hide_
import homeinterior.composeapp.generated.resources.passicon
import homeinterior.composeapp.generated.resources.show_1_
import kotlinx.coroutines.flow.SharedFlow
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.viewmodel.koinViewModel
import org.yourappdev.homeinterior.data.remote.util.ResultState
import org.yourappdev.homeinterior.ui.authentication.AuthViewModel
import org.yourappdev.homeinterior.ui.authentication.register.RegisterEvent
import org.yourappdev.homeinterior.ui.authentication.register.RegisterState
import org.yourappdev.homeinterior.ui.UiUtils.BackIconButton
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

@Composable
fun NewPassRoot(
    authViewModel: AuthViewModel = koinViewModel(),
    onBack: () -> Unit = {},
    onSuccess: () -> Unit = {}
) {
    val state by authViewModel.state.collectAsState()
    NewPasswordScreen(state, authViewModel.uiEvent, onBack, onSuccess, authViewModel::onRegisterFormEvent)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewPasswordScreen(
    state: RegisterState,
    uiEvent: SharedFlow<CommonUiEvent>,
    onBack: () -> Unit,
    onSuccess: () -> Unit,
    onAuthEvent: (event: RegisterEvent) -> Unit
) {

    val snackBarState = rememberCustomSnackbarState()
    LaunchedEffect(Unit) {
        uiEvent.collect { event ->
            when (event) {
                is CommonUiEvent.ShowError -> {
                    snackBarState.showError(event.message)
                }

                CommonUiEvent.NavigateToSuccess -> {
                    onSuccess()
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
        if (state.forgetPasswordResetResponse is ResultState.Loading) {
            ProgressLoading()
        }
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
        ) {
            BackIconButton {
                onBack()
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Forgot Password?",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = app_color
            )
            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Recover you password if you have forgot the password!",
                fontSize = 14.sp,
                color = smallText,
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Email field
            Text(
                text = "Enter New Password",
                fontSize = 14.sp,
                color = smallText,
            )

            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = state.newPassword,
                onValueChange = { onAuthEvent(RegisterEvent.NewPasswordUpdate(it)) },
                modifier = Modifier.fillMaxWidth(),
                placeholder = {
                    Text(
                        "••••••••••",
                        color = grey_border
                    )
                },
                leadingIcon = {
                    Image(
                        painter = painterResource(Res.drawable.passicon),
                        contentDescription = null,
                        colorFilter = ColorFilter.tint(color = LocalContentColor.current)
                    )
                },
                trailingIcon = {
                    Box(modifier = Modifier.size(30.dp).clip(CircleShape).clickable {
                        onAuthEvent(RegisterEvent.TogglePassword(!state.showPassword))
                    }, contentAlignment = Alignment.Center) {
                        Image(
                            painter = painterResource(if (state.showPassword) Res.drawable.show_1_ else Res.drawable.hide_),
                            contentDescription = "Close",
                            colorFilter = ColorFilter.tint(color = buttonBack),
                            modifier = Modifier.size(24.dp)
                        )
                    }
                },
                visualTransformation = if (state.showPassword) VisualTransformation.None else PasswordVisualTransformation(),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedBorderColor = grey_border,
                    focusedBorderColor = green_border,
                    focusedLeadingIconColor = green_border,
                    unfocusedLeadingIconColor = grey_border
                ),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Submit button
            Button(
                onClick = { onAuthEvent(RegisterEvent.ForgetPasswordReset) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = green_btn
                )
            ) {
                Text(
                    text = "Submit",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}
