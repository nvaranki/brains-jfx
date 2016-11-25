package com.varankin.brains.jfx.db;

import com.varankin.brains.db.DbАтрибутный;
import com.varankin.brains.db.Транзакция;
import java.util.function.Supplier;
import javafx.beans.property.ReadOnlyObjectPropertyBase;

/**
 *
 * @author Varankine
 */
final class FxReadOnlyProperty<T> 
        extends ReadOnlyObjectPropertyBase<T>
{
    private final FxPropertyDescriptor<T> descriptor;

    FxReadOnlyProperty( DbАтрибутный элемент, String название, Supplier<T> supplier )
    {
        descriptor = new FxPropertyDescriptor<>( элемент, название, supplier, null );
    }

    //<editor-fold defaultstate="collapsed" desc="ObservableObjectValue">
    
    @Override
    public T get()
    {
        try( final Транзакция т = getBean().транзакция() )
        {
            return descriptor.supplier.get();
        }
        catch( Exception e )
        {
            throw new RuntimeException( e );
        }
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
