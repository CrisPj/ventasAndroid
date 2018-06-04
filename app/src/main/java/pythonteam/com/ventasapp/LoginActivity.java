package pythonteam.com.ventasapp;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import pythonteam.com.ventasapp.api.LoginRequest;
import pythonteam.com.ventasapp.util.Retrofits;
import pythonteam.com.ventasapp.util.SharedPreferencesManager;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

public class LoginActivity extends AppCompatActivity
{

    @BindView(R.id.et_email)
    TextInputEditText email;

    @BindView(R.id.et_password)
    TextInputEditText password;


    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mContext = getApplicationContext();
        ButterKnife.bind(this);
    }


    @OnClick(R.id.btn_login)
    void oncli(){
        final ProgressDialog progressDialog = new ProgressDialog(LoginActivity.this,
                R.style.Theme_AppCompat_DayNight_Dialog_Alert);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Authenticating...");
        progressDialog.show();
        Retrofits.get().getUser(new LoginRequest(email.getText().toString(),password.getText().toString())).enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                progressDialog.dismiss();
                if (response.isSuccessful())
                {
                    SharedPreferencesManager.write(SharedPreferencesManager.TOKEN, response.body().getToken());
                    SharedPreferencesManager.write(SharedPreferencesManager.USER_ID, response.body().getId());
                    mContext.startActivity(new Intent(getBaseContext(), MainActivity.class));

                }else {
                    Toast.makeText(mContext,"Datos incorrectos",Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                progressDialog.dismiss();
                Timber.d(t);
                Toast.makeText(getBaseContext(),"No hay conexion a interne",Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }

    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }
}
