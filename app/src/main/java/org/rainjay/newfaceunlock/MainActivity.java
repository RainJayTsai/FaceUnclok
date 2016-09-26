package org.rainjay.newfaceunlock;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void registerOnClick(View view) {
        Log.d("rainjay","start register");
        startActivity(new Intent().setClass(MainActivity.this,RegisterActivity.class));

    }
}