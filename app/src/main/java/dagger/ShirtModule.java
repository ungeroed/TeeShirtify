package dagger;
import java.util.ArrayList;

import javax.inject.Singleton;

import ungeroed.com.teeshirtify.Shirt;

/**
 * Created by Morten on 02/09/2017.
 */

@Module
public class ShirtModule {
    @Singleton
    @Provides
    ArrayList<Shirt> provideShirt() {
        return new ArrayList<Shirt>();
    }
}
