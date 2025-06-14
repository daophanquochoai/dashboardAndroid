package doctorhoai.learn.Api;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import doctorhoai.learn.Model.Bill;
import doctorhoai.learn.Model.Response;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;
import retrofit2.http.*;
import java.util.concurrent.TimeUnit;

public interface BillService {

    OkHttpClient okHttpClient = new OkHttpClient.Builder()
            .connectTimeout(10, TimeUnit.SECONDS)
            .readTimeout(10, TimeUnit.SECONDS)
            .writeTimeout(10, TimeUnit.SECONDS)
            .build();

    Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").setLenient().create();

    BillService apiService = new Retrofit.Builder()
            .baseUrl("http://192.168.1.22:6380/doctorhoai/proxy/")
            .client(okHttpClient)
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
            .create(BillService.class);

    @GET("payment-service/api/bill/custom")
    Call<Response> getBills(
            @Header("Authorization") String token,
            @Query("page") String page,
            @Query("limit") String limit,
            @Query("active") String active,
            @Query("orderBy") String orderBy,
            @Query("asc") String asc,
            @Query("q") String q
    );

    @POST("payment-service/api/bill/add")
    Call<Response> addBill(
            @Header("Authorization") String token,
            @Body Bill bill
    );

    @PATCH("payment-service/api/bill/active/{id}")
    Call<Response> activeBill(
            @Header("Authorization") String token,
            @Path("id") String id
    );

    @GET("payment-service/api/bill/{id}")
    Call<Response> getBillById(
            @Header("Authorization") String token,
            @Path("id") String id
    );
}