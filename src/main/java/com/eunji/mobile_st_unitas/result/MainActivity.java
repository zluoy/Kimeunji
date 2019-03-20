package com.eunji.mobile_st_unitas.result;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.eunji.mobile_st_unitas.R;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.security.MessageDigest;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();

        getAppKeyHash();

        adapter = new RecyclerAdapter(recyclerView, img, this);

        adapter.setOnLoadMoreListener(new LoadMoreItems() {
        @Override
        public void LoadItems() {
                if (img.size() <= 20) {
                img.add(null);
                adapter.notifyItemInserted(img.size() - 1);
                new Handler().postDelayed(new Runnable() {
        @Override
        public void run() {
            img.remove(img.size() - 1);
            adapter.notifyItemRemoved(img.size());

            //Generating more data
            int index = img.size();
            int end = index + 10;
            for (int i = index; i < end; i++) {
                img.add(new ImageItem(imgurl.get(i), imgHeight.get(i), imgWidth.get(i)));
            }

            adapter.notifyDataSetChanged();
            adapter.setLoaded();
            }
            }, 5000);
            } else {
            Toast.makeText(MainActivity.this, "Loading data completed", Toast.LENGTH_SHORT).show();
                }
            }
        });

//        TimerTask searchTask = new TimerTask() {
//
//            public void run() {
//
//                Log.e("searchTask ", "timer");
//
//                // 검색창을 확인
//                newWord = editText.getText().toString();
//
//                if(!newWord.equals(oldWord)) {
//                    // 검색어 변경되었음
//                    Log.d("new search","new!");
//                    searchNum = 0;
//                }
//
//                if(searchNum == 0 && newWord.equals(oldWord)) {
//                    // 검색어 변함없음
//                    Log.d("search", "search!");
//                    getImg(oldWord);
//                }
//
//                oldWord = newWord;
//            }
//
//        };
//
//        Timer timer = new Timer();
//        // 0초 시작, 1초마다 반복
//        timer.schedule(searchTask, 0, 1000);

    }


    private void initView() {
        editText = (EditText) findViewById(R.id.searchWord);
        img = new ArrayList<>();

        oldWord = editText.getText().toString();

//        progressBar = (ProgressBar) findViewById(R.id.progressBar);
//        progressBar.setVisibility(View.VISIBLE);

        gson = new Gson();

        recyclerView = findViewById(R.id.recyclerView);
//        adapter = new RecyclerAdapter(img, this);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        btn = (Button) findViewById(R.id.button);
        btn.setOnClickListener(this);

    }


    private void getImg(String word) {
//        Log.d("word1",word1);
//        Log.d("word2",word2);
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
                            Toast.makeText(getApplicationContext(), "검색어가 없습니다 !", Toast.LENGTH_SHORT).show();
                        else
                            Toast.makeText(getApplicationContext(), "오류가 있습니다 !", Toast.LENGTH_SHORT).show();
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

    private void getAppKeyHash() {
        try {
            PackageInfo info = getPackageManager().getPackageInfo(getPackageName(), PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md;
                md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                String something = new String(Base64.encode(md.digest(), 0));
                Log.e("Hash key", something);
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            Log.e("name not found", e.toString());
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button:
                String word = editText.getText().toString();
                getImg(word);
                break;
        }
    }
}


//adapter = new RecyclerAdapter(recyclerView, img, this);
//
//        adapter.setOnLoadMoreListener(new LoadMoreItems() {
//@Override
//public void LoadItems() {
//        if (img.size() <= 20) {
//        img.add(null);
//        adapter.notifyItemInserted(img.size() - 1);
//        new Handler().postDelayed(new Runnable() {
//@Override
//public void run() {
//        img.remove(img.size() - 1);
//        adapter.notifyItemRemoved(img.size());
//
//        //Generating more data
//        int index = img.size();
//        int end = index + 10;
//        for (int i = index; i < end; i++) {
//        img.add(new ImageItem(imgurl.get(i), imgHeight.get(i), imgWidth.get(i)));
//        }
//        adapter.notifyDataSetChanged();
//        adapter.setLoaded();
//        }
//        }, 5000);
//        } else {
//        Toast.makeText(MainActivity.this, "Loading data completed", Toast.LENGTH_SHORT).show();
//        }
//        }
//        });