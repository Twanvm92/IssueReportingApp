package com.example.justin.verbeterjegemeente.util;

public class Foo {

    int value;

    public Foo() {
        // nothing has to be set here
    }

    public Foo(int value) {
        this.value = value;
    }

    public static boolean isEmpty(Foo foo) {
        return foo.value == 0;
    }
}
