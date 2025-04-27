package doctorhoai.learn.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.widget.*;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.google.gson.Gson;
import doctorhoai.learn.Activity.LoadingDialog;
import doctorhoai.learn.Activity.LoginActivity;
import doctorhoai.learn.Api.FilmService;
import doctorhoai.learn.Model.ErrorResponse;
import doctorhoai.learn.Model.Response;
import doctorhoai.learn.Model.TypeFilm;
import doctorhoai.learn.R;
import doctorhoai.learn.Utils.ShareData;
import retrofit2.Call;
import retrofit2.Callback;

import java.io.IOException;
import java.util.List;
import java.util.Objects;


public class AddTypeFilmFragment extends Fragment {

    private EditText edtId, edtTypeFilm;
    private Spinner spinner;
    private TextView tvTitle, tvId, tvErrorName;
    private Button btnAdd, btnCancel;
    private String token;
    private TypeFilm typeFilm = new TypeFilm(null,"","ACTIVE");
    private ShareData shareData;
    private LoadingDialog loadingDialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_type_film, container, false);

        initView(view);
        
        Bundle bundle = getArguments();
        if( null != bundle ) {
            String type = bundle.getString("type");
            if( type == "ADD"){
                tvTitle.setText("Thêm loại phim");
                btnAdd.setText("Thêm");
                tvId.setVisibility(View.GONE);
                edtId.setVisibility(View.GONE);
                functionAdd();
            }else{
                tvTitle.setText("Cập nhật loại phim");
                btnAdd.setText("Cập nhật");
                TypeFilm typeF = (TypeFilm) bundle.getSerializable("data");
                edtId.setText(typeF.getId());
                edtId.setEnabled(false);
                edtTypeFilm.setText(typeF.getName());
                spinner.setSelection(typeF.getActive().toString().equals("ACTIVE") ? 0 : 1);
                functionUpdate();
            }
        }else{
            Objects.requireNonNull(getActivity()).getSupportFragmentManager().popBackStack();
        }

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if( position == 0 ){
                    typeFilm.setActive("ACTIVE");
                }else {
                    typeFilm.setActive("DELETE");
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Objects.requireNonNull(getActivity()).getSupportFragmentManager().popBackStack();
            }
        });

        return view;
    }

    public void initView ( View view) {
        shareData = ShareData.getInstance(getActivity());
        token = shareData.getToken();
        edtId = view.findViewById(R.id.edt_id);
        edtTypeFilm = view.findViewById(R.id.edt_type_film);
        spinner = view.findViewById(R.id.spinner_type_film);
        List<String> list = List.of("Hoạt động", "N.Hoạt động");
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                getActivity(),
                android.R.layout.simple_spinner_item,
                list
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        tvTitle = view.findViewById(R.id.tvTitle);
        btnAdd = view.findViewById(R.id.btn_save);
        btnCancel = view.findViewById(R.id.btn_cancel);
        tvId = view.findViewById(R.id.tvId);
        loadingDialog = new LoadingDialog(getActivity());
        tvErrorName = view.findViewById(R.id.txt_error_name);
    }

    public void functionAdd(){
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadingDialog.startLoadingDialog();
                typeFilm.setName(edtTypeFilm.getText().toString());
                if( edtTypeFilm.getText().toString().equals("")){
                    tvErrorName.setVisibility(View.VISIBLE);
                    tvErrorName.setText("Loại phim không thể trống");
                }else{
                    FilmService.apiService.addFilm("Bearer " + token, typeFilm).enqueue(new Callback<Response>() {
                        @Override
                        public void onResponse(Call<Response> call, retrofit2.Response<Response> response) {
                            if( response.isSuccessful()){
                                loadingDialog.dismissDialog();
                                Toast.makeText(getActivity(), "Thêm loại phim thành công", Toast.LENGTH_SHORT).show();
                                Objects.requireNonNull(getActivity()).getSupportFragmentManager().popBackStack();
                            }else   if( response.code() == 401 ){
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

                        @Override
                        public void onFailure(Call<Response> call, Throwable throwable) {
                            Toast.makeText(getActivity(), "Hệ thống lỗi", Toast.LENGTH_SHORT).show();
                            loadingDialog.dismissDialog();
                        }
                    });
                }

            }
        });
    }

    public void functionUpdate(){
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadingDialog.startLoadingDialog();
                typeFilm.setName(edtTypeFilm.getText().toString());
                if( edtTypeFilm.getText().toString().equals("")){
                    tvErrorName.setVisibility(View.VISIBLE);
                    tvErrorName.setText("Loại phim không thể trống");
                }else{
                    FilmService.apiService.updateFilm("Bearer " + token,edtId.getText().toString(), typeFilm).enqueue(new Callback<Response>() {
                        @Override
                        public void onResponse(Call<Response> call, retrofit2.Response<Response> response) {
                            if( response.isSuccessful()){
                                loadingDialog.dismissDialog();
                                Toast.makeText(getActivity(), "Cập nhật loại phim thành công", Toast.LENGTH_SHORT).show();
                                Objects.requireNonNull(getActivity()).getSupportFragmentManager().popBackStack();
                            }else   if( response.code() == 401 ){
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

                        @Override
                        public void onFailure(Call<Response> call, Throwable throwable) {
                            Toast.makeText(getActivity(), "Hệ thống lỗi", Toast.LENGTH_SHORT).show();
                            loadingDialog.dismissDialog();
                        }
                    });
                }

            }
        });
    }
}