package com.example.tweetssearch.util

import org.junit.Assert
import org.junit.Test

class TweetUtilTest {

    @Test
    fun convertCreatedAt_success() {
        val s = TweetUtil().convertCreatedAt("Thu Mar 25 13:50:48 +0000 2021")
        // 時差は９時間とする
        Assert.assertSame("2021/03/25 22:50:48" == s, true)
    }

    @Test
    fun convertCreatedAt_wrongFormat() {
        val s = TweetUtil().convertCreatedAt("2021/03/25 22:50:48 +0900")
        Assert.assertSame("format error" == s, true)
    }

    @Test
    fun convertCreatedAt_emptyText() {
        val s = TweetUtil().convertCreatedAt("")
        Assert.assertSame("format error" == s, true)
    }

}