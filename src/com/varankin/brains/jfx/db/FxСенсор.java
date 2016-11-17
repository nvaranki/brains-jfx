package com.varankin.brains.jfx.db;

import com.varankin.brains.db.DbСенсор;
import com.varankin.brains.db.DbСигнал;
import javafx.beans.property.Property;
import javafx.beans.property.ReadOnlyListProperty;

/**
 *
 * @author Varankine
 */
public final class FxСенсор extends FxЭлемент<DbСенсор>
{
    private final ReadOnlyListProperty<FxПараметр> ПАРАМЕТРЫ;
    private final ReadOnlyListProperty<FxКлассJava> КЛАССЫ;
    private final Property<DbСигнал.Приоритет> ПРИОРИТЕТ;

    public FxСенсор( DbСенсор сенсор ) 
    {
        super( сенсор );
        ПАРАМЕТРЫ = buildReadOnlyListProperty( сенсор, "параметры", 
            new FxList<>( сенсор.параметры(), e -> new FxПараметр( e ), e -> e.getSource() ) );
        КЛАССЫ = buildReadOnlyListProperty( сенсор, "классы", 
            new FxList<>( сенсор.классы(), e -> new FxКлассJava( e ), e -> e.getSource() ) );
        ПРИОРИТЕТ = new FxProperty<>( сенсор, "приоритет" );
    }

    public ReadOnlyListProperty<FxПараметр> параметры()
    {
        return ПАРАМЕТРЫ;
    }
    
    public ReadOnlyListProperty<FxКлассJava> классы()
    {
        return КЛАССЫ;
    }
    
    public Property<DbСигнал.Приоритет> приоритет()
    {
        return ПРИОРИТЕТ;
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
