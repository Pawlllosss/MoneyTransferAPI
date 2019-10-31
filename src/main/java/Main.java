import api.configuration.ApiInitializer;
import api.configuration.ApiModule;
import com.google.inject.Guice;
import com.google.inject.Injector;

public class Main {

    public static void main(String[] args) {
        Injector injector = Guice.createInjector(new ApiModule());
        injector.getInstance(ApiInitializer.class).run();
    }
}
