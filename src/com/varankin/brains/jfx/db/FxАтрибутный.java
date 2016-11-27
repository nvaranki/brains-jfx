package com.varankin.brains.jfx.db;

import com.varankin.brains.db.DbАтрибутный;
import com.varankin.brains.db.Транзакция;
import com.varankin.brains.io.xml.XmlBrains;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javafx.beans.binding.ListExpression;
import javafx.beans.property.ListProperty;
import javafx.beans.property.ReadOnlyListProperty;
import javafx.beans.property.ReadOnlyListWrapper;
import javafx.beans.property.ReadOnlyProperty;
import javafx.beans.property.SimpleListProperty;
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
    private final ListProperty<FxProperty> АТРИБУТЫ;
    private final ReadOnlyProperty<DbАтрибутный.Ключ> ТИП;

    public FxАтрибутный( T элемент )
    {
        ЭЛЕМЕНТ = элемент;
        АТРИБУТЫ = new SimpleListProperty<>( FXCollections.observableArrayList() );
        ТИП = new FxReadOnlyProperty<>( элемент, "тип", null, () -> элемент.тип() );
    }
    
    public final T getSource()
    {
        return ЭЛЕМЕНТ;
    }
    
    public final FxАрхив архив()
    {
        return FxФабрика.getInstance().создать( ЭЛЕМЕНТ.архив() );
    }
    
    public final ReadOnlyProperty<DbАтрибутный.Ключ> тип()
    {
        return ТИП;
    }
    
    public final FxProperty атрибут( String название, String uri )
    {
        FxProperty p = атрибуты().stream()
                .filter( (а) -> а.getName().equals( название ) )
                .findFirst().orElse( null ); 
        if( p == null )
            АТРИБУТЫ.add( p = new FxProperty( ЭЛЕМЕНТ, название, uri, 
                () -> ЭЛЕМЕНТ.атрибут( название, uri, null ),
                (t) -> ЭЛЕМЕНТ.определить( название, uri, t ) ) );
        return p;
    }
    
    public final ListProperty<FxProperty> атрибуты()
    {
        if( АТРИБУТЫ.isEmpty() )
            try( final Транзакция т = ЭЛЕМЕНТ.транзакция() )
            {
                for( DbАтрибутный.Ключ ключ : ЭЛЕМЕНТ.ключи() )
                {
                    String название = ключ.название();
                    String uri = ключ.uri();
                    if( !XmlBrains.XMLNS_BRAINS.equals( uri ) ) //TODO почему не все?
                        АТРИБУТЫ.add( new FxProperty( ЭЛЕМЕНТ, название, uri, 
                            () -> ЭЛЕМЕНТ.атрибут( название, uri, null ),
                            (t) -> ЭЛЕМЕНТ.определить( название, uri, t ) ) );
                }
                т.завершить( true );
            }
            catch( Exception e )
            {
                throw new RuntimeException( "Failure to build list of custom properties on " + ЭЛЕМЕНТ, e );
            }
        return АТРИБУТЫ;
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
            return !"атрибуты".equals( m.getName() ) && ListExpression.class.isAssignableFrom( m.getReturnType() );
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
