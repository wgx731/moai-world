package com.github.wgx731.kotlin

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class SampleTest {

    @Test
    fun testMyLanguage() {
        assertEquals("Kotlin", Sample().kotlinLanguage().name)
        assertEquals(10, Sample().kotlinLanguage().hotness)
    }

}