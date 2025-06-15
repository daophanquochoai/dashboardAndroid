package doctorhoai.learn.Fragment;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import doctorhoai.learn.Activity.LoadingDialog;
import doctorhoai.learn.Activity.LoginActivity;
import doctorhoai.learn.Api.RoomService;
import doctorhoai.learn.Model.ErrorResponse;
import doctorhoai.learn.Model.Response;
import doctorhoai.learn.Model.Room;
import doctorhoai.learn.R;
import doctorhoai.learn.Utils.ShareData;
import retrofit2.Call;
import retrofit2.Callback;

public class ThemPhongFragment extends Fragment {

    private TextView tv_title, tv_err_name;
    private EditText edt_name;
    private ShareData shareData;
    private String token;
    private LoadingDialog loadingDialog;
    private ObjectMapper mapper = new ObjectMapper();
    private Spinner spinnerStatus;
    private LinearLayout layoutChairContainer;
    private Button btnCancel, btnSave;
    private static final int PICK_IMAGE_REQUEST = 1;
    private Room room = new Room("", "", new ArrayList<>(), "", "ACTIVE", 0);
    private List<List<Integer>> currentChairMatrix = new ArrayList<>();
    private String selectedBranchId = "";
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_them_phong, container, false);

        initView(view);

        Bundle args = getArguments();
        if (args != null && args.getSerializable("data") instanceof Room) {
            room = (Room) args.getSerializable("data");
            if (room != null) {
                currentChairMatrix = room.getPositionChair();
                drawChairLayout(currentChairMatrix);
            }
        }
        Bundle bundle = getArguments();
        if (bundle != null) {
            String type = bundle.getString("type");
            selectedBranchId = bundle.getString("branchId");
            if ("ADD".equals(type)) {
                tv_title.setText("Thêm phòng");
                btnSave.setText("Thêm");
                if (currentChairMatrix == null || currentChairMatrix.isEmpty()) {
                    currentChairMatrix = new ArrayList<>();
                    for (int i = 0; i < 14; i++) {
                        List<Integer> row = new ArrayList<>();
                        for (int j = 0; j < 20; j++) {
                            row.add(0);
                        }
                        currentChairMatrix.add(row);
                    }
                }

                drawChairLayout(currentChairMatrix);
                functionAdd();
            }else{
                tv_title.setText("Cập nhật phòng");
                btnSave.setText("Cập nhật");
                Room room1 = (Room) bundle.getSerializable("data");
                room.setBranchId(selectedBranchId);

                edt_name.setText(room1.getName());
                spinnerStatus.setSelection(room1.getStatus().equals("ACTIVE") ? 0 : 1);
                currentChairMatrix = room1.getPositionChair();
                drawChairLayout(currentChairMatrix);

                functionUpdate();
            }
        }
        else{
            Objects.requireNonNull(getActivity()).getSupportFragmentManager().popBackStack();
        }
        spinnerStatus.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if( position == 0 ){
                    room.setStatus("ACTIVE");
                }else {
                    room.setStatus("DELETE");
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Objects.requireNonNull(getActivity()).getSupportFragmentManager().popBackStack();
            }
        });

        return view;
    }

    private void functionUpdate() {
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = edt_name.getText().toString();
                tv_err_name.setVisibility(View.GONE);

                if (name.isEmpty()) {
                    tv_err_name.setVisibility(View.VISIBLE);
                    tv_err_name.setText("Tên phòng chiếu không thể trống");
                    return;
                }

                if (currentChairMatrix == null || currentChairMatrix.isEmpty()) {
                    Toast.makeText(getActivity(), "Vui lòng chọn sơ đồ ghế", Toast.LENGTH_SHORT).show();
                    return;
                }

                room.setName(name);
                room.setPositionChair(currentChairMatrix);
                room.setBranchId(selectedBranchId);
                room.setSlot(calculateSlot(currentChairMatrix));
//                Log.d("DEBUG", "room.getId() = " + room.getId());
//                Log.d("DEBUG", "room.getName() = " + room.getName());
//                Log.d("DEBUG", "room.getBranchId() = " + room.getBranchId());
//                Log.d("DEBUG", "room.getStatus() = " + room.getStatus());
//                Log.d("DEBUG", "room.getSlot() = " + room.getSlot());
//                Log.d("DEBUG", "room.getPositionChair() = " + room.getPositionChair());
                loadingDialog.startLoadingDialog();
                if (room.getBranchId() == null) {
                    room.setBranchId(selectedBranchId);
                }

                Log.d("DEBUG", "Sending room: " + new Gson().toJson(room));

                RoomService.apiService.updateRoom("Bearer " + token, room.getId(), room).enqueue(new Callback<Response>() {
                    @Override
                    public void onResponse(Call<Response> call, retrofit2.Response<Response> response) {
                        loadingDialog.dismissDialog();

                        if (response.isSuccessful()) {
                            Toast.makeText(getActivity(), "Cập nhật phòng chiếu thành công", Toast.LENGTH_SHORT).show();
                            Objects.requireNonNull(getActivity()).getSupportFragmentManager().popBackStack();
                        } else if (response.code() == 401) {
                            Toast.makeText(getActivity(), "Hết phiên làm việc", Toast.LENGTH_SHORT).show();
                            shareData.clearToken();
                            Intent intent = new Intent(getActivity(), LoginActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                        } else if (response.code() == 400) {
                            try {
                                String errorJson = response.errorBody().string();
                                ErrorResponse errorResponse = new Gson().fromJson(errorJson, ErrorResponse.class);
                                Toast.makeText(getActivity(), errorResponse.getMessage(), Toast.LENGTH_SHORT).show();
                            } catch (IOException e) {
                                Toast.makeText(getActivity(), "Lỗi hệ thống", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(getActivity(), "Hệ thống lỗi", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<Response> call, Throwable t) {
                        loadingDialog.dismissDialog();
                        Toast.makeText(getActivity(), "Không thể kết nối máy chủ", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }


    private void functionAdd() {
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = edt_name.getText().toString().trim();
                tv_err_name.setVisibility(View.GONE);

                if (name.isEmpty()) {
                    tv_err_name.setVisibility(View.VISIBLE);
                    tv_err_name.setText("Tên phòng không thể trống");
                    return;
                }

                if (currentChairMatrix == null || currentChairMatrix.isEmpty()) {
                    Toast.makeText(getActivity(), "Vui lòng chọn sơ đồ ghế", Toast.LENGTH_SHORT).show();
                    return;
                }

                room.setName(name);
                room.setPositionChair(currentChairMatrix);
                room.setBranchId(selectedBranchId);
                room.setStatus("ACTIVE");
                room.setSlot(calculateSlot(currentChairMatrix));

                loadingDialog.startLoadingDialog();

                RoomService.apiService.addRoom("Bearer " + token, room).enqueue(new Callback<Response>() {
                    @Override
                    public void onResponse(Call<Response> call, retrofit2.Response<Response> response) {
                        loadingDialog.dismissDialog();

                        if (response.isSuccessful()) {
                            Toast.makeText(getActivity(), "Thêm phòng chiếu thành công", Toast.LENGTH_SHORT).show();
                            Objects.requireNonNull(getActivity()).getSupportFragmentManager().popBackStack();
                        } else if (response.code() == 401) {
                            Toast.makeText(getActivity(), "Hết phiên làm việc", Toast.LENGTH_SHORT).show();
                            shareData.clearToken();
                            Intent intent = new Intent(getActivity(), LoginActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                        } else if (response.code() == 400) {
                            try {
                                String errorJson = response.errorBody().string();
                                ErrorResponse errorResponse = new Gson().fromJson(errorJson, ErrorResponse.class);
                                Toast.makeText(getActivity(), errorResponse.getMessage(), Toast.LENGTH_SHORT).show();
                            } catch (IOException e) {
                                Toast.makeText(getActivity(), "Lỗi hệ thống", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(getActivity(), "Hệ thống lỗi", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<Response> call, Throwable t) {
                        loadingDialog.dismissDialog();
                        Toast.makeText(getActivity(), "Không thể kết nối máy chủ", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    private void initView(View view) {
        shareData = ShareData.getInstance(getActivity());
        token = shareData.getToken();

        edt_name = view.findViewById(R.id.edt_name_room);
        spinnerStatus = view.findViewById(R.id.spinner_status);
        layoutChairContainer = view.findViewById(R.id.layoutChairContainer);
        List<String> list = List.of("Hoạt động", "N.Hoạt động");
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                getActivity(),
                android.R.layout.simple_spinner_item,
                list
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerStatus.setAdapter(adapter);
        tv_title = view.findViewById(R.id.tvTitle);
        btnSave = view.findViewById(R.id.btn_save);
        btnCancel = view.findViewById(R.id.btn_cancel);

        loadingDialog = new LoadingDialog(getActivity());
        tv_err_name = view.findViewById(R.id.txt_error_name);
    }
    private int calculateSlot(List<List<Integer>> matrix) {
        int count = 0;
        for (List<Integer> row : matrix) {
            for (int seat : row) {
                if (seat == 1) count++;
                else if (seat == 2) count += 2;
            }
        }
        return count;
    }
    private void drawChairLayout(List<List<Integer>> positionChair) {
        layoutChairContainer.removeAllViews();

        TextView screenView = new TextView(getContext());
        screenView.setText("MÀN HÌNH");
        screenView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        screenView.setTextColor(Color.BLACK);
        screenView.setTextSize(18);
        screenView.setPadding(0, 20, 0, 20);
        screenView.setBackgroundColor(Color.LTGRAY);
        layoutChairContainer.addView(screenView);

        for (int rowIndex = 0; rowIndex < positionChair.size(); rowIndex++) {
            List<Integer> row = positionChair.get(rowIndex);

            GridLayout rowLayout = new GridLayout(getContext());
            rowLayout.setColumnCount(row.size());
            rowLayout.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT));
            rowLayout.setPadding(0, 10, 0, 10);

            for (int colIndex = 0; colIndex < row.size(); ) {
                int seatType = row.get(colIndex);

                TextView chair = new TextView(getContext());
                chair.setText(" ");
                chair.setHeight(80);
                chair.setClickable(true);
                chair.setFocusable(true);
                GridLayout.LayoutParams params = new GridLayout.LayoutParams();
                params.setMargins(5, 5, 5, 5);
                chair.setLayoutParams(params);

                if (seatType == 1) {
                    chair.setWidth(80);
                    chair.setBackgroundColor(Color.parseColor("#4CAF50"));
                } else if (seatType == 2) {
                    chair.setWidth(170); // ghế đôi
                    chair.setBackgroundColor(Color.parseColor("#FF9800"));
                } else {
                    chair.setWidth(80);
                    chair.setBackgroundColor(Color.LTGRAY);
                }

                int finalRow = rowIndex;
                int finalCol = colIndex;
                chair.setOnClickListener(v -> {
                    int currentType = positionChair.get(finalRow).get(finalCol);

                    if (currentType == 0) {
                        positionChair.get(finalRow).set(finalCol, 1);
                    } else if (currentType == 1) {
                        if (finalCol + 1 < row.size() && row.get(finalCol + 1) == 0) {
                            row.set(finalCol, 2);
                            row.set(finalCol + 1, -1);
                        } else {
                            row.set(finalCol, 0);
                        }
                    } else if (currentType == 2) {
                        positionChair.get(finalRow).set(finalCol, 0);
                        if (finalCol + 1 < positionChair.get(finalRow).size() &&
                                positionChair.get(finalRow).get(finalCol + 1) == -1) {
                            positionChair.get(finalRow).set(finalCol + 1, 0);
                        }
                    }

                    drawChairLayout(positionChair); // redraw
                });

                rowLayout.addView(chair);

                if (seatType == 2) {
                    colIndex += 2;
                } else if (seatType == -1) {
                    colIndex++;
                    continue;
                } else {
                    colIndex++;
                }
            }

            layoutChairContainer.addView(rowLayout);
        }
    }
}