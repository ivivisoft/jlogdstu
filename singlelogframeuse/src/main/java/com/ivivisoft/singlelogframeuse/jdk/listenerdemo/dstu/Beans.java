/*
 *  Copyright (c) 2016, 张威, ivivisoft@gmail.com
 *  
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *  
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *  
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.ivivisoft.singlelogframeuse.jdk.listenerdemo.dstu;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * A class that provides access to the java.beans.PropertyChangeListener
 * and java.beans.PropertyChangeEvent without creating a static dependency
 * on java.beans.
 */
public final class Beans {

    private static final Class<?> propertyChangeListenerClass =
            getClass("java.beans.PropertyChangeListener");

    private static final Class<?> propertyChangeEventClass =
            getClass("java.beans.PropertyChangeEvent");

    private static final Method propertyChangeMethod =
            getMethod(propertyChangeListenerClass,
                    "propertyChange",
                    propertyChangeEventClass);

    private static final Constructor<?> propertyEventCtor =
            getConstructor(propertyChangeEventClass,
                    Object.class,
                    String.class,
                    Object.class,
                    Object.class);

    private static Class<?> getClass(String name) {
        try {
            return Class.forName(name, true, Beans.class.getClassLoader());
        } catch (ClassNotFoundException e) {
            return null;
        }
    }

    private static Constructor<?> getConstructor(Class<?> c, Class<?>... types) {
        try {
            return (c == null) ? null : c.getDeclaredConstructor(types);
        } catch (NoSuchMethodException x) {
            throw new AssertionError(x);
        }
    }

    private static Method getMethod(Class<?> c, String name, Class<?>... types) {
        try {
            return (c == null) ? null : c.getMethod(name, types);
        } catch (NoSuchMethodException e) {
            throw new AssertionError(e);
        }
    }

    /**
     * Returns {@code true} if java.beans is present.
     */
    static boolean isBeansPresent() {
        return propertyChangeListenerClass != null &&
                propertyChangeEventClass != null;
    }

    /**
     * Returns a new PropertyChangeEvent with the given source, property
     * name, old and new values.
     */
    static Object newPropertyChangeEvent(Object source, String prop,
                                         Object oldValue, Object newValue) {
        try {
            return propertyEventCtor.newInstance(source, prop, oldValue, newValue);
        } catch (InstantiationException | IllegalAccessException x) {
            throw new AssertionError(x);
        } catch (InvocationTargetException x) {
            Throwable cause = x.getCause();
            if (cause instanceof Error)
                throw (Error) cause;
            if (cause instanceof RuntimeException)
                throw (RuntimeException) cause;
            throw new AssertionError(x);
        }
    }

    /**
     * Invokes the given PropertyChangeListener's propertyChange method
     * with the given event.
     */
    static void invokePropertyChange(Object listener, Object ev) {
        try {
            propertyChangeMethod.invoke(listener, ev);
        } catch (IllegalAccessException x) {
            throw new AssertionError(x);
        } catch (InvocationTargetException x) {
            Throwable cause = x.getCause();
            if (cause instanceof Error)
                throw (Error) cause;
            if (cause instanceof RuntimeException)
                throw (RuntimeException) cause;
            throw new AssertionError(x);
        }
    }
}
