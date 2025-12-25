package org.example.devac.utils;

import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;

import java.beans.PropertyDescriptor;
import java.util.Arrays;

public class PropertyUtils {

    public static String[] getNullPropertyNames(Object source) {
        /*
        *Esta función recibe como parametro un objeto (se espera que sea iterable)
        * y arma un array con todas las propiedades que sean null.
        * Sirve para usarse con BeanUtils.copyProperties() y "patchear" objetos, quitando
        * los nulls.
         */
        BeanWrapper src = new BeanWrapperImpl(source);
        return Arrays.stream(src.getPropertyDescriptors())
                .map(PropertyDescriptor::getName)
                .filter(name -> src.getPropertyValue(name) == null)
                .toArray(String[]::new);
    }
}