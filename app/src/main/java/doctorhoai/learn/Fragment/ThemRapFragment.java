package doctorhoai.learn.Fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

import doctorhoai.learn.Activity.LoadingDialog;
import doctorhoai.learn.Activity.LoginActivity;
import doctorhoai.learn.Api.BranchService;
import doctorhoai.learn.Api.FilmService;
import doctorhoai.learn.Model.Branch;
import doctorhoai.learn.Model.BranchCreate;
import doctorhoai.learn.Model.ErrorResponse;
import doctorhoai.learn.Model.Film;
import doctorhoai.learn.Model.Response;
import doctorhoai.learn.Model.TypeFilm;
import doctorhoai.learn.R;
import doctorhoai.learn.Utils.ShareData;
import retrofit2.Call;
import retrofit2.Callback;

public class ThemRapFragment extends Fragment {
    private TextView tv_title, tv_err_name, tv_id, tv_err_address;
    private EditText edt_id, edt_name, edt_address;
    private ShareData shareData;
    private String token;
    private LoadingDialog loadingDialog;
    private ObjectMapper mapper = new ObjectMapper();
    private Spinner spinnerStatus;
    private Button btnCancel, btnSave, btnRoom;
    private static final int PICK_IMAGE_REQUEST = 1;
    private Branch branch = new Branch("","","", "ACTIVE");

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_them_rap, container, false);

        initView(view);
        Bundle bundle = getArguments();
        if( null != bundle ) {
            String type = bundle.getString("type");
            if( type == "ADD"){
                tv_title.setText("Thêm rạp");
                btnSave.setText("Thêm");
                tv_id.setVisibility(View.GONE);
                edt_id.setVisibility(View.GONE);
                btnRoom.setVisibility(View.GONE);
                functionAdd();
            }else{
                tv_title.setText("Cập nhật rạp");
                btnSave.setText("Cập nhật");
                btnRoom.setVisibility(View.VISIBLE);

                Branch branch1 = (Branch) bundle.getSerializable("data");
                edt_id.setText(branch1.getId());
                edt_id.setEnabled(false);
                edt_name.setText(branch1.getNameBranch());
                edt_address.setText(branch1.getAddress());
                spinnerStatus.setSelection(branch1.getStatus().toString().equals("ACTIVE") ? 0 : 1);
                btnRoom.setOnClickListener(v -> {
                    RoomFragment roomFragment = new RoomFragment();
                    Bundle bundleRoom = new Bundle();
                    bundleRoom.putString("branchId", branch1.getId());
                    roomFragment.setArguments(bundleRoom);

                    requireActivity().getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.frame_container, roomFragment)
                            .addToBackStack(null)
                            .commit();
                });
                functionUpdate();
            }
        }else{
            Objects.requireNonNull(getActivity()).getSupportFragmentManager().popBackStack();
        }
        spinnerStatus.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if( position == 0 ){
                    branch.setStatus("ACTIVE");
                }else {
                    branch.setStatus("DELETE");
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

    public void initView ( View view ) {

        shareData = ShareData.getInstance(getActivity());
        token = shareData.getToken();
        edt_id = view.findViewById(R.id.edt_id);
        edt_name = view.findViewById(R.id.edt_name_branch);
        edt_address = view.findViewById(R.id.edt_address);
        spinnerStatus = view.findViewById(R.id.spinner_status);
        List<String> list = List.of("Hoạt động", "N.Hoạt động");
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                getActivity(),
                android.R.layout.simple_spinner_item,
                list
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerStatus.setAdapter(adapter);
        tv_title = view.findViewById(R.id.tvTitle);
        btnSave = view.findViewById(R.id.btn_save);
        btnCancel = view.findViewById(R.id.btn_cancel);
        btnRoom = view.findViewById(R.id.btn_room);
        tv_id = view.findViewById(R.id.tvId);
        loadingDialog = new LoadingDialog(getActivity());
        tv_err_name = view.findViewById(R.id.txt_error_name);
        tv_err_address = view.findViewById(R.id.txt_error_address);
    }

    public void functionAdd(){
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                branch.setAddress(edt_address.getText().toString());
                branch.setNameBranch(edt_name.getText().toString());
                boolean hasError = false;

                tv_err_name.setVisibility(View.GONE);
                tv_err_address.setVisibility(View.GONE);

                if (edt_name.getText().toString().equals("")) {
                    tv_err_name.setVisibility(View.VISIBLE);
                    tv_err_name.setText("Tên rạp không thể trống");
                    hasError = true;
                }
                if (edt_address.getText().toString().equals("")) {
                    tv_err_address.setVisibility(View.VISIBLE);
                    tv_err_address.setText("Địa chỉ không thể trống");
                    hasError = true;
                }
                if (hasError) {
                    return;
                }
                loadingDialog.startLoadingDialog();
                BranchService.apiService.addBranch("Bearer " + token, branch).enqueue(new Callback<Response>() {
                    @Override
                    public void onResponse(Call<Response> call, retrofit2.Response<Response> response) {
                        if( response.isSuccessful()){
                            loadingDialog.dismissDialog();
                            Toast.makeText(getActivity(), "Thêm rạp thành công", Toast.LENGTH_SHORT).show();
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
        });
    }

    public void functionUpdate(){
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                branch.setNameBranch(edt_name.getText().toString());
                branch.setAddress(edt_address.getText().toString());

                boolean hasError = false;

                tv_err_name.setVisibility(View.GONE);
                tv_err_address.setVisibility(View.GONE);

                if (edt_name.getText().toString().equals("")) {
                    tv_err_name.setVisibility(View.VISIBLE);
                    tv_err_name.setText("Tên rạp không thể trống");
                    hasError = true;
                }
                if (edt_address.getText().toString().equals("")) {
                    tv_err_address.setVisibility(View.VISIBLE);
                    tv_err_address.setText("Địa chỉ không thể trống");
                    hasError = true;
                }
                if (hasError) {
                    return;
                }
                loadingDialog.startLoadingDialog();
                BranchService.apiService.updateBranch("Bearer " + token,edt_id.getText().toString(), branch).enqueue(new Callback<Response>() {
                    @Override
                    public void onResponse(Call<Response> call, retrofit2.Response<Response> response) {
                        if( response.isSuccessful()){
                            loadingDialog.dismissDialog();
                            Toast.makeText(getActivity(), "Cập nhật rạp phim thành công", Toast.LENGTH_SHORT).show();
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
        });
    }
}