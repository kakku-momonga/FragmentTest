package jp.co.toshiba.iflink.fragmenttest;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.snackbar.Snackbar;

import static androidx.core.content.ContextCompat.getSystemService;

public class GpsFragment  extends Fragment implements LocationListener {
    LocationManager mLocationManager;
    /** 位置情報の通知するための最小時間間隔（ミリ秒） */
    final long minTime = 1000;
    /** 位置情報を通知するための最小距離間隔（メートル）*/
    final long minDistance = 1;

    static public final String prefKeyIdo="ido";
    static public final String prefKeyKeido = "keido";
    static public final String prefKeyTitle = "title";


    static public String gpsTitle;

    Double ido;
    Double keido;

    public int id;

    EditText txtName ;
    Button btnExit;
    Button btnGPS;
    //-------------------------------
    //
    // リスナー
    //
    //-------------------------------
    private GpsFragment.SetGPSListener listener;
    public interface SetGPSListener {
        void setGPSCallback(Fragment fragment, int id, GPSData gpsData);
    }
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            // 親フラグメントにイベントを送信できるように呼び出し元であるSampleFragmentオブジェクトを取得し、
            // listenerのインスタンスを生成する
            listener = (GpsFragment.SetGPSListener) getTargetFragment();
        } catch (ClassCastException e) {
            // 親フラグメントがインターフェースを実装していない場合は例外を投げる
            listener = null;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        int granted = ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION);
        if (granted != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 1000);
        }
        callRequestLocationUpdates();

        // 先ほどのレイアウトをここでViewとして作成します
        return inflater.inflate(R.layout.fragment_gps, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        int granted = ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION);
        if (granted != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 1000);
        }
        Snackbar.make(view, "GPSが有効になるまで時間がかかる場合があります", Snackbar.LENGTH_LONG).show();

        txtName = (EditText)view.findViewById(R.id.txtGPSName);
        btnExit = (Button)view.findViewById(R.id.btnGPSExit);
        btnGPS = (Button)view.findViewById(R.id.btnGPS);

        btnExit.setEnabled(false);
        btnGPS.setEnabled(false);

        // GPS範囲作成
        btnGPS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gpsTitle = txtName.getText().toString();
//                BasicData.setGPSName(gpsTitle);
                //MapsFragment.gpsTitle = gpsTitle;
                // 保存する
                btnGPS.setEnabled(false);
            }
        });

        btnExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // 保存する
                if(listener!=null){
                    listener.setGPSCallback(GpsFragment.this, id, new GPSData());
                }
                getFragmentManager().popBackStack();
            }
        });
    }
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

        btnExit.setEnabled(true);
        btnGPS.setEnabled(true);

    }

    // 表示させるFragmentを切り替えるメソッドを定義（表示したいFragmentを引数として渡す）
    private void replaceFragment(Fragment fragment) {
        // フラグメントマネージャーの取得
        FragmentManager manager = getFragmentManager(); // アクティビティではgetSupportFragmentManager()?
        // フラグメントトランザクションの開始
        FragmentTransaction transaction = manager.beginTransaction();
        // MainFragmentを追加
        Bundle bundle = new Bundle();
        bundle.putDouble(prefKeyIdo, ido);
        bundle.putDouble(prefKeyKeido, keido);
        bundle.putString(prefKeyTitle, gpsTitle);
        //値を書き込む
        fragment.setArguments(bundle);
        transaction.replace(R.id.fragmentview, fragment);
        // 置き換えのトランザクションをバックスタックに保存する
        transaction.addToBackStack(null);
        // フラグメントトランザクションをコミット
        transaction.commit();
    }

}