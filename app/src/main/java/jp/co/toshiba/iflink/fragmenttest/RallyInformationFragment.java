package jp.co.toshiba.iflink.fragmenttest;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.UUID;

public class RallyInformationFragment extends Fragment implements DownLoader.Callback{
    Button btnRegister;
    Button btnCheckBackground;
    Button btnSlash;
    EditText txtName;
    EditText txtBackgroudURL;
    RadioGroup radioKinboGroup;
    RadioGroup radioStampGroup;
    RadioGroup radioBeepGroup;
    RadioGroup radioEnableGroup;

    static public String uuidString;
    static public String title;
    static public String background;

    static public int kinboRadio = 1;
    static public int stampRadio = 1;
    static public int beepRadio = 1;
    static public int enableRadio = 2;

    static boolean shinki;

    static SharedPreferences shared;
    static public String prefName = "RallyInfo";
    static public String perfKeyRallyName = "RallyName";
    static public String prefKeyKinbo = "KinboType";
    static public String prefKeyHowtoStamp = "Stamp";
    static public String prefKeyBeep = "beep";
    static public String prefKeyBackground = "background";
    static public String prefKeyNo = "no";

    static public rallyInformationavedData savedData ;

    static public final String INFORMATIONDATANAME = "data1";

    public String ba = "";

    Boolean backgroundFlag = false;

    View vw;

    RallyInformationFragment rif;

    final String TAG = "RallyInformationFragment";

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        // 先ほどのレイアウトをここでViewとして作成します


        return inflater.inflate(R.layout.fragment_rally_information, container, false);
    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        vw = view;

        radioKinboGroup = (RadioGroup) view.findViewById(R.id.radioKinboGroup);
        radioStampGroup = (RadioGroup) view.findViewById(R.id.radioStampGroup);
        radioBeepGroup = (RadioGroup) view.findViewById(R.id.radioBeepGroup);
        radioEnableGroup = view.findViewById(R.id.radioEnableGroup);
        btnSlash = view.findViewById(R.id.slash);

        if(BasicData.currentRallyName!=null && !BasicData.currentRallyName.equals("")  &&BasicData.attribute.containsKey(BasicData.currentRallyName)){
            kinboRadio = BasicData.attribute.get(BasicData.currentRallyName).kinboMode;
            switch (kinboRadio){
                case 1:
                    radioKinboGroup.check(R.id.radioKinboNone);
                    break;
                case 2:
                    radioKinboGroup.check(R.id.radioGPS);
                    break;
                case 3:
                    radioKinboGroup.check(R.id.radioBeacon);
                    break;
                default:
                    Toast.makeText(getContext(), "kinboMode error("+kinboRadio+")", Toast.LENGTH_LONG).show();
                    Log.e(TAG, "kinboMode error("+kinboRadio+")");
                    kinboRadio = 1;
                    radioKinboGroup.check(R.id.radioKinboNone);
                    break;
            }
        }else {
            radioKinboGroup.check(R.id.radioKinboNone);
            kinboRadio = 1;
        }
        if(BasicData.currentRallyName!=null && !BasicData.currentRallyName.equals("")  &&BasicData.attribute.containsKey(BasicData.currentRallyName)){
            stampRadio = BasicData.attribute.get(BasicData.currentRallyName).stampMode;
            switch (stampRadio){
                case 1:
                    radioStampGroup.check(R.id.radioQR);
                    break;
                case 2:
                    radioStampGroup.check(R.id.radioNFC);
                    break;
                case 3:
                    radioStampGroup.check(R.id.radioStamp);
                    break;
                case 4:
                    radioStampGroup.check(R.id.radioButton);
                    break;
                default:
                    Toast.makeText(getContext(), "stampMode error("+stampRadio+")", Toast.LENGTH_LONG).show();
                    Log.e(TAG, "stampMode error("+stampRadio+")");
                    stampRadio = 1;
                    radioStampGroup.check(R.id.radioQR);
                    break;
            }
        }else {
            radioStampGroup.check(R.id.radioQR);
            stampRadio = 1;
        }

        if(BasicData.currentRallyName!=null && !BasicData.currentRallyName.equals("")  &&BasicData.attribute.containsKey(BasicData.currentRallyName)){
            beepRadio = BasicData.attribute.get(BasicData.currentRallyName).beepMode;
            switch (beepRadio){
                case 1:
                    radioBeepGroup.check(R.id.radioNoneBeep);
                    break;
                case 2:
                    radioBeepGroup.check(R.id.radioBeep);
                    break;
                case 3:
                    radioBeepGroup.check(R.id.radioBeepAndVibration);
                    break;
                case 4:
                    radioBeepGroup.check(R.id.radioVibration);
                    break;
                default:
                    Toast.makeText(getContext(), "beepMode error("+beepRadio+")", Toast.LENGTH_LONG).show();
                    Log.e(TAG, "beepMode error("+beepRadio+")");
                    beepRadio = 1;
                    radioBeepGroup.check(R.id.radioNoneBeep);
                    break;
            }
        }else {
            radioBeepGroup.check(R.id.radioNoneBeep);
            beepRadio = 1;
        }

