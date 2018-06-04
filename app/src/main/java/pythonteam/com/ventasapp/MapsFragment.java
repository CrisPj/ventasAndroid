package pythonteam.com.ventasapp;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.Objects;

import pythonteam.com.ventasapp.models.Customer;
import pythonteam.com.ventasapp.models.Route;
import pythonteam.com.ventasapp.util.Retrofits;
import pythonteam.com.ventasapp.util.SharedPreferencesManager;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

public class MapsFragment extends Fragment implements OnMapReadyCallback {

    MapView mMapView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.activity_maps, container, false);
        mMapView = (MapView) root.findViewById(R.id.mapView);
        mMapView.onCreate(savedInstanceState);
        mMapView.onResume();
        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }
        mMapView.getMapAsync(this);

        return root;


    }


    @Override
    public void onMapReady(GoogleMap googleMap) {

        if (ActivityCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (ContextCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            return;
        }
        googleMap.setMyLocationEnabled(true);
        Retrofits.get().getEmployeeRoute(SharedPreferencesManager.read(SharedPreferencesManager.USER_ID,-1)).enqueue(new Callback<ArrayList<Route>>() {
            @Override
            public void onResponse(Call<ArrayList<Route>> call, Response<ArrayList<Route>> response) {
                if (response.isSuccessful())
                {
                    for (Route r : Objects.requireNonNull(response.body())) {
                         googleMap.addMarker(new MarkerOptions().position(new LatLng(r.getLatLong().getX(),r.getLatLong().getY())).title(r.getCustomerName()).snippet(r.getIdRoute()+""));
                    }
                }
                final double latitude = 20.527961;
                final double longitude = -100.811288;
                CameraPosition cameraPosition = new CameraPosition.Builder().target(new LatLng(latitude,longitude)).zoom(12).build();
                googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
            }

            @Override
            public void onFailure(Call<ArrayList<Route>> call, Throwable t) {
                Timber.d(t);
            }
        });
    }

    public void newRoute() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View ve = inflater.inflate(R.layout.dialog_route, null);
        EditText dialogRoute = ve.findViewById(R.id.dialogRoute);
        Spinner dialogCliente = ve.findViewById(R.id.dialogCliente);
        Retrofits.get().getAvalCustomers().enqueue(new Callback<ArrayList<Customer>>() {
            @Override
            public void onResponse(Call<ArrayList<Customer>> call, Response<ArrayList<Customer>> response) {
                if (response.isSuccessful())
                    dialogCliente.setAdapter(new ArrayAdapter<Customer>(getActivity(), android.R.layout.simple_spinner_dropdown_item, response.body()));
            }

            @Override
            public void onFailure(Call<ArrayList<Customer>> call, Throwable t) {
                Timber.d(t);
            }
        });

        builder.setView(ve)
                .setPositiveButton("Guardar", (dialog, id) -> {
                    Route r = new Route();
                    Customer select = ((Customer) dialogCliente.getSelectedItem());
                    if (select == null )
                    {
                        Toast.makeText(getContext(),"No hay clientes disponibles",Toast.LENGTH_LONG).show();
                        dialog.cancel();
                    }
                    else if (dialogRoute.getText().toString().equals(""))
                    {
                        Toast.makeText(getContext(),"Debe definir un id de ruta", Toast.LENGTH_LONG).show();
                        dialog.cancel();
                    }
                    else {
                        r.setIdCustomer(select.getId());
                        r.setIdEmployee(SharedPreferencesManager.read(SharedPreferencesManager.USER_ID, -1));
                        r.setIdRoute(Integer.parseInt(dialogRoute.getText().toString()));
                        Retrofits.get().createRoute(r).enqueue(new Callback<Boolean>() {
                            @Override
                            public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                                Toast.makeText(getContext(), "Ruta creada correctamente", Toast.LENGTH_LONG).show();

                            }

                            @Override
                            public void onFailure(Call<Boolean> call, Throwable t) {
                                Timber.d(t);
                            }
                        });
                        dialog.cancel();
                    }
                })
                .setNegativeButton("Cerrar",(dialog, id) -> dialog.cancel());
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}
