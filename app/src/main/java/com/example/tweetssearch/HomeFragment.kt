package com.example.tweetssearch

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import com.example.tweetssearch.adapter.KeywordAdapter
import com.example.tweetssearch.adapter.TweetAdapter
import com.example.tweetssearch.databinding.FragmentHomeBinding
import com.example.tweetssearch.model.dummyTweets

/**
 * 開始地点となるフラグメント
 */
class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

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
        val dummyKeywords = listOf("hoge", "foo", "piyo")
        val keywordsRecyclerView = binding.recyclerKeywords
        val tweetsRecyclerView = binding.recyclerTweets
        val editText = binding.textInputEditText

        keywordsRecyclerView.adapter = KeywordAdapter(requireActivity(), dummyKeywords) { keyword ->
            keywordsRecyclerView.visibility = View.GONE
            tweetsRecyclerView.visibility = View.VISIBLE
            editText.clearFocus()
        }

        tweetsRecyclerView.adapter = TweetAdapter(requireActivity(), dummyTweets) {}

        editText.onFocusChangeListener = View.OnFocusChangeListener { view1, hasFocus ->

            if (hasFocus) {
                keywordsRecyclerView.visibility = View.VISIBLE
                tweetsRecyclerView.visibility = View.GONE
            } else {
                // hide the software keyboard
                val imm =
                    requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?
                imm?.hideSoftInputFromWindow(
                    view1?.windowToken, InputMethodManager.HIDE_NOT_ALWAYS
                )
            }
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
    }
}