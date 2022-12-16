package com.example.ebaton

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.EaseInCirc
import androidx.compose.animation.core.EaseInOutBounce
import androidx.compose.animation.core.EaseInSine
import androidx.compose.animation.core.EaseOutExpo
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.example.ebaton.ui.theme.EbatonTheme
import kotlinx.coroutines.runBlocking

private val networkService = KtorService()
private var ducks = listOf<DuckResponse>()

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            EbatonTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Content()
                }
            }
        }
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
private fun Content() {
    val isDetailsDisplayed = remember { mutableStateOf(false) }
    val selectedDuck = remember { mutableStateOf<DuckResponse?>(null) }

    AnimatedVisibility(
        visible = !isDetailsDisplayed.value,
        modifier = Modifier.fillMaxSize(),
        enter = fadeIn(
            animationSpec = tween(
                durationMillis = 2400
            )
        ) + slideInHorizontally(
            animationSpec = tween(
                durationMillis = 800,
                easing = EaseInSine
            )
        ) + slideInVertically(
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioHighBouncy
            )
        ) + scaleIn(
            animationSpec = tween(
                durationMillis = 1200,
                easing = EaseInOutBounce,
                delayMillis = 2400
            )
        ),
        exit = fadeOut(
            animationSpec = tween(
                durationMillis = 2400
            )
        ) + slideOutHorizontally(
            animationSpec = tween(
                durationMillis = 1200,
                easing = EaseInSine
            )
        ) + scaleOut(
            animationSpec = tween(
                durationMillis = 800,
                easing = EaseInOutBounce
            )
        )
    ) {
        RecyclerView {
            selectedDuck.value = it
            isDetailsDisplayed.value = true
        }
    }

    AnimatedVisibility(
        visible = isDetailsDisplayed.value,
        modifier = Modifier.fillMaxSize(),
        enter = slideInHorizontally(
            animationSpec = tween(
                durationMillis = 1200,
                easing = EaseInSine
            )
        ) + scaleIn(
            animationSpec = tween(
                durationMillis = 1200,
                easing = EaseInOutBounce
            )
        ) + fadeIn(
            animationSpec = tween(
                durationMillis = 800,
                easing = EaseInCirc
            )
        ),
        exit = slideOutVertically(
            animationSpec = tween(
                durationMillis = 1200,
                easing = EaseInSine
            )
        ) + fadeOut(
            animationSpec = tween(
                durationMillis = 1600,
                easing = EaseOutExpo
            )
        ) + scaleOut(
            animationSpec = tween(
                durationMillis = 800,
                easing = EaseInOutBounce
            )
        )
    ) {
        selectedDuck.value?.let {
            DetailsView(response = it) {
                isDetailsDisplayed.value = false
            }
        } ?: WhatTheFuckAreYouDoing()
    }
}

@Composable
private fun RecyclerView(
    clickListener: (DuckResponse) -> Unit
) {
    val state = remember { mutableStateOf(ducks) }

    Box(
        modifier = Modifier.fillMaxSize(),
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight(),
        ) {
            items(state.value) {
                val isFirst = state.value.first() == it
                val isLast = state.value.last() == it
                RecyclerViewAdapter(
                    response = it,
                    isFirst = isFirst,
                    isLast = isLast,
                    clickListener = clickListener
                )
            }
        }
        Button(
            onClick = {
                ducks = (0 until 10).map {
                    runBlocking { networkService.getRandomDuck() }
                }
                state.value = ducks
            },
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
        ) {
            Text(text = "Загрузить 10 уток!")
        }
    }
}

@Composable
private fun RecyclerViewAdapter(
    response: DuckResponse,
    isFirst: Boolean,
    isLast: Boolean,
    clickListener: (DuckResponse) -> Unit
) {
    var topPadding: Dp = 4.dp
    var bottomPadding: Dp = 4.dp

    if (isFirst) {
        topPadding = 8.dp
        bottomPadding = 4.dp
    }

    if (isLast) {
        topPadding = 4.dp
        bottomPadding = 52.dp
    }

    Column(modifier = Modifier
        .fillMaxWidth()
        .clickable { clickListener(response) }) {
        Spacer(
            modifier = Modifier
                .fillMaxWidth()
                .height(topPadding)
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp)
                .padding(horizontal = 8.dp)
        ) {

            Image(
                painter = rememberAsyncImagePainter(model = response.url),
                contentDescription = null,
                alignment = Alignment.Center,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxHeight()
                    .aspectRatio(1F)
            )
            Text(
                text = "Рандомная утка, ничего необычного",
                modifier = Modifier
                    .align(Alignment.CenterVertically)
                    .padding(horizontal = 8.dp)
            )
        }
        Spacer(
            modifier = Modifier
                .fillMaxWidth()
                .height(bottomPadding)
        )
    }
}

@Composable
private fun DetailsView(
    response: DuckResponse,
    closeButtonListener: () -> Unit
) {
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        item {
            Image(
                painter = rememberAsyncImagePainter(model = response.url),
                contentDescription = null,
                alignment = Alignment.Center,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1F)
            )
        }
        item {
            Spacer(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(16.dp)
            )
        }
        item {
            Text(
                text = """
            |Привет, друг!
            |
            |Если ты тут, чтобы увидеть какой-то интересный факт про уток, то извини - апишка этих данных не предоставляет.
            |Но я благодарен, что ты заинтересовался/ась моей работой для EБ@TON, я старался всё сделать как можно хуже!
            |
            |С наступающим новым годом!
            |
            |P.S. ${response.message}.
            """.trimMargin(),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp)
            )
        }
        item {
            Spacer(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
            )
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Button(modifier = Modifier
            .fillMaxWidth()
            .align(Alignment.BottomCenter), onClick = { closeButtonListener() }) {
            Text(text = "Закрыть детали утки")
        }
    }

}

@Composable
fun WhatTheFuckAreYouDoing() {
    Text(
        text = "Ты чиво наделал......",
        modifier = Modifier.fillMaxSize(),
        textAlign = TextAlign.Center,
    )
}