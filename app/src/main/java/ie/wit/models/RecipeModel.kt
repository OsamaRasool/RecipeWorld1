package ie.wit.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize


@Parcelize
data class RecipeModel (var id: Long =0,
                        var name: String = "",
                        var ingredients: String = "",
                        var recipeExplain: String = ""

) : Parcelable
