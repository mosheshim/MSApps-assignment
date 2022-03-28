package mosh.com.assignment.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import mosh.com.assignment.api.NewsService
import mosh.com.assignment.enum.Categories
import mosh.com.assignment.firebase.FBRepository
import mosh.com.assignment.models.Article
import mosh.com.assignment.models.ArticleResponse
import retrofit2.Response
import java.lang.RuntimeException

class SharedViewModel : ViewModel() {

    private val repository = FBRepository()

    val auth get() = repository.auth

    private val newsService = NewsService.create()

    private val fetchedNews = mutableMapOf<Categories, MutableList<Article>>()

    var chosenCategory: Categories? = null

    var chosenArticle: Article? = null

    val isArticleFavorite: Boolean
        get() {
            val favList = fetchedNews[Categories.FAVORITES]
            return !favList.isNullOrEmpty() && favList.contains(chosenArticle!!)
        }

    var apiKey: String? = null
        set(value) {
            if (value.isNullOrEmpty())
                throw RuntimeException("No api key is found in gradle.properties")
            field = value
        }

//  Auth state listener for fetching favorites articles from Firebase when user is logged in
    init {
        auth.addAuthStateListener { currentAuthStatus ->
            if (currentAuthStatus.currentUser != null)
                repository.fetchFavorites {dealWithRespond(it, Categories.FAVORITES)}
        }
    }

    /**
     * Checks if the articles already been fetched,
     * if not, calls the right method according to the category
     */
    fun getNewsByCategory(onFetch: (List<Article>?) -> Unit) {
        viewModelScope.launch {
            if (fetchedNews.containsKey(chosenCategory))
                onFetch(fetchedNews[chosenCategory]!!)
            else if (chosenCategory!! == Categories.FAVORITES)
            repository.fetchFavorites {
                onFetch(dealWithRespond(it, chosenCategory!!))
            }
            else
                onFetch(fetchFromAPINews(chosenCategory!!))
        }
    }

    /**
     * Fetch articles from api
     */
    private suspend fun fetchFromAPINews(category: Categories): List<Article>? {
        val response: Response<ArticleResponse> =
            newsService.getNewsByCategory(apiKey!!, category.name)
        return if (response.isSuccessful &&
            response.body()?.status == "ok"
        )  response.body()?.articles
        else null
    }

    /**
     * Helper method
     * Save articles if not null
     */
    private fun dealWithRespond(articles:List<Article>?, category: Categories):List<Article>?{
        if (!articles.isNullOrEmpty())
            fetchedNews[category] = articles.toMutableList()
        return articles
    }

    /**
     * Manage action when favorite button is clicked.
     * Wont do any action if user is not logged in.
     * Adds or removes article from favorite list and updates the database
     */
    fun favoriteButtonOnClick() {
        if (auth.currentUser == null) return
        val favList = fetchedNews[Categories.FAVORITES]
        if (isArticleFavorite)
            favList!!.remove(chosenArticle)
        else favList!!.add(0,chosenArticle!!)
        repository.saveFavorites(favList)
    }
}

