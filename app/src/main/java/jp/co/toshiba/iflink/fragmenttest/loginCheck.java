package jp.co.toshiba.iflink.fragmenttest;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;


public class loginCheck extends AsyncTask<String, Void, String> {
    final String TAGHTTP = "INSERTUSER";
    String url;

    Context context;
    SharedPreferences shared ;
    LoginFragment loginFragment;

    //private Activity mActivity;
    public loginCheck(Context context, SharedPreferences shared, LoginFragment loginFragment,String url) {
        //mActivity = activity;
        this.shared = shared;
        this.url = url;
        this.context = context;
        this.loginFragment = loginFragment;
    }
    @Override
    protected String doInBackground(String... params) {
        Log.i(TAGHTTP, "doInBackground start");
        String user = params[0];
        String password= params[1];

        //password = ContextFragment.hash(password);
        HashMap<String, String> map = new HashMap<>();
        map.put("mail", user);
        map.put("password", password);


        HttpURLConnection con = null;



        String a = "";
        try {
            String buffer = "";
            Log.i(TAGHTTP, "URL=" + this.url);
            URL url = new URL(this.url);
            con = (HttpURLConnection) url.openConnection();


            con.setRequestMethod("POST");
            con.setInstanceFollowRedirects(false);
            con.setDoOutput(true);
            con.setDoInput(true);

            Log.i(TAGHTTP, "POSTする");
            con.setRequestProperty("Accept-Language", "jp");
            //con.setRequestProperty("Content-Type", "application/json; charset=utf-8");
            con.addRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
            con.setConnectTimeout(100000);
            //レスポンスデータ読み取りタイムアウトを設定する。
            con.setReadTimeout(100000);

            OutputStream os = con.getOutputStream();
            PrintStream ps = new PrintStream(os);
            Uri.Builder builder = new Uri.Builder();
            //HashMapを[key=value]形式の文字列に変換する

            for (String key : map.keySet()) {
                //[key=value]形式の文字列に変換する。
                builder.appendQueryParameter(key , map.get(key));
            }
            //[key=value&key=value…]形式の文字列に変換する。
            String join = builder.build().getEncodedQuery();
            ps.print(join);
            ps.close();
            Log.i(TAGHTTP, "POSTした");
            // API return value
            InputStream stream = con.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(stream, "UTF-8"));
            StringBuilder inpputbuilder = new StringBuilder();

            String line = "";
            while ((line = reader.readLine()) != null) {
                Log.i(TAGHTTP, "line = " + line);
                inpputbuilder.append(line);
            }
            stream.close();
            a = inpputbuilder.toString();

        } catch (IOException e) {
            Log.e(TAGHTTP, e.getLocalizedMessage());
            e.printStackTrace();
            //} catch (JSONException e) {
            //    e.printStackTrace();
        } catch (Exception e) {
            Log.e(TAGHTTP, e.getLocalizedMessage());
            e.printStackTrace();
        } finally{
            con.disconnect();
        }
        Log.i(TAGHTTP, "doInBackground end");
        return a;
    }
    public void onPostExecute(String text) {
        Log.i(TAGHTTP, "onPostExecute end");
        if(text.equals("ok")==false){
            RegisterFragment.preferOk=false;

            Toast.makeText(context, "メールアドレスまたはパスワードが間違っています", Toast.LENGTH_LONG).show();
            return;
        }
        RegisterFragment.preferOk=true;
        SharedPreferences.Editor editor = shared.edit();
        editor.putBoolean(RegisterFragment.prefKeyOk,RegisterFragment.preferOk);
        editor.putString(RegisterFragment.prefKeyMail, RegisterFragment.mail);
        editor.putString(RegisterFragment.prefKeyPassword, RegisterFragment.password);
        editor.apply();
        loginFragment.replaceFragment(new MenuFragment());

    }
}
