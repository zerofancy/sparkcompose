import com.google.gson.Gson
import retrofit2.converter.gson.GsonConverterFactory

object GsonUtil {
    val gson by lazy {
        Gson()
    }

    val gsonConverter by lazy {
        GsonConverterFactory.create(gson)
    }
}