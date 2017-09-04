package dagger;

import android.app.Application;
import android.content.Context;
import javax.inject.Singleton;

import dagger.FeatureSubComponent.CheckoutFragmentSubComponent;
import dagger.FeatureSubComponent.NavigationActivitySubComponent;
import dagger.FeatureSubComponent.ShirtDetailsFragmentSubComponent;
import dagger.FeatureSubComponent.ShirtFragmentSubComponent;
import dagger.Test.TestComponent;


/**
 * Created by Morten on 01/09/2017.
 */
@Module (subcomponents = { NavigationActivitySubComponent.class, ShirtFragmentSubComponent.class,
        ShirtDetailsFragmentSubComponent.class, CheckoutFragmentSubComponent.class})
public class AppModule {

    private Application application;

    public AppModule(Application application) {
        this.application = application;
    }

    @Provides
    @Singleton
    public Context provideContext() {
        return application;
    }

}
