package com.aeolou.colorpick.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.aeolou.colorpick.R;
import com.aeolou.colorpick.view.ColorBarView;

public class ColorBarActivity extends AppCompatActivity implements View.OnClickListener {
    private ColorBarView mCbv_color_pick;
    private TextView mTv_text_color_a;
    private TextView mTv_text_color_b;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_color_bar);
        initView();
        initListener();
    }


    private void initView() {
        mCbv_color_pick = findViewById(R.id.cbv_color_pick);
        mTv_text_color_a = findViewById(R.id.tv_text_color_a);
        mTv_text_color_b = findViewById(R.id.tv_text_color_b);
        mTv_text_color_a.setBackgroundColor(-15981057);
        mTv_text_color_b.setBackgroundColor(-13959424);
    }

    private void initListener() {
        mTv_text_color_a.setOnClickListener(this);
        mTv_text_color_b.setOnClickListener(this);
        mCbv_color_pick.setOnColorChangerListener(new ColorBarView.OnColorChangeListener() {
            @Override
            public void onColorChange(int color) {
                mTv_text_color_b.setBackgroundColor(color);
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_text_color_a:
                mCbv_color_pick.setCurrentColor(((ColorDrawable) mTv_text_color_a.getBackground()).getColor());
                break;
            case R.id.tv_text_color_b:
                mCbv_color_pick.setCurrentColor(((ColorDrawable) mTv_text_color_b.getBackground()).getColor());
                break;
        }
    }
}
