package com.varankin.brains.jfx.db;

import com.varankin.brains.db.DbАтрибутный;
import com.varankin.brains.db.КороткийКлюч;

import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Supplier;
import javafx.beans.property.ObjectPropertyBase;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.beans.value.WeakChangeListener;
import javafx.concurrent.Task;

/**
 * Свойство {@linkplain FxАтрибутный атрибутного узла базы данных} с возможностью изменения. 
 *
 * <p>Процесс получения значения свойства использует последнее установленное значение. 
 * Процесс установки значения свойства происходит в рамках отдельной 
 * {@linkplain Транзакция транзакции}, с {@linkplain Блокировка блокировкой} 
 * по записи. </p>
 * 
 * <p>Свойство выполняет операции установки и получения значения в рамках текущей
 * {@linkplain Thread нити выполнения}. Свойство применяет прямой вызов методов  
 * {@linkplain FxАтрибутный объекта}. Оно не использует отдельную {@linkplain Task задачу} 
 * для этой цели, так как предполагается, что свойство участвует в групповой операции, 
 * которая выполняется в отдельной задаче. А конкурирующие блокировки групповой и данной 
 * операции могут привести к конфликту доступа.</p>
 *  
 * @author &copy; 2019 Николай Варанкин
 * 
 * @param <T> тип значения свойства.
 */
public final class FxPropertyImpl<T> 
        extends ObjectPropertyBase<T>
        implements FxProperty<T>
{
    private final FxPropertyDescriptor<T> descriptor;
    private final ChangeListener<T> chl;

    FxPropertyImpl( DbАтрибутный элемент, String название, КороткийКлюч ключ, 
            Supplier<T> supplier, Consumer<T> consumer )
    {
        super( FxPropertyDescriptor.get( элемент, supplier ) );
        descriptor = new FxPropertyDescriptor<>( элемент, название, ключ, supplier, consumer );
        chl = this::onValueChange;
        addListener( new WeakChangeListener<>( chl ) );
    }
    
    private void onValueChange( ObservableValue<? extends T> o, T ov, T nv )
    {
        // установить запрошенное значение
        FxPropertyDescriptor.set( descriptor.bean, descriptor.consumer, nv );
        // получить фактически установленное значение (допустимо String="5" -> Long=5L и т.п.)
        T t = FxPropertyDescriptor.get( descriptor.bean, descriptor.supplier );
        // переустановить на актуальное значение, при необходимости
        if( !Objects.deepEquals( nv, t ) ) // Objects.equals(a,b) compares arrays as Object's, so use deepEquals
            set( t );
    };

    @Override
    public КороткийКлюч ключ()
    {
        return descriptor.ключ;
    }

    @Override
    public Runnable getFireHandler() 
    {
        return () -> super.fireValueChangedEvent();
    }

    //<editor-fold defaultstate="collapsed" desc="ObjectPropertyBase">
    
    @Override
    public void bind( ObservableValue<? extends T> newObservable )
    {
        super.bind( newObservable );
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
