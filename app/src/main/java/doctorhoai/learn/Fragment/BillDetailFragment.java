package doctorhoai.learn;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import doctorhoai.learn.Activity.LoadingDialog;
import doctorhoai.learn.Activity.LoginActivity;
import doctorhoai.learn.Adapter.ChairAdapter;
import doctorhoai.learn.Adapter.DishAdapter;
import doctorhoai.learn.Api.BillService;
import doctorhoai.learn.Model.Bill;
import doctorhoai.learn.Model.ErrorResponse;
import doctorhoai.learn.Model.Response;
import doctorhoai.learn.Utils.ShareData;
import com.bumptech.glide.Glide;
import retrofit2.Call;
import retrofit2.Callback;
import java.io.IOException;
import java.util.ArrayList;

public class BillDetailFragment extends Fragment {
    private Bill bill;
    private TextView tvBillId, tvTotalPrice, tvTransactionCode, tvTimestamp, tvFilmName, tvShowTime, tvRoom, tvBranch;
    private EditText edtPaymentMethod, edtStatus, edtUserName, edtEmail, edtPhone;
    private ImageView ivQrCode;
    private RecyclerView rvChairs, rvDishes;
    private ChairAdapter chairAdapter;
    private DishAdapter dishAdapter;
    private Button btnEdit, btnSave;
    private boolean isEditMode = false;
    private ShareData shareData;
    private String token;
    private Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").setLenient().create();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_bill_detail, container, false);

        // Ánh xạ view
        tvBillId = view.findViewById(R.id.tv_bill_id);
        tvTotalPrice = view.findViewById(R.id.tv_total_price);
        tvTransactionCode = view.findViewById(R.id.tv_transaction_code);
        edtPaymentMethod = view.findViewById(R.id.edt_payment_method);
        edtStatus = view.findViewById(R.id.edt_status);
        tvTimestamp = view.findViewById(R.id.tv_timestamp);
        edtUserName = view.findViewById(R.id.edt_user_name);
        edtEmail = view.findViewById(R.id.edt_email);
        edtPhone = view.findViewById(R.id.edt_phone);
        tvFilmName = view.findViewById(R.id.tv_film_name);
        tvShowTime = view.findViewById(R.id.tv_show_time);
        tvRoom = view.findViewById(R.id.tv_room);
        tvBranch = view.findViewById(R.id.tv_branch);
        ivQrCode = view.findViewById(R.id.iv_qr_code);
        rvChairs = view.findViewById(R.id.rv_chairs);
        rvDishes = view.findViewById(R.id.rv_dishes);
        btnEdit = view.findViewById(R.id.btn_edit);
        btnSave = view.findViewById(R.id.btn_save);

        // Khởi tạo ShareData và token
        shareData = ShareData.getInstance(getActivity());
        token = shareData.getToken();

        // Lấy dữ liệu từ Bundle
        if (getArguments() != null) {
            bill = (Bill) getArguments().getSerializable("bill");
        }

        // Hiển thị dữ liệu
        if (bill != null) {
            displayBillData();
        }

        // Xử lý nút chỉnh sửa
        btnEdit.setOnClickListener(v -> {
            isEditMode = true;
            btnEdit.setVisibility(View.GONE);
            btnSave.setVisibility(View.VISIBLE);
            enableEditFields(true);
        });

        // Xử lý nút lưu
        btnSave.setOnClickListener(v -> {
            // Cập nhật bill từ EditText
            bill.setPaymentMethod(edtPaymentMethod.getText().toString());
            bill.setStatus(edtStatus.getText().toString());
            bill.setUserName(edtUserName.getText().toString());
            bill.setEmail(edtEmail.getText().toString());
            bill.setNumberPhone(edtPhone.getText().toString());

            // Gọi API cập nhật trạng thái
            updateBillStatus();
        });

        return view;
    }

    private void displayBillData() {
        tvBillId.setText("Mã hóa đơn: " + (bill.getId() != null ? bill.getId() : "N/A"));
        tvTotalPrice.setText("Tổng tiền: " + (bill.getTotalPrice() != null ? bill.getTotalPrice() + " VNĐ" : "N/A"));
        tvTransactionCode.setText("Mã giao dịch: " + (bill.getTransactionCode() != null && !bill.getTransactionCode().isEmpty() ? bill.getTransactionCode() : "Không có"));
        edtPaymentMethod.setText(bill.getPaymentMethod() != null ? bill.getPaymentMethod() : "N/A");
        edtStatus.setText(bill.getStatus() != null ? bill.getStatus() : "N/A");
        tvTimestamp.setText("Thời gian: " + (bill.getTimestamp() != null ? bill.getTimestamp() : "N/A"));
        edtUserName.setText(bill.getUserName() != null ? bill.getUserName() : "N/A");
        edtEmail.setText(bill.getEmail() != null ? bill.getEmail() : "N/A");
        edtPhone.setText(bill.getNumberPhone() != null ? bill.getNumberPhone() : "N/A");
        tvFilmName.setText("Tên phim: " + (bill.getNameFilm() != null ? bill.getNameFilm() : "N/A"));
        tvShowTime.setText("Thời gian chiếu: " + (bill.getTimeStart() != null ? bill.getTimeStart() : "N/A") + " - " +
                (bill.getTimeEnd() != null ? bill.getTimeEnd() : "N/A") + ", " + (bill.getTimeStampSee() != null ? bill.getTimeStampSee() : "N/A"));
        tvRoom.setText("Phòng: " + (bill.getNameRoom() != null ? bill.getNameRoom() : "N/A"));
        tvBranch.setText("Chi nhánh: " + (bill.getNameBranch() != null ? bill.getNameBranch() : "N/A") +
                ", " + (bill.getAddress() != null ? bill.getAddress() : "N/A"));

        // Tải QR code
        if (bill.getQrCode() != null && !bill.getQrCode().isEmpty()) {
            Glide.with(this).load(bill.getQrCode()).placeholder(android.R.drawable.ic_menu_gallery).into(ivQrCode);
        } else {
            ivQrCode.setImageResource(android.R.drawable.ic_menu_close_clear_cancel);
        }

        // Thiết lập RecyclerView cho ghế
        rvChairs.setLayoutManager(new LinearLayoutManager(getContext()));
        chairAdapter = new ChairAdapter(bill.getChairs() != null ? bill.getChairs() : new ArrayList<>());
        rvChairs.setAdapter(chairAdapter);

        // Thiết lập RecyclerView cho món ăn
        rvDishes.setLayoutManager(new LinearLayoutManager(getContext()));
        dishAdapter = new DishAdapter(bill.getDishes() != null ? bill.getDishes() : new ArrayList<>());
        rvDishes.setAdapter(dishAdapter);
    }

    private void enableEditFields(boolean enable) {
        edtPaymentMethod.setEnabled(enable);
        edtStatus.setEnabled(enable);
        edtUserName.setEnabled(enable);
        edtEmail.setEnabled(enable);
        edtPhone.setEnabled(enable);
    }

    private void updateBillStatus() {
        LoadingDialog loadingDialog = new LoadingDialog(getActivity());
        loadingDialog.startLoadingDialog();

        // Giả định PATCH /api/bill/active/{id} thay đổi status thành SUCCESS
        if (edtStatus.getText().toString().equalsIgnoreCase("SUCCESS")) {
            BillService.apiService.activeBill("Bearer " + token, bill.getId())
                    .enqueue(new Callback<Response>() {
                        @Override
                        public void onResponse(Call<Response> call, retrofit2.Response<Response> response) {
                            if (response.isSuccessful()) {
                                // Làm mới dữ liệu
                                fetchBillDetails();
                            } else {
                                handleErrorResponse(response, loadingDialog);
                            }
                        }

                        @Override
                        public void onFailure(Call<Response> call, Throwable t) {
                            loadingDialog.dismissDialog();
                            Toast.makeText(getContext(), "Hệ thống xảy ra lỗi", Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            loadingDialog.dismissDialog();
            Toast.makeText(getContext(), "Chỉ hỗ trợ cập nhật trạng thái thành SUCCESS", Toast.LENGTH_SHORT).show();
        }
    }

    private void fetchBillDetails() {
        BillService.apiService.getBillById("Bearer " + token, bill.getId())
                .enqueue(new Callback<Response>() {
                    @Override
                    public void onResponse(Call<Response> call, retrofit2.Response<Response> response) {
                        LoadingDialog loadingDialog = new LoadingDialog(getActivity());
                        loadingDialog.dismissDialog();
                        if (response.isSuccessful()) {
                            try {
                                Response res = gson.fromJson(gson.toJson(response.body()), Response.class);
                                bill = gson.fromJson(gson.toJson(res.getData()), Bill.class);
                                displayBillData();
                                isEditMode = false;
                                btnEdit.setVisibility(View.VISIBLE);
                                btnSave.setVisibility(View.GONE);
                                enableEditFields(false);
                                Toast.makeText(getContext(), "Cập nhật hóa đơn thành công", Toast.LENGTH_SHORT).show();
                            } catch (Exception e) {
                                Toast.makeText(getContext(), "Lỗi khi làm mới dữ liệu", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            handleErrorResponse(response, loadingDialog);
                        }
                    }

                    @Override
                    public void onFailure(Call<Response> call, Throwable t) {
                        LoadingDialog loadingDialog = new LoadingDialog(getActivity());
                        loadingDialog.dismissDialog();
                        Toast.makeText(getContext(), "Hệ thống xảy ra lỗi", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void handleErrorResponse(retrofit2.Response<Response> response, LoadingDialog loadingDialog) {
        loadingDialog.dismissDialog();
        if (response.code() == 401) {
            Toast.makeText(getContext(), "Hết phiên làm việc", Toast.LENGTH_SHORT).show();
            shareData.clearToken();
            Intent intent = new Intent(getActivity(), LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        } else {
            try {
                String errorJson = response.errorBody().string();
                ErrorResponse errorResponse = gson.fromJson(errorJson, ErrorResponse.class);
                Toast.makeText(getContext(), errorResponse.getMessage(), Toast.LENGTH_SHORT).show();
            } catch (IOException e) {
                Toast.makeText(getContext(), "Lỗi khi cập nhật hóa đơn", Toast.LENGTH_SHORT).show();
            }
        }
    }
}