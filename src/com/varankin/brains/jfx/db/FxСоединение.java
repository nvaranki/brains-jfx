package com.varankin.brains.jfx.db;

import com.varankin.brains.db.DbСоединение;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ReadOnlyListProperty;

/**
 *
 * @author Varankine
 */
public final class FxСоединение extends FxЭлемент<DbСоединение>
{
    private final ReadOnlyListProperty<FxКонтакт> КОНТАКТЫ;
    private final BooleanProperty МЕСТНОЕ;

    public FxСоединение( DbСоединение соединение ) 
    {
        super( соединение );
        КОНТАКТЫ = buildReadOnlyListProperty( соединение, "контакты", 
            new FxList<>( соединение.контакты(), e -> new FxКонтакт( e ), e -> e.getSource() ) );
        МЕСТНОЕ = buildBooleanProperty( соединение, "местное" );
    }

    public ReadOnlyListProperty<FxКонтакт> контакты()
    {
        return КОНТАКТЫ;
    }
    
    public BooleanProperty местное()
    {
        return МЕСТНОЕ;
    }
    
}
