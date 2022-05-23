package com.example.jetnews.data.posts.impl

import android.content.Context
import androidx.room.Room
import com.example.jetnews.R
import com.example.jetnews.data.AppDatabase
import com.example.jetnews.data.Result
import com.example.jetnews.data.posts.PostsRepository
import com.example.jetnews.data.service.ApiService
import com.example.jetnews.model.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.simplexml.SimpleXmlConverterFactory
import java.time.*
import java.time.format.DateTimeFormatter
import kotlin.concurrent.thread


@OptIn(ExperimentalCoroutinesApi::class)
class RssPostsRepository(appContext: Context) : PostsRepository {
    val url = "https://www.sandiegouniontribune.com/news/rss2.0.xml";
    private val favorites = MutableStateFlow<Set<String>>(setOf())
    val retrofit = Retrofit.Builder()
        // This is a fake URL, not used.
        .baseUrl("https://www.nbcsandiego.com/?rss=y/")
        .addConverterFactory(SimpleXmlConverterFactory.create())
        .build();
    val feedAPI: ApiService = retrofit.create(ApiService::class.java)
    private val db = Room.databaseBuilder(
        appContext,
        AppDatabase::class.java, "AppDatabase"
    ).build()

    private val mutex = Mutex()

    override suspend fun getPost(postId: String?): Result<Post> {
        return withContext(Dispatchers.IO) {
            val postData : PostData = db.postsDao().getPost(postId!!)
            Result.Success(postDataToPost(listOf(postData))[0]);
        }
    }

    override suspend fun getFilteredPostsFeed(searchInput: String): Result<PostsFeed> {
        return withContext(Dispatchers.IO) {
            val postsData = db.postsDao().filterPosts(searchInput = "%$searchInput%")
            val postList: List<Post> = postDataToPost(postsData)
            val postsFeed = PostsFeed(
                emptyPost,
                postList,
            )
            Result.Success(postsFeed);
        }
    }

    override suspend fun filterFavorites(): Result<PostsFeed> {
        return withContext(Dispatchers.IO) {
            val postsData = db.postsDao().getTop100FavoritesMostRecent()
            val postList: List<Post> = postDataToPost(postsData)
            val postsFeed = PostsFeed(
                emptyPost,
                postList,
            )
            Result.Success(postsFeed);
        }
    }

    override suspend fun getPostsFeed(): Result<PostsFeed> {
        return withContext(Dispatchers.IO) {
            val response = feedAPI.rss(url)?.execute();
            val items1: List<Item>? = response?.body()?.channels?.get(0)?.items
            val postList1: List<PostData> = itemToPostData(items1)
            db.postsDao().insertPosts(postList1)
            val items: List<PostData> = db.postsDao().getTop100MostRecent()
            val postList: List<Post> = postDataToPost(items)
            val postsFeed = PostsFeed(
                    postList[0],
                    postList,
            )
            val favoritedPosts = mutableListOf<String>()
            for (post in items) {
                if (post.isFavorite == true) {
                    favoritedPosts.add(post.title)
                    println(post)
                }
            }
            println(favoritedPosts)
            favorites.value = favoritedPosts.toSet()
            Result.Success(postsFeed);
        }
    }

    fun itemToPostData(items: List<Item>?): List<PostData> {
        val posts: MutableList<PostData> = mutableListOf()
        if (items == null) {
            return posts
        }

        for (item in items) {
            if (item.creator == null) {
                item.creator = " "
            }
            if (item.mediaContent == null) {
                item.mediaContent = MediaContent()
            }
            val date = pubDateToDate(item.pubDate!!)
            posts.add(
                PostData(
                    title = item.title!!,
                    author = item.creator!!,
                    date = date.toEpochSecond(ZoneOffset.UTC),
                    pubDate = dateToPubDate(date),
                    imageURL = item.mediaContent!!.url,
                    text = item.description!!,
                    url = item.link
                ))

        }
        return posts
    }

    fun pubDateToDate(pubDate: String): LocalDateTime {
        val rfcFormatter = DateTimeFormatter.RFC_1123_DATE_TIME
        return LocalDateTime.parse(pubDate, rfcFormatter)
    }

    fun dateToPubDate(date: LocalDateTime): String {
        val formatter = DateTimeFormatter.ofPattern("E yyyy-MM-dd hh:mm:ss a");
        val zoneId = ZoneId.of("America/Los_Angeles")
        val zdt: ZonedDateTime = date.atZone(zoneId)
        return zdt.format(formatter)
    }

    fun postDataToPost(postData: List<PostData>?): List<Post> {
        val posts: MutableList<Post> = mutableListOf()
        if (postData == null) {
            return posts
        }

        for (item in postData) {
            if (item.author == null) {
                item.author = " "
            }
            posts.add(
                Post(id = item.title!!,
                title = item.title!!,
                metadata = Metadata(
                    author = PostAuthor(item.author!!),
                    date = item.pubDate!!,
                    readTimeMinutes = if (item.text != null && item.text.length > 100) item.text.length / 100 else 1
                ),
                imageURL = item.imageURL,
                imageThumbId = R.drawable.post_1_thumb,
                paragraphs = listOf(Paragraph(
                    type = ParagraphType.Text,
                    text = item.text!!
                )),
                url = item.url
            ))

        }
        return posts
    }

    override fun observeFavorites(): Flow<Set<String>> {
        return favorites
    }

    override suspend fun toggleFavorite(postId: String) {
        // Here you insert postId (title of post) into the favorites
        mutex.withLock {
            val post = db.postsDao().getPost(postId)
            post.isFavorite = post.isFavorite != true
            db.postsDao().insertPostData(post)
            val posts = db.postsDao().getFavorites()
            val favoritedPosts = posts.map { it.title }
            favorites.value = favoritedPosts.toSet()
        }
    }
}