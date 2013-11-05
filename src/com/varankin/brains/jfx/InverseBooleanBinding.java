package com.varankin.brains.jfx;

import javafx.beans.binding.BooleanBinding;
import javafx.beans.value.ObservableBooleanValue;

/**
 *
 * @author &copy; 2013 Николай Варанкин
 */
public final class InverseBooleanBinding extends BooleanBinding
{
    private final ObservableBooleanValue SOURCE;

    public InverseBooleanBinding( ObservableBooleanValue source )
    {
        super.bind( source );
        SOURCE = source;
    }

    @Override
    protected boolean computeValue()
    {
        return !SOURCE.get();
    }
    
}
