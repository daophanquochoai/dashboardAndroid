package doctorhoai.learn.Fragment;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.MotionEvent;
import android.widget.*;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;
import doctorhoai.learn.Activity.LoadingDialog;
import doctorhoai.learn.Activity.LoginActivity;
import doctorhoai.learn.Api.AccountService;
import doctorhoai.learn.Model.Employee;
import doctorhoai.learn.Model.ErrorResponse;
import doctorhoai.learn.Model.Param;
import doctorhoai.learn.Model.Response;
import doctorhoai.learn.R;
import doctorhoai.learn.Utils.ShareData;
import retrofit2.Call;
import retrofit2.Callback;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

public class NhanVienFragment extends Fragment {

    private Param param = new Param(0,"asc",10,"name", "", "none");
    private String token;
    private ShareData shareData;
    private TableLayout employeeTable;
    private Integer totalPage;
    private List<Employee> employees;
    private ObjectMapper mapper = new ObjectMapper();
    private ProgressBar progressBarTableEmployee;
    private ImageView imgTable, imgPrev, imgNext;
    private TextView tvCounter;
    private Spinner spinnerAsc, spinnerOrderBy;
    private Button btnThem;
    private EditText edtSearch;
    private LoadingDialog loadingDialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_nhan_vien, container, false);

        initView(view);

        fetchEmployee();

        btnThem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ThemNhanVienFragment fragment = new ThemNhanVienFragment();

                Bundle bundle = new Bundle();
                bundle.putString("type", "ADD");
                fragment.setArguments(bundle);

                // Dùng FragmentManager của HomeActivity
                getActivity()
                        .getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.frame_container, fragment)
                        .addToBackStack(null)
                        .commit();
            }
        });

        imgPrev.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                employees = new ArrayList<>();
                if( param.getPage() > 0 ){
                    param.setPage(param.getPage() - 1);
                }
                fetchEmployee();
            }
        });
        imgNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                employees = new ArrayList<>();
                if( param.getPage() < totalPage ){
                    param.setPage(param.getPage() + 1);
                }
                fetchEmployee();
            }
        });

        spinnerAsc.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if( position == 0 ){
                    param.setAsc("desc");
                }else if( position == 1 ){
                    param.setAsc("asc");
                }
                fetchEmployee();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        spinnerOrderBy.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if( position == 0 ){
                    param.setOrderBy("name");
                }else if( position == 1 ){
                    param.setOrderBy("email");
                }
                fetchEmployee();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        edtSearch.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if( event.getAction() == MotionEvent.ACTION_UP){
                    Drawable drawableEnd = edtSearch.getCompoundDrawables()[2];
                    if( drawableEnd != null){
                        if(event.getX() >= edtSearch.getWidth() - edtSearch.getCompoundPaddingEnd()) {
                            param.setQ(edtSearch.getText().toString());
                            fetchEmployee();
                            return true;
                        }
                    }
                }
                return false;
            }
        });

        return view;
    }

    public void initView  (View view) {
        loadingDialog = new LoadingDialog(getActivity());
        edtSearch = view.findViewById(R.id.edt_search);
        shareData = ShareData.getInstance(getActivity());
        token = shareData.getToken();
        employeeTable = view.findViewById(R.id.table_employee);
        employeeTable.setVisibility(View.GONE);
        progressBarTableEmployee = view.findViewById(R.id.proccesBar_table_employee);
        progressBarTableEmployee.setVisibility(View.VISIBLE);
        imgTable = view.findViewById(R.id.img_table);
        imgPrev = view.findViewById(R.id.img_prev);
        imgNext = view.findViewById(R.id.img_next);
        tvCounter = view.findViewById(R.id.tv_counter);
        spinnerAsc = view.findViewById(R.id.spinner_asc);
        spinnerOrderBy = view.findViewById(R.id.spinner_orderBy);
        btnThem = view.findViewById(R.id.btn_them);
        List<String> listAsc = List.of("Tăng dần", "Giảm dần");
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                getActivity(),
                android.R.layout.simple_spinner_item,
                listAsc
        );
        List<String> listOrder = List.of("Tên", "Email");
        ArrayAdapter<String> adapterOrder = new ArrayAdapter<>(
                getActivity(),
                android.R.layout.simple_spinner_item,
                listOrder
        );
        spinnerAsc.setAdapter(adapter);
        spinnerOrderBy.setAdapter(adapterOrder);
    }

    public void initTable(){
        tvCounter.setText( (param.getPage() + 1) + "/" + totalPage);
        if( param.getPage() + 1 == totalPage ){
            imgNext.setVisibility(View.GONE);
        }else{
            imgNext.setVisibility(View.VISIBLE);
        }
        if( param.getPage() == 0){
            imgPrev.setVisibility(View.GONE);
        }else{
            imgPrev.setVisibility(View.VISIBLE);
        }
        int childCount = employeeTable.getChildCount();
        if (childCount > 1) {
            employeeTable.removeViews(1, childCount - 1);
        }
        for( int i = 0 ; i < employees.size() ; i++ ){
            Employee employee = employees.get(i);
            TableRow row = new TableRow(getContext());
            row.setBackground(getResources().getDrawable(R.drawable.bg_border_bottom));
            row.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new MaterialAlertDialogBuilder(getActivity(), R.style.CustomDialogTheme)
                            .setTitle("Reset tài khoản")
                            .setMessage("Bạn có chắc chắn muốn reset tài khoản " + employee.getName() + " không?")
                            .setNegativeButton("Hủy", (dialog, which) -> {
                                dialog.dismiss();
                            })
                            .setPositiveButton("Reset", (dialog, which) -> {
                                loadingDialog.startLoadingDialog();
                                AccountService.apiService.resetEmployment("Bearer " + token ,employee.getId()).enqueue(new Callback<Response>() {
                                    @Override
                                    public void onResponse(Call<Response> call, retrofit2.Response<Response> response) {
                                        if( response.isSuccessful() ){
                                            Toast.makeText(getActivity(), "Reset thành công", Toast.LENGTH_SHORT).show();
                                        }
                                        else if( response.raw().code() == 401 ){
                                            Toast.makeText(getActivity(), "Hết phiên làm việc", Toast.LENGTH_SHORT).show();
                                            shareData.clearToken();
                                            Intent intent = new Intent(getActivity(), LoginActivity.class);
                                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                            startActivity(intent);
                                        } else if ( response.code() == 400){
                                            String errorJson = null;
                                            try {
                                                errorJson = response.errorBody().string();
                                            } catch (IOException e) {
                                                throw new RuntimeException(e);
                                            }
                                            Gson gson = new Gson();
                                            ErrorResponse errorResponse = gson.fromJson(errorJson, ErrorResponse.class);
                                            Toast.makeText(getActivity(), errorResponse.getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                        else{
                                            Toast.makeText(getActivity(), "Hệ thống xảy ra lỗi", Toast.LENGTH_SHORT).show();
                                        }
                                    }

                                    @Override
                                    public void onFailure(Call<Response> call, Throwable throwable) {
                                        Toast.makeText(getActivity(), "Hệ thống xảy ra lỗi", Toast.LENGTH_SHORT).show();
                                    }
                                });
                                loadingDialog.dismissDialog();
                                dialog.dismiss();
                            })
                            .show();
                }
            });
            TextView tvTen = new TextView(getContext());
            tvTen.setGravity(1);
            tvTen.setTextSize(14);
            tvTen.setText(employee.getName());
            tvTen.setPadding(8, 8, 8, 8);
            tvTen.setLayoutParams(new TableRow.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 2));
            row.addView(tvTen);

            TextView tvEmail = new TextView(getContext());
            tvEmail.setGravity(1);
            tvEmail.setTextSize(14);
            tvEmail.setText(employee.getEmail());
            tvEmail.setPadding(8, 8, 8, 8);
            tvEmail.setLayoutParams(new TableRow.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1));
            row.addView(tvEmail);

            TextView tvCCCD = new TextView(getContext());
            tvCCCD.setTextSize(14);
            tvCCCD.setGravity(1);
            tvCCCD.setText(employee.getCccd());
            tvCCCD.setPadding(8, 8, 8, 8);
            tvCCCD.setLayoutParams(new TableRow.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1));
            row.addView(tvCCCD);

            TextView tvTrangThai = new TextView(getContext());
            tvTrangThai.setTextSize(14);
            tvTrangThai.setGravity(1);
            tvTrangThai.setText(employee.getStatus().equals("ACTIVE") ? "Còn hoạt động" : "Đã khóa");
            tvTrangThai.setPadding(8, 8, 8, 8);
            if( employee.getStatus().equals("ACTIVE") ){
                tvTrangThai.setTextColor(Color.GREEN);
            }else{
                tvTrangThai.setTextColor(Color.RED);
            }
            tvTrangThai.setLayoutParams(new TableRow.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1));
            row.addView(tvTrangThai);

            employeeTable.addView(row);
        }
    }

    public void fetchEmployee () {
        progressBarTableEmployee.setVisibility(View.VISIBLE);
        imgTable.setVisibility(View.GONE);
        System.out.println( param.getQ());
        AccountService.apiService.getEmployment( "Bearer " + token, param.getPage(), param.getLimit(), param.getAsc(), param.getStatus(), param.getOrderBy(), param.getQ()).enqueue(new Callback<Response>() {
            @Override
            public void onResponse(Call<Response> call, retrofit2.Response<Response> response) {
                if( response.isSuccessful() ){
                    try{
                        Object obj = response.body();
                        Response res = mapper.convertValue(obj, Response.class);
                        LinkedTreeMap<String, Object> data = mapper.convertValue(res.getData(), LinkedTreeMap.class);
                        employees = mapper.convertValue(data.get("content"), new TypeReference<List<Employee>>() {});
                        totalPage = mapper.convertValue(data.get("totalPages"), new TypeReference<Integer>() {});

                        initTable();
                        progressBarTableEmployee.setVisibility(View.GONE);
                        employeeTable.setVisibility(View.VISIBLE);

                        if( employees.size() == 0 ){
                            employeeTable.setVisibility(View.GONE);
                            imgTable.setVisibility(View.VISIBLE);
                        }
                    }catch (Exception e){
                        progressBarTableEmployee.setVisibility(View.GONE);
                        imgTable.setVisibility(View.VISIBLE);
                        employeeTable.setVisibility(View.GONE);
                        e.printStackTrace();
                        Toast.makeText(getActivity(), "Hệ thống xảy ra lỗi", Toast.LENGTH_SHORT).show();
                    }
                }else{
                    if( response.raw().code() == 401 ){
                        Toast.makeText(getActivity(), "Hết phiên làm việc", Toast.LENGTH_SHORT).show();
                        shareData.clearToken();
                        Intent intent = new Intent(getActivity(), LoginActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    } else if ( response.code() == 400){
                        String errorJson = null;
                        try {
                            errorJson = response.errorBody().string();
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                        Gson gson = new Gson();
                        ErrorResponse errorResponse = gson.fromJson(errorJson, ErrorResponse.class);
                        Toast.makeText(getActivity(), errorResponse.getMessage(), Toast.LENGTH_SHORT).show();
                        progressBarTableEmployee.setVisibility(View.GONE);
                        imgTable.setVisibility(View.VISIBLE);
                        employeeTable.setVisibility(View.GONE);
                    }
                    else{
                        progressBarTableEmployee.setVisibility(View.GONE);
                        imgTable.setVisibility(View.VISIBLE);
                        employeeTable.setVisibility(View.GONE);
                        Toast.makeText(getActivity(), "Hệ thống xảy ra lỗi", Toast.LENGTH_SHORT).show();
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
}