package com.varankin.brains.jfx.db;

import com.varankin.brains.db.DbАтрибутный;
import com.varankin.brains.io.xml.XmlBrains;
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

    FxReadOnlyProperty( DbАтрибутный элемент, String название, 
            Supplier<T> supplier )
    {
        this( элемент, название, XmlBrains.XMLNS_BRAINS, supplier );
    }

    FxReadOnlyProperty( DbАтрибутный элемент, String название, String scope, 
            Supplier<T> supplier )
    {
        descriptor = new FxPropertyDescriptor<>( элемент, название, scope, supplier, null );
    }

    public String getScope()
    {
        return descriptor.uri;
    }
    
    //<editor-fold defaultstate="collapsed" desc="ObservableObjectValue">
    
    @Override
    public T get()
    {
        return FxPropertyDescriptor.get( descriptor.bean, descriptor.supplier );
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
