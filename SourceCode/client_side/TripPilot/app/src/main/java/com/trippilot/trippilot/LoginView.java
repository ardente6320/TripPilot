package com.trippilot.trippilot;

import android.app.Activity;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Member;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPrivateKeySpec;
import java.security.spec.RSAPublicKeySpec;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

public class LoginView extends AppCompatActivity implements View.OnClickListener {
    EditText edt_id, edt_pwd;
    Button login_btn, register_btn;
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    String id, pwd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        pref = getSharedPreferences("TripPilotAutoLogin", Activity.MODE_PRIVATE);
        id = pref.getString("TripPilotID", null);
        pwd = pref.getString("TripPilotPWD", null);
        if (id != null && pwd != null) {
            try {
                ApplicationInfo appInfo = getPackageManager().getApplicationInfo(getPackageName(), PackageManager.GET_META_DATA);
                if (appInfo.metaData != null) {
                    JSONObject json_data = new ServerConnector(1).execute(appInfo.metaData.getString("select_member"), "member_id", id).get();
                }
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        setContentView(R.layout.login_view);
        edt_id = (EditText) findViewById(R.id.edt_id);
        edt_pwd = (EditText) findViewById(R.id.edt_pwd);
        login_btn = (Button) findViewById(R.id.login_btn);
        register_btn = (Button) findViewById(R.id.register_btn);

        login_btn.setOnClickListener(this);
        register_btn.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        int btn_id = view.getId();
        switch (btn_id) {
            case R.id.login_btn:
                id = edt_id.getText().toString();
                pwd = edt_pwd.getText().toString();
                try {
                    ApplicationInfo appInfo = getPackageManager().getApplicationInfo(getPackageName(), PackageManager.GET_META_DATA);
                    if (appInfo.metaData != null) {
                        new ServerConnector(2).execute(appInfo.metaData.getString("select_member"), "member_id", id);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case R.id.register_btn:
                Intent intent = new Intent(this, MemberView.class).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                intent.putExtra("type", 0);
                startActivityForResult(intent, 100);
        }
    }
    public void errorHandle(String msg){
        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(LoginView.this);
        builder.setTitle("오류");
        builder.setMessage(msg);
        builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        builder.show();
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == 100) {
                editor = pref.edit();
                editor.putString("TripPilotID", data.getStringExtra("id"));
                editor.putString("TripPilotPWD", data.getStringExtra("password"));
                editor.commit();
            }
        }
        Intent intent = new Intent(LoginView.this, MainActivity.class);
        intent.putExtra("member_id", id);
        startActivity(intent);
        finish();
    }
    class ServerConnector extends AsyncTask<String,Void,JSONObject> {
        int type;
         public ServerConnector(int type) {
            this.type = type;
         }
        @Override
        protected JSONObject doInBackground(String... strings) {
            HttpURLConnection con = null;
            try {
                URL myurl = new URL(strings[0]);
                con = (HttpURLConnection) myurl.openConnection();
                con.setDefaultUseCaches(false);
                con.setDoInput(true);                         // 서버에서 읽기 모드 지정
                con.setDoOutput(true);                       // 서버로 쓰기 모드 지정
                con.setRequestMethod("POST");
                con.setRequestProperty("content-type", "application/x-www-form-urlencoded; charset=UTF-8");
                StringBuffer buffer = new StringBuffer();

                for(int i =1; i<strings.length;i+=2){
                    buffer.append(strings[i]).append("=").append(strings[i+1]);
                    if(i < strings.length-2)
                        buffer.append("&");
                }

                PrintWriter pw = new PrintWriter(new OutputStreamWriter(con.getOutputStream(), "UTF-8"));
                pw.write(buffer.toString());
                pw.flush();

                int response = con.getResponseCode();
                if (response >=200 && response <=300) {
                    StringBuilder builder = new StringBuilder();
                    try (BufferedReader reader = new BufferedReader(
                            new InputStreamReader(con.getInputStream(),"UTF-8"))) {
                        String line;
                        while ((line = reader.readLine()) != null) {
                            builder.append(line);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    return new JSONObject(builder.toString());
                } else {
                    Log.e("TAG-Server-error", "Connection Error!");
                }

            } catch (Exception e) {

                e.printStackTrace();

            } finally {
                con.disconnect();
            }
            return null;
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            JSONObject json_data = jsonObject;
            if (type == 1) {
                try {
                    if (json_data.getString("result").equals("success")) {
                        if (json_data.getString("password").equals(pwd)) {
                            Intent intent = new Intent(LoginView.this, MainActivity.class);
                            intent.putExtra("member_id", id);
                            startActivity(intent);
                            finish();
                        }
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            } else if (type == 2) {
                try {
                    if (json_data.getString("result").equals("success")) {
                        if (json_data.getString("password").equals(pwd)) {
                            editor = pref.edit();
                            editor.putString("TripPilotID", id);
                            editor.putString("TripPilotPWD", pwd);
                            editor.commit();
                            Intent intent = new Intent(LoginView.this, MainActivity.class);
                            intent.putExtra("member_id", id);
                            startActivity(intent);
                            finish();
                        } else {
                            errorHandle("아이디 또는 패스워드가 틀렸습니다");
                            return;
                        }
                    } else {
                        errorHandle("아이디 또는 패스워드가 틀렸습니다");
                        return;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}