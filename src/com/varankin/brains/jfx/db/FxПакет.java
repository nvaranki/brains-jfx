package com.varankin.brains.jfx.db;

import com.varankin.brains.db.DbПакет;
import javafx.beans.property.ReadOnlyListProperty;

/**
 *
 * @author Varankine
 */
public final class FxПакет extends FxУзел<DbПакет>
{
    private final ReadOnlyListProperty<FxПроект> ПРОЕКТЫ;
    private final ReadOnlyListProperty<FxБиблиотека> БИБЛИОТЕКИ;

    public FxПакет( DbПакет пакет ) 
    {
        super( пакет );
        ПРОЕКТЫ = buildReadOnlyListProperty( пакет, "проекты", 
            new FxList<>( пакет.проекты(), e -> new FxПроект( e ), e -> e.getSource() ) );
        БИБЛИОТЕКИ = buildReadOnlyListProperty( пакет, "библиотеки", 
            new FxList<>( пакет.библиотеки(), e -> new FxБиблиотека( e ), e -> e.getSource() ) );
    }

    public ReadOnlyListProperty<FxПроект> проекты()
    {
        return ПРОЕКТЫ;
    }
    
    public ReadOnlyListProperty<FxБиблиотека> библиотеки()
    {
        return БИБЛИОТЕКИ;
    }
    
}
