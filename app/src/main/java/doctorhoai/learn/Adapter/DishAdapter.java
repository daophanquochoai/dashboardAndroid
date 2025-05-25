package doctorhoai.learn.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import doctorhoai.learn.Model.BillDishDto;
import doctorhoai.learn.R;

import java.util.List;

public class DishAdapter extends RecyclerView.Adapter<DishAdapter.DishViewHolder> {
    private List<BillDishDto> dishes;

    public DishAdapter(List<BillDishDto> dishes) {
        this.dishes = dishes;
    }

    @NonNull
    @Override
    public DishViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_dish, parent, false);
        return new DishViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DishViewHolder holder, int position) {
        BillDishDto dish = dishes.get(position);
        holder.tvDishName.setText("Tên món: " + (dish.getName() != null ? dish.getName() : "N/A"));
        holder.tvDishPrice.setText("Giá: " + (dish.getPrice() != null ? dish.getPrice() + " VNĐ" : "N/A"));
    }

    @Override
    public int getItemCount() {
        return dishes != null ? dishes.size() : 0;
    }

    static class DishViewHolder extends RecyclerView.ViewHolder {
        TextView tvDishName, tvDishPrice;

        DishViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDishName = itemView.findViewById(R.id.tv_dish_name);
            tvDishPrice = itemView.findViewById(R.id.tv_dish_price);
        }
    }
}