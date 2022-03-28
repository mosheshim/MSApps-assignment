package mosh.com.assignment.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import mosh.com.assignment.databinding.CategoryButtonBinding
import mosh.com.assignment.enum.Categories

class CategoriesAdapter(private val onClick: (Categories) -> Unit) :
    RecyclerView.Adapter<CategoriesAdapter.ViewHolder>() {

    class ViewHolder(val binding: CategoryButtonBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            CategoryButtonBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val category = Categories.values()[position]
        holder.binding.categoryButton.apply {
            text = category.name
            setOnClickListener { onClick(category) }
        }
    }

    override fun getItemCount(): Int = Categories.values().size
}