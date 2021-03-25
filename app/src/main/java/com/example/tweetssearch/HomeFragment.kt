package com.example.tweetssearch

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.tweetssearch.adapter.KeywordAdapter
import com.example.tweetssearch.adapter.TweetAdapter
import com.example.tweetssearch.component.LoadingDialog
import com.example.tweetssearch.databinding.FragmentHomeBinding
import timber.log.Timber

/**
 * 開始地点となるフラグメント
 */
class HomeFragment : Fragment() {
    private val loading = LoadingDialog.newInstance()

    private var _binding: FragmentHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private val viewModel by activityViewModels<MainViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val keywordsRecyclerView = binding.recyclerKeywords
        val tweetsRecyclerView = binding.recyclerTweets
        val editText = binding.textInputEditText

        binding.buttonSearch.setOnClickListener() {
            val text = editText.text.toString()
            if (text.isNotEmpty()) {
                viewModel.tweetsSearch(text)
                loading.show(parentFragmentManager, "tag")
            }
            editText.clearFocus()
        }

        val keywordAdapter = KeywordAdapter(requireActivity()) { keyword ->
            viewModel.tweetsSearch(keyword)
            editText.clearFocus()
            loading.show(parentFragmentManager, "tag")
        }
        keywordsRecyclerView.adapter = keywordAdapter

        viewModel.liveKeywords.observe(viewLifecycleOwner, { keywords ->
            if (keywords.isNullOrEmpty()) return@observe
            keywordAdapter.updateDataSet(keywords)
        })

        val initialTweetsAdapter = TweetAdapter(requireActivity()) { editText.clearFocus() }
        tweetsRecyclerView.adapter = initialTweetsAdapter
        tweetsRecyclerView.setHasFixedSize(true)

        // fragment_home.xmlで定義済み
        val manager = tweetsRecyclerView.layoutManager!! as LinearLayoutManager

        class RecyclerViewScrollListener(layoutManager: LinearLayoutManager) : EndlessRecyclerViewScrollListener(layoutManager){
            override fun onLoadMore(page: Int, totalItemsCount: Int, view: RecyclerView?) {
                Timber.d("page: $page $totalItemsCount")
                viewModel.nextTweetsSearch()
            }
        }
        // 一番下までスクロールしたら、onLoadMore が実行される
        tweetsRecyclerView.addOnScrollListener(RecyclerViewScrollListener(manager))

        viewModel.liveTweets.observe(viewLifecycleOwner, { tweets ->
            loading.dismiss()
            if (!tweets.isNullOrEmpty()) {
                initialTweetsAdapter.updateDataSet(tweets)
                tweetsRecyclerView.setHasFixedSize(true)
            }
        })

        editText.onFocusChangeListener = View.OnFocusChangeListener { view1, hasFocus ->

            if (hasFocus) {
                viewModel.loadKeywordsHistory()
                keywordsRecyclerView.visibility = View.VISIBLE
            } else {
                keywordsRecyclerView.visibility = View.GONE

                // hide the software keyboard
                val imm =
                    requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?
                imm?.hideSoftInputFromWindow(
                    view1?.windowToken, InputMethodManager.HIDE_NOT_ALWAYS
                )
            }
        }

        editText.requestFocus()

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
    }
}