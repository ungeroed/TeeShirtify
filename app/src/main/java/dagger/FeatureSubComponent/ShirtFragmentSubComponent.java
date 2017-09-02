package dagger.FeatureSubComponent;

import android.app.Fragment;

import dagger.Subcomponent;
import dagger.android.AndroidInjector;
import ungeroed.com.teeshirtify.ShirtFragment;

/**
 * Created by Morten on 01/09/2017.
 */
@Subcomponent(modules = {})
public interface ShirtFragmentSubComponent extends AndroidInjector<ShirtFragment> {
    @Subcomponent.Builder
    public abstract class Builder extends AndroidInjector.Builder<ShirtFragment> {}
}
