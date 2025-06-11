package doctorhoai.learn.Fragment;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import doctorhoai.learn.Activity.LoadingDialog;
import doctorhoai.learn.Activity.LoginActivity;
import doctorhoai.learn.Api.DishService;
import doctorhoai.learn.Model.Dish;
import doctorhoai.learn.Model.ErrorResponse;
import doctorhoai.learn.Model.Response;
import doctorhoai.learn.R;
import doctorhoai.learn.Utils.ShareData;
import retrofit2.Call;
import retrofit2.Callback;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class DishManagerFragment extends Fragment {
    private TableLayout tblDishes;
    private Button btnThem;
    private EditText edtSearch;
    private ShareData shareData;
    private String token;
    private Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").setLenient().create();
    private List<Dish> dishList = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dish_manager, container, false);

        // Ánh xạ view
        tblDishes = view.findViewById(R.id.tbl_dishes);
        btnThem = view.findViewById(R.id.btn_them);
        edtSearch = view.findViewById(R.id.edt_search);

        // Khởi tạo ShareData và token
        shareData = ShareData.getInstance(getActivity());
        token = shareData.getToken();

        // Lấy danh sách món ăn
        fetchDishes("");

        // Xử lý nút thêm
        btnThem.setOnClickListener(v -> showAddDishDialog());

        // Xử lý tìm kiếm
        edtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                fetchDishes(s.toString());
            }
        });

        return view;
    }

    private void fetchDishes(String query) {
        LoadingDialog loadingDialog = new LoadingDialog(getActivity());
        loadingDialog.startLoadingDialog();

        DishService.apiService.getAllDishes("Bearer " + token, "1", "10", "ACTIVE", "name", "true", query)
                .enqueue(new Callback<Response>() {
                    @Override
                    public void onResponse(Call<Response> call, retrofit2.Response<Response> response) {
                        loadingDialog.dismissDialog();
                        if (response.isSuccessful()) {
                            try {
                                Response res = gson.fromJson(gson.toJson(response.body()), Response.class);
                                dishList = gson.fromJson(gson.toJson(res.getData()), new com.google.gson.reflect.TypeToken<List<Dish>>(){}.getType());
                                displayDishes();
                            } catch (Exception e) {
                                Toast.makeText(getContext(), "Lỗi khi hiển thị món ăn", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            handleErrorResponse(response);
                        }
                    }

                    @Override
                    public void onFailure(Call<Response> call, Throwable t) {
                        loadingDialog.dismissDialog();
                        Toast.makeText(getContext(), "Hệ thống xảy ra lỗi", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void displayDishes() {
        tblDishes.removeAllViews();

        // Header
        TableRow headerRow = new TableRow(getContext());
        String[] headers = {"ID", "Tên món", "Giá", "Trạng thái", "Hành động"};
        for (String header : headers) {
            TextView tv = new TextView(getContext());
            tv.setText(header);
            tv.setPadding(8, 8, 8, 8);
            tv.setTextColor(getResources().getColor(android.R.color.black));
            tv.setTextSize(16);
            headerRow.addView(tv);
        }
        tblDishes.addView(headerRow);

        // Dữ liệu
        for (Dish dish : dishList) {
            TableRow row = new TableRow(getContext());
            row.setBackgroundResource(R.drawable.bg_border_bottom);
            row.setOnClickListener(v -> showDishDetail(dish));

            // ID
            TextView tvId = new TextView(getContext());
            tvId.setText(dish.getId() != null ? dish.getId().substring(0, 8) : "N/A");
            tvId.setPadding(8, 8, 8, 8);
            row.addView(tvId);

            // Tên món
            TextView tvName = new TextView(getContext());
            tvName.setText(dish.getName() != null ? dish.getName() : "N/A");
            tvName.setPadding(8, 8, 8, 8);
            row.addView(tvName);

            // Giá
            TextView tvPrice = new TextView(getContext());
            tvPrice.setText(dish.getPrice() != null ? dish.getPrice() + " VNĐ" : "N/A");
            tvPrice.setPadding(8, 8, 8, 8);
            row.addView(tvPrice);

            // Trạng thái
            TextView tvActive = new TextView(getContext());
            tvActive.setText(dish.getActive() != null ? dish.getActive() : "N/A");
            tvActive.setPadding(8, 8, 8, 8);
            row.addView(tvActive);

            // Hành động
            TextView tvAction = new TextView(getContext());
            tvAction.setText("Xóa | Kích hoạt");
            tvAction.setTextColor(getResources().getColor(android.R.color.holo_blue_dark));
            tvAction.setPadding(8, 8, 8, 8);
            tvAction.setOnClickListener(v -> showActionDialog(dish));
            row.addView(tvAction);

            tblDishes.addView(row);
        }
    }

    private void showAddDishDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_add_dish, null);
        builder.setView(dialogView);

        EditText edtName = dialogView.findViewById(R.id.edt_name);
        EditText edtPrice = dialogView.findViewById(R.id.edt_price);
        EditText edtImage = dialogView.findViewById(R.id.edt_image);
        Button btnSave = dialogView.findViewById(R.id.btn_save);
        Button btnCancel = dialogView.findViewById(R.id.btn_cancel);

        AlertDialog dialog = builder.create();

        btnSave.setOnClickListener(v -> {
            String name = edtName.getText().toString();
            String priceStr = edtPrice.getText().toString();
            String image = edtImage.getText().toString();

            if (name.isEmpty() || priceStr.isEmpty()) {
                Toast.makeText(getContext(), "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
                return;
            }

            try {
                Float price = Float.parseFloat(priceStr);
                Dish dish = new Dish();
                dish.setName(name);
                dish.setPrice(price);
                dish.setImage(image.isEmpty() ? null : image);
                dish.setActive("ACTIVE");

                addDish(dish);
                dialog.dismiss();
            } catch (NumberFormatException e) {
                Toast.makeText(getContext(), "Giá không hợp lệ", Toast.LENGTH_SHORT).show();
            }
        });

        btnCancel.setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }

    private void addDish(Dish dish) {
        LoadingDialog loadingDialog = new LoadingDialog(getActivity());
        loadingDialog.startLoadingDialog();

        DishService.apiService.addDish("Bearer " + token, dish)
                .enqueue(new Callback<Response>() {
                    @Override
                    public void onResponse(Call<Response> call, retrofit2.Response<Response> response) {
                        loadingDialog.dismissDialog();
                        if (response.isSuccessful()) {
                            fetchDishes("");
                            Toast.makeText(getContext(), "Thêm món ăn thành công", Toast.LENGTH_SHORT).show();
                        } else {
                            handleErrorResponse(response);
                        }
                    }

                    @Override
                    public void onFailure(Call<Response> call, Throwable t) {
                        loadingDialog.dismissDialog();
                        Toast.makeText(getContext(), "Hệ thống xảy ra lỗi", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void showDishDetail(Dish dish) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_edit_dish, null);
        builder.setView(dialogView);

        EditText edtName = dialogView.findViewById(R.id.edt_name);
        EditText edtPrice = dialogView.findViewById(R.id.edt_price);
        EditText edtImage = dialogView.findViewById(R.id.edt_image);
        Button btnSave = dialogView.findViewById(R.id.btn_save);
        Button btnCancel = dialogView.findViewById(R.id.btn_cancel);

        edtName.setText(dish.getName());
        edtPrice.setText(dish.getPrice() != null ? dish.getPrice().toString() : "");
        edtImage.setText(dish.getImage());

        AlertDialog dialog = builder.create();

        btnSave.setOnClickListener(v -> {
            String name = edtName.getText().toString();
            String priceStr = edtPrice.getText().toString();
            String image = edtImage.getText().toString();

            if (name.isEmpty() || priceStr.isEmpty()) {
                Toast.makeText(getContext(), "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
                return;
            }

            try {
                Float price = Float.parseFloat(priceStr);
                dish.setName(name);
                dish.setPrice(price);
                dish.setImage(image.isEmpty() ? null : image);

                updateDish(dish);
                dialog.dismiss();
            } catch (NumberFormatException e) {
                Toast.makeText(getContext(), "Giá không hợp lệ", Toast.LENGTH_SHORT).show();
            }
        });

        btnCancel.setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }

    private void updateDish(Dish dish) {
        LoadingDialog loadingDialog = new LoadingDialog(getActivity());
        loadingDialog.startLoadingDialog();

        DishService.apiService.updateDish("Bearer " + token, dish.getId(), dish)
                .enqueue(new Callback<Response>() {
                    @Override
                    public void onResponse(Call<Response> call, retrofit2.Response<Response> response) {
                        loadingDialog.dismissDialog();
                        if (response.isSuccessful()) {
                            fetchDishes("");
                            Toast.makeText(getContext(), "Cập nhật món ăn thành công", Toast.LENGTH_SHORT).show();
                        } else {
                            handleErrorResponse(response);
                        }
                    }

                    @Override
                    public void onFailure(Call<Response> call, Throwable t) {
                        loadingDialog.dismissDialog();
                        Toast.makeText(getContext(), "Hệ thống xảy ra lỗi", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void showActionDialog(Dish dish) {
        new AlertDialog.Builder(getContext())
                .setTitle("Hành động")
                .setItems(new String[]{"Xóa", "Kích hoạt"}, (dialog, which) -> {
                    if (which == 0) {
                        confirmDeleteDish(dish);
                    } else {
                        activeDish(dish);
                    }
                })
                .setNegativeButton("Hủy", null)
                .show();
    }

    private void confirmDeleteDish(Dish dish) {
        new AlertDialog.Builder(getContext())
                .setTitle("Xóa món ăn")
                .setMessage("Bạn có chắc chắn muốn xóa món ăn " + dish.getName() + "?")
                .setPositiveButton("Xóa", (dialog, which) -> deleteDish(dish))
                .setNegativeButton("Hủy", null)
                .show();
    }

    private void deleteDish(Dish dish) {
        LoadingDialog loadingDialog = new LoadingDialog(getActivity());
        loadingDialog.startLoadingDialog();

        DishService.apiService.deleteDish("Bearer " + token, dish.getId())
                .enqueue(new Callback<Response>() {
                    @Override
                    public void onResponse(Call<Response> call, retrofit2.Response<Response> response) {
                        loadingDialog.dismissDialog();
                        if (response.isSuccessful()) {
                            fetchDishes("");
                            Toast.makeText(getContext(), "Xóa món ăn thành công", Toast.LENGTH_SHORT).show();
                        } else {
                            handleErrorResponse(response);
                        }
                    }

                    @Override
                    public void onFailure(Call<Response> call, Throwable t) {
                        loadingDialog.dismissDialog();
                        Toast.makeText(getContext(), "Hệ thống xảy ra lỗi", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void activeDish(Dish dish) {
        LoadingDialog loadingDialog = new LoadingDialog(getActivity());
        loadingDialog.startLoadingDialog();

        DishService.apiService.activeDish("Bearer " + token, dish.getId())
                .enqueue(new Callback<Response>() {
                    @Override
                    public void onResponse(Call<Response> call, retrofit2.Response<Response> response) {
                        loadingDialog.dismissDialog();
                        if (response.isSuccessful()) {
                            fetchDishes("");
                            Toast.makeText(getContext(), "Kích hoạt món ăn thành công", Toast.LENGTH_SHORT).show();
                        } else {
                            handleErrorResponse(response);
                        }
                    }

                    @Override
                    public void onFailure(Call<Response> call, Throwable t) {
                        loadingDialog.dismissDialog();
                        Toast.makeText(getContext(), "Hệ thống xảy ra lỗi", Toast.LENGTH_SHORT).show();
                    }
                });
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
                Toast.makeText(getContext(), errorResponse.getMessage(), Toast.LENGTH_SHORT).show();
            } catch (IOException e) {
                Toast.makeText(getContext(), "Lỗi khi xử lý phản hồi", Toast.LENGTH_SHORT).show();
            }
        }
    }
}