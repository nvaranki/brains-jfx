package com.varankin.brains.jfx.db;

import com.varankin.brains.db.DbТекстовыйБлок;
import javafx.beans.property.Property;

/**
 *
 * @author Varankine
 */
public final class FxТекстовыйБлок extends FxАтрибутный<DbТекстовыйБлок>
{
    private final Property<String> ТЕКСТ;

    public FxТекстовыйБлок( DbТекстовыйБлок блок ) 
    {
        super( блок );
        ТЕКСТ = new FxProperty<>( блок, "текст" );
    }

    public Property<String> текст()
    {
        return ТЕКСТ;
    }
    
}
