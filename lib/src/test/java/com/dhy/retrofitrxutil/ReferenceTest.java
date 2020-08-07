package com.dhy.retrofitrxutil;

import java.lang.ref.WeakReference;

public class ReferenceTest {
    Object global = this;
    Object lock;

    public static void main(String[] args) {
        ReferenceTest main = new ReferenceTest();
        WeakReference<A> a = new WeakReference<>(new A());
        WeakReference<B> b = new WeakReference<>(new B());

        a.get().global = main.global;
        b.get().global = a.get().global;
        main.lock = b.get().global;
        System.gc();
        System.out.println("gc a " + (a.get() == null));
        System.out.println("gc b " + (b.get() == null));
    }

    static final class A {
        Object global;
    }

    static final class B {
        Object global;
    }
}
