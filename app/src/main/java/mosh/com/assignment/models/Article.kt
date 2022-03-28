package mosh.com.assignment.models

import com.google.gson.annotations.SerializedName
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.*

data class Article(
    val source: Source = Source(),
    val author: String? = null,
    val title: String = "",
    val description: String? = null,
    @SerializedName("url")
    val websiteUrl: String = "",
    @SerializedName("urlToImage")
    val imgUrl: String = "",
    @SerializedName("publishedAt")
    val publishedDate: Date = Date(),
    val content: String? = null
) {
    /**
     * Return the date as a string in 'dd/MM/yy' format
     */
    val publishedDateFormatted: String
        get() {
            val localDate = publishedDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate()
            return localDate.format(DateTimeFormatter.ofPattern("dd/MM/yy"))
        }
}

data class ArticleResponse(
    val status: String,
    val articles: List<Article>?,
)

data class Source(
    val id: String? = null,
    val name: String? = null,
)