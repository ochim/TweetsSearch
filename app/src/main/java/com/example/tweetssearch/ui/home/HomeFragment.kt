package com.example.tweetssearch.ui.home

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.tweetssearch.databinding.FragmentHomeBinding
import com.example.tweetssearch.model.TweetNetworkModelState
import com.example.tweetssearch.ui.adapter.TweetAdapter
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import javax.inject.Inject


/**
 * 開始地点となるフラグメント
 */
@AndroidEntryPoint
class HomeFragment : Fragment() {
    private lateinit var loadingBar: Snackbar

    private var _binding: FragmentHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    @Inject
    lateinit var viewModel: HomeViewModel

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
        loadingBar = Snackbar.make(view, "Loading...", BaseTransientBottomBar.LENGTH_INDEFINITE)

        val keywordsRecyclerView: ComposeView = binding.recyclerKeywords
        val tweetsRecyclerView = binding.recyclerTweets
        val editText = binding.textInputEditText

        binding.buttonCancel.setOnClickListener { editText.clearFocus() }

        val keywordClick: (String) -> Unit = { keyword ->
            viewModel.tweetsSearch(keyword)
            editText.clearFocus()
            editText.setText(keyword)
        }

        keywordsRecyclerView.setContent {
            KeywordsList(keywords = viewModel.keywordsState, onItemClick = keywordClick)
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

        fun hideInputShowTweetsUI(view: View) {
            keywordsRecyclerView.visibility = View.GONE
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
                    hideInputShowTweetsUI(textView)
                    viewModel.tweetsSearch(textView.text.toString())
                }
                return@setOnEditorActionListener true
            }
            false
        }

        tweetsRecyclerView.adapter = TweetAdapter() {
            editText.clearFocus()
            viewModel.requireInputState = false
            val action = HomeFragmentDirections.actionHomeFragmentToDetailFragment(it)
            view.findNavController().navigate(action)
        }
        tweetsRecyclerView.addOnScrollListener(InfiniteScrollListener(tweetsRecyclerView.adapter!!))
        tweetsRecyclerView.setHasFixedSize(true)

        viewModel.liveState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is TweetNetworkModelState.Fetching -> {
                    loadingBar.show()
                }
                is TweetNetworkModelState.RefreshedOK -> {
                    loadingBar.dismiss()
                    if (state.data.isNotEmpty()) {
                        (tweetsRecyclerView.adapter as TweetAdapter).submitList(state.data)
                        tweetsRecyclerView.setHasFixedSize(true)
                        tweetsRecyclerView.scrollToPosition(0)
                    }
                    editText.clearFocus()
                }
                is TweetNetworkModelState.AppendedOK -> {
                    loadingBar.dismiss()
                    if (state.data.isNotEmpty()) {
                        (tweetsRecyclerView.adapter as TweetAdapter).submitList(state.data)
                        tweetsRecyclerView.setHasFixedSize(true)
                    }
                }
                is TweetNetworkModelState.FetchedError -> {
                    loadingBar.dismiss()
                    Toast.makeText(requireActivity(), state.exception.message, Toast.LENGTH_LONG)
                        .show()
                    editText.clearFocus()
                }
                else -> {}
            }
        }

        if (viewModel.requireInputState) {
            editText.requestFocus()
        } else {
            editText.clearFocus()
            hideInputShowTweetsUI(editText)
        }
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
            if (loadingBar.isShown) {
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

