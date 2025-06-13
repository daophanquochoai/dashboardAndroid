package doctorhoai.learn.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import doctorhoai.learn.Activity.LoadingDialog;
import doctorhoai.learn.Activity.LoginActivity;
import doctorhoai.learn.Api.AccountService;
import doctorhoai.learn.Model.EmployeeCreate;
import doctorhoai.learn.Model.ErrorResponse;
import doctorhoai.learn.Model.Response;
import doctorhoai.learn.R;
import doctorhoai.learn.Utils.ShareData;
import retrofit2.Call;
import retrofit2.Callback;

import java.io.IOException;
import java.util.Objects;

public class ThemNhanVienFragment extends Fragment {

    private TextView tvTitle, tvErrorName, tvErrorEmail, tvErrorUsername, tvErrorPassword, tvErrorCccd;
    private EditText edtName, edtEmail, edtUsername, edtPassword, edtRePass, edtCccd;
    private Button btnCancel, btnSave;
    private String token;
    private ShareData shareData;
    private ObjectMapper mapper = new ObjectMapper();
    private LoadingDialog loadingDialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_them_nhan_vien, container, false);

        initView(view);
        Bundle bundle = getArguments();
        if( null != bundle ) {
            String type = bundle.getString("type");
            if( type.equals("ADD")){
                tvTitle.setText("THÊM NHÂN VIÊN");
                functionAdd();
            }else {
                tvTitle.setText("CẬP NHẬT NHÂN VIÊN");
            }
        }else{
            Objects.requireNonNull(getActivity()).getSupportFragmentManager().popBackStack();
        }

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Objects.requireNonNull(getActivity()).getSupportFragmentManager().popBackStack();
            }
        });



        return view;
    }

    public void initView ( View view ) {
        shareData = ShareData.getInstance(getContext());
        token = shareData.getToken();
        loadingDialog = new LoadingDialog(getActivity());
        tvTitle = view.findViewById(R.id.tvTitle);
        edtName = view.findViewById(R.id.edt_name);
        edtEmail = view.findViewById(R.id.edt_email);
        edtUsername = view.findViewById(R.id.edt_username);
        edtPassword = view.findViewById(R.id.edt_password);
        edtRePass = view.findViewById(R.id.edt_repass);
        edtCccd = view.findViewById(R.id.edt_cccd);
        btnCancel = view.findViewById(R.id.btn_cancel);
        btnSave = view.findViewById(R.id.btn_save);

        tvErrorName = view.findViewById(R.id.txt_error_name);
        tvErrorEmail = view.findViewById(R.id.txt_error_email);
        tvErrorUsername = view.findViewById(R.id.txt_error_username);
        tvErrorCccd = view.findViewById(R.id.txt_error_cccd);
        tvErrorPassword = view.findViewById(R.id.txt_error_pass);
    }

    public void resetForm () {
        tvErrorName.setText("");
        tvErrorName.setVisibility(View.GONE);
        tvErrorEmail.setText("");
        tvErrorEmail.setVisibility(View.GONE);
        tvErrorUsername.setText("");
        tvErrorUsername.setVisibility(View.GONE);
        tvErrorCccd.setText("");
        tvErrorCccd.setVisibility(View.GONE);
        tvErrorPassword.setText("");
        tvErrorPassword.setVisibility(View.GONE);
    }

    public boolean isValidEmail(String email) {
        String emailPattern = "[a-zA-Z0-9._-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,4}";
        return email.matches(emailPattern);
    }
    public void functionAdd(){
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadingDialog.startLoadingDialog();
                resetForm();
                if( edtName.getText().toString().length() < 5 ){
                    tvErrorName.setText("* Tên không hợp lệ");
                    tvErrorName.setVisibility(View.VISIBLE);
                    loadingDialog.dismissDialog();
                    return;
                }
                if( !isValidEmail(edtEmail.getText().toString()) ){
                    tvErrorEmail.setText("* Email không hợp lệ");
                    tvErrorEmail.setVisibility(View.VISIBLE);
                    loadingDialog.dismissDialog();
                    return;
                }
                if( edtUsername.getText().toString().length() < 5 ){
                    tvErrorUsername.setText("* Tài khoản nên có hơn 5 kí tự");
                    tvErrorUsername.setVisibility(View.VISIBLE);
                    loadingDialog.dismissDialog();
                    return;
                }
                if (edtPassword.getText().toString().length() < 8 ){
                    tvErrorPassword.setText("* Mật khẩu có hơn 8 kí tự");
                    tvErrorPassword.setVisibility(View.VISIBLE);
                    loadingDialog.dismissDialog();
                    return;
                }
                if( !edtRePass.getText().toString().equals(edtPassword.getText().toString()) ){
                    tvErrorPassword.setText("* Mật khẩu chưa khớp");
                    tvErrorPassword.setVisibility(View.VISIBLE);
                    loadingDialog.dismissDialog();
                    return;
                }
                if( edtCccd.getText().toString().length() > 12 || edtCccd.getText().toString().length() < 10 ){
                    tvErrorCccd.setText("* CCCD có độ dài từ 10 đến 12 kí tự");
                    tvErrorCccd.setVisibility(View.VISIBLE);
                    loadingDialog.dismissDialog();
                    return;
                }
                EmployeeCreate employeeCreate = new EmployeeCreate(edtName.getText().toString(),edtCccd.getText().toString(),edtEmail.getText().toString(),edtUsername.getText().toString(),edtPassword.getText().toString(),1,"ACTIVE");
                AccountService.apiService.addEmployment("Bearer " + token, employeeCreate).enqueue(new Callback<Response>() {
                    @Override
                    public void onResponse(Call<Response> call, retrofit2.Response<Response> response) {
                        if( response.isSuccessful()){
                            loadingDialog.dismissDialog();
                            Toast.makeText(getActivity(), "Thêm nhân viên thành công", Toast.LENGTH_SHORT).show();
                            Objects.requireNonNull(getActivity()).getSupportFragmentManager().popBackStack();
                        }else{
                            if( response.code() == 401 ){
                                Toast.makeText(getActivity(), "Hết phiên làm việc", Toast.LENGTH_SHORT).show();
                                shareData.clearToken();
                                Intent intent = new Intent(getActivity(), LoginActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                            }else if ( response.code() == 400){
                                String errorJson = null;
                                try {
                                    errorJson = response.errorBody().string();
                                } catch (IOException e) {
                                    throw new RuntimeException(e);
                                }
                                Gson gson = new Gson();
                                ErrorResponse errorResponse = gson.fromJson(errorJson, ErrorResponse.class);
                                Toast.makeText(getActivity(), errorResponse.getMessage(), Toast.LENGTH_SHORT).show();
                                loadingDialog.dismissDialog();
                            }
                            else{
                                Toast.makeText(getActivity(), "Hệ thống lỗi", Toast.LENGTH_SHORT).show();
                                loadingDialog.dismissDialog();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<Response> call, Throwable throwable) {
                        Toast.makeText(getActivity(), "Hết phiên làm việc", Toast.LENGTH_SHORT).show();
                        shareData.clearToken();
                        Intent intent = new Intent(getActivity(), LoginActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    }
                });
            }
        });
    }
}