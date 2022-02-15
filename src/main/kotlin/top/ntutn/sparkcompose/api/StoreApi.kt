package top.ntutn.sparkcompose.api

import com.google.gson.annotations.SerializedName
import retrofit2.http.GET
import retrofit2.http.Path

interface StoreApi {
    companion object {
        const val BASE_URL = "https://d.store.deepinos.org.cn/"
    }
    @GET("store/{type}/applist.json")
    suspend fun getAppList(@Path("type") type: String): List<AppListItem>
}

data class AppListItem(
    @SerializedName("Name")
    val name: String = "",
    @SerializedName("Version")
    val version: String = "",
    @SerializedName("Filename")
    val fileName: String = "",
    @SerializedName("Pkgname")
    val packageName: String = "",
    @SerializedName("Author")
    val author: String = "",
    @SerializedName("Contributor")
    val contributor: String = "",
    @SerializedName("Website")
    val website: String = "",
    @SerializedName("Update")
    val updateTime: String = "",
    @SerializedName("Size")
    val size: String = "",
    @SerializedName("More")
    val description: String = "",
    @SerializedName("Tags")
    val tags: String = "",
    @SerializedName("img_urls")
    val imageUrls: String = "",
    @SerializedName("icons")
    val icons: String = "",
)