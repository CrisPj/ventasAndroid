package pythonteam.com.ventasapp;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.widget.Toast;

import com.birbit.android.jobqueue.Job;
import com.birbit.android.jobqueue.Params;
import com.birbit.android.jobqueue.RetryConstraint;

import pythonteam.com.ventasapp.models.Order;
import pythonteam.com.ventasapp.util.Retrofits;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CartJob extends Job {
    public static final int PRIORITY = 500;
    private final Order order;

    public CartJob(Order order) {
        super(new Params(PRIORITY).requireNetwork().persist());
        this.order = order;
    }

    @Override
    public void onRun() throws Throwable {
        Retrofits.get().createOrder(order).enqueue(new Callback<Boolean>() {
            @Override
            public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                Toast.makeText(getApplicationContext(), "Se creo orden offline",Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFailure(Call<Boolean> call, Throwable t) {
            }
        });
    }

    @Override
    protected void onCancel(int cancelReason, @Nullable Throwable throwable) {

    }

    @Override
    protected RetryConstraint shouldReRunOnThrowable(@NonNull Throwable throwable, int runCount, int maxRunCount) {
        return null;
    }


    @Override
    public void onAdded() {}
}
