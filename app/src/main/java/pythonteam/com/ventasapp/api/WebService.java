package pythonteam.com.ventasapp.api;

import java.util.ArrayList;

import pythonteam.com.ventasapp.User;
import pythonteam.com.ventasapp.models.Customer;
import pythonteam.com.ventasapp.models.Order;
import pythonteam.com.ventasapp.models.Product;
import pythonteam.com.ventasapp.models.Route;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;


public interface WebService {

    @POST("login")
    Call<User> getUser(@Body LoginRequest loginRequest);

    @POST("test")
    Call<User> testUser(@Body User user);

    //Customers
    @GET("customers")
    Call<ArrayList<Customer>> getCustomers();

    @GET("customers/{id}")
    Call<Customer> getCustomer(@Path("id") int id);

    @POST("customers")
    Call<Boolean> createCustomer(@Body Customer customer);

    @PUT("customers")
    Call<Customer> updateCustomer(@Body Customer customer);

    @DELETE("customers/{id}")
    Call<Boolean> deleteCustomer(@Path("id") int id);

    //Customers
    @GET("products")
    Call<ArrayList<Product>> getProducts();

    @GET("products/{id}")
    Call<Product> getProduct(@Path("id") int id);

    @POST("products")
    Call<Boolean> createProduct(@Body Product product);

    @PUT("products")
    Call<Product> updateProduct(@Body Product product);

    @DELETE("products/{id}")
    Call<Boolean> deleteProduct(@Path("id") int id);

    //Orders
    @GET("orders")
    Call<ArrayList<Order>> getOrders();

    @GET("orders/{id}")
    Call<Order> getOrder(@Path("id") int id);

    @POST("orders")
    Call<Boolean> createOrder(@Body Order order);

    @PUT("orders")
    Call<Order> updateOrder(@Body Order order);

    @DELETE("orders/{id}")
    Call<Boolean> deleteOrder(@Path("id") int id);

    @GET("users/routes/{id}")
    Call<ArrayList<Route>> getEmployeeRoute(@Path("id") int id);

    @POST("token")
    Call<Boolean> addToken(@Body String token);

    @GET("users/customers")
    Call<ArrayList<Customer>> getAvalCustomers();

    @POST("users/routes")
    Call<Boolean> createRoute(@Body Route route);

}
