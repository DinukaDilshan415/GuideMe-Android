package me.dinuka.guideme.helper;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class WishlistHelper {
    private static final String PREF_NAME = "wishlist_prefs";
    private static final String KEY_WISHLIST = "my_wishlist";

    public static void saveWishlist(Context context, List<HashMap<String, Object>> wishlist) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        Gson gson = new Gson();
        String json = gson.toJson(wishlist);

        editor.putString(KEY_WISHLIST, json);
        editor.apply();
    }

    public static List<HashMap<String, Object>> getWishlist(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        String json = prefs.getString(KEY_WISHLIST, null);

        if (json == null) {
            return new ArrayList<>();
        }

        Gson gson = new Gson();
        Type type = new TypeToken<List<HashMap<String, Object>>>() {}.getType();
        return gson.fromJson(json, type);
    }

    public static void removeFromWishlist(Context context, String productId) {

        List<HashMap<String, Object>> currentList = getWishlist(context);


        for (int i = 0; i < currentList.size(); i++) {
            HashMap<String, Object> item = currentList.get(i);

            String idInMap = String.valueOf(item.get("l_id"));

            if (idInMap.equals(productId)) {
                Log.d("WishlistHelper", "Found item to remove with ID: " + productId);

                currentList.remove(i);

                Toast.makeText(context, "Item removed from wishlist", Toast.LENGTH_SHORT).show();
                break;
            }
        }

        saveWishlist(context, currentList);
    }

    public static void clearWishlist(Context context) {
        SharedPreferences prefs = context.getSharedPreferences("wishlist_prefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        editor.remove("my_wishlist");

        editor.apply();
    }
}
