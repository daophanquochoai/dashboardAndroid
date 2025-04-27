package doctorhoai.learn.Activity;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import android.text.InputType;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import doctorhoai.learn.Api.AccountService;
import doctorhoai.learn.Model.Login;
import doctorhoai.learn.Model.Token;
import doctorhoai.learn.R;
import doctorhoai.learn.Utils.JwtUtils;
import doctorhoai.learn.Utils.ShareData;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    EditText username, password;
    Button btnLogin;
    TextView txtErrorUserName, txtErrorPassword;
    boolean isPassword = true;
    LoadingDialog loadingDialog;
    ShareData shareData;
    JwtUtils jwtUtils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //checkLogin
        checkLogin();

        //Init view
        initView();

        //action
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                txtErrorUserName.setVisibility(View.GONE);
                txtErrorPassword.setVisibility(View.GONE);
                if( username.getText().length() < 6){
                    txtErrorUserName.setText("* Tài khoản nên có hơn 8 kí tự");
                    txtErrorUserName.setVisibility(View.VISIBLE);
                    return;
                }
                if( password.getText().length() < 6){
                    txtErrorPassword.setText("* Mật khẩu nên có hơn 8 kí tự");
                    txtErrorPassword.setVisibility(View.VISIBLE);
                    return;
                }
                loadingDialog.startLoadingDialog();
                Login login = new Login(username.getText().toString(), password.getText().toString());
                AccountService.apiService.login(login).enqueue(new Callback<Token>() {
                    @Override
                    public void onResponse(Call<Token> call, Response<Token> response) {
                        if( response.isSuccessful() ){
                            loadingDialog.dismissDialog();
                            Token token = response.body();
                            //save token
                            shareData = ShareData.getInstance(LoginActivity.this);
                            shareData.saveToken(token.getAccessToken());
                            shareData.saveUser(jwtUtils.getUser(token.getAccessToken()));

                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            startActivity(intent);
                        }else{
                            Toast.makeText(LoginActivity.this, "Tài khoản và mật khẩu chưa chính xác", Toast.LENGTH_SHORT).show();
                            loadingDialog.dismissDialog();
                        }
                    }

                    @Override
                    public void onFailure(Call<Token> call, Throwable throwable) {
                        loadingDialog.dismissDialog();
                        Toast.makeText(LoginActivity.this, "Không thể kết nối đến máy chủ", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        password.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if( event.getAction() == MotionEvent.ACTION_UP){
                    Drawable drawableEnd = password.getCompoundDrawables()[2];
                    if( drawableEnd != null){
                        if(event.getX() >= password.getWidth() - password.getCompoundPaddingEnd()) {
                            if( isPassword ){
                                password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                                password.setCompoundDrawablesWithIntrinsicBounds(R.drawable.lock_icon, 0, R.drawable.eye_on, 0);
                            }else{
                                password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                                password.setCompoundDrawablesWithIntrinsicBounds(R.drawable.lock_icon, 0, R.drawable.eye_off, 0);
                            }
                            isPassword = !isPassword;
                            return true;
                        }
                    }
                }
                return false;
            }
        });

    }

    public void initView() {
        username = findViewById(R.id.edt_username);
        password = findViewById(R.id.edt_password);
        btnLogin = findViewById(R.id.btn_login);
        txtErrorPassword = findViewById(R.id.txt_error_password);
        txtErrorUserName = findViewById(R.id.txt_error_username);
        txtErrorUserName.setVisibility(View.GONE);
        txtErrorPassword.setVisibility(View.GONE);
        loadingDialog = new LoadingDialog(LoginActivity.this);
    }

    public void checkLogin(){
        shareData = ShareData.getInstance(LoginActivity.this);
        String token = shareData.getToken();
        jwtUtils = JwtUtils.getInstance();
        if( jwtUtils.expireToken(token) ){
            shareData.clearToken();
        }
        else if( token != null ){
            shareData.saveUser(jwtUtils.getUser(token));
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }
    }
}