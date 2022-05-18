package com.example.jetnews.model

import org.simpleframework.xml.ElementList
import org.simpleframework.xml.Root
import java.io.Serializable


@Root(name = "rss", strict = false)
data class Rss constructor(
    @field:ElementList(inline = true, name = "channel")
    @param:ElementList(inline = true, name = "channel")
    var channels: List<Channel>? = null
) : Serializable {
    override fun toString(): String {
        return "Feed: \n[Channel: \n$channels]"
    }
}