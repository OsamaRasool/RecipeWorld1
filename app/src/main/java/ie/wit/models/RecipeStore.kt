package ie.wit.models

interface RecipeStore {
    fun findAll(): List<RecipeModel>
    fun findById(id: Long) : RecipeModel?
    fun create(recipe: RecipeModel)
    fun delete(recipe: RecipeModel)
}