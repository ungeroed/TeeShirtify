package dagger.FeatureSubComponent;

import dagger.Subcomponent;
import dagger.android.AndroidInjector;
import ungeroed.com.teeshirtify.CheckoutFragment;

/**
 * Created by Morten on 03/09/2017.
 */

@Subcomponent
public interface CheckoutFragmentSubComponent extends AndroidInjector<CheckoutFragment> {
    @Subcomponent.Builder
    public abstract class Builder extends AndroidInjector.Builder<CheckoutFragment> {}
}
