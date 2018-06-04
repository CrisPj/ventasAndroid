package pythonteam.com.ventasapp;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import pythonteam.com.ventasapp.adapter.OrdersAdapter;
import pythonteam.com.ventasapp.models.Customer;
import pythonteam.com.ventasapp.models.Order;
import pythonteam.com.ventasapp.util.Retrofits;
import pythonteam.com.ventasapp.util.SharedPreferencesManager;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

public class OrdersFragment extends Fragment {

    private RecyclerView recyclerView;
    OrdersAdapter mAdapter;
    private ProgressBar progressContainer;
    private OrderInteractionListener mListener;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Retrofits.get().getOrders().enqueue(new Callback<ArrayList<Order>>() {
            @Override
            public void onResponse(Call<ArrayList<Order>> call, Response<ArrayList<Order>> response) {
                if (response.isSuccessful())
                {
                    mValues = response.body();
                    mAdapter = new OrdersAdapter(mValues, mListener, getContext());
                    recyclerView.setAdapter(mAdapter);
                    mAdapter.notifyDataSetChanged();
                    progressContainer.setVisibility(View.GONE);
                }
                else if (response.code()==401)
                {
                    Toast.makeText(getContext(), "Token expirado", Toast.LENGTH_LONG).show();
                    getContext().startActivity(new Intent(getContext(), LoginActivity.class));
                    getActivity().finish();
                }
            }

            @Override
            public void onFailure(Call<ArrayList<Order>> call, Throwable t) {
                Timber.d(t);
            }
        });
    }

    ArrayList<Order> mValues = new ArrayList<>();
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_orders, container, false);

        recyclerView = (RecyclerView) view.findViewById(R.id.listEmployees);
        progressContainer = view.findViewById(R.id.progressSpinner);
        return view;
    }
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mListener = new OrderInteractionListener();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mAdapter != null)
        {
            progressContainer.setVisibility(View.VISIBLE);
            Retrofits.get().getOrders().enqueue(new Callback<ArrayList<Order>>() {
                @Override
                public void onResponse(Call<ArrayList<Order>> call, Response<ArrayList<Order>> response) {
                    if (response.isSuccessful()) {
                        mValues = response.body();
                        mAdapter = new OrdersAdapter(mValues,mListener,getContext());
                        recyclerView.setAdapter(mAdapter);
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
                public void onFailure(Call<ArrayList<Order>> call, Throwable t) {
                    Timber.d(t);
                    progressContainer.setVisibility(View.GONE);
                }
            });
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public void newOrder() {
    }

    public interface OnListFragmentInteractionListener {
        void onListFragmentInteraction(Order order);
    }

    public class OrderInteractionListener implements OrdersFragment.OnListFragmentInteractionListener {

        @Override
        public void onListFragmentInteraction(Order order) {
            Intent i = new Intent(getActivity(), OrderActivity.class);
            i.putExtra("orderID", Integer.toString(order.getOrderId()));
            startActivity(i);
        }
    }

}
