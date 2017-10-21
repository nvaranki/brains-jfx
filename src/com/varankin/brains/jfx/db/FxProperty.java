package com.varankin.brains.jfx.db;

import com.varankin.brains.db.DbАтрибутный;
import com.varankin.brains.db.КороткийКлюч;
import java.util.function.Consumer;
import java.util.function.Supplier;
import javafx.beans.property.ObjectPropertyBase;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.beans.value.WeakChangeListener;

/**
 *
 * @author Varankine
 */
public final class FxProperty<T> 
        extends ObjectPropertyBase<T>
{
    private final FxPropertyDescriptor<T> descriptor;
    private final ChangeListener<T> chl;

    FxProperty( DbАтрибутный элемент, String название, КороткийКлюч ключ, 
            Supplier<T> supplier, Consumer<T> consumer )
    {
        super( FxPropertyDescriptor.get( элемент, supplier ) );
        descriptor = new FxPropertyDescriptor<>( элемент, название, ключ, supplier, consumer );
        chl = ( ObservableValue<? extends T> o, T ov, T nv ) -> 
                FxPropertyDescriptor.set( элемент, consumer, nv );
        addListener( new WeakChangeListener<>( chl ) );
    }

    public КороткийКлюч ключ()
    {
        return descriptor.ключ;
    }

    public String getScope()
    {
        return descriptor.ключ.ЗОНА;
    }
    
    //<editor-fold defaultstate="collapsed" desc="ObjectPropertyBase">
    
    @Override
    public void bind( ObservableValue<? extends T> newObservable )
    {
        super.bind( newObservable );
//        throw new UnsupportedOperationException();
    }
    
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="ReadOnlyProperty">
    
    @Override
    public DbАтрибутный getBean()
    {
        return descriptor.bean;
    }
    
    @Override
    public String getName()
    {
        return descriptor.name;
    }
    
    //</editor-fold>
    
}
