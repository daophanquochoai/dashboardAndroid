package doctorhoai.learn.Fragment;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
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
import doctorhoai.learn.Model.Dish;
import doctorhoai.learn.Model.ErrorResponse;
import doctorhoai.learn.Model.Response;
import doctorhoai.learn.Model.TypeDish;
import doctorhoai.learn.R;
import doctorhoai.learn.Utils.ShareData;
import retrofit2.Call;
import retrofit2.Callback;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class DishListFragment extends Fragment {
    private TableLayout tblDishes;
    private Button btnThem;
    private EditText edtSearch;
    private Spinner spinnerAsc, spinnerOrderBy;
    private ImageView imgPrev, imgNext, imgTable;
    private TextView tvCounter;
    private ProgressBar progressBar;
    private ShareData shareData;
    private String token;
    private Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").setLenient().create();
    private List<Dish> dishList = new ArrayList<>();
    private List<TypeDish> typeDishList = new ArrayList<>();
    private int currentPage = 1;
    private String orderBy = "name";
    private String asc = "true";
    private String query = "";
    private String typeDishId;
    private String typeDishName;

    private static final String ARG_TYPE_DISH_ID = "type_dish_id";

    public static DishListFragment newInstance(String typeDishId) {
        DishListFragment fragment = new DishListFragment();
        Bundle args = new Bundle();
        args.putString(ARG_TYPE_DISH_ID, typeDishId);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dish_list, container, false);

        // Ánh xạ view
        tblDishes = view.findViewById(R.id.table_dish);
        btnThem = view.findViewById(R.id.btn_them);
        edtSearch = view.findViewById(R.id.edt_search);
        spinnerAsc = view.findViewById(R.id.spinner_asc);
        spinnerOrderBy = view.findViewById(R.id.spinner_orderBy);
        imgPrev = view.findViewById(R.id.img_prev);
        imgNext = view.findViewById(R.id.img_next);
        tvCounter = view.findViewById(R.id.tv_counter);
        imgTable = view.findViewById(R.id.img_table);
        progressBar = view.findViewById(R.id.proccesBar_table_dish);

        // Khởi tạo ShareData và token
        shareData = ShareData.getInstance(getActivity());
        token = shareData.getToken();

        // Lấy typeDishId từ arguments
        if (getArguments() != null) {
            typeDishId = getArguments().getString(ARG_TYPE_DISH_ID);
        }

        // Thiết lập Spinner
        setupSpinners();

        // Lấy danh sách loại món ăn và món ăn
        fetchTypeDishes();

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
                query = s.toString().trim().toLowerCase();
                filterDishes();
            }
        });

        // Xử lý phân trang
        imgPrev.setOnClickListener(v -> {
            if (currentPage > 1) {
                currentPage--;
                filterDishes();
            }
        });

        imgNext.setOnClickListener(v -> {
            currentPage++;
            filterDishes();
        });

        return view;
    }

    private void setupSpinners() {
        // Spinner orderBy
        String[] orderByOptions = {"name", "price"};
        ArrayAdapter<String> orderByAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, orderByOptions);
        orderByAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerOrderBy.setAdapter(orderByAdapter);
        spinnerOrderBy.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                orderBy = orderByOptions[position];
                filterDishes();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        // Spinner asc
        String[] ascOptions = {"Tăng dần", "Giảm dần"};
        ArrayAdapter<String> ascAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, ascOptions);
        ascAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerAsc.setAdapter(ascAdapter);
        spinnerAsc.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                asc = position == 0 ? "true" : "false";
                filterDishes();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    private void fetchTypeDishes() {
        progressBar.setVisibility(View.VISIBLE);
        imgTable.setVisibility(View.GONE);
        tblDishes.setVisibility(View.GONE);

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

                                // Tìm loại món ăn và danh sách món ăn tương ứng
                                for (TypeDish typeDish : typeDishList) {
                                    if (typeDish.getId().equals(typeDishId)) {
                                        typeDishName = typeDish.getName();
                                        dishList = typeDish.getDishes();
                                        break;
                                    }
                                }

                                if (dishList == null || dishList.isEmpty()) {
                                    imgTable.setVisibility(View.VISIBLE);
                                    tblDishes.setVisibility(View.GONE);
                                } else {
                                    imgTable.setVisibility(View.GONE);
                                    tblDishes.setVisibility(View.VISIBLE);
                                    filterDishes();
                                }
                            } catch (Exception e) {
                                Toast.makeText(getContext(), "Lỗi khi lấy danh sách món ăn: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            handleErrorResponse(response);
                        }
                    }

                    @Override
                    public void onFailure(Call<Response> call, Throwable t) {
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(getContext(), "Hệ thống xảy ra lỗi khi lấy loại món ăn: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void filterDishes() {
        List<Dish> filteredList = new ArrayList<>();

        // Lọc theo query và trạng thái ACTIVE
        for (Dish dish : dishList) {
            if (dish.getActive().equals("ACTIVE") &&
                    (query.isEmpty() || dish.getName().toLowerCase().contains(query))) {
                filteredList.add(dish);
            }
        }

        // Sắp xếp
        if (orderBy.equals("name")) {
            filteredList.sort((d1, d2) -> asc.equals("true") ?
                    d1.getName().compareTo(d2.getName()) :
                    d2.getName().compareTo(d1.getName()));
        } else if (orderBy.equals("price")) {
            filteredList.sort((d1, d2) -> asc.equals("true") ?
                    Float.compare(d1.getPrice(), d2.getPrice()) :
                    Float.compare(d2.getPrice(), d1.getPrice()));
        }

        // Phân trang
        int start = (currentPage - 1) * 10;
        int end = Math.min(start + 10, filteredList.size());
        List<Dish> pagedList = start < filteredList.size() ?
                filteredList.subList(start, end) : new ArrayList<>();

        // Hiển thị
        if (pagedList.isEmpty()) {
            imgTable.setVisibility(View.VISIBLE);
            tblDishes.setVisibility(View.GONE);
        } else {
            imgTable.setVisibility(View.GONE);
            tblDishes.setVisibility(View.VISIBLE);
            displayDishes(pagedList);
        }
        tvCounter.setText(String.valueOf(currentPage));
    }

    private void displayDishes(List<Dish> dishes) {
        tblDishes.removeAllViews();

        // Header
        TableRow headerRow = new TableRow(getContext());
        String[] headers = {"ID món ăn", "Tên món", "Giá", "Trạng thái"};
        for (String header : headers) {
            TextView tv = new TextView(getContext());
            tv.setText(header);
            tv.setPadding(8, 8, 8, 8);
            tv.setTextColor(getResources().getColor(R.color.white));
            tv.setGravity(android.view.Gravity.CENTER);
            headerRow.addView(tv);
        }
        headerRow.setBackgroundResource(R.drawable.bg_table);
        tblDishes.addView(headerRow);
        // Định dạng giá tiền
        DecimalFormat decimalFormat = new DecimalFormat("#,###");
        // Dữ liệu
        for (Dish dish : dishes) {
            TableRow row = new TableRow(getContext());
            row.setBackgroundResource(R.drawable.bg_table_row);
            row.setOnClickListener(v -> showDishDetailDialog(dish));

            // ID
            TextView tvId = new TextView(getContext());
            tvId.setText(dish.getId() != null ? dish.getId().substring(0, 8) : "N/A");
            tvId.setPadding(8, 8, 8, 8);
            tvId.setGravity(android.view.Gravity.CENTER);
            row.addView(tvId);

            // Tên món
            TextView tvName = new TextView(getContext());
            tvName.setText(dish.getName() != null ? dish.getName() : "N/A");
            tvName.setPadding(8, 8, 8, 8);
            tvName.setGravity(android.view.Gravity.CENTER);
            row.addView(tvName);

            // Giá
            TextView tvPrice = new TextView(getContext());
            tvPrice.setText(dish.getPrice() != null ? decimalFormat.format(dish.getPrice()) + " VND" : "N/A");
            tvPrice.setPadding(8, 8, 8, 8);
            tvPrice.setGravity(android.view.Gravity.CENTER);
            row.addView(tvPrice);

            // Trạng thái
            TextView tvActive = new TextView(getContext());
            tvActive.setText(dish.getActive() != null ? dish.getActive() : "N/A");
            tvActive.setPadding(8, 8, 8, 8);
            tvActive.setGravity(android.view.Gravity.CENTER);
            row.addView(tvActive);

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
        TextView tvTypeDish = dialogView.findViewById(R.id.tv_type_dish);
        Button btnSave = dialogView.findViewById(R.id.btn_save);
        Button btnCancel = dialogView.findViewById(R.id.btn_cancel);

        tvTypeDish.setText(typeDishName != null ? typeDishName : "N/A");

        AlertDialog dialog = builder.create();

        btnSave.setOnClickListener(v -> {
            String name = edtName.getText().toString().trim();
            String priceStr = edtPrice.getText().toString().trim();
            String image = edtImage.getText().toString().trim();

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
                dish.setTypeDish(typeDishId); // Gán typeDishId

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
        progressBar.setVisibility(View.VISIBLE);
        DishService.apiService.addDish("Bearer " + token, dish)
                .enqueue(new Callback<Response>() {
                    @Override
                    public void onResponse(Call<Response> call, retrofit2.Response<Response> response) {
                        progressBar.setVisibility(View.GONE);
                        if (response.isSuccessful()) {
                            fetchTypeDishes(); // Làm mới danh sách
                            Toast.makeText(getContext(), "Thêm món ăn thành công", Toast.LENGTH_SHORT).show();
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

    private void showDishDetailDialog(Dish dish) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_edit_dish, null);
        builder.setView(dialogView);

        EditText edtName = dialogView.findViewById(R.id.edt_name);
        EditText edtPrice = dialogView.findViewById(R.id.edt_price);
        EditText edtImage = dialogView.findViewById(R.id.edt_image);
        TextView tvTypeDish = dialogView.findViewById(R.id.tv_type_dish);
        Spinner spinnerStatus = dialogView.findViewById(R.id.spinner_status);
        Button btnSave = dialogView.findViewById(R.id.btn_save);
        Button btnCancel = dialogView.findViewById(R.id.btn_cancel);
        Button btnDelete = dialogView.findViewById(R.id.btn_delete);

        // Hiển thị tên loại món ăn
        String currentTypeDishId = dish.getTypeDish() != null ? dish.getTypeDish() : typeDishId;
        String currentTypeDishName = typeDishName != null ? typeDishName : "N/A";
        tvTypeDish.setText(currentTypeDishName);

        // Thiết lập Spinner cho Status
        String[] statusOptions = {"ACTIVE", "DELETE"};
        ArrayAdapter<String> statusAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, statusOptions);
        statusAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerStatus.setAdapter(statusAdapter);

        // Chọn Status hiện tại
        String currentStatus = dish.getActive();
        for (int i = 0; i < statusOptions.length; i++) {
            if (statusOptions[i].equals(currentStatus)) {
                spinnerStatus.setSelection(i);
                break;
            }
        }

        edtName.setText(dish.getName());
        edtPrice.setText(dish.getPrice() != null ? dish.getPrice().toString() : "");
        edtImage.setText(dish.getImage());

        AlertDialog dialog = builder.create();

        btnSave.setOnClickListener(v -> {
            String name = edtName.getText().toString().trim();
            String priceStr = edtPrice.getText().toString().trim();
            String image = edtImage.getText().toString().trim();
            int selectedStatusPosition = spinnerStatus.getSelectedItemPosition();

            if (name.isEmpty() || priceStr.isEmpty() || selectedStatusPosition == -1) {
                Toast.makeText(getContext(), "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
                return;
            }

            try {
                Float price = Float.parseFloat(priceStr);
                dish.setName(name);
                dish.setPrice(price);
                dish.setImage(image.isEmpty() ? null : image);
                dish.setTypeDish(currentTypeDishId);
                dish.setActive(statusOptions[selectedStatusPosition]);

                updateDish(dish);
                dialog.dismiss();
            } catch (NumberFormatException e) {
                Toast.makeText(getContext(), "Giá không hợp lệ", Toast.LENGTH_SHORT).show();
            }
        });

        btnDelete.setOnClickListener(v -> {
            showDeleteConfirmationDialog(dish, dialog);
        });

        btnCancel.setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }

    private void showDeleteConfirmationDialog(Dish dish, AlertDialog parentDialog) {
        new AlertDialog.Builder(getContext())
                .setTitle("Xóa món ăn")
                .setMessage("Bạn có chắc muốn xóa món " + dish.getName() + "?")
                .setPositiveButton("Xóa", (dialog, which) -> {
                    dish.setActive("DELETE");
                    updateDish(dish);
                    parentDialog.dismiss();
                })
                .setNegativeButton("Hủy", null)
                .show();
    }

    private void updateDish(Dish dish) {
        progressBar.setVisibility(View.VISIBLE);
        DishService.apiService.updateDish("Bearer " + token, dish.getId(), dish)
                .enqueue(new Callback<Response>() {
                    @Override
                    public void onResponse(Call<Response> call, retrofit2.Response<Response> response) {
                        progressBar.setVisibility(View.GONE);
                        if (response.isSuccessful()) {
                            fetchTypeDishes(); // Làm mới danh sách
                            Toast.makeText(getContext(), dish.getActive().equals("DELETE") ? "Xóa món ăn thành công" : "Cập nhật món ăn thành công", Toast.LENGTH_SHORT).show();
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

    private void handleErrorResponse(retrofit2.Response<Response> response) {
        if (response.code() == 401) {
            Toast.makeText(getContext(), "Hết phiên làm việc", Toast.LENGTH_SHORT).show();
            shareData.clearToken();
            startActivity(new Intent(getActivity(), LoginActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
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