package com.varankin.brains.jfx.db;

import com.sun.javafx.property.adapter.PropertyDescriptor;
import com.varankin.brains.db.DbАтрибутный;
import com.varankin.brains.db.Транзакция;
import java.lang.reflect.Method;
import javafx.beans.property.adapter.ReadOnlyJavaBeanProperty;
import javafx.beans.value.ObservableValue;

/**
 *
 * @author Varankine
 */
final class FxPropertyDescriptor extends PropertyDescriptor
{
    
    FxPropertyDescriptor( String propertyName, Class<?> beanClass, Method getter, Method setter )
    {
        super( propertyName, beanClass, getter, setter );
    }
    
    public class FxListener<T> extends Listener<T> 
    {
        
        public FxListener( DbАтрибутный bean, ReadOnlyJavaBeanProperty<T> property )
        {
            super( bean, property );
        }

        @Override
        public void changed( ObservableValue<? extends T> observable, T oldValue, T newValue ) 
        {
            try( final Транзакция т = ((DbАтрибутный)getBean()).транзакция() )
            {
                super.changed( observable, oldValue, newValue );
                т.завершить( true );
            }
            catch( Exception e )
            {
                throw new RuntimeException( e );
            }
        }
        
    }
    
}
