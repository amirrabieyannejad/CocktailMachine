package com.example.cocktailmachine.data.db;

import com.example.cocktailmachine.data.Recipe;
import com.example.cocktailmachine.data.db.elements.DataBaseElement;
import com.example.cocktailmachine.data.db.elements.SQLImageUrlElement;
import com.example.cocktailmachine.data.db.elements.SQLIngredientPump;

import java.util.LinkedList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Helper<T extends DataBaseElement> {
    private static final String TAG = "Helper";




    public List<Object> getIdsExtend(List<? extends T> elements) {
        List<Object> res = new LinkedList<>();
        for(int i=0; i<elements.size(); i++){
            res.add(elements.get(i).getID());
        }
        return res;
    }





    //Statics

    public static List<String> getUrls(List<? extends SQLImageUrlElement> urls){
        List<String> res = new LinkedList<>();
        for(SQLImageUrlElement u: urls){
            res.add(u.getUrl());
        }
        return res;
    }



    public static SQLIngredientPump getWithPumpID(List<SQLIngredientPump>  items,long pump){
        for(SQLIngredientPump item: items){
            if(item.getPumpID()==pump){
                return item;
            }
        }
        return null;
    }

    public static SQLIngredientPump getWithIngredientID(List<SQLIngredientPump>  items,long ingredient){
        for(SQLIngredientPump item: items){
            if(item.getIngredientID()==ingredient){
                return item;
            }
        }
        return null;
    }

    public static String stringAppender(HashMap<String, String> map){
        StringBuilder res = new StringBuilder();
        for(Map.Entry<String, String> entry: map.entrySet()){
            res.append(entry.getKey());
            res.append(" ");
            res.append(entry.getValue());
            res.append(",");
        }
        return res.toString();
    }


    public static String objToString(List<? extends Object> objs, String prefix, String joiner, String suffix){
        StringBuilder res = new StringBuilder();
        res.append(prefix);
        for(Object obj: objs){
            res.append(obj.toString());
            res.append(joiner);
        }
        res.append(suffix);
        return res.toString();
    }




    public static Helper<Recipe> getRecipeHelper(){
        return new Helper<Recipe>();
    }



}
