package com.varankin.brains.jfx.db;

import com.varankin.brains.db.DbРасчет;
import javafx.beans.property.ReadOnlyListProperty;

/**
 *
 * @author Varankine
 */
public final class FxРасчет extends FxЭлемент<DbРасчет>
{
    private final ReadOnlyListProperty<FxСоединение> СОЕДИНЕНИЯ;
    private final ReadOnlyListProperty<FxТочка> ТОЧКИ;

    public FxРасчет( DbРасчет расчет ) 
    {
        super( расчет );
        СОЕДИНЕНИЯ = buildReadOnlyListProperty( расчет, "соединения", 
            new FxList<>( расчет.соединения(), e -> new FxСоединение( e ), e -> e.getSource() ) );
        ТОЧКИ = buildReadOnlyListProperty( расчет, "точки", 
            new FxList<>( расчет.точки(), e -> new FxТочка( e ), e -> e.getSource() ) );
    }

    public ReadOnlyListProperty<FxСоединение> соединения()
    {
        return СОЕДИНЕНИЯ;
    }
    
    public ReadOnlyListProperty<FxТочка> точки()
    {
        return ТОЧКИ;
    }

    @Override
    /*default*/ public <X> X выполнить( FxОператор<X> оператор, FxАтрибутный узел )
    {
        X результат;
        if( узел instanceof FxСоединение )
            результат = оператор.выполнить( (FxСоединение)узел, соединения() );
        else if( узел instanceof FxТочка )
            результат = оператор.выполнить( (FxТочка)узел, точки() );
        else 
            результат = /*FxЭлемент.*/super.выполнить( оператор, узел );
        return результат;
    }
    
}
