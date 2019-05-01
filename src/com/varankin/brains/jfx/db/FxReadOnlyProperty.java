package com.varankin.brains.jfx.db;

import com.varankin.brains.db.КороткийКлюч;
import javafx.beans.property.ReadOnlyProperty;

/**
 *
 *  
 * @author &copy; 2019 Николай Варанкин
 * @param <T>
 */
public interface FxReadOnlyProperty<T> extends ReadOnlyProperty<T>
{
    КороткийКлюч ключ();
    Runnable getFireHandler();
}
