package pythonteam.com.ventasapp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.birbit.android.jobqueue.JobManager;

import java.util.ArrayList;

import pythonteam.com.ventasapp.models.Product;
import pythonteam.com.ventasapp.util.Retrofits;
import pythonteam.com.ventasapp.util.SharedPreferencesManager;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

public class ProductsFragment extends Fragment {


    private ProductInteractionListener mListener;
    ArrayList<Product> mValues = new ArrayList<>();
    AppCompatEditText dialogName;
    AppCompatEditText dialogStock;
    AppCompatEditText dialogDesc;
    AppCompatEditText dialogPrice;
    private RecyclerView recyclerView;
    ProductsRecyclerViewAdapter mAdapter;
    private ProgressBar progressContainer;
    private JobManager jobManager;


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mListener = new ProductInteractionListener();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        jobManager = VentasApp.getInstance().getJobManager();
        Retrofits.get().getProducts().enqueue(new Callback<ArrayList<Product>>() {
            @Override
            public void onResponse(Call<ArrayList<Product>> call, Response<ArrayList<Product>> response) {
                if (response.isSuccessful()) {
                    mValues = response.body();
                    mAdapter = new ProductsRecyclerViewAdapter(mValues, mListener, getContext());
                    recyclerView.setAdapter(mAdapter);
                    mAdapter.notifyDataSetChanged();
                    progressContainer.setVisibility(View.GONE);
                }
                else if (response.code()==401)
                {
                    SharedPreferencesManager.write(SharedPreferencesManager.TOKEN,"");
                    Toast.makeText(getContext(), "Token expirado", Toast.LENGTH_LONG).show();
                    getContext().startActivity(new Intent(getContext(), LoginActivity.class));
                    getActivity().finish();
                }
            }

            @Override
            public void onFailure(Call<ArrayList<Product>> call, Throwable t) {
                Timber.d(t);
                progressContainer.setVisibility(View.GONE);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mAdapter != null ) {
            progressContainer.setVisibility(View.VISIBLE);
            Retrofits.get().getProducts().enqueue(new Callback<ArrayList<Product>>() {
                @Override
                public void onResponse(Call<ArrayList<Product>> call, Response<ArrayList<Product>> response) {
                    if (response.isSuccessful()) {
                        mValues = response.body();
                        mAdapter.notifyDataSetChanged();
                        recyclerView.setAdapter(mAdapter);
                        progressContainer.setVisibility(View.GONE);
                    }
                }

                @Override
                public void onFailure(Call<ArrayList<Product>> call, Throwable t) {
                    Timber.d(t);
                    progressContainer.setVisibility(View.GONE);
                }
            });
        }
    }

    public interface OnListFragmentInteractionListener {
        void onListFragmentInteraction(Product product);
    }

    public class ProductInteractionListener implements ProductsFragment.OnListFragmentInteractionListener {
        @Override
        public void onListFragmentInteraction(Product product) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            LayoutInflater inflater = getActivity().getLayoutInflater();

            View ve = inflater.inflate(R.layout.dialog_product, null);
            AppCompatEditText dialogName = ve.findViewById(R.id.dialogName);
            AppCompatEditText dialogDesc = ve.findViewById(R.id.dialogDesc);
            AppCompatEditText dialogPrice = ve.findViewById(R.id.dialogPrice);
            AppCompatEditText dialogStock = ve.findViewById(R.id.dialogStock);
            AppCompatEditText dialogAval = ve.findViewById(R.id.dialogAval);

            dialogName.setText(product.getName());
            dialogDesc.setText(product.getDescription());
            dialogPrice.setText(String.format("%s", product.getPrice()));
            dialogStock.setText(String.format("%s", product.getStock()));
            dialogAval.setText(String.format("%s", product.getAvailable()));
            builder.setView(ve)
                    // Add action buttons
                    .setPositiveButton("Actualizar", (dialog, id) -> {
                        product.setName(dialogName.getText().toString());
                        product.setDescription(dialogDesc.getText().toString());
                        product.setPrice(Double.parseDouble(dialogPrice.getText().toString()));
                        product.setStock(Integer.parseInt(dialogStock.getText().toString()));
                        product.setAvailable(Integer.parseInt(dialogAval.getText().toString()));
                        Retrofits.get().updateProduct(product).enqueue(new Callback<Product>() {
                            @Override
                            public void onResponse(Call<Product> call, Response<Product> response) {
                                if (response.isSuccessful()) {

                                    mAdapter.notifyDataSetChanged();
                                }
                                dialog.cancel();
                            }

                            @Override
                            public void onFailure(Call<Product> call, Throwable t) {
                                Timber.d(t);
                            }
                        });
                    })
                    .setNegativeButton("Cancelar", (dialog, id) -> dialog.cancel());
            AlertDialog dialog = builder.create();
            dialog.show();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_products, container, false);
        progressContainer = view.findViewById(R.id.progressSpinner);
        recyclerView = view.findViewById(R.id.listEmployees);
        return view;
    }

    public void newProduct() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();


        View ve = inflater.inflate(R.layout.dialog_product, null);
        dialogStock = ve.findViewById(R.id.dialogStock);
        dialogName = ve.findViewById(R.id.dialogName);
        dialogDesc = ve.findViewById(R.id.dialogDesc);
        dialogPrice = ve.findViewById(R.id.dialogPrice);

        Product product = new Product();
        builder.setView(ve)
                // Add action buttons
                .setPositiveButton("Guardar", (dialog, id) -> {
                    product.setName(dialogName.getText().toString());
                    product.setDescription(dialogDesc.getText().toString());
                    product.setPrice(Double.parseDouble(dialogPrice.getText().toString()));
                    product.setStock(Integer.parseInt(dialogStock.getText().toString()));
                    Retrofits.get().createProduct(product).enqueue(new Callback<Boolean>() {
                        @Override
                        public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                            if (response.isSuccessful()) {
                                Toast.makeText(getContext(), "Producto creado", Toast.LENGTH_LONG).show();
                                mValues.add(product);
                                mAdapter.setProducts(mValues);
                                mAdapter.notifyDataSetChanged();
                            }
                            else if (response.code()==504)
                            {
                                jobManager.addJobInBackground(new ProductJob(product));
                                Toast.makeText(getContext(), "Producto en espera", Toast.LENGTH_LONG).show();
                            }
                            else if (response.code()==401)
                            {
                                Toast.makeText(getContext(), "Token expirado", Toast.LENGTH_LONG).show();
                                getContext().startActivity(new Intent(getContext(), LoginActivity.class));
                                getActivity().finish();
                            }
                            else{
                                Toast.makeText(getContext(), "Error al crear orden", Toast.LENGTH_LONG).show();
                            }
                            dialog.cancel();
                        }

                        @Override
                        public void onFailure(Call<Boolean> call, Throwable t) {
                            Timber.d(t);
                        }
                    });
                })
                .setNegativeButton("Cancelar", (dialog, id) -> dialog.cancel());
        AlertDialog dialog = builder.create();
        dialog.show();
    }


}

