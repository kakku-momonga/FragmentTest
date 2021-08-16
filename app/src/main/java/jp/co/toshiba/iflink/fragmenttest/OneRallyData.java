package jp.co.toshiba.iflink.fragmenttest;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.HashMap;

public class OneRallyData {
    public String uuid; /* key:rallyName, value:uuie */
    public HashMap<String,GPSData> gpsData; /* key:rallyName, value:GPStitle, lat, lng */
    public Integer numberData; /* key:rallyName, value:Number */
    public String mail = "";
    public String rallylName;
    public RallyAttribute attribute;
    public String finishMessage;

    public OneRallyData(){
        gpsData = new HashMap<>();
        uuid="";
        numberData=0;
        rallylName="";
        attribute=new RallyAttribute();
        finishMessage="";
    }

    public String toJson(){
        Type type = new TypeToken<OneRallyData>() {}.getType();
        Gson gson = new GsonBuilder().create();
        JsonObject myCustomArray = gson.toJsonTree(this).getAsJsonObject();
        String a = myCustomArray.toString();
        return a;
    }

    public void toBasicData(){
        BasicData.setMail(mail);
        BasicData.rallyName.put(rallylName, uuid);
        /*BasicData.gpsData.put(rallylName, new HashMap<>());
        for(String key : gpsData.keySet()){
            GPSData g = gpsData.get(key);
            BasicData.gpsData.get(rallylName).put(key, g);
        }*/
        BasicData.numberData.put(rallylName, numberData);
        BasicData.reverseData.put(numberData, rallylName);
        BasicData.attribute.put(rallylName, attribute);
        BasicData.finishMessage.put(rallylName, this.finishMessage);
    }

    public void fromBasicData(String sRallyName){
        if(BasicData.rallyName.containsKey(sRallyName)==false){
            return;
        }
        uuid = BasicData.rallyName.get(sRallyName);
        if(BasicData.numberData.containsKey(sRallyName)){
            numberData = BasicData.numberData.get(sRallyName);
        }else{
            numberData = -1;
        }
        /*if(BasicData.gpsData.containsKey(sRallyName)){
            gpsData = BasicData.gpsData.get(sRallyName);
        }else{
            gpsData = new HashMap<>();
        }*/
        if(BasicData.attribute.containsKey(sRallyName)){
            attribute = BasicData.attribute.get(sRallyName);
        }else{
            attribute = new RallyAttribute();
        }

        mail = BasicData.mail;
        rallylName = sRallyName;
        finishMessage = BasicData.finishMessage.get(BasicData.currentRallyName);
    }

}
