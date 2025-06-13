package doctorhoai.learn.Fragment;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;

import java.io.IOException;
import java.util.List;

import doctorhoai.learn.Activity.LoginActivity;
import doctorhoai.learn.Api.BranchService;
import doctorhoai.learn.Model.Branch;
import doctorhoai.learn.Model.ErrorResponse;
import doctorhoai.learn.Model.Param;
import doctorhoai.learn.Model.Response;
import doctorhoai.learn.R;
import doctorhoai.learn.Utils.ShareData;
import retrofit2.Call;
import retrofit2.Callback;

public class RapFragment extends Fragment {
    private Param param = new Param(0,"asc", 10, "name", "", "none");
    private ProgressBar progressBar;
    private ImageView imgNotFound, imgPrev, imgNext;
    private TableLayout branchTable;
    private ShareData shareData;
    private ObjectMapper mapper = new ObjectMapper();
    private List<Branch> branches;
    private TextView tvCounter;
    private Integer totalPage = 1;
    private Spinner spinnerAsc, spinnerOrderBy, sprinnerStatus;
    private Button btnThem;
    private EditText edtSearch;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_rap, container, false);

        initView(view);

        fetchBranchByCustom();

        spinnerAsc.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    param.setAsc("asc");
                }else if (position == 1) {
                    param.setAsc("desc");
                }
                fetchBranchByCustom();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spinnerOrderBy.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if( position == 0) {
                    param.setOrderBy("id");
                }else if (position == 1) {
                    param.setOrderBy("name");
                }
                fetchBranchByCustom();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        sprinnerStatus.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if( position == 0 ){
                    param.setStatus("none");
                }else if( position == 1){
                    param.setStatus("ACTIVE");
                }else if( position == 2){
                    param.setStatus("DELETE");
                }
                fetchBranchByCustom();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        imgNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if( param.getPage() < totalPage -1 ){
                    param.setPage(param.getPage()+1);
                    fetchBranchByCustom();
                }
            }
        });
        imgPrev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if( param.getPage() > 0){
                    param.setPage(param.getPage()-1);
                    fetchBranchByCustom();
                }
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
                            fetchBranchByCustom();
                            return true;
                        }
                    }
                }
                return false;
            }
        });

        return view;
    }
    public void initView (View view ) {
        progressBar = view.findViewById(R.id.proccesBar_table_branch);
        imgNotFound = view.findViewById(R.id.img_table);
        imgPrev = view.findViewById(R.id.img_prev);
        imgNext = view.findViewById(R.id.img_next);
        branchTable = view.findViewById(R.id.table_branch);
        shareData = ShareData.getInstance(getActivity());
        tvCounter = view.findViewById(R.id.tv_counter);
        spinnerAsc = view.findViewById(R.id.spinner_asc);
        List<String> listAsc = List.of("Tăng dần", "Giảm dần");
        ArrayAdapter<String> adapterAsc = new ArrayAdapter<>(
                getActivity(),
                android.R.layout.simple_spinner_item,
                listAsc
        );
        spinnerAsc.setAdapter(adapterAsc);
        spinnerOrderBy = view.findViewById(R.id.spinner_orderBy);
        List<String> listOrder = List.of("Id", "Tên rạp");
        ArrayAdapter<String> adapterOrder = new ArrayAdapter<>(
                getActivity(),
                android.R.layout.simple_spinner_item,
                listOrder
        );
        spinnerOrderBy.setAdapter(adapterOrder);
        edtSearch = view.findViewById(R.id.edt_search);
        btnThem = view.findViewById(R.id.btn_them);
        sprinnerStatus = view.findViewById(R.id.spinner_status);
        List<String> listStatus = List.of("Tất cả","Hoạt động", "Ngưng hoạt động");
        ArrayAdapter<String> adapterStatus = new ArrayAdapter<>(
                getActivity(),
                android.R.layout.simple_spinner_item,
                listStatus
        );
        sprinnerStatus.setAdapter(adapterStatus);
        btnThem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ThemRapFragment themRapFragment = new ThemRapFragment();
                Bundle bundle = new Bundle();
                bundle.putString("type", "ADD");
                themRapFragment.setArguments(bundle);
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.frame_container, themRapFragment)
                        .addToBackStack(null)
                        .commit();
            }
        });
    }
    public void fetchBranchByCustom () {

        //loading
        progressBar.setVisibility(View.VISIBLE);
        imgNotFound.setVisibility(View.GONE);

        BranchService.apiService.getBranch(param.getPage(),param.getLimit(), param.getOrderBy(), param.getAsc(), param.getStatus(), param.getQ()).enqueue(new Callback<Response>() {
            @Override
            public void onResponse(Call<Response> call, retrofit2.Response<Response> response) {
                if( response.isSuccessful() ){
                    Response res = response.body();
                    LinkedTreeMap<String, Object> data = mapper.convertValue(res.getData(), LinkedTreeMap.class);
                    branches = mapper.convertValue(data.get("content"), new TypeReference<List<Branch>>() {});
                    totalPage = mapper.convertValue(data.get("totalPages"), new TypeReference<Integer>() {});
                    initTable();

                    progressBar.setVisibility(View.GONE);
                    branchTable.setVisibility(View.VISIBLE);

                    if( branches.size() == 0 ){
                        branchTable.setVisibility(View.GONE);
                        imgNotFound.setVisibility(View.VISIBLE);
                    }

                }else if( response.code() == 401 ){
                    Toast.makeText(getActivity(), "Hết phiên làm việc", Toast.LENGTH_SHORT).show();
                    shareData.clearToken();
                    Intent intent = new Intent(getActivity(), LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                }else if( response.code() == 400 ){
                    String errorJson = null;
                    try {
                        errorJson = response.errorBody().string();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    Gson gson = new Gson();
                    ErrorResponse errorResponse = gson.fromJson(errorJson, ErrorResponse.class);
                    Toast.makeText(getActivity(), errorResponse.getMessage(), Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                    branchTable.setVisibility(View.GONE);
                    imgNotFound.setVisibility(View.VISIBLE);
                }else{
                    //Toast.makeText(getActivity(), "Hệ thống xảy ra lỗi", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                    branchTable.setVisibility(View.GONE);
                    //imgNotFound.setVisibility(View.VISIBLE);
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
    public void initTable () {
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
        int childCount = branchTable.getChildCount();
        if (childCount > 1) {
            branchTable.removeViews(1, childCount - 1);
        }
        for( int i = 0 ; i < branches.size() ; i++ ){
            Branch branch = branches.get(i);
            TableRow row = new TableRow(getContext());
            row.setBackground(getResources().getDrawable(R.drawable.bg_border_bottom));
            row.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Bundle bundle = new Bundle();
                    ThemRapFragment themRapFragment = new ThemRapFragment();
                    bundle.putString("type", "UPDATE");
                    bundle.putSerializable("data", branch);
                    themRapFragment.setArguments(bundle);
                    getActivity().getSupportFragmentManager().beginTransaction()
                            .replace(R.id.frame_container, themRapFragment)
                            .addToBackStack(null)
                            .commit();
                }
            });

            LinearLayout containerLayout = new LinearLayout(getContext());
            containerLayout.setOrientation(LinearLayout.VERTICAL);

            // 2. Set LayoutParams cho containerLayout (weight = 2)
            TableRow.LayoutParams containerParams = new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 2f);
            containerLayout.setLayoutParams(containerParams);

            TextView tvName = new TextView(getContext());
            tvName.setTextSize(14);
            tvName.setText(branch.getNameBranch());
            tvName.setPadding(8, 8, 8, 8);
            tvName.setLayoutParams(new TableRow.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 2));
            row.addView(tvName);

            TextView tvAddress = new TextView(getContext());
            tvAddress.setTextSize(14);
            tvAddress.setText(branch.getAddress());
            tvAddress.setPadding(8, 8, 8, 8);
            tvAddress.setLayoutParams(new TableRow.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 2));
            row.addView(tvAddress);

            TextView tvStatus = new TextView(getContext());
            tvStatus.setTextSize(14);
            tvStatus.setText(branch.getStatus().toString().equals("ACTIVE")? "Hoạt động" : branch.getStatus().toString().equals("DELETE") ? "Ngưng hoạt động" : "Không xác định");
            if( branch.getStatus().toString().equals("ACTIVE") ){
                tvStatus.setTextColor(Color.GREEN);
            } else if( branch.getStatus().toString().equals("DELETE") ){
                tvStatus.setTextColor(Color.RED);
            }
            else{
                tvStatus.setTextColor(Color.RED);
            }
            tvStatus.setPadding(8, 8, 8, 8);
            tvStatus.setLayoutParams(new TableRow.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1));
            row.addView(tvStatus);

            branchTable.addView(row);
        }
    }
}