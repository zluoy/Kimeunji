package com.eunji.mobile_st_unitas;

import android.support.v7.widget.RecyclerView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.eunji.mobile_st_unitas.result.ImageItem;
import com.eunji.mobile_st_unitas.result.KakaoAPI;
import com.eunji.mobile_st_unitas.result.KakaoData;
import com.eunji.mobile_st_unitas.result.RecyclerAdapter;
import com.google.gson.Gson;

import java.util.ArrayList;

import retrofit2.Retrofit;

public class MainModel {

    EditText editText;
    Button btn;

    String oldWord, newWord;

    Gson gson;

    Retrofit retrofit;
    KakaoAPI kakaoAPI;
    KakaoData kakaoData;

    RecyclerView recyclerView;
    RecyclerAdapter adapter;

    ArrayList<ImageItem> img;

    ArrayList<String> imgurl = new ArrayList<>();
    ArrayList<Integer> imgHeight = new ArrayList<>();
    ArrayList<Integer> imgWidth = new ArrayList<>();

    int searchNum = 0;
    ProgressBar progressBar;

}
