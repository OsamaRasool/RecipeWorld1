package ie.wit.fragments

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import ie.wit.R
import ie.wit.main.RecipeApp
import ie.wit.models.RecipeModel
import kotlinx.android.synthetic.main.fragment_recipe.*
import kotlinx.android.synthetic.main.fragment_recipe.view.*


class RecipeFragment : Fragment() {

    lateinit var app: RecipeApp
    var recipe = RecipeModel()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        app = activity?.application as RecipeApp
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val root = inflater.inflate(R.layout.fragment_recipe, container, false)
        activity?.title = getString(R.string.action_Recipe)


        setButtonListener(root)
        return root;
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            RecipeFragment().apply {
                arguments = Bundle().apply {}
            }
    }

    fun setButtonListener(layout: View) {
        layout.button_addRecipe.setOnClickListener {

            recipe.ingredients = recipeIngredients.text.toString()
            recipe.recipeExplain = recipeExplain.text.toString()
            if (recipe.name.isNotEmpty())
            else {
                app.recipesStore.create(
                    RecipeModel(
                        name = recipeName.text.toString(),
                        ingredients = recipeIngredients.text.toString(),
                        recipeExplain = recipeExplain.text.toString()
                    )
                )


            }

        }




    }
}