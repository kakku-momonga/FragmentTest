package jp.co.toshiba.iflink.fragmenttest;

import android.app.Activity;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;

public class Points {
    public int stamp;
    public int beep;
    public int kinbo;
    public String haikeiUrl;
    public float smallSize;
    public float mediumSize;
    public float largeSize;
    public String rallyName ;
    public String uuid;
    public HashMap<Integer,PointData> pointData;
    static public final String POINSDATA = "data5";
    static public String prefFile = "POINTS";
    static public String prefKeyData = "DATA";
    static public int workingId; /* work */
    static public  String workingStampUrl;
    static public String workingBeacon;
    public HashMap<Integer, String> gettingMessages;
    final String TAG = "Points";
    public int maxNumber;

    public Points(){
        pointData = new HashMap<>();
        gettingMessages = new HashMap<>();
        maxNumber = 0;
    }

    public void setNumber(int id){
        PointData pd = pointData.get(id);
        if(pd.myNumber>0){
            return;
        }
        maxNumber++;
        pd.myNumber = maxNumber;
    }

    public ArrayList<String> getAllMessages(){
        ArrayList<String> array=new ArrayList<>();

        for(int idx : pointData.keySet()){
            String mes = pointData.get(idx).pointMessage;
            array.add(mes);
        }
        return array;
    }
    public void DeletePoint(int id){
        if(pointData.containsKey(id)==false){
            Log.e(TAG, "delete : no such id ("+id+")");
            return;
        }
        pointData.remove(id);
    }
    public void add(int id, float x, float y, int size, String stamp, HashMap<String, Integer> qr, HashMap<String, Integer> beacon, HashMap<String, Integer> nfc){
        PointData  pd = new PointData();
        pd.x = x;
        pd.y = y;
        pd.pointSize = size;
        pd.id = id;
        pd.nfc = nfc;
        pd.stampUrl = stamp;
        pd.beacon = beacon;
        pd.qr = qr;
        pd.gps = new GPSData();
        pointData.put(id,pd);
    }
    public void setGPS(int id, GPSData gps){
        PointData pd = pointData.get(id);
        pd.gps = gps;
    }
    public void setQr(int id, String qr){
        PointData pd = pointData.get(id);
        pd.setQr(qr);
    }
    public void setBeacon(int id, String beacon){
        if(pointData.containsKey(id)==false){
            return;
        }
        PointData pd = pointData.get(id);
        pd.setBeacon(beacon);
    }
    public void setNfc(int id, String nfc){
        if(pointData.containsKey(id)==false){
            return;
        }
        PointData pd = pointData.get(id);
        pd.setNfc(nfc);
    }
    public void setStamp(int id, String stamp){
        if(pointData.containsKey(id)==false){
            return;
        }
        PointData pd = pointData.get(id);
        pd.stampUrl = stamp;
    }
    public String getStamp(int id){
        if(pointData.containsKey(id)==false){
            return "";
        }
        PointData pd = pointData.get(id);
        return pd.stampUrl;
    }

    public GPSData getGps(int id){
        if(pointData.containsKey(id)==false){
            return null;
        }
        PointData pd = pointData.get(id);
        GPSData gpsData =  pd.gps;
        return gpsData;
    }

    public void setGettingTampMessage(int id, String message){
        PointData p = pointData.get(id);
        if(p==null){
            Log.e(TAG, "no such id");
            return;
        }
        p.pointMessage = message;
    }

}

class PointData{
    public int id;
    public int pointSize;
    public float x;
    public float y;
    public String stampUrl;
    public HashMap<String, Integer> qr;
    public HashMap<String, Integer> nfc;
    public HashMap<String, Integer> beacon;
    public GPSData gps;
    public String pointMessage;
    public int myNumber;
    public String imageName="";

    public PointData(){
        myNumber = -1;
    }

    public void setNfc(String n){
        if (nfc==null){
            nfc = new HashMap<>();
        }
        nfc.put(n, 0);
    }
    public void setBeacon(String n){
        if (beacon==null){
            beacon = new HashMap<>();
        }
        beacon.put(n, 0);
    }
    public void setQr(String n){
        if (qr==null){
            qr = new HashMap<>();
        }
        qr.put(n, 0);
    }

    public ArrayList<String> getNfcArray(){
        ArrayList<String> ar = new ArrayList<>();
        if(nfc==null){
            return  ar;
        }
        for(String key : nfc.keySet()){
            ar.add(key);

        }
        return  ar;
    }
    public ArrayList<String> getBeaconArray(){
        ArrayList<String> ar = new ArrayList<>();
        if(beacon==null){
            return  ar;
        }
        for(String key : beacon.keySet()){
            ar.add(key);

        }
        return  ar;
    }
    public ArrayList<String> getQrArray(){
        ArrayList<String> ar = new ArrayList<>();
        if(qr==null){
            return  ar;
        }
        for(String key : qr.keySet()){
            ar.add(key);

        }
        return  ar;
    }
}
