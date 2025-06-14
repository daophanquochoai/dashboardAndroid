package doctorhoai.learn.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import doctorhoai.learn.Api.BranchService;
import doctorhoai.learn.Model.Bill;
import doctorhoai.learn.Model.Branch;
import doctorhoai.learn.R;

public class BranchAdapter extends RecyclerView.Adapter<BranchAdapter.BranchViewHolder> {
    private Context context;
    private List<Branch> branchList;
    private OnBranchClickListener listener;
    public interface OnBranchClickListener {
        void onBranchClick(Branch branch);
    }

    public BranchAdapter(Context context, List<Branch> branchList, OnBranchClickListener listener) {
        this.context = context;
        this.branchList = branchList;
        this.listener = listener;
    }
    public void setBranchs(List<Branch> newBranch) {
        this.branchList = newBranch;
        notifyDataSetChanged();
    }
    @NonNull
    @Override
    public BranchViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.fragment_room, parent, false);
        return new BranchAdapter.BranchViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BranchViewHolder holder, int position) {
        Branch branch = branchList.get(position);
        holder.tvId.setText(branch.getId());
        holder.tvName.setText(branch.getNameBranch());
        holder.tvAddress.setText(branch.getAddress());

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onBranchClick(branch);
            }
        });
    }

    @Override
    public int getItemCount() {
        return branchList != null ? branchList.size() : 0;
    }

    public static class BranchViewHolder extends RecyclerView.ViewHolder {
        TextView tvId, tvName, tvAddress;

        public BranchViewHolder(@NonNull View itemView) {
            super(itemView);
            tvId = itemView.findViewById(R.id.edt_id);
            tvName = itemView.findViewById(R.id.edt_name_branch);
            tvAddress = itemView.findViewById(R.id.edt_address);
        }
    }
}
