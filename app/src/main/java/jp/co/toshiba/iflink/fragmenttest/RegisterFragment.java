package jp.co.toshiba.iflink.fragmenttest;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
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

public class RegisterFragment extends Fragment {
    Button btnregister;
    EditText txtmail;
    EditText txtpassword;
    EditText txtconfirm;
    TextView txtError;
    Timer timer;

    static SharedPreferences shared;

    public static final String prefKeyMail = "mail";
    public static final String prefKeyPassword = "password";
    public static final String prefKeyOk = "ok";

    static public Boolean preferOk;
    public static String mail;
    public static String password;

    static final String TAG = "USERREGISTER";


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        shared = getActivity().getSharedPreferences("DataSave", Context.MODE_PRIVATE);
        mail = shared.getString(prefKeyMail, "");
        password = shared.getString(prefKeyPassword, "");
        preferOk = shared.getBoolean(prefKeyOk, false);


        preferOk = false;


        // 先ほどのレイアウトをここでViewとして作成します
        return inflater.inflate(R.layout.fragment_register, container, false);
    }

    // Viewが生成し終わった時に呼ばれるメソッド
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        btnregister = (Button)view.findViewById(R.id.btnRegister);

        txtmail = (EditText) view.findViewById(R.id.txtMail);

        txtpassword = (EditText)view.findViewById(R.id.txtPassword);
        txtconfirm = (EditText)view.findViewById(R.id.txtPasswordConfirm);

        txtError = (TextView )view.findViewById(R.id.txtMessage) ;

        txtError.setText("");

        txtmail.setText(mail);
        //txtpassword.setText(preferPassword);
        //txtconfirm.setText(preferPassword);


        // Buttonのクリックした時の処理を書きます
        btnregister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String p1 = txtpassword.getText().toString();
                String p2 = txtconfirm.getText().toString();

                if(p1.equals(p2)==false){
                    txtError.setText("パスワードが一致しません");
                    txtError.setVisibility(View.VISIBLE);
                    new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            txtError.setVisibility(View.INVISIBLE);
                        }
                    }, 2500);
                    return;
                }
                if(p1.length()<=10){
                    txtError.setText("パスワードが正しくありません");
                    txtError.setVisibility(View.VISIBLE);
                    new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            txtError.setVisibility(View.INVISIBLE);
                        }
                    }, 2500);
                    return;
                }

                mail = txtmail.getText().toString();
                if(mail.length()<3){
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
                preferOk = true;

                //preferMail = mail;
                //preferPassword = p1;
                password = hash(p1);

                // SharedPreferencesに保存する
                SharedPreferences.Editor editor = shared.edit();
                editor.putBoolean(prefKeyOk,preferOk);
                editor.putString(prefKeyMail, mail);
                editor.putString(prefKeyPassword, password);
                editor.apply();

                // DBに保存する
                InsertUser insertUser = new InsertUser("http://red.lexsolejp.net/stamprally/userRegister.php");
                insertUser.execute(mail, password);

                // 画面遷移
                replaceFragment(new MenuFragment());
            }
        });
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
}
