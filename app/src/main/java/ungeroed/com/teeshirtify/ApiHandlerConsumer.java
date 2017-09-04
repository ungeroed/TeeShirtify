package ungeroed.com.teeshirtify;

import javax.inject.Inject;

import dagger.DaggerAppComponent;

/**
 * This  class is created merely to function as an example of how to test isolated objects using Dagger
 * and Mockito. It holds no value to the project besides being a working example. See 'dagger.test' package
 * for further related test classes.
 */

public class ApiHandlerConsumer {
    @Inject ApiHandler handler;

    @Inject public ApiHandlerConsumer(){
        DaggerAppComponent.create().inject(this);
    }

    /**
     * Retrieves the number of elements in the apimodel and divide the number with
     * the provided input.
     * @param divider the number to divide the amount of shirts with.
     * @return
     */
    public Double DivideNumberOfElementsWith(Integer divider){
        if(divider == 0){return null;}
        Double amount = new Double(handler.getProductCount(new String[]{"All","All"}));
        return amount/divider;
    }


}
