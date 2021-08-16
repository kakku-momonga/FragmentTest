package jp.co.toshiba.iflink.fragmenttest;

import android.content.Context;
import android.content.Intent;
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

import androidx.fragment.app.Fragment;

import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;

public class QRFragment extends Fragment {
    static EditText txtQRValue;
    Button btnExit;
    Button btnOk;
    ListView listView;
    private QRFragment.SetQRlListener listener;
    public int id;
    QRFragment qrFragment;

    final String TAG = "QRFragment";

    //-------------------------------
    //
    // リスナー
    //
    //-------------------------------
    public interface SetQRlListener {
        void setQRCallback(Fragment fragment, int id, String url);
    }
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            // 親フラグメントにイベントを送信できるように呼び出し元であるSampleFragmentオブジェクトを取得し、
            // listenerのインスタンスを生成する
            listener = (QRFragment.SetQRlListener) getTargetFragment();
        } catch (ClassCastException e) {
            // 親フラグメントがインターフェースを実装していない場合は例外を投げる
            listener = null;
        }
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        return inflater.inflate(R.layout.fragment_qr, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        qrFragment = this;

        txtQRValue = view.findViewById(R.id.txtQR);
        btnExit = view.findViewById(R.id.btnQRExit);
        btnOk = view.findViewById(R.id.btnRegisterQR);
        listView = view.findViewById(R.id.qrlist);

        btnExit.setEnabled(true);

        txtQRValue.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                return false;
            }
        });

        btnExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(listener!=null){
                    listener.setQRCallback(QRFragment.this, id, txtQRValue.getText().toString());
                }
                getFragmentManager().popBackStack();
            }
        });

        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Intent intent = new Intent(getActivity(),ScannedBarcodeActivity.class);
                    startActivity(intent);
                }catch (Exception ex){
                    Log.i("fdaf", ex.getLocalizedMessage());
                }

/*                String w = txtQRValue.getText().toString();
                if(w.equals("")){
                    Snackbar.make(view, "QR値を入力してください", Snackbar.LENGTH_LONG).show();
                    return;
                }*/
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
}
