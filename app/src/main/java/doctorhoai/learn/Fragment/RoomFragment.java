package doctorhoai.learn.Fragment;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import doctorhoai.learn.Activity.LoginActivity;
import doctorhoai.learn.Api.RoomService;
import doctorhoai.learn.Model.ErrorResponse;
import doctorhoai.learn.Model.Param;
import doctorhoai.learn.Model.Response;
import doctorhoai.learn.Model.Room;
import doctorhoai.learn.R;
import doctorhoai.learn.Utils.ShareData;
import retrofit2.Call;
import retrofit2.Callback;

public class RoomFragment extends Fragment {
    private Param param = new Param(0,"asc", 10, "name", "", "none");
    private TableLayout roomTable;
    private ShareData shareData;
    private ProgressBar progressBar;
    private ObjectMapper mapper = new ObjectMapper();
    private List<Room> rooms;
    private Button btnThem;
    private ImageView imgNotFound;
    private EditText edtSearch;
    private String branchId;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_room, container, false);

        initView(view);
        Bundle bundle = getArguments();
        if (bundle != null) {
            branchId = bundle.getString("branchId");
            if (branchId != null) {
                fetchRoom(branchId);
            }
        }
        edtSearch.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if( event.getAction() == MotionEvent.ACTION_UP){
                    Drawable drawableEnd = edtSearch.getCompoundDrawables()[2];
                    if( drawableEnd != null){
                        if(event.getX() >= edtSearch.getWidth() - edtSearch.getCompoundPaddingEnd()) {
                            param.setQ(edtSearch.getText().toString());
                            fetchRoomSearch();
                            return true;
                        }
                    }
                }
                return false;
            }
        });
        return view;
    }
    private void fetchRoomSearch(){
        progressBar.setVisibility(View.VISIBLE);
        imgNotFound.setVisibility(View.GONE);

        RoomService.apiService.getRoom(param.getPage(),param.getLimit(), param.getOrderBy(), param.getAsc(), param.getStatus(), param.getQ()).enqueue(new Callback<Response>() {
            @Override
            public void onResponse(Call<Response> call, retrofit2.Response<Response> response) {
                if( response.isSuccessful() ){
                    Response res = response.body();
//                    LinkedTreeMap<String, Object> data = mapper.convertValue(res.getData(), LinkedTreeMap.class);
//                    rooms = mapper.convertValue(data.get("content"), new TypeReference<List<Room>>() {});
                    rooms = mapper.convertValue(res.getData(), new TypeReference<List<Room>>() {});
                    if (branchId != null) {
                        List<Room> filteredRooms = new ArrayList<>();
                        for (Room r : rooms) {
                            if (branchId.equals(r.getBranchId())) {
                                filteredRooms.add(r);
                            }
                        }
                        rooms = filteredRooms;
                    }
                    initTable();

                    progressBar.setVisibility(View.GONE);
                    roomTable.setVisibility(View.VISIBLE);

                    if( rooms.size() == 0 ){
                        roomTable.setVisibility(View.GONE);
                        imgNotFound.setVisibility(View.VISIBLE);
                    }

                }else if( response.code() == 401 ){
                    Toast.makeText(getActivity(), "Hết phiên làm việc", Toast.LENGTH_SHORT).show();
                    shareData.clearToken();
                    Intent intent = new Intent(getActivity(), LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                }else if( response.code() == 400 ){
                    String errorJson = null;
                    try {
                        errorJson = response.errorBody().string();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    Gson gson = new Gson();
                    ErrorResponse errorResponse = gson.fromJson(errorJson, ErrorResponse.class);
                    Toast.makeText(getActivity(), errorResponse.getMessage(), Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                    roomTable.setVisibility(View.GONE);
                    imgNotFound.setVisibility(View.VISIBLE);
                }else{
                    //Toast.makeText(getActivity(), "Hệ thống xảy ra lỗi", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                    roomTable.setVisibility(View.GONE);
                    //imgNotFound.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailure(Call<Response> call, Throwable throwable) {
                Toast.makeText(getActivity(), "Hết phiên làm việc", Toast.LENGTH_SHORT).show();
                shareData.clearToken();
                Intent intent = new Intent(getActivity(), LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });
    }
    private void fetchRoom(String branchId) {
        progressBar.setVisibility(View.VISIBLE);
        imgNotFound.setVisibility(View.GONE);

        RoomService.apiService.getRoomByBranchId(branchId).enqueue(new Callback<Response>() {
            @Override
            public void onResponse(Call<Response> call, retrofit2.Response<Response> response) {
                if (response.isSuccessful()) {
                    Response res = response.body();

                    rooms = mapper.convertValue(res.getData(), new TypeReference<List<Room>>() {});

                    progressBar.setVisibility(View.GONE);

                    if (rooms == null || rooms.isEmpty()) {
                        roomTable.setVisibility(View.GONE);
                        imgNotFound.setVisibility(View.VISIBLE);
                    } else {
                        initTable();
                        roomTable.setVisibility(View.VISIBLE);
                    }
                }else if( response.code() == 401 ){
                    Toast.makeText(getActivity(), "Hết phiên làm việc", Toast.LENGTH_SHORT).show();
                    shareData.clearToken();
                    Intent intent = new Intent(getActivity(), LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                }else if( response.code() == 400 ){
                    String errorJson = null;
                    try {
                        errorJson = response.errorBody().string();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    Gson gson = new Gson();
                    ErrorResponse errorResponse = gson.fromJson(errorJson, ErrorResponse.class);
                    Toast.makeText(getActivity(), errorResponse.getMessage(), Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                    roomTable.setVisibility(View.GONE);
                    imgNotFound.setVisibility(View.VISIBLE);
                }else{
                    //Toast.makeText(getActivity(), "Hệ thống xảy ra lỗi", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                    roomTable.setVisibility(View.GONE);
                    //imgNotFound.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailure(Call<Response> call, Throwable t) {
                Toast.makeText(getActivity(), "Hết phiên làm việc", Toast.LENGTH_SHORT).show();
                shareData.clearToken();
                Intent intent = new Intent(getActivity(), LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });
    }

    private void initView(View view) {
        imgNotFound = view.findViewById(R.id.img_table);
        roomTable = view.findViewById(R.id.table_room);
        shareData = ShareData.getInstance(getActivity());
        progressBar = view.findViewById(R.id.proccesBar_table_room);
        List<String> listAsc = List.of("Tăng dần", "Giảm dần");
        ArrayAdapter<String> adapterAsc = new ArrayAdapter<>(
                getActivity(),
                android.R.layout.simple_spinner_item,
                listAsc
        );

        List<String> listOrder = List.of("Id", "Tên rạp");
        ArrayAdapter<String> adapterOrder = new ArrayAdapter<>(
                getActivity(),
                android.R.layout.simple_spinner_item,
                listOrder
        );

        edtSearch = view.findViewById(R.id.edt_search);
        btnThem = view.findViewById(R.id.btn_them);

        btnThem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ThemPhongFragment themPhongFragment = new ThemPhongFragment();
                Bundle bundle = new Bundle();
                bundle.putString("type", "ADD");
                bundle.putString("branchId", branchId);
                themPhongFragment.setArguments(bundle);
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.frame_container, themPhongFragment)
                        .addToBackStack(null)
                        .commit();
            }
        });
    }
    public void initTable () {
       // roomTable.removeAllViews();
        if (roomTable.getChildCount() > 1) {
            roomTable.removeViews(1, roomTable.getChildCount() - 1);
        }
        for( int i = 0 ; i < rooms.size() ; i++ ){
            Room room = rooms.get(i);
            Log.d("ROOMS_DATA", new Gson().toJson(room));          TableRow row = new TableRow(getContext());
            row.setBackground(getResources().getDrawable(R.drawable.bg_border_bottom));
            row.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ThemPhongFragment themPhongFragment = new ThemPhongFragment();
                    Bundle bundle = new Bundle();
                    bundle.putString("type", "UPDATE");
                    bundle.putString("branchId", branchId);
                    bundle.putSerializable("data", room);
                    themPhongFragment.setArguments(bundle);
                    getActivity().getSupportFragmentManager().beginTransaction()
                            .replace(R.id.frame_container, themPhongFragment)
                            .addToBackStack(null)
                            .commit();
                }
            });

            LinearLayout containerLayout = new LinearLayout(getContext());
            containerLayout.setOrientation(LinearLayout.VERTICAL);

            TableRow.LayoutParams containerParams = new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 2f);
            containerLayout.setLayoutParams(containerParams);

            TextView tvName = new TextView(getContext());
            tvName.setTextSize(14);
            tvName.setText(room.getName());
            tvName.setPadding(8, 8, 8, 8);
            tvName.setLayoutParams(new TableRow.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 2));
            row.addView(tvName);

            TextView tvStatus = new TextView(getContext());
            tvStatus.setTextSize(14);
            tvStatus.setText(room.getStatus().toString().equals("ACTIVE")? "Hoạt động" : room.getStatus().toString().equals("DELETE") ? "Ngưng hoạt động" : "Không xác định");
            if( room.getStatus().toString().equals("ACTIVE") ){
                tvStatus.setTextColor(Color.GREEN);
            } else if( room.getStatus().toString().equals("DELETE") ){
                tvStatus.setTextColor(Color.RED);
            }
            else{
                tvStatus.setTextColor(Color.RED);
            }
            tvStatus.setPadding(8, 8, 8, 8);
            tvStatus.setLayoutParams(new TableRow.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1));
            row.addView(tvStatus);

            roomTable.addView(row);
        }
    }
}