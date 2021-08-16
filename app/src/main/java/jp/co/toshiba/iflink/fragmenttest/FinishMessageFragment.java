package jp.co.toshiba.iflink.fragmenttest;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import androidx.fragment.app.Fragment;

import java.util.ArrayList;

public class FinishMessageFragment extends Fragment {
    EditText txtMessage;
    //-------------------------------
    //
    // リスナー
    //
    //-------------------------------
    public interface Callback {
        public void done(Object obj);
    }

    private FinishMessageFragment.Callback Callback;

    public void setCallbacks(FinishMessageFragment.Callback apiManagerCallback) {
        this.Callback = apiManagerCallback;
    }

    //-------------------------------
    //
    // onCreateView
    //
    //-------------------------------

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);


        return inflater.inflate(R.layout.fragment_finishmessage, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        txtMessage = view.findViewById(R.id.txtFinishMessage);
        String a = "";
        if(BasicData.finishMessage.containsKey(BasicData.currentRallyName)){
            a = BasicData.finishMessage.get(BasicData.currentRallyName);
        }
        txtMessage.setText(a);
    }

    @Override
    public void onPause() {
        super.onPause();


        BasicData.finishMessage.put(BasicData.currentRallyName, txtMessage.getText().toString());

        if(Callback!=null){
            Callback.done("ok");
        }
    }
}
