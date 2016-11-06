package com.varankin.brains.jfx.db;

import com.varankin.brains.db.DbСенсор;
import com.varankin.brains.db.DbСигнал;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyListProperty;

import static com.varankin.brains.jfx.db.FxАтрибутный.buildObjectProperty;

/**
 *
 * @author Varankine
 */
public final class FxСенсор extends FxЭлемент<DbСенсор>
{
    private final ReadOnlyListProperty<FxПараметр> ПАРАМЕТРЫ;
    private final ReadOnlyListProperty<FxКлассJava> КЛАССЫ;
    private final ObjectProperty<DbСигнал.Приоритет> ПРИОРИТЕТ;

    public FxСенсор( DbСенсор сенсор ) 
    {
        super( сенсор );
        ПАРАМЕТРЫ = buildReadOnlyListProperty( сенсор, "параметры", 
            new FxList<>( сенсор.параметры(), e -> new FxПараметр( e ), e -> e.getSource() ) );
        КЛАССЫ = buildReadOnlyListProperty( сенсор, "классы", 
            new FxList<>( сенсор.классы(), e -> new FxКлассJava( e ), e -> e.getSource() ) );
        ПРИОРИТЕТ = buildObjectProperty( сенсор, "приоритет" );
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
