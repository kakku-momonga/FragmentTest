package jp.co.toshiba.iflink.fragmenttest;

import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;

public class DeleteUuid  extends AsyncTask<String, Void, String> {
    final String TAGHTTP = "DELETEUUID";

    //TextView tv;

    public DeleteUuid() {
    }
    @Override
    protected String doInBackground(String... params) {
        Log.i(TAGHTTP, "doInBackground start");
        String uuid = params[0];

        HashMap<String, String> map = new HashMap<>();
        map.put("uuid", uuid);

        HttpURLConnection con = null;

        String a = "";
        try {
            String buffer = "";
            Log.i(TAGHTTP, "URL=" + ServerUrl.deleteUuid);
            URL url = new URL(ServerUrl.deleteUuid);
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
        Log.i(TAGHTTP, "onPostExecute end : "+text);
        //tv.setText(text);
    }
}