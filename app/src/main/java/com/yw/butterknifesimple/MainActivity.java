package com.yw.butterknifesimple;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.yw.butterknife.ButterKnife;
import com.yw.butterknife_annotations.BindView;


public class MainActivity extends AppCompatActivity {
    @BindView(R.id.hello)
    TextView tv_hello;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        tv_hello.setText("您好啊");
        tv_hello.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this,"杨洛峋小宝宝真可爱",Toast.LENGTH_LONG).show();
            }
        });



    }
}