        if(BasicData.currentRallyName!=null && !BasicData.currentRallyName.equals("")  &&BasicData.attribute.containsKey(BasicData.currentRallyName)){
            enableRadio = BasicData.attribute.get(BasicData.currentRallyName).enabled;
            switch (enableRadio){
                case 1:
                    radioEnableGroup.check(R.id.radioEnable);
                    break;
                case 2:
                    radioEnableGroup.check(R.id.radioDisenable);
                    break;
                default:
                    Toast.makeText(getContext(), "enableRadio error("+enableRadio+")", Toast.LENGTH_LONG).show();
                    Log.e(TAG, "enableRadio error("+enableRadio+")");
                    enableRadio = 2;
                    radioEnableGroup.check(R.id.radioDisenable);
                    break;
            }
        }else {
            radioEnableGroup.check(R.id.radioDisenable);
            enableRadio = 2;
        }

            radioKinboGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {

                if(R.id.radioKinboNone==checkedId){
                    kinboRadio = 1;
                }
                if(R.id.radioGPS==checkedId){
                    kinboRadio = 2;
                }
                if(R.id.radioBeacon==checkedId){
                    kinboRadio = 3;
                }
                //Log.i(tag, "チェックID : "+checkedId);
            }
        });

        radioStampGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if(R.id.radioQR==checkedId){
                    stampRadio = 1;//QR
                }
                if(R.id.radioNFC==checkedId){
                    stampRadio = 2;//NFC
                }
                if(R.id.radioStamp==checkedId){
                    stampRadio = 3;//電子スタンプ
                }
                if(R.id.radioButton==checkedId){
                    stampRadio = 4;// ボタン
                }
                 //Log.i(tag, "チェックID : "+checkedId);
            }
        });

        radioBeepGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if(R.id.radioNoneBeep==checkedId){
                    beepRadio = 1;
                }
                if(R.id.radioBeep==checkedId){
                    beepRadio = 2;
                }
                if(R.id.radioBeepAndVibration==checkedId){
                    beepRadio = 3;
                }
                if(R.id.radioVibration==checkedId){
                    beepRadio = 4;
                }
                //Log.i(tag, "チェックID : "+checkedId);
            }
        });

        radioEnableGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if(R.id.radioEnable==checkedId){
                    enableRadio = 1;
                }
                if(R.id.radioDisenable==checkedId){
                    enableRadio = 2;
                }
                //Log.i(tag, "チェックID : "+checkedId);
            }
        });

        txtName = (EditText)view.findViewById(R.id.txtRallyName);
        txtName.setText("");

        txtBackgroudURL = (EditText)view.findViewById(R.id.txtBackgroundURL);
        txtBackgroudURL.setText("");

        rif = this;

        btnCheckBackground = (Button)view.findViewById(R.id.btnCheckBackground);
        btnCheckBackground.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = txtBackgroudURL.getText().toString();
                String title = txtName.getText().toString();
                if(title.isEmpty()){
                    Toast.makeText(getContext(), "タイトルを入力してください", Toast.LENGTH_LONG).show();
                    return;
                }

                if(url.isEmpty()){
                    Snackbar.make(view, "背景画像名称が変更されました", Snackbar.LENGTH_LONG).show();
                    btnRegister.setEnabled(false);
                    return;
                }
                String ext = url.substring(url.lastIndexOf("."));
                if(!ext.toLowerCase().equals(".png")){
                    Snackbar.make(view, "PNG画像のみの対応となっています", Snackbar.LENGTH_LONG).show();
                    return;
                }
                btnCheckBackground.setEnabled(false);
                try {
                    DownLoader downLoader = new DownLoader(null, btnRegister, getContext());
                    downLoader.setCallbacks(rif);
                    downLoader.execute(url);
                }catch (Exception ex){
                    Log.e("","");
                }

            }
        });

        btnRegister = (Button)view.findViewById(R.id.btnNewRegister) ;
        btnRegister.setEnabled(false);

        //----------------------------------------------
        //
        // ラリー情報登録
        //
        //----------------------------------------------
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                title = txtName.getText().toString();
                background = txtBackgroudURL.getText().toString();
                if(title.isEmpty()){
                    Snackbar.make(vw, "タイトルを入力してください", Snackbar.LENGTH_LONG).show();
                    return;
                }
                if(background.isEmpty()){
                    Snackbar.make(vw, "背景画像URLを入力してください", Snackbar.LENGTH_LONG).show();
                    return;
                }
                if(stampRadio==4 && kinboRadio==1){
                    Snackbar.make(vw, "ボタンと近傍なしの組み合わせはできません", Snackbar.LENGTH_LONG).show();
                    return;
                }
                btnRegister.setEnabled(false);
                try {
                    BasicData.setCurrentRallyName(title);
                    BasicData.setAttribute(stampRadio, beepRadio, kinboRadio, background,enableRadio);
                    OneRallyData one = new OneRallyData();
                    one.fromBasicData(title);
                    String a = one.toJson();

                    InsertData insertData = new InsertData(ServerUrl.setData);
                    insertData.execute(BasicData.mail, BasicData.rallyName.get(title), BasicData.BASICDATA, a);
                }catch (Exception ex){
                    Toast.makeText(getContext(), ex.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                    return;
                }

                //----------------------------------------------
                // 画面遷移
                //----------------------------------------------
                if (shinki==true) {
                    SharedPreferences shared = getActivity().getSharedPreferences(Points.prefFile, Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = shared.edit();
                    editor.remove(Points.prefKeyData);
                    editor.apply();
                }
                replaceFragment(new ContextFragment());
            }
        });
        txtName.setEnabled(true);
        btnCheckBackground.setEnabled(true);

