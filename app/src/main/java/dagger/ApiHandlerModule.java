package dagger;

import javax.inject.Singleton;

import ungeroed.com.teeshirtify.ApiHandler;


/**
 * Created by Morten on 03/09/2017.
 */
@Module
public class ApiHandlerModule {
    @Singleton
    @Provides
    public ApiHandler provideApiHandler() {
        return new ApiHandler();
    }
}
