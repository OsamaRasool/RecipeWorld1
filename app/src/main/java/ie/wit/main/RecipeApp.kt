package ie.wit.main


import android.app.Application
import android.util.Log
import ie.wit.models.RecipeMemStore

import ie.wit.models.RecipeStore

class RecipeApp : Application() {

    lateinit var recipesStore: RecipeStore

    override fun onCreate() {
        super.onCreate()
        recipesStore = RecipeMemStore()
        Log.v("Recipe","Recipe World App started")
    }
}