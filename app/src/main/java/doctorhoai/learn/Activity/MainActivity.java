package doctorhoai.learn.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import com.google.android.material.navigation.NavigationView;
import doctorhoai.learn.Api.AccountService;
import doctorhoai.learn.Fragment.BillFragment;
import doctorhoai.learn.Fragment.NhanVienFragment;
import doctorhoai.learn.Fragment.PhimFragment;
import doctorhoai.learn.Fragment.RapFragment;
import doctorhoai.learn.Fragment.TongQuanFragment;
import doctorhoai.learn.Fragment.TypeFilmFragment;
import doctorhoai.learn.Model.Login;
import doctorhoai.learn.Model.Token;
import doctorhoai.learn.Model.User;
import doctorhoai.learn.R;
import doctorhoai.learn.Utils.JwtUtils;
import doctorhoai.learn.Utils.ShareData;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    // Declare
    ShareData shareData;
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    TextView txt_name, txt_email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
        loadData();

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.frame_container, new TongQuanFragment()).commit();
            navigationView.setCheckedItem(R.id.home);
        }
    }

    public void initView() {
        shareData = ShareData.getInstance(MainActivity.this);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        drawerLayout = findViewById(R.id.draw_layout);
        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open_nav, R.string.close_nav);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.getDrawerArrowDrawable().setColor(getResources().getColor(R.color.white));
        actionBarDrawerToggle.syncState();

        View headerView = navigationView.getHeaderView(0);
        txt_name = headerView.findViewById(R.id.txt_name);
        txt_email = headerView.findViewById(R.id.txt_email);
    }

    public void loadData() {
        JwtUtils jwtUtils = JwtUtils.getInstance();
        User user = shareData.getUser() == null ? jwtUtils.getUser(shareData.getToken()) : shareData.getUser();
        if (user == null) finish();
        txt_name.setText(user.getName());
        txt_email.setText(user.getEmail());
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        if (menuItem.getItemId() == R.id.home) {
            getSupportFragmentManager().beginTransaction().replace(R.id.frame_container, new TongQuanFragment()).commit();
        } else if (menuItem.getItemId() == R.id.employee) {
            getSupportFragmentManager().beginTransaction().replace(R.id.frame_container, new NhanVienFragment()).commit();
        } else if (menuItem.getItemId() == R.id.film) {
            getSupportFragmentManager().beginTransaction().replace(R.id.frame_container, new PhimFragment()).commit();
        } else if (menuItem.getItemId() == R.id.typefilm) {
            getSupportFragmentManager().beginTransaction().replace(R.id.frame_container, new TypeFilmFragment()).commit();
        } else if (menuItem.getItemId() == R.id.bills) {
            getSupportFragmentManager().beginTransaction().replace(R.id.frame_container, new BillFragment()).commit();

        } else if (menuItem.getItemId() == R.id.branch){
            getSupportFragmentManager().beginTransaction().replace(R.id.frame_container, new RapFragment()).commit();

        }else if( menuItem.getItemId() == R.id.logout){
            shareData.clearToken();
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); // Xóa stack cũ
            startActivity(intent);
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }
}