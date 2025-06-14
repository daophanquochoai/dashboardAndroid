package doctorhoai.learn.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;

import doctorhoai.learn.Adapter.ChairAdapter;
import doctorhoai.learn.Adapter.DishAdapter;
import doctorhoai.learn.Model.Bill;
import doctorhoai.learn.R;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class BillDetailFragment extends Fragment {
    private Bill bill;
    private TextView tvBillId, tvTotalPrice, tvTransactionCode, tvPaymentMethod, tvStatus, tvTimestamp, tvUserName, tvEmail, tvPhone, tvFilmName, tvShowTime, tvRoom, tvBranch;
    private ImageView ivQrCode;
    private RecyclerView rvChairs, rvDishes;
    private ChairAdapter chairAdapter;
    private DishAdapter dishAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_bill_detail, container, false);

        // Ánh xạ view
        tvBillId = view.findViewById(R.id.tv_bill_id);
        tvTotalPrice = view.findViewById(R.id.tv_total_price);
        tvTransactionCode = view.findViewById(R.id.tv_transaction_code);
        tvPaymentMethod = view.findViewById(R.id.tv_payment_method);
        tvStatus = view.findViewById(R.id.tv_status);
        tvTimestamp = view.findViewById(R.id.tv_timestamp);
        tvUserName = view.findViewById(R.id.tv_user_name);
        tvEmail = view.findViewById(R.id.tv_email);
        tvPhone = view.findViewById(R.id.tv_phone);
        tvFilmName = view.findViewById(R.id.tv_film_name);
        tvShowTime = view.findViewById(R.id.tv_show_time);
        tvRoom = view.findViewById(R.id.tv_room);
        tvBranch = view.findViewById(R.id.tv_branch);
        ivQrCode = view.findViewById(R.id.iv_qr_code);
        rvChairs = view.findViewById(R.id.rv_chairs);
        rvDishes = view.findViewById(R.id.rv_dishes);

        // Lấy dữ liệu từ Bundle
        if (getArguments() != null) {
            bill = (Bill) getArguments().getSerializable("bill");
        }

        // Hiển thị dữ liệu
        if (bill != null) {
            tvBillId.setText("Mã hóa đơn: " + (bill.getId() != null ? bill.getId() : "N/A"));
            DecimalFormat DecimalFormat = new DecimalFormat("#,###");
            tvTotalPrice.setText("Tổng tiền: " + (bill.getTotalPrice() != null ? DecimalFormat.format(bill.getTotalPrice()) + " VND" : "N/A"));
            tvTransactionCode.setText("Mã giao dịch: " + (bill.getTransactionCode() != null && !bill.getTransactionCode().isEmpty() ? bill.getTransactionCode() : "Không có"));
            tvPaymentMethod.setText("Phương thức thanh toán: " + (bill.getPaymentMethod() != null ? bill.getPaymentMethod() : "N/A"));
            tvStatus.setText("Trạng thái: " + (bill.getStatus() != null ? bill.getStatus() : "N/A"));
            tvTimestamp.setText("Thời gian: " + (bill.getTimestamp() != null ? bill.getTimestamp() : "N/A"));
            tvUserName.setText("Tên khách hàng: " + (bill.getUserName() != null ? bill.getUserName() : "N/A"));
            tvEmail.setText("Email: " + (bill.getEmail() != null ? bill.getEmail() : "N/A"));
            tvPhone.setText("Số điện thoại: " + (bill.getNumberPhone() != null ? bill.getNumberPhone() : "N/A"));
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

        return view;
    }
}