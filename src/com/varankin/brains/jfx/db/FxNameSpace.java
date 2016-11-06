package com.varankin.brains.jfx.db;

import com.varankin.brains.db.DbNameSpace;
import java.util.List;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.StringProperty;

/**
 *
 * @author Varankine
 */
public final class FxNameSpace extends FxАтрибутный<DbNameSpace>
{
    private final ObjectProperty<List<String>> ВАРИАНТЫ;
    private final StringProperty НАЗВАНИЕ;
    private final StringProperty URI;

    public FxNameSpace( DbNameSpace элемент ) 
    {
        super( элемент );
        ВАРИАНТЫ = buildObjectProperty( элемент, "варианты" );
        НАЗВАНИЕ = buildStringProperty( элемент, "название" );
        URI = buildStringProperty( элемент, "uri" );
    }

    public ObjectProperty<List<String>> варианты()
    {
        return ВАРИАНТЫ;
    }
    
    public StringProperty название()
    {
        return НАЗВАНИЕ;
    }
    
    public StringProperty uri()
    {
        return URI;
    }
    
}
