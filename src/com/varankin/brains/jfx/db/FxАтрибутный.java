package com.varankin.brains.jfx.db;

import com.varankin.brains.db.DbАтрибутный;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javafx.beans.property.ReadOnlyListProperty;
import javafx.beans.property.ReadOnlyListWrapper;
import javafx.beans.property.StringProperty;
import javafx.beans.property.adapter.*;
import javafx.collections.FXCollections;

/**
 *
 * @author Varankine
 * @param <T>
 */
public class FxАтрибутный<T extends DbАтрибутный>
{
    private final T ЭЛЕМЕНТ;
    private final Map<String,StringProperty> АТРИБУТЫ;

    public FxАтрибутный( T элемент )
    {
        ЭЛЕМЕНТ = элемент;
        АТРИБУТЫ = new HashMap<>();
    }
    
    public final T getSource()
    {
        return ЭЛЕМЕНТ;
    }
    
//    @Override
//    public final DbАтрибут атрибут_( String название, String uri )
//    {
//        КлючImpl ключ = new КлючImpl( название, uri, null );
//        DbАтрибут атрибут = АТРИБУТЫ.get( ключ );
//        if( атрибут == null )
//            АТРИБУТЫ.put( ключ, атрибут = new NeoАтрибут<>( ключ, getNode(), String.class ) );
//        return атрибут;
//    }
    
    protected static JavaBeanBooleanProperty buildBooleanProperty( 
            Object элемент, String название )
    {
        try
        {
            return JavaBeanBooleanPropertyBuilder.create()
                    .bean( элемент ).name( название )
                    .getter( название ).setter( название )
                    .build();
        }
        catch( NoSuchMethodException ex )
        {
            throw new RuntimeException( ex );
        }
    }
    
    protected static JavaBeanIntegerProperty buildIntegerProperty( 
            Object элемент, String название )
    {
        try
        {
            return JavaBeanIntegerPropertyBuilder.create()
                    .bean( элемент ).name( название )
                    .getter( название ).setter( название )
                    .build();
        }
        catch( NoSuchMethodException ex )
        {
            throw new RuntimeException( ex );
        }
    }
    
    protected static JavaBeanLongProperty buildLongProperty( 
            Object элемент, String название )
    {
        try
        {
            return JavaBeanLongPropertyBuilder.create()
                    .bean( элемент ).name( название )
                    .getter( название ).setter( название )
                    .build();
        }
        catch( NoSuchMethodException ex )
        {
            throw new RuntimeException( ex );
        }
    }
    
    protected static JavaBeanFloatProperty buildFloatProperty( 
            Object элемент, String название )
    {
        try
        {
            return JavaBeanFloatPropertyBuilder.create()
                    .bean( элемент ).name( название )
                    .getter( название ).setter( название )
                    .build();
        }
        catch( NoSuchMethodException ex )
        {
            throw new RuntimeException( ex );
        }
    }
    
    protected static JavaBeanStringProperty buildStringProperty( 
            Object элемент, String название )
    {
        try
        {
            return JavaBeanStringPropertyBuilder.create()
                    .bean( элемент ).name( название )
                    .getter( название ).setter( название )
                    .build();
        }
        catch( NoSuchMethodException ex )
        {
            throw new RuntimeException( ex );
        }
    }
    
    protected static JavaBeanObjectProperty buildObjectProperty( 
            Object элемент, String название )
    {
        try
        {
            return JavaBeanObjectPropertyBuilder.create()
                    .bean( элемент ).name( название )
                    .getter( название ).setter( название )
                    .build();
        }
        catch( NoSuchMethodException ex )
        {
            throw new RuntimeException( ex );
        }
    }
    
    protected static <E,X> ReadOnlyListProperty<E> buildReadOnlyListProperty( 
            Object элемент, String название, List<E> list )
    {
        return new ReadOnlyListWrapper<>( элемент, название, 
            FXCollections.observableList( list ) ).getReadOnlyProperty();
    }
    
}
