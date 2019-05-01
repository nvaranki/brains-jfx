package com.varankin.brains.jfx.db;

import com.varankin.brains.db.DbАрхив;
import com.varankin.brains.db.DbАтрибутный;
import com.varankin.brains.db.КороткийКлюч;
import com.varankin.brains.db.Транзакция;
import com.varankin.characteristic.Изменение;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collections;
import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javafx.application.Platform;
import javafx.beans.binding.ListExpression;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 *
 * @author &copy; 2019 Николай Варанкин
 * @param <T> тип вложенного элемента.
 */
public class FxАтрибутный<T extends DbАтрибутный>
{
    static final КороткийКлюч КЛЮЧ_А_ТИП = new КороткийКлюч( "тип", null );
    static final КороткийКлюч КЛЮЧ_А_ВОССТАНОВИМЫЙ = new КороткийКлюч( "восстановимый", null );

    private final T ЭЛЕМЕНТ;
    private final Map<String,ObservableList<? extends FxАтрибутный>> КОЛЛЕКЦИИ;
    private final ListProperty<FxProperty> АТРИБУТЫ_ПРОЧИЕ;
    private final ReadOnlyListWrapper<FxReadOnlyProperty> АТРИБУТЫ_ОСНОВНЫЕ;
    private final FxReadOnlyPropertyImpl<DbАтрибутный.Ключ> ТИП;
    private final FxReadOnlyPropertyImpl<Boolean> ВОССТАНОВИМЫЙ;
    private final Map<КороткийКлюч,FxReadOnlyProperty> AM = new HashMap<>();
    private boolean атрибутыПрочиеЗагружены, атрибутыОсновныеЗагружены, ci;

