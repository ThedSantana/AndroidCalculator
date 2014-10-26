package com.example.dbkats.calc;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import android.content.Intent;
import android.widget.EditText;
import android.util.Log;

public class MyActivity extends Activity {

    public static String EXTRA_MSG = "hello";

    public void sendMessage(View view) {

        Intent intent = new Intent(this, DisplayMessageActivity.class);
        EditText editText1 = (EditText) findViewById(R.id.num1);
        String message1 = editText1.getText().toString();

        EditText editText2 = (EditText) findViewById(R.id.num2);
        String message2 = editText2.getText().toString();

        int a = Integer.parseInt(message1);
        int b = Integer.parseInt(message2);
        int c = a + b;

        intent.putExtra(EXTRA_MSG, "" + c);

//        Log.d("debug", message);
//        intent.putExtra(EXTRA_MSG, message);
        startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.my, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
