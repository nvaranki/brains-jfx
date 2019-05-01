package com.varankin.brains.jfx.db;

import com.varankin.brains.db.DbТекстовыйБлок;

import static com.varankin.brains.db.DbТекстовыйБлок.*;

/**
 *
 * @author &copy; 2019 Николай Варанкин
 */
public final class FxТекстовыйБлок extends FxАтрибутный<DbТекстовыйБлок>
{
    private final FxPropertyImpl<String> ТЕКСТ;

    public FxТекстовыйБлок( DbТекстовыйБлок элемент ) 
    {
        super( элемент );
        ТЕКСТ = new FxPropertyImpl<>( элемент, "текст" , КЛЮЧ_А_ТЕКСТ, () -> элемент.текст(), (t) -> элемент.текст( t ) );
    }

    public FxProperty<String> текст()
    {
        return ТЕКСТ;
    }
    
}
