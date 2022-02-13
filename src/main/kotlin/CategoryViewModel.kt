import api.AppListItem
import api.StoreApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import util.GsonUtil


class CategoryViewModel {
    val appList = MutableStateFlow<List<AppListItem>>(listOf())

    val categories = listOf(
        "network" to "网络",
        "chat" to "聊天",
        "music" to "音乐",
        "video" to "影视",
        "image_graphics" to "图像",
        "games" to "游戏",
        "office" to "办公",
        "reading" to "阅读",
        "development" to "开发",
        "tools" to "工具",
        "themes" to "主题",
        "others" to "其他",
    )
    var currentIndex = MutableStateFlow(0)

    fun switchList(index: Int) {
        currentIndex.value = index
        GlobalScope.launch {
            val result = withContext(Dispatchers.IO) {
                val retrofit = Retrofit.Builder()
                    .baseUrl(StoreApi.BASE_URL)
                    .addConverterFactory(GsonUtil.gsonConverter)
                    .build()
                val service: StoreApi = retrofit.create(StoreApi::class.java)
                service.getAppList(categories[index].first)
            }
            appList.value = result
        }
    }
}