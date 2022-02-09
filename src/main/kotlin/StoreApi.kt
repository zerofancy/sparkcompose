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
    val name: String = "Name",
    @SerializedName("Version")
    val version: String = "Version",
    @SerializedName("Filename")
    val fileName: String = "Filename",
    @SerializedName("Pkgname")
    val packageName: String = "Pkgname",
    @SerializedName("Author")
    val author: String = "Author",
    @SerializedName("Contributor")
    val contributor: String = "Contributor",
    @SerializedName("Website")
    val website: String = "Website",
    @SerializedName("Update")
    val updateTime: String = "Update",
    @SerializedName("Size")
    val size: String = "Size",
    @SerializedName("More")
    val description: String = "More",
    @SerializedName("Tags")
    val tags: String = "Tags",
    @SerializedName("img_urls")
    val imageUrls: String = "img_urls",
    @SerializedName("icons")
    val icons: String = "icons",
)