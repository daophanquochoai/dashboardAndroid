package doctorhoai.learn.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import doctorhoai.learn.Activity.LoginActivity;
import doctorhoai.learn.Api.DishService;
import doctorhoai.learn.Model.ErrorResponse;
import doctorhoai.learn.Model.Response;
import doctorhoai.learn.Model.TypeDish;
import doctorhoai.learn.R;
import doctorhoai.learn.Utils.ShareData;
import retrofit2.Call;
import retrofit2.Callback;
import java.util.ArrayList;
import java.util.List;

public class DishManagerFragment extends Fragment {
    private TableLayout tblTypeDishes;
    private ImageView imgTypeDish;
    private ProgressBar progressBar;
    private ShareData shareData;
    private String token;
    private Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").setLenient().create();
    private List<TypeDish> typeDishList = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_type_dish_manager, container, false);

        // Ánh xạ view
        tblTypeDishes = view.findViewById(R.id.table_type_dish);
        imgTypeDish = view.findViewById(R.id.img_type_dish);
        progressBar = view.findViewById(R.id.progressBar_type_dish);

        // Khởi tạo ShareData và token
        shareData = ShareData.getInstance(getActivity());
        token = shareData.getToken();

        // Lấy danh sách loại món ăn
        fetchTypeDishes();

        return view;
    }

    private void fetchTypeDishes() {
        progressBar.setVisibility(View.VISIBLE);
        imgTypeDish.setVisibility(View.GONE);
        tblTypeDishes.setVisibility(View.GONE);

        DishService.apiService.getTypeDishes("Bearer " + token, "0", "10", "asc", "none", "name", "")
                .enqueue(new Callback<Response>() {
                    @Override
                    public void onResponse(Call<Response> call, retrofit2.Response<Response> response) {
                        progressBar.setVisibility(View.GONE);
                        if (response.isSuccessful()) {
                            try {
                                Response res = response.body();
                                JsonObject data = gson.toJsonTree(res.getData()).getAsJsonObject();
                                typeDishList = gson.fromJson(data.get("content"), new com.google.gson.reflect.TypeToken<List<TypeDish>>(){}.getType());
                                if (typeDishList.isEmpty()) {
                                    imgTypeDish.setVisibility(View.VISIBLE);
                                    tblTypeDishes.setVisibility(View.GONE);
                                } else {
                                    imgTypeDish.setVisibility(View.GONE);
                                    tblTypeDishes.setVisibility(View.VISIBLE);
                                    displayTypeDishes();
                                }
                            } catch (Exception e) {
                                Toast.makeText(getContext(), "Lỗi khi hiển thị loại món ăn: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            handleErrorResponse(response);
                        }
                    }

                    @Override
                    public void onFailure(Call<Response> call, Throwable t) {
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(getContext(), "Hệ thống xảy ra lỗi: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void displayTypeDishes() {
        tblTypeDishes.removeAllViews();

        // Header
        TableRow headerRow = new TableRow(getContext());
        String[] headers = {"ID loại món", "Tên loại món"};
        for (String header : headers) {
            TextView tv = new TextView(getContext());
            tv.setText(header);
            tv.setPadding(8, 8, 8, 8);
            tv.setTextColor(getResources().getColor(R.color.white));
            tv.setGravity(android.view.Gravity.CENTER);
            headerRow.addView(tv);
        }
        headerRow.setBackgroundResource(R.drawable.bg_table);
        tblTypeDishes.addView(headerRow);

        // Dữ liệu
        for (TypeDish typeDish : typeDishList) {
            TableRow row = new TableRow(getContext());
            row.setBackgroundResource(R.drawable.bg_table_row);
            row.setOnClickListener(v -> navigateToDishList(typeDish.getId()));

            // ID
            TextView tvId = new TextView(getContext());
            tvId.setText(typeDish.getId() != null ? typeDish.getId().substring(0, 8) : "N/A");
            tvId.setPadding(8, 8, 8, 8);
            tvId.setGravity(android.view.Gravity.CENTER);
            row.addView(tvId);

            // Tên loại món
            TextView tvName = new TextView(getContext());
            tvName.setText(typeDish.getName() != null ? typeDish.getName() : "N/A");
            tvName.setPadding(8, 8, 8, 8);
            tvName.setGravity(android.view.Gravity.CENTER);
            row.addView(tvName);

            tblTypeDishes.addView(row);
        }
    }

    private void navigateToDishList(String typeDishId) {
        DishListFragment fragment = DishListFragment.newInstance(typeDishId);
        getParentFragmentManager().beginTransaction()
                .replace(R.id.frame_container, fragment)
                .addToBackStack(null)
                .commit();
    }

    private void handleErrorResponse(retrofit2.Response<Response> response) {
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
                Toast.makeText(
                        getContext(),
                        (errorResponse != null && errorResponse.getMessage() != null && !errorResponse.getMessage().isEmpty())
                                ? errorResponse.getMessage()
                                : "Đã xảy ra lỗi không xác định",
                        Toast.LENGTH_SHORT
                ).show();
            } catch (Exception e) {
                Toast.makeText(getContext(), "Lỗi khi xử lý phản hồi", Toast.LENGTH_SHORT).show();
            }
        }
    }
}