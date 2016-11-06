package com.varankin.brains.jfx.db;

import com.varankin.brains.db.DbСигнал;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyListProperty;

/**
 *
 * @author Varankine
 */
public final class FxСигнал extends FxЭлемент<DbСигнал>
{
    private final ReadOnlyListProperty<FxПараметр> ПАРАМЕТРЫ;
    private final ReadOnlyListProperty<FxКлассJava> КЛАССЫ;
    private final ObjectProperty<DbСигнал.Приоритет> ПРИОРИТЕТ;

    public FxСигнал( DbСигнал сигнал ) 
    {
        super( сигнал );
        ПАРАМЕТРЫ = buildReadOnlyListProperty( сигнал, "параметры", 
            new FxList<>( сигнал.параметры(), e -> new FxПараметр( e ), e -> e.getSource() ) );
        КЛАССЫ = buildReadOnlyListProperty( сигнал, "классы", 
            new FxList<>( сигнал.классы(), e -> new FxКлассJava( e ), e -> e.getSource() ) );
        ПРИОРИТЕТ = buildObjectProperty( сигнал, "приоритет" );
    }

    public ReadOnlyListProperty<FxПараметр> параметры()
    {
        return ПАРАМЕТРЫ;
    }
    
    public ReadOnlyListProperty<FxКлассJava> классы()
    {
        return КЛАССЫ;
    }
    
    public ObjectProperty<DbСигнал.Приоритет> приоритет()
    {
        return ПРИОРИТЕТ;
    }
    
}
