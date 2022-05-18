package com.example.jetnews.model

import org.simpleframework.xml.Element
import org.simpleframework.xml.ElementList
import org.simpleframework.xml.Root
import java.io.Serializable


@Root(name = "channel", strict = false)
data class Channel constructor (
    @field:Element(name = "title", required = false)
    var title: String? = null,
    @field:Element(name = "description", required = false)
    var description: String? = null,
    @field:ElementList(inline = true, required = false, name = "item")
    var items: List<Item>? = null
) : Serializable {
    override fun toString(): String {
        return "Feed: \n[Item: \n$items]"
    }
}