package doctorhoai.learn.Fragment;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import androidx.fragment.app.Fragment;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;
import doctorhoai.learn.Activity.LoadingDialog;
import doctorhoai.learn.Activity.LoginActivity;
import doctorhoai.learn.Api.BillService;
import doctorhoai.learn.BillDetailFragment;
import doctorhoai.learn.Model.Bill;
import doctorhoai.learn.Model.ErrorResponse;
import doctorhoai.learn.Model.BillParam;
import doctorhoai.learn.Model.Response;
import doctorhoai.learn.R;
import doctorhoai.learn.Utils.ShareData;
import retrofit2.Call;
import retrofit2.Callback;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class BillFragment extends Fragment {

    private BillParam param = new BillParam("0", "asc", "10", "timestamp", "", "none");
    private String token;
    private ShareData shareData;
    private TableLayout billTable;
    private Integer totalPage;
    private List<Bill> bills;
    private ObjectMapper mapper = new ObjectMapper();
    private ProgressBar progressBarTableBill;
    private ImageView imgTable, imgPrev, imgNext;
    private TextView tvCounter;
    private Spinner spinnerAsc, spinnerOrderBy;
    private Button btnThem;
    private EditText edtSearch;
    private LoadingDialog loadingDialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_bill, container, false);

        initView(view);
        fetchBills();

        imgPrev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bills = new ArrayList<>();
                int page = Integer.parseInt(param.getPage());
                if (page > 0) {
                    param.setPage(String.valueOf(page - 1));
                }
                fetchBills();
            }
        });

        imgNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bills = new ArrayList<>();
                int page = Integer.parseInt(param.getPage());
                if (page < totalPage - 1) {
                    param.setPage(String.valueOf(page + 1));
                }
                fetchBills();
            }
        });

        spinnerAsc.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                param.setAsc(position == 0 ? "desc" : "asc");
                fetchBills();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        spinnerOrderBy.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                param.setOrderBy(position == 0 ? "timestamp" : "totalPrice");
                fetchBills();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        edtSearch.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    Drawable drawableEnd = edtSearch.getCompoundDrawables()[2];
                    if (drawableEnd != null) {
                        if (event.getX() >= (edtSearch.getWidth() - edtSearch.getCompoundPaddingEnd())) {
                            param.setQ(edtSearch.getText().toString());
                            fetchBills();
                            return true;
                        }
                    }
                }
                return false;
            }
        });

        return view;
    }

    private void initView(View view) {
        loadingDialog = new LoadingDialog(getActivity());
        edtSearch = view.findViewById(R.id.edt_search);
        shareData = ShareData.getInstance(getActivity());
        token = shareData.getToken();
        billTable = view.findViewById(R.id.table_bill);
        billTable.setVisibility(View.GONE);
        progressBarTableBill = view.findViewById(R.id.proccesBar_table_bill);
        progressBarTableBill.setVisibility(View.VISIBLE);
        imgTable = view.findViewById(R.id.img_table);
        imgPrev = view.findViewById(R.id.img_prev);
        imgNext = view.findViewById(R.id.img_next);
        tvCounter = view.findViewById(R.id.tv_counter);
        spinnerAsc = view.findViewById(R.id.spinner_asc);
        spinnerOrderBy = view.findViewById(R.id.spinner_orderBy);
        btnThem = view.findViewById(R.id.btn_them);

        List<String> listAsc = new ArrayList<>();
        listAsc.add("Giảm dần");
        listAsc.add("Tăng dần");
        ArrayAdapter<String> adapterAsc = new ArrayAdapter<>(
                getActivity(), android.R.layout.simple_spinner_item, listAsc);
        spinnerAsc.setAdapter(adapterAsc);

        List<String> listOrder = new ArrayList<>();
        listOrder.add("Thời gian");
        listOrder.add("Tổng tiền");
        ArrayAdapter<String> adapterOrder = new ArrayAdapter<>(
                getActivity(), android.R.layout.simple_spinner_item, listOrder);
        spinnerOrderBy.setAdapter(adapterOrder);
    }

    private void initTable() {
        tvCounter.setText((Integer.parseInt(param.getPage()) + 1) + "/" + totalPage);
        imgNext.setVisibility(Integer.parseInt(param.getPage()) + 1 == totalPage ? View.GONE : View.VISIBLE);
        imgPrev.setVisibility(Integer.parseInt(param.getPage()) == 0 ? View.GONE : View.VISIBLE);

        int childCount = billTable.getChildCount();
        if (childCount > 1) {
            billTable.removeViews(1, childCount - 1);
        }

        for (Bill bill : bills) {
            TableRow row = new TableRow(getContext());
            row.setBackgroundResource(R.drawable.bg_border_bottom);
            row.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Tạo Bundle và truyền Bill
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("bill", bill);

                    // Tạo và chuyển sang BillDetailFragment
                    BillDetailFragment detailFragment = new BillDetailFragment();
                    detailFragment.setArguments(bundle);

                    // Thay thế fragment
                    requireActivity().getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.frame_container, detailFragment)
                            .addToBackStack(null)
                            .commit();
                }
            });

            TextView tvId = new TextView(getContext());
            tvId.setGravity(1);
            tvId.setTextSize(14);
            tvId.setText(bill.getId() != null ? bill.getId() : "");
            tvId.setPadding(8, 8, 8, 8);
            tvId.setLayoutParams(new TableRow.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1));
            row.addView(tvId);

            TextView tvTotalPrice = new TextView(getContext());
            tvTotalPrice.setGravity(1);
            tvTotalPrice.setTextSize(14);
            tvTotalPrice.setText(bill.getTotalPrice() != null ? String.format("%.2f", bill.getTotalPrice()) : "0.00");
            tvTotalPrice.setPadding(8, 8, 8, 8);
            tvTotalPrice.setLayoutParams(new TableRow.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1));
            row.addView(tvTotalPrice);

            TextView tvTimestamp = new TextView(getContext());
            tvTimestamp.setGravity(1);
            tvTimestamp.setTextSize(14);
            tvTimestamp.setText(bill.getTimestamp() != null ? bill.getTimestamp() : "");
            tvTimestamp.setPadding(8, 8, 8, 8);
            tvTimestamp.setLayoutParams(new TableRow.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1));
            row.addView(tvTimestamp);

            TextView tvUserName = new TextView(getContext());
            tvUserName.setGravity(1);
            tvUserName.setTextSize(14);
            tvUserName.setText(bill.getUserName() != null ? bill.getUserName() : "");
            tvUserName.setPadding(8, 8, 8, 8);
            tvUserName.setLayoutParams(new TableRow.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1));
            row.addView(tvUserName);

            TextView tvStatus = new TextView(getContext());
            tvStatus.setGravity(1);
            tvStatus.setTextSize(14);
            tvStatus.setText(bill.getStatus() != null ? bill.getStatus() : "");
            tvStatus.setPadding(8, 8, 8, 8);
            tvStatus.setTextColor(bill.getStatus() != null && bill.getStatus().equals("SUCCESS") ? Color.GREEN : Color.RED);
            tvStatus.setLayoutParams(new TableRow.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1));
            row.addView(tvStatus);

            billTable.addView(row);
        }
    }

    private void fetchBills() {
        progressBarTableBill.setVisibility(View.VISIBLE);
        imgTable.setVisibility(View.GONE);
        billTable.setVisibility(View.GONE);

        BillService.apiService.getBills("Bearer " + token, param.getPage(), param.getLimit(), param.getActive(), param.getOrderBy(), param.getAsc(), param.getQ())
                .enqueue(new Callback<Response>() {
                    @Override
                    public void onResponse(Call<Response> call, retrofit2.Response<Response> response) {
                        if (response.isSuccessful()) {
                            try {
                                Response res = mapper.convertValue(response.body(), Response.class);
                                LinkedTreeMap<String, Object> data = mapper.convertValue(res.getData(), LinkedTreeMap.class);
                                bills = mapper.convertValue(data.get("data"), new TypeReference<List<Bill>>() {});
                                totalPage = mapper.convertValue(data.get("totalPages"), new TypeReference<Integer>() {});

                                initTable();
                                progressBarTableBill.setVisibility(View.GONE);
                                billTable.setVisibility(bills.size() > 0 ? View.VISIBLE : View.GONE);
                                imgTable.setVisibility(bills.size() == 0 ? View.VISIBLE : View.GONE);
                            } catch (Exception e) {
                                progressBarTableBill.setVisibility(View.GONE);
                                imgTable.setVisibility(View.VISIBLE);
                                billTable.setVisibility(View.GONE);
                                Toast.makeText(getActivity(), "Hệ thống xảy ra lỗi", Toast.LENGTH_SHORT).show();
                                e.printStackTrace();
                            }
                        } else {
                            progressBarTableBill.setVisibility(View.GONE);
                            imgTable.setVisibility(View.VISIBLE);
                            billTable.setVisibility(View.GONE);
                            if (response.code() == 401) {
                                Toast.makeText(getActivity(), "Hết phiên làm việc", Toast.LENGTH_SHORT).show();
                                shareData.clearToken();
                                Intent intent = new Intent(getActivity(), LoginActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                            } else if (response.code() == 400) {
                                try {
                                    String errorJson = response.errorBody().string();
                                    Gson gson = new Gson();
                                    ErrorResponse errorResponse = gson.fromJson(errorJson, ErrorResponse.class);
                                    Toast.makeText(getActivity(), errorResponse.getMessage(), Toast.LENGTH_SHORT).show();
                                } catch (IOException e) {
                                    throw new RuntimeException(e);
                                }
                            } else {
                                Toast.makeText(getActivity(), "Hệ thống xảy ra lỗi", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<Response> call, Throwable t) {
                        Toast.makeText(getActivity(), "Hệ thống xảy ra lỗi", Toast.LENGTH_SHORT).show();
                        progressBarTableBill.setVisibility(View.GONE);
                        imgTable.setVisibility(View.VISIBLE);
                        billTable.setVisibility(View.GONE);
                    }
                });
    }
}