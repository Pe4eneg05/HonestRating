@file:Suppress("DEPRECATION")

package com.pechenegmobilecompanyltd.honestrating.ui.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.with
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.pechenegmobilecompanyltd.honestrating.R
import com.pechenegmobilecompanyltd.honestrating.model.OnboardingPage
import com.pechenegmobilecompanyltd.honestrating.ui.theme.PrimaryColor
import kotlinx.coroutines.launch
import kotlin.io.path.ExperimentalPathApi

@OptIn(ExperimentalPathApi::class, ExperimentalAnimationApi::class)
@Composable
fun OnboardingScreen(navController: NavController) {
    // Устанавливаем цвет системной панели
    val systemUiController = rememberSystemUiController()
    systemUiController.setSystemBarsColor(
        color = PrimaryColor,
        darkIcons = false
    )

    // Добавляем корутинный scope
    val scope = rememberCoroutineScope()

    // писок страниц онбординга
    val pages = listOf(
        OnboardingPage(
            title = stringResource(R.string.onboarding_title_1),
            description = stringResource(R.string.onboarding_desc_1),
            lottieRes = R.raw.welcome
        ),
        OnboardingPage(
            title = stringResource(R.string.onboarding_title_2),
            description = stringResource(R.string.onboarding_desc_2),
            lottieRes = R.raw.search
        ),
        OnboardingPage(
            title = stringResource(R.string.onboarding_title_3),
            description = stringResource(R.string.onboarding_desc_3),
            lottieRes = R.raw.done
        )
    )

    val pagerState = rememberPagerState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Вертикальный пейджер с контентом
        HorizontalPager(
            count = pages.size,
            state = pagerState,
            modifier = Modifier
                .weight(1f),
            contentPadding = PaddingValues(horizontal = 16.dp),
            key = { it } // Для правильной анимации
        ) { page ->
            // Анимация появления страницы - ИЗМЕНЕНО НАПРАВЛЕНИЕ
            AnimatedVisibility(
                visible = pagerState.currentPage == page,
                enter = fadeIn() + slideInHorizontally(
                    initialOffsetX = { fullWidth -> fullWidth }  // Начинаем справа
                ),
                exit = fadeOut() + slideOutHorizontally(
                    targetOffsetX = { fullWidth -> -fullWidth }  // Уходим влево
                )
            ) {
                OnboardingPageContent(page = pages[page])
            }
        }

        // Панель с индикаторами и кнопкой
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 32.dp, vertical = 24.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Индикаторы
            Row {
                repeat(pages.size) { index ->
                    val color by animateColorAsState(
                        targetValue = if (pagerState.currentPage == index)
                            MaterialTheme.colorScheme.primary
                        else
                            MaterialTheme.colorScheme.onBackground.copy(alpha = 0.3f),
                        animationSpec = tween(300)
                    )

                    Box(
                        modifier = Modifier
                            .padding(4.dp)
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(color)
                    )
                }
            }

            // Анимированная кнопка
            Button(
                onClick = {
                    if (pagerState.currentPage < pages.size - 1) {
                        // Вызываем внутри корутины
                        scope.launch {
                            pagerState.animateScrollToPage(pagerState.currentPage + 1)
                        }
                    } else {
                        navController.navigate("auth") {
                            popUpTo("onboarding") { inclusive = true }
                        }
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                )
            ) {
                AnimatedContent(
                    targetState = pagerState.currentPage == pages.size - 1,
                    transitionSpec = {
                        fadeIn(animationSpec = tween(300)) with fadeOut(animationSpec = tween(300))
                    }
                ) { isLastPage ->
                    Text(
                        text = if (isLastPage)
                            stringResource(R.string.start)
                        else
                            stringResource(R.string.next),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

@Composable
fun OnboardingPageContent(page: OnboardingPage) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(40.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Lottie анимация
        val composition by rememberLottieComposition(
            LottieCompositionSpec.RawRes(page.lottieRes)
        )
        val progress by animateLottieCompositionAsState(
            composition = composition,
            iterations = LottieConstants.IterateForever,
            speed = 1f,
            restartOnPlay = false
        )

        LottieAnimation(
            composition = composition,
            progress = { progress },
            modifier = Modifier
                .size(250.dp)
                .padding(16.dp)
        )

        // Заголовок
        Text(
            text = page.title,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(top = 32.dp)
        )

        // Описание
        Text(
            text = page.description,
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f),
            modifier = Modifier.padding(top = 16.dp)
        )
    }
}