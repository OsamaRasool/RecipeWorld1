package ie.wit.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import ie.wit.R
import ie.wit.main.RecipeApp
import ie.wit.models.RecipeModel
import ie.wit.utils.createLoader
import ie.wit.utils.hideLoader
import ie.wit.utils.showLoader
import kotlinx.android.synthetic.main.fragment_edit.view.*
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info

class EditFragment : Fragment(), AnkoLogger {

    lateinit var app: RecipeApp
    lateinit var loader: AlertDialog
    lateinit var root: View
    var editRecipe: RecipeModel? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        app = activity?.application as RecipeApp

        arguments?.let {
            editRecipe = it.getParcelable("editRecipe")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        root = inflater.inflate(R.layout.fragment_edit, container, false)
        activity?.title = getString(R.string.action_edit)
        loader = createLoader(activity!!)

        Log.e("CHECK_ERROR","EDIT FRAGMENT")
        root.editrecipeName.setText(editRecipe!!.name)
        root.editrecipeIngredients.setText(editRecipe!!.ingredients)
        root.editfullRecipe.setText(editRecipe!!.recipeExplain)


        root.editUpdateButton.setOnClickListener {
            showLoader(loader, "Updating Recipe on Server...")
            updateRecipeData()
            updateRecipe(editRecipe!!.uid, editRecipe!!)
            updateUserRecipe(
                app.auth.currentUser!!.uid,
                editRecipe!!.uid, editRecipe!!
            )
        }

        return root
    }

    companion object {
        @JvmStatic
        fun newInstance(recipe: RecipeModel) =
            EditFragment().apply {
                arguments = Bundle().apply {
                    putParcelable("editrecipe", recipe)
                }
            }
    }

    fun updateRecipeData() {
        editRecipe!!.name = root.editrecipeName.text.toString()
        editRecipe!!.ingredients = root.editrecipeIngredients.text.toString()
        editRecipe!!.recipeExplain = root.editfullRecipe.text.toString()
    }

    fun updateUserRecipe(userId: String, uid: String?, recipe: RecipeModel) {
        app.database.child("user-recipes").child(userId).child(uid!!)
            .addListenerForSingleValueEvent(
                object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        snapshot.ref.setValue(recipe)
                        activity!!.supportFragmentManager.beginTransaction()
                            .replace(R.id.homeFrame, RecipeListFragment.newInstance())
                            .addToBackStack(null)
                            .commit()
                        hideLoader(loader)
                    }

                    override fun onCancelled(error: DatabaseError) {
                        info("Firebase Recipe error : ${error.message}")
                    }
                })
    }

    fun updateRecipe(uid: String?, recipe: RecipeModel) {
        app.database.child("recipes").child(uid!!)
            .addListenerForSingleValueEvent(
                object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        snapshot.ref.setValue(recipe)
                        hideLoader(loader)
                    }

                    override fun onCancelled(error: DatabaseError) {
                        info("Firebase Recipe error : ${error.message}")
                    }
                })
    }
}


