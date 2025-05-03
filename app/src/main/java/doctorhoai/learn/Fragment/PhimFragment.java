package doctorhoai.learn.Fragment;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.MotionEvent;
import android.widget.*;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.bumptech.glide.Glide;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;
import doctorhoai.learn.Activity.LoginActivity;
import doctorhoai.learn.Api.FilmService;
import doctorhoai.learn.Model.*;
import doctorhoai.learn.R;
import doctorhoai.learn.Utils.ShareData;
import retrofit2.Call;
import retrofit2.Callback;

import java.io.IOException;
import java.util.List;

public class PhimFragment extends Fragment {

    private Param param = new Param(0,"asc", 10, "name", "", "none");
    private ProgressBar progressBar;
    private ImageView imgNotFound, imgPrev, imgNext;
    private TableLayout filmTable;
    private ShareData shareData;
    private ObjectMapper mapper = new ObjectMapper();
    private List<Film> films;
    private TextView tvCounter;
    private Integer totalPage = 1;
    private Spinner spinnerAsc, spinnerOrderBy, sprinnerStatus;
    private Button btnThem;
    private EditText edtSearch;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_phim, container, false);

        initView(view);

        fetchFilmByCustom();

        spinnerAsc.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    param.setAsc("asc");
                }else if (position == 1) {
                    param.setAsc("desc");
                }
                fetchFilmByCustom();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spinnerOrderBy.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if( position == 0) {
                    param.setOrderBy("id");
                }else if (position == 1) {
                    param.setOrderBy("name");
                }
                fetchFilmByCustom();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        sprinnerStatus.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if( position == 0 ){
                    param.setStatus("none");
                }else if( position == 1){
                    param.setStatus("ACTIVE");
                }else if( position == 2){
                    param.setStatus("DELETE");
                }else if( position == 3){
                    param.setStatus("COMMING_SOON");
                }
                fetchFilmByCustom();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        imgNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if( param.getPage() < totalPage -1 ){
                    param.setPage(param.getPage()+1);
                    fetchFilmByCustom();
                }
            }
        });
        imgPrev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if( param.getPage() > 0){
                    param.setPage(param.getPage()-1);
                    fetchFilmByCustom();
                }
            }
        });

        edtSearch.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if( event.getAction() == MotionEvent.ACTION_UP){
                    Drawable drawableEnd = edtSearch.getCompoundDrawables()[2];
                    if( drawableEnd != null){
                        if(event.getX() >= edtSearch.getWidth() - edtSearch.getCompoundPaddingEnd()) {
                            param.setQ(edtSearch.getText().toString());
                            fetchFilmByCustom();
                            return true;
                        }
                    }
                }
                return false;
            }
        });

        return view;
    }

    public void initView (View view ) {
        progressBar = view.findViewById(R.id.proccesBar_table_film);
        imgNotFound = view.findViewById(R.id.img_table);
        imgPrev = view.findViewById(R.id.img_prev);
        imgNext = view.findViewById(R.id.img_next);
        filmTable = view.findViewById(R.id.table_film);
        shareData = ShareData.getInstance(getActivity());
        tvCounter = view.findViewById(R.id.tv_counter);
        spinnerAsc = view.findViewById(R.id.spinner_asc);
        List<String> listAsc = List.of("Tăng dần", "Giảm dần");
        ArrayAdapter<String> adapterAsc = new ArrayAdapter<>(
                getActivity(),
                android.R.layout.simple_spinner_item,
                listAsc
        );
        spinnerAsc.setAdapter(adapterAsc);
        spinnerOrderBy = view.findViewById(R.id.spinner_orderBy);
        List<String> listOrder = List.of("Id", "Tên phim");
        ArrayAdapter<String> adapterOrder = new ArrayAdapter<>(
                getActivity(),
                android.R.layout.simple_spinner_item,
                listOrder
        );
        spinnerOrderBy.setAdapter(adapterOrder);
        edtSearch = view.findViewById(R.id.edt_search);
        btnThem = view.findViewById(R.id.btn_them);
        sprinnerStatus = view.findViewById(R.id.spinner_status);
        List<String> listStatus = List.of("Tất cả","Hoạt động", "Ngưng chiếu", "Sắp ra mắt");
        ArrayAdapter<String> adapterStatus = new ArrayAdapter<>(
                getActivity(),
                android.R.layout.simple_spinner_item,
                listStatus
        );
        sprinnerStatus.setAdapter(adapterStatus);
        //action add film
        btnThem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ThemPhimFragment themPhimFragment = new ThemPhimFragment();
                Bundle bundle = new Bundle();
                bundle.putString("type", "ADD");
                themPhimFragment.setArguments(bundle);
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.frame_container, themPhimFragment)
                        .addToBackStack(null)
                        .commit();
            }
        });
    }

    public void fetchFilmByCustom () {

        //loading
        progressBar.setVisibility(View.VISIBLE);
        imgNotFound.setVisibility(View.GONE);

        FilmService.apiService.getFilm(param.getPage(),param.getLimit(), param.getOrderBy(), param.getAsc(), param.getStatus(), param.getQ()).enqueue(new Callback<Response>() {
            @Override
            public void onResponse(Call<Response> call, retrofit2.Response<Response> response) {
                if( response.isSuccessful() ){
                    Response res = response.body();
                    LinkedTreeMap<String, Object> data = mapper.convertValue(res.getData(), LinkedTreeMap.class);
                    films = mapper.convertValue(data.get("content"), new TypeReference<List<Film>>() {});
                    totalPage = mapper.convertValue(data.get("totalPages"), new TypeReference<Integer>() {});
                    initTable();

                    progressBar.setVisibility(View.GONE);
                    filmTable.setVisibility(View.VISIBLE);

                    if( films.size() == 0 ){
                        filmTable.setVisibility(View.GONE);
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
                    filmTable.setVisibility(View.GONE);
                    imgNotFound.setVisibility(View.VISIBLE);
                }else{
                    Toast.makeText(getActivity(), "Hệ thống xảy ra lỗi", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                    filmTable.setVisibility(View.GONE);
                    imgNotFound.setVisibility(View.VISIBLE);
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

    public void initTable () {
        tvCounter.setText( (param.getPage() + 1) + "/" + totalPage);
        if( param.getPage() + 1 == totalPage ){
            imgNext.setVisibility(View.GONE);
        }else{
            imgNext.setVisibility(View.VISIBLE);
        }
        if( param.getPage() == 0){
            imgPrev.setVisibility(View.GONE);
        }else{
            imgPrev.setVisibility(View.VISIBLE);
        }
        int childCount = filmTable.getChildCount();
        if (childCount > 1) {
            filmTable.removeViews(1, childCount - 1);
        }
        for( int i = 0 ; i < films.size() ; i++ ){
            Film film = films.get(i);
            TableRow row = new TableRow(getContext());
            row.setBackground(getResources().getDrawable(R.drawable.bg_border_bottom));
            row.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Bundle bundle = new Bundle();
                    ThemPhimFragment themPhimFragment = new ThemPhimFragment();
                    bundle.putString("type", "UPDATE");
                    bundle.putSerializable("data", film);
                    themPhimFragment.setArguments(bundle);
                    getActivity().getSupportFragmentManager().beginTransaction()
                            .replace(R.id.frame_container, themPhimFragment)
                            .addToBackStack(null)
                            .commit();
                }
            });

            LinearLayout containerLayout = new LinearLayout(getContext());
            containerLayout.setOrientation(LinearLayout.VERTICAL);

            // 2. Set LayoutParams cho containerLayout (weight = 2)
            TableRow.LayoutParams containerParams = new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 2f);
            containerLayout.setLayoutParams(containerParams);

            ImageView imageView = new ImageView(getContext());
            Glide.with(getActivity())
                            .load(film.getImage())
                                    .into(imageView);
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);

            float density = getResources().getDisplayMetrics().density;

            // Chiều rộng = 100dp, Chiều cao = 50dp (tạo hình chữ nhật)
            int widthInPx = (int) (100 * density);
            int heightInPx = (int) (50 * density);

            TableRow.LayoutParams layoutParams = new TableRow.LayoutParams(widthInPx, heightInPx);
            imageView.setLayoutParams(layoutParams);
            containerLayout.addView(imageView);
            row.addView(containerLayout);

            TextView tvName = new TextView(getContext());
            tvName.setTextSize(14);
            tvName.setText(film.getName());
            tvName.setPadding(8, 8, 8, 8);
            tvName.setLayoutParams(new TableRow.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 2));
            row.addView(tvName);

            TextView tvStatus = new TextView(getContext());
            tvStatus.setTextSize(14);
            tvStatus.setText(film.getStatus().toString().equals("ACTIVE")? "Hoạt động" : film.getStatus().toString().equals("DELETE") ? "Ngưng chiếu" : "Sắp ra mắt");
            if( film.getStatus().toString().equals("ACTIVE") ){
                tvStatus.setTextColor(Color.GREEN);
            } else if( film.getStatus().toString().equals("COMMING_SOON") ){
                tvStatus.setTextColor(Color.BLUE);
            }
            else{
                tvStatus.setTextColor(Color.RED);
            }
            tvStatus.setPadding(8, 8, 8, 8);
            tvStatus.setLayoutParams(new TableRow.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1));
            row.addView(tvStatus);

            filmTable.addView(row);
        }
    }
}