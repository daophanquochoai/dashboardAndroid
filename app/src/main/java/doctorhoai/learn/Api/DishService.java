package doctorhoai.learn.Api;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import doctorhoai.learn.Model.Dish;
import doctorhoai.learn.Model.Response;
import doctorhoai.learn.Model.TypeDish;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;
import retrofit2.http.*;
import java.util.List;
import java.util.concurrent.TimeUnit;

public interface DishService {

    OkHttpClient okHttpClient = new OkHttpClient.Builder()
            .connectTimeout(10, TimeUnit.SECONDS)
            .readTimeout(10, TimeUnit.SECONDS)
            .writeTimeout(10, TimeUnit.SECONDS)
            .build();

    Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").setLenient().create();

    DishService apiService = new Retrofit.Builder()
            .baseUrl("http://10.0.2.2:6380/doctorhoai/proxy/")
            .client(okHttpClient)
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
            .create(DishService.class);

    @GET("dish-service/api/dish/all")
    Call<Response> getAllDishes(
            @Header("Authorization") String token,
            @Query("page") String page,
            @Query("limit") String limit,
            @Query("active") String active,
            @Query("orderBy") String orderBy,
            @Query("asc") String asc,
            @Query("q") String q
    );

    @POST("dish-service/api/dish/add")
    Call<Response> addDish(
            @Header("Authorization") String token,
            @Body Dish dish
    );

    @PUT("dish-service/api/dish/update/{id}")
    Call<Response> updateDish(
            @Header("Authorization") String token,
            @Path("id") String id,
            @Body Dish dish
    );

    @PATCH("dish-service/api/dish/delete/{id}")
    Call<Response> deleteDish(
            @Header("Authorization") String token,
            @Path("id") String id
    );

    @PATCH("dish-service/api/dish/active/{id}")
    Call<Response> activeDish(
            @Header("Authorization") String token,
            @Path("id") String id
    );

    @GET("dish-service/api/dish/{id}")
    Call<Response> getDishById(
            @Header("Authorization") String token,
            @Path("id") String id
    );

    @GET("dish-service/api/type-dish/all")
    Call<Response> getAllTypeDishes(
            @Header("Authorization") String token
    );
}