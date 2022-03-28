package mosh.com.assignment.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import mosh.com.assignment.R
import mosh.com.assignment.databinding.ArticleCardBinding
import mosh.com.assignment.models.Article

class ArticleCardViewAdapter(
    private val articles: List<Article>,
    private val onClick: (Article) -> Unit
) :
    RecyclerView.Adapter<ArticleCardViewAdapter.ViewHolder>() {

    class ViewHolder(val binding: ArticleCardBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ArticleCardBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val article = articles[position]
        holder.binding.apply {

            titlePreviewTextView.text = article.title

            publishedAtPreviewTextView.text = article.publishedDateFormatted

            Glide.with(holder.itemView)
                .load(article.imgUrl)
                .placeholder(R.drawable.progress_animation)
                .error(R.drawable.no_image_available)
                .into(imagePreviewImageView)
        }
        holder.itemView.setOnClickListener {
            onClick(article)
        }
    }

    override fun getItemCount(): Int = articles.size
}