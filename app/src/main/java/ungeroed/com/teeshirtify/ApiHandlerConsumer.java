package ungeroed.com.teeshirtify;

import javax.inject.Inject;

import dagger.DaggerAppComponent;

/**
 * Created by Morten on 03/09/2017.
 */

public class ApiHandlerConsumer {
    @Inject ApiHandler handler;

    @Inject public ApiHandlerConsumer(){
        DaggerAppComponent.create().inject(this);
    }

    public Double DivideNumberOfElementsWith(Integer divider){
        if(divider == 0){return null;}
        Double amount = new Double(handler.getProductCount(new String[]{"All","All"}));
        return amount/divider;
    }


}
