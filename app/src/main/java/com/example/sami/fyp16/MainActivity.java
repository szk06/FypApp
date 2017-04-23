package com.example.sami.fyp16;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.StringBuilderPrinter;
import android.view.View;
import android.widget.EditText;

import com.example.sami.fyp16.classes.BackSignIN;
import com.example.sami.fyp16.classes.Session;

import java.util.Objects;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity {

    private EditText edit_username;
    private EditText edit_password;
    private String username_string;
    private String password_string;
    private SQLiteDatabase mydatabase;
    private Session session;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        edit_username = (EditText) findViewById(R.id.user_name);
        edit_password = (EditText) findViewById(R.id.password);

        mydatabase = openOrCreateDatabase("App_Users",MODE_PRIVATE,null);

        session = new Session(this);//Create the session
        if(session.loggedin()){
            Log.d("Going_to_App","Going to BackLocationInsert");
            Intent newActivity =  new Intent(MainActivity.this,UserUpdateLocation.class);
            MainActivity.this.startActivity(newActivity);
        }

        mydatabase.execSQL("CREATE TABLE IF NOT EXISTS users(Username VARCHAR);");
    }
    public void submit_bluetooth(View v){
        Intent newActivity = new Intent(MainActivity.this,BluetoothActivity.class);
        MainActivity.this.startActivity(newActivity);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void submit_user(View v){
        Log.d("Button_click","submit_user");
        username_string = edit_username.getText().toString();
        password_string = edit_password.getText().toString();
        BackSignIN backsign = new BackSignIN(this);
        backsign.execute(username_string,password_string);
        String out ="";
        try{
            out = backsign.get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        Log.d("Out_value_Login", out);
        if(Objects.equals(out, "sucess")){
            mydatabase.execSQL("DELETE FROM users");
            session.setLoggedin(true);
            mydatabase.execSQL("INSERT INTO users VALUES('" + username_string + "');");
            Intent newActivity =  new Intent(MainActivity.this,UserUpdateLocation.class);
            MainActivity.this.startActivity(newActivity);
        }else{
            Log.d("Error","wrongCredentials");
        }
    }
}
