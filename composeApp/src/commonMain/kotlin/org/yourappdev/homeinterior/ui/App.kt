package org.yourappdev.homeinterior.ui

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import coil3.ImageLoader
import coil3.SingletonImageLoader
import coil3.compose.LocalPlatformContext
import coil3.network.ktor3.KtorNetworkFetcherFactory
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.KoinApplication
import org.koin.compose.viewmodel.koinViewModel
import org.koin.dsl.KoinAppDeclaration
import org.yourappdev.homeinterior.di.appModule
import org.yourappdev.homeinterior.navigation.Routes
import org.yourappdev.homeinterior.platformModule
import org.yourappdev.homeinterior.ui.authentication.AuthViewModel
import org.yourappdev.homeinterior.ui.authentication.ForgetPassword.ForgetEmailRoot
import org.yourappdev.homeinterior.ui.authentication.ForgetPassword.ForgetOTPRoot
import org.yourappdev.homeinterior.ui.authentication.ForgetPassword.NewPassRoot
import org.yourappdev.homeinterior.ui.authentication.Login.LoginRoot
import org.yourappdev.homeinterior.ui.authentication.Login.WelcomeScreen
import org.yourappdev.homeinterior.ui.BottomBarScreen.BaseBottomBarScreen
import org.yourappdev.homeinterior.ui.OnBoarding.BaseScreen
import org.yourappdev.homeinterior.ui.OnBoarding.OnBoardingViewModel
import org.yourappdev.homeinterior.ui.OnBoarding.SplashScreen
import org.yourappdev.homeinterior.ui.authentication.Verification.VerificationRoot
import org.yourappdev.homeinterior.ui.theme.AppTypography
import org.yourappdev.homeinterior.utils.createImageLoader

@Composable
@Preview
fun App(koinAppDeclaration: KoinAppDeclaration? = null) {

    val context = LocalPlatformContext.current
    SingletonImageLoader.setSafe {
        createImageLoader(context)
    }


    KoinApplication(application = {
        koinAppDeclaration?.invoke(this)
        modules(appModule() + platformModule())
    }) {
        val navController = rememberNavController()
        val authViewModel: AuthViewModel = koinViewModel()
        MaterialTheme(typography = AppTypography()) {
            NavHost(navController, startDestination = Routes.Splash) {
                composable<Routes.Welcome> {
                    WelcomeScreen(onLogin = {
                        navController.navigate(Routes.Login)
                    })
                }
                composable<Routes.Login> {
                    LoginRoot(
                        authViewModel = authViewModel,
                        navController = navController,
                        onBackClick = {
                            navController.popBackStack()
                        })

                }
                composable<Routes.Splash> {
                    SplashScreen(navController = navController,
                        authViewModel = authViewModel)
                }
                composable<Routes.ForgetEmail> {
                    ForgetEmailRoot(onBack = {
                        navController.navigateUp()
                    }, onSuccess = {
                        navController.navigate(Routes.ForgetOTP)
                    })
                }

                composable<Routes.ForgetOTP> {
                    val parent = remember(navController) {
                        navController.previousBackStackEntry
                    }
                    parent?.let {
                        val authViewModel: AuthViewModel = koinViewModel(viewModelStoreOwner = it)
                        ForgetOTPRoot(onBackClick = {
                            navController.navigateUp()
                        }, authViewModel = authViewModel, onSuccess = {
                            navController.navigate(Routes.ForgetNewPass)
                        })
                    }
                }

                composable<Routes.ForgetNewPass> {
                    val parent = remember(it) {
                        navController.getBackStackEntry(Routes.ForgetEmail)
                    }

                    val authViewModel: AuthViewModel = koinViewModel(viewModelStoreOwner = parent)
                    NewPassRoot(authViewModel, onBack = {
                        navController.navigateUp()
                    }, onSuccess = {
                        navController.navigate(Routes.Login) {
                            popUpTo(Routes.Login) {
                                inclusive = false
                            }
                        }
                    })

                }

                composable<Routes.Verification> {
                    VerificationRoot(
                        onBackClick = { navController.popBackStack() },
                        authViewModel = authViewModel,
                        onSuccess = {
                            navController.navigate(Routes.BaseAppScreen) {
                                popUpTo(0) { inclusive = true }
                                launchSingleTop = true
                            }
                        }
                    )
                }
                composable<Routes.OnBoarding> {
                    val onBoardingViewModel: OnBoardingViewModel = koinViewModel()
                    BaseScreen() {
                        onBoardingViewModel.onBoardingDone()
                        navController.navigate(Routes.BaseAppScreen)
                    }
                }
                composable<Routes.BaseAppScreen> {
                    BaseBottomBarScreen(rootNavController = navController,
                        authViewModel = authViewModel)

                }
            }
        }
    }
}