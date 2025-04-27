package doctorhoai.learn.Utils;

import android.content.Context;
import android.content.SharedPreferences;
import com.google.gson.Gson;
import doctorhoai.learn.Model.User;

public class ShareData {

    private static ShareData instance;

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    private ShareData(Context context) {
        sharedPreferences = context.getSharedPreferences("token", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    public static synchronized ShareData getInstance(Context context) {
        if(instance == null) {
            instance = new ShareData(context);
        }
        return instance;
    }

    public void saveToken( String token){
        editor.putString("token", token);
        editor.apply();
    }

    public String getToken(){
        return sharedPreferences.getString("token", null);
    }

    public void clearToken() {
        editor.remove("token");
        editor.apply();
    }

    public void saveUser ( User user){
        Gson gson = new Gson();
        String json = gson.toJson(user);
        editor.putString("user", json);
        editor.apply();
    }

    public User getUser(){
        Gson gson = new Gson();
        String json = sharedPreferences.getString("user", null);
        return gson.fromJson(json, User.class);
    }
}
