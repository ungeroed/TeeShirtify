package dagger.FeatureSubComponent;

import dagger.Subcomponent;
import dagger.android.AndroidInjector;
import ungeroed.com.teeshirtify.MyShirtRecyclerViewAdapter;
import ungeroed.com.teeshirtify.NavigationActivity;

/**
 * Created by Morten on 01/09/2017.
 */
@Subcomponent
public interface NavigationActivitySubComponent extends AndroidInjector<NavigationActivity> {
    @Subcomponent.Builder
    abstract class Builder extends AndroidInjector.Builder<NavigationActivity> {
    }
}