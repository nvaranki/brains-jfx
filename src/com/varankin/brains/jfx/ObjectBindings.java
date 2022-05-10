package com.varankin.brains.jfx;

import javafx.beans.binding.*;
import javafx.beans.property.Property;

/**
 *
 * @author Николай
 */
public class ObjectBindings
{
    private ObjectBindings() {}
    
    public static BooleanBinding isNotNull( final Property<?> property )
    {
        return new BooleanBinding()
        {
            {
                super.bind( property );
            }

            @Override
            protected boolean computeValue()
            {
                return property != null && property.getValue() != null;
            }
            
        };
    }
}
