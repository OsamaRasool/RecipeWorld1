package ie.wit.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ie.wit.R
import ie.wit.models.RecipeModel
import kotlinx.android.synthetic.main.card_recipe.view.*

class RecipeAdapter constructor(private var recipes: List<RecipeModel>)
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
        holder.bind(recipe)
    }

    override fun getItemCount(): Int = recipes.size

    class MainHolder constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bind(recipe: RecipeModel) {
            itemView.recipeNameList.text = recipe.name
            itemView.recipeIngredientsList.text = recipe.ingredients
            itemView.recipeExplainList.text= recipe.recipeExplain

        }
    }
}