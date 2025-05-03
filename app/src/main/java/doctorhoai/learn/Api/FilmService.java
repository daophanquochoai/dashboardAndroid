package doctorhoai.learn.Api;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import doctorhoai.learn.Model.Film;
import doctorhoai.learn.Model.Response;
import doctorhoai.learn.Model.TypeFilm;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;
import retrofit2.http.*;

import java.util.concurrent.TimeUnit;

public interface FilmService {

    OkHttpClient okHttpClient = new OkHttpClient.Builder()
            .connectTimeout(10, TimeUnit.SECONDS)
            .readTimeout(10, TimeUnit.SECONDS)
            .writeTimeout(10, TimeUnit.SECONDS)
            .build();
    Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").setLenient().create();
    FilmService apiService  = new Retrofit.Builder()
            .baseUrl("http://10.0.2.2:6380/doctorhoai/proxy/")
            .client(okHttpClient)
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build().create(FilmService.class);

    @GET("film-service/api/typefilm/get/custom")
    Call<Response> getTypeFilm(
        @Query("page") Integer page,
        @Query("limit") Integer limit,
        @Query("orderBy") String orderBy,
        @Query("asc") String asc,
        @Query("status") String status,
        @Query("q") String q
    );

    @POST("film-service/api/typefilm/add")
    Call<Response> addTypeFilm(
            @Header("Authorization") String token,
            @Body TypeFilm typeFilm
    );

    @PUT("film-service/api/typefilm/update/{id}")
    Call<Response> updateTypeFilm(
            @Header("Authorization") String token,
            @Path("id") String id,
            @Body TypeFilm typeFilm
    );

    @GET("film-service/api/film/get/custom")
    Call<Response> getFilm(
            @Query("page") Integer page,
            @Query("limit") Integer limit,
            @Query("orderBy") String orderBy,
            @Query("asc") String asc,
            @Query("status") String status,
            @Query("q") String q
    );
    @GET("film-service/api/sub/all")
    Call<Response> getAllSub();

    @GET("film-service/api/typefilm/get/all")
    Call<Response> getAllTypeFilm();

    @POST("film-service/api/film/add")
    Call<Response> addFilm(
            @Header("Authorization") String token,
            @Body Film film
    );

    @PUT("film-service/api/film/update/{id}")
    Call<Response> updateFilm(
            @Header("Authorization") String token,
            @Path("id") String id,
            @Body Film film
    );
}
