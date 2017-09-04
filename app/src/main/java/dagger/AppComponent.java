package dagger;

import javax.inject.Singleton;
import dagger.android.AndroidInjectionModule;
import ungeroed.com.teeshirtify.ApiHandler;
import ungeroed.com.teeshirtify.ApiHandlerConsumer;
import ungeroed.com.teeshirtify.App;
import ungeroed.com.teeshirtify.CheckoutFragment;
import ungeroed.com.teeshirtify.MyShirtRecyclerViewAdapter;
import ungeroed.com.teeshirtify.ShirtDetailsFragment;
import ungeroed.com.teeshirtify.ShirtFragment;

/**
 * Main application daggercomponent.
 */

@Singleton
@Component(modules = {AppModule.class,  ApiHandlerModule.class, AndroidInjectionModule.class,
        BuildersModule.class})
public interface AppComponent {
    void inject(App app);
    void inject(ShirtFragment target);
    void inject(ShirtDetailsFragment target);
    void inject(CheckoutFragment target);
    void inject(MyShirtRecyclerViewAdapter target);
    void inject(ApiHandler target);
    void inject(ApiHandlerConsumer target);
}
