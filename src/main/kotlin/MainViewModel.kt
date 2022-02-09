import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class MainViewModel {
    val appList = MutableStateFlow<List<AppListItem>>(listOf())

    fun switchList() {
        GlobalScope.launch {
            val result = withContext(Dispatchers.IO) {
                val retrofit = Retrofit.Builder()
                    .baseUrl(StoreApi.BASE_URL)
                    .addConverterFactory(GsonUtil.gsonConverter)
                    .build()
                val service: StoreApi = retrofit.create(StoreApi::class.java)
                service.getAppList("games")
            }
            appList.value = result
        }
    }
}