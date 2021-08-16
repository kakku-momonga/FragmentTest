package jp.co.toshiba.iflink.fragmenttest;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Html;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import java.security.MessageDigest;
import java.util.Timer;

public class LoginFragment extends Fragment {
    Button btnregister;
    EditText txtmail;
    EditText txtpassword;
    EditText txtconfirm;
    TextView txtError;
    TextView txtRegister;
    Timer timer;
    LoginFragment loginFragment;

    static SharedPreferences shared;



    static final String TAG = "USERREGISTER";


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        loginFragment = this;

        shared = getActivity().getSharedPreferences("DataSave", Context.MODE_PRIVATE);
        RegisterFragment.mail = shared.getString(RegisterFragment.prefKeyMail, "");
        RegisterFragment.password = shared.getString(RegisterFragment.prefKeyPassword, "");
        RegisterFragment.preferOk = shared.getBoolean(RegisterFragment.prefKeyOk, false);


        RegisterFragment.preferOk = false;


        // 先ほどのレイアウトをここでViewとして作成します
        return inflater.inflate(R.layout.fragment_login, container, false);
    }

    // Viewが生成し終わった時に呼ばれるメソッド
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        btnregister = (Button)view.findViewById(R.id.btnLogin);

        txtmail = (EditText) view.findViewById(R.id.txtLoginMail);

        txtpassword = (EditText)view.findViewById(R.id.txtLoginPassword);

        txtError = (TextView )view.findViewById(R.id.txtLoginMessage) ;

        txtError.setText("");

        txtmail.setText(RegisterFragment.mail);
        //txtpassword.setText(preferPassword);
        //txtconfirm.setText(preferPassword);

        txtRegister = view.findViewById(R.id.txtRegister);
        txtRegister.setText(Html.fromHtml(getString(R.string.txtRegister),Html.FROM_HTML_MODE_LEGACY));
        txtRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                replaceFragment(new RegisterFragment());
            }
        });


        // Buttonのクリックした時の処理を書きます
        btnregister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String p1 = txtpassword.getText().toString();
                //String p2 = txtconfirm.getText().toString();


                RegisterFragment.mail = txtmail.getText().toString();
                if(RegisterFragment.mail.length()<3){
                    txtError.setText("メールが正しくありません");
                    txtError.setVisibility(View.VISIBLE);
                    new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            txtError.setVisibility(View.INVISIBLE);
                        }
                    }, 2500);
                    return;
                }
                loginCheck insertUser = new loginCheck(getContext(),shared,loginFragment, ServerUrl.login);
                insertUser.execute(RegisterFragment.mail, hash(p1));

                /*
                RegisterFragment.preferOk = true;

                //preferMail = mail;
                //preferPassword = p1;
                RegisterFragment.password = hash(p1);

                // SharedPreferencesに保存する

                // 画面遷移
                replaceFragment(new MenuFragment());

                 */
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
/*
        SharedPreferences.Editor editor = shared.edit();
        editor.putBoolean("preferOk",preferOk);
        editor.putString("mail", mail);
        editor.putString("password", password);
        editor.apply();
*/

    }

    public String hash(String toHash){
        String passwordToHash = toHash;
        String generatedPassword = null;
        MessageDigest md;

 /*       // Create MessageDigest instance for MD5
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

  */
        generatedPassword = ContextFragment.hash(passwordToHash);

        return  generatedPassword;

    }
    public void replaceFragment(Fragment fragment) {
        // フラグメントマネージャーの取得
        FragmentManager manager = getFragmentManager(); // アクティビティではgetSupportFragmentManager()?
        // フラグメントトランザクションの開始
        FragmentTransaction transaction = manager.beginTransaction();
        // MainFragmentを追加
        /*Bundle bundle = new Bundle();
        bundle.putDouble(prefKeyIdo, ido);
        bundle.putDouble(prefKeyKeido, keido);
        bundle.putString(prefKeyTitle, gpsTitle);
        //値を書き込む
        fragment.setArguments(bundle);*/
        transaction.replace(R.id.fragmentview, fragment);
        // 置き換えのトランザクションをバックスタックに保存する
        transaction.addToBackStack(null);
        // フラグメントトランザクションをコミット
        transaction.commit();
    }
}
