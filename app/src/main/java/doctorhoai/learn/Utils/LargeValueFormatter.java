package doctorhoai.learn.Utils;

import com.github.mikephil.charting.formatter.ValueFormatter;

public class LargeValueFormatter extends ValueFormatter {
    @Override
    public String getFormattedValue(float value) {
        if (value >= 1_000_000_000) {
            return String.format("%.1f T", value / 1_000_000_000);
        } else if (value >= 1_000_000) {
            return String.format("%.1f M", value / 1_000_000);
        } else if (value >= 1_000) {
            return String.format("%.1f K", value / 1_000);
        } else {
            return String.format("%.0f", value);
        }
    }
}