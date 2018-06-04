package pythonteam.com.ventasapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;


import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import pythonteam.com.ventasapp.models.Customer;
import pythonteam.com.ventasapp.models.Order;
import pythonteam.com.ventasapp.models.Product;
import pythonteam.com.ventasapp.util.Retrofits;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrderActivity extends AppCompatActivity {

    Order order;
    String orderID;

    @BindView(R.id.title) TextView title;
    @BindView(R.id.noOrden) TextView noOrden;
    @BindView(R.id.customer) TextView customer;
    @BindView(R.id.status) TextView status;
    @BindView(R.id.orderDate) TextView orderDate;
    @BindView(R.id.product) TextView products;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);
        ButterKnife.bind(this);

        Bundle extras = getIntent().getExtras();
        orderID = extras.getString("orderID");

    }

    @Override
    protected void onResume() {
        super.onResume();

        fillData();

    }

    private void fillData() {
        Retrofits.get().getOrder(Integer.parseInt(orderID)).enqueue(new Callback<Order>() {
            @Override
            public void onResponse(Call<Order> call, Response<Order> response) {
                if(response.isSuccessful()){
                    order = response.body();

                    Retrofits.get().getCustomer( order.getCustomerId() ).enqueue(new Callback<Customer>() {
                        @Override
                        public void onResponse(Call<Customer> call, Response<Customer> response) {
                            if(response.isSuccessful()){

                                Customer cu = response.body();
                                title.setText("Orden de "+cu.getName());
                                noOrden.setText(order.getOrderId()+"");
                                customer.setText(order.getCustomerId()+" ( "+cu.getName()+" )");
                                if (order.isStatus()){
                                    status.setText("\uD83D\uDD35 Completado");
                                }else {
                                    status.setText("\uD83D\uDD34 No completado");
                                }
                                orderDate.setText(
                                        order.getOrderdate().getDayOfMonth()+"/"+
                                                order.getOrderdate().getMonthValue()+"/"+
                                                order.getOrderdate().getYear());
                                String productsList = "";
                                for( int i=0; i<order.getProductList().size(); i++){
                                    ArrayList<Product> productos = order.getProductList();
                                    productsList += "● "+productos.get(i).getName()+"\n";
                                }
                                products.setText(productsList);

                            }
                        }
                        @Override
                        public void onFailure(Call<Customer> call, Throwable t) {
                            Toast.makeText(getApplicationContext(), "Server error", Toast.LENGTH_LONG).show();
                        }
                    });

                }else{
                    Toast.makeText(getApplicationContext(), "Response error", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<Order> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "Server error", Toast.LENGTH_LONG).show();
            }
        });
    }

}