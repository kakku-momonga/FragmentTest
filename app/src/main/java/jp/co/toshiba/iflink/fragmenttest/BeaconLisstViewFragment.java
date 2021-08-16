package jp.co.toshiba.iflink.fragmenttest;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.HashMap;

public class BeaconLisstViewFragment extends Fragment {
    ListView listView;

    public int id;
    HashMap<Integer, String> beaconMap;

    final String TAG = "BeaconLisstViewFragment";
    //-------------------------------
    //
    // リスナー
    //
    //-------------------------------
    private BeaconLisstViewFragment.SetBeaconIDlListener listener;

    public interface SetBeaconIDlListener {
        void setBeaconCallback(String uuid);
    }
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            // 親フラグメントにイベントを送信できるように呼び出し元であるSampleFragmentオブジェクトを取得し、
            // listenerのインスタンスを生成する
            listener = (BeaconLisstViewFragment.SetBeaconIDlListener) getTargetFragment();
        } catch (ClassCastException e) {
            // 親フラグメントがインターフェースを実装していない場合は例外を投げる
            listener = null;
        }
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        return inflater.inflate(R.layout.beacon_listview_context_menu, container, false);

    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        listView = view.findViewById(R.id.beaconListView);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String s = beaconMap.get(position);
                if(listener!=null){
                    listener.setBeaconCallback(s);
                }
                getFragmentManager().popBackStack();

            }
        });

        try {
            Bundle bundle = getArguments();
            id = bundle.getInt("id");
            ArrayList<String> s = bundle.getStringArrayList("data");
            beaconMap = new HashMap<>();
            Integer i = 0;
            for(String d : s){
                beaconMap.put(i, d);
                i++;
            }
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                    getContext(),
                    R.layout.beacon_list, s);
            listView.setAdapter(adapter);
        }catch (Exception ex){
            Log.e(TAG, ex.getLocalizedMessage());
        }

    }
}