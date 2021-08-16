package jp.co.toshiba.iflink.fragmenttest;

import android.Manifest;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.common.internal.Constants;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.Arrays;

public class NfcFragment extends Fragment {
    EditText txtNfcValue;
    Button btnExit;
    Button btnOk;
    ListView listView;
    private NfcFragment.SetNfclListener listener;
    public int id;
    private NfcAdapter mNfcAdapter;
    static public PendingIntent pendingIntent;
    IntentFilter intentFilter;

    final String TAG = "NfcFragment";

    //-------------------------------
    //
    // リスナー
    //
    //-------------------------------
    public interface SetNfclListener {
        void setNfcCallback(Fragment fragment, int id, String url);
    }
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            // 親フラグメントにイベントを送信できるように呼び出し元であるSampleFragmentオブジェクトを取得し、
            // listenerのインスタンスを生成する
            listener = (NfcFragment.SetNfclListener) getTargetFragment();
        } catch (ClassCastException e) {
            // 親フラグメントがインターフェースを実装していない場合は例外を投げる
            listener = null;
        }
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        int granted = ContextCompat.checkSelfPermission(getContext(), Manifest.permission.NFC);
        if (granted != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.NFC}, 1020);
        }
        mNfcAdapter = android.nfc.NfcAdapter.getDefaultAdapter(getContext());


        return inflater.inflate(R.layout.fragment_nfc, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        txtNfcValue = view.findViewById(R.id.txtNfcValue);
        btnExit = view.findViewById(R.id.btnNfcExit);
        btnOk = view.findViewById(R.id.btnReadNfc);
        listView = view.findViewById(R.id.nfcListView);

        btnExit.setEnabled(true);

        txtNfcValue.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                return false;
            }
        });

        btnExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(listener!=null){
                    listener.setNfcCallback(NfcFragment.this, id, txtNfcValue.getText().toString());
                }
                getFragmentManager().popBackStack();
            }
        });

        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(BasicData.nfcFlag==1){
                    txtNfcValue.setText(BasicData.nfcUuid);
                }
            }
        });

        try {
            Bundle bundle = getArguments();
            id = bundle.getInt("id");
            ArrayList<String> s = bundle.getStringArrayList("data");
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                    getContext(),
                    R.layout.beacon_list, s);
            listView.setAdapter(adapter);
        }catch (Exception ex){
            Log.e(TAG, ex.getLocalizedMessage());
        }


    }
    @Override
    public void onResume(){

        super.onResume();

        // NFCがかざされたときの設定
        mNfcAdapter.enableForegroundDispatch(getActivity(), pendingIntent, null, null);
    }
    @Override
    public void onPause(){
        super.onPause();

        // Activityがバックグラウンドになったときは、受け取らない
        mNfcAdapter.disableForegroundDispatch(getActivity());
    }
    @Override
    public void onStart() {
        super.onStart();
        if (getActivity() != null && getActivity().getIntent().hasExtra(NfcAdapter.EXTRA_ID)) {
            // do whatever needed
        }
    }

}
