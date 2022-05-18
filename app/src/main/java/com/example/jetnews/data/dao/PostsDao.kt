package com.example.jetnews.data.dao

import androidx.room.*
import com.example.jetnews.model.PostData

@Dao
interface PostsDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPostData(vararg postData: PostData)

    @Delete
    fun delete(post: PostData)

    @Query("SELECT * FROM posts")
    fun getAll(): List<PostData>

    @Query("SELECT * FROM posts WHERE title = :title")
    suspend fun getPost(title: String): PostData

    @Query("SELECT * FROM posts WHERE isFavorite = 1")
    suspend fun getFavorites(): List<PostData>
}
