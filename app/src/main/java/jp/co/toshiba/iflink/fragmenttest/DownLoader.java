package jp.co.toshiba.iflink.fragmenttest;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.nfc.Tag;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.MessageDigest;

public class DownLoader extends AsyncTask<String, Void, Bitmap> {
    private ImageView imageView;
    Button btn;
    private Bitmap mask;
    String errorTxt;
    Context context;
    String uri;
    static public String fileName;

    File file;

    final String TAG = "Downloader";

    public interface Callback {
        public void success(Object obj);
        public void failed(Object obj);
    }

    private Callback Callback;

    public void setCallbacks(Callback apiManagerCallback) {
        this.Callback = apiManagerCallback;
    }


    public DownLoader(ImageView imageView, Button btn, Context context){
        this.imageView = imageView;
        this.btn = btn;
        this.context = context;
    }

    @Override
    protected Bitmap doInBackground(String... params) {
        // 受け取ったbuilderでインターネット通信する
        HttpURLConnection connection = null;
        InputStream inputStream = null;
        Bitmap bitmap = null;
        uri = params[0];
        HttpURLConnection con = null;

        try{

            URL url = new URL(uri);
            con = (HttpURLConnection) url.openConnection();


            con.setRequestMethod("POST");
            con.setInstanceFollowRedirects(false);
            //con.setDoOutput(true);
            con.setDoInput(true);

            //con.setRequestProperty("Content-Type", "application/json; charset=utf-8");
            con.setConnectTimeout(100000);
            //レスポンスデータ読み取りタイムアウトを設定する。
            con.setReadTimeout(100000);
            connection = (HttpURLConnection)url.openConnection();
            connection.setRequestMethod("GET");
            try {
                connection.connect();
                inputStream = connection.getInputStream();
            }catch (Exception ex){
                return null;
            }

            bitmap = BitmapFactory.decodeStream(inputStream);
        }catch (Exception ex){
            errorTxt = ex.getLocalizedMessage();
            //Toast.makeText(context, errorTxt, Toast.LENGTH_LONG).show();
            if(Callback!=null){
                Callback.failed(ex.getLocalizedMessage());
            }
            return null;
        }finally {
            if (connection != null){
                connection.disconnect();
            }
            try{
                if (inputStream != null){
                    inputStream.close();
                }
            }catch (IOException exception){
                return null;
            }
        }
        fileName = hash(uri) + ".png";
        try {
            file = new File(context.getFilesDir(), fileName);

            FileOutputStream outStream = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outStream);
            outStream.flush();
            outStream.close();

            //imageView.setImageBitmap(BitmapFactory.decodeFile(file.getPath()));

        }catch (Exception ex){
            errorTxt = ex.getLocalizedMessage();
            //Toast.makeText(context, ex.getLocalizedMessage(), Toast.LENGTH_LONG).show();
            if(Callback!=null){
                Callback.failed(ex.getLocalizedMessage());
            }

            return null;
        }

        return bitmap;
    }
    @Override
    protected void onCancelled() {
        Log.e(TAG, errorTxt);
        return;
    }

    @Override
    protected void onPostExecute(Bitmap result){
        if(result==null){
            Toast.makeText(context, errorTxt, Toast.LENGTH_LONG).show();
            if(Callback!=null){
                Callback.failed("result is null");
            }
            return;
        }
         try{
            Drawable drawable= new BitmapDrawable(BitmapFactory.decodeFile(file.getPath()));
            if(imageView!=null) {
                imageView.setImageDrawable(drawable);
                imageView.setVisibility(View.VISIBLE);
            }
        }catch (Exception ex){
            if(Callback!=null){
                Callback.failed(ex.getLocalizedMessage());
            }

        }
        try{
            if(btn!=null) {
                btn.setEnabled(true);
            }
        }catch (Exception ex){
            if(Callback!=null){
                Callback.failed(ex.getLocalizedMessage());
            }

        }
        if(Callback!=null){
            Callback.success("");
        }

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
            if(Callback!=null){
                Callback.failed(ex.getLocalizedMessage());
            }
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
