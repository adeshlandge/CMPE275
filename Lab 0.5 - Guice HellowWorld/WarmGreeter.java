package com.adesh.googleguice;

import com.google.inject.Inject;
import com.google.inject.name.Named;

public class WarmGreeter implements Greeter {
    private final String name;

    @Inject
    public WarmGreeter(@Named("name") String name) {
        this.name = name;
    }

    public void greet() {
        System.out.println("Hello, my dear World. I am  " + name +". Nice to see you!");
    }
}
