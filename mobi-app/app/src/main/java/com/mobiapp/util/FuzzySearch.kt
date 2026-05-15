package com.mobiapp.util

object FuzzySearch {

    fun score(query: String, target: String): Int {
        if (query.isEmpty()) return 100
        val q = query.lowercase()
        val t = target.lowercase()
        if (t.contains(q)) return 90
        if (q.length == 1) return if (t.contains(q[0])) 60 else 0
        val dist = levenshtein(q, t.take(q.length + 4))
        val maxDist = (q.length * 0.4).toInt().coerceAtLeast(1)
        return if (dist <= maxDist) (70 - dist * 15).coerceAtLeast(10) else 0
    }

    fun matches(query: String, target: String, threshold: Int = 10): Boolean =
        score(query, target) >= threshold

    fun matchesAll(tokens: List<String>, target: String): Boolean =
        tokens.all { token -> matches(token, target) }

    private fun levenshtein(a: String, b: String): Int {
        val m = a.length; val n = b.length
        val dp = Array(m + 1) { IntArray(n + 1) }
        for (i in 0..m) dp[i][0] = i
        for (j in 0..n) dp[0][j] = j
        for (i in 1..m) for (j in 1..n) {
            dp[i][j] = if (a[i - 1] == b[j - 1]) dp[i - 1][j - 1]
            else minOf(dp[i - 1][j], dp[i][j - 1], dp[i - 1][j - 1]) + 1
        }
        return dp[m][n]
    }
}
