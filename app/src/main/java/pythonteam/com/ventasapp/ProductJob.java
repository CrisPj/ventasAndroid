package pythonteam.com.ventasapp;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.widget.Toast;

import com.birbit.android.jobqueue.Job;
import com.birbit.android.jobqueue.Params;
import com.birbit.android.jobqueue.RetryConstraint;

import pythonteam.com.ventasapp.models.Order;
import pythonteam.com.ventasapp.models.Product;
import pythonteam.com.ventasapp.util.Retrofits;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProductJob extends Job {
    public static final int PRIORITY = 500;
    private final Product product;

    public ProductJob(Product product) {
        super(new Params(PRIORITY).requireNetwork().persist());
        this.product = product;
    }

    @Override
    public void onAdded() {

    }

    @Override
    public void onRun() throws Throwable {
        Retrofits.get().createProduct(product).enqueue(new Callback<Boolean>() {
            @Override
            public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                Toast.makeText(getApplicationContext(), "Se creo product offline",Toast.LENGTH_LONG).show();
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
}
