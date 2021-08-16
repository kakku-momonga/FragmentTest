package jp.co.toshiba.iflink.fragmenttest;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import androidx.fragment.app.Fragment;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.SortedSet;
import java.util.prefs.Preferences;

public class GetStampMessaFragment extends Fragment {
    public int id;

    EditText ed;

    final String TAG = "GetStampMessaFragment";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);


        return inflater.inflate(R.layout.fragment_getstampmessage, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ArrayList<String> array = ContextFragment.pointsArray.get(BasicData.currentRallyName).getAllMessages();
        ArrayList<String> a = new ArrayList<>();
        for(String s : array){
            if (s==null || s.equals("")){
                continue;
            }
            a.add(StaticMethod.limittedString(s, 16));
        }

        ListView lv = view.findViewById(R.id.PointMessageListView);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                getContext(),
                R.layout.layout_pointsmessages, a);
        lv.setAdapter(adapter);

        //id = getArguments().getInt("id");

        String s = ContextFragment.pointsArray.get(BasicData.currentRallyName).pointData.get(id).pointMessage;

        ed = view.findViewById(R.id.pointMessage);

        ed.setText(s);

    }
    @Override
    public void onPause() {
        super.onPause();
        String s = ed.getText().toString();
        ContextFragment.pointsArray.get(BasicData.currentRallyName).pointData.get(id).pointMessage = s;
        Log.i(TAG, ContextFragment.pointsArray.get(BasicData.currentRallyName).pointData.get(id).pointMessage);
        save();
    }
    public void save(){
        SharedPreferences shared = getActivity().getSharedPreferences(Points.prefFile, Context.MODE_PRIVATE);

        String a = toJson();
        InsertData insertData = new InsertData(ServerUrl.setData);
        insertData.execute(BasicData.mail, BasicData.rallyName.get(BasicData.currentRallyName),
                Points.POINSDATA, a);
        SharedPreferences.Editor editor = shared.edit();
        editor.putString(Points.prefKeyData, a);
        editor.apply();

    }
    public String toJson(){
        Type type = new TypeToken<Points>() {}.getType();
        Gson gson = new GsonBuilder().create();
        JsonObject myCustomArray = gson.toJsonTree(ContextFragment.pointsArray.get(BasicData.currentRallyName)).getAsJsonObject();
        String a = myCustomArray.toString();
        return a;
    }

}
