package com.yifanli.mtgdecktracker.deck_data_classes;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

/**
 * Created by Yifan on 9/6/2016.
 * Used to serialize and deserialize json from internal storage
 */
public class JsonSerialerDeSerializer implements JsonSerializer<Card>, JsonDeserializer<Card>{
    private static final String LOG_TAG = JsonSerialerDeSerializer.class.getSimpleName();
    @Override
    public JsonElement serialize(Card src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject element = new JsonObject();
        element.addProperty("type", src.getClass().getSimpleName());
        element.add("data", context.serialize(src, src.getClass()));
        /*Log.i(LOG_TAG, "serializing card with custom serializer. \ntype: " + src.getClass().getSimpleName());
        Log.i(LOG_TAG, element.toString());*/
        return element;
    }

    @Override
    public Card deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonObj = json.getAsJsonObject();
        Gson gson = new GsonBuilder().create();
        /*String printOut = gson.toJson(jsonObj);
        Log.i(LOG_TAG, printOut);*/
        String type = jsonObj.get("type").getAsString();
        JsonElement dataPart = jsonObj.get("data");
        if(type.equals(BasicLand.class.getSimpleName())){
            Type basicLandType = new TypeToken<BasicLand>(){}.getType();
            return context.deserialize(dataPart, basicLandType);
        }
        else if(type.equals(NonBasicLand.class.getSimpleName())){
            Type nonBasicLandType = new TypeToken<NonBasicLand>(){}.getType();
            return context.deserialize(dataPart, nonBasicLandType);
        }
        else{
            throw new UnsupportedOperationException("Error: card type is not a basic or nonbasic land");
        }
    }


}
