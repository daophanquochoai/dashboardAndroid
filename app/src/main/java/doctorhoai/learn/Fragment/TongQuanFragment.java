package doctorhoai.learn.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.widget.*;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.*;
import com.github.mikephil.charting.utils.ColorTemplate;
import doctorhoai.learn.Activity.LoginActivity;
import doctorhoai.learn.Adapter.FilmRevenueAdapter;
import doctorhoai.learn.Api.AccountService;
import doctorhoai.learn.Model.Response;
import doctorhoai.learn.Model.Revenue;
import doctorhoai.learn.Model.RevenueFilm;
import doctorhoai.learn.R;
import doctorhoai.learn.Utils.LargeValueFormatter;
import doctorhoai.learn.Utils.ShareData;
import retrofit2.Call;
import retrofit2.Callback;
import com.github.mikephil.charting.charts.PieChart;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class TongQuanFragment extends Fragment {

    ShareData shareData;
    BarChart barChart;
    PieChart pieChart;
    ObjectMapper mapper = new ObjectMapper();
    String token;
    ProgressBar progressBar_BarChart, progressBar_PieChart, progressBar_Recycler;
    Spinner spinner, spinnerMonth;
    String year = LocalDateTime.now().getYear() + "";
    String month = LocalDateTime.now().getMonthValue() + "";
    RecyclerView recyclerView;
    List<RevenueFilm> revenueFilms;
    private ImageView imgBar, imgPie, imgRecy;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tong_quan, container, false);

        initView(view);

        initSpinnerYear();
        drawBarChart(year);
        drawPiChart(month, year);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedItem = parent.getItemAtPosition(position).toString();
                year = selectedItem;
                drawBarChart(selectedItem);
                drawPiChart(month, year);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        spinnerMonth.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedItem = parent.getItemAtPosition(position).toString();
                month = selectedItem.substring(1);
                drawPiChart(month, year);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        return view;
    }

    public void initView (View view) {
        progressBar_BarChart = view.findViewById(R.id.proccesBar_Bar);
        progressBar_PieChart = view.findViewById(R.id.proccesBar_Pie);
        progressBar_Recycler = view.findViewById(R.id.proccesBar_recycler);
        barChart = view.findViewById(R.id.barchart);
        barChart.setVisibility(View.GONE);
        pieChart = view.findViewById(R.id.piechart);
        pieChart.setVisibility(View.GONE);
        shareData = ShareData.getInstance(getActivity());
        spinner = view.findViewById(R.id.spinner);
        spinnerMonth = view.findViewById(R.id.spinner_month);
        List<String> list = List.of("T1", "T2","T3","T4","T5","T6","T7","T8","T9","T10","T11","T12");
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                getActivity(),
                android.R.layout.simple_spinner_item,
                list
        );
        spinnerMonth.setAdapter(adapter);
        spinnerMonth.setSelection(Integer.valueOf(month));
        token = shareData.getToken();
        recyclerView = view.findViewById(R.id.recyclerView_film_revenue);
        recyclerView.setVisibility(View.GONE);
        imgBar = view.findViewById(R.id.img_bar);
        imgPie = view.findViewById(R.id.img_pie);
        imgRecy = view.findViewById(R.id.img_recycler);
    }

    public ArrayList<BarEntry> changeToBarEntry (List<Revenue> list){
        ArrayList<BarEntry> entries = list.stream().map( r -> {
                return new BarEntry(r.getMonth(), Float.parseFloat(r.getTotalPrice()));
            }).collect(Collectors.toCollection(ArrayList::new));
        return entries;
    }

    // call revenue in year;
    public void initBarChart(){
        barChart.setDrawGridBackground(false);
        //remove the bar shadow, default false if not set
        barChart.setDrawBarShadow(false);
        //remove border of the chart, default false if not set
        barChart.setDrawBorders(false);

        barChart.getAxisRight().setEnabled(false);


        //remove the description label text located at the lower right corner
        Description description = new Description();
        description.setEnabled(false);
        barChart.setDescription(description);

        //setting animation for y-axis, the bar will pop up from 0 to its value within the time we set
        barChart.animateY(1000);
        //setting animation for x-axis, the bar will pop up separately within the time we set
        barChart.animateX(1000);

        XAxis xAxis = barChart.getXAxis();
        //change the position of x-axis to the bottom
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        //set the horizontal distance of the grid line
        xAxis.setGranularity(1f);
        //hiding the x-axis line, default true if not set
        xAxis.setDrawAxisLine(false);
        //hiding the vertical grid lines, default true if not set
        xAxis.setDrawGridLines(false);
        xAxis.setLabelCount(12, true);

        YAxis leftAxis = barChart.getAxisLeft();
        leftAxis.setValueFormatter(new LargeValueFormatter());
        //hiding the left y-axis line, default true if not set
        leftAxis.setDrawAxisLine(false);

        YAxis rightAxis = barChart.getAxisRight();
        //hiding the right y-axis line, default true if not set
        rightAxis.setDrawAxisLine(false);

        Legend legend = barChart.getLegend();
        //setting the shape of the legend form to line, default square shape
        legend.setForm(Legend.LegendForm.LINE);
        //setting the text size of the legend
        legend.setTextSize(11f);
        //setting the alignment of legend toward the chart
        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.LEFT);
        //setting the stacking direction of legend
        legend.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        //setting the location of legend outside the chart, default false if not set
        legend.setDrawInside(false);
    }

    public void drawBarChart ( String year) {
        imgBar.setVisibility(View.GONE);
        progressBar_BarChart.setVisibility(View.VISIBLE);

        AccountService.apiService.getReportYear("Bearer " + token, Integer.parseInt(year)).enqueue(new Callback<Response>() {
            @Override
            public void onResponse(Call<Response> call, retrofit2.Response<Response> response) {
                if( response.isSuccessful() ){
                    try{
                        Response res = response.body();
                        Object obj = res.getData();
                        ArrayList<BarEntry> entries = changeToBarEntry(mapper.convertValue(obj, new TypeReference<List<Revenue>>() {}));
                        BarDataSet dataSet = new BarDataSet(entries, "Doanh thu theo tháng");
                        dataSet.setValueFormatter(new LargeValueFormatter());
                        BarData data = new BarData(dataSet);
                        data.setBarWidth(0.9f); // độ rộng cột
                        dataSet.setColors(ColorTemplate.COLORFUL_COLORS);
                        barChart.setDrawBorders(false);
                        barChart.setData(data);
                        barChart.setFitBars(true); // hiển thị đầy đủ cột
                        initBarChart();

                        barChart.invalidate(); // vẽ lại chart
                        barChart.setVisibility(View.VISIBLE);
                        progressBar_BarChart.setVisibility(View.GONE);
                        if( entries.size() == 0 ){
                            barChart.setVisibility(View.GONE);
                            imgBar.setVisibility(View.VISIBLE);
                        }
                    }catch (Exception e){
                        imgBar.setVisibility(View.VISIBLE);
                        progressBar_BarChart.setVisibility(View.GONE);
                        barChart.setVisibility(View.GONE);
                        Toast.makeText(getActivity(), e.getCause().toString(), Toast.LENGTH_SHORT).show();
                    }
                }else{
                    if( response.raw().code() == 401){
                        Toast.makeText(getActivity(), "Hết phiên làm việc", Toast.LENGTH_SHORT).show();
                        shareData.clearToken();
                        Intent intent = new Intent(getActivity(), LoginActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    }else{
                        progressBar_BarChart.setVisibility(View.GONE);
                        imgBar.setVisibility(View.VISIBLE);
                        Toast.makeText(getActivity(), "Hệ thống xảy ra lỗi", Toast.LENGTH_SHORT).show();
                    }
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

    public void drawPiChart(String month, String year){
        progressBar_PieChart.setVisibility(View.VISIBLE);
        imgPie.setVisibility(View.GONE);

        AccountService.apiService.getFilmRevenue(token,month, year).enqueue(new Callback<Response>() {
            @Override
            public void onResponse(Call<Response> call, retrofit2.Response<Response> response) {
                if( response.isSuccessful() ){
                    Response res = response.body();
                    Object obj = res.getData();
                    revenueFilms = mapper.convertValue(obj, new TypeReference<List<RevenueFilm>>() {});
                    displayRevenueFilm();
                    ArrayList<PieEntry> entries = changeToPieEntry(revenueFilms);
                    PieDataSet pieDataSet = new PieDataSet(entries, "Doanh thu phim");
                    pieDataSet.setValueTextSize(12f);
                    pieDataSet.setColors(ColorTemplate.COLORFUL_COLORS);
                    PieData data = new PieData(pieDataSet);
                    pieChart.setData(data);
                    initPieChart();
                    pieChart.invalidate();

                    pieChart.setVisibility(View.VISIBLE);
                    progressBar_PieChart.setVisibility(View.GONE);

                    if( entries.size() == 0 ){
                        pieChart.setVisibility(View.GONE);
                        imgPie.setVisibility(View.VISIBLE);
                    }
                }else{
                    if( response.raw().code() == 401){
                        Toast.makeText(getActivity(), "Hết phiên làm việc", Toast.LENGTH_SHORT).show();
                        shareData.clearToken();
                        Intent intent = new Intent(getActivity(), LoginActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    }else{
                        pieChart.setVisibility(View.GONE);
                        progressBar_PieChart.setVisibility(View.GONE);
                        imgPie.setVisibility(View.VISIBLE);

                        recyclerView.setVisibility(View.GONE);
                        imgRecy.setVisibility(View.VISIBLE);
                        progressBar_Recycler.setVisibility(View.GONE);

                        Toast.makeText(getActivity(), "Hệ thống xảy ra lỗi", Toast.LENGTH_SHORT).show();
                    }
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

    //call film in month
    private void initPieChart(){
        //using percentage as values instead of amount
        pieChart.setUsePercentValues(true);

        //remove the description label on the lower left corner, default true if not set
        pieChart.getDescription().setEnabled(false);

        //enabling the user to rotate the chart, default true
        pieChart.setRotationEnabled(true);
        //adding friction when rotating the pie chart
        pieChart.setDragDecelerationFrictionCoef(0.9f);
        //setting the first entry start from right hand side, default starting from top
        pieChart.setRotationAngle(0);

        //highlight the entry when it is tapped, default true if not set
        pieChart.setHighlightPerTapEnabled(true);
        //adding animation so the entries pop up from 0 degree
        pieChart.animateY(1400);

    }

    public ArrayList<PieEntry> changeToPieEntry(List<RevenueFilm> list){
        ArrayList<PieEntry> entries = list.stream().map( r -> {
            return new PieEntry( Float.parseFloat(r.getTotal_revenue()), r.getName());
        }).collect(Collectors.toCollection(ArrayList::new));
        return entries;
    }

    //call year
    public void initSpinnerYear() {
        AccountService.apiService.getFilmRevenueYear("Bearer " + token).enqueue(new Callback<Response>() {
            @Override
            public void onResponse(Call<Response> call, retrofit2.Response<Response> response) {
                if( response.isSuccessful() ){
                    try{
                        Response res = response.body();
                        Object obj = res.getData();
                        List<String> list = mapper.convertValue(obj, new TypeReference<List<String>>() {});
                        if( list.size() == 0 ){
                            list.add(year);
                        }else{
                            if( !list.get(0).equals(year) ){
                                list.add(0, year);
                            }
                        }
                        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                                getActivity(),
                                android.R.layout.simple_spinner_item,
                                list
                        );
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spinner.setAdapter(adapter);
                        spinner.setSelection(0);

                    }catch (Exception e){
                        e.printStackTrace();
                        Toast.makeText(getActivity(), e.getCause().toString(), Toast.LENGTH_SHORT).show();
                    }
                }else{
                    if( response.raw().code() == 401){
                        Toast.makeText(getActivity(), "Hết phiên làm việc", Toast.LENGTH_SHORT).show();
                        shareData.clearToken();
                        Intent intent = new Intent(getActivity(), LoginActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    }else{
                        Toast.makeText(getActivity(), "Hệ thống xảy ra lỗi", Toast.LENGTH_SHORT).show();
                    }
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

    public void displayRevenueFilm(){
        progressBar_Recycler.setVisibility(View.VISIBLE);
        imgRecy.setVisibility(View.GONE);

        FilmRevenueAdapter filmRevenueAdapter = new FilmRevenueAdapter(getActivity(), revenueFilms);
        recyclerView.setAdapter(filmRevenueAdapter);
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setHasFixedSize(true);

        recyclerView.setVisibility(View.VISIBLE);
        progressBar_Recycler.setVisibility(View.GONE);

        if( revenueFilms.size() == 0 ){
            recyclerView.setVisibility(View.GONE);
            imgRecy.setVisibility(View.VISIBLE);
        }
    }


}
