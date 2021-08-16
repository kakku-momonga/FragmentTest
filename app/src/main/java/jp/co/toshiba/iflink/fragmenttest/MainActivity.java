package jp.co.toshiba.iflink.fragmenttest;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.Manifest;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.os.RemoteException;
import android.util.Log;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Timer;
import java.util.TimerTask;
import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.Identifier;
import org.altbeacon.beacon.MonitorNotifier;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;
import org.altbeacon.beacon.powersave.BackgroundPowerSaver;
import org.altbeacon.beacon.startup.RegionBootstrap;

public class MainActivity extends AppCompatActivity  implements BeaconConsumer, NfcFragment.SetNfclListener {
    private final int REQUEST_CODE = 1321;
    BeaconManager beaconManager;
    private static final String IBEACON_FORMAT = "m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24";
    final String TAG = "StampRallyMaker";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SharedPreferences shared;
        shared = getSharedPreferences("DataSave", Context.MODE_PRIVATE);
        String preferMail = shared.getString(RegisterFragment.prefKeyMail, "");
        String preferPassword = shared.getString(RegisterFragment.prefKeyPassword, "");
        Boolean preferOk = shared.getBoolean(RegisterFragment.prefKeyOk, false);

        if (preferOk==true) {
            addFragment(new MenuFragment());
        } else {
            addFragment(new LoginFragment());
        }
        String[] permissions = {
                Manifest.permission.INTERNET,
                Manifest.permission.CAMERA,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.NFC,
                Manifest.permission.BLUETOOTH,
                Manifest.permission.BLUETOOTH_ADMIN
        };
        checkPermission(permissions, REQUEST_CODE);

        beaconManager = BeaconManager.getInstanceForApplication(this);
        beaconManager.getBeaconParsers().add(new BeaconParser().setBeaconLayout(IBEACON_FORMAT));


    }
    public void checkPermission(final String[] permissions,final int request_code){
        // 許可されていないものだけダイアログが表示される
        ActivityCompat.requestPermissions(this, permissions, request_code);
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {

            case REQUEST_CODE:
                for (int i = 0; i < permissions.length; i++) {
                    if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                        Snackbar.make(getWindow().getDecorView(),"設定からすべて許可をしてください",Snackbar.LENGTH_LONG).show();
                        Timer timer1 = new Timer();
                        timer1.scheduleAtFixedRate(new TimerTask() {
                            @Override
                            public void run() {
                                // TODO Auto-generated method stub
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        finish();
                                    }
                                });
                            }
                        }, 5000, 5000);
                    }
                }
                break;
            default:
                break;
        }
    }


    // Fragmentを表示させるメソッドを定義（表示したいFragmentを引数として渡す）
    private void addFragment(Fragment fragment) {
        // フラグメントマネージャーの取得
        FragmentManager manager = getSupportFragmentManager();
        // フラグメントトランザクションの開始
        FragmentTransaction transaction = manager.beginTransaction();
        // MainFragmentを追加
        transaction.replace(R.id.fragmentview, fragment);
        // フラグメントトランザクションのコミット。コミットすることでFragmentの状態が反映される
        transaction.commit();
    }

    // 戻るボタン「←」をアクションバー（上部バー）にセットするメソッドを定義
    public void setupBackButton(boolean enableBackButton) {
        // アクションバーを取得
        ActionBar actionBar = getSupportActionBar();
        // アクションバーに戻るボタン「←」をセット（引数が true: 表示、false: 非表示）
        actionBar.setDisplayHomeAsUpEnabled(enableBackButton);
    }

    @Override
    public void onNewIntent(Intent intent){
        super.onNewIntent(intent);
        setIntent(intent);
        byte[] uid = intent.getByteArrayExtra(NfcAdapter.EXTRA_ID);
        String w = Arrays.toString(uid);

        BasicData.nfcFlag = 1;
        BasicData.nfcUuid = w;
    }
    @Override
    public void onResume(){

        super.onResume();

        // NFCがかざされたときの設定
        Intent intent = new Intent(this, this.getClass());
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        // ほかのアプリを開かないようにする
        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, 0);
        NfcFragment.pendingIntent = pendingIntent;
        beaconManager.bind(this);

    }
    @Override
    protected void onPause() {
        super.onPause();
        // サービスの停止
        beaconManager.unbind(this);
    }

    @Override
    public void onBeaconServiceConnect() {
        final Region mRegion = new Region("iBeacon", null, null, null);

        beaconManager.addRangeNotifier(new RangeNotifier() {

            @Override
            public void didRangeBeaconsInRegion(Collection<Beacon> beacons, Region region) {
                //検出したBeaconの情報を全てlog出力
                for(Beacon beacon: beacons) {
                    Log.d("MyActivity", "UUID:" + beacon.getId1() + ", major:"
                            + beacon.getId2() + ", minor:" + beacon.getId3() + ", RSSI:"
                            + beacon.getRssi() + ", TxPower:" + beacon.getTxPower()
                            + ", Distance:" + beacon.getDistance());
                    if(beacon.getId1()==null || beacon.getId1().toUuid().toString().equals("")){
                        continue;
                    }
                    String w = beacon.getId1().toUuid().toString();
                    BasicData.addUuid(w);
                }
            }
        });
        try {
            beaconManager.startRangingBeaconsInRegion(mRegion);
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, e.getLocalizedMessage());
        }
    }

    @Override
    public void setNfcCallback(Fragment fragment, int id, String url) {

    }
}