package com.aeolou.colorpick.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.aeolou.colorpick.R;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private Button mBtn_color_bar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        initListener();
    }

    private void initView() {
        mBtn_color_bar = findViewById(R.id.btn_color_bar);
    }


    private void initListener() {
        mBtn_color_bar.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_color_bar:
                startActivity(new Intent(MainActivity.this, ColorBarActivity.class));
                break;
        }
    }
}
