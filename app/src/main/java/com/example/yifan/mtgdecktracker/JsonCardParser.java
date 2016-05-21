package com.example.yifan.mtgdecktracker;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Yifan on 5/20/2016.
 */
public class JsonCardParser {
    private JSONArray JsonArr;

    private NonLand card;


    public JsonCardParser(JSONArray JsonArr){
        this.JsonArr = JsonArr;
    }

    public NonLand getCardAtIndex(int index) throws JSONException {
        JSONObject jsonCard = JsonArr.getJSONObject(index);
        String name = jsonCard.getString("name");
        int cmc = jsonCard.getInt("cmc");
        String cost = jsonCard.getString("cost");
        String ImageURL =  jsonCard.getJSONArray("editions").getJSONObject(0).getString("image_url"); //editions is various reprints of the card, thats why it has its own array. For now just get the original print.
        card = new NonLand(name, cmc, cost, ImageURL);
        return (NonLand) getCard();
    }


    public JSONArray getJsonArr() {
        return JsonArr;
    }


    public Card getCard() {
        return card;
    }
}
