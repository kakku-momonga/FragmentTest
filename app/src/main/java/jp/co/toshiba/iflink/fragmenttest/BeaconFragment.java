package jp.co.toshiba.iflink.fragmenttest;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.RemoteException;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.snackbar.Snackbar;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.Identifier;
import org.altbeacon.beacon.MonitorNotifier;
import org.altbeacon.beacon.Region;

import java.util.ArrayList;
import java.util.Collection;

public class BeaconFragment extends Fragment implements BeaconLisstViewFragment.SetBeaconIDlListener {
    EditText txtBeaconUUID;
    Button btnExit;
    Button btnOk;
    ListView beaconList;
    ArrayList<String> data = new ArrayList<>();
    static BeaconManager beaconManager = null;
    private BeaconFragment.SetBeaconlListener listener;
    public int id;
    String uuid="";

    final String TAG = "BEACONLIST";

    @Override
    public void setBeaconCallback(String uuid) {
        txtBeaconUUID.setText(uuid);
        this.uuid = uuid;
    }


    //-------------------------------
    //
    // リスナー
    //
    //-------------------------------
    public interface SetBeaconlListener {
        void setBeaconCallback(Fragment fragment, int id, String uuid);
    }
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            // 親フラグメントにイベントを送信できるように呼び出し元であるSampleFragmentオブジェクトを取得し、
            // listenerのインスタンスを生成する
            listener = (BeaconFragment.SetBeaconlListener) getTargetFragment();
        } catch (ClassCastException e) {
            // 親フラグメントがインターフェースを実装していない場合は例外を投げる
            listener = null;
        }
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);


        return inflater.inflate(R.layout.fragment_beacon, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        txtBeaconUUID = view.findViewById(R.id.txtBeacon);
        btnExit = view.findViewById(R.id.btnBeaconExit);
        btnOk = view.findViewById(R.id.btnRegisterBeacon);
        beaconList = view.findViewById(R.id.listBeacon);

        btnExit.setEnabled(true);

        txtBeaconUUID.setText(uuid);
/*
        txtBeaconUUID.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                return false;
            }
        });
*/
        btnExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(listener!=null){
                    listener.setBeaconCallback(BeaconFragment.this, id, txtBeaconUUID.getText().toString());
                }
                getFragmentManager().popBackStack();
            }
        });

        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                data = BasicData.getBeaconArray();
                if(data==null || data.size()==0){
                    Toast.makeText(getContext(), "ビーコンが見つかりません", Toast.LENGTH_LONG).show();
                    return;
                }
                Bundle bundle = new Bundle();
                bundle.putStringArrayList("data", data);
                bundle.putInt("id", id);
                BeaconLisstViewFragment beaconLisstViewFragment = new BeaconLisstViewFragment();
                beaconLisstViewFragment.setArguments(bundle);
                replaceFragment(beaconLisstViewFragment);

            }
        });

        try {
            Bundle bundle = getArguments();
            id = bundle.getInt("id");
            ArrayList<String> s = bundle.getStringArrayList("data");
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                    getContext(),
                    R.layout.beacon_list, s);
            beaconList.setAdapter(adapter);
        }catch (Exception ex){
            Log.e(TAG, ex.getLocalizedMessage());
        }

    }
    @Override
    public void onResume() {
        super.onResume();
        txtBeaconUUID.setText(uuid);
        /*if(beaconManager.isBound(this)){
            beaconManager.setBackgroundMode(false);
        }*/
    }

    @Override
    public void onPause() {
        super.onPause();
        /*if(beaconManager.isBound(this)){
            beaconManager.setBackgroundMode(true);
        }*/
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //beaconManager.unbind(this);
    }
    // 表示させるFragmentを切り替えるメソッドを定義（表示したいFragmentを引数として渡す）
    private void replaceFragment(Fragment fragment) {
        fragment.setTargetFragment(this, 2102);
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

}
