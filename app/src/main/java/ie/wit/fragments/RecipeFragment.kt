package ie.wit.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import ie.wit.R
import ie.wit.main.RecipeApp
import ie.wit.models.RecipeModel
import ie.wit.utils.*
import kotlinx.android.synthetic.main.fragment_recipe.*
import kotlinx.android.synthetic.main.fragment_recipe.view.*
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info
import java.util.HashMap




class RecipeFragment : Fragment(), AnkoLogger {

    lateinit var app: RecipeApp
    var recipe = RecipeModel()
    lateinit var loader : AlertDialog

    lateinit var eventListener : ValueEventListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        app = activity?.application as RecipeApp
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val root = inflater.inflate(R.layout.fragment_recipe, container, false)
        loader = createLoader(activity!!)
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

            recipe.name = recipeName.text.toString().trim()
            recipe.ingredients = recipeIngredients.text.toString().trim()
            recipe.recipeExplain = recipeExplain.text.toString().trim()
            Log.e("CHECK",recipe.name.toString())
            if (recipe == null)
            {
                Log.e("SEEEE","hello")
            }
            else {
                Log.e("SEEEE","hahaha")
                writeNewRecipe(
                    RecipeModel(
                        name = recipeName.text.toString(),
                        ingredients = recipeIngredients.text.toString(),
                        recipeExplain = recipeExplain.text.toString(),
                        email = app.auth.currentUser?.email
                    )
                )
                Toast.makeText(context, "Data Submitted Successfully!", Toast.LENGTH_SHORT).show()
                recipeName.setText("")
                recipeIngredients.setText("")
                recipeExplain.setText("")
            }
        }
    }


    override fun onResume() {
        super.onResume()
        getTotalRecipes(app.auth.currentUser?.uid)
    }

    override fun onPause() {
        super.onPause()
        if (app.database.child("user-recipes") != null){
            app.database.child("user-recipes")
                .child(app.auth.currentUser!!.uid)
                .removeEventListener(eventListener)
        }else{
            Log.e("ERROR","No data")
        }

    }

    fun writeNewRecipe(recipe: RecipeModel) {
        // Create new recipe at /recipes & /recipes/$uid
        Log.e("SEEEE","writeRecipeMethodCalled")
        showLoader(loader, "Adding Recipe to Firebase")
        info("Firebase DB Reference : $app.database")
        val uid = app.auth.currentUser!!.uid
        val key = app.database.child("recipes").push().key
        if (key == null) {
            info("Firebase Error : Key Empty")
            return
        }
        recipe.uid = key
        val recipeValues = recipe.toMap()

        val childUpdates = HashMap<String, Any>()
        childUpdates["/recipes/$key"] = recipeValues
        childUpdates["/user-recipes/$uid/$key"] = recipeValues

        app.database.updateChildren(childUpdates)
        hideLoader(loader)
    }

    fun getTotalRecipes(userId: String?) {
        eventListener = object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {
                info("Firebase Recipe error : ${error.message}")
            }

            override fun onDataChange(snapshot: DataSnapshot) {

                val children = snapshot.children
                children.forEach {
                    val recipe = it.getValue<RecipeModel>(RecipeModel::class.java)

                }


            }
        }
        //app.database = FirebaseDatabase.getInstance().reference


        app.database.child("user-recipes").child(userId!!)
            .addValueEventListener(eventListener)
    }



}