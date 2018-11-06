package com.hcmus.dreamers.foodmap.common;

import android.content.Context;

import com.hcmus.dreamers.foodmap.Model.Restaurant;
import com.hcmus.dreamers.foodmap.database.DBManager;

import java.util.List;

public class FoodMapManager {

    private static List<Restaurant> restaurants;

    public static Restaurant findRestaurant(int id_rest){
        int n = restaurants.size();
        for (int i = 0; i< n; i++)
        {
            if (restaurants.get(i).getId() == id_rest)
            {
                return restaurants.get(i);
            }
        }
        return null;
    }

    public static void addRestaurant(Context context, Restaurant restaurant){
        DBManager dbManager = new DBManager(context);
        restaurants.add(restaurant);
        dbManager.addRestaurant(restaurant);
        dbManager.close();
    }

    public static List<Restaurant> getRestaurants(){
        return restaurants;
    }

    public static void setRestaurants(Context context, List<Restaurant> restaurants) {
        FoodMapManager.restaurants = restaurants;

        DBManager dbManager = new DBManager(context);
        for (Restaurant rest: FoodMapManager.restaurants) {
            dbManager.addRestaurant(rest);
        }
        dbManager.close();
    }
}
