package pythonteam.com.ventasapp.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import pythonteam.com.ventasapp.OrdersFragment;
import pythonteam.com.ventasapp.R;
import pythonteam.com.ventasapp.models.Order;
import pythonteam.com.ventasapp.util.Retrofits;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrdersAdapter extends RecyclerView.Adapter<OrdersAdapter.ViewHolder>  {


    private final OrdersFragment.OrderInteractionListener mListener;
    private final Context context;
    private ArrayList<Order> orders;

    public OrdersAdapter(ArrayList<Order> mValues, OrdersFragment.OrderInteractionListener mListener, Context context) {
        this.orders = mValues;
        this.mListener = mListener;
        this.context = context;
    }

    @NonNull
    @Override
    public OrdersAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.fragment_order, parent, false);
        return new ViewHolder(mListener, view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrdersAdapter.ViewHolder holder, int position) {
        holder.mItem = orders.get(position);
        String id = "" + holder.mItem.getEmployeeId();
        holder.txtEmployeeId.setText(id);
        String customerId = "" + holder.mItem.getCustomerId();
        holder.txtCustomerId.setText(customerId);
        holder.txtStatus.setText(String.format("%s", holder.mItem.isStatus()));
        holder.txtOrderDate.setText(String.format("%d/%d/%d", holder.mItem.getOrderdate().getDayOfMonth(), holder.mItem.getOrderdate().getMonthValue(), holder.mItem.getOrderdate().getYear()));

        holder.btnODelete.setOnClickListener(v ->
        {
            Retrofits.get().deleteOrder(holder.mItem.getOrderId()).enqueue(new Callback<Boolean>() {
                @Override
                public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                    Toast.makeText(context,"Orden Borrada", Toast.LENGTH_LONG).show();
                    orders.remove(holder.mItem);
                    notifyDataSetChanged();
                }

                @Override
                public void onFailure(Call<Boolean> call, Throwable t) {
                    Toast.makeText(context, "No ha podido ser borrado", Toast.LENGTH_LONG).show();
                }
            });

        });
        holder.mView.setOnClickListener(v -> {
            if (null != mListener) {
                // Notify the active callbacks interface (the activity, if the
                // fragment is attached to one) that an item has been selected.
                mListener.onListFragmentInteraction(holder.mItem);
            }
        });

    }

    @Override
    public int getItemCount() {
        return orders.size();
    }

    public void setOrders(ArrayList<Order> mValues) {
        this.orders = mValues;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private final OrdersFragment.OrderInteractionListener mListener;
        public final View mView;
        public final TextView txtCustomerId;
        public final TextView txtEmployeeId;
        public final TextView txtStatus;
        public final TextView txtOrderDate;
        public final ImageButton btnODelete;
        public Order mItem;

        public ViewHolder(OrdersFragment.OrderInteractionListener mListener,View view) {
            super(view);
            mView = view;
            this.mListener = mListener;
            txtCustomerId =   view.findViewById(R.id.txtCustomerId);
            txtEmployeeId =   view.findViewById(R.id.txtEmployeeId);
            txtStatus =   view.findViewById(R.id.txtStatus);
            txtOrderDate =   view.findViewById(R.id.txtOrderDate);
            btnODelete = view.findViewById(R.id.btnODelete);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + txtCustomerId.getText() + "'";
        }
    }
}
