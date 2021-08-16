package jp.co.toshiba.iflink.fragmenttest;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;

public class MapsFragment extends Fragment {
    Double ido;
    Double keido;

    //final Double roiMeter =  0.001096640471549139;

    //ArrayList<PolygonPoints> arrayPolygonPoints = null;
    PolygonPoints currentPolygonPoints = null;
    ArrayList<Marker> markers = null;
    ImageButton btnSave;
    ImageButton btnUndo;
    GoogleMap map;

    static SharedPreferences shared;
    //static public String gpsTitle;

    public int id;

    static public GPSData gpsData;

    String TAG = "MAPFRAGMENT";

    static public final String MAPINFORMATIONDATANAME = "data2";

    //-------------------------------
    //
    // リスナー
    //
    //-------------------------------
    private MapsFragment.setGPSLitener listener;
    public interface setGPSLitener {
        void setGPSCallback(Fragment fragment, int id, GPSData gpsData);
    }
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            // 親フラグメントにイベントを送信できるように呼び出し元であるSampleFragmentオブジェクトを取得し、
            // listenerのインスタンスを生成する
            listener = (MapsFragment.setGPSLitener) getTargetFragment();
        } catch (ClassCastException e) {
            // 親フラグメントがインターフェースを実装していない場合は例外を投げる
            listener = null;
        }
    }
    //-------------------------------
    //
    // MAPS ready
    //
    //-------------------------------

    private OnMapReadyCallback callback = new OnMapReadyCallback() {

        /**
         * Manipulates the map once available.
         * This callback is triggered when the map is ready to be used.
         * This is where we can add markers or lines, add listeners or move the camera.
         * In this case, we just add a marker near Sydney, Australia.
         * If Google Play services is not installed on the device, the user will be prompted to
         * install it inside the SupportMapFragment. This method will only be triggered once the
         * user has installed Google Play services and returned to the app.
         */
        @Override
        public void onMapReady(GoogleMap googleMap) {
            map = googleMap;
            //-----------------------------------------
            // Mapの初期設定
            //-----------------------------------------
            LatLng sydney = new LatLng(ido, keido);
            googleMap.getUiSettings().setZoomGesturesEnabled(true);
            googleMap.getUiSettings().setRotateGesturesEnabled(true);

            googleMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
            googleMap.animateCamera(CameraUpdateFactory.zoomTo((float)15.0));

            currentPolygonPoints = new PolygonPoints();
            currentPolygonPoints.points = new ArrayList<>();
            if(gpsData!=null && gpsData.coord!=null && gpsData.coord.size()!=0) {
                for (GPSCood g : gpsData.coord) {
                    Point p = new Point(g.lat, g.lng);
                    currentPolygonPoints.points.add(p);
                }

                drawPolygons();
            }
            googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                @Override
                public void onMapClick(LatLng latLng) {

                }
            });
            googleMap.setOnPolygonClickListener(new GoogleMap.OnPolygonClickListener() {
                @Override
                public void onPolygonClick(Polygon polygon) {

                }
            });
            googleMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
                @Override
                public void onMapLongClick(LatLng latLng) {
                    googleMap.clear();
                    googleMap.addMarker(new MarkerOptions().position(latLng).title("Polygon"));
                    currentPolygonPoints.addLatLng(latLng);
                    int cnt = currentPolygonPoints.getCount();
                    if (cnt > 1){//2点以上あるなら線を引く
                        PolylineOptions polygonOptions = new PolylineOptions();
                        polygonOptions.color(Color.parseColor("red") );
                        polygonOptions.clickable(true);
                        ArrayList<Point> points = currentPolygonPoints.getAll();
                        int i=0;

                        for (Point point: points) {

                            LatLng w = new LatLng(point.x, point.y);
                            polygonOptions.add(w);
                        }
                        googleMap.addPolyline(polygonOptions);
                    }
                    /*
                        int pcnt = currentPolygonPoints.getCount();
                        if (pcnt>2){
                            PolylineOptions polygonOptions = new PolylineOptions();
                            polygonOptions.color(Color.parseColor("red") );
                            polygonOptions.clickable(true);
                            ArrayList<Point> points = currentPolygonPoints.getAll();
                            int i=0;

                            for (Point point: points) {

                                LatLng w = new LatLng(point.x, point.y);
                                polygonOptions.add(w);
                            }
                            googleMap.addPolyline(polygonOptions);
                        }

                     */

                }
            });
        }
    };

    void drawPolygons(){
        map.clear();
        int cnt = currentPolygonPoints.getCount();
        if (cnt > 1){
            PolylineOptions polygonOptions = new PolylineOptions();
            polygonOptions.color(Color.parseColor("red") );
            polygonOptions.clickable(true);
            ArrayList<Point> points = currentPolygonPoints.getAll();
            int i=0;

            for (Point point: points) {

                LatLng w = new LatLng(point.x, point.y);
                polygonOptions.add(w);
            }
            try {
                map.addPolyline(polygonOptions);
            }catch (Exception ex){
                Toast.makeText(getContext(),ex.getLocalizedMessage(), Toast.LENGTH_LONG).show();
            }
        }

    }



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        try{
            shared = getActivity().getSharedPreferences(BasicData.prefFile,Context.MODE_PRIVATE);

            currentPolygonPoints = new PolygonPoints();
            //currentPolygonPoints.gpsTitle = gpsTitle;
            currentPolygonPoints.rallyTitle = BasicData.currentRallyName;

        }catch (Exception ex){

        }


        return inflater.inflate(R.layout.fragment_maps, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        ido = getArguments().getDouble(GpsFragment.prefKeyIdo);
        keido = getArguments().getDouble(GpsFragment.prefKeyKeido);

        SupportMapFragment mapFragment =
                (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(callback);
        }
        //arrayPolygonPoints = new ArrayList<>();
        //currentPolygonPoints = new PolygonPoints();
        markers = new ArrayList<>();

        btnSave = (ImageButton)view.findViewById(R.id.btnSave);
        btnUndo = (ImageButton)view.findViewById(R.id.btnUndo);

        btnUndo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentPolygonPoints.remove();
                drawPolygons();
            }
        });
        //-----------------------------------------------
        //
        //ポリゴン化して保存
        //
        //-----------------------------------------------
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentPolygonPoints==null){
                    currentPolygonPoints = new PolygonPoints();
                    //return;
                }
                int cnt = currentPolygonPoints.getCount();
                if(cnt<3){
                    currentPolygonPoints = new PolygonPoints();
                    //return;
                }
                //BasicData.setGPSName(gpsTitle);
                GPSData gpsData = new GPSData();
                if(currentPolygonPoints.getCount()>=3) {
                    currentPolygonPoints.toPoligon();
                    drawPolygons();
                    ArrayList<Point> points = currentPolygonPoints.getAll();
                    for (Point point: points) {
                        gpsData.add(point.x, point.y);
                    }
                }
                if(listener!=null){
                    listener.setGPSCallback(MapsFragment.this, id, gpsData);
                }

                /*
                // 保存する
                OneRallyData one = new OneRallyData();
                one.fromBasicData(BasicData.currentRallyName);
                String a = one.toJson();
                InsertData insertData = new InsertData(ServerUrl.setData);
                insertData.execute(BasicData.mail, BasicData.rallyName.get(BasicData.currentRallyName),
                        BasicData.BASICDATA, a);
*/
                getFragmentManager().popBackStack();

            }
        });
        try {
            Bundle bundle = getArguments();
            id = bundle.getInt("id");
        }catch (Exception ex){
            Log.e(TAG, ex.getLocalizedMessage());
        }


        class Point {

            public Point(double x, double y) {
                this.x = x;
                this.y = y;
            }

            public double x;
            public double y;

            @Override
            public String toString() {
                return String.format("(%f,%f)", x, y);
            }
        }

        class PolygonPoints {
            ArrayList<Point> points;
            String title;

            public PolygonPoints(){
                points = new ArrayList<>();
            }
            public void addPoint(Double lat, Double lng){
                Point point = new Point(lat, lng);
                points.add(point);
            }
            public void remove(){
                points.remove(points.size() - 1);
            }
            public ArrayList<Point> getAll(){
                return points;
            }
            public void putTitle(String title){
                this.title = title;
            }
            public String getTitle(){
                return this.title;
            }
            public int getCount(){
                return points.size();
            }
            public void addLatLng(LatLng latLng){
                Point point = new Point(latLng.latitude, latLng.longitude);
                points.add(point);
            }
            public void toPoligon(){
                if (points.size()<3){
                    points.clear();
                    return;
                }
                Point wstart = points.get(0);
                points.add(wstart);
            }
        }
    }
    class Point {

        public Point(double x, double y) {
            this.x = x;
            this.y = y;
        }

        public double x;
        public double y;

        @Override
        public String toString() {
            return String.format("(%f,%f)", x, y);
        }
    }

    class PolygonPoints {
        public ArrayList<Point> points;
        public String gpsTitle;
        public String rallyTitle;

        public PolygonPoints(){
            points = new ArrayList<>();
        }
        public void addPoint(Double lat, Double lng){
            Point point = new Point(lat, lng);
            points.add(point);
        }
        public void remove(){
            if(points.size()>0){
                points.remove(points.size() - 1);
            }
        }
        public ArrayList<Point> getAll(){
            return points;
        }
        public void putTitle(String title){
            this.rallyTitle = title;
        }
        public void putRallyTitle(String title){
            this.rallyTitle = title;
        }
        public String getTitle(){
            return this.rallyTitle;
        }
        public String getRallyTitle() {
            return this.rallyTitle;
        }
        public int getCount(){
            return points.size();
        }
        public void addLatLng(LatLng latLng){
            Point point = new Point(latLng.latitude, latLng.longitude);
            points.add(point);
        }
        public void toPoligon(){
            if (points.size()<3){
                points.clear();
                return;
            }
            Point wstart = points.get(0);
            Point wend = points.get(points.size()-1);
            if((wstart.x == wend.x)&&(wstart.y==wend.y) ){

            }else {
                points.add(wstart);
            }
        }
    }
}