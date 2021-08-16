package jp.co.toshiba.iflink.fragmenttest;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

public class OpenRallyFragment extends Fragment {
    static public ContextListData contextListData1;
    String rallyName;
    static HashMap<String,String> datas;
    ListView listView;
    Button btnDisp;
    Button btnDel;
    TextView txt;

    int position = -1;
    OpenRallyFragment thisClass;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        // 先ほどのレイアウトをここでViewとして作成します
        return inflater.inflate(R.layout.fragrent_rally_open, container, false);
    }



    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        listView = view.findViewById(R.id.listRally);
        txt = view.findViewById(R.id.txtSelectedRallyName);

        btnDisp = view.findViewById(R.id.btnDisp);
        btnDel = view.findViewById(R.id.btnDel);
        btnDisp.setEnabled(false);
        btnDel.setEnabled(false);

        position = -1;

        thisClass = this;

        txt.setText("");

        btnDisp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HashMap<String, String> item = BasicData.listData.get(thisClass.position);
                rallyName = item.get("name");
                BasicData.setCurrentRallyName(rallyName);
                OneRallyData oneRallyData = new OneRallyData();
                oneRallyData.fromBasicData(BasicData.currentRallyName);// BasicData確認用
                Type type = new TypeToken<Points>() {}.getType();
                Gson gson = new GsonBuilder().create();
                String a = datas.get(rallyName);
                if(a.equals("[{}]")){
                    SharedPreferences shared = getActivity().getSharedPreferences(Points.prefFile, Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = shared.edit();
                    editor.remove(Points.prefKeyData);
                    editor.apply();

                }else {
                    Points points = gson.fromJson(a, type);
                    SharedPreferences shared = getActivity().getSharedPreferences(Points.prefFile, Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = shared.edit();
                    editor.putString(Points.prefKeyData, a);
                    editor.apply();
                }
                btnDel.setEnabled(false);
                btnDisp.setEnabled(false);
                replaceFragment(new RallyInformationFragment());
                txt.setText("");

            }
        });

        btnDel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HashMap<String, String> item = BasicData.listData.get(thisClass.position);
                rallyName = item.get("name");
                String uuid = BasicData.rallyName.get(rallyName);
                BasicData.rallyName.remove(rallyName);
                int number = BasicData.numberData.get(rallyName);
                BasicData.numberData.remove(rallyName);
                BasicData.listData.remove(position);

                DeleteUuid du = new DeleteUuid();
                du.execute(uuid);
                ArrayList<HashMap<String, String>> wdata = new ArrayList<>();
                SimpleAdapter arrayAdapterw = new SimpleAdapter(getContext(), wdata,
                        R.layout.list,
                        new String[]{"name", "number"},
                        new int[]{R.id.txt1, R.id.txt2});
                listView.setAdapter(arrayAdapterw);
                BasicData.listData.clear();
                for (String key : BasicData.rallyName.keySet()) {
                    String a = String.format("uuid:%s, title:%s", BasicData.rallyName.get(key), key);
                    HashMap<String, String> m = new HashMap<>();
                    String d = String.format("%04d", BasicData.numberData.get(key));
                    m.put("name", key);
                    m.put("number", d);
                    BasicData.listData.add(m);
                }
                SimpleAdapter arrayAdapter = new SimpleAdapter(getContext(), BasicData.listData,
                        R.layout.list,
                        new String[]{"name", "number"},
                        new int[]{R.id.txt1, R.id.txt2});
                listView.setAdapter(arrayAdapter);
                txt.setText("");
                btnDel.setEnabled(false);
                btnDisp.setEnabled(false);

                /*
                Type type = new TypeToken<Points>() {}.getType();
                Gson gson = new GsonBuilder().create();
                if(datas.containsKey(rallyName)){
                    datas.remove(rallyName);
                }
                BasicData.listData = new ArrayList<>();
                HashMap<String, String> dataItem = new HashMap<>();
                for (String key : BasicData.rallyName.keySet()) {
                    String a = String.format("uuid:%s, title:%s", BasicData.rallyName.get(key), key);
                    HashMap<String, String> m = new HashMap<>();
                    String d = String.format("%04d", BasicData.numberData.get(key));
                    m.put("name", key);
                    m.put("number", d);
                    BasicData.listData.add(m);
                }
                SimpleAdapter arrayAdapter = new SimpleAdapter(getContext(), BasicData.listData,
                        R.layout.list,
                        new String[]{"name", "number"},
                        new int[]{R.id.txt1, R.id.txt2});
                listView.setAdapter(arrayAdapter);

                 */
            }


        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                thisClass.position = position;
                btnDel.setEnabled(true);
                btnDisp.setEnabled(true);
                HashMap<String, String> item = BasicData.listData.get(thisClass.position);
                txt.setText(item.get("name"));
            }
        });

        listTitle listTitle = new listTitle(getContext(), listView,ServerUrl.getRallyList);
        listTitle.execute(BasicData.mail, ServerUrl.MAKELIST);
    }
    // 表示させるFragmentを切り替えるメソッドを定義（表示したいFragmentを引数として渡す）
    private void replaceFragment(Fragment fragment) {
        Bundle bundle = new Bundle();
        bundle.putString("rally", rallyName);
        fragment.setArguments(bundle);
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
