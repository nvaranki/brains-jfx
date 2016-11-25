package com.varankin.brains.jfx.db;

import com.varankin.brains.db.DbАтрибутный;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 *
 * @author Varankine
 */
final class FxPropertyDescriptor<T>
{
    final DbАтрибутный bean;
    final String name;
    final Supplier<T> supplier;
    final Consumer<T> consumer;
    
    FxPropertyDescriptor( DbАтрибутный bean, String name, Supplier<T> supplier, Consumer<T> consumer )
    {
        this.bean = bean;
        this.name = name;
        this.supplier = supplier;
        this.consumer = consumer;
    }
    
}
