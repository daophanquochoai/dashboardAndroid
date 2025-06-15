package doctorhoai.learn.Api;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.concurrent.TimeUnit;

import doctorhoai.learn.Model.Branch;
import doctorhoai.learn.Model.BranchCreate;
import doctorhoai.learn.Model.Response;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface BranchService {
    OkHttpClient okHttpClient = new OkHttpClient.Builder()
            .connectTimeout(10, TimeUnit.SECONDS)
            .readTimeout(10, TimeUnit.SECONDS)
            .writeTimeout(10, TimeUnit.SECONDS)
            .build();
    Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").setLenient().create();
    BranchService apiService  = new Retrofit.Builder()
            .baseUrl("http://10.0.2.2:6380/doctorhoai/proxy/")
            .client(okHttpClient)
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build().create(BranchService.class);

    @GET("room-service/api/branch/get/branch")
    Call<Response> getBranch(
            @Query("page") Integer page,
            @Query("limit") Integer limit,
            @Query("orderBy") String orderBy,
            @Query("asc") String asc,
            @Query("status") String status,
            @Query("q") String q
    );

    @POST("room-service/api/branch/add")
    Call<Response> addBranch(
            @Header("Authorization") String token,
            @Body Branch branch
    );

    @PUT("room-service/api/branch/update/{id}")
    Call<Response> updateBranch(
            @Header("Authorization") String token,
            @Path("id") String id,
            @Body Branch branch
    );

}
