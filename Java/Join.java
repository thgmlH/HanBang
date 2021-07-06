package com.example.hb;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;


public class Join extends AppCompatActivity {

    private EditText et_Name, et_Id, et_PassWord, et_PassWordCheck, et_Phone;
    private AlertDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join);
        getSupportActionBar().setTitle("회원가입");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); //툴바에 뒤로가기 추가

        //***변수 등록
        et_Name = findViewById(R.id.et_Name);
        et_Id = findViewById(R.id.et_Id);
        et_PassWord = findViewById(R.id.et_PassWord);
        et_PassWordCheck = findViewById(R.id.et_PassWordCheck);
        et_Phone = findViewById(R.id.et_Phone);

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.actionbar_actions, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        int id = item.getItemId();

        if (id == android.R.id.home) {
            AlertDialog.Builder builder = new AlertDialog.Builder(Join.this);
            dialog = builder.setTitle("경고")
                    .setMessage("확인 버튼을 누르면 회원가입이 취소됩니다.")
                    .setNegativeButton("취소", null)
                    .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            onBackPressed();
                        }
                    })
                    .create();
            dialog.show();
        }

        switch (item.getItemId()){
            case R.id.action_main:
                playBtn();
                return true;

            case R.id.action_myinformation:
                playBtn1();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void playBtn() {
        findViewById(R.id.action_main).setOnClickListener(view -> {
            Intent intent1 = new Intent(Join.this, Main.class);
            startActivity(intent1);
        });
    }


    private void playBtn1() {
        findViewById(R.id.action_myinformation).setOnClickListener(view -> {
            Intent intent1 = new Intent(Join.this, MyInformation.class);
            startActivity(intent1);
        });
    }


    //*** DB와 통신 - 아이디 확인
    public void onButtonIdClicked(View v){
        new JSONTask().execute("http://210.115.230.153:32430/id");
        //Toast.makeText(Join.this, "전세가 선택되었습니다.", Toast.LENGTH_SHORT).show();
    }


    public class JSONTask extends AsyncTask<String, String, String> {
        @Override
        protected String doInBackground(String... urls) {
            try {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("ID", et_Id.getText().toString());
                jsonObject.put("NAME", et_Name.getText().toString());
                jsonObject.put("Phone", et_Phone.getText().toString());
                jsonObject.put("Password", et_PassWord.getText().toString());


                HttpURLConnection con = null;
                BufferedReader reader = null;

                try{
                    URL url = new URL(urls[0]);
                    con = (HttpURLConnection) url.openConnection();
                    con.setRequestMethod("POST");
                    con.setRequestProperty("Cache-Control", "no-cache");
                    con.setRequestProperty("Content-Type", "application/json");
                    // con.setRequestProperty("Accept", "text/html");
                    con.setDoOutput(true);
                    con.setDoInput(true);
                    con.connect();

                    OutputStream outStream = con.getOutputStream();
                    BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outStream));
                    writer.write(jsonObject.toString());
                    writer.flush();
                    writer.close();

                    InputStream stream = con.getInputStream();

                    reader = new BufferedReader(new InputStreamReader(stream));

                    StringBuffer buffer = new StringBuffer();

                    String line = "";
                    while((line = reader.readLine()) != null){
                        buffer.append(line);
                    }

                    return buffer.toString();

                } catch (MalformedURLException e){
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    if(con != null){
                        con.disconnect();
                    }
                    try {
                        if(reader != null){
                            reader.close();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            et_Id.setText(result);
        }
    }

    public void onButtonPwCheckClicked(View view){
        if(!et_PassWordCheck.getText().toString().equals(et_PassWord.getText().toString())){
            //Toast.makeText(getApplicationContext(),"비밀번호가 일치하지 않습니다.",Toast.LENGTH_SHORT).show();

            AlertDialog.Builder builder = new AlertDialog.Builder(Join.this);
            dialog = builder.setMessage("비밀번호가 일치하지 않습니다.")
                    .setPositiveButton("확인",null)
                    .create();
            dialog.show();
            et_PassWordCheck.setTextColor(Color.RED);
        }

        else if(et_PassWordCheck.getText().toString().equals("") || et_PassWord.getText().toString().equals("")){
            AlertDialog.Builder builder = new AlertDialog.Builder(Join.this);
            dialog = builder.setMessage("비밀번호를 입력하세요.")
                    .setPositiveButton("확인",null)
                    .create();
            dialog.show();
        }

        else{
            et_PassWordCheck.setTextColor(Color.BLACK);
            Toast.makeText(getApplicationContext(),"비밀번호가 일치합니다.",Toast.LENGTH_SHORT).show();
        }
    }



    //***회원가입 버튼 누를 떄
    public void JoinButton(View view){
        Toast.makeText(getApplicationContext(),"회원가입을 성공하셨습니다.",Toast.LENGTH_SHORT).show();
        new JSONTask().execute("http://210.115.230.153:32430/join");

        Intent intent = new Intent(this, Login.class);
        startActivity(intent);

    }
}
