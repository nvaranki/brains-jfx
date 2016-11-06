package com.varankin.brains.jfx.db;

import com.varankin.brains.db.DbМусор;
import javafx.beans.property.ReadOnlyListProperty;

/**
 *
 * @author Varankine
 */
public final class FxМусор extends FxАтрибутный<DbМусор>
{
    private final ReadOnlyListProperty<FxАтрибутный> МУСОР;

    public FxМусор( DbМусор мусор ) 
    {
        super( мусор );
        МУСОР = buildReadOnlyListProperty( мусор, "мусор", 
            new FxList<>( мусор.мусор(), e -> new FxАтрибутный( e ), e -> e.getSource() ) );
    }

    public ReadOnlyListProperty<FxАтрибутный> мусор()
    {
        return МУСОР;
    }
    
}
