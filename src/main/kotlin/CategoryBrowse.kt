import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.Button
import androidx.compose.material.ButtonColors
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import java.awt.Desktop
import java.net.URI

@Composable
@Preview
private fun CategoryBrowsePreview() = CategoryBrowse(onItemSelect = {})

@Composable
fun CategoryBrowse(onItemSelect: (AppListItem) -> Unit) {
    val categoryViewModel = remember {
        CategoryViewModel().apply { switchList(0) }
    }
    val appList = categoryViewModel.appList.collectAsState()

    Column {
        val categoryListState = rememberLazyListState()
        val scrollDelta = remember { mutableStateOf(0f) }

        LaunchedEffect(scrollDelta.value) {
            val offset = scrollDelta.value.takeIf { it != 0f } ?: return@LaunchedEffect
            val consumed = categoryListState.scrollBy(-offset)
            scrollDelta.value -= consumed
        }
        val categoryDragState = rememberDraggableState { delta ->
            scrollDelta.value = delta
        }
        LazyRow(
            modifier = Modifier
                .draggable(categoryDragState, Orientation.Horizontal),
            state = categoryListState
        ) {
            items(categoryViewModel.categories.size) { index ->
                Spacer(modifier = Modifier.width(if (index == 0) 16.dp else 8.dp))
                Button(
                    onClick = { categoryViewModel.switchList(index) },
                    colors = object : ButtonColors {
                        val currentIndex = categoryViewModel.currentIndex.collectAsState()

                        @Composable
                        override fun backgroundColor(enabled: Boolean): State<Color> {
                            return rememberUpdatedState(
                                if (index == currentIndex.value) {
                                    MaterialTheme.colors.primary
                                } else {
                                    MaterialTheme.colors.onPrimary
                                }
                            )
                        }

                        @Composable
                        override fun contentColor(enabled: Boolean): State<Color> {
                            return rememberUpdatedState(
                                if (index == currentIndex.value) {
                                    MaterialTheme.colors.onPrimary
                                } else {
                                    MaterialTheme.colors.primary
                                }
                            )
                        }
                    }
                ) {
                    Text(categoryViewModel.categories[index].second)
                }
            }
        }
        val mainLazyListState = rememberLazyListState()
        LazyColumn(
            modifier = Modifier,
            state = mainLazyListState,
        ) {
            items(appList.value.size) { index ->
                val item = appList.value[index]
                StoreItemCard(
                    modifier = Modifier.clickable {
                        if (Desktop.isDesktopSupported() && Desktop.getDesktop()
                                .isSupported(Desktop.Action.BROWSE)
                        ) {
                            onItemSelect(item)
                        }
                    },
                    appIcon = item.icons,
                    appName = item.name,
                    description = item.description,
                )
            }
        }
    }
}