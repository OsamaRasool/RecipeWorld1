package ie.wit.models

import android.os.Parcelable
import com.google.firebase.database.Exclude
import kotlinx.android.parcel.Parcelize


@Parcelize
data class RecipeModel (var uid: String? = "",
                        var name: String = "N/A",
                        var ingredients: String = "",
                        var recipeExplain: String = "",

                        var email: String? = "osamarasool@gmail.com"


) : Parcelable

{
    @Exclude
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "uid" to uid,
            "name" to name,
            "ingredients" to ingredients,
            "recipeExplain" to recipeExplain,
            "email" to email
        )
    }
}