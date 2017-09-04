package dagger.Test;

import javax.inject.Singleton;

import dagger.ApiHandlerModule;
import dagger.Component;
import ungeroed.com.teeshirtify.ApiHandlerConsumer;

/**
 * Created by Morten on 03/09/2017.
 */

@Singleton
@Component(modules = ApiHandlerModule.class)
public interface TestComponent {
    ApiHandlerConsumer apiHandlerConsumer();

    void inject(ApiHandlerDaggerTest test);

    @Component.Builder
    interface Builder {
        TestComponent build();
        Builder apiModule(ApiHandlerModule bm);
    }
}
