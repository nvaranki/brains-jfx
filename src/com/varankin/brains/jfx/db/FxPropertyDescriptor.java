package com.varankin.brains.jfx.db;

import com.varankin.brains.db.type.DbАтрибутный;
import com.varankin.brains.db.xml.ЗонныйКлюч;
import com.varankin.brains.db.Транзакция;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * Контейнер свойств для {@link FxProperty} и {@link FxReadOnlyProperty}.
 * 
 * @author &copy; 2021 Николай Варанкин
 */
final class FxPropertyDescriptor<T>
{
    final DbАтрибутный bean;
    final String name;
    final ЗонныйКлюч ключ;
    final Supplier<T> supplier;
    final Consumer<T> consumer;
    
    FxPropertyDescriptor( DbАтрибутный bean, String name, ЗонныйКлюч ключ, 
            Supplier<T> supplier, Consumer<T> consumer )
    {
        this.bean = bean;
        this.name = name;
        this.ключ = ключ;
        this.supplier = supplier;
        this.consumer = consumer;
    }
    
    static <T> T get( DbАтрибутный элемент, Supplier<T> supplier )
    {
        try( final Транзакция т = элемент.транзакция() )
        {
            т.согласовать( Транзакция.Режим.ЧТЕНИЕ_БЕЗ_ЗАПИСИ, элемент.архив() );
            T t = supplier.get();
            т.завершить( true );
            return t;
        }
        catch( Exception e )
        {
            throw new RuntimeException( "Failure to get property value on " + элемент, e );
        }
    }
    
    static <T> void set( DbАтрибутный элемент, Consumer<T> consumer, T value )
    {
        try( final Транзакция т = элемент.транзакция() )
        {
            т.согласовать( Транзакция.Режим.ЗАПРЕТ_ДОСТУПА, элемент.архив() );
            consumer.accept( value );
            т.завершить( true );
        }
        catch( Exception e )
        {
            throw new RuntimeException( "Failure to set property value on " + элемент, e );
        }
    }
    
}
