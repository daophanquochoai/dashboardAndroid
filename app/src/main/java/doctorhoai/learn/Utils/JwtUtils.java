package doctorhoai.learn.Utils;

import android.util.Base64;
import com.google.gson.JsonIOException;
import doctorhoai.learn.Model.User;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class JwtUtils {

    private static JwtUtils instance;

    private JwtUtils() {}

    public static synchronized JwtUtils getInstance() {
        if(instance == null) {
            instance = new JwtUtils();
        }
        return instance;
    }

    public JSONObject decodeJwt(String jwt) {
        try {
            // Tách phần payload (ở giữa)
            String[] parts = jwt.split("\\.");
            if (parts.length < 2) return null;

            String payload = parts[1];

            // Thêm padding nếu thiếu
            int padding = 4 - (payload.length() % 4);
            if (padding > 0 && padding < 4) {
                for (int i = 0; i < padding; i++) {
                    payload += "=";
                }
            }

            // Decode Base64URL -> JSON
            byte[] decodedBytes = Base64.decode(payload, Base64.URL_SAFE);
            String decodedJson = new String(decodedBytes);

            return new JSONObject(decodedJson);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public boolean expireToken (String token) {
        if( token == null || token.isEmpty() ) { return true; }
        JSONObject obj = decodeJwt(token);
        try{
            User user = changeToUser(obj);
            if (user == null || user.getExp() == null) return true;
            System.out.println(user);
            long now = System.currentTimeMillis() / 1000;
            return now > user.getExp();
        }catch ( JSONException e){
            e.printStackTrace();
            return true;
        }
    }

    public User getUser ( String token ){
        if( token == null || token.isEmpty() ) { return null; }
        JSONObject obj = decodeJwt(token);
        try{
            return changeToUser(obj);
        }catch (JSONException e){
            e.printStackTrace();
            return null;
        }
    }

    public User changeToUser ( JSONObject jsonObject) throws JSONException {
        User user = new User();
        user.setCccd(jsonObject.getString("cccd"));
        user.setEmail(jsonObject.getString("email"));
        user.setName(jsonObject.getString("name"));
        user.setId(jsonObject.getString("id"));
        user.setSub(jsonObject.getString("sub"));
        user.setExp(jsonObject.getInt("exp"));
        user.setRoles( convertJsonArrayToList((jsonObject.optJSONArray("roles"))));
        return user;
    }

    public List<String> convertJsonArrayToList(JSONArray jsonArray) {
        List<String> list = new ArrayList<>();
        if (jsonArray != null) {
            for (int i = 0; i < jsonArray.length(); i++) {
                list.add(jsonArray.optString(i));
            }
        }
        return list;
    }
}
