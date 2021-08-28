package ie.wit.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ie.wit.R
import ie.wit.models.RecipeModel
import kotlinx.android.synthetic.main.card_recipe.view.*



interface RecipeListener {
    fun onRecipeClick(recipe: RecipeModel)
}

class RecipeAdapter constructor( var recipes: ArrayList<RecipeModel>,
                                 private val listener: RecipeListener)
    : RecyclerView.Adapter<RecipeAdapter.MainHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainHolder {
        return MainHolder(
            LayoutInflater.from(parent?.context).inflate(
                R.layout.card_recipe,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: MainHolder, position: Int) {
        val recipe = recipes[holder.adapterPosition]
        holder.bind(recipe,listener)
    }

    override fun getItemCount(): Int = recipes.size

    fun removeAt(position: Int) {
        recipes.removeAt(position)
        notifyItemRemoved(position)
    }

    class MainHolder constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {


        fun bind(recipe: RecipeModel, listener: RecipeListener) {
            itemView.tag = recipe
            itemView.recipeNameList.text = recipe.name
            itemView.recipeIngredientsList.text = recipe.ingredients
            itemView.recipeExplainList.text= recipe.recipeExplain
            itemView.setOnClickListener { listener.onRecipeClick(recipe) }
        }
    }
}