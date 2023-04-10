package com.adesh.googleguice;

import com.google.inject.Inject;
import com.google.inject.name.Named;

public class CoolGreeter implements Greeter {
    private final String name;

    @Inject
    public CoolGreeter(@Named("name") String name) {
        this.name = name;
    }

    public void greet() {
        System.out.println("Hey World. Me " + name);
    }
}

