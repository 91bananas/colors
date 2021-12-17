@file:OptIn(ExperimentalNavigationApi::class)

package com.chrynan.colors.sample.android

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.chrynan.colors.Color
import com.chrynan.colors.NamedColor
import com.chrynan.colors.extension.Red
import com.chrynan.colors.compose.toComposeColor
import com.chrynan.navigation.compose.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            var goToDetails: ((namedColor: NamedColor) -> Unit)? = null

            val navigator = rememberNavigatorByKey<Screen>(Screen.ColorList) { key ->
                when (key) {
                    Screen.ColorList -> {
                        ColorListScreen(onColorSelected = {
                            goToDetails?.invoke(it)
                        })
                    }
                    is Screen.ColorDetail -> {
                        ColorDetailScreen(namedColor = key.namedColor)
                    }
                    Screen.Palette -> {
                        PaletteScreen()
                    }
                }
            }

            goToDetails = { navigator.goTo(Screen.ColorDetail(namedColor = it)) }

            BackHandler {
                navigator.goBack()
            }

            Scaffold(
                bottomBar = {
                    BottomBar(navigator)
                }
            ) {
                Column {
                    Toolbar()

                    NavContainer(navigator)
                }
            }
        }
    }

    @Composable
    private fun Toolbar() {
        TopAppBar {
            Box(modifier = Modifier.padding(8.dp)) {
                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight(),
                    text = "Colors",
                    textAlign = TextAlign.Start,
                    fontSize = 24.sp,
                    color = Color.White.toComposeColor()
                )
            }
        }
    }

    @Composable
    private fun BottomBar(navigator: ComposeNavigatorByKey<Screen>) {
        val currentRoute = navigator.currentKey

        BottomNavigation {
            listOf(Screen.ColorList, Screen.Palette).forEach {
                BottomNavigationItem(
                    selected = currentRoute == it,
                    selectedContentColor = Color.Red.toComposeColor(),
                    unselectedContentColor = Color.White.toComposeColor(),
                    icon = {
                        Icon(it.icon, contentDescription = "")
                    },
                    label = {
                        Text(stringResource(it.title))
                    },
                    onClick = {
                        navigator.goTo(it)
                    }
                )
            }
        }
    }
}
