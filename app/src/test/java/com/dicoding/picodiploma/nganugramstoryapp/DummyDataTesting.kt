package com.dicoding.picodiploma.nganugramstoryapp

import com.dicoding.picodiploma.nganugramstoryapp.data.response.ListStoryItem

object DummyDataTesting {

    fun generateDummyStories(): List<ListStoryItem> {
        val items = mutableListOf<ListStoryItem>()
        for (i in 0..10) {
            val storyItem = ListStoryItem(
                photoUrl = "https://example.com/photo_$i.jpg",
                createdAt = "2024-12-16T10:00:00Z",
                name = "Author $i",
                description = "Description $i",
                id = "id_$i",
                lat = -6.1234,
                lon = 106.1234
            )
            items.add(storyItem)
        }
        return items
    }
}
