package com.example.parallaxscrollcompose

import android.content.res.Resources
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Text
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.parallaxscrollcompose.ui.theme.ParallaxScrollComposeTheme

/*
Make a cool parallax scroll effect you can use to pimp your app.
https://www.youtube.com/watch?v=1qscY5b4Q2s&list=PLQkwcJG4YTCSpJ2NLhDTHhi6XBNfk9WiC&index=21
 */
// Define a classe da atividade principal, estendendo ComponentActivity
class MainActivity : ComponentActivity() {
    // Sobrescreve o método onCreate
    override fun onCreate(savedInstanceState: Bundle?) {
        // Chama o método onCreate da superclasse
        super.onCreate(savedInstanceState)

        // Define o conteúdo da atividade usando o Jetpack Compose
        setContent {
            // Aplica o tema ParallaxScrollComposeTheme
            ParallaxScrollComposeTheme {
                // Define as velocidades de rolagem para a lua e o fundo do meio
                val moonScrollSpeed = 0.08f
                val midBgScrollSpeed = 0.03f

                // Calcula a altura da imagem com base na largura da tela
                val imageHeight = (LocalConfiguration.current.screenWidthDp * (2f / 3f)).dp

                // Lembra o estado da lista preguiçosa e os deslocamentos para lua e fundo do meio
                val lazyListState = rememberLazyListState()
                var moonOffset by remember {
                    mutableStateOf(0f)
                }
                var midBgOffset by remember {
                    mutableStateOf(0f)
                }

                // Cria uma NestedScrollConnection para lidar com o comportamento de rolagem
                val nestedScrollConnection = object : NestedScrollConnection {
                    override fun onPreScroll(
                        available: Offset,
                        source: NestedScrollSource
                    ): Offset {
                        val delta = available.y
                        val layoutInfo = lazyListState.layoutInfo

                        // Verifica se o primeiro ou último item está visível para determinar o comportamento de rolagem
                        if (lazyListState.firstVisibleItemIndex == 0) {
                            return Offset.Zero
                        }
                        if (layoutInfo.visibleItemsInfo.lastOrNull()?.index == layoutInfo.totalItemsCount - 1) {
                            return Offset.Zero
                        }

                        // Atualiza os deslocamentos com base na velocidade de rolagem e delta
                        moonOffset += delta * moonScrollSpeed
                        midBgOffset += delta * midBgScrollSpeed

                        // Retorna Offset.Zero para consumir a rolagem
                        return Offset.Zero
                    }
                }

                // Define o LazyColumn com rolagem aninhada e lazyListState
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .nestedScroll(nestedScrollConnection),
                    state = lazyListState
                ) {
                    // Cria 10 itens de exemplo
                    items(10) {
                        Text(
                            text = "Item de exemplo",
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        )
                    }

                    // Cria uma caixa contendo imagens com efeito de paralaxe
                    item {
                        Box(
                            modifier = Modifier
                                .clipToBounds()
                                .fillMaxWidth()
                                .height(imageHeight + midBgOffset.toDp())
                                .background(
                                    Brush.verticalGradient(
                                        listOf(
                                            Color(0xFFf32b21),
                                            Color(0xFFf9a521)
                                        )
                                    )
                                )
                        ) {
                            // Imagem da lua com translação com base em moonOffset
                            Image(
                                painter = painterResource(id = R.drawable.ic_moonbg),
                                contentDescription = "lua",
                                contentScale = ContentScale.FillWidth,
                                alignment = Alignment.BottomCenter,
                                modifier = Modifier
                                    .matchParentSize()
                                    .graphicsLayer {
                                        translationY = moonOffset
                                    }
                            )

                            // Imagem do fundo do meio com translação com base em midBgOffset
                            Image(
                                painter = painterResource(id = R.drawable.ic_midbg),
                                contentDescription = "fundo do meio",
                                contentScale = ContentScale.FillWidth,
                                alignment = Alignment.BottomCenter,
                                modifier = Modifier
                                    .matchParentSize()
                                    .graphicsLayer {
                                        translationY = midBgOffset
                                    }
                            )

                            // Imagem do fundo externo sem translação
                            Image(
                                painter = painterResource(id = R.drawable.ic_outerbg),
                                contentDescription = "fundo externo",
                                contentScale = ContentScale.FillWidth,
                                alignment = Alignment.BottomCenter,
                                modifier = Modifier.matchParentSize()
                            )
                        }
                    }

                    // Cria mais 20 itens de exemplo
                    items(20) {
                        Text(
                            text = "Item de exemplo",
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        )
                    }
                }
            }
        }
    }

    // Função de extensão para converter Float para Dp
    private fun Float.toDp(): Dp {
        return (this / Resources.getSystem().displayMetrics.density).dp
    }
}
