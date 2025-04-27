package doctorhoai.learn.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import doctorhoai.learn.Model.RevenueFilm;
import doctorhoai.learn.R;
import doctorhoai.learn.Utils.LargeValueFormatter;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class FilmRevenueAdapter extends RecyclerView.Adapter<FilmRevenueAdapter.ViewHolder> {

    private Context context;
    private List<RevenueFilm> list;

    public FilmRevenueAdapter(Context context, List<RevenueFilm> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public @NotNull ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.film_revenue_item, viewGroup, false);
        return new FilmRevenueAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull FilmRevenueAdapter.ViewHolder viewHolder, int i) {
        RevenueFilm revenueFilm = list.get(i);
        viewHolder.txt_film.setText(revenueFilm.getName().length() > 50 ? revenueFilm.getName().substring(0, 50) + "..." : revenueFilm.getName());
        viewHolder.txt_revenue.setText(new LargeValueFormatter().getFormattedValue(Float.valueOf(revenueFilm.getTotal_revenue())));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private TextView txt_film, txt_revenue;

        public ViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            txt_revenue = itemView.findViewById(R.id.txt_revenue);
            txt_film = itemView.findViewById(R.id.txt_film);
        }
    }
}
