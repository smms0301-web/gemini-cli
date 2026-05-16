package com.mobiapp.util

object FuzzySearch {

    private fun levenshtein(a: String, b: String): Int {
        val dp = Array(a.length + 1) { IntArray(b.length + 1) }
        for (i in 0..a.length) dp[i][0] = i
        for (j in 0..b.length) dp[0][j] = j
        for (i in 1..a.length) {
            for (j in 1..b.length) {
                dp[i][j] = if (a[i - 1] == b[j - 1]) dp[i - 1][j - 1]
                else minOf(dp[i - 1][j], dp[i][j - 1], dp[i - 1][j - 1]) + 1
            }
        }
        return dp[a.length][b.length]
    }

    fun score(query: String, target: String): Float {
        if (query.isBlank()) return 1f
        val q = query.lowercase().trim()
        val t = target.lowercase()
        if (t.contains(q)) return 1f
        val words = t.split("\\s+".toRegex())
        val bestWord = words.minOfOrNull { levenshtein(q, it) } ?: levenshtein(q, t)
        val maxLen = maxOf(q.length, words.maxOfOrNull { it.length } ?: t.length)
        return 1f - bestWord.toFloat() / maxLen.toFloat()
    }

    fun matches(query: String, target: String, threshold: Float = 0.5f): Boolean {
        if (query.isBlank()) return true
        return score(query, target) >= threshold
    }

    fun matchesAll(tokens: List<String>, target: String, threshold: Float = 0.5f): Boolean {
        return tokens.all { matches(it, target, threshold) }
    }
}
