package jp.co.toshiba.iflink.fragmenttest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

//---------------------------------------------
//
// ログインしたらmailに入れる
// ほかはスタンプラリー名称ごとの値
// 違うメアドでログインしたら全データを削除する
//
//---------------------------------------------

public class BasicData {
    static public HashMap<String, String> rallyName; /* key:rallyName, value:uuie */
    //static public HashMap<String, HashMap<String,GPSData>> gpsData; /* key:rallyName, value:GPStitle, lat, lng */
    static public HashMap<String, Integer> numberData; /* key:rallyName, value:Number */
    static public HashMap<Integer, String> reverseData; /* key:number, value:RallyName */
    static public HashMap<String, RallyAttribute> attribute;
    static public ArrayList<HashMap<String, String>> listData; /* ListViewデータ(work) */
    static public String mail = "";
    static public String currentRallyName="";
    static public HashMap<String, Boolean> beaconUUIDs; /*work */
    static public HashMap<String, String> finishMessage;

    static public final String BASICDATA = "data7";
    static public final String prefFile = "GPS";
    static public final String prefGPS = "gps";

    static public int nfcFlag = 0;
    static public String nfcUuid;

/*
    static public void setGPSName(String sGPSName){
        if(gpsData.get(currentRallyName).containsKey(sGPSName)==false){
            gpsData.get(currentRallyName).put(sGPSName, new GPSData());
        }
    }

 */
    static public ArrayList<String> getBeaconArray(){
        ArrayList<String> array = new ArrayList<>();
        if(beaconUUIDs==null){
            return array;
        }
        for(String key : beaconUUIDs.keySet()){
            array.add(key);
        }
        return array;
    }


    static public void setAttribute(int stamp, int beep, int kinbo, String back, int enab){
        RallyAttribute attr = new RallyAttribute();
        attr.stampMode = stamp;
        attr.kinboMode = kinbo;
        attr.beepMode = beep;
        attr.background = back;
        attr.enabled = enab;
        attribute.put(currentRallyName, attr);

    }
    static public void setNumber(int iNumber){
        numberData.put(currentRallyName, iNumber);
        reverseData.put(iNumber, currentRallyName);
    }
    static boolean hasNumber(){
        return numberData.containsKey(currentRallyName);
    }

    static public void setCurrentRallyName(String sRallyName){
        if (rallyName.containsKey(sRallyName)==false){
            currentRallyName = sRallyName;
            String uuid = UUID.randomUUID().toString();
            rallyName.put(sRallyName, uuid);
            //gpsData.put(sRallyName, new HashMap<>());
        }
        currentRallyName = sRallyName;
    }

    static void setMail(String mail1){
        if (mail1==null){
            return;
        }
        finishMessage = new HashMap<>();
        if(mail.equals("")){
            rallyName = new HashMap<>();
            //gpsData = new HashMap<>();
            numberData = new HashMap<>();
            reverseData = new HashMap<>();
            attribute = new HashMap<>();
            currentRallyName="";
        }
        if (mail1.equals(mail)==false){
            rallyName = new HashMap<>();
            //gpsData = new HashMap<>();
            numberData = new HashMap<>();
            reverseData = new HashMap<>();
            attribute = new HashMap<>();
            currentRallyName="";
        }
        mail = mail1;
    }
    static void clear(){
        rallyName = new HashMap<>();
        numberData = new HashMap<>();
        reverseData = new HashMap<>();
        attribute = new HashMap<>();
        currentRallyName="";
        //gpsData = new HashMap<>();
    }

    static void addUuid(String uuid){
        if(beaconUUIDs==null){
            beaconUUIDs = new HashMap<>();
        }
        beaconUUIDs.put(uuid, false);
    }

    static ArrayList<String> getUuidArray(){
        ArrayList<String> array = new ArrayList<>();
        if(beaconUUIDs==null){
            return array;
        }
        for(String uuid : beaconUUIDs.keySet()){
            array.add(uuid);
        }
        return array;
    }
}


