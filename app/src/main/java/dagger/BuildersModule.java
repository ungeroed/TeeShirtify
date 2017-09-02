package dagger;

import android.app.Activity;
import android.app.Fragment;

import dagger.FeatureSubComponent.NavigationActivitySubComponent;
import dagger.FeatureSubComponent.ShirtFragmentSubComponent;
import dagger.android.ActivityKey;
import dagger.android.AndroidInjector;
import dagger.android.FragmentKey;
import dagger.multibindings.IntoMap;
import ungeroed.com.teeshirtify.NavigationActivity;
import ungeroed.com.teeshirtify.ShirtFragment;

/**
 * Created by Morten on 01/09/2017.
 */

@Module
public abstract class BuildersModule {
    @Binds
    @IntoMap
    @ActivityKey(NavigationActivity.class)
    abstract AndroidInjector.Factory<? extends Activity> bindNavigationActivityInjectorFactory(NavigationActivitySubComponent.Builder builder);
// Add more bindings here for other sub components
    @Binds
    @IntoMap
    @FragmentKey(ShirtFragment.class)
    abstract AndroidInjector.Factory<? extends Fragment> bindShirtFragmentInjectorFactory(ShirtFragmentSubComponent.Builder builder);

}
