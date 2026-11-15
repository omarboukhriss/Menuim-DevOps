package com.tableorder.util;

import com.tableorder.R;
import com.tableorder.model.Order;
import com.tableorder.model.OrderItem;
import com.tableorder.model.OrderStatus;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * OrderUtils — small static helpers shared by several screens
 * (manager orders, client tracking, reviews).
 */
public final class OrderUtils {

    private OrderUtils() { }

    /** "2x Pizza, 1x Coffee" */
    public static String itemsSummary(Order order) {
        StringBuilder sb = new StringBuilder();
        if (order.getItems() != null) {
            for (OrderItem item : order.getItems()) {
                if (sb.length() > 0) {
                    sb.append(", ");
                }
                sb.append(item.getQty()).append("x ").append(item.getName());
            }
        }
        return sb.toString();
    }

    /** "14:35" */
    public static String formatTime(long millis) {
        return new SimpleDateFormat("HH:mm", Locale.US).format(new Date(millis));
    }

    /** "02/11 14:35" */
    public static String formatDateTime(long millis) {
        return new SimpleDateFormat("dd/MM HH:mm", Locale.US).format(new Date(millis));
    }

    /** Text shown on the status badge. */
    public static String statusLabel(OrderStatus status) {
        switch (status) {
            case PENDING:   return "Pending";
            case ACCEPTED:  return "Accepted";
            case REJECTED:  return "Rejected";
            case PREPARING: return "Preparing";
            case READY:     return "Ready";
            case SERVED:    return "Served";
            default:        return status.name();
        }
    }

    /** Color resource for the status badge (colors are defined in colors.xml). */
    public static int statusColorRes(OrderStatus status) {
        switch (status) {
            case PENDING:   return R.color.status_pending;
            case ACCEPTED:  return R.color.status_accepted;
            case REJECTED:  return R.color.status_rejected;
            case PREPARING: return R.color.status_preparing;
            case READY:     return R.color.status_ready;
            case SERVED:    return R.color.status_served;
            default:        return R.color.primary;
        }
    }
}
