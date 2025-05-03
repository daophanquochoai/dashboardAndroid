package doctorhoai.learn.Fragment;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Html;
import android.widget.*;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.bumptech.glide.Glide;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.gson.Gson;
import doctorhoai.learn.Activity.LoadingDialog;
import doctorhoai.learn.Activity.LoginActivity;
import doctorhoai.learn.Api.FilmService;
import doctorhoai.learn.Api.ImageService;
import doctorhoai.learn.Model.*;
import doctorhoai.learn.R;
import doctorhoai.learn.Utils.ShareData;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;

import java.io.*;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static android.app.Activity.RESULT_OK;
import static android.app.ProgressDialog.show;

public class ThemPhimFragment extends Fragment {

    private TextView tvTitle, tv_error_name, tv_error_type, tv_error_nation, tv_error_duration, tv_error_description, tv_error_content, tv_error_trailer;
    private EditText edt_name, edt_type, edt_sub, edt_nation, edt_duration, edt_description, edt_content, edt_trailer;
    private ShareData shareData;
    private String token;
    private LoadingDialog loadingDialog;
    private List<Sub> subs;
    private List<TypeFilm> typeFilms;
    private boolean[] booleanSub;
    private boolean[] booleanTypeFilm;
    private ObjectMapper mapper = new ObjectMapper();
    private Button btnUpload;
    private ImageView imgUpload;
    private Spinner spinnerStatus, spinnerAge;
    private Button btnCancel, btnCreate;
    private static final int PICK_IMAGE_REQUEST = 1;
    private Film film = new Film("","",99,"", new ArrayList<>(), "","","","","", new ArrayList<>(), "ACTIVE");

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_them_phim, container, false);

        initView(view);

        loadingDialog.startLoadingDialog();
        CompletableFuture<Void> subAsync =  fetchSub();
        CompletableFuture<Void> typeAsync = fetchTypeFilm();

        CompletableFuture.allOf(subAsync,typeAsync)
                .thenRun(() -> {
                    Bundle bundle = getArguments();
                    if( bundle == null){
                        Objects.requireNonNull(getActivity()).getSupportFragmentManager().popBackStack();
                    }else {
                        String type = bundle.getString("type");
                        if( type.equals("ADD")){
                            tvTitle.setText("THÊM PHIM");
                            btnCreate.setText("Tạo");
                        }else {
                            tvTitle.setText("CHỈNH SỬA PHIM");
                            btnCreate.setText("Cập nhật");
                            film = (Film) bundle.getSerializable("data");
                            edt_name.setText(film.getName());
                            edt_nation.setText(film.getNation());
                            edt_duration.setText(film.getDuration());
                            edt_trailer.setText(film.getTrailer());
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                edt_description.setText(Html.fromHtml(film.getDescription(), Html.FROM_HTML_MODE_LEGACY));
                                edt_content.setText(Html.fromHtml(film.getContent(), Html.FROM_HTML_MODE_LEGACY));
                            } else {
                                edt_content.setText(Html.fromHtml(film.getContent()));
                                edt_description.setText(Html.fromHtml(film.getDescription()));
                            }
                            if( film.getAge() == 13 ){
                                spinnerAge.setSelection(2);
                            }else if( film.getAge() == 18 ){
                                spinnerAge.setSelection(1);
                            }else{
                                spinnerAge.setSelection(0);
                            }
                            if( film.getStatus().toString().equals("ACTIVE")){
                                spinnerStatus.setSelection(0);
                            }else if( film.getStatus().toString().equals("COMMING_SOON")){
                                spinnerStatus.setSelection(1);
                            }else{
                                spinnerStatus.setSelection(2);
                            }
                            if(  film.getTypeFilms() != null && typeFilms != null ){
                                StringBuilder selectTypeFilm = new StringBuilder();
                                for( int i = 0 ; i < typeFilms.size() && film.getTypeFilms().size() > 0 ; i++ ){
                                    int finalI = i;
                                    Optional<TypeFilm> typeFilmOptional = film.getTypeFilms().stream().filter(item -> item.getId().equals(typeFilms.get(finalI).getId())).findAny();
                                    if( typeFilmOptional.isPresent()){
                                        booleanTypeFilm[i] = true;
                                        selectTypeFilm.append(typeFilms.get(i).getName()).append(", ");

                                    }
                                }
                                if (selectTypeFilm.length() > 0) {
                                    selectTypeFilm.setLength(selectTypeFilm.length() - 2); // Xóa dấu ", " cuối
                                }
                                edt_type.setText(selectTypeFilm.toString());
                            }
                            if( film.getImage() != null ){
                                Glide.with(getActivity())
                                        .load(film.getImage())
                                        .into(imgUpload);
                                imgUpload.setVisibility(View.VISIBLE);
                            }
                            if( film.getSub() != null && subs != null ){
                                StringBuilder selectedSubs = new StringBuilder();
                                for( int i = 0 ; i < subs.size() && film.getSub().size() > 0 ; i++ ){
                                    int finalI = i;
                                    Optional<Sub> subOptional = film.getSub().stream().filter(item -> item.getId().equals(subs.get(finalI).getId())).findAny();
                                    if( subOptional.isPresent()){
                                        booleanSub[i] = true;
                                        selectedSubs.append(subs.get(i).getName()).append(", ");
                                    }
                                }
                                if (selectedSubs.length() > 0) {
                                    selectedSubs.setLength(selectedSubs.length() - 2); // Xóa dấu ", " cuối
                                }
                                edt_sub.setText(selectedSubs.toString());
                            }
                        }
                    }
                    loadingDialog.dismissDialog();
                })
                .exceptionally(ex-> {
                    Toast.makeText(getActivity(), ex.getMessage(), Toast.LENGTH_SHORT).show();
                    return null;
                })
        ;

        initError();

        chooseSubFilm();
        chooseTypeFilm();

        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, PICK_IMAGE_REQUEST);
            }
        });

        spinnerStatus.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if( position == 0 ){
                    film.setStatus("ACTIVE");
                }else if( position == 1 ){
                    film.setStatus("COMMING_SOON");
                }else if( position == 2 ){
                    film.setStatus("DELETE");
                }else {
                    Toast.makeText(getActivity(), "Chưa hỗ trợ", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spinnerAge.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if( position == 0 ){
                    film.setAge(99);
                }else if( position == 1 ){
                    film.setAge(18);
                }else if( position == 2 ){
                    film.setAge(13);
                }else{
                    Toast.makeText(getActivity(), "Chưa được hỗ trợ", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        return view;
    }

    public void initView ( View view ){
        tvTitle = view.findViewById(R.id.tvTitle);
        tv_error_name = view.findViewById(R.id.txt_error_name);
        tv_error_type = view.findViewById(R.id.txt_error_type);
        tv_error_nation = view.findViewById(R.id.txt_error_nation);
        tv_error_duration = view.findViewById(R.id.txt_error_duration);
        tv_error_description = view.findViewById(R.id.txt_error_description);
        tv_error_content = view.findViewById(R.id.txt_error_content);
        tv_error_trailer = view.findViewById(R.id.txt_error_trailer);

        edt_name = view.findViewById(R.id.edt_name_film);
        edt_type = view.findViewById(R.id.edt_type_film);
        edt_type.setKeyListener(null);
        edt_sub = view.findViewById(R.id.edt_sub_film);
        edt_sub.setKeyListener(null);
        edt_nation = view.findViewById(R.id.edt_nation);
        edt_duration = view.findViewById(R.id.edt_duration);
        edt_description = view.findViewById(R.id.edt_description);
        edt_content = view.findViewById(R.id.edt_content);
        edt_trailer = view.findViewById(R.id.edt_trailer);

        shareData = ShareData.getInstance(getActivity());
        token = shareData.getToken();
        loadingDialog = new LoadingDialog(getActivity());

        btnUpload = view.findViewById(R.id.btn_upload);
        imgUpload = view.findViewById(R.id.img_upload);

        btnCancel = view.findViewById(R.id.btn_cancel);
        btnCreate = view.findViewById(R.id.btn_save);

        spinnerStatus = view.findViewById(R.id.spinner_status_film);
        spinnerAge = view.findViewById(R.id.spinner_age);
        List<String> listStatus = List.of("Đang chiếu", "Sắp ra mắt", "Ngừng chiếu");
        ArrayAdapter<String> adapterStatus = new ArrayAdapter<>(
                getActivity(),
                android.R.layout.simple_spinner_item,
                listStatus
        );
        adapterStatus.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerStatus.setAdapter(adapterStatus);
        List<String> listAge = List.of("Dành cho mọi lứa tuổi", "Trên 18 tuổi", "Trên 13 tuổi");
        ArrayAdapter<String> adapterAge = new ArrayAdapter<>(
                getActivity(),
                android.R.layout.simple_spinner_item,
                listAge
        );
        adapterAge.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerAge.setAdapter(adapterAge);

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Objects.requireNonNull(getActivity()).getSupportFragmentManager().popBackStack();
            }
        });

        btnCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initError();
                if( edt_name.getText().toString().equals("")){
                    tv_error_name.setText("Tên phim không thể trống");
                    tv_error_name.setVisibility(View.VISIBLE);
                    return;
                }
                if( edt_nation.getText().toString().equals("")){
                    tv_error_nation.setText("Quốc giá khng thể trống");
                    tv_error_nation.setVisibility(View.VISIBLE);
                    return;
                }
                if( edt_duration.getText().toString().equals("")){
                    tv_error_duration.setText("Thời lượng không thể trống");
                    tv_error_duration.setVisibility(View.VISIBLE);
                    return;
                }
                if( edt_description.getText().toString().equals("")){
                    tv_error_description.setText("Mô tả không thể trống");
                    tv_error_description.setVisibility(View.VISIBLE);
                    return;
                }
                if( edt_content.getText().toString().equals("")){
                    tv_error_content.setText("Nội dung không thể trống");
                    tv_error_content.setVisibility(View.VISIBLE);
                    return;
                }
                if( edt_trailer.getText().toString().equals("")){
                    tv_error_trailer.setText("Trailer không thể trống");
                    tv_error_trailer.setVisibility(View.VISIBLE);
                    return;
                }
                film.setName(edt_name.getText().toString());
                film.setNation(edt_nation.getText().toString());
                film.setDuration(edt_duration.getText().toString());
                film.setDescription(edt_description.getText().toString());
                film.setContent(edt_content.getText().toString());
                film.setTrailer(edt_trailer.getText().toString());
                Bundle bundle = getArguments();
                String type = bundle.getString("type");
                if( type.equals("ADD")){
                    createFilm();
                }else{
                    updateFilm();
                }
            }
        });
    }

    public void updateFilm(){
        loadingDialog.startLoadingDialog();
        FilmService.apiService.updateFilm("Bearer " + token, film.getId(),film).enqueue(new Callback<Response>() {
            @Override
            public void onResponse(Call<Response> call, retrofit2.Response<Response> response) {
                if( response.isSuccessful() ){
                    Toast.makeText(getActivity(), "Cập nhật phim thành công", Toast.LENGTH_SHORT).show();
                    Objects.requireNonNull(getActivity()).getSupportFragmentManager().popBackStack();
                }
                else if( response.code() == 401 ){
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
                }else {
                    Toast.makeText(getActivity(), "Hệ thống lỗi", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Response> call, Throwable throwable) {
                Toast.makeText(getActivity(), "Hệ thống lỗi", Toast.LENGTH_SHORT).show();
            }
        });
        loadingDialog.dismissDialog();
    }

    public void createFilm() {
        loadingDialog.startLoadingDialog();
        FilmService.apiService.addFilm("Bearer " + token,film).enqueue(new Callback<Response>() {
            @Override
            public void onResponse(Call<Response> call, retrofit2.Response<Response> response) {
                if( response.isSuccessful() ){
                    Toast.makeText(getActivity(), "Thêm phim thành công", Toast.LENGTH_SHORT).show();
                    Objects.requireNonNull(getActivity()).getSupportFragmentManager().popBackStack();
                }
                else if( response.code() == 401 ){
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
                }else {
                    Toast.makeText(getActivity(), "Hệ thống lỗi", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Response> call, Throwable throwable) {
                Toast.makeText(getActivity(), "Hệ thống lỗi", Toast.LENGTH_SHORT).show();
            }
        });
        loadingDialog.dismissDialog();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        loadingDialog.startLoadingDialog();
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            try{
                Uri imageUri = data.getData();
                InputStream inputStream = getActivity().getContentResolver().openInputStream(imageUri);
                byte[] bytes = getBytes(inputStream);

                RequestBody requestFile = RequestBody.create(MediaType.parse("image/*"), bytes);
                MultipartBody.Part body = MultipartBody.Part.createFormData("image", "upload.jpg", requestFile);

                ImageService.apiService.uploadImage(body).enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(Call<String> call, retrofit2.Response<String> response) {
                        if( response.isSuccessful()){
                            Glide.with(getActivity())
                                    .load(response.body())
                                    .into(imgUpload);
                            film.setImage(response.body());
                            imgUpload.setVisibility(View.VISIBLE);
                        }else if( response.code() == 401 ){
                            Toast.makeText(getActivity(), "Hết phiên làm việc", Toast.LENGTH_SHORT).show();
                            shareData.clearToken();
                            Intent intent = new Intent(getActivity(), LoginActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                        }else{
                            Toast.makeText(getActivity(), "Hệ thống lỗi", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<String> call, Throwable throwable) {
                        Toast.makeText(getActivity(), "Hệ thống lỗi", Toast.LENGTH_SHORT).show();
                    }
                });
            } catch (FileNotFoundException e) {
                Toast.makeText(getActivity(), "Hệ thống lỗi", Toast.LENGTH_SHORT).show();
            } catch (IOException e) {
                Toast.makeText(getActivity(), "Hệ thống lỗi", Toast.LENGTH_SHORT).show();
            }
        }
        loadingDialog.dismissDialog();
    }

    private byte[] getBytes(InputStream inputStream) throws IOException {
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
        int bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];

        int len;
        while ((len = inputStream.read(buffer)) != -1) {
            byteBuffer.write(buffer, 0, len);
        }
        return byteBuffer.toByteArray();
    }

    public void initError(){
        tv_error_name.setVisibility(View.GONE);
        tv_error_type.setVisibility(View.GONE);
        tv_error_nation.setVisibility(View.GONE);
        tv_error_duration.setVisibility(View.GONE);
        tv_error_description.setVisibility(View.GONE);
        tv_error_content.setVisibility(View.GONE);
        tv_error_trailer.setVisibility(View.GONE);
    }

    public void chooseSubFilm () {
        edt_sub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new MaterialAlertDialogBuilder(getActivity(), R.style.CustomDialogTheme)
                        .setMultiChoiceItems(
                                subs.stream().map(Sub::getName).toArray(CharSequence[]::new),
                                booleanSub,
                                new DialogInterface.OnMultiChoiceClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                                        // Khi chọn hoặc bỏ chọn một item
                                        booleanSub[which] = isChecked;
                                        List<Sub> subTemp = film.getSub();
                                        if( isChecked ){
                                            subTemp.add(subs.get(which));
                                        }else{
                                            Sub temp = subs.get(which);
                                            subTemp = subTemp.stream().filter(item -> !item.getId().equals(temp.getId())).collect(Collectors.toList());
                                        }
                                        film.setSub(subTemp);
                                    }
                                }
                        )
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // Khi bấm OK: tổng hợp lại các mục đã chọn
                                StringBuilder selectedSubs = new StringBuilder();
                                for (int i = 0; i < subs.size(); i++) {
                                    if (booleanSub[i]) {
                                        selectedSubs.append(subs.get(i).getName()).append(", ");
                                    }
                                }
                                if (selectedSubs.length() > 0) {
                                    selectedSubs.setLength(selectedSubs.length() - 2); // Xóa dấu ", " cuối
                                }
                                edt_sub.setText(selectedSubs.toString());
                            }
                        })
                        .setNegativeButton("Cancel", null)
                        .show();
            }
        });
    }

    public CompletableFuture<Void> fetchSub(){
        CompletableFuture<Void> future = new CompletableFuture<>();
        FilmService.apiService.getAllSub().enqueue(new Callback<Response>() {
            @Override
            public void onResponse(Call<Response> call, retrofit2.Response<Response> response) {
                if( response.isSuccessful() ){
                    Response res = response.body();
                    subs = mapper.convertValue(res.getData(), new TypeReference<List<Sub>>() {});
                    booleanSub = new boolean[subs.size()];
                    Arrays.fill(booleanSub,false);
                }else{
                    if( response.raw().code() == 401 ){
                        Toast.makeText(getActivity(), "Hết phiên làm việc", Toast.LENGTH_SHORT).show();
                        shareData.clearToken();
                        Intent intent = new Intent(getActivity(), LoginActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    } else if ( response.code() == 400){
                        String errorJson = null;
                        try {
                            errorJson = response.errorBody().string();
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                        Gson gson = new Gson();
                        ErrorResponse errorResponse = gson.fromJson(errorJson, ErrorResponse.class);
                        Toast.makeText(getActivity(), errorResponse.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                    else{
                        Toast.makeText(getActivity(), "Hệ thống xảy ra lỗi", Toast.LENGTH_SHORT).show();
                    }
                }
                future.complete(null);
            }

            @Override
            public void onFailure(Call<Response> call, Throwable throwable) {
                Toast.makeText(getActivity(), "Hết phiên làm việc", Toast.LENGTH_SHORT).show();
                shareData.clearToken();
                Intent intent = new Intent(getActivity(), LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                future.complete(null);
            }
        });
        return future;
    }

    public CompletableFuture<Void> fetchTypeFilm () {
        CompletableFuture<Void> future = new CompletableFuture<>();
        FilmService.apiService.getAllTypeFilm().enqueue(new Callback<Response>() {
            @Override
            public void onResponse(Call<Response> call, retrofit2.Response<Response> response) {
                if( response.isSuccessful() ){
                    Response res = response.body();
                    typeFilms = mapper.convertValue(res.getData(), new TypeReference<List<TypeFilm>>() {});
                    booleanTypeFilm = new boolean[typeFilms.size()];
                    Arrays.fill(booleanTypeFilm,false);
                }else{
                    if( response.raw().code() == 401 ){
                        Toast.makeText(getActivity(), "Hết phiên làm việc", Toast.LENGTH_SHORT).show();
                        shareData.clearToken();
                        Intent intent = new Intent(getActivity(), LoginActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    } else if ( response.code() == 400){
                        String errorJson = null;
                        try {
                            errorJson = response.errorBody().string();
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                        Gson gson = new Gson();
                        ErrorResponse errorResponse = gson.fromJson(errorJson, ErrorResponse.class);
                        Toast.makeText(getActivity(), errorResponse.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                    else{
                        Toast.makeText(getActivity(), "Hệ thống xảy ra lỗi", Toast.LENGTH_SHORT).show();
                    }
                }
                future.complete(null);
            }

            @Override
            public void onFailure(Call<Response> call, Throwable throwable) {
                Toast.makeText(getActivity(), "Hết phiên làm việc", Toast.LENGTH_SHORT).show();
                shareData.clearToken();
                Intent intent = new Intent(getActivity(), LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                future.complete(null);
                startActivity(intent);
            }
        });
        return future;
    }

    public void chooseTypeFilm () {
        edt_type.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new MaterialAlertDialogBuilder(getActivity(), R.style.CustomDialogTheme)
                        .setMultiChoiceItems(
                                typeFilms.stream().map(TypeFilm::getName).toArray(CharSequence[]::new),
                                booleanTypeFilm,
                                new DialogInterface.OnMultiChoiceClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                                        // Khi chọn hoặc bỏ chọn một item
                                        booleanTypeFilm[which] = isChecked;
                                        List<TypeFilm> typeTemp = film.getTypeFilms();
                                        if( isChecked ){
                                            typeTemp.add(typeFilms.get(which));
                                        }else{
                                            TypeFilm temp = typeFilms.get(which);
                                            typeTemp = typeTemp.stream().filter( item -> !item.getId().equals(temp.getId())).collect(Collectors.toList());
                                        }
                                        film.setTypeFilms(typeTemp);
                                    }
                                }
                        )
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // Khi bấm OK: tổng hợp lại các mục đã chọn
                                StringBuilder selectTypeFilm = new StringBuilder();
                                for (int i = 0; i < typeFilms.size(); i++) {
                                    if (booleanTypeFilm[i]) {
                                        selectTypeFilm.append(typeFilms.get(i).getName()).append(", ");
                                    }
                                }
                                if (selectTypeFilm.length() > 0) {
                                    selectTypeFilm.setLength(selectTypeFilm.length() - 2); // Xóa dấu ", " cuối
                                }
                                edt_type.setText(selectTypeFilm.toString());
                            }
                        })
                        .setNegativeButton("Cancel", null)
                        .show();
            }
        });
    }
}