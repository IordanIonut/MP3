package com.example.mp3;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;
import okhttp3.*;
import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class GrabbingStageActivity extends AppCompatActivity {
    private EditText etForm, etFormOne;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grabbing_stage);
        etForm = (EditText) findViewById(R.id.etForm);
        etFormOne = (EditText) findViewById(R.id.etFormOne);
        etFormOne.setText(" ");
        etForm.setText(getIntent().getStringExtra("formValue"));
        etFormOne.setText(getIntent().getStringExtra("formOneValue"));
        etFormOne.setEnabled(false);
        etForm.setEnabled(false);
        String videoId = getIntent().getStringExtra("formValue").substring(getIntent().getStringExtra("formValue").indexOf("=") + 1);
        retrieveVideoData(videoId);
    }
    private void retrieveVideoData(String videoUrl) {
        OkHttpClient client = new OkHttpClient();
        HttpUrl.Builder urlBuilder = HttpUrl.parse("https://youtube-video-download-info.p.rapidapi.com/dl").newBuilder();
        urlBuilder.addQueryParameter("id", videoUrl);
        Request request = new Request.Builder()
                .url(urlBuilder.build())
                .addHeader("X-RapidAPI-Key", "14ce7d2fcfmsh80dc245e906e431p10efbdjsn558add0c89e3")
                .addHeader("X-RapidAPI-Host", "youtube-video-download-info.p.rapidapi.com")
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    try {
                        String responseData = response.body().string();
                        JSONObject jsonObject = new JSONObject(responseData);

                        String length = jsonObject.getString("length");
                        String thumbnailUrl = jsonObject.getString("thumb");
                        String viewCount = jsonObject.optString("view_count", "0");
                        String title = jsonObject.getString("title");

                        HttpUrl.Builder urlBuilder = HttpUrl.parse("https://youtube-mp3-downloader2.p.rapidapi.com/ytmp3/ytmp3/").newBuilder();
                        urlBuilder.addQueryParameter("url", "https://www.youtube.com/watch?v="+videoUrl);
                        Request request = new Request.Builder()
                                .url(urlBuilder.build())
                                .addHeader("X-RapidAPI-Key", "14ce7d2fcfmsh80dc245e906e431p10efbdjsn558add0c89e3")
                                .addHeader("X-RapidAPI-Host", "youtube-mp3-downloader2.p.rapidapi.com")
                                .build();

                        client.newCall(request).enqueue(new Callback() {
                            @Override
                            public void onFailure(Call call, IOException e) {
                                e.printStackTrace();
                            }
                            @Override
                            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                                if (response.isSuccessful()) {
                                    try {
                                        String responseData = response.body().string();
                                        JSONObject jsonObject = new JSONObject(responseData);
                                        String link = jsonObject.getString("link");
                                        String size = jsonObject.getString("size");
                                        Intent intent = new Intent(GrabbingStageActivity.this, DowloadinStageActivity.class);
                                        intent.putExtra("thumbnailUrl", thumbnailUrl);
                                        intent.putExtra("length", length);
                                        intent.putExtra("title", title);
                                        intent.putExtra("viewCount", viewCount);
                                        intent.putExtra("link", link);
                                        intent.putExtra("size",size);
                                        intent.putExtra("formValue", getIntent().getStringExtra("formValue"));
                                        intent.putExtra("formOneValue", getIntent().getStringExtra("formOneValue"));
                                        startActivity(intent);
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                        Log.d("test123", "Failed to parse JSON response");
                                    }
                                }
                            }
                        });
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Log.d("test123", "Failed to parse JSON response");
                    }
                } else {
                    Log.d("test123","Dont work request Api");
                }
            }
        });
    }
}