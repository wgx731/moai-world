package com.github.wgx731.kotlin

/**
 * The `Language` type defines a programming language with a name and hotness score.
 *
 * @property name The name of the language.
 * @property hotness A score from 1 to 10 of user enthusiasm. 10 = so hot right now
 */
data class Language(val name: String, val hotness: Int)

class KotlinSample {
    /**
     * @return data relating to the Kotlin {@code Language}.
     */
    fun kotlinLanguage() = Language("Kotlin", 10)
}
