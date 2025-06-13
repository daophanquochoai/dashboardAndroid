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
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import doctorhoai.learn.Activity.LoginActivity;
import doctorhoai.learn.Api.FilmService;
import doctorhoai.learn.Model.*;
import doctorhoai.learn.R;
import doctorhoai.learn.Utils.ShareData;
import retrofit2.Call;
import retrofit2.Callback;

import java.io.IOException;
import java.util.List;

public class TypeFilmFragment extends Fragment {

    private Param param = new Param(0,"asc", 10, "name", "", "none");
    private ProgressBar progressBar;
    private ImageView imgNotFound, imgPrev, imgNext;
    private TableLayout typeFilmTable;
    private ShareData shareData;
    private ObjectMapper mapper = new ObjectMapper();
    private List<TypeFilm> typeFilms;
    private TextView tvCounter;
    private Integer totalPage = 1;
    private Spinner spinnerAsc, spinnerOrderBy;
    private Button btnThem;
    private EditText edtSearch;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_type_film, container, false);

        initView(view);
        fetchTypeFilm();

        imgPrev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if( param.getPage() > 0 ){
                    param.setPage(param.getPage() - 1);
                }
                fetchTypeFilm();
            }
        });
        imgNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if( param.getPage() + 1< totalPage ){
                    param.setPage(param.getPage() + 1);
                }
                fetchTypeFilm();
            }
        });

        spinnerAsc.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if( position == 0) {
                    param.setAsc("asc");
                }else if( position == 1) {
                    param.setAsc("desc");
                }
                fetchTypeFilm();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        spinnerOrderBy.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if( position == 0 ){
                    param.setOrderBy("id");
                }else if( position == 1) {
                    param.setOrderBy("name");
                }
                fetchTypeFilm();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

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
                            fetchTypeFilm();
                            return true;
                        }
                    }
                }
                return false;
            }
        });

        btnThem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddTypeFilmFragment addTypeFilmFragment = new AddTypeFilmFragment();
                Bundle bundle = new Bundle();
                bundle.putString("type", "ADD");
                addTypeFilmFragment.setArguments(bundle);
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.frame_container, addTypeFilmFragment)
                        .addToBackStack(null)
                        .commit();
            }
        });


        return view;
    }

    public void initView ( View view) {
        progressBar = view.findViewById(R.id.proccesBar_table_typeFilm);
        imgNotFound = view.findViewById(R.id.img_table);
        typeFilmTable = view.findViewById(R.id.table_typeFilm);
        typeFilmTable.setVisibility(View.GONE);
        shareData = ShareData.getInstance(getActivity());
        imgNotFound = view.findViewById(R.id.img_table);
        imgPrev = view.findViewById(R.id.img_prev);
        imgNext = view.findViewById(R.id.img_next);
        tvCounter = view.findViewById(R.id.tv_counter);
        spinnerAsc = view.findViewById(R.id.spinner_asc);
        spinnerOrderBy = view.findViewById(R.id.spinner_orderBy);
        List<String> listAsc = List.of("Tăng dần", "Giảm dần");
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                getActivity(),
                android.R.layout.simple_spinner_item,
                listAsc
        );
        List<String> listOrder = List.of("Id", "Tên");
        ArrayAdapter<String> adapterOrder = new ArrayAdapter<>(
                getActivity(),
                android.R.layout.simple_spinner_item,
                listOrder
        );
        spinnerAsc.setAdapter(adapter);
        spinnerOrderBy.setAdapter(adapterOrder);
        btnThem = view.findViewById(R.id.btn_them);
        edtSearch = view.findViewById(R.id.edt_search);
    }

    public void fetchTypeFilm () {
        progressBar.setVisibility(View.VISIBLE);
        imgNotFound.setVisibility(View.GONE);
        FilmService.apiService.getTypeFilm(param.getPage(), param.getLimit(), param.getOrderBy(), param.getAsc(), param.getStatus(), param.getQ()).enqueue(new Callback<Response>() {
            @Override
            public void onResponse(Call<Response> call, retrofit2.Response<Response> response) {
                if( response.isSuccessful() ){
                    Response res = response.body();
                    ResponsePage responsePage = mapper.convertValue(res.getData(), new TypeReference<ResponsePage>() {});
                    typeFilms = mapper.convertValue(responsePage.getData(), new TypeReference<List<TypeFilm>>() {});
                    totalPage = responsePage.getTotalPage();
                    initTable();

                    progressBar.setVisibility(View.GONE);
                    typeFilmTable.setVisibility(View.VISIBLE);

                    if( typeFilms.size() == 0 ){
                        imgNotFound.setVisibility(View.VISIBLE);
                        typeFilmTable.setVisibility(View.GONE);
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
                    typeFilmTable.setVisibility(View.GONE);
                    imgNotFound.setVisibility(View.VISIBLE);
                }else{
                    Toast.makeText(getActivity(), "Hệ thống xảy ra lỗi", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                    typeFilmTable.setVisibility(View.GONE);
                    imgNotFound.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailure(Call<Response> call, Throwable throwable) {
                Toast.makeText(getActivity(), "Hệ thống xảy ra lỗi", Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE);
                typeFilmTable.setVisibility(View.GONE);
                imgNotFound.setVisibility(View.VISIBLE);
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
        int childCount = typeFilmTable.getChildCount();
        if (childCount > 1) {
            typeFilmTable.removeViews(1, childCount - 1);
        }
        for( int i = 0 ; i < typeFilms.size() ; i++ ){
            TypeFilm typeFilm = typeFilms.get(i);
            TableRow row = new TableRow(getContext());
            row.setBackground(getResources().getDrawable(R.drawable.bg_border_bottom));
            row.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AddTypeFilmFragment addTypeFilmFragment = new AddTypeFilmFragment();
                    Bundle bundle = new Bundle();
                    bundle.putString("type", "UPDATE");
                    bundle.putSerializable("data", typeFilm);
                    addTypeFilmFragment.setArguments(bundle);
                    getActivity().getSupportFragmentManager().beginTransaction()
                            .replace(R.id.frame_container, addTypeFilmFragment)
                            .addToBackStack(null)
                            .commit();
                }
            });
            TextView tvId = new TextView(getContext());
            tvId.setTextSize(14);
            tvId.setText(typeFilm.getId());
            tvId.setPadding(8, 8, 8, 8);
            tvId.setLayoutParams(new TableRow.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 2));
            row.addView(tvId);

            TextView tvName = new TextView(getContext());
            tvName.setTextSize(14);
            tvName.setText(typeFilm.getName());
            tvName.setPadding(8, 8, 8, 8);
            tvName.setLayoutParams(new TableRow.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1));
            row.addView(tvName);

            TextView tvStatus = new TextView(getContext());
            tvStatus.setTextSize(14);
            tvStatus.setText(typeFilm.getActive().toString().equals("ACTIVE")? "Hoạt động" : "N.hoạt động");
            if( typeFilm.getActive().toString().equals("ACTIVE") ){
                tvStatus.setTextColor(Color.GREEN);
            }else{
                tvStatus.setTextColor(Color.RED);
            }
            tvStatus.setPadding(8, 8, 8, 8);
            tvStatus.setLayoutParams(new TableRow.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1));
            row.addView(tvStatus);

            typeFilmTable.addView(row);
        }
    }
}