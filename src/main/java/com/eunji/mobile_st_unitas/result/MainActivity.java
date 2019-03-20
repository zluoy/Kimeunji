package com.eunji.mobile_st_unitas.result;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.util.Log;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.eunji.mobile_st_unitas.R;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    private EditText editText;

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


        // paging 코드.
//        adapter = new RecyclerAdapter(recyclerView, img, this);
//
//        adapter.setOnLoadMoreListener(new LoadMoreItems() {
//        @Override
//        public void LoadItems() {
//                if (img.size() <= 20) {
//                img.add(null);
//                adapter.notifyItemInserted(img.size() - 1);
//                new Handler().postDelayed(new Runnable() {
//        @Override
//        public void run() {
//            img.remove(img.size() - 1);
//            adapter.notifyItemRemoved(img.size());
//
//            //Generating more data
//            int index = img.size();
//            int end = index + 10;
//            for (int i = index; i < end; i++) {
//                img.add(new ImageItem(imgurl.get(i), imgHeight.get(i), imgWidth.get(i)));
//            }
//
//            adapter.notifyDataSetChanged();
//            adapter.setLoaded();
//            }
//            }, 5000);
//            } else {
//            Toast.makeText(MainActivity.this, "Loading data completed", Toast.LENGTH_SHORT).show();
//                }
//            }
//        });

        TimerTask searchTask = new TimerTask() {

            public void run() {

                Log.e("searchTask ", "timer");

                // 검색창을 확인
                newWord = editText.getText().toString();

                if(!newWord.equals(oldWord)) {
                    // 검색어 변경되었음
                    Log.d("new search","new!");
                    searchNum = 0;
                }

                if(searchNum == 0 && newWord.equals(oldWord)) {
                    // 검색어 변함없음
                    Log.d("search", "search!");
                    getImg(oldWord);
                }

                oldWord = newWord;
            }

        };

        Timer timer = new Timer();
        // 0초 시작, 1초마다 반복
        timer.schedule(searchTask, 0, 1000);

    }


    private void initView() {
        editText = (EditText) findViewById(R.id.searchWord);
        img = new ArrayList<>();

        oldWord = editText.getText().toString();

        gson = new Gson();

        recyclerView = findViewById(R.id.recyclerView);
        adapter = new RecyclerAdapter(img, this);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

    }


    // API 연동하여 이미지 url 가져와 보여주는 함수.
    private void getImg(String word) {
        searchNum++;

        // Retrofit 이용하여 통신, API에 데이터 요청
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
                    // 검색어가 없거나 오류인 경우
                    // 요청 결과 메세지가 Bad Request
                    if(response.message().equals("Bad Request")) {
                        if(editText.getText().toString().equals(""))
                            Toast.makeText(getApplicationContext(), "검색어가 없습니다 !", Toast.LENGTH_SHORT).show();
                        else
                            Toast.makeText(getApplicationContext(), "오류가 있습니다 !", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        // 요청이 잘 처리된 경우
                        JsonObject result = response.body();
                        Log.d("RESULT: ", response.body().toString());

                        JsonArray ja = result.getAsJsonArray("documents");

                        for (int i = 0; i < ja.size() ; i++) {
                            kakaoData = gson.fromJson(ja.get(i), KakaoData.class);
                            // 이미지 리사이징 위해 width, height 구함
                            imgHeight.add(kakaoData.getHeight());
                            imgWidth.add(kakaoData.getWidth());
                            imgurl.add(kakaoData.getImage_url());
                            img.add(new ImageItem(imgurl.get(i), imgHeight.get(i), imgWidth.get(i)));

                        }
                        // 검색을 마친 검색어를 oldWord에 넣음
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

    // Hash Key 값 찾는 함수.
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