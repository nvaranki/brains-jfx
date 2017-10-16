package com.varankin.brains.jfx.db;

import com.varankin.brains.db.DbАтрибутный;
import com.varankin.brains.db.КороткийКлюч;
import com.varankin.brains.db.Транзакция;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javafx.beans.binding.ListExpression;
import javafx.beans.property.ListProperty;
import javafx.beans.property.Property;
import javafx.beans.property.ReadOnlyListProperty;
import javafx.beans.property.ReadOnlyListWrapper;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import static com.varankin.brains.db.DbАтрибутный.КЛЮЧ_А_ТИП;

/**
 *
 * @author Varankine
 * @param <T>
 */
public class FxАтрибутный<T extends DbАтрибутный>
{
    private final T ЭЛЕМЕНТ;
    private final ListProperty<FxProperty> АТРИБУТЫ;
    private final FxReadOnlyProperty<DbАтрибутный.Ключ> ТИП;
    private final Map<КороткийКлюч,FxProperty> AM = new HashMap<>();
    private boolean ami;

    public FxАтрибутный( T элемент )
    {
        ЭЛЕМЕНТ = элемент;
        АТРИБУТЫ = new SimpleListProperty<>( FXCollections.observableArrayList() );
        ТИП = new FxReadOnlyProperty<>( элемент, "тип", КЛЮЧ_А_ТИП, () -> элемент.тип() );
    }
    
    public final T getSource()
    {
        return ЭЛЕМЕНТ;
    }
    
    public final FxАрхив архив()
    {
        return FxФабрика.getInstance().создать( ЭЛЕМЕНТ.архив() );
    }
    
    public final FxReadOnlyProperty<DbАтрибутный.Ключ> тип()
    {
        return ТИП;
    }
    
    public final FxProperty атрибут( String название, String uri )
    {
        атрибуты();
        КороткийКлюч кк = new КороткийКлюч( название, uri );
        FxProperty p = AM.get( кк );
        if( p == null )
        {
            String префикс = null;//uri.substring( Math.max( uri.lastIndexOf( '/' ) "New name space";
            архив().определитьПространствоИмен( uri, префикс );
            p = new FxProperty( ЭЛЕМЕНТ, название, кк, 
                    () -> ЭЛЕМЕНТ.атрибут( название, uri, null ),
                    (t) -> ЭЛЕМЕНТ.определить( название, uri, t ) );
            AM.put( кк, p );
            АТРИБУТЫ.add( p );
        }
        return p;
    }
    
    public final ListProperty<FxProperty> атрибуты()
    {
        if( !ami )
            try( final Транзакция т = ЭЛЕМЕНТ.транзакция() )
            {
                for( FxProperty p : атрибутыBrains().values() )
                    AM.put( p.ключ(), p );
                АТРИБУТЫ.addAll( AM.values() );

                for( DbАтрибутный.Ключ ключ : ЭЛЕМЕНТ.ключи() )
                {
                    КороткийКлюч кк = new КороткийКлюч( ключ.название(), ключ.uri() );
                    if( !AM.containsKey( кк ))
                    {
                        FxProperty p = new FxProperty( ЭЛЕМЕНТ, кк.NAME, кк, 
                            () -> ЭЛЕМЕНТ.атрибут( кк.NAME, кк.URI, null ),
                            t  -> ЭЛЕМЕНТ.определить( кк.NAME, кк.URI, t ) );
                        AM.put( кк, p );
                        АТРИБУТЫ.add( p );
                    }    
                }
                ami = true;
                т.завершить( true );
            }
            catch( Exception e )
            {
                AM.clear();
                АТРИБУТЫ.clear();
                ami = false;
                throw new RuntimeException( "Failure to build list of properties on " + ЭЛЕМЕНТ, e );
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
     * @return карта атрибутов элемента: название метода -> атрибут.
     */
    private Map<String,FxProperty> атрибутыBrains()
    {
        return Arrays.stream( getClass().getMethods() )
            .filter( m -> !"атрибут".equals( m.getName() ) )
            .filter( m -> !"атрибуты".equals( m.getName() ) )
            .filter( m -> !ListExpression.class.isAssignableFrom( m.getReturnType() ) )
            .filter( m -> Property.class.isAssignableFrom( m.getReturnType() ) )
            .collect( Collectors.toMap( m -> m.getName(), m ->
            {
                try
                {
                    m.setAccessible( true ); // проблема с унаследованным public final
                    return ((FxProperty)m.invoke( FxАтрибутный.this ));
                }
                catch( SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex )
                {
                    Logger.getLogger( getClass().getName() ).log( Level.SEVERE, m.getName(), ex );
                    return null;
                }
            } ) );
    }
    
    
    /**
     * @return карта коллекций элемента: название метода -> коллекция.
     */
    public Map<String,ObservableList<? extends FxАтрибутный>> коллекции()
    {
        return Arrays.stream( getClass().getMethods() )
                .filter( m -> !"атрибуты".equals( m.getName() ) )
                .filter( m -> ListExpression.class.isAssignableFrom( m.getReturnType() ) )
                .collect( Collectors.toMap( m -> m.getName(), new XM( this ) ) );
    }
    
    /**
     * Создает свободную копию образца для использования в архиве.
     * Копируются все атрибуты и вложенные элементы коллекций, рекурсивно.
     * Произведенный дубликат должен быть вложен в коллекцию одного из 
     * элементов, принадлежащих архиву.
     * 
     * @param образец элемент для копирования.
     * @param архив   архив, для которого делается копия.
     * @return дубликат образца.
     */
    public static FxАтрибутный<?> дублировать( FxАтрибутный<?> образец, FxАрхив архив )
    {
        // создать дубликат по образцу
        FxАтрибутный дубликат = архив.создатьНовыйЭлемент( образец.getSource().тип() );
        // скопировать все атрибуты
        образец.getSource().ключи().forEach( ключ -> 
        {
            FxProperty p_to = дубликат.атрибут( ключ.название(), ключ.uri() );
            FxProperty p_from = образец.атрибут( ключ.название(), ключ.uri() );
            p_to.setValue( p_from.getValue() );
        });
        // скопировать все коллекции
        образец.коллекции().values().forEach( коллекция -> коллекция.forEach( 
            e -> дубликат.выполнить( ( о, к ) -> к.add( о ), дублировать( e, архив ) ) ) );
        // вернуть свободный (невложенный!) дубликат
        return дубликат;
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
