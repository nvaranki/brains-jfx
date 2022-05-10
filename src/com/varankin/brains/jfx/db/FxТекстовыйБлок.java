package com.varankin.brains.jfx.db;

import com.varankin.brains.db.type.DbТекстовыйБлок;

import static com.varankin.brains.db.xml.type.XmlТекстовыйБлок.*;

/**
 *
 * @author &copy; 2021 Николай Варанкин
 */
public final class FxТекстовыйБлок extends FxАтрибутный<DbТекстовыйБлок>
{
    private final FxPropertyImpl<String> ТЕКСТ;
    private final FxPropertyImpl<Long>   СТРОКА;

    public FxТекстовыйБлок( DbТекстовыйБлок элемент ) 
    {
        super( элемент );
        ТЕКСТ  = new FxPropertyImpl<>( элемент, "текст" , КЛЮЧ_А_ТЕКСТ,  элемент::текст,  элемент::текст );
        СТРОКА = new FxPropertyImpl<>( элемент, "строка", КЛЮЧ_А_СТРОКА, элемент::строка, элемент::строка );
    }

    public FxProperty<String> текст()
    {
        return ТЕКСТ;
    }

    public FxProperty<Long> строка()
    {
        return СТРОКА;
    }
    
}
