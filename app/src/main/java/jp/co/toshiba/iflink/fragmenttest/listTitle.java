package jp.co.toshiba.iflink.fragmenttest;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
//--------------------------------------------------------------
//
// mailをキーに一覧を取得する
//
//--------------------------------------------------------------
public class listTitle extends AsyncTask<String, Void, String> {
    final String TAGHTTP = "LISTTITLE";
    String url;
    String user;
    int makeListFlag;

    static HashMap<String,ContextListData> list;

    Context context;
    ListView listView;
    //TextView tv ;
    //private Activity mActivity;
    public listTitle(Context context,ListView listView, String url) {
        //mActivity = activity;
        //tv = textView;
        this.context = context;
        this.url = url;
        this.listView = listView;
    }


    @Override
    protected String doInBackground(String... params) {
        Log.i(TAGHTTP, "doInBackground start");
        user = params[0];
        HashMap<String, String> map = new HashMap<>();
        map.put("user", user);

        try {
            makeListFlag = Integer.valueOf(params[1]);
        }catch (Exception ex){
            Log.i(TAGHTTP, ex.getLocalizedMessage());
            makeListFlag = 0;
        }

        HttpURLConnection con = null;

        list = new HashMap<>();
        //ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(context, R.layout.list);

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
        //---------------------------
        // BasicDataを初期設定する
        //---------------------------
        BasicData.setMail("");
        BasicData.setMail(user);
        //---------------------------
        // データ無しならリターン
        //---------------------------
        if(text.equals("[]")){
            return;
        }
        //---------------------------
        // JSONを展開する
        //---------------------------
        Type type = new TypeToken<HashMap<String,ContextListData>>(){}.getType();
        Gson gson = new GsonBuilder().create();
        list = new HashMap<>();
        try {
            HashMap<String, ContextListData> wl;
            list = gson.fromJson(text, type);
        }catch (Exception ex){
            Log.e(TAGHTTP, ex.getLocalizedMessage());
        }
        //---------------------------
        // BasicDataを展開する
        //---------------------------
        Type typeOne = new TypeToken<OneRallyData>() {}.getType();
        OpenRallyFragment.datas = new HashMap<>();

        for(String key : list.keySet()){
            ContextListData c = list.get(key);
            String sOne = list.get(key).data7;
            Gson g = new GsonBuilder().create();
            OneRallyData one = g.fromJson(sOne, typeOne);
            one.numberData = Integer.valueOf(c.digit.trim());
            one.toBasicData();
            OpenRallyFragment.datas.put(one.rallylName,list.get(key).data5);
        }

        //--------------------
        // 一覧リストを作成する
        //--------------------
        if(makeListFlag!=0) {

            BasicData.listData = new ArrayList<>();
            HashMap<String, String> dataItem = new HashMap<>();
            for (String key : BasicData.rallyName.keySet()) {
                String a = String.format("uuid:%s, title:%s", BasicData.rallyName.get(key), key);
                Log.i(TAGHTTP, a);
                HashMap<String, String> m = new HashMap<>();
                String d = String.format("%04d", BasicData.numberData.get(key));
                m.put("name", key);
                m.put("number", d);
                BasicData.listData.add(m);
            }
            ArrayList<HashMap<String, String>> wdata = new ArrayList<>();
            SimpleAdapter arrayAdapterw = new SimpleAdapter(context, wdata,
                    R.layout.list,
                    new String[]{"name", "number"},
                    new int[]{R.id.txt1, R.id.txt2});
            listView.setAdapter(arrayAdapterw);

            SimpleAdapter arrayAdapter = new SimpleAdapter(context, BasicData.listData,
                    R.layout.list,
                    new String[]{"name", "number"},
                    new int[]{R.id.txt1, R.id.txt2});
            listView.setAdapter(arrayAdapter);
        }

    }
}
