package com.varankin.brains.jfx.db;

import com.varankin.brains.db.DbПараметр;
import javafx.beans.property.Property;
import javafx.beans.property.ReadOnlyListProperty;

/**
 *
 * @author Varankine
 */
public final class FxПараметр extends FxЭлемент<DbПараметр>
{
    private final ReadOnlyListProperty<FxПараметр> ПАРАМЕТРЫ;
    private final ReadOnlyListProperty<FxКлассJava> КЛАССЫ;
    private final Property<String> ИНДЕКС;

    public FxПараметр( DbПараметр параметр ) 
    {
        super( параметр );
        ПАРАМЕТРЫ = buildReadOnlyListProperty( параметр, "параметры", 
            new FxList<>( параметр.параметры(), e -> new FxПараметр( e ), e -> e.getSource() ) );
        КЛАССЫ = buildReadOnlyListProperty( параметр, "классы", 
            new FxList<>( параметр.классы(), e -> new FxКлассJava( e ), e -> e.getSource() ) );
        ИНДЕКС = new FxProperty<>( параметр, "индекс" );
    }

    public ReadOnlyListProperty<FxПараметр> параметры()
    {
        return ПАРАМЕТРЫ;
    }
    
    public ReadOnlyListProperty<FxКлассJava> классы()
    {
        return КЛАССЫ;
    }
    
    public Property<String> индекс()
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
