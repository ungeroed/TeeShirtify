package dagger;


import ungeroed.com.teeshirtify.NavigationActivity;
import ungeroed.com.teeshirtify.ShirtFragment;

/**
 * Created by Morten on 02/09/2017.
 */
@Module
public abstract class OnFragmentInteractionListenerModule {

    @Binds
    public abstract ShirtFragment.OnListFragmentInteractionListener onListFragmentInteractionListener(NavigationActivity navigationActivity);

}
