package dagger;

import android.app.Activity;
import android.app.Fragment;
import dagger.FeatureSubComponent.CheckoutFragmentSubComponent;
import dagger.FeatureSubComponent.NavigationActivitySubComponent;
import dagger.FeatureSubComponent.ShirtDetailsFragmentSubComponent;
import dagger.FeatureSubComponent.ShirtFragmentSubComponent;
import dagger.android.ActivityKey;
import dagger.android.AndroidInjector;
import dagger.android.FragmentKey;
import dagger.multibindings.IntoMap;
import ungeroed.com.teeshirtify.CheckoutFragment;
import ungeroed.com.teeshirtify.NavigationActivity;
import ungeroed.com.teeshirtify.ShirtDetailsFragment;
import ungeroed.com.teeshirtify.ShirtFragment;

/**
 * Builders module links the activity and fragment classes to their counterpart subcomponents
 */

@Module
public abstract class BuildersModule {
    @Binds
    @IntoMap
    @ActivityKey(NavigationActivity.class)
    abstract AndroidInjector.Factory<? extends Activity> bindNavigationActivityInjectorFactory(NavigationActivitySubComponent.Builder builder);

    @Binds
    @IntoMap
    @FragmentKey(ShirtFragment.class)
    abstract AndroidInjector.Factory<? extends Fragment> bindShirtFragmentInjectorFactory(ShirtFragmentSubComponent.Builder builder);

    @Binds
    @IntoMap
    @FragmentKey(ShirtDetailsFragment.class)
    abstract AndroidInjector.Factory<? extends Fragment> bindShirtDetailsFragmentInjectorFactory(ShirtDetailsFragmentSubComponent.Builder builder);

    @Binds
    @IntoMap
    @FragmentKey(CheckoutFragment.class)
    abstract AndroidInjector.Factory<? extends Fragment> bindCheckoutFragmentInjectorFactory(CheckoutFragmentSubComponent.Builder builder);

}