    public FxАтрибутный( T элемент )
    {
        ЭЛЕМЕНТ = элемент;
        КОЛЛЕКЦИИ = new HashMap<>();
        АТРИБУТЫ_ПРОЧИЕ = new SimpleListProperty<>( FXCollections.observableArrayList() );
        АТРИБУТЫ_ОСНОВНЫЕ = new ReadOnlyListWrapper<>( FXCollections.observableArrayList() );
        ТИП = new FxReadOnlyPropertyImpl<>( элемент, "тип", КЛЮЧ_А_ТИП, () -> элемент.тип() );
        ВОССТАНОВИМЫЙ = new FxReadOnlyPropertyImpl<>( элемент, "восстановимый", 
                КЛЮЧ_А_ВОССТАНОВИМЫЙ, () -> элемент.восстановимый() );
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
    
    public final FxReadOnlyProperty<Boolean> восстановимый()
    {
        return ВОССТАНОВИМЫЙ;
    }
    
    public final <E,T extends FxReadOnlyProperty<E>> 
    T атрибут( String название, String uri, Class<T> type )
    {
        return атрибут( new КороткийКлюч( название, uri ), type );
    }
    
    public final <E,T extends FxReadOnlyProperty<E>> 
    T атрибут( КороткийКлюч кк, Class<T> type )
    {
        FxReadOnlyProperty a = атрибут( кк );
        if( type.isInstance( a ))
            return type.cast( a );
        else
            throw new ClassCastException( type.getName() );
    }
    
    private FxReadOnlyProperty атрибут( КороткийКлюч кк )
    {
        атрибутыОсновные();
        атрибутыПрочие();

        String название = кк.НАЗВАНИЕ;
        String uri = кк.ЗОНА;
        FxReadOnlyProperty p = AM.get( кк );
        if( p == null )
        {
            String префикс = null;//uri.substring( Math.max( uri.lastIndexOf( '/' ) "New name space";
            архив().определитьПространствоИмен( uri, префикс );
            FxPropertyImpl np = new FxPropertyImpl( ЭЛЕМЕНТ, название, кк, 
                    () -> ЭЛЕМЕНТ.атрибут( название, uri, null ),
                    (t) -> ЭЛЕМЕНТ.определить( название, uri, t ) );
            AM.put( кк, np );
            АТРИБУТЫ_ПРОЧИЕ.add( np );
            p = np;
        }
        return p;
    }
    
    public final ReadOnlyListProperty<FxReadOnlyProperty> атрибутыОсновные()
    {
        if( !атрибутыОсновныеЗагружены )
        {
            try( final Транзакция т = ЭЛЕМЕНТ.транзакция() )
            {
                АТРИБУТЫ_ОСНОВНЫЕ.clear();
                for( FxReadOnlyProperty p : извлечьАтрибутыBrains().values() )
                {
                    AM.put( p.ключ(), p );
                    АТРИБУТЫ_ОСНОВНЫЕ.add( p );
                }
                атрибутыОсновныеЗагружены = true;
                т.завершить( true );
            }
            catch( Exception e )
            {
                АТРИБУТЫ_ОСНОВНЫЕ.clear();
                атрибутыОсновныеЗагружены = false;
                throw new RuntimeException( "Failure to build list of primary properties on " + ЭЛЕМЕНТ, e );
            }
        }
        return АТРИБУТЫ_ОСНОВНЫЕ.getReadOnlyProperty();
    }
    
    public final ListProperty<FxProperty> атрибутыПрочие()
    {
        атрибутыОсновные();

        if( !атрибутыПрочиеЗагружены )
            try( final Транзакция т = ЭЛЕМЕНТ.транзакция() )
            {
                АТРИБУТЫ_ПРОЧИЕ.clear();
                for( DbАтрибутный.Ключ ключ : ЭЛЕМЕНТ.ключи() )
                {
                    КороткийКлюч кк = new КороткийКлюч( ключ.название(), ключ.uri() );
                    if( !AM.containsKey( кк ))
                    {
                        FxPropertyImpl p = new FxPropertyImpl( ЭЛЕМЕНТ, кк.НАЗВАНИЕ, кк, 
                            () -> ЭЛЕМЕНТ.атрибут( кк, null ),
                            t  -> ЭЛЕМЕНТ.определить( кк, t ) );
                        AM.put( кк, p );
                        АТРИБУТЫ_ПРОЧИЕ.add( p );
                    }    
                }
                атрибутыПрочиеЗагружены = true;
                т.завершить( true );
            }
            catch( Exception e )
            {
                АТРИБУТЫ_ПРОЧИЕ.clear();
                атрибутыПрочиеЗагружены = false;
                throw new RuntimeException( "Failure to build list of foreign properties on " + ЭЛЕМЕНТ, e );
            }
        return АТРИБУТЫ_ПРОЧИЕ;
    }
    
    static <X extends DbАтрибутный, E extends FxАтрибутный<X>> 
    ReadOnlyListProperty<E> buildReadOnlyListProperty( 
            Object элемент, String название, FxList<X,E> list )
    {
        ReadOnlyListProperty<E> property = new ReadOnlyListWrapper<>( элемент, название, 
                FXCollections.observableList( list ) ).getReadOnlyProperty();
        list.наблюдатели().add( c -> обновить( property, c ) );
        return property;
    }

    private static <X extends DbАтрибутный,E extends FxАтрибутный<X>> 
    void обновить( ReadOnlyListProperty<E> property, Изменение<X> изменение ) 
    {
        Platform.runLater( () ->
        {
            if( изменение.ПРЕЖНЕЕ != null )
                property.removeIf( a -> a.getSource().equals( изменение.ПРЕЖНЕЕ ) );
            if( изменение.АКТУАЛЬНОЕ != null && property.stream()
                    .map( FxАтрибутный::getSource )
                    .noneMatch( a -> a.equals( изменение.АКТУАЛЬНОЕ ) ) )
                property.add( (E) FxФабрика.getInstance().создать( изменение.АКТУАЛЬНОЕ ) );
        } );
    }
    
    /**
     * @return карта атрибутов элемента: название метода -> атрибут.
     */
    private Map<String,FxReadOnlyProperty> извлечьАтрибутыBrains()
    {
        return Arrays.stream( getClass().getMethods() )
            .filter( m -> FxReadOnlyProperty.class.isAssignableFrom( m.getReturnType() ) ) // FxProperty тоже
            .filter( m -> !"атрибут".equals( m.getName() ) )
            .filter( m -> !"атрибутыОсновные".equals( m.getName() ) )
            .filter( m -> !"атрибутыПрочие".equals( m.getName() ) )
            .filter( m -> !ListExpression.class.isAssignableFrom( m.getReturnType() ) )
            .collect( Collectors.toMap( m -> m.getName(), m ->
            {
                try
                {
                    m.setAccessible( true ); // проблема с унаследованным public final
                    return ((FxReadOnlyProperty)m.invoke( FxАтрибутный.this ));
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
        FxАтрибутный<?> дубликат = архив.создатьНовыйЭлемент( образец.getSource().тип() );
        // скопировать все атрибуты
        образец.getSource().ключи().forEach( ключ -> 
        {
            FxProperty копия   = дубликат.атрибут( ключ.название(), ключ.uri(), FxProperty.class );
            FxProperty оригинал = образец.атрибут( ключ.название(), ключ.uri(), FxProperty.class );
            копия.setValue( оригинал.getValue() );
        });
        // скопировать все коллекции
        FxОператор оператор = ( о, к ) -> к.add( о );
        образец.коллекции().values().forEach( коллекция -> коллекция.forEach( 
            e -> дубликат.выполнить( оператор, дублировать( e, архив ) ) ) );
        // вернуть свободный (невложенный!) дубликат
        return дубликат;
    }

    /**
     * Выполняет поиск экземпляра объекта в цепочке владельцев
     * от архива до этого элемента. Возвращенный экземпляр 
     * присутствует в коллекции. Для данного объекта это не 
     * обязательно.
     * 
     * @param владельцы владельцы (архив не включен).
     * @param поСвязи   {@code true} если данный объект должен быть
     *          привязан к владельцу по какой-либо связи вхождения.
     * @return экземпляр этого объекта.
     * @exception IllegalStateException если цепочка владельцев не соответствует базе данных.
     */
    FxАтрибутный<?> предок( Deque<DbАтрибутный> владельцы, boolean поСвязи ) 
    {
        // поиск кандидата по цепочке владельцев
        FxАтрибутный<?> кандидат = архив();
        while( !владельцы.isEmpty() )
        {
            DbАтрибутный предок = владельцы.poll();
            кандидат = кандидат.коллекции().values().stream()
                .flatMap( c -> c.stream() )
                .filter( e -> e.getSource().equals( предок ) )
                .findAny()
                .orElse( null );
            if( кандидат == null )
                throw new IllegalStateException( "Fx-предок не найден по Db-предку" );
        }
        Supplier<FxАтрибутный> t_get = () -> поСвязи ? null : FxАтрибутный.this; // null при возврате == этот объект
        // проверка поиска
        FxАтрибутный t = кандидат.коллекции().values().stream()
            .flatMap( c -> c.stream() )
            .filter( e -> e.getSource().equals( getSource() ) )
            .findAny()
            .orElseGet( (Supplier)t_get );
        if( t != null )
            return кандидат;
        else
            throw new IllegalStateException( "Fx-объект не соответствует объекту поиска предка" );
    }

    /**
     * Выполняет поиск экземпляра объекта в цепочке владельцев
     * от архива до этого элемента. Возвращенный экземпляр 
     * присутствует в коллекции. Для данного объекта это не 
     * обязательно.
     * 
     * @param поСвязи {@code true} если данный объект должен быть
     *          привязан к владельцу по какой-либо связи вхождения.
     * @return экземпляр этого объекта из коллекции или {@code null} если цепочка 
     *          владельцев разорвана (предок удален из базы данных).
     * @exception IllegalStateException если цепочка владельцев не соответствует базе данных.
     */
    public FxАтрибутный<?> предок( boolean поСвязи ) 
    {
        LinkedList<DbАтрибутный> владельцы = new LinkedList<>(); // цепочка предков между архивом и элементом 
        for( DbАтрибутный предок = ЭЛЕМЕНТ.предок( поСвязи ); предок != null; предок = предок.предок() )
            if( предок instanceof DbАрхив )
                return предок( владельцы, поСвязи );
            else
                владельцы.add( 0, предок );
        return null; // цепочка владельцев разорвана (предок удален из БД)
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
