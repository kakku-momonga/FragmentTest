package jp.co.toshiba.iflink.fragmenttest;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import java.util.Map;
import java.util.Set;

public class MenuFragment extends Fragment {
    ImageView imageViewNew ;
    ImageView imageViewOpen;
    ImageView imageViewLogout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        SharedPreferences shared;
        shared = getActivity().getSharedPreferences("DataSave", Context.MODE_PRIVATE);
        RegisterFragment.mail = shared.getString(RegisterFragment.prefKeyMail, "");
        RegisterFragment.password = shared.getString(RegisterFragment.prefKeyPassword, "");
        RegisterFragment.preferOk = shared.getBoolean(RegisterFragment.prefKeyOk, false);

        BasicData.setMail(RegisterFragment.mail);

        // 先ほどのレイアウトをここでViewとして作成します
        return inflater.inflate(R.layout.fragment_menu, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        imageViewNew = (ImageView) view.findViewById(R.id.imageViewNew);
        imageViewOpen = (ImageView) view.findViewById(R.id.imageViewOpen);

        imageViewOpen.setEnabled(true);
        imageViewOpen.setEnabled(true);

        imageViewLogout = view.findViewById(R.id.imageViewLogout);

        imageViewNew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageViewOpen.setEnabled(false);
                imageViewOpen.setEnabled(false);
                RallyInformationFragment.shinki = true;
                replaceFragment(new RallyInformationFragment());
            }
        });

        imageViewOpen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageViewOpen.setEnabled(false);
                imageViewOpen.setEnabled(false);
                RallyInformationFragment.shinki = false;
                replaceFragment(new OpenRallyFragment());
            }
        });

        imageViewLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageViewOpen.setEnabled(false);
                imageViewOpen.setEnabled(false);
                replaceFragment(new LoginFragment());
            }
        });
    }
    // 表示させるFragmentを切り替えるメソッドを定義（表示したいFragmentを引数として渡す）
    private void replaceFragment(Fragment fragment) {
        Bundle bundle = new Bundle();
        bundle.putString("rally", "");
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
