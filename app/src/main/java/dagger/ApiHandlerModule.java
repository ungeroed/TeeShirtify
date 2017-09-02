package dagger;
import javax.inject.Singleton;

import ungeroed.com.teeshirtify.ApiHandler;

/**
 * Created by Morten on 01/09/2017.
 */
@Module
public class ApiHandlerModule {
    @Provides
    @Singleton
    ApiHandler provideApiHandler() {
        return new ApiHandler();
    }
}


