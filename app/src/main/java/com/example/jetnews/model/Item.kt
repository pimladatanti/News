package com.example.jetnews.model

import org.simpleframework.xml.Element
import org.simpleframework.xml.Root
import java.io.Serializable

@Root(name = "item", strict = false)
data class Item constructor(
    @field:Element(required = false, name = "title")
    var title: String? = null,

    @field:Element(required = false, name = "creator")
    var creator: String? = null,

    @field:Element(required = false, name = "pubDate")
    var pubDate: String? = null,

    @field:Element(required = false, name = "description")
    var description: String? = null,

    @field:Element(name = "link")
    var link: String? = null,



    //@Element(required = false, name = "media:content")
    //private String mediaContent;
    @field:Element(required = false, name = "content")
    var mediaContent: MediaContent? = null
) : Serializable {
    override fun toString(): String {
        return """
            
            
            Item{pubDate='$pubDate', title='$title', description='$description', link='$link', mediaContent='$mediaContent'}
            ----------------------------------------------------------------------------------------------- 
            
            """.trimIndent()
    }
}