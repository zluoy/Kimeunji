package com.eunji.mobile_st_unitas;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainViewModel implements View.OnClickListener  {
    private Activity activity;
    private MainModel mainModel;

    private EditText editText;
    private Button btn;

    private String oldWord, newWord;

    private Gson gson;

    private Retrofit retrofit;
    private KakaoAPI kakaoAPI;
    private KakaoData kakaoData;

    private RecyclerView recyclerView;
    private RecyclerAdapter adapter;

    private ArrayList<ImageItem> img;

    private ArrayList<String> imgurl = new ArrayList<>();
    private ArrayList<Integer> imgHeight = new ArrayList<>();
    private ArrayList<Integer> imgWidth = new ArrayList<>();

    private int searchNum = 0;
    private ProgressBar progressBar;

    public MainViewModel(Activity activity) {
        this.activity = activity;
        this.mainModel = new MainModel();
        initView(activity);
    }

    private void initView(Activity activity) {
        editText = (EditText) activity.findViewById(R.id.searchWord);
        img = new ArrayList<>();

        oldWord = editText.getText().toString();

//        progressBar = (ProgressBar) findViewById(R.id.progressBar);
//        progressBar.setVisibility(View.VISIBLE);

        gson = new Gson();

        recyclerView = activity.findViewById(R.id.recyclerView);
        adapter = new RecyclerAdapter(img, activity.getApplicationContext());

        recyclerView.setLayoutManager(new LinearLayoutManager(activity.getApplicationContext()));
        recyclerView.setAdapter(adapter);

        btn = (Button) activity.findViewById(R.id.button);
        btn.setOnClickListener(this);

    }


    private void getImg(String word) {
        searchNum++;

        retrofit =new Retrofit.Builder().addConverterFactory(GsonConverterFactory.create())
                .baseUrl(KakaoAPI.url)
                .build();
        kakaoAPI = retrofit.create(KakaoAPI.class);
        Call<JsonObject> call = kakaoAPI.getJson("KakaoAK "+ KakaoAPI.Key,word);

        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                Log.d("State ",response.toString());
                System.out.println(response.message());

                if(response.isSuccessful()) {
                    if(response.message().equals("Bad Request")) {
                        if(editText.getText().toString().equals(""))
                            Toast.makeText(activity.getApplicationContext(), "검색어가 없습니다 !", Toast.LENGTH_SHORT).show();
                        else
                            Toast.makeText(activity.getApplicationContext(), "오류가 있습니다 !", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        JsonObject result = response.body();
                        Log.d("RESULT: ", response.body().toString());

                        JsonArray ja = result.getAsJsonArray("documents");

                        for (int i = 0; i < ja.size() ; i++) {
                            kakaoData = gson.fromJson(ja.get(i), KakaoData.class);
                            imgHeight.add(kakaoData.getHeight());
                            imgWidth.add(kakaoData.getWidth());
                            imgurl.add(kakaoData.getImage_url());
                            System.out.println(kakaoData.getImage_url());
                            System.out.println("WIDTH: "+kakaoData.getWidth());
                            System.out.println("HEIGHT: "+ kakaoData.getHeight());
                            img.add(new ImageItem(imgurl.get(i), imgHeight.get(i), imgWidth.get(i)));

                        }


                        oldWord = newWord;
                        adapter.setItems(img);
                    }
                }
            }
            @Override
            public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {
            }
        });
    }

    @Override
    public void onClick(View v) {

    }
}