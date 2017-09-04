package dagger;

import javax.inject.Singleton;
import ungeroed.com.teeshirtify.ApiHandler;


/**
 * Module to handle lifecycle and enforce singleton pattern for ApiHandler
 */
@Module
public class ApiHandlerModule {
    @Singleton
    @Provides
    public ApiHandler provideApiHandler() {
        return new ApiHandler();
    }
}
