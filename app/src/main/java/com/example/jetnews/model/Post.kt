/*
 * Copyright 2020 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.jetnews.model

import androidx.annotation.DrawableRes
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.net.URL

data class Post(
    val id: String,
    val title: String,
    val subtitle: String? = null,
    val url: String? = null,
    val publication: Publication? = null,
    val metadata: Metadata,
    val paragraphs: List<Paragraph> = emptyList(),
    val imageURL: String? = null,
    val isFavorite: Boolean? = false,
    @DrawableRes val imageThumbId: Int
)

@Entity(tableName = "posts")
data class PostData(
    @PrimaryKey val title: String,
    @ColumnInfo(name = "imageUrl") val imageURL: String? = null,
    @ColumnInfo(name = "text") val text: String? = null,
    @ColumnInfo(name = "author") var author: String? = null,
    @ColumnInfo(name = "url") val url: String? = null,
    @ColumnInfo(name = "date") val date: Long? = null,
    @ColumnInfo(name = "pubDate") val pubDate: String? = null,
    @ColumnInfo(name = "isFavorite") var isFavorite: Boolean? = false,
)

data class Metadata(
    val author: PostAuthor,
    val date: String,
    val readTimeMinutes: Int
)

data class PostAuthor(
    val name: String,
    //val url: String? = null
)

data class Publication(
    val name: String,
    val logoUrl: String
)

data class Paragraph(
    val type: ParagraphType,
    var text: String,
    val markups: List<Markup> = emptyList()
)

data class Markup(
    val type: MarkupType,
    val start: Int,
    val end: Int,
    val href: String? = null
)

enum class MarkupType {
    Link,
    Code,
    Italic,
    Bold,
}

enum class ParagraphType {
    Title,
    Caption,
    Header,
    Subhead,
    Text,
    CodeBlock,
    Quote,
    Bullet,
}
