package pythonteam.com.ventasapp.fragments;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatTextView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import com.google.gson.Gson;

import butterknife.BindView;
import butterknife.ButterKnife;
import pythonteam.com.ventasapp.R;
import pythonteam.com.ventasapp.models.Customer;
import pythonteam.com.ventasapp.util.Retrofits;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

public class CustomerDialog extends DialogFragment {

    @BindView(R.id.dialogName) AppCompatEditText dialogName;
    @BindView(R.id.dialogPhone) AppCompatEditText dialogPhone;
    @BindView(R.id.dialogEmail) AppCompatEditText dialogEmail;
    @BindView(R.id.dialogLat)
    AppCompatTextView dialogLat;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();

        View v = inflater.inflate(R.layout.dialog_customer, null);
        ButterKnife.bind(this, v);
        Bundle b = getArguments();
        String json = b.getString("customer");

        Customer c = new Gson().fromJson(json,Customer.class);
        dialogName.setText(c.getName());
        dialogPhone.setText(c.getPhone());
        dialogEmail.setText(c.getEmail());
        String lat = "X: " + c.getLatlong().getX() + " Y :" + c.getLatlong().getY();
        dialogLat.setText(lat);
        builder.setView(v)
                // Add action buttons
                .setPositiveButton("Actualizar", (dialog, id) -> {
                    c.setName(dialogName.getText().toString());
                    c.setPhone(dialogPhone.getText().toString());
                    c.setEmail(dialogEmail.getText().toString());
                    Retrofits.get().updateCustomer(c).enqueue(new Callback<Customer>() {
                        @Override
                        public void onResponse(Call<Customer> call, Response<Customer> response) {
                            dialog.cancel();
                        }

                        @Override
                        public void onFailure(Call<Customer> call, Throwable t) {
                            Timber.d(t);
                        }
                    });
                })
                .setNegativeButton("Cancelar", (dialog, id) -> dialog.cancel());
        return builder.create();


    }
}
