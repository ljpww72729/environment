package com.ww.lp.environment.module;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.ww.lp.environment.R;
import com.ww.lp.environment.module.webview.NormalWVActvity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        TextView webView = (TextView) findViewById(R.id.webview);
        webView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, NormalWVActvity.class);
                intent.putExtra(NormalWVActvity.LOADURL,"http://www.wowofun.com/test/epapp/index.html");
                startActivity(intent);
            }
        });
    }
}
