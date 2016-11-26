package com.varankin.brains.jfx.db;

import com.varankin.brains.db.DbАтрибутный;
import com.varankin.brains.db.Транзакция;
import java.util.function.Consumer;
import java.util.function.Supplier;
import javafx.beans.property.ObjectPropertyBase;
import javafx.beans.value.ObservableValue;

/**
 *
 * @author Varankine
 */
final class FxProperty<T> 
        extends ObjectPropertyBase<T>
{
    private final FxPropertyDescriptor<T> descriptor;

    FxProperty( DbАтрибутный элемент, String название, Supplier<T> supplier, Consumer<T> consumer )
    {
        descriptor = new FxPropertyDescriptor<>( элемент, название, supplier, consumer );
    }

    //<editor-fold defaultstate="collapsed" desc="ObjectPropertyBase">
    
    @Override
    public T get()
    {
        try( final Транзакция т = getBean().транзакция() )
        {
            т.согласовать( Транзакция.Режим.ЧТЕНИЕ_БЕЗ_ЗАПИСИ, getBean().архив() );
            T t = descriptor.supplier.get();
            super.get();
            т.завершить( true );
            return t;
        }
        catch( Exception e )
        {
            throw new RuntimeException( "Failure to get property value on " + getBean(), e );
        }
    }
    
    @Override
    public void set( T value )
    {
        try( final Транзакция т = getBean().транзакция() )
        {
            т.согласовать( Транзакция.Режим.ЗАПРЕТ_ДОСТУПА, getBean().архив() );
            descriptor.consumer.accept( value );
            super.set( value );
            т.завершить( true );
        }
        catch( Exception e )
        {
            throw new RuntimeException( "Failure to set property value on " + getBean(), e );
        }
    }
    
    @Override
    public void bind( ObservableValue<? extends T> newObservable )
    {
        throw new UnsupportedOperationException();
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
