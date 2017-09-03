package dagger.FeatureSubComponent;

import dagger.Subcomponent;
import dagger.android.AndroidInjector;
import ungeroed.com.teeshirtify.ShirtDetailsFragment;


/**
 * Created by Morten on 03/09/2017.
 */

@Subcomponent
public interface ShirtDetailsFragmentSubComponent extends AndroidInjector<ShirtDetailsFragment> {
    @Subcomponent.Builder
    public abstract class Builder extends AndroidInjector.Builder<ShirtDetailsFragment> {}
}
