package jp.co.toshiba.iflink.fragmenttest;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.view.KeyEvent;
import androidx.fragment.app.Fragment;

import com.google.android.material.snackbar.Snackbar;

public class StampFragment extends Fragment{
    EditText txtStampUrl;
    Button btnExit;
    Button btnOk;
    ImageView imageViewStamp;

    Button btnHttps;
    boolean flag;
    private StampUrlListener listener;
    public int id;

    SharedPreferences shd;

    final String TAG="StampFragment";

    //-------------------------------
    //
    // リスナー
    //
    //-------------------------------
    public interface StampUrlListener {
        void stammpCallback(Fragment fragment, int id, String url, Drawable drawable);
    }
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            // 親フラグメントにイベントを送信できるように呼び出し元であるSampleFragmentオブジェクトを取得し、
            // listenerのインスタンスを生成する
            listener = (StampUrlListener) getTargetFragment();
        } catch (ClassCastException e) {
            // 親フラグメントがインターフェースを実装していない場合は例外を投げる
            listener = null;
        }
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        return inflater.inflate(R.layout.fragment_stamp, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        shd = getActivity().getSharedPreferences("stamp", Context.MODE_PRIVATE);

        String txt = shd.getString("url", "");

        txtStampUrl = view.findViewById(R.id.txtStampUrl);
        btnExit = view.findViewById(R.id.btnStampExit);
        btnOk = view.findViewById(R.id.btnRegisterStampUrl);
        imageViewStamp = view.findViewById(R.id.ImageviewStamp);

        btnExit.setEnabled(false);

        flag = false;
        btnExit.setEnabled(false);

        txtStampUrl.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                flag = false;
                return false;
            }
        });

        btnExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(flag==false){
                    Snackbar.make(view, "スタンプURLは保存されませんでした", Snackbar.LENGTH_LONG).show();
                    return;
                }else{
                }
                if(listener!=null){
                    listener.stammpCallback(StampFragment.this, id, txtStampUrl.getText().toString(),
                            imageViewStamp.getDrawable());
                }
                getFragmentManager().popBackStack();
            }
        });

        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnExit.setEnabled(false);
                String w = txtStampUrl.getText().toString();
                if(w.equals("")){
                    Points.workingStampUrl = "";
                    Snackbar.make(view, "スタンプURLを入力してください", Snackbar.LENGTH_LONG).show();
                    return;
                }
                String ext = w.substring(w.lastIndexOf("."));
                if(!ext.toLowerCase().equals(".png")){
                    Snackbar.make(view, "PNG画像のみの対応となっています", Snackbar.LENGTH_LONG).show();
                    return;
                }

                SharedPreferences.Editor editor = shd.edit();
                editor.putString("url", w);
                editor.apply();

                DownLoader downLoader = new DownLoader(imageViewStamp, btnExit, getContext());
                downLoader.execute(w);
                flag=true;
                //btnExit.setEnabled(true);
            }
        });
        try {
            Bundle bundle = getArguments();
            String stampName = bundle.getString("stamp");
            txtStampUrl.setText(stampName);
            id = bundle.getInt("id");
        }catch (Exception ex){
            Log.e(TAG, ex.getLocalizedMessage());
        }

        btnHttps = view.findViewById(R.id.btnHttps);
        Button btnSlash = view.findViewById(R.id.btnSlash);
        Button btnDotCom = view.findViewById(R.id.btnDotCom);
        Button btnDotNet = view.findViewById(R.id.btnDotNet);
        Button btnDotCoJp = view.findViewById(R.id.btnDotCoJp);
        Button btnRally = view.findViewById(R.id.btnRally);

        btnHttps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String w = btnHttps.getText().toString();
                String a = txtStampUrl.getText().toString();
                w = a + w;
                txtStampUrl.setText(w);
                txtStampUrl.setSelection(txtStampUrl.getText().length());

                //txtStampUrl.setSelection(w.length()-1);
            }
        });
        btnSlash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String w = btnSlash.getText().toString();
                String a = txtStampUrl.getText().toString();
                w = a + w;
                txtStampUrl.setText(w);
                txtStampUrl.setSelection(txtStampUrl.getText().length());
                //txtStampUrl.setSelection(w.length()-1);
            }
        });
        btnDotCom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String w = btnDotCom.getText().toString();
                String a = txtStampUrl.getText().toString();
                w = a + w;
                txtStampUrl.setText(w);
                txtStampUrl.setSelection(txtStampUrl.getText().length());
                //txtStampUrl.setSelection(w.length()-1);
            }
        });
        btnDotNet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String w = btnDotNet.getText().toString();
                String a = txtStampUrl.getText().toString();
                w = a + w;
                txtStampUrl.setText(w);
                txtStampUrl.setSelection(txtStampUrl.getText().length());
                //txtStampUrl.setSelection(w.length()-1);
            }
        });
        btnDotCoJp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String w = btnDotCoJp.getText().toString();
                String a = txtStampUrl.getText().toString();
                w = a + w;
                txtStampUrl.setText(w);
                txtStampUrl.setSelection(txtStampUrl.getText().length());
                //txtStampUrl.setSelection(w.length()-1);
            }
        });
        btnRally.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String w = btnRally.getText().toString();
                String a = txtStampUrl.getText().toString();
                w = a + w;
                txtStampUrl.setText(w);
                txtStampUrl.setSelection(txtStampUrl.getText().length());
                //txtStampUrl.setSelection(w.length()-1);
            }
        });

    }
}
