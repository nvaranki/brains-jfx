package com.varankin.brains.jfx.db;

import com.varankin.brains.db.DbПоле;
import javafx.beans.property.ReadOnlyListProperty;

/**
 *
 * @author Varankine
 */
public final class FxПоле extends FxЭлемент<DbПоле> implements FxКоммутируемый
{
    private final ReadOnlyListProperty<FxСоединение> СОЕДИНЕНИЯ;
    private final ReadOnlyListProperty<FxСенсор> СЕНСОРЫ;

    public FxПоле( DbПоле поле ) 
    {
        super( поле );
        СОЕДИНЕНИЯ = buildReadOnlyListProperty( поле, "соединения", 
            new FxList<>( поле.соединения(), поле, e -> new FxСоединение( e ), e -> e.getSource() ) );
        СЕНСОРЫ = buildReadOnlyListProperty( поле, "сенсоры", 
            new FxList<>( поле.сенсоры(), поле, e -> new FxСенсор( e ), e -> e.getSource() ) );
    }

    @Override
    public ReadOnlyListProperty<FxСоединение> соединения()
    {
        return СОЕДИНЕНИЯ;
    }
    
    public ReadOnlyListProperty<FxСенсор> сенсоры()
    {
        return СЕНСОРЫ;
    }
    
    @Override
    /*default*/ public <X> X выполнить( FxОператор<X> оператор, FxАтрибутный узел )
    {
        X результат;
        if( узел instanceof FxСоединение )
            результат = оператор.выполнить( (FxСоединение)узел, соединения() );
        else if( узел instanceof FxСенсор )
            результат = оператор.выполнить( (FxСенсор)узел, сенсоры() );
        else 
            результат = /*FxЭлемент.*/super.выполнить( оператор, узел );
        return результат;
    }
    
}
