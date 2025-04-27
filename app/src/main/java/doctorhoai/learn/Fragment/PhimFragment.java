//package doctorhoai.learn.Fragment;
//
//import android.os.Bundle;
//import android.widget.*;
//import androidx.fragment.app.Fragment;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import doctorhoai.learn.Model.Param;
//import doctorhoai.learn.Model.TypeFilm;
//import doctorhoai.learn.R;
//import doctorhoai.learn.Utils.ShareData;
//
//import java.util.List;
//
//public class PhimFragment extends Fragment {
//
//    private Param param = new Param(0,"asc", 10, "name", "", "none");
//    private ProgressBar progressBar;
//    private ImageView imgNotFound, imgPrev, imgNext;
//    private TableLayout filmTable;
//    private ShareData shareData;
//    private ObjectMapper mapper = new ObjectMapper();
//    private List<TypeFilm> typeFilms;
//    private TextView tvCounter;
//    private Integer totalPage = 1;
//    private Spinner spinnerAsc, spinnerOrderBy;
//    private Button btnThem;
//    private EditText edtSearch;
//
//
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container,
//                             Bundle savedInstanceState) {
//        View view =  inflater.inflate(R.layout.fragment_phim, container, false);
//
//        return view;
//    }
//
//    public void initView (View view ) {
//        progressBar = view.findViewById(R.id.proccesBar_table_film);
//        imgNotFound = view.findViewById(R.id.img_table);
//        imgPrev = view.findViewById(R.id.img_prev);
//        imgNext = view.findViewById(R.id.img_next);
//        filmTable = view.findViewById(R.id.table_film);
//        shareData =
//    }
//}