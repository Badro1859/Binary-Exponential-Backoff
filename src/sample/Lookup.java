package sample;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Bader eddine
 */
public class Lookup {

    Map<Class, Object> services = new HashMap<>() ;

    public <T> void register (Class<? super T> service, T instance){
        services.put(service, instance) ;
    }

    public <T> T getService (Class<T> service){
        T instance = (T) services.get(service) ;
        return  instance ;
    }

    private Lookup() {
    }

    public static Lookup getInstance() {
        return LookupHolder.INSTANCE;
    }

    private static class LookupHolder {

        private static final Lookup INSTANCE = new Lookup();
    }

}

