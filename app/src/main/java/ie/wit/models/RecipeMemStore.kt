package ie.wit.models

import android.util.Log

var lastId = 0L

internal fun getId(): Long {
    return lastId++
}

class RecipeMemStore : RecipeStore {

    var recipes = ArrayList<RecipeModel>()

    override fun findAll(): List<RecipeModel> {
        return recipes
    }

    override fun findById(id:Long) : RecipeModel? {
        val foundRecipe: RecipeModel? = recipes.find { it.id == id }
        return foundRecipe
    }

    override fun create(recipe: RecipeModel) {
        recipe.id = getId()
        recipes.add(recipe)
        logAll()
    }

    override fun delete(recipe: RecipeModel) {
       recipes.remove(recipe)
    }

    fun logAll() {
        Log.v("Recipe","** All Recipes **")
        recipes.forEach { Log.v("Recipe","${it}") }
    }
}