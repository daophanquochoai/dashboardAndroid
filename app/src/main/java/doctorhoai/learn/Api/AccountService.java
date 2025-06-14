package doctorhoai.learn.Api;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import doctorhoai.learn.Model.EmployeeCreate;
import doctorhoai.learn.Model.Login;
import doctorhoai.learn.Model.Response;
import doctorhoai.learn.Model.Token;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;
import retrofit2.http.*;

import java.util.concurrent.TimeUnit;

public interface AccountService {

    OkHttpClient okHttpClient = new OkHttpClient.Builder()
            .connectTimeout(10, TimeUnit.SECONDS)
            .readTimeout(10, TimeUnit.SECONDS)
            .writeTimeout(10, TimeUnit.SECONDS)
            .build();
    Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").setLenient().create();
    AccountService apiService  = new Retrofit.Builder()
            .baseUrl("http://192.168.1.22:6380/doctorhoai/proxy/")
            .client(okHttpClient)
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build().create(AccountService.class);

    @GET("user-service/api/customer/enviroment")
    Call<String> getEnviroment();

    @POST("api/authenticate")
    Call<Token> login(
            @Body Login login
    );

    @GET("payment-service/api/report/year/{year}")
    Call<Response> getReportYear(
            @Header("Authorization") String token,
            @Path("year") Integer year
    );

    @GET("film-service/api/revenue/film/all/{month}/{year}")
    Call<Response> getFilmRevenue(
            @Header("Authorization") String token,
            @Path("month") String month,
            @Path("year") String year
    );

    @GET("payment-service/api/report/year")
    Call<Response> getFilmRevenueYear(
            @Header("Authorization") String token
    );

    @GET("user-service/api/employment/get/employee")
    Call<Response> getEmployment(
            @Header("Authorization") String token,
            @Query("page") Integer page,
            @Query("limit") Integer limit,
            @Query("asc") String asc,
            @Query("status") String status,
            @Query("orderBy") String orderBy,
            @Query("q") String q
    );

    @POST("user-service/api/employment/add")
    Call<Response> addEmployment(
            @Header("Authorization") String token,
            @Body EmployeeCreate employeeCreate
    );

    @PUT("user-service/api/employment/reset/{id}")
    Call<Response> resetEmployment(
            @Header("Authorization") String token,
            @Path("id") String id
    );
}
