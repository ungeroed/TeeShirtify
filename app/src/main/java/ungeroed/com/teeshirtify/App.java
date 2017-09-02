package ungeroed.com.teeshirtify;

import android.app.Activity;
import android.app.Application;
import android.app.Fragment;

import javax.inject.Inject;

import dagger.AppComponent;
import dagger.AppModule;
import dagger.DaggerAppComponent;
import dagger.android.DispatchingAndroidInjector;
import dagger.android.HasDispatchingActivityInjector;
import dagger.android.HasDispatchingFragmentInjector;

/**
 * Created by Morten on 01/09/2017.
 */

public class App extends Application implements HasDispatchingActivityInjector, HasDispatchingFragmentInjector {
    @Inject
    DispatchingAndroidInjector<Activity> dispatchingAndroidInjector;

    @Inject
    DispatchingAndroidInjector<Fragment> dispatchingFragmentInjector;

    @Override
    public void onCreate() {
        super.onCreate();
        appComponent = initDagger(this);
        appComponent.inject(this);
    }

    private AppComponent appComponent;

    public AppComponent getAppComponent() {
        return appComponent;
    }

    protected AppComponent initDagger(App application) {
        return DaggerAppComponent.create();
    }

    @Override
    public DispatchingAndroidInjector<Activity> activityInjector() {
        return dispatchingAndroidInjector;
    }

    @Override
    public DispatchingAndroidInjector<Fragment> fragmentInjector() {
        return dispatchingFragmentInjector;
    }
}
