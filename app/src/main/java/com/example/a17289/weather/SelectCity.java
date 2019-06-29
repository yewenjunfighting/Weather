package com.example.a17289.weather;

import android.app.Activity;
import android.content.Intent;
import android.media.Image;
import android.media.ImageWriter;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

/**
 * Created by 17289 on 2019/6/29.
 */

public class SelectCity extends Activity implements View.OnClickListener{

    private ImageView mBackBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.select_city);

        mBackBtn = (ImageView) findViewById(R.id.title_back);
        mBackBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.title_back:
                Intent i = new Intent();
                i.putExtra("cityCode", "101160101");
                setResult(RESULT_OK, i);
                finish();
                break;
            default:
                break;
        }
    }
}
