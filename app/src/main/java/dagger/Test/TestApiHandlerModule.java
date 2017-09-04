package dagger.Test;
import org.mockito.Mockito;

import ungeroed.com.teeshirtify.ApiHandler;

/**
 * Created by Morten on 03/09/2017.
 */
public class TestApiHandlerModule extends dagger.ApiHandlerModule {

    @Override
    public ApiHandler provideApiHandler() {
        return Mockito.mock(ApiHandler.class);
    }

}
