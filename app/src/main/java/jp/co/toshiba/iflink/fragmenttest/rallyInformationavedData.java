package jp.co.toshiba.iflink.fragmenttest;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;

public class rallyInformationavedData {
    static public String perfKeyArray = "array";

    Activity activity;
    SharedPreferences shared;

    public HashMap<String, String> savedData;

    static public String uuidString;
    static public String title;
    static public String background;

    static public int kinboRadio = 0;
    static public int stampRadio = 0;
    static public int beepRadio = 0;

    final String keyUuidString = "uuidString";
    final String keyTitle = "title";
    final String keyBackground = "background";
    final String keyKinboRadio = "kinboRadio";
    final String keyStampRadio = "stampRadio";
    final String keyBeepRadio = "beepRadio";

    final public String TAG = "rallyInformationavedData";


    public rallyInformationavedData(Activity activity1){
        savedData = new HashMap<>();
        this.activity = activity1;
        String prefName = RallyInformationFragment.prefName;
        shared = this.activity.getSharedPreferences(prefName, Context.MODE_PRIVATE);

    }
    public void loadFromPreferences(){
        try {
            Type type = new TypeToken<HashMap<String, String>>() {
            }.getType();
            Gson gson = new GsonBuilder().create();
            String a = shared.getString(perfKeyArray, "");

            savedData = gson.fromJson(a, type);

            uuidString = savedData.get(keyUuidString);
            title = savedData.get(keyTitle);
            background = savedData.get(keyBackground);
            kinboRadio = Integer.valueOf(savedData.get(keyKinboRadio));
            stampRadio = Integer.valueOf(savedData.get(keyStampRadio));
            beepRadio = Integer.valueOf(savedData.get(keyBeepRadio));
        }catch (Exception ex){
            Log.i("loadFromPreferences : ",ex.getLocalizedMessage());

        }

    }
    public void writeToPreferences(){
        uuidString = RallyInformationFragment.uuidString;
        title = RallyInformationFragment.title;
        background = RallyInformationFragment.background;
        kinboRadio = RallyInformationFragment.kinboRadio;
        stampRadio = RallyInformationFragment.stampRadio;
        beepRadio = RallyInformationFragment.beepRadio;

        savedData.put(keyUuidString, uuidString);
        savedData.put(keyTitle, title);
        savedData.put(keyBackground, background);
        savedData.put(keyKinboRadio, String.valueOf(kinboRadio));
        savedData.put(keyStampRadio, String.valueOf(stampRadio));
        savedData.put(keyBeepRadio, String.valueOf(beepRadio));

        //Preferenceに保存する
        try {
            Gson gson = new GsonBuilder().create();
            JsonObject myCustomArray = gson.toJsonTree(savedData).getAsJsonObject();

            String a = myCustomArray.toString();

            SharedPreferences.Editor editor = shared.edit();
            editor.putString(perfKeyArray, a);
            editor.apply();
        }catch (Exception ex){
            Log.e(TAG, ex.getLocalizedMessage());
        }

    }

    public String toJson(){
        Gson gson = new Gson();
        String json = gson.toJson(savedData);
        return json;
    }

}
