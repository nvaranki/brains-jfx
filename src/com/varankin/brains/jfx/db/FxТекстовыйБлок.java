package com.varankin.brains.jfx.db;

import com.varankin.brains.db.DbТекстовыйБлок;
import javafx.beans.property.StringProperty;

/**
 *
 * @author Varankine
 */
public final class FxТекстовыйБлок extends FxАтрибутный<DbТекстовыйБлок>
{
    private final StringProperty ТЕКСТ;

    public FxТекстовыйБлок( DbТекстовыйБлок блок ) 
    {
        super( блок );
        ТЕКСТ = buildStringProperty( блок, "текст" );
    }

    public StringProperty текст()
    {
        return ТЕКСТ;
    }
    
}
