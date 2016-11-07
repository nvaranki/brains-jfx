package com.varankin.brains.jfx.db;

import com.varankin.brains.db.DbКонтакт;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyListProperty;

/**
 *
 * @author Varankine
 */
public final class FxКонтакт extends FxЭлемент<DbКонтакт>
{
    private final ReadOnlyListProperty<FxПараметр> ПАРАМЕТРЫ;
    private final ReadOnlyListProperty<FxКлассJava> КЛАССЫ;
    private final IntegerProperty ПРИОРИТЕТ;
    private final ObjectProperty<Short> СВОЙСТВА;

    public FxКонтакт( DbКонтакт контакт ) 
    {
        super( контакт );
        ПАРАМЕТРЫ = buildReadOnlyListProperty( контакт, "параметры", 
            new FxList<>( контакт.параметры(), e -> new FxПараметр( e ), e -> e.getSource() ) );
        КЛАССЫ = buildReadOnlyListProperty( контакт, "классы", 
            new FxList<>( контакт.классы(), e -> new FxКлассJava( e ), e -> e.getSource() ) );
        ПРИОРИТЕТ = buildIntegerProperty( контакт, "приоритет" );
        СВОЙСТВА = buildObjectProperty( контакт, "свойства" );
    }

    public ReadOnlyListProperty<FxПараметр> параметры()
    {
        return ПАРАМЕТРЫ;
    }
    
    public ReadOnlyListProperty<FxКлассJava> классы()
    {
        return КЛАССЫ;
    }
    
    public IntegerProperty приоритет()
    {
        return ПРИОРИТЕТ;
    }
    
    public ObjectProperty<Short> свойства()
    {
        return СВОЙСТВА;
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
