package com.softDev.tamilentertainment.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.google.gson.Gson;
import com.softDev.tamilentertainment.model.FavouriteItem;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SharedPreference {

	public static final String PREFS_NAME = "STATIONS";
	public static final String FAVORITES = "Station_Favorite";
	
	public SharedPreference() {
		super();
	}

	// This four methods are used for maintaining favorites.
	public void saveFavorites(Context context, List<FavouriteItem> favorites) {
		SharedPreferences settings;
		Editor editor;

		settings = context.getSharedPreferences(PREFS_NAME,
				Context.MODE_PRIVATE);
		editor = settings.edit();

		Gson gson = new Gson();
		String jsonFavorites = gson.toJson(favorites);

		editor.putString(FAVORITES, jsonFavorites);

		editor.commit();
	}

	public void addFavorite(Context context, FavouriteItem favouriteItem) {
		List<FavouriteItem> favorites = getFavorites(context);
		if (favorites == null)
			favorites = new ArrayList<FavouriteItem>();
		favorites.add(favouriteItem);
		saveFavorites(context, favorites);
	}

	public void removeFavorite(Context context, FavouriteItem favouriteItem) {
		ArrayList<FavouriteItem> favorites = getFavorites(context);
		if (favorites != null) {
			favorites.remove(favouriteItem);
			saveFavorites(context, favorites);
		}
	}

	public ArrayList<FavouriteItem> getFavorites(Context context) {
		SharedPreferences settings;
		List<FavouriteItem> favorites;

		settings = context.getSharedPreferences(PREFS_NAME,
				Context.MODE_PRIVATE);

		if (settings.contains(FAVORITES)) {
			String jsonFavorites = settings.getString(FAVORITES, null);
			Gson gson = new Gson();
			FavouriteItem[] favoriteItems = gson.fromJson(jsonFavorites,
					FavouriteItem[].class);

			favorites = Arrays.asList(favoriteItems);
			favorites = new ArrayList<FavouriteItem>(favorites);
		} else
			return null;

		return (ArrayList<FavouriteItem>) favorites;
	}
}
