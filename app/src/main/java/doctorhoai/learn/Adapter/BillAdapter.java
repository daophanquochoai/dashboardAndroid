package doctorhoai.learn.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import doctorhoai.learn.Model.Bill;
import doctorhoai.learn.R;

public class BillAdapter extends RecyclerView.Adapter<BillAdapter.BillViewHolder> {

    private Context context;
    private List<Bill> billList;
    private OnBillClickListener onBillClickListener;

    public interface OnBillClickListener {
        void onBillClick(Bill bill);
    }

    public BillAdapter(Context context, List<Bill> billList, OnBillClickListener listener) {
        this.context = context;
        this.billList = billList;
        this.onBillClickListener = listener;
    }

    public void setBills(List<Bill> newList) {
        this.billList = newList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public BillViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_bill, parent, false);
        return new BillViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BillViewHolder holder, int position) {
        Bill bill = billList.get(position);

        holder.tvBillId.setText("ID: " + bill.getId());
        holder.tvCustomer.setText("Customer: " + bill.getUserName());
        holder.tvAmount.setText("Amount: " + String.format("%,.0f đ", bill.getTotalPrice()));
        holder.tvStatus.setText("Status: " + bill.getStatus());
        holder.tvCreatedDate.setText("Time: " + bill.getTimestamp());

        // Xử lý click vào item
        holder.itemView.setOnClickListener(v -> {
            if (onBillClickListener != null) {
                onBillClickListener.onBillClick(bill);
            }
        });
    }

    @Override
    public int getItemCount() {
        return billList.size();
    }

    public static class BillViewHolder extends RecyclerView.ViewHolder {
        TextView tvBillId, tvCustomer, tvAmount, tvStatus, tvCreatedDate;


        public BillViewHolder(@NonNull View itemView) {
            super(itemView);
            tvBillId = itemView.findViewById(R.id.tv_bill_id);
            tvCustomer = itemView.findViewById(R.id.tv_bill_user_name);
            tvAmount = itemView.findViewById(R.id.tv_bill_total_price);
            tvStatus = itemView.findViewById(R.id.tv_bill_status);
            tvCreatedDate = itemView.findViewById(R.id.tv_bill_timestamp);
        }
    }
}