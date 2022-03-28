package mosh.com.assignment.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import mosh.com.assignment.R
import mosh.com.assignment.adapters.ArticleCardViewAdapter
import mosh.com.assignment.databinding.FragmentArticlesBinding
import mosh.com.assignment.models.Article

class FragmentArticles : Fragment() {

    private var _binding: FragmentArticlesBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: SharedViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewModel = ViewModelProvider(requireActivity())[SharedViewModel::class.java]

        requireActivity().title =
            viewModel.chosenCategory!!.value.replaceFirstChar { c -> c.uppercase() }

        _binding = FragmentArticlesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//  Change the toolbar title to the name of the category chosen
        requireActivity()
            .findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar)
            .title = viewModel.chosenCategory!!.value.replaceFirstChar { c -> c.uppercase() }
        viewModel.getNewsByCategory {
            updateUi(it)
        }
    }

    /**
     * Update ui depends on the value that was passed
     * Null or empty list will show a text
     * Else will show the articles
     */
    private fun updateUi(news: List<Article>?) {
        binding.progressBar.visibility = View.GONE

        when (news?.size) {
            null -> binding.errorTextView.text = getString(R.string.server_error_text)
            0 -> binding.errorTextView.text = getString(R.string.no_news_found)
            else -> {
                binding.articlesRecyclerView.adapter = ArticleCardViewAdapter(news) {
                    viewModel.chosenArticle = it
                    findNavController().navigate(R.id.navigation_to_article_page)
                }
                binding.articlesRecyclerView.layoutManager = LinearLayoutManager(
                    requireContext(),
                    RecyclerView.VERTICAL,
                    false
                )
            }
        }
    }
}