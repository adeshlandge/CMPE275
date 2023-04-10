package com.adesh.googleguice;

import javax.inject.Inject;
import com.google.inject.Guice;
import com.google.inject.Module;
import com.google.inject.Injector;
import com.google.inject.name.Named;

public class App {
	@Inject 
	@Named("CoolGreeter")
	private Greeter coolGreeter;
	@Inject 
	@Named("WarmGreeter")
	private Greeter warmGreeter;
	
    public static void main( String[] args ){
    	App app = new App();
        Module module = new GreeterModule();
        Injector injector = Guice.createInjector(module);
        injector.injectMembers(app);//injects the implementation of the service
        
        app.coolGreeter.greet();
        app.warmGreeter.greet();
    }

}
