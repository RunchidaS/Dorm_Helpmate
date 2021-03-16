package com.example.dormhelpmate.storage;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.dormhelpmate.post;

public class MySharedPreferences {

    private static Context mContext;
    private static MySharedPreferences instance;
    private static SharedPreferences prefs;
    private static String PREFS_NAME = "WYM";

   public MySharedPreferences(){
       prefs = mContext.getSharedPreferences(PREFS_NAME,mContext.MODE_PRIVATE);
   }
   public  static  MySharedPreferences getInstance(Context context){
       mContext = context;
       if(prefs==null){
           instance = new MySharedPreferences();
       }
       return instance;
   }

    public void setUserData(String key, String value){
        prefs.edit().putString(key,value).apply();
    }
    public String getUserData(String key){
        return prefs.getString(key,"");
    }

}
