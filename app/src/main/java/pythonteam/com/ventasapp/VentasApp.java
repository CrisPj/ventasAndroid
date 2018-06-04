package pythonteam.com.ventasapp;

import android.app.Application;

import com.birbit.android.jobqueue.JobManager;
import com.birbit.android.jobqueue.config.Configuration;
import com.birbit.android.jobqueue.scheduling.FrameworkJobSchedulerService;

import pythonteam.com.ventasapp.util.Retrofits;
import pythonteam.com.ventasapp.util.SharedPreferencesManager;
import timber.log.Timber;

public class VentasApp extends Application {
    private static VentasApp instance;
    JobManager jobManager;

    public VentasApp()
    {
        instance = this;
    }

    public synchronized JobManager getJobManager() {
        if (jobManager == null) {
            configureJobManager();
        }
        return jobManager;
    }

    private void configureJobManager() {
        Configuration.Builder builder = new Configuration.Builder(this)
                .minConsumerCount(1)//always keep at least one consumer alive
                .maxConsumerCount(3)//up to 3 consumers at a time
                .loadFactor(3)//3 jobs per consumer
                .consumerKeepAlive(120);//wait 2 minute
        builder.scheduler(FrameworkJobSchedulerService.createSchedulerFor(this,
                MyJobService.class), true);
        jobManager = new JobManager(builder.build());
    }

    @Override
    public void onCreate() {
        super.onCreate();
        getJobManager();
        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        }
        SharedPreferencesManager.init(this);
        Retrofits.init(this);

    }

    public static VentasApp getInstance() {
        return instance;
    }
}
