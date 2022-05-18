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


@OptIn(ExperimentalCoroutinesApi::class)
class RssPostsRepository(appContext: Context) : PostsRepository {
    val url = "https://www.nbcsandiego.com/?rss=y/";
    private val favorites = MutableStateFlow<Set<String>>(setOf())
    val retrofit = Retrofit.Builder()
        .baseUrl("https://www.nbcsandiego.com/?rss=y/")
        .addConverterFactory(SimpleXmlConverterFactory.create())
        .build();
    val feedAPI: ApiService = retrofit.create(ApiService::class.java)
    val db = Room.databaseBuilder(
        appContext,
        AppDatabase::class.java, "AppDatabase"
    ).build()

    private val mutex = Mutex()

    override suspend fun getPost(postId: String?): Result<Post> {
        return withContext(Dispatchers.IO) {
            val response = feedAPI.rss(url)?.execute();
            val items: List<Item>? = response?.body()?.channels?.get(0)?.items
            val postList: List<Post> = itemToPost(items)
            Result.Success(postList[0]);
        }
    }

    override suspend fun getPostsFeed(): Result<PostsFeed> {
        return withContext(Dispatchers.IO) {
            val response = feedAPI.rss(url)?.execute();
            val items: List<Item>? = response?.body()?.channels?.get(0)?.items
            val postList: List<Post> = itemToPost(items)
            val postsFeed = PostsFeed(
                    postList[0],
                    postList,
                    postList,
                    postList
            )
            Result.Success(postsFeed);
        }
    }

    fun itemToPost(items: List<Item>?): List<Post> {
        val posts: MutableList<Post> = mutableListOf()
        if (items == null) {
            return posts
        }

        for (item in items) {
            if (item.creator == null) {
                item.creator = " "
            }
            posts.add(
                Post(id = item.title!!,
                title = item.title!!,
                metadata = Metadata(
                    author = PostAuthor(item.creator!!),
                    date = item.pubDate!!,
                    readTimeMinutes = 1
                ),
                //imageId = R.drawable.post_1,
                    imageURL = item.mediaContent!!.url,
                imageThumbId = R.drawable.post_1_thumb,
                paragraphs = listOf(Paragraph(
                    type = ParagraphType.Text,
                    text = item.description!!
                )),
                url = item.link
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
            if (post.isFavorite == true) {
                db.postsDao().insertPostData(PostData(title = postId, isFavorite = false))
            } else {
                db.postsDao().insertPostData(PostData(title = postId, isFavorite = true))
            }
            val posts = db.postsDao().getFavorites()
            val favoritedPosts = posts.map { it.title }
            favorites.value = favoritedPosts.toSet()
        }
    }
}