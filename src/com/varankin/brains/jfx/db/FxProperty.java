package com.varankin.brains.jfx.db;

import javafx.beans.property.Property;

/**
 *
 *  
 * @author &copy; 2019 Николай Варанкин
 * @param <T>
 */
public interface FxProperty<T> extends FxReadOnlyProperty<T>, Property<T>
{
    
}
