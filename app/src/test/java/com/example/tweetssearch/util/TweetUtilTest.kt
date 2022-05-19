package com.example.tweetssearch.util

import com.google.common.truth.Truth.assertThat
import org.junit.Before
import org.junit.Test

class TweetUtilTest {
    private lateinit var tweetUtil: TweetUtil

    @Before
    fun setup() {
        tweetUtil = TweetUtil()
    }

    @Test
    fun 作成日時たる引数を日本時間の形式に変換する() {
        val s = tweetUtil.convertCreatedAt("Thu Mar 25 13:50:48 +0000 2021")
        // 時差は９時間とする
        assertThat("2021/03/25 22:50:48").isEqualTo(s)
    }

    @Test
    fun 作成日時たる引数の形式が誤りの場合に例外の文字列に変換する() {
        val s = tweetUtil.convertCreatedAt("2021/03/25 22:50:48 +0900")
        assertThat("java.text.ParseException").isEqualTo(s)
    }

    @Test
    fun 作成日時たる引数が空の場合に例外の文字列に変換する() {
        val s = tweetUtil.convertCreatedAt("")
        assertThat("java.text.ParseException").isEqualTo(s)
    }

}