package com.tableorder.ui.manager;

import android.graphics.Color;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;

import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.tableorder.R;
import com.tableorder.databinding.ActivityDashboardBinding;
import com.tableorder.model.Order;
import com.tableorder.model.OrderItem;
import com.tableorder.model.OrderStatus;
import com.tableorder.model.Review;
import com.tableorder.viewmodel.DashboardViewModel;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * DashboardActivity — Stage 9.
 *
 * Today's revenue, today's order count, top-5 best sellers (bar chart),
 * average rating (all reviews) and busiest hour (today).
 *
 * Which orders count? Only orders that were really accepted by the manager:
 * ACCEPTED, PREPARING, READY, SERVED. PENDING and REJECTED bring no money.
 */
public class DashboardActivity extends AppCompatActivity {

    private ActivityDashboardBinding binding;

    // Last data received. Both streams update the screen, so we keep both.
    private List<Order> orders = new ArrayList<>();
    private List<Review> reviews = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDashboardBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.toolbar.setNavigationOnClickListener(v -> finish());

        setupChart();

        DashboardViewModel viewModel = new ViewModelProvider(this).get(DashboardViewModel.class);
        viewModel.getOrders().observe(this, list -> {
            orders = list != null ? list : new ArrayList<>();
            refresh();
        });
        viewModel.getReviews().observe(this, list -> {
            reviews = list != null ? list : new ArrayList<>();
            refresh();
        });
    }

    /** One-time chart look: simple, no description, no legend. */
    private void setupChart() {
        binding.barChart.getDescription().setEnabled(false);
        binding.barChart.getLegend().setEnabled(false);
        binding.barChart.getAxisRight().setEnabled(false);
        binding.barChart.setDrawGridBackground(false);
        binding.barChart.setFitBars(true);
        binding.barChart.setNoDataText("No sales today yet");

        binding.barChart.getAxisLeft().setAxisMinimum(0f);
        binding.barChart.getAxisLeft().setGranularity(1f);      // whole numbers only

        XAxis x = binding.barChart.getXAxis();
        x.setPosition(XAxis.XAxisPosition.BOTTOM);
        x.setGranularity(1f);
        x.setDrawGridLines(false);
    }

    /** Recomputes every number and the chart. */
    private void refresh() {
        long startOfDay = startOfToday();

        double revenue = 0;
        int orderCount = 0;
        Map<String, Integer> soldByItem = new HashMap<>();   // item name -> units sold today
        int[] ordersPerHour = new int[24];

        for (Order order : orders) {
            if (order.getCreatedAt() < startOfDay || !isCounted(order.getStatus())) {
                continue;
            }
            revenue += order.getTotal();
            orderCount++;

            // Which hour was the order placed?
            Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(order.getCreatedAt());
            ordersPerHour[cal.get(Calendar.HOUR_OF_DAY)]++;

            if (order.getItems() != null) {
                for (OrderItem item : order.getItems()) {
                    int sold = soldByItem.containsKey(item.getName())
                            ? soldByItem.get(item.getName()) : 0;
                    soldByItem.put(item.getName(), sold + item.getQty());
                }
            }
        }

        binding.tvRevenue.setText(String.format(Locale.US, "%.2f TND", revenue));
        binding.tvOrderCount.setText(String.valueOf(orderCount));
        binding.tvAvgRating.setText(averageRatingText());
        binding.tvBusiestHour.setText(busiestHourText(ordersPerHour));
        showTopItems(soldByItem);
    }

    private boolean isCounted(OrderStatus status) {
        return status == OrderStatus.ACCEPTED || status == OrderStatus.PREPARING
                || status == OrderStatus.READY || status == OrderStatus.SERVED;
    }

    /** Midnight of today in milliseconds. */
    private long startOfToday() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTimeInMillis();
    }

    private String averageRatingText() {
        if (reviews.isEmpty()) {
            return "-";
        }
        double sum = 0;
        for (Review r : reviews) {
            sum += r.getRating();
        }
        return String.format(Locale.US, "★ %.1f", sum / reviews.size());
    }

    private String busiestHourText(int[] ordersPerHour) {
        int bestHour = -1;
        int bestCount = 0;
        for (int h = 0; h < 24; h++) {
            if (ordersPerHour[h] > bestCount) {
                bestCount = ordersPerHour[h];
                bestHour = h;
            }
        }
        if (bestHour < 0) {
            return "-";
        }
        return String.format(Locale.US, "%02d:00 - %02d:00", bestHour, (bestHour + 1) % 24);
    }

    /** Sorts the items by units sold and draws the best 5. */
    private void showTopItems(Map<String, Integer> soldByItem) {
        List<Map.Entry<String, Integer>> entries = new ArrayList<>(soldByItem.entrySet());
        Collections.sort(entries, (a, b) -> b.getValue() - a.getValue());   // biggest first

        int count = Math.min(5, entries.size());
        if (count == 0) {
            binding.barChart.clear();      // shows the "No sales today yet" text
            binding.barChart.invalidate();
            return;
        }

        List<BarEntry> barEntries = new ArrayList<>();
        String[] labels = new String[count];
        for (int i = 0; i < count; i++) {
            barEntries.add(new BarEntry(i, entries.get(i).getValue()));
            labels[i] = entries.get(i).getKey();
        }

        BarDataSet dataSet = new BarDataSet(barEntries, "Units sold");
        dataSet.setColor(ContextCompat.getColor(this, R.color.primary));
        dataSet.setValueTextSize(12f);
        dataSet.setValueTextColor(Color.BLACK);
        // Show "3" instead of "3.0" above the bars
        dataSet.setValueFormatter(new ValueFormatter() {
            @Override
            public String getBarLabel(BarEntry barEntry) {
                return String.valueOf((int) barEntry.getY());
            }
        });

        BarData data = new BarData(dataSet);
        data.setBarWidth(0.6f);

        binding.barChart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(labels));
        binding.barChart.getXAxis().setLabelCount(count);
        binding.barChart.setData(data);
        binding.barChart.invalidate();      // redraw
    }
}
