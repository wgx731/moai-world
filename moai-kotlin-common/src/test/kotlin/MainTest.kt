package com.github.wgx731.kotlin

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class KotlinSampleTest {

    @Test
    fun testMyLanguage() {
        assertEquals("Kotlin", KotlinSample().kotlinLanguage().name)
        assertEquals(10, KotlinSample().kotlinLanguage().hotness)
    }

}