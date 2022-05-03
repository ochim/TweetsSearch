package com.example.tweetssearch.util

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class TweetUtilTest {

    @Test
    fun convertCreatedAt_success() {
        val s = TweetUtil().convertCreatedAt("Thu Mar 25 13:50:48 +0000 2021")
        // 時差は９時間とする
        assertThat("2021/03/25 22:50:48").isEqualTo(s)
    }

    @Test
    fun convertCreatedAt_wrongFormat() {
        val s = TweetUtil().convertCreatedAt("2021/03/25 22:50:48 +0900")
        assertThat("java.text.ParseException").isEqualTo(s)
    }

    @Test
    fun convertCreatedAt_emptyText() {
        val s = TweetUtil().convertCreatedAt("")
        assertThat("java.text.ParseException").isEqualTo(s)
    }

}