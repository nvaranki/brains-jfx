package com.varankin.brains.jfx.db;

import com.varankin.brains.db.DbNameSpace;
import java.util.List;
import javafx.beans.property.Property;

/**
 *
 * @author Varankine
 */
public final class FxNameSpace extends FxАтрибутный<DbNameSpace>
{
    private final Property<List<String>> ВАРИАНТЫ;
    private final Property<String> НАЗВАНИЕ;
    private final Property<String> URI;

    FxNameSpace( DbNameSpace элемент ) 
    {
        super( элемент );
        ВАРИАНТЫ = new FxProperty<>( элемент, "варианты", () -> элемент.варианты(), (t) -> элемент.варианты( t ) );
        НАЗВАНИЕ = new FxProperty<>( элемент, "название", () -> элемент.название(), (t) -> элемент.название( t ) );
        URI      = new FxProperty<>( элемент, "uri",      () -> элемент.uri(),      (t) -> элемент.uri( t ) );
    }

    public Property<List<String>> варианты()
    {
        return ВАРИАНТЫ;
    }
    
    public Property<String> название()
    {
        return НАЗВАНИЕ;
    }
    
    public Property<String> uri()
    {
        return URI;
    }
    
}
