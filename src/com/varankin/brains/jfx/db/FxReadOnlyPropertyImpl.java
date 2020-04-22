package com.varankin.brains.jfx.db;

import com.varankin.brains.db.DbАтрибутный;
import com.varankin.brains.db.КороткийКлюч;
import java.util.function.Supplier;
import javafx.beans.property.ReadOnlyObjectPropertyBase;
import javafx.concurrent.Task;

/**
 * Свойство {@linkplain FxАтрибутный атрибутного узла базы данных} без возможности изменения. 
 * Процесс получения значения свойства происходит в рамках отдельной 
 * транзакции, с {@linkplain Блокировка блокировкой} 
 * по чтению.<\p>
 * 
 * <p>Свойство выполняет операцию получения значения в рамках текущей
 * {@linkplain Thread нити выполнения}. Свойство применяет прямой вызов методов  
 * {@linkplain FxАтрибутный объекта}. Оно не использует отдельную {@linkplain Task задачу} 
 * для этой цели, так как предполагается, что свойство участвует в групповой операции, 
 * которая выполняется в отдельной задаче. А конкурирующие блокировки групповой и данной 
 * операции могут привести к конфликту доступа.</p>
 *  
 * @author &copy; 2020 Николай Варанкин
 * 
 * @param <T> тип значения свойства.
 */
public final class FxReadOnlyPropertyImpl<T> 
        extends ReadOnlyObjectPropertyBase<T>
        implements FxReadOnlyProperty<T>
{
    private final FxPropertyDescriptor<T> descriptor;

    FxReadOnlyPropertyImpl( DbАтрибутный элемент, String название, КороткийКлюч ключ,
            Supplier<T> supplier )
    {
        descriptor = new FxPropertyDescriptor<>( элемент, название, ключ, supplier, null );
    }

    @Override
    public КороткийКлюч ключ()
    {
        return descriptor.ключ;
    }

    @Override
    public Runnable getFireHandler() 
    {
        return () -> fireValueChangedEvent();
    }
    
    //<editor-fold defaultstate="collapsed" desc="ObservableObjectValue">
    
    /**
     * Возвращает значения свойства. Применяется прямой вызов методов  
     * {@linkplain FxАтрибутный объекта} в рамках отдельной 
     * {@linkplain Транзакция транзакции}. Так как момент фактического 
     * изменения значения в базе данных незвестен, это исключает 
     * кэширование полученного значения. 
     * 
     * @return значения свойства.
     */
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

    //<editor-fold defaultstate="collapsed" desc="ReadOnlyObjectPropertyBase">

    @Override
    protected void fireValueChangedEvent()
    {
        super.fireValueChangedEvent();
    }
    
    //</editor-fold>
    
}
