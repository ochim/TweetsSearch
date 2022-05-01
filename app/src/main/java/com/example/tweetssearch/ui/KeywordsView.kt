package com.example.tweetssearch.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.android.material.composethemeadapter.MdcTheme

@Composable
fun KeywordsList(keywords: List<String>, onItemClick: (String) -> Unit) {
    val listState = rememberLazyListState()

    MdcTheme {
        LazyColumn(
            state = listState,
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(items = keywords) {
                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onItemClick(it) },
                    style = MaterialTheme.typography.h6,
                    text = it,
                )
            }
        }
    }
}
