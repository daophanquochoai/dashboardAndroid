package doctorhoai.learn.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import doctorhoai.learn.Model.BillChairDto;
import doctorhoai.learn.R;

import java.util.List;

public class ChairAdapter extends RecyclerView.Adapter<ChairAdapter.ChairViewHolder> {
    private List<BillChairDto> chairs;

    public ChairAdapter(List<BillChairDto> chairs) {
        this.chairs = chairs;
    }

    @NonNull
    @Override
    public ChairViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chair, parent, false);
        return new ChairViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChairViewHolder holder, int position) {
        BillChairDto chair = chairs.get(position);
        holder.tvChairCode.setText("Mã ghế: " + (chair.getChairCode() != null ? chair.getChairCode() : "N/A"));
        holder.tvChairPrice.setText("Giá: " + (chair.getPrice() != null ? chair.getPrice() + " VNĐ" : "N/A"));
        if (chair.getTicket() != null) {
            holder.tvTicketName.setText("Tên vé: " + (chair.getTicket().getName() != null ? chair.getTicket().getName() : "N/A"));
            holder.tvTicketType.setText("Loại vé: " + (chair.getTicket().getTypeTicket() != null ? chair.getTicket().getTypeTicket() : "N/A"));
        } else {
            holder.tvTicketName.setText("Tên vé: Không có");
            holder.tvTicketType.setText("Loại vé: Không có");
        }
    }

    @Override
    public int getItemCount() {
        return chairs != null ? chairs.size() : 0;
    }

    static class ChairViewHolder extends RecyclerView.ViewHolder {
        TextView tvChairCode, tvChairPrice, tvTicketName, tvTicketType;

        ChairViewHolder(@NonNull View itemView) {
            super(itemView);
            tvChairCode = itemView.findViewById(R.id.tv_chair_code);
            tvChairPrice = itemView.findViewById(R.id.tv_chair_price);
            tvTicketName = itemView.findViewById(R.id.tv_ticket_name);
            tvTicketType = itemView.findViewById(R.id.tv_ticket_type);
        }
    }
}