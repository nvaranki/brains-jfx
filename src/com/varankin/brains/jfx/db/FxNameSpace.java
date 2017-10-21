package com.varankin.brains.jfx.db;

import com.varankin.brains.db.DbNameSpace;
import com.varankin.brains.db.КороткийКлюч;
import java.util.List;

import static com.varankin.brains.db.DbNameSpace.*;
import static com.varankin.brains.io.xml.XmlBrains.XMLNS_BRAINS;

/**
 *
 * @author Varankine
 */
public final class FxNameSpace extends FxАтрибутный<DbNameSpace>
{
    static final КороткийКлюч КЛЮЧ_А_НАЗВАНИЕ  = new КороткийКлюч( "название",  XMLNS_BRAINS );

    private final FxReadOnlyProperty<List<String>> ВАРИАНТЫ;
    private final FxProperty<String> НАЗВАНИЕ;
    private final FxProperty<String> URI;

    FxNameSpace( DbNameSpace элемент ) 
    {
        super( элемент );
        //ВАРИАНТЫ = new FxProperty<>( элемент, "варианты", КЛЮЧ_А_ВАРИАНТЫ, () -> элемент.варианты(), t -> элемент.варианты( t ) );
        ВАРИАНТЫ = new FxReadOnlyProperty<>( элемент, "варианты", КЛЮЧ_А_ВАРИАНТЫ, () -> элемент.варианты() );
        НАЗВАНИЕ = new FxProperty<>( элемент, "название", КЛЮЧ_А_НАЗВАНИЕ, () -> элемент.название(), t -> элемент.название( t ) );
        URI      = new FxProperty<>( элемент, "uri",      КЛЮЧ_А_ЗОНА,     () -> элемент.uri(),      t -> элемент.uri( t )      );
    }

    public FxReadOnlyProperty<List<String>> варианты()
    {
        return ВАРИАНТЫ;
    }
    
    public FxProperty<String> название()
    {
        return НАЗВАНИЕ;
    }
    
    public FxProperty<String> uri()
    {
        return URI;
    }
    
}
