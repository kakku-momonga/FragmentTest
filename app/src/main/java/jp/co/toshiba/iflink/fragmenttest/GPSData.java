package jp.co.toshiba.iflink.fragmenttest;

import java.util.ArrayList;

public class GPSData {
//    String GPSTitle;
    ArrayList<GPSCood> coord;

    public GPSData(){
        coord = new ArrayList<>();
    }
    public void add(Double lat, Double lng){
        GPSCood g = new GPSCood();
        g.lat = lat;
        g.lng = lng;

        coord.add(g);
    }
    public void undo(){
        int count = coord.size();
        if (count==0){
            return;
        }
        coord.remove(count-1);
    }
    public void clear(){
        coord = new ArrayList<>();
    }
}

class GPSCood{
    Double lat;
    Double lng;
}
