package ie.wit.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import ie.wit.R
import ie.wit.adapters.RecipeAdapter
import ie.wit.adapters.RecipeListener
import ie.wit.main.RecipeApp
import ie.wit.models.RecipeModel
import ie.wit.utils.*
import kotlinx.android.synthetic.main.fragment_recipe_list.view.*
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info

class RecipeListFragment : Fragment() , AnkoLogger, RecipeListener {

    lateinit var app: RecipeApp
    lateinit var loader : AlertDialog
    lateinit var root: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        app = activity?.application as RecipeApp
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        root = inflater.inflate(R.layout.fragment_recipe_list, container, false)
        activity?.title = getString(R.string.action_RecipeList)

        root.recyclerView.setLayoutManager(LinearLayoutManager(activity))
        setSwipeRefresh()

        val swipeDeleteHandler = object : SwipeToDeleteCallback(activity!!) {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val adapter = root.recyclerView.adapter as RecipeAdapter
                adapter.removeAt(viewHolder.adapterPosition)
                deleteRecipe((viewHolder.itemView.tag as RecipeModel).uid)
                deleteUserRecipe(app.auth.currentUser!!.uid,
                    (viewHolder.itemView.tag as RecipeModel).uid)
            }
        }

        val itemTouchDeleteHelper = ItemTouchHelper(swipeDeleteHandler)
        itemTouchDeleteHelper.attachToRecyclerView(root.recyclerView)

        val swipeEditHandler = object : SwipeToEditCallback(activity!!) {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                onRecipeClick(viewHolder.itemView.tag as RecipeModel)
            }
        }
        val itemTouchEditHelper = ItemTouchHelper(swipeEditHandler)
        itemTouchEditHelper.attachToRecyclerView(root.recyclerView)

        return root
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            RecipeListFragment().apply {
                arguments = Bundle().apply { }
            }
    }

    fun setSwipeRefresh() {
        root.swiperefresh.setOnRefreshListener(object : SwipeRefreshLayout.OnRefreshListener {
            override fun onRefresh() {
                root.swiperefresh.isRefreshing = true
                getAllRecipes(app.auth.currentUser!!.uid)
            }
        })
    }

    fun checkSwipeRefresh() {
        if (root.swiperefresh.isRefreshing) root.swiperefresh.isRefreshing = false
    }

    fun deleteUserRecipe(userId: String, uid: String?) {
        app.database.child("user-recipes").child(userId).child(uid!!)
            .addListenerForSingleValueEvent(
                object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        snapshot.ref.removeValue()
                    }
                    override fun onCancelled(error: DatabaseError) {
                        info("Firebase  Recipe error : ${error.message}")
                    }
                })
    }

    fun deleteRecipe(uid: String?) {
        app.database.child("recipes").child(uid!!)
            .addListenerForSingleValueEvent(
                object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        snapshot.ref.removeValue()
                    }

                    override fun onCancelled(error: DatabaseError) {
                        info("Firebase Recipe error : ${error.message}")
                    }
                })
    }

    override fun onRecipeClick(recipe: RecipeModel) {
        activity!!.supportFragmentManager.beginTransaction()
            .replace(R.id.homeFrame, EditFragment.newInstance(recipe))
            .addToBackStack(null)
            .commit()
    }

    override fun onResume() {
        super.onResume()
        getAllRecipes(app.auth.currentUser!!.uid)
    }

    fun getAllRecipes(userId: String?) {
        loader = createLoader(activity!!)
        showLoader(loader, "Downloading Recipes from Firebase")
        val recipiesList = ArrayList<RecipeModel>()
        app.database.child("user-recipes").child(userId!!)
            .addValueEventListener(object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) {
                    info("Firebase Recipe error : ${error.message}")
                }

                override fun onDataChange(snapshot: DataSnapshot) {
                    hideLoader(loader)

                        val children = snapshot.children
                        children.forEach {
                            val recipe = it.
                            getValue<RecipeModel>(RecipeModel::class.java)
                        recipiesList.add(recipe!!)
                        root.recyclerView.adapter =
                           RecipeAdapter(recipiesList, this@RecipeListFragment)
                        root.recyclerView.adapter?.notifyDataSetChanged()
                        checkSwipeRefresh()

                        app.database.child("user-recipes").child(userId)
                            .removeEventListener(this)
                    }
                }
            })
    }
}