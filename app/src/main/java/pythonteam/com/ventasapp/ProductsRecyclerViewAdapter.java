package pythonteam.com.ventasapp;

import android.content.Context;
import android.content.res.Configuration;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import pythonteam.com.ventasapp.models.Product;
import pythonteam.com.ventasapp.util.Retrofits;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

class ProductsRecyclerViewAdapter extends RecyclerView.Adapter<ProductsRecyclerViewAdapter.ViewHolder> {
    private final ProductsFragment.ProductInteractionListener mListener;
    private final Context context;
    private ArrayList<Product> products;

    public ProductsRecyclerViewAdapter(ArrayList<Product> mValues, ProductsFragment.ProductInteractionListener mListener, Context context) {
        this.products = mValues;
        this.mListener = mListener;
        this.context = context;
    }


    @NonNull
    @Override
    public ProductsRecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_product, parent, false);
        return new ProductsRecyclerViewAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductsRecyclerViewAdapter.ViewHolder holder, int position) {
        holder.mItem = products.get(position);
        holder.mContent1.setText(products.get(position).getName());
        holder.mContent2.setText(products.get(position).getDescription());
        String price = products.get(position).getPrice() + "";
        holder.mContent3.setText(price);
        holder.mStock.setText(new StringBuilder().append("").append(holder.mItem.getStock()).toString());
        holder.mAval.setText(new StringBuilder().append("").append(holder.mItem.getAvailable()).toString());
        holder.btnCarro.setOnClickListener(v-> {
            AlertDialog.Builder alert = new AlertDialog.Builder(context);
            alert.setTitle("Quantity");
            alert.setMessage("Enter Quantity");

            final EditText input = new EditText(context);
            input.setInputType(InputType.TYPE_CLASS_NUMBER);
            input.setRawInputType(Configuration.KEYBOARD_12KEY);
            alert.setView(input);
            alert.setPositiveButton("OK", (dialog, which) -> {
                int quantity = Integer.parseInt( input.getText().toString());
                holder.mItem.setQuantity(quantity);
                DataApplication.carrito.addProduct(holder.mItem);

            });

            alert.setNegativeButton("Cancel", (dialog, whichButton) -> {
                // do nothing
            });
            alert.show();
        });
        holder.mPDelete.setOnClickListener(v -> {
            holder.mPDelete.setEnabled(false);
            Retrofits.get().deleteProduct(holder.mItem.getId()).enqueue(new Callback<Boolean>() {
                @Override
                public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                    Toast.makeText(context,"Producto Borrado", Toast.LENGTH_LONG).show();
                    products.remove(position);
                    notifyDataSetChanged();
                }

                @Override
                public void onFailure(Call<Boolean> call, Throwable t) {
                    holder.mPDelete.setEnabled(true);
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
        return products.size();
    }

    public void setProducts(ArrayList<Product> mValues) {
        this.products = mValues;
        notifyDataSetChanged();
    }


    class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mContent1;
        public final TextView mContent2;
        public final TextView mContent3;
        public final TextView mStock;
        public final TextView mAval;
        public final ImageView mImage;
        public Product mItem;
        public ImageButton mPDelete;
        public ImageButton btnCarro;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mContent1 = view.findViewById(R.id.txtProductName);
            mContent2 = view.findViewById(R.id.txtProductDesc);
            mContent3 = view.findViewById(R.id.txtProductPrice);
            mStock = view.findViewById(R.id.txtProductStock);
            mAval = view.findViewById(R.id.txtProductAvailable);
            mImage = view.findViewById(R.id.productImage);
            mPDelete = view.findViewById(R.id.btnPDelete);
            btnCarro = view.findViewById(R.id.btnCarro);

        }

        @Override
        public String toString() {
            return super.toString() + " '" + mContent1.getText() + "'";
        }
    }
}