/*
        for(int i = 0; i < radioStampGroup.getChildCount(); i++){
            ((RadioButton)radioStampGroup.getChildAt(i)).setEnabled(true);
        }
*/
        Bundle bundle = getArguments();
        String rally = bundle.getString("rally");

        if(!rally.equals("")) {
            for(int i = 0; i < radioStampGroup.getChildCount(); i++){
                ((RadioButton)radioStampGroup.getChildAt(i)).setEnabled(false);
            }
            for(int i = 0; i < radioKinboGroup.getChildCount(); i++){
                ((RadioButton)radioKinboGroup.getChildAt(i)).setEnabled(false);
            }

        }
        /*
        if(!rally.equals("")){
            txtName.setText(rally);
            txtBackgroudURL.setText(BasicData.attribute.get(rally).background);
            int b = BasicData.attribute.get(rally).beepMode;
            switch (b) {
                case 1:
                    radioBeepGroup.check(R.id.radioNoneBeep);
                    break;
                case 2:
                    radioBeepGroup.check(R.id.radioBeep);
                    break;
                case 3:
                    radioBeepGroup.check(R.id.radioBeepAndVibration);
                    break;
                case 4:
                    radioBeepGroup.check(R.id.radioVibration);
                    break;
            }
            switch (BasicData.attribute.get(rally).stampMode){
                case 1:
                    radioStampGroup.check(R.id.radioQR);
                    break;
                case 2:
                    radioStampGroup.check(R.id.radioNFC);
                    break;
                case 4:
                    radioStampGroup.check(R.id.radioStamp);
                    break;
                case 5:
                    radioStampGroup.check(R.id.radioButton);
                    break;
            }
            switch (BasicData.attribute.get(rally).kinboMode){
                case 1:
                    radioKinboGroup.check(R.id.radioKinboNone);
                    break;
                case 2:
                    radioKinboGroup.check(R.id.radioGPS);
                    break;
                case 3:
                    radioKinboGroup.check(R.id.radioBeacon);
                    break;
            }
            switch (BasicData.attribute.get(rally).enabled){
                case 1:
                    radioEnableGroup.check(R.id.radioEnable);
                    break;
                case 2:
                    radioEnableGroup.check(R.id.radioDisenable);
                    break;
            }

            txtName.setEnabled(false);
            for(int i = 0; i < radioStampGroup.getChildCount(); i++){
                ((RadioButton)radioStampGroup.getChildAt(i)).setEnabled(false);
            }
            for(int i = 0; i < radioKinboGroup.getChildCount(); i++){
                ((RadioButton)radioKinboGroup.getChildAt(i)).setEnabled(false);
            }
        }

         */
        btnSlash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                txtBackgroudURL.setText(txtBackgroudURL.getText().toString()+"/");
                txtBackgroudURL.setSelection(txtBackgroudURL.getText().length());
            }
        });
        if(BasicData.currentRallyName!=null && !BasicData.currentRallyName.equals("")) {
            txtName.setText(BasicData.currentRallyName);
            txtBackgroudURL.setText(BasicData.attribute.get(BasicData.currentRallyName).background);
        }

    }


    // 表示させるFragmentを切り替えるメソッドを定義（表示したいFragmentを引数として渡す）
    private void replaceFragment(Fragment fragment) {
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
    public void success(Object obj) {
        btnRegister.setEnabled(true);
    }

    @Override
    public void failed(Object obj) {
        Snackbar.make(vw, "背景画像名称が間違っています", Snackbar.LENGTH_LONG).show();
        btnRegister.setEnabled(false);
        btnCheckBackground.setEnabled(true);
    }
}

