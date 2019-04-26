package com.varankin.brains.jfx.db;

import com.varankin.brains.db.DbАрхив;
import com.varankin.brains.db.DbАтрибутный;
import com.varankin.brains.db.Транзакция;
import com.varankin.brains.io.xml.XmlBrains;

import java.util.Date;
import javafx.beans.property.ReadOnlyListProperty;
import javafx.beans.property.ReadOnlyProperty;
import javafx.collections.ObservableList;

import static com.varankin.brains.db.DbАрхив.*;

/**
 *
 * @author &copy; 2019 Николай Варанкин
 */
public final class FxАрхив extends FxАтрибутный<DbАрхив>
{
    private final ReadOnlyListProperty<FxПакет> ПАКЕТЫ;
    private final ReadOnlyListProperty<FxNameSpace> NAMESPACES;
    private final ReadOnlyListProperty<FxМусор> МУСОР;
    private final ReadOnlyProperty<String> РАСПОЛОЖЕНИЕ;
    private final FxReadOnlyProperty<Date> СОЗДАН, ИЗМЕНЕН;

    FxАрхив( DbАрхив элемент ) 
    {
        super( элемент );
        ПАКЕТЫ = buildReadOnlyListProperty( элемент, "пакеты", 
            new FxList<>( элемент.пакеты(), элемент, e -> new FxПакет( e ), e -> e.getSource() ) );
        NAMESPACES = buildReadOnlyListProperty( элемент, "namespaces", 
            new FxList<>( элемент.namespaces(), элемент, e -> new FxNameSpace( e ), e -> e.getSource() ) );
        МУСОР = buildReadOnlyListProperty( элемент, "мусор", 
            new FxList<>( элемент.мусор(), элемент, e -> new FxМусор( e ), e -> e.getSource() ) );
        РАСПОЛОЖЕНИЕ = new FxReadOnlyProperty<>( элемент, "расположение", КЛЮЧ_А_РАСПОЛОЖЕНИЕ, () -> элемент.расположение() );
        СОЗДАН       = new FxReadOnlyProperty<>( элемент, "создан",       КЛЮЧ_А_СОЗДАН,       () -> элемент.создан()       );
        ИЗМЕНЕН      = new FxReadOnlyProperty<>( элемент, "изменен",      КЛЮЧ_А_ИЗМЕНЕН,      () -> элемент.изменен()      );
        элемент.обработчик( a -> ИЗМЕНЕН.getFireHandler() );
    }

    public ReadOnlyListProperty<FxПакет> пакеты()
    {
        return ПАКЕТЫ;
    }
    
    public ReadOnlyListProperty<FxNameSpace> namespaces()
    {
        return NAMESPACES;
    }
    
    public ReadOnlyListProperty<FxМусор> мусор()
    {
        return МУСОР;
    }
    
    public ReadOnlyProperty<String> расположение()
    {
        return РАСПОЛОЖЕНИЕ;
    }
    
    public ReadOnlyProperty<Date> создан()
    {
        return СОЗДАН;
    }
    
    public ReadOnlyProperty<Date> изменен()
    {
        return ИЗМЕНЕН;
    }
    
    public FxАтрибутный создатьНовыйЭлемент( String название, String uri )
    {
        return FxФабрика.getInstance().создать( getSource().создатьНовыйЭлемент( название, uri ) );
    }
    
    public FxАтрибутный создатьНовыйЭлемент( DbАтрибутный.Ключ тип )
    {
        return создатьНовыйЭлемент( тип.название(), тип.uri() );
    }
    
    /**
     * Безвозвратно удаляет объект из базы данных.
     * 
     * @param объект удаляемый объект.
     * @return {@code true} если объект был удален.
     */
    public boolean удалить( FxАтрибутный<? extends DbАтрибутный> объект ) 
    {
        объект.getSource().удалить();
        return true;
    }
    
    /**
     * Переносит объект в {@linkplain FxМусор мусорную корзину}.
     * 
     * @param объект переносимый объект.
     * @return {@code true} если объект был перемещен.
     */
    public boolean утилизировать( FxАтрибутный<? extends DbАтрибутный> объект ) 
    {
        if( МУСОР.isEmpty() )
            МУСОР.add( (FxМусор) создатьНовыйЭлемент( 
                    XmlBrains.XML_BASKET, XmlBrains.XMLNS_BRAINS ) );
        boolean утилизировано = false;
        for( FxМусор корзина : МУСОР )
            if( !утилизировано )
                утилизировано = корзина.мусор().add( объект );
        return утилизировано;
    }
    
    private static final FxОператор<Boolean> ОПЕРАТОР_ВЛОЖИТЬ = new FxОператор<Boolean>()
    {
        @Override
        public <T> Boolean выполнить( T объект, ObservableList<T> коллекция )
        {
            return коллекция.add( объект );
        }
    };
    
    /**
     * Возвращает объект из {@linkplain FxМусор мусорной корзины}
     * в коллекцию прежнего владельца.
     * 
     * @param объект переносимый объект.
     * @return {@code true} если объект был перемещен.
     */
    public boolean вернуть( FxАтрибутный<? extends DbАтрибутный> объект ) 
    {
        FxАтрибутный<?> предок = объект.предок( false );
        return предок.выполнить( ОПЕРАТОР_ВЛОЖИТЬ, объект );
    }

    public FxNameSpace определитьПространствоИмен( String uri, String префикс )
    {
        return NAMESPACES.stream()
            .filter( ns -> ns.uri().getValue().equalsIgnoreCase( uri ) )
            .findAny().orElseGet( () -> 
            {
                try( final Транзакция транзакция = getSource().транзакция() )
                {
                    FxNameSpace ns = FxФабрика.getInstance().создать( 
                        getSource().определитьПространствоИмен( uri, префикс ) );
                    транзакция.завершить( NAMESPACES.add( ns ) );
                    return ns;
                }
                catch( Exception e )
                {
                    throw new RuntimeException( "Failed to create namespace", e );
                }
            } );
    }
    
    @Override
    /*default*/ public <X> X выполнить( FxОператор<X> оператор, FxАтрибутный узел )
    {
        X результат;
        if( узел instanceof FxПакет )
            результат = оператор.выполнить( (FxПакет)узел, пакеты() );
        else if( узел instanceof FxМусор )
            результат = оператор.выполнить( (FxМусор)узел, мусор() );
        else if( узел instanceof FxNameSpace )
            //TODO TransactionFailureException: Transaction rolled back even if marked as successful
            результат = оператор.выполнить( (FxNameSpace)узел, namespaces() );
        else if( узел != null )
            throw new ClassCastException( узел.getClass().getName() );
        else 
            throw new NullPointerException();
        return результат;
    }

}
