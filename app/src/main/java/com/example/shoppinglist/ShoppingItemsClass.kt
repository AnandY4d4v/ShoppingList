import android.content.Context
import android.content.SharedPreferences
import com.example.shoppinglist.ShoppingItem
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

object ShoppingItemsManager {

    private const val PREFS_NAME = "ShoppingListPrefs"
    private const val ITEMS_KEY = "shopping_items"

    private lateinit var prefs: SharedPreferences

    fun init(context: Context) {
        prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    fun getShoppingItems(): List<ShoppingItem> {
        val json = prefs.getString(ITEMS_KEY, null)
        return if (json.isNullOrEmpty()) {
            emptyList()
        } else {
            val type = object : TypeToken<List<ShoppingItem>>() {}.type
            Gson().fromJson(json, type)
        }
    }

    fun saveShoppingItems(items: List<ShoppingItem>) {
        val json = Gson().toJson(items)
        prefs.edit().putString(ITEMS_KEY, json).apply()
    }
}
