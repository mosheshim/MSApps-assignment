package mosh.com.assignment.ui

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.ImageViewCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import mosh.com.assignment.ExtensionsUtils.Companion.toast
import mosh.com.assignment.R
import mosh.com.assignment.databinding.FragmentArticlePageBinding
import mosh.com.assignment.models.Article

class FragmentArticlePage : Fragment() {

    private var _binding: FragmentArticlePageBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: SharedViewModel
    private lateinit var article: Article

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewModel = ViewModelProvider(requireActivity())[SharedViewModel::class.java]

        article = viewModel.chosenArticle!!
//  Change the toolbar title to the name of the title
        requireActivity()
            .findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar)
            .title = viewModel.chosenArticle?.source?.name ?: getString(R.string.unknown_source)

        _binding = FragmentArticlePageBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            Glide.with(requireContext())
                .load(article.imgUrl)
                .placeholder(R.drawable.progress_animation)
                .error(R.drawable.no_image_available)
                .into(articleImageView)

//  If content and description empty/null, shows a default text
            if (article.description.isNullOrEmpty() && article.content.isNullOrEmpty())
                articleDescriptionTextView.text =
                    getString(R.string.no_content_text)
            else {
                articleDescriptionTextView.text = article.description
                articleContentTextView.text = article.content
            }

            articleAuthorTextView.text = article.author
            articlePublishDateTextView.text = article.publishedDateFormatted

            changeFavButtonColor()
//  If user is signed in, calls viewModel's favorite button function and shows a toast according
//  to the action that has been taken
            binding.favoriteButton.setOnClickListener {
                if (viewModel.auth.currentUser == null)
                    toast(getString(R.string.to_add_favorites_please_sign_in),Toast.LENGTH_LONG)
                else {
                    viewModel.favoriteButtonOnClick()
                    changeFavButtonColor()
                    val toastText =
                        if (viewModel.isArticleFavorite)
                            getString(R.string.added)
                        else getString(R.string.removed)
                    toast(toastText)
                }

            }
//  Send the user to the article web page
            articleWebsiteButton.setOnClickListener {
                startActivity(
                    Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse(article.websiteUrl)
                    )
                )
            }
        }
    }

    /**
     * Change the button color according to if the article is favorite or not
     */
    private fun changeFavButtonColor() {
        if (viewModel.isArticleFavorite)
            ImageViewCompat.setImageTintList(
                binding.favoriteButton, ColorStateList.valueOf(Color.rgb(214,234,9))
            )
        else ImageViewCompat.setImageTintList(
            binding.favoriteButton, ColorStateList.valueOf(Color.rgb(153,153,153))
        )
    }
}