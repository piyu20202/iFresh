package com.ifresh.customer.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.toolbox.NetworkImageView;
import com.ifresh.customer.R;
import com.ifresh.customer.activity.TrackerDetailActivity;
import com.ifresh.customer.helper.ApiConfig;
import com.ifresh.customer.helper.Constant;
import com.ifresh.customer.helper.Session;
import com.ifresh.customer.helper.VolleyCallback;
import com.ifresh.customer.model.OrderTracker_2;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.ifresh.customer.activity.TrackerDetailActivity.pBar;

public class ItemsAdapter_2 extends RecyclerView.Adapter<ItemsAdapter_2.CartItemHolder> {

    Activity activity;
    ArrayList<OrderTracker_2> orderTrackerArrayList;
    String from = "";



    public ItemsAdapter_2(Activity activity, ArrayList<OrderTracker_2> orderTrackerArrayList) {
        this.activity = activity;
        this.orderTrackerArrayList = orderTrackerArrayList;

    }

    public ItemsAdapter_2(Activity activity, ArrayList<OrderTracker_2> orderTrackerArrayList, String from) {
        this.activity = activity;
        this.orderTrackerArrayList = orderTrackerArrayList;
        this.from = from;

    }

    @Override
    public CartItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.items_lyt, null);
        CartItemHolder cartItemHolder = new CartItemHolder(v);
        return cartItemHolder;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(final CartItemHolder holder, final int position) {

        final OrderTracker_2 order = orderTrackerArrayList.get(position);

        String payType = "";
        if (order.getPayment_method().equalsIgnoreCase("cod"))
            payType = activity.getResources().getString(R.string.cod);
        else
            payType = order.getPayment_method();
        String activeStatus = order.getActiveStatus();

        if(order.getOrder_type().equalsIgnoreCase("2"))
        {
            holder.txtqtxt.setVisibility(View.GONE);
            holder.txtqty.setVisibility(View.GONE);
        }
        else{
            holder.txtqtxt.setVisibility(View.VISIBLE);
            holder.txtqty.setVisibility(View.VISIBLE);
            holder.txtqty.setText(order.getQuantity());
        }


         if(order.getOrder_type().equalsIgnoreCase("2"))
         {
             holder.txtunit.setVisibility(View.GONE);
             holder.txtunit_1.setVisibility(View.GONE);
         }
         else{
             holder.txtunit.setVisibility(View.VISIBLE);
             holder.txtunit_1.setVisibility(View.VISIBLE);
             holder.txtunit_1.setText(order.getUnit() + " " + order.getMeasurement());
         }

        if(order.getOrder_type().equalsIgnoreCase("2"))
        {
            holder.txtprice.setVisibility(View.GONE);
        }
        else{
            holder.txtprice.setVisibility(View.VISIBLE);
            holder.txtprice.setText(Constant.SETTING_CURRENCY_SYMBOL + order.getPrice());
        }

        holder.txtpaytype.setText(activity.getResources().getString(R.string.via) + payType);
        holder.txtstatus.setText(activeStatus);

        if(order.getActiveStatus().equalsIgnoreCase("cancelled"))
        {
            holder.txtstatus.setTextColor(activity.getResources().getColor(R.color.red));
        }
        else if(order.getActiveStatus().equalsIgnoreCase("received"))
        {
            holder.txtstatus.setTextColor(activity.getResources().getColor(R.color.colorPrimary));
        }
        else if(order.getActiveStatus().equalsIgnoreCase("delivered"))
        {
            holder.txtstatus.setTextColor(activity.getResources().getColor(R.color.orange));
        }
        holder.txtstatusdate.setText(order.getActiveStatusDate());

        if(order.getName().equalsIgnoreCase("null"))
        {
           if(order.getOrder_type().equalsIgnoreCase("2"))
           {
               holder.txtname.setText("Medical Prescription");
           }
           else{
               holder.txtname.setText("iFresh Product");
           }
        }
        else{
            holder.txtname.setText(order.getName());
        }


        holder.imgorder.setDefaultImageResId(R.drawable.placeholder);
        holder.imgorder.setErrorImageResId(R.drawable.placeholder);
        holder.imgorder.setImageUrl(order.getImage(), Constant.imageLoader);

        holder.carddetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.startActivity(new Intent(activity, TrackerDetailActivity.class).putExtra("model", order));
            }
        });

       /* holder.btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateOrderStatus(activity, order, Constant.CANCELLED, holder, from);
            }
        });

        holder.btnReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SimpleDateFormat myFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                Date date = new Date();
                System.out.println(myFormat.format(date));
                String inputString1 = order.getActiveStatusDate();
                String inputString2 = myFormat.format(date);
                try {
                    Date date1 = myFormat.parse(inputString1);
                    Date date2 = myFormat.parse(inputString2);
                    long diff = date2.getTime() - date1.getTime();
                    //  System.out.println("Days: "+TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS));

                    if (TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS) <= Constant.ORDER_DAY_LIMIT) {
                        updateOrderStatus(activity, order, Constant.RETURNED, holder, from);

                    } else {
                        final Snackbar snackbar = Snackbar.make(activity.findViewById(android.R.id.content), activity.getResources().getString(R.string.product_return) + Constant.ORDER_DAY_LIMIT + activity.getString(R.string.day_max_limit), Snackbar.LENGTH_INDEFINITE);
                        snackbar.setAction(activity.getResources().getString(R.string.ok), new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                snackbar.dismiss();

                            }
                        });
                        snackbar.setActionTextColor(Color.RED);
                        View snackbarView = snackbar.getView();
                        TextView textView = snackbarView.findViewById(com.google.android.material.R.id.snackbar_text);
                        textView.setMaxLines(5);
                        snackbar.show();

                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        });
        */

        if (from.equals("detail")) {

            if (order.getActiveStatus().equalsIgnoreCase("cancelled")) {
                holder.txtstatus.setTextColor(Color.RED);
                holder.btnCancel.setVisibility(View.GONE);
            } else if (order.getActiveStatus().equalsIgnoreCase("delivered")) {
                holder.btnCancel.setVisibility(View.GONE);
                holder.btnReturn.setVisibility(View.VISIBLE);
            } else if (order.getActiveStatus().equalsIgnoreCase("returned")) {
                holder.btnCancel.setVisibility(View.GONE);
                holder.btnReturn.setVisibility(View.GONE);
            } else {
                holder.btnCancel.setVisibility(View.VISIBLE);
            }

            holder.lyttracker.setVisibility(View.VISIBLE);

            if (order.getActiveStatus().equalsIgnoreCase("cancelled")) {
                holder.lyttracker.setVisibility(View.GONE);
            } else {
                if (order.getActiveStatus().equals("returned")) {
                    holder.l4.setVisibility(View.VISIBLE);
                    holder.returnLyt.setVisibility(View.VISIBLE);
                }
                holder.lyttracker.setVisibility(View.VISIBLE);

                ApiConfig.setOrderTrackerLayout_2(activity, order, holder);
            }
        }
    }

    private void updateOrderStatus(final Activity activity, final OrderTracker_2 order, final String status, final CartItemHolder holder, final String from) {

        final Map<String, String> params = new HashMap<>();
        params.put(Constant.UPDATE_ORDER_ITEM_STATUS, Constant.GetVal);
        params.put(Constant.ORDER_ITEM_ID, order.getId());
        params.put(Constant.ORDER_ID, order.getOrder_id());
        params.put(Constant.STATUS, status);


        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(activity);
        // Setting Dialog Message
        if (status.equals(Constant.CANCELLED)) {
            alertDialog.setTitle(activity.getResources().getString(R.string.cancel_order));
            alertDialog.setMessage(activity.getResources().getString(R.string.cancel_msg));
        } else if (status.equals(Constant.RETURNED)) {
            alertDialog.setTitle(activity.getResources().getString(R.string.return_order));
            alertDialog.setMessage(activity.getResources().getString(R.string.return_msg));
        }
        alertDialog.setCancelable(false);
        final AlertDialog alertDialog1 = alertDialog.create();

        // Setting OK Button
        alertDialog.setPositiveButton(activity.getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                if (pBar != null)
                    pBar.setVisibility(View.VISIBLE);
                ApiConfig.RequestToVolley(new VolleyCallback() {
                    @Override
                    public void onSuccess(boolean result, String response) {
                        // System.out.println("================= " + response);
                        if (result) {
                            try {
                                JSONObject object = new JSONObject(response);
                                if (!object.getBoolean(Constant.ERROR)) {
                                    if (status.equals(Constant.CANCELLED)) {
                                        holder.btnCancel.setVisibility(View.GONE);
                                        holder.txtstatus.setText(status);
                                        holder.txtstatus.setTextColor(Color.RED);
                                        holder.lyttracker.setVisibility(View.GONE);
                                        order.status = status;
                                        if (from.equals("detail")) {
                                            if (orderTrackerArrayList.size() == 1) {
                                                TrackerDetailActivity.btnCancel.setVisibility(View.GONE);
                                                TrackerDetailActivity.lyttracker.setVisibility(View.GONE);
                                            }
                                        }
                                        ApiConfig.getWalletBalance(activity, new Session(activity));
                                    } else {
                                        holder.btnReturn.setVisibility(View.GONE);
                                        holder.txtstatus.setText(status);
                                    }
                                    Constant.isOrderCancelled = true;
                                }
                                Toast.makeText(activity, object.getString("message"), Toast.LENGTH_LONG).show();
                                if (pBar != null)
                                    pBar.setVisibility(View.GONE);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }, activity, Constant.ORDERPROCESS_URL, params, false);

            }
        });
        alertDialog.setNegativeButton(activity.getResources().getString(R.string.no), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                alertDialog1.dismiss();
            }
        });
        // Showing Alert Message
        alertDialog.show();
    }

    public class CartItemHolder extends RecyclerView.ViewHolder {
        TextView txtqty, txtqtxt,txtprice, txtpaytype, txtstatus, txtstatusdate, txtname,txtunit_1,txtunit;
        NetworkImageView imgorder;
        CardView carddetail;
        RecyclerView recyclerView;
        Button btnCancel, btnReturn;
        View l4;
        LinearLayout lyttracker, returnLyt;

        public CartItemHolder(View itemView) {
            super(itemView);
            txtqtxt = itemView.findViewById(R.id.txtqtxt);
            txtqty = itemView.findViewById(R.id.txtqty);
            txtunit_1 = itemView.findViewById(R.id.txtunit_1);
            txtunit = itemView.findViewById(R.id.txtunit);
            txtprice = itemView.findViewById(R.id.txtprice);
            txtpaytype = itemView.findViewById(R.id.txtpaytype);
            txtstatus = itemView.findViewById(R.id.txtstatus);
            txtstatusdate = itemView.findViewById(R.id.txtstatusdate);
            txtname = itemView.findViewById(R.id.txtname);
            btnCancel = itemView.findViewById(R.id.btnCancel);
            imgorder = itemView.findViewById(R.id.imgorder);
            carddetail = itemView.findViewById(R.id.carddetail);
            recyclerView = itemView.findViewById(R.id.recyclerView);
            btnReturn = itemView.findViewById(R.id.btnReturn);
            lyttracker = itemView.findViewById(R.id.lyttracker);
            l4 = itemView.findViewById(R.id.l4);
            returnLyt = itemView.findViewById(R.id.returnLyt);
        }
    }


    @Override
    public int getItemCount() {
        return orderTrackerArrayList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

}
