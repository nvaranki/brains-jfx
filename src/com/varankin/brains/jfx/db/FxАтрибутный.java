package com.varankin.brains.jfx.db;

import com.varankin.brains.db.DbАтрибутный;
import com.varankin.brains.db.КороткийКлюч;
import com.varankin.brains.db.Транзакция;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javafx.beans.binding.ListExpression;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 *
 * @author Varankine
 * @param <T>
 */
public class FxАтрибутный<T extends DbАтрибутный>
{
    static final КороткийКлюч КЛЮЧ_А_ТИП = new КороткийКлюч( "тип", null );

    private final T ЭЛЕМЕНТ;
    private final Map<String,ObservableList<? extends FxАтрибутный>> КОЛЛЕКЦИИ;
    private final ListProperty<FxProperty> АТРИБУТЫ_ПРОЧИЕ;
    private final ReadOnlyListWrapper<ReadOnlyProperty> АТРИБУТЫ_ОСНОВНЫЕ;
    private final FxReadOnlyProperty<DbАтрибутный.Ключ> ТИП;
    private final Map<КороткийКлюч,FxProperty> AM = new HashMap<>();
    private boolean ami, abi, ci;

    public FxАтрибутный( T элемент )
    {
        ЭЛЕМЕНТ = элемент;
        КОЛЛЕКЦИИ = new HashMap<>();
        АТРИБУТЫ_ПРОЧИЕ = new SimpleListProperty<>( FXCollections.observableArrayList() );
        АТРИБУТЫ_ОСНОВНЫЕ = new ReadOnlyListWrapper<>( FXCollections.observableArrayList() );
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
        return атрибут( new КороткийКлюч( название, uri ) );
    }
    
    public final FxProperty атрибут( КороткийКлюч кк )
    {
        атрибутыОсновные();
        атрибутыПрочие();

        String название = кк.НАЗВАНИЕ;
        String uri = кк.ЗОНА;
        FxProperty p = AM.get( кк );
        if( p == null )
        {
            String префикс = null;//uri.substring( Math.max( uri.lastIndexOf( '/' ) "New name space";
            архив().определитьПространствоИмен( uri, префикс );
            p = new FxProperty( ЭЛЕМЕНТ, название, кк, 
                    () -> ЭЛЕМЕНТ.атрибут( название, uri, null ),
                    (t) -> ЭЛЕМЕНТ.определить( название, uri, t ) );
            AM.put( кк, p );
            АТРИБУТЫ_ПРОЧИЕ.add( p );
        }
        return p;
    }
    
    public final ReadOnlyListProperty<ReadOnlyProperty> атрибутыОсновные()
    {
        if(!abi)
        {
            try( final Транзакция т = ЭЛЕМЕНТ.транзакция() )
            {
                АТРИБУТЫ_ОСНОВНЫЕ.clear();
                for( ReadOnlyProperty p : извлечьАтрибутыBrains().values() )
                    if( p instanceof FxProperty )
                    {
                        AM.put( ((FxProperty)p).ключ(), null );
                        АТРИБУТЫ_ОСНОВНЫЕ.add( (FxProperty)p );
                    }
                    else if( p instanceof FxReadOnlyProperty )
                    {
                        AM.put( ((FxReadOnlyProperty)p).ключ(), null );
                        АТРИБУТЫ_ОСНОВНЫЕ.add( (FxReadOnlyProperty)p );
                    }
                    else
                        throw new ClassCastException( p.getClass().getName() );
                abi = true;
                т.завершить( true );
            }
            catch( Exception e )
            {
                АТРИБУТЫ_ОСНОВНЫЕ.clear();
                abi = false;
                throw new RuntimeException( "Failure to build list of primary properties on " + ЭЛЕМЕНТ, e );
            }
        }
        return АТРИБУТЫ_ОСНОВНЫЕ.getReadOnlyProperty();
    }
    
    public final ListProperty<FxProperty> атрибутыПрочие()
    {
        атрибутыОсновные();

        if( !ami )
            try( final Транзакция т = ЭЛЕМЕНТ.транзакция() )
            {
                АТРИБУТЫ_ПРОЧИЕ.clear();
                for( DbАтрибутный.Ключ ключ : ЭЛЕМЕНТ.ключи() )
                {
                    КороткийКлюч кк = new КороткийКлюч( ключ.название(), ключ.uri() );
                    if( !AM.containsKey( кк ))
                    {
                        FxProperty p = new FxProperty( ЭЛЕМЕНТ, кк.НАЗВАНИЕ, кк, 
                            () -> ЭЛЕМЕНТ.атрибут( кк.НАЗВАНИЕ, кк.ЗОНА, null ),
                            t  -> ЭЛЕМЕНТ.определить( кк.НАЗВАНИЕ, кк.ЗОНА, t ) );
                        AM.put( кк, p );
                        АТРИБУТЫ_ПРОЧИЕ.add( p );
                    }    
                }
                ami = true;
                т.завершить( true );
            }
            catch( Exception e )
            {
                АТРИБУТЫ_ПРОЧИЕ.clear();
                ami = false;
                throw new RuntimeException( "Failure to build list of foreign properties on " + ЭЛЕМЕНТ, e );
            }
        return АТРИБУТЫ_ПРОЧИЕ;
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
    private Map<String,ReadOnlyProperty> извлечьАтрибутыBrains()
    {
        return Arrays.stream( getClass().getMethods() )
            .filter( m -> ReadOnlyProperty.class.isAssignableFrom( m.getReturnType() ) ) // Property тоже
            .filter( m -> !"атрибут".equals( m.getName() ) )
            .filter( m -> !"атрибутыОсновные".equals( m.getName() ) )
            .filter( m -> !"атрибутыПрочие".equals( m.getName() ) )
            .filter( m -> !ListExpression.class.isAssignableFrom( m.getReturnType() ) )
            .collect( Collectors.toMap( m -> m.getName(), m ->
            {
                try
                {
                    m.setAccessible( true ); // проблема с унаследованным public final
                    return ((ReadOnlyProperty)m.invoke( FxАтрибутный.this ));
                }
                catch( SecurityException | IllegalAccessException 
                    | IllegalArgumentException | InvocationTargetException ex )
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
        if( !ci )
        {
            КОЛЛЕКЦИИ.putAll( Arrays.stream( getClass().getMethods() )
                .filter( m -> ListExpression.class.isAssignableFrom( m.getReturnType() ) )
                .filter( m -> !"атрибут".equals( m.getName() ) )
                .filter( m -> !"атрибутыОсновные".equals( m.getName() ) )
                .filter( m -> !"атрибутыПрочие".equals( m.getName() ) )
                .collect( Collectors.toMap( m -> m.getName(), new XM( this ) ) ) );
            ci = true;
        }
        return Collections.unmodifiableMap( КОЛЛЕКЦИИ );
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
