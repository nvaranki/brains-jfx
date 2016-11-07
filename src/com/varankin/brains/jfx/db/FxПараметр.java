package com.varankin.brains.jfx.db;

import com.varankin.brains.db.DbПараметр;
import javafx.beans.property.ReadOnlyListProperty;
import javafx.beans.property.StringProperty;

/**
 *
 * @author Varankine
 */
public final class FxПараметр extends FxЭлемент<DbПараметр>
{
    private final ReadOnlyListProperty<FxПараметр> ПАРАМЕТРЫ;
    private final ReadOnlyListProperty<FxКлассJava> КЛАССЫ;
    private final StringProperty ИНДЕКС;

    public FxПараметр( DbПараметр параметр ) 
    {
        super( параметр );
        ПАРАМЕТРЫ = buildReadOnlyListProperty( параметр, "параметры", 
            new FxList<>( параметр.параметры(), e -> new FxПараметр( e ), e -> e.getSource() ) );
        КЛАССЫ = buildReadOnlyListProperty( параметр, "классы", 
            new FxList<>( параметр.классы(), e -> new FxКлассJava( e ), e -> e.getSource() ) );
        ИНДЕКС = buildStringProperty( параметр, "индекс" );
    }

    public ReadOnlyListProperty<FxПараметр> параметры()
    {
        return ПАРАМЕТРЫ;
    }
    
    public ReadOnlyListProperty<FxКлассJava> классы()
    {
        return КЛАССЫ;
    }
    
    public StringProperty индекс()
    {
        return ИНДЕКС;
    }

    @Override
    /*default*/ public <X> X выполнить( FxОператор<X> оператор, FxАтрибутный узел )
    {
        X результат;
        if( узел instanceof FxКлассJava )
            результат = оператор.выполнить( (FxКлассJava)узел, классы() );
        else if( узел instanceof FxПараметр )
            результат = оператор.выполнить( (FxПараметр)узел, параметры() );
        else 
            результат = /*FxЭлемент.*/super.выполнить( оператор, узел );
        return результат;
    }
    
}
