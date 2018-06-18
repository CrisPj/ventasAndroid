package pythonteam.com.ventasapp.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import pythonteam.com.ventasapp.CustomersFragment;
import pythonteam.com.ventasapp.ProductsFragment;
import pythonteam.com.ventasapp.R;
import pythonteam.com.ventasapp.models.Customer;
import pythonteam.com.ventasapp.models.Product;
import pythonteam.com.ventasapp.util.Retrofits;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CustomersAdapter extends RecyclerView.Adapter<CustomersAdapter.ViewHolder>  {


    private final CustomersFragment.CustomerInteractionListener mListener;
    private final Context context;
    private ArrayList<Customer> customers;

    public CustomersAdapter(ArrayList<Customer> mValues, CustomersFragment.CustomerInteractionListener mListener, Context context) {
        this.customers = mValues;
        this.mListener = mListener;
        this.context = context;
    }

    @NonNull
    @Override
    public CustomersAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.fragment_customer, parent, false);
        return new ViewHolder(mListener, view);
    }

    @Override
    public void onBindViewHolder(@NonNull CustomersAdapter.ViewHolder holder, int position) {
        holder.mItem = customers.get(position);
        holder.mCustomerName.setText(holder.mItem.getName());
        holder.mCustomerMail.setText(holder.mItem.getEmail());
        holder.mCustomerPhone.setText(holder.mItem.getPhone());
        holder.btnMail.setOnClickListener( v -> {
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("text/plain");
            intent.putExtra(Intent.EXTRA_SUBJECT, "Donapp");
            intent.putExtra(Intent.EXTRA_TEXT, "Hola "+ holder.mItem.getName() +"!");
            intent.putExtra(Intent.EXTRA_EMAIL, new String[]{holder.mItem.getEmail()});
            context.startActivity(intent);
        });
        holder.btnCDelete.setOnClickListener(v ->
        {
            Retrofits.get().deleteCustomer(holder.mItem.getId()).enqueue(new Callback<Boolean>() {
                @Override
                public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                    Toast.makeText(context,"Cliente Borrado", Toast.LENGTH_LONG).show();
                    customers.remove(holder.mItem);
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
        return customers.size();
    }

    public void setCustomers(ArrayList<Customer> mValues) {
        this.customers = mValues;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private final CustomersFragment.CustomerInteractionListener mListener;
        View mView;
        @BindView(R.id.customerName) public TextView mCustomerName;
        @BindView(R.id.customerMail) public TextView mCustomerMail;
        @BindView(R.id.customerPhone) public TextView mCustomerPhone;
        @BindView(R.id.btnCDelete) public AppCompatButton btnCDelete;
        @BindView(R.id.btnMail) public AppCompatButton btnMail;
        public Customer mItem;

        public ViewHolder(CustomersFragment.CustomerInteractionListener mListener, View view) {
            super(view);
            this.mListener = mListener;
            mView = view;
            ButterKnife.bind(this, view);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mCustomerName.getText() + "'";
        }


    }
}
