package com.example.android.dogsapp.data.domain

import org.junit.Assert.assertEquals
import org.junit.Test

class DogTest {

    @Test
    fun `displayBreedName extracts single-word breed and title-cases it`() {
        val dog = Dog("https://images.dog.ceo/breeds/akita/x.jpg")
        assertEquals("Akita", dog.displayBreedName())
    }

    @Test
    fun `displayBreedName turns hyphenated breed into spaced title case`() {
        val dog = Dog("https://images.dog.ceo/breeds/danish-swedish-farmdog/x.jpg")
        assertEquals("Danish Swedish Farmdog", dog.displayBreedName())
    }

    @Test
    fun `displayBreedName returns empty string when the url is too short`() {
        val dog = Dog("https://example.com/")
        assertEquals("", dog.displayBreedName())
    }
}
