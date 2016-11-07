package com.varankin.brains.jfx.db;

import com.varankin.brains.db.DbАтрибутный;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import javafx.beans.binding.ListExpression;
import javafx.beans.property.ReadOnlyListProperty;
import javafx.beans.property.ReadOnlyListWrapper;
import javafx.beans.property.StringProperty;
import javafx.beans.property.adapter.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

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
                    .getter( getAccessibleMethod( элемент, название, 0 ) )
                    .setter( getAccessibleMethod( элемент, название, 1 ) )
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
                    .getter( getAccessibleMethod( элемент, название, 0 ) )
                    .setter( getAccessibleMethod( элемент, название, 1 ) )
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
                    .getter( getAccessibleMethod( элемент, название, 0 ) )
                    .setter( getAccessibleMethod( элемент, название, 1 ) )
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
                    .getter( getAccessibleMethod( элемент, название, 0 ) )
                    .setter( getAccessibleMethod( элемент, название, 1 ) )
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
                    .getter( getAccessibleMethod( элемент, название, 0 ) )
                    .setter( getAccessibleMethod( элемент, название, 1 ) )
                    .build();
        }
        catch( NoSuchMethodException ex )
        {
            throw new RuntimeException( ex );
        }
    }
    
    private static Method getAccessibleMethod( Object элемент, String название, int pc )
    {
        for( Method m : элемент.getClass().getMethods() )
            if( m.getName().equals( название ) )
                if( m.getParameterCount() == pc )
                {
                    m.setAccessible( true ); // проблема с унаследованным public final
                    return m;
                }
        return null;
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
    
    /**
     * @return карта коллекций элемента: название метода -> коллекция.
     */
    public Map<String,ObservableList<? extends FxАтрибутный>> коллекции()
    {
        return Arrays.stream( getClass().getMethods() )
                .filter( XM::isListExpression )
                .collect( Collectors.toMap( m -> m.getName(), new XM( this ) ) );
    }
    
    /**
     * Извлекатель коллекции элемента.
     */
    private static class XM implements Function<Method,ObservableList<? extends FxАтрибутный>>
    {
        final FxАтрибутный атрибутный;

        XM( FxАтрибутный атрибутный )
        {
            this.атрибутный = атрибутный;
        }
        
        static boolean isListExpression( Method m )
        {
            return ListExpression.class.isAssignableFrom( m.getReturnType() );
        }
        
        @Override
        public ObservableList<? extends FxАтрибутный> apply( Method m )
        {
            try
            {
                m.setAccessible( true ); // проблема с унаследованным public final
                return ((ListExpression)m.invoke( атрибутный )).getValue();
            }
            catch( SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex )
            {
                Logger.getLogger( getClass().getName() ).log( Level.SEVERE, m.getName(), ex );
                return null;
            }
        }
    }
    
    /**
     * Выполняет заданный оператор над узлом и подходящей коллекцией.
     * 
     * @param <X>      класс результата, возвращаемого оператором.
     * @param оператор оператор над узлом и коллекцией.
     * @param узел     узел базы данных.
     * @return результат, возвращенный оператором.
     * @exception NullPointerException если узел - {@code null}. 
     */
    /*default*/ public <X> X выполнить( FxОператор<X> оператор, FxАтрибутный узел )
    {
        String s = "UNSUPPORTED OPERATOR OVER NODE=" + ( узел != null ? узел.getSource().тип().название() : null );
        throw new UnsupportedOperationException( s );
    }
    
}
