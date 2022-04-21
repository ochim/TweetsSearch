package com.example.tweetssearch

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.tweetssearch.adapter.KeywordAdapter
import com.example.tweetssearch.adapter.TweetAdapter
import com.example.tweetssearch.component.LoadingDialog
import com.example.tweetssearch.database.Database
import com.example.tweetssearch.databinding.FragmentHomeBinding
import com.example.tweetssearch.model.TweetNetworkModelState
import com.example.tweetssearch.repository.AccessTokenInterface
import com.example.tweetssearch.repository.AccessTokenRepository
import com.example.tweetssearch.repository.KeywordsRepository
import com.example.tweetssearch.repository.TweetsRemoteDataSource
import com.example.tweetssearch.repository.TweetsSearchInterface
import com.example.tweetssearch.repository.TweetsSearchRepository
import com.example.tweetssearch.repository.TwitterRepository
import kotlinx.coroutines.Dispatchers
import timber.log.Timber


/**
 * 開始地点となるフラグメント
 */
class HomeFragment : Fragment() {
    private var loading: LoadingDialog? = null

    private var _binding: FragmentHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private val viewModel: MainViewModel by viewModels {
        MainViewModelFactory(
            TweetsSearchRepository(
                TweetsRemoteDataSource(
                    Dispatchers.IO,
                    TwitterRepository.retrofit.create(TweetsSearchInterface::class.java)
                )
            ),
            KeywordsRepository(
                Dispatchers.IO,
                Database.db?.keywordHistoryDao()
            ),
            AccessTokenRepository(
                Dispatchers.IO,
                TwitterRepository.retrofit.create(AccessTokenInterface::class.java),
                requireContext()
            )
        )
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
                editText.setText(text)
            }
            editText.clearFocus()
        }

        binding.buttonCancel.setOnClickListener { editText.clearFocus() }

        val keywordAdapter = KeywordAdapter() { keyword ->
            viewModel.tweetsSearch(keyword)
            editText.clearFocus()
            editText.setText(keyword)
        }
        keywordsRecyclerView.adapter = keywordAdapter

        viewModel.liveKeywords.observe(viewLifecycleOwner) { keywords ->
            if (keywords.isNullOrEmpty()) return@observe
            keywordAdapter.updateDataSet(keywords)
        }

        val swipeRefreshLayout: SwipeRefreshLayout = binding.swipedLayout
        val onRefreshListener = SwipeRefreshLayout.OnRefreshListener {
            val text = editText.text.toString()
            if (text.isNotEmpty()) {
                // 最新の候補に更新する
                viewModel.tweetsSearch(text)
                // 更新マークを非表示にする
                swipeRefreshLayout.isRefreshing = false
            }
        }
        // 更新時のコールバックをセットする
        swipeRefreshLayout.setOnRefreshListener(onRefreshListener)

        swipeRefreshLayout.setProgressBackgroundColorSchemeColor(Color.TRANSPARENT)
        swipeRefreshLayout.setColorSchemeColors(Color.TRANSPARENT)

        tweetsRecyclerView.adapter = TweetAdapter() { editText.clearFocus() }
        tweetsRecyclerView.addOnScrollListener(InfiniteScrollListener(tweetsRecyclerView.adapter!!))
        tweetsRecyclerView.setHasFixedSize(true)

        viewModel.liveState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is TweetNetworkModelState.Fetching -> {
                    loading = LoadingDialog.newInstance()
                    loading!!.show(parentFragmentManager, "tag")
                }
                is TweetNetworkModelState.FetchedOK -> {
                    loading?.dismiss()
                    loading = null
                    if (state.data.isNotEmpty()) {
                        (tweetsRecyclerView.adapter as TweetAdapter).updateDataSet(state.data)
                        tweetsRecyclerView.setHasFixedSize(true)
                    }
                }
                is TweetNetworkModelState.FetchedError -> {
                    loading?.dismiss()
                    loading = null
                    Toast.makeText(requireActivity(), state.exception.message, Toast.LENGTH_LONG)
                        .show()
                }
                else -> {}
            }
        }

        fun hideInputShowTweetsUI(view: View) {
            keywordsRecyclerView.visibility = View.GONE
            binding.buttonSearch.visibility = View.GONE
            binding.buttonCancel.visibility = View.GONE
            tweetsRecyclerView.visibility = View.VISIBLE
            swipeRefreshLayout.visibility = View.VISIBLE

            // hide the software keyboard
            val imm =
                requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?
            imm?.hideSoftInputFromWindow(
                view.windowToken, InputMethodManager.HIDE_NOT_ALWAYS
            )
        }

        editText.onFocusChangeListener = View.OnFocusChangeListener { view1, hasFocus ->

            if (hasFocus) {
                viewModel.loadKeywordsHistory()
                keywordsRecyclerView.visibility = View.VISIBLE
                binding.buttonSearch.visibility = View.VISIBLE
                binding.buttonCancel.visibility = View.VISIBLE
                tweetsRecyclerView.visibility = View.INVISIBLE
                swipeRefreshLayout.visibility = View.INVISIBLE
            } else {
                hideInputShowTweetsUI(view1)
            }
        }

        editText.setOnEditorActionListener { textView, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                if (textView.text.isNotEmpty()) {
                    editText.clearFocus()
                    hideInputShowTweetsUI(textView)
                    viewModel.tweetsSearch(textView.text.toString())
                }
                return@setOnEditorActionListener true
            }
            false
        }

        editText.requestFocus()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    /**
     * リストの下端までスクロールしたタイミングで発火するリスナー
     */
    inner class InfiniteScrollListener(private val adapter: RecyclerView.Adapter<RecyclerView.ViewHolder>) :
        RecyclerView.OnScrollListener() {

        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)

            // アダプターが保持しているアイテムの合計
            val itemCount = adapter.itemCount
            // 画面に表示されているアイテム数
            val childCount = recyclerView.childCount
            val manager = recyclerView.layoutManager as LinearLayoutManager
            // 画面に表示されている一番上のアイテムの位置
            val firstPosition = manager.findFirstVisibleItemPosition()

            // 何度もリクエストしないようにロード中は何もしない。
            if (loading != null && loading!!.showsDialog) {
                return
            }

            // 以下の条件に当てはまれば一番下までスクロールされたと判断できる。
            if (itemCount == childCount + firstPosition) {
                // 追加読み込みする
                Timber.d("fetch api more")
                viewModel.nextTweetsSearch()
            }
        }
    }
}

