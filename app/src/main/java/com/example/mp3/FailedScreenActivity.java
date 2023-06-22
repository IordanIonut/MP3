package com.example.mp3;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import com.bumptech.glide.Glide;

public class FailedScreenActivity extends AppCompatActivity {
    private ImageView imageImageNine;
    private TextView txtBLACKPINKHo, txt143BViews, txtDistance, txtFilesize;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_failed_screen);

        imageImageNine = findViewById(R.id.imageImageNine);
        txtBLACKPINKHo = (TextView) findViewById(R.id.txtBLACKPINKHo);
        txt143BViews = (TextView) findViewById(R.id.txt143BViews);
        txtDistance = (TextView) findViewById(R.id.txtDistance);
        txtFilesize = (TextView) findViewById(R.id.txtFilesize);

        String link = getIntent().getStringExtra("link");
        String thumbnailUrl = getIntent().getStringExtra("thumbnailUrl");

        Glide.with(this)
                .load(thumbnailUrl)
                .into(imageImageNine);

        txtBLACKPINKHo.setText(getIntent().getStringExtra("title"));
        txt143BViews.setText(getIntent().getStringExtra("viewCount"));
        txtDistance.setText(getIntent().getStringExtra("length"));
    }
    public void startAgain(View view){
        Intent intent = new Intent(FailedScreenActivity.this, InitialScreenActivity.class);
        startActivity(intent);
    }
}