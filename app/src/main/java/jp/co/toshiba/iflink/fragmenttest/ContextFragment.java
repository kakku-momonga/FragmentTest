package jp.co.toshiba.iflink.fragmenttest;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.lang.reflect.Type;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class ContextFragment extends Fragment implements View.OnLongClickListener,
        View.OnTouchListener, StampFragment.StampUrlListener,NfcFragment.SetNfclListener,
        MapsFragment.setGPSLitener, BeaconFragment.SetBeaconlListener, QRFragment.SetQRlListener,
        LocationListener, FinishMessageFragment.Callback {
    Activity activity;

    FrameLayout frameLayout ;

    HashMap<Integer, IdAttributes> idAttribute;

    // menu A
    List<ContextMenuItem> contextMenuItems;
    Dialog customDialog;
    LayoutInflater inflater;
    View child;
    ListView listView;
    ContextMenuAdapter adapter;
    // Touch coordinates

    float touch_x;
    float touch_y;

    // Drawables;

    Drawable c_small;
    Drawable c_medium;
    Drawable c_large;
    Drawable c_s_small;
    Drawable c_s_medium;
    Drawable c_s_large;
    Drawable c_small_dialog;
    Drawable trashcan;
    Drawable checkimage;
    Drawable qrcodeDrawable;
    Drawable nfcDrawable;
    Drawable iflink;
    Drawable beaconDrawable;
    Drawable gpsDrawable;
    Drawable stampDrawable;
    Drawable messageDrawable;
    Drawable finishDrawable;

    // widths of Drawables
    float c_small_width;
    float c_medium_width;
    float c_large_width;
    float c_s_small_width;
    float c_s_medium_width;
    float c_s_large_width;

    // heights of Drawables
    float c_small_height;
    float c_medium_height;
    float c_large_height;
    float c_s_small_height;
    float c_s_medium_height;
    float c_s_large_height;

    FrameLayout ly;
    ImageView tempview;

    static int s_auto_id = 10000;
    static int m_auto_id = 20000;
    static int l_auto_id = 30000;

    static final String TAG = "CONTEXTFRAGMENT";


    float downX = 0;
    float downY = 0;

    static int dialogFlag = -1;

    ImageView selectedImageView = null;

    public String uuidString;
    public String title;
    public String background;

    public int kinboRadio = 0;
    public int stampRadio = 0;
    public int beepRadio = 0;

    //NfcAdapter nfcAdapter;

    FloatingActionButton fabSave;

    public SharedPreferences shared ;
    static public HashMap<String, Points> pointsArray;

    View rootView;

    Double ido;
    Double keido;
    LocationManager mLocationManager;
    /** 位置情報の通知するための最小時間間隔（ミリ秒） */
    final long minTime = 1000;
    /** 位置情報を通知するための最小距離間隔（メートル）*/
    final long minDistance = 1;
    boolean locationFlag = false;

    static public final String prefKeyIdo="ido";
    static public final String prefKeyKeido = "keido";
    static public final String prefKeyTitle = "title";

    ContextFragment thisClass;

    private void callRequestLocationUpdates() {
        mLocationManager = (LocationManager) getContext().getSystemService(Context.LOCATION_SERVICE);
        String locationProvider = null;
        //複数プロパイダで処理を回すことで高速化を目指す
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            Toast.makeText(getContext(),"このアプリにGPSアクセスを許可してください", Toast.LENGTH_LONG);
            return;
        }
        if (mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, minTime, minDistance, this);
        }
        mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, minTime, minDistance, this);
    }

    //
    // ロケーション変更リスナー
    //
    @Override
    public void onLocationChanged(@NonNull Location location) {
        // 位置情報取得
        ido = location.getLatitude();
        keido = location.getLongitude();
        locationFlag=true;
    }



    public void reDraw(){
        if (pointsArray==null){
            return;
        }
        if(pointsArray.containsKey(BasicData.currentRallyName)==false){
            return;
        }
        Points p = pointsArray.get(BasicData.currentRallyName);
        for (Integer key : p.pointData.keySet()){
            PointData s = p.pointData.get(key);
            int size = s.pointSize;
            float width;
            if(size==1){width=p.smallSize;}else if(size==2){width=p.mediumSize;}else /*if(size==3)*/{width=p.largeSize;}
            s.pointSize = (int)width;
            createImageView(null, s, 0);
        }

    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        thisClass = this;

        uuidString=BasicData.rallyName.get(BasicData.currentRallyName);
        title=BasicData.currentRallyName;
        background=BasicData.attribute.get(BasicData.currentRallyName).background;
        kinboRadio=BasicData.attribute.get(BasicData.currentRallyName).kinboMode;
        stampRadio=BasicData.attribute.get(BasicData.currentRallyName).stampMode;
        beepRadio=BasicData.attribute.get(BasicData.currentRallyName).beepMode;

        idAttribute = new HashMap<>();

        shared = getActivity().getSharedPreferences(Points.prefFile, Context.MODE_PRIVATE);

        callRequestLocationUpdates();


        return inflater.inflate(R.layout.fragment_context, container, false);
    }



    //------------------------------------------------//
    //
    // ImageView作成
    //
    //------------------------------------------------//
    public void createImageView(ImageView imageView,PointData pointData,int selectFlag){
        try {
            if(pointData.myNumber==-1){
                if(pointsArray.get(BasicData.currentRallyName).pointData.containsKey(pointData)==false){
                    pointsArray.get(BasicData.currentRallyName).pointData.put(pointData.id, pointData);
                }
                pointsArray.get(BasicData.currentRallyName).setNumber(pointData.id);
            }
            boolean flag = true;
            if(imageView==null) {
                imageView = new ImageView(getContext());
                flag = false;
            }
            if (pointData==null){
                return;
            }
            /*if(selectFlag == 0 && flag==true && imageView.getId()==pointData.id){
                String drawable = pointData.imageName;
                if(drawable.equals("c_small") || drawable.equals("c_medium") || drawable.equals("c_large")){
                    return;
                }
            }*/
            imageView.setId(pointData.id);
            ConstraintLayout.LayoutParams layoutParams =
                    new ConstraintLayout.LayoutParams(
                            ConstraintLayout.LayoutParams.WRAP_CONTENT,
                            ConstraintLayout.LayoutParams.WRAP_CONTENT);

            layoutParams.matchConstraintMaxHeight = (int) pointData.pointSize;
            layoutParams.matchConstraintMaxWidth = (int) pointData.pointSize;

            layoutParams.leftMargin = (int)pointData.x;
            layoutParams.topMargin = (int)pointData.y;

            imageView.setLayoutParams(layoutParams);

            imageView.setAlpha((float) 0.8);

            try {
                String eg = "";
                if(pointData.imageName==null || pointData.imageName.equals("")) {
                    if (pointData.pointSize == (int) c_small_width) {
                        eg = "c_small";
                    }else if (pointData.pointSize == (int) c_medium_width){
                        eg = "c_medium";
                    }else{
                        eg = "c_large";
                    }
                }
                BitmapDrawable b;
                if(pointData.pointSize == (int)c_small_width){
                    if(pointData.imageName!=null && pointData.imageName.equals("c_y_small")){
                        b = (BitmapDrawable)c_s_small;
                    }else
                    if(selectFlag==0){
                        b= (BitmapDrawable) c_small;
                        pointData.imageName = "c_small";
                    }else{
                        b= (BitmapDrawable) c_s_small;
                        pointData.imageName = "c_s_small";
                    }
                }else
                if(pointData.pointSize == (int)c_medium_width){
                    if(pointData.imageName!=null && pointData.imageName.equals("c_y_medium")){
                        b = (BitmapDrawable)c_s_medium;
                    }else
                    if(selectFlag==0){
                        b= (BitmapDrawable) c_medium;
                        pointData.imageName = "c_medium";
                    }else{
                        b= (BitmapDrawable) c_s_medium;
                        pointData.imageName = "c_s_medium";
                    }
                }else{
                    if(pointData.imageName!=null && pointData.imageName.equals("c_y_large")){
                        b = (BitmapDrawable)c_s_large;
                    }else
                    if(selectFlag==0){
                        b= (BitmapDrawable) c_large;
                        pointData.imageName = "c_large";
                    }else{
                        b= (BitmapDrawable) c_s_large;
                        pointData.imageName = "c_s_large";
                    }
                }
                Bitmap bmp = b.getBitmap();
                Bitmap mutableBitmap = bmp.copy(Bitmap.Config.ARGB_8888, true);
                Canvas canvas = new Canvas(mutableBitmap);
                Paint paint = new Paint();
                paint.setColor(Color.BLUE);
                paint.setTextSize(20);
                if (pointData.pointSize==(int)c_small_width) {
                    canvas.drawText(String.valueOf(pointData.myNumber), 40, 60, paint);
                }else if(pointData.pointSize == (int)c_medium_width) {
                    canvas.drawText(String.valueOf(pointData.myNumber), 70, 80, paint);
                }else {
                    canvas.drawText(String.valueOf(pointData.myNumber), 60, 70, paint);
                }

                //canvas.drawText("adffsfasfdfaafafffaffds", 0, 10, paint);

                imageView.setImageBitmap(mutableBitmap);
            }catch (Exception ex){
                Log.e(TAG, ex.getLocalizedMessage());
            }
            if(flag==false) {
                setImageViewLongClickListener(imageView);
                setImageViewTouchListener(imageView);
            }

            if (flag == false) {
                // imageviewをレイアウトに追加する
                ly.addView(imageView);
                IdAttributes a = new IdAttributes();
                a.beacon = pointData.beacon;
                a.nfc = pointData.nfc;
                a.qr = pointData.qr;
                a.stamp = pointData.stampUrl;
                idAttribute.put(pointData.id, a);
                //ly.addView(imageView);
                save();
            }
        }catch (Exception ex){
            Log.e(TAG, ex.getLocalizedMessage());
        }
    }

    public void setImageViewLongClickListener(ImageView imageView){
        imageView.setOnLongClickListener(new View.OnLongClickListener() {

            @Override
            public boolean onLongClick(View v) {
                ImageView img = (ImageView) v;
                int id = img.getId();
                PointData pointData = pointsArray.get(BasicData.currentRallyName).pointData.get(id);
                //createImageView(img, pointData, 1);
                // 最後の一つのみ選択可能にするため。以前のはOFFする
                for (int i = 0; i < ly.getChildCount(); i++) {
                    View vv = ly.getChildAt(i);
                    if (vv instanceof ImageView) {
                        ImageView img2 = (ImageView) vv;
                        int id2 = img2.getId();
                        if (id2 != id) {
                            pointData = pointsArray.get(BasicData.currentRallyName).pointData.get(id2);
                            //createImageView(img2, pointData, 0);
                        }
                    }
                }

                return true;
            }

        });

    }

    public void setImageViewTouchListener(ImageView imageView){
        imageView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (v instanceof ImageView) {

                } else {
                    return false;
                }
                ImageView img = (ImageView) v;
                selectedImageView = img;
                Points.workingId = img.getId();
                switch (event.getAction() & MotionEvent.ACTION_MASK) {
                    case MotionEvent.ACTION_UP:
                        if(contextMenuItems==null){
                            contextMenuItems = new ArrayList<>();
                        }
                        contextMenuItems.clear();
                        contextMenuItems.add(new ContextMenuItem(checkimage, getString(R.string.pointconfirm)));
                        if(stampRadio==1) {
                            contextMenuItems.add(new ContextMenuItem(qrcodeDrawable, getString(R.string.qr)));
                        }
                        if(stampRadio==2){
                            contextMenuItems.add(new ContextMenuItem(nfcDrawable, getString(R.string.nfc)));
                        }
                        //contextMenuItems.add(new ContextMenuItem(iflink, getString(R.string.iflink)));
                        if(kinboRadio==3) { //ビーコン
                            contextMenuItems.add(new ContextMenuItem(beaconDrawable, getString(R.string.beacon)));
                        }
                        if(kinboRadio==2) { //GPS
                            contextMenuItems.add(new ContextMenuItem(gpsDrawable, getString(R.string.gps)));
                        }
                        contextMenuItems.add(new ContextMenuItem(stampDrawable, getString(R.string.stamp)));
                        contextMenuItems.add(new ContextMenuItem(messageDrawable, getString(R.string.stampgetmessage)));
                        //contextMenuItems.add(new ContextMenuItem(finishDrawable, getString(R.string.finishmessage)));
                        contextMenuItems.add(new ContextMenuItem(trashcan, getString(R.string.deletepoint)));
                        dialogFlag = 2;
                        selectedImageView = img;
                        try {
                            customDialog.show();
                        }catch (Exception ex){
                            Log.e(TAG, ex.getLocalizedMessage());
                        }

                        break;
                    case MotionEvent.ACTION_DOWN:
                        downX = event.getX();
                        downY = event.getY();
                        for (int i = 0; i < ly.getChildCount(); i++) {
                            View vv = ly.getChildAt(i);
                            if (vv instanceof ImageView) {
                                ImageView img2 = (ImageView) vv;
                                if (img != img2) {
                                    continue;
                                }
                                int id = img2.getId();
                                //Drawable da = img2.getDrawable();
                                PointData pointData = pointsArray.get(BasicData.currentRallyName).pointData.get(id);
                                //createImageView(img2, pointData, 1);
                            }
                        }

                        break;
                    case MotionEvent.ACTION_MOVE:
                        float moveX = event.getX();
                        float moveY = event.getY();
                        float dX = moveX - downX;
                        float dY = moveY - downY;
                        img.setX(img.getX() + dX);
                        img.setY(img.getY() + dY);
                        break;
                    default:
                        break;
                }
                return false;
            }
        });

    }
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        //------------------------------------------------//
        //
        // 初期処理
        //
        //------------------------------------------------//
        activity = getActivity();
        ly = (FrameLayout) view.findViewById(R.id.framelayout);
        ly.setOnLongClickListener(this);
        // Load Resources

        c_small = getResources().getDrawable(R.drawable.circle_large, null);
        c_small_width = c_small.getIntrinsicWidth();
        c_small_height = c_small.getIntrinsicWidth();


        c_medium = getResources().getDrawable(R.drawable.circle_extra_large, null);
        c_medium_width = c_medium.getIntrinsicWidth();
        c_medium_height = c_medium.getIntrinsicWidth();

        c_large = getResources().getDrawable(R.drawable.circle_middle, null);
        c_large_width = c_large.getIntrinsicWidth();
        c_large_height = c_large.getIntrinsicWidth();

        c_s_small = getResources().getDrawable(R.drawable.circle_y_large, null);
        c_s_small_width = c_s_small.getIntrinsicWidth();
        c_s_small_height = c_s_small.getIntrinsicWidth();

        c_s_medium = getResources().getDrawable(R.drawable.circle_y_extra_large, null);
        c_s_medium_width = c_s_medium.getIntrinsicWidth();
        c_s_medium_height = c_s_medium.getIntrinsicWidth();

        c_s_large = getResources().getDrawable(R.drawable.circle_y_middle, null);
        c_s_large_width = c_s_large.getIntrinsicWidth();
        c_s_large_height = c_s_large.getIntrinsicWidth();


        //------------------------------------------------//
        //
        // データチェック
        //
        //------------------------------------------------//
        if(title==null || title.equals("")){
            Snackbar.make(view, "このデータは使用できません", Snackbar.LENGTH_LONG).show();
            getFragmentManager().popBackStack();
            return;
        }
        if(uuidString==null || uuidString.equals("")){
            Snackbar.make(view, "このデータは使用できません", Snackbar.LENGTH_LONG).show();
            getFragmentManager().popBackStack();
            return;
        }

        rootView = view;
        //------------------------------------------------//
        //
        // 保存データ読み込み
        //
        //------------------------------------------------//
        String sPoints = shared.getString(Points.prefKeyData, "");
        if(!sPoints.equals("")){
            //pointsArray = new ArrayList<>();
            Gson gson = new GsonBuilder().create();
            Type type = new TypeToken<Points>(){}.getType();
            Points points = gson.fromJson(sPoints, type);
            pointsArray = new HashMap<>();
            pointsArray.put(BasicData.currentRallyName, points);
            //for(Points p : pointsArray){
            Points p = pointsArray.get(BasicData.currentRallyName);
            for (Integer key : p.pointData.keySet()){
                PointData s = p.pointData.get(key);
                //int size = s.pointSize;
                //float width;
                //if(size==1){width=p.smallSize;}else if(size==2){width=p.mediumSize;}else /*if(size==3)*/{width=p.largeSize;}
                //ImageView img = new ImageView(getContext());
                //img.setId(key);

                createImageView(null, s, 0);
                //ly.addView(img);

            }

        }else{
            pointsArray = new HashMap<>();
            Points points = new Points();
            points.stamp = stampRadio;
            points.beep = beepRadio;
            points.haikeiUrl = background;
            points.smallSize = c_small_width;
            points.mediumSize = c_medium_width;
            points.largeSize = c_large_width;
            points.rallyName = BasicData.currentRallyName;

            pointsArray.put(BasicData.currentRallyName, points);

        }


        //------------------------------------------------//
        //
        // 保存
        //
        //------------------------------------------------//
        fabSave = view.findViewById(R.id.fabSave);
        fabSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                getFragmentManager().popBackStack();
            }
        });
        //------------------------------------------------//
        //
        // 背景画像読み込み
        //
        //------------------------------------------------//
        if(background!=null && !background.equals("")) {
            String fileName = hash(background) + ".png";
            frameLayout = view.findViewById(R.id.framelayout);
            File file = new File(getContext().getFilesDir(), fileName);

            Drawable drawable = new BitmapDrawable(BitmapFactory.decodeFile(file.getPath()));
            frameLayout.setBackground(drawable);
        }

        // menu dialog
        registerForContextMenu(ly);

        // which circle do you put on UI
        inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        child = inflater.inflate(R.layout.listview_context_menu, null);
        listView = (ListView) child.findViewById(R.id.listView_context_menu);

        // dialogにImageViewで使用するリソースを使用するとなぜか、ImageViewのレイアウトが崩れる
        // ので、別のリソースとする

        c_small_dialog = getResources().getDrawable(R.drawable.circle_small_dialog, null);

        // images for ImageView moved dialog

        //-------------------------------
        //
        // リソースLoad
        //
        //-------------------------------
        trashcan = getResources().getDrawable(R.drawable.trash, null);
        checkimage = getResources().getDrawable(R.drawable.check, null);
        qrcodeDrawable = getResources().getDrawable(R.drawable.qr, null);
        nfcDrawable = getResources().getDrawable(R.drawable.nfc, null);
        beaconDrawable = getResources().getDrawable(R.drawable.beacon, null);
        iflink = getResources().getDrawable(R.drawable.iflink, null);
        stampDrawable = getResources().getDrawable(R.drawable.stamp, null);
        gpsDrawable = getResources().getDrawable(R.drawable.gps, null);
        messageDrawable = getResources().getDrawable(R.drawable.message, activity.getTheme());
        finishDrawable = getResources().getDrawable(R.drawable.finish, activity.getTheme());

        // Dialogの設定

        contextMenuItems = new ArrayList<ContextMenuItem>();
        adapter = new ContextMenuAdapter(getContext(),
                contextMenuItems);
        listView.setAdapter(adapter);
        customDialog = new Dialog(getContext());
        customDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        customDialog.setContentView(child);


        ly.setOnTouchListener(this);


        // dialog menu item click listener

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {
                ContextMenuItem a=contextMenuItems.get(position);
                String txt = a.getText();

                int flag = dialogFlag;
                dialogFlag = -1;
                customDialog.dismiss();
                int lyid = ly.getId();
                if (flag == 1) {
                    if(position==3){
                        //
                        // ラリー完了メッセージ
                        //
                        FinishMessageFragment f = new FinishMessageFragment();
                        f.setCallbacks(thisClass);
                        replaceFragment(f);
                        return;
                    }

                    //ImageView imageView = new ImageView(getContext());
                    int width = -1;
                    int height = -1;
                    PointData pointData = new PointData();

                    // 小型サイズのスタンプ領域を置く
                    if (position == 0) {

                        // 小さいスタンプを選択した
                        pointData.id = s_auto_id++;

                        //imageView.setId(s_auto_id);
                        width = (int) c_small_width;
                        height = (int) c_small_height;
                        pointData.pointSize = width;
                    }

                    // 中間サイズのスタンプ領域を置く
                    if (position == 1) {

                        // 中くらいのスタンプを選択した

                        //imageView.setId(m_auto_id);
                        pointData.id = m_auto_id++;

                        width = (int) c_medium_width;
                        height = (int) c_medium_height;
                        pointData.pointSize = width;

                    }

                    // 大型サイズのスタンプ領域を置く
                    if (position == 2) {
                        //imageView.setId(l_auto_id++);
                        pointData.id = l_auto_id++;
                        width = (int) c_large_width;
                        height = (int) c_large_height;
                        pointData.pointSize = width;
                    }
                    //ly.addView(imageView);
                    pointData.pointSize=width;
                    pointData.x = (int) touch_x - (width / 2);;
                    pointData.y = (int) touch_y - (height / 2);
                    //pointData.id = imageView.getId();
                    createImageView(null, pointData, 0);
                    // イメージの位置とサイズを設定する
/*                    ConstraintLayout.LayoutParams layoutParams =
                            new ConstraintLayout.LayoutParams(
                                    ConstraintLayout.LayoutParams.WRAP_CONTENT,
                                    ConstraintLayout.LayoutParams.WRAP_CONTENT);

                    layoutParams.leftMargin = (int) touch_x - (width / 2);
                    layoutParams.topMargin = (int) touch_y - (height / 2);
                    layoutParams.matchConstraintMaxHeight = (int) height;
                    layoutParams.matchConstraintMaxWidth = (int) width;

                    pointData.x = layoutParams.leftMargin;
                    pointData.y = layoutParams.topMargin;

                    createImageView(null, pointData, 0);

                    /*imageView.setLayoutParams(layoutParams);

                    imageView.setAlpha((float) 0.8);

                    // imageviewをレイアウトに追加する
                    ly.addView(imageView);
                    setImageViewLongClickListener(imageView);
                    // ImageView LongTouchリスナーの設定
                    setImageViewTouchListener(imageView);

                    pointsArray.get(BasicData.currentRallyName).pointData.put(pointData.id, pointData);*/
                    //save();


                    // imageview touchリスナーの設定
                }else if (flag == 2){

                    if(pointsArray!=null && pointsArray.containsKey(BasicData.currentRallyName)){
                        Points points = pointsArray.get(BasicData.currentRallyName);
                        if(points.pointData.containsKey(selectedImageView.getId())==false){
                            points.add(selectedImageView.getId(), selectedImageView.getX(),
                                    selectedImageView.getY(), selectedImageView.getHeight(),
                                    "",
                                    new HashMap<>(),
                                    new HashMap<>(),
                                    new HashMap<>()
                                    );
                        }
                    }else {
                        if(pointsArray==null){
                            pointsArray = new HashMap<>();

                        }
                        Points p = new Points();
                        p.add(selectedImageView.getId(), selectedImageView.getX(),
                                selectedImageView.getY(), selectedImageView.getHeight(),
                                "",
                                new HashMap<>(),
                                new HashMap<>(),
                                new HashMap<>()
                        );
                        pointsArray.put(BasicData.currentRallyName, p);
                        Points points = pointsArray.get(BasicData.currentRallyName);
                        points.stamp = stampRadio;
                        points.beep = beepRadio;
                        points.haikeiUrl = background;
                        points.smallSize = c_small_width;
                        points.mediumSize = c_medium_width;
                        points.largeSize = c_large_width;
                        points.rallyName = BasicData.currentRallyName;

                    }
                    Points points = pointsArray.get(BasicData.currentRallyName);

                    /*ImageView img = (ImageView)v;
                    int mid = img.getId();
                    Drawable mda = img.getDrawable();*/
                    // ImageViewを選択したときの Menu
                    if(txt.equals(getString(R.string.pointconfirm))){
                        // 位置確定処理
                        //SelectedImageClear(-1);
                        int imgId = selectedImageView.getId();
                        PointData pointData = pointsArray.get(BasicData.currentRallyName).pointData.get(imgId);
                        pointData.x = selectedImageView.getX();
                        pointData.y = selectedImageView.getY();
                        save();

                    }else if (txt.equals(getString(R.string.qr))){
                        // QRコード関連付け
                        ArrayList<String> s = points.pointData.get(selectedImageView.getId()).getQrArray();

                        Bundle bundle = new Bundle();
                        bundle.putStringArrayList("data", s);
                        bundle.putInt("id", selectedImageView.getId());
                        QRFragment q = new QRFragment();
                        q.setArguments(bundle);
                        replaceFragment(q);
                    }else if (txt.equals(getString(R.string.nfc))){
                        // NFC関連付け
                        ArrayList<String> s = points.pointData.get(selectedImageView.getId()).getNfcArray();

                        Bundle bundle = new Bundle();
                        bundle.putStringArrayList("data", s);
                        bundle.putInt("id", selectedImageView.getId());
                        NfcFragment nf = new NfcFragment();
                        nf.id = Points.workingId;
                        nf.setArguments(bundle);
                        replaceFragment(nf);
                    }else if (txt.equals(getString(R.string.beacon))){
                        ArrayList<String> s = points.pointData.get(selectedImageView.getId()).getBeaconArray();

                        Bundle bundle = new Bundle();
                        bundle.putStringArrayList("data", s);
                        bundle.putInt("id", selectedImageView.getId());
                        BeaconFragment b = new BeaconFragment();
                        b.id = Points.workingId;
                        b.setArguments(bundle);
                        replaceFragment(b);
                    }else if (txt.equals(getString(R.string.gps))){
                        if(locationFlag==false){
                            Snackbar.make(view, "GPSが有効になるまでお待ち下さい", Snackbar.LENGTH_LONG).show();
                            return;
                        }
                        GPSData s = points.pointData.get(selectedImageView.getId()).gps;
                        MapsFragment g = new MapsFragment();
                        Bundle bundle = new Bundle();
                        bundle.putInt("id", selectedImageView.getId());
                        bundle.putDouble(prefKeyIdo, ido);
                        bundle.putDouble(prefKeyKeido, keido);
                        bundle.putString(prefKeyTitle, "");

                        MapsFragment.gpsData = s;
                        g.setArguments(bundle);
//                        bundle.putStringArrayList("data", s);
                        //g.id = Points.workingId;
                        //MapsFragment.gpsData = s;
                        replaceFragment(g);

                    } else if (txt.equals(getString(R.string.stamp))){
                        String s = points.getStamp(selectedImageView.getId());
                        Bundle bundle = new Bundle();
                        bundle.putString("stamp", s);
                        bundle.putInt("id", selectedImageView.getId());
                        StampFragment stampFragment = new StampFragment();
                        stampFragment.setArguments(bundle);
                        replaceFragment(stampFragment);
                        SelectedImageClear(-1);

                    } else if (txt.equals(getString(R.string.deletepoint))) {
                        // 削除
                        Drawable da = ifSelected(selectedImageView.getDrawable());
                        if(da!=null){
                            ly.removeView(selectedImageView);
                        }
                        Points points1 = pointsArray.get(BasicData.currentRallyName);
                        points1.pointData.remove(selectedImageView.getId());
                        ly.removeView(selectedImageView);
                        //SelectedImageClear(-1);
                        save();
                    }else if (txt.equals(getString(R.string.stampgetmessage))){
                        GetStampMessaFragment g = new GetStampMessaFragment();
                        g.id = selectedImageView.getId();
                        replaceFragment(g);
                    }
                }
            }
        });
    }

    /*
     * 引数のDrawableが選択状態BMPなら
     * 対応する日選択BMPを返す。
     * そうでなければnullを返す
     */
    public Drawable ifSelected(Drawable img){

        if (img == c_s_large){
            return c_large;
        }else if(img == c_s_medium) {
            return c_medium;
        }else if(img == c_s_small)  {
            return c_small;
        }
        return null;
    }

    /*
     * スタンプ場所の☓イメージをクリアする
     */
    public void SelectedImageClear(int exclusionImageid){
        for(int i=0; i < ly.getChildCount(); i++){
            View vv = ly.getChildAt(i);
            if (vv instanceof ImageView) {
                ImageView img = (ImageView) vv;
                int id = img.getId();
                if(id == exclusionImageid){
                    continue;
                }
                PointData pointData = pointsArray.get(BasicData.currentRallyName).pointData.get(id);
               // createImageView(img, pointData, 0);
            }
        }
    }


    @Override
    public boolean  onTouch(View v, MotionEvent event){
        try {
            touch_x = event.getX();
            touch_y = event.getY();

            //SelectedImageClear(-1);

            return false;
        }catch (Exception exception){
            Log.e(TAG, exception.getLocalizedMessage());
            return false;
        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View view, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, view, menuInfo);

        int viewId = view.getId();

        if (viewId == R.id.framelayout) {
            contextMenuItems.clear();
            contextMenuItems.add(new ContextMenuItem(c_small_dialog, getString(R.string.small)));
            contextMenuItems.add(new ContextMenuItem(c_small_dialog, getString(R.string.medium)));
            contextMenuItems.add(new ContextMenuItem(c_small_dialog, getString(R.string.large)));
            contextMenuItems.add(new ContextMenuItem(finishDrawable, getString(R.string.finishmessage)));
            dialogFlag = 1;
            customDialog.show();

        } else {

        }
    }

    @Override
    public boolean onLongClick(View v) {
        int viewId = v.getId();
        Log.i(TAG, "viewid : "+viewId);
        if (v instanceof ImageView) {
            ImageView img = (ImageView)v;
            float x = img.getX();
            float y = img.getY();
        }
        /*

         */
        return false;
    }

    static public String hash(String toHash){
        String passwordToHash = toHash;
        String generatedPassword = null;
        MessageDigest md;

        // Create MessageDigest instance for MD5
        try {
            md = MessageDigest.getInstance("SHA-256");
        }catch (Exception ex){
            //txtResult.setText(ex.getLocalizedMessage());
            Log.e(TAG, ex.getLocalizedMessage());
            return ex.getLocalizedMessage();
        }
        //Add password bytes to digest
        md.update(passwordToHash.getBytes());
        //Get the hash's bytes
        byte[] bytes = md.digest();
        //This bytes[] has bytes in decimal format;
        //Convert it to hexadecimal format
        StringBuilder sb = new StringBuilder();
        for(byte b: bytes) {
            sb.append(String.format("%02x", b&0xff) );
        }
        //Get complete hashed password in hex format
        generatedPassword = sb.toString();

        return  generatedPassword;

    }


    @Override
    public void stammpCallback(Fragment fragment, int id, String url, Drawable drawable) {
        if(url==null||url.equals("")){
            return;
        }
        IdAttributes ia = new IdAttributes();
        if (idAttribute.containsKey(id)){
            ia = idAttribute.get(id);
        }
        ia.stamp = url;
        idAttribute.put(id,ia);
        pointsArray.get(BasicData.currentRallyName).setStamp(id, url);

        Bitmap bitmap = ((BitmapDrawable)drawable).getBitmap();
        Bitmap.createScaledBitmap(bitmap, (int)pointsArray.get(BasicData.currentRallyName).pointData.get(id).x,
                (int)pointsArray.get(BasicData.currentRallyName).pointData.get(id).y, false);
        Drawable drawable1 = new BitmapDrawable(getResources(), bitmap);
        //selectedImageView.setImageDrawable(drawable1);

        save();

    }

    @Override
    public void setNfcCallback(Fragment fragment, int id, String nfc) {
        if(nfc==null||nfc.equals("")){
            return;
        }
        IdAttributes ia = new IdAttributes();
        if (idAttribute.containsKey(id)){
            ia = idAttribute.get(id);
        }
        ia.setNfc(nfc);
        idAttribute.put(id,ia);
        pointsArray.get(BasicData.currentRallyName).setNfc(id, nfc);
        save();
    }
    // 表示させるFragmentを切り替えるメソッドを定義（表示したいFragmentを引数として渡す）
    private void replaceFragment(Fragment fragment) {
        fragment.setTargetFragment(this, 2002);
        // フラグメントマネージャーの取得
        FragmentManager manager = getFragmentManager(); // アクティビティではgetSupportFragmentManager()?
        // フラグメントトランザクションの開始
        FragmentTransaction transaction = manager.beginTransaction();
        // レイアウトをfragmentに置き換え（追加）
        transaction.replace(R.id.fragmentview, fragment);
        // 置き換えのトランザクションをバックスタックに保存する
        transaction.addToBackStack(null);
        // フラグメントトランザクションをコミット
        transaction.commit();
    }

    @Override
    public void setGPSCallback(Fragment fragment, int id, GPSData gpsData) {
        if(gpsData==null){
            return;
        }
        pointsArray.get(BasicData.currentRallyName).setGPS(id, gpsData);
        save();
    }

    //
    public void save(){
        String a = toJson();
        InsertData insertData = new InsertData(ServerUrl.setData);
        insertData.execute(BasicData.mail, BasicData.rallyName.get(BasicData.currentRallyName),
                Points.POINSDATA, a);
        SharedPreferences.Editor editor = shared.edit();
        editor.putString(Points.prefKeyData, a);
        editor.apply();

        for(int id : pointsArray.get(BasicData.currentRallyName).pointData.keySet()) {
            String imageName = pointsArray.get(BasicData.currentRallyName).pointData.get(id).imageName;
            if(imageName==null || imageName.equals("")){
                continue;
            }
            if (pointsArray.get(BasicData.currentRallyName).pointData.get(id).stampUrl != null &&
                    !pointsArray.get(BasicData.currentRallyName).pointData.get(id).stampUrl.equals("")) {
                PointData pointData = pointsArray.get(BasicData.currentRallyName).pointData.get(id);
                String name = pointData.imageName;
                switch (BasicData.attribute.get(BasicData.currentRallyName).kinboMode){
                    case    1:
                        getYellowName(id, pointData, name);
                        break;
                    case 2:
                        if(pointData.qr!= null && pointData.qr.size()>0){
                            getYellowName(id, pointData, name);
                        }
                        break;
                    case 3:
                        if(pointData.beacon!= null && pointData.beacon.size()>0){
                            getYellowName(id, pointData, name);
                        }
                        break;
                }

            }
        }

    }
    public void getYellowName(int id, PointData pointData, String name){
        if(name.equals("c_y_large")||name.equals("c_y_medium")|| name.equals("c_y_small")){
            return;
        }
        String ea = "";
        if(name.equals("c_large")){
            ea = "c_y_large";
        }else if(name.equals("c_medium")){
            ea = "c_y_medium";
        }else if(name.equals("c_small")){
            ea = "c_y_small";
        }
        int id2 = id;
        ImageView img2 = null;
        for (int i = 0; i < ly.getChildCount(); i++) {
            View vv = ly.getChildAt(i);
            if (vv instanceof ImageView) {
                img2 = (ImageView) vv;
                id2 = img2.getId();
                if (id2 != id) {
                    break;
                }
            }
        }
        if(img2==null){
            return;
        }
        final ImageView finalView = img2;
        final String finalName = ea;
        final PointData finalPointData = pointData;
        new Thread(new Runnable(){

            @Override
            public void run() {
                // これは別スレッド上での処理


                // ２秒待って，タイムラグを生む
                try {
                    Thread.sleep(200);
                } catch (InterruptedException ignore) {
                }


                // UIスレッド上で，
                // ２つ目の画像を表示する（フロッピーの保存アイコン）
                activity.runOnUiThread(new Runnable(){
                    @Override
                    public void run() {
                        createImageView(finalView, pointData, 0);
                        finalView.invalidate();
                        ly.invalidate();
                    }
                });
            }

        }).start();

    }
    public String toJson(){
        Type type = new TypeToken<Points>() {}.getType();
        Gson gson = new GsonBuilder().create();
        JsonObject myCustomArray = gson.toJsonTree(pointsArray.get(BasicData.currentRallyName)).getAsJsonObject();
        String a = myCustomArray.toString();
        return a;
    }

    @Override
    public void setBeaconCallback(Fragment fragment, int id, String uuid) {
        if(uuid==null||uuid.equals("")){
            return;
        }
        IdAttributes ia = new IdAttributes();
        if (idAttribute.containsKey(id)){
            ia = idAttribute.get(id);
        }
        ia.setBeacon(uuid);
        idAttribute.put(id,ia);
        pointsArray.get(BasicData.currentRallyName).setBeacon(id, uuid);
        save();
    }

    @Override
    public void setQRCallback(Fragment fragment, int id, String url) {
        if(url==null||url.equals("")){
            return;
        }
        IdAttributes ia = new IdAttributes();
        if (idAttribute.containsKey(id)){
            ia = idAttribute.get(id);
        }
        ia.setQr(url);
        idAttribute.put(id,ia);
        pointsArray.get(BasicData.currentRallyName).setQr(id, url);
        save();
    }

    @Override
    public void done(Object obj) {
        try {
            OneRallyData one = new OneRallyData();
            one.fromBasicData(BasicData.currentRallyName);
            String a = one.toJson();

            InsertData insertData = new InsertData(ServerUrl.setData);
            insertData.execute(BasicData.mail, BasicData.rallyName.get(title), BasicData.BASICDATA, a);

           /* SharedPreferences shared1 = getActivity().getSharedPreferences(BasicData.prefFile,Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = shared1.edit();
            editor.putString(BasicData., a);
            editor.apply();
*/
        }catch (Exception ex){
            Toast.makeText(getContext(), ex.getLocalizedMessage(), Toast.LENGTH_LONG).show();
            return;
        }

        //save();
        reDraw();
    }
}
class IdAttributes {
    public String stamp;
    public HashMap<String, Integer> nfc;
    public HashMap<String, Integer> beacon;
    public HashMap<String, Integer> qr;

    public void setNfc(String value){
        if(nfc==null){
            nfc = new HashMap<>();
        }
        nfc.put(value, 0);
    }

    public void setBeacon(String value){
        if(beacon==null){
            beacon = new HashMap<>();
        }
        beacon.put(value, 0);
    }

    public void setQr(String value){
        if(qr==null){
            qr = new HashMap<>();
        }
        qr.put(value, 0);
    }

    public HashMap<String, Integer> getNfc(){
        return  nfc;
    }
    public HashMap<String, Integer> getQr(){
        return  qr;
    }
    public HashMap<String, Integer> getBeacon(){
        return  beacon;
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