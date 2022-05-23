package com.example.jetnews.data.dao

import androidx.room.*
import com.example.jetnews.model.PostData

@Dao
interface PostsDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPostData(vararg postData: PostData)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    @JvmSuppressWildcards
    fun insertPosts(posts: List<PostData>)

    @Delete
    fun delete(post: PostData)

    @Query("SELECT * FROM posts ORDER BY date DESC LIMIT 100")
    suspend fun getTop100MostRecent(): List<PostData>

    @Query("SELECT * FROM posts WHERE isFavorite = 1 ORDER BY date DESC LIMIT 100")
    suspend fun getTop100FavoritesMostRecent(): List<PostData>

    @Query("SELECT * FROM posts WHERE title = :title")
    suspend fun getPost(title: String): PostData

    @Query("SELECT * FROM posts where LOWER(title) like LOWER(:searchInput)")
    suspend fun filterPosts(searchInput: String): List<PostData>

    @Query("SELECT * FROM posts WHERE isFavorite = 1")
    suspend fun getFavorites(): List<PostData>
}
