package com.example.jetnews.model

import org.simpleframework.xml.Attribute
import org.simpleframework.xml.Element
import org.simpleframework.xml.Root
import java.io.Serializable

@Root(name = "content", strict = false)
data class MediaContent constructor(
    @field:Element(required = false, name = "description")
    var mediaDescription: String? = null,

    @field:Element(required = false, name = "credit")
    var mediaCredit: String? = null,

    @field:Attribute(name = "url")
    var url: String? = null
) : Serializable {
    override fun toString(): String {
        return "MediaContent{" +
                "mediaDescription='" + mediaDescription + '\'' +
                "mediaCredit='" + mediaCredit + '\'' +
                "url='" + url + '\'' +
                '}'
    }
}