package pythonteam.com.ventasapp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;

import pythonteam.com.ventasapp.util.SharedPreferencesManager;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        Context mContext = getBaseContext();
        FirebaseMessaging.getInstance().subscribeToTopic("all")
                .addOnCompleteListener(task -> Toast.makeText(mContext, "Subscrito a firebase",Toast.LENGTH_LONG).show());
        String token = SharedPreferencesManager.read(SharedPreferencesManager.TOKEN, "");
        if (!token.equals("")) {
            mContext.startActivity(new Intent(mContext, MainActivity.class));
            finish();
        } else {
            mContext.startActivity(new Intent(mContext, LoginActivity.class));
            finish();
        }
    }
}
