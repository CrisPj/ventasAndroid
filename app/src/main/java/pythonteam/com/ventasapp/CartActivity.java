package pythonteam.com.ventasapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.birbit.android.jobqueue.JobManager;

import java.io.IOException;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import pythonteam.com.ventasapp.models.Customer;
import pythonteam.com.ventasapp.models.Order;
import pythonteam.com.ventasapp.util.Retrofits;
import pythonteam.com.ventasapp.util.SharedPreferencesManager;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CartActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    ArrayList<Customer> customer_list = new ArrayList<>();
    int custID;

    @BindView(R.id.customer_select) Spinner customer;
    @BindView(R.id.btn_create_basket) Button btn_create;
    private JobManager jobManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);
        ButterKnife.bind(this);
        jobManager = VentasApp.getInstance().getJobManager();


        customer.setOnItemSelectedListener(this);

        Retrofits.get().getCustomers().enqueue(new Callback<ArrayList<Customer>>() {
            @Override
            public void onResponse(Call<ArrayList<Customer>> call, Response<ArrayList<Customer>> response) {
                if(response.isSuccessful()){
                    customer_list = response.body();
                    ArrayList<String> list_names = new ArrayList<>();
                    for (int i=0; i<customer_list.size(); i++){
                        list_names.add(customer_list.get(i).getName());
                    }
                    customer.setAdapter(new ArrayAdapter<>(getApplication(), android.R.layout.simple_spinner_dropdown_item, list_names));
                }else{
                    Toast.makeText(getBaseContext(), "Response error", Toast.LENGTH_LONG).show();
                }
            }
            @Override
            public void onFailure(Call<ArrayList<Customer>> call, Throwable t) {
                Toast.makeText(getBaseContext(), "Server error", Toast.LENGTH_LONG).show();
            }
        });

        btn_create.setOnClickListener(v -> {
            if( DataApplication.carrito.getProductList().size()> 0 ){
                DataApplication.carrito.setCustomerId(custID);
                DataApplication.carrito.setEmployeeId(SharedPreferencesManager.read(SharedPreferencesManager.USER_ID,-1));
                createOrder();
            }
        });

    }

    private void createOrder(  ){
        Retrofits.get().createOrder( DataApplication.carrito).enqueue(new Callback<Boolean>() {
            @Override
            public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                if(response.isSuccessful()){
                    Toast.makeText(getApplicationContext(), "Orden creada", Toast.LENGTH_LONG).show();
                    DataApplication.carrito = new Order();
                    finish();
                }else if (response.code()==504)
                {
                    jobManager.addJobInBackground(new CartJob(DataApplication.carrito));
                    Toast.makeText(getApplicationContext(), "Orden en espera", Toast.LENGTH_LONG).show();
                    DataApplication.carrito = new Order();
                    finish();
                }
                else{

                    Toast.makeText(getApplicationContext(), "Error al crear orden", Toast.LENGTH_LONG).show();
                    finish();
                }
            }
            @Override
            public void onFailure(Call<Boolean> call, Throwable t) {
                if (t instanceof IOException)
                {
                    jobManager.addJobInBackground(new CartJob(DataApplication.carrito));
                    DataApplication.carrito = new Order();
                }
                Toast.makeText(getApplicationContext(), "Server error, intente más tarde.", Toast.LENGTH_LONG).show();

                finish();
            }
        });
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

        custID = customer_list.get(position).getId();

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}