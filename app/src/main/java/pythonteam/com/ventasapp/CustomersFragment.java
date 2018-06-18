package pythonteam.com.ventasapp;


import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.util.ArrayList;

import pythonteam.com.ventasapp.adapter.CustomersAdapter;
import pythonteam.com.ventasapp.models.Customer;
import pythonteam.com.ventasapp.models.Latlong;
import pythonteam.com.ventasapp.util.Retrofits;
import pythonteam.com.ventasapp.util.SharedPreferencesManager;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

public class CustomersFragment extends Fragment {

    private CustomerInteractionListener mListener;
    AppCompatEditText dialogName;
    AppCompatEditText dialogPhone;
    AppCompatEditText dialogEmail;
    AppCompatTextView dialogLat;
    AppCompatButton btnDialogUpdate;
    LocationManager mLocationManager;
    private ProgressBar progressContainer;
    private RecyclerView recyclerView;
    CustomersAdapter mAdapter;


    public CustomersFragment() {

    }

    ArrayList<Customer> mValues = new ArrayList<>();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Retrofits.get().getCustomers().enqueue(new Callback<ArrayList<Customer>>() {
            @Override
            public void onResponse(Call<ArrayList<Customer>> call, Response<ArrayList<Customer>> response) {
                if (response.isSuccessful()) {
                    mValues = response.body();
                    mAdapter = new CustomersAdapter(mValues, mListener, getContext());
                    recyclerView.setAdapter(mAdapter);
                    mAdapter.notifyDataSetChanged();
                    progressContainer.setVisibility(View.GONE);
                } else
                    Timber.d("Hubo un error al procesar lo solicitado");
            }

            @Override
            public void onFailure(Call<ArrayList<Customer>> call, Throwable t) {
                Timber.d(t);
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_customers, container, false);
        progressContainer = view.findViewById(R.id.progressSpinner);
        mLocationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);



        Context context = view.getContext();
        recyclerView = (RecyclerView) view.findViewById(R.id.list);
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mListener = new CustomerInteractionListener();
    }

    @Override
    public void onDetach() {
        mListener = null;
        super.onDetach();

    }

    public void newCustomer() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();

        View ve = inflater.inflate(R.layout.dialog_customer, null);
        dialogName = ve.findViewById(R.id.dialogName);
        dialogPhone = ve.findViewById(R.id.dialogPhone);
        dialogEmail = ve.findViewById(R.id.dialogEmail);
        dialogLat = ve.findViewById(R.id.dialogLat);
        btnDialogUpdate = ve.findViewById(R.id.btnDialogUpdate);
        Customer c = new Customer();
        Latlong latlong = new Latlong();
        latlong.setX(20.541397);
        latlong.setY(-100.816643);
        c.setLatlong(latlong);
        DataApplication.tempLatlong = latlong;
        btnDialogUpdate.setOnClickListener(i -> {
            Intent intMap = new Intent(getContext(), MapsActivity.class);
            intMap.putExtra("actualLat", DataApplication.tempLatlong.getX());
            intMap.putExtra("actualLong", DataApplication.tempLatlong.getY());
            startActivity(intMap);
        });
        String lat = "X: " + c.getLatlong().getX() + " Y :" + c.getLatlong().getY();
        dialogLat.setText(lat);
        builder.setView(ve)
                // Add action buttons
                .setPositiveButton("Guardar", (dialog, id) -> {
                    c.setName(dialogName.getText().toString());
                    c.setPhone(dialogPhone.getText().toString());
                    c.setEmail(dialogEmail.getText().toString());
                    Retrofits.get().createCustomer(c).enqueue(new Callback<Boolean>() {
                        @Override
                        public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                            if (response.isSuccessful())
                            {
                                Toast.makeText(getContext(), "Cliente creado", Toast.LENGTH_LONG).show();
                                mValues.add(c);
                                mAdapter.setCustomers(mValues);
                                mAdapter.notifyDataSetChanged();
                            }
                            else if (response.code()==401)
                            {
                                SharedPreferencesManager.write(SharedPreferencesManager.TOKEN,"");
                                Toast.makeText(getContext(), "Token expirado", Toast.LENGTH_LONG).show();
                                getContext().startActivity(new Intent(getContext(), LoginActivity.class));
                                getActivity().finish();
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

    @Override
    public void onResume() {
        super.onResume();
        if(mAdapter != null) {
            Retrofits.get().getCustomers().enqueue(new Callback<ArrayList<Customer>>() {
                @Override
                public void onResponse(Call<ArrayList<Customer>> call, Response<ArrayList<Customer>> response) {
                    if (response.isSuccessful()) {
                        mValues = response.body();
                        mAdapter = new CustomersAdapter(mValues, mListener, getContext());
                        recyclerView.setAdapter(mAdapter);
                        mAdapter.notifyDataSetChanged();
                        progressContainer.setVisibility(View.GONE);
                    } else
                        Timber.d("Hubo un error al procesar lo solicitado");
                }

                @Override
                public void onFailure(Call<ArrayList<Customer>> call, Throwable t) {
                    Timber.d(t);
                }
            });
        }
    }

    public interface OnListFragmentInteractionListener {
        void onListFragmentInteraction(Customer customer);
    }

    public class CustomerInteractionListener implements OnListFragmentInteractionListener {
        @Override
        public void onListFragmentInteraction(Customer customer) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            LayoutInflater inflater = getActivity().getLayoutInflater();

            DataApplication.tempLatlong = customer.getLatlong();

            View ve = inflater.inflate(R.layout.dialog_customer, null);
            dialogName = ve.findViewById(R.id.dialogName);
            dialogPhone = ve.findViewById(R.id.dialogPhone);
            dialogEmail = ve.findViewById(R.id.dialogEmail);
            dialogLat = ve.findViewById(R.id.dialogLat);
            dialogName.setText(customer.getName());
            dialogPhone.setText(customer.getPhone());
            dialogEmail.setText(customer.getEmail());
            btnDialogUpdate = ve.findViewById(R.id.btnDialogUpdate);
            btnDialogUpdate.setOnClickListener(i -> {
                Intent intMap = new Intent(getContext(), MapsActivity.class);
                intMap.putExtra("actualLat", DataApplication.tempLatlong.getX());
                intMap.putExtra("actualLong", DataApplication.tempLatlong.getY());
                startActivity(intMap);
            });
            String lat = "X: " + customer.getLatlong().getX() + " Y :" + customer.getLatlong().getY();
            dialogLat.setText(lat);
            builder.setView(ve)
                    // Add action buttons
                    .setPositiveButton("Actualizar", (dialog, id) -> {
                        customer.setName(dialogName.getText().toString());
                        customer.setPhone(dialogPhone.getText().toString());
                        customer.setEmail(dialogEmail.getText().toString());
                        customer.setLatlong(DataApplication.tempLatlong);
                        Retrofits.get().updateCustomer(customer).enqueue(new Callback<Customer>() {
                            @Override
                            public void onResponse(Call<Customer> call, Response<Customer> response) {
                                if (response.isSuccessful())
                                {
                                    mAdapter.notifyDataSetChanged();
                                }
                                dialog.cancel();
                            }

                            @Override
                            public void onFailure(Call<Customer> call, Throwable t) {
                                Timber.d(t);
                            }
                        });
                    })
                    .setNegativeButton("Cancelar", (dialog, id) -> dialog.cancel());
            AlertDialog dialog = builder.create();
            dialog.show();
        }
    }



}
