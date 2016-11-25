package com.varankin.brains.jfx.db;

import com.varankin.brains.db.DbКонтакт;
import javafx.beans.property.Property;
import javafx.beans.property.ReadOnlyListProperty;

/**
 *
 * @author Varankine
 */
public final class FxКонтакт extends FxЭлемент<DbКонтакт>
{
    private final ReadOnlyListProperty<FxПараметр> ПАРАМЕТРЫ;
    private final ReadOnlyListProperty<FxКлассJava> КЛАССЫ;
    private final Property<Integer> ПРИОРИТЕТ;
    private final Property<Short> СВОЙСТВА;

    public FxКонтакт( DbКонтакт элемент ) 
    {
        super( элемент );
        ПАРАМЕТРЫ = buildReadOnlyListProperty( элемент, "параметры", 
            new FxList<>( элемент.параметры(), элемент, e -> new FxПараметр( e ), e -> e.getSource() ) );
        КЛАССЫ = buildReadOnlyListProperty( элемент, "классы", 
            new FxList<>( элемент.классы(), элемент, e -> new FxКлассJava( e ), e -> e.getSource() ) );
        ПРИОРИТЕТ = new FxProperty<>( элемент, "приоритет", () -> элемент.приоритет(), (t) -> элемент.приоритет( t ) );
        СВОЙСТВА = new FxProperty<>( элемент, "свойства", () -> элемент.свойства(), (t) -> элемент.свойства( t ) );
    }

    public ReadOnlyListProperty<FxПараметр> параметры()
    {
        return ПАРАМЕТРЫ;
    }
    
    public ReadOnlyListProperty<FxКлассJava> классы()
    {
        return КЛАССЫ;
    }
    
    public Property<Integer> приоритет()
    {
        return ПРИОРИТЕТ;
    }
    
    public Property<Short> свойства()
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
