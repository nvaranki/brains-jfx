package com.varankin.brains.jfx.db;

import com.varankin.brains.db.DbЛента;
import javafx.beans.property.ReadOnlyListProperty;

/**
 *
 * @author Varankine
 */
public final class FxЛента extends FxЭлемент<DbЛента>
{
    private final ReadOnlyListProperty<FxСоединение> СОЕДИНЕНИЯ;

    public FxЛента( DbЛента лента ) 
    {
        super( лента );
        СОЕДИНЕНИЯ = buildReadOnlyListProperty( лента, "соединения", 
            new FxList<>( лента.соединения(), e -> new FxСоединение( e ), e -> e.getSource() ) );
    }

    public ReadOnlyListProperty<FxСоединение> соединения()
    {
        return СОЕДИНЕНИЯ;
    }

    @Override
    /*default*/ public <X> X выполнить( FxОператор<X> оператор, FxАтрибутный узел )
    {
        X результат;
        if( узел instanceof FxСоединение )
            результат = оператор.выполнить( (FxСоединение)узел, соединения() );
        else 
            результат = /*FxЭлемент.*/super.выполнить( оператор, узел );
        return результат;
    }
    
}
