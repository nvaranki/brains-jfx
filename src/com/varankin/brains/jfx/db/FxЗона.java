package com.varankin.brains.jfx.db;

import com.varankin.brains.db.КороткийКлюч;
import com.varankin.brains.db.DbЗона;
import java.util.List;

import static com.varankin.brains.db.DbЗона.*;
import static com.varankin.brains.io.xml.XmlBrains.XMLNS_BRAINS;

/**
 *
 * @author &copy; 2020 Николай Варанкин
 */
public final class FxЗона extends FxАтрибутный<DbЗона>
{
    static final КороткийКлюч КЛЮЧ_А_НАЗВАНИЕ  = new КороткийКлюч( "название",  XMLNS_BRAINS );

    private final FxReadOnlyPropertyImpl<List<String>> ВАРИАНТЫ;
    private final FxPropertyImpl<String> НАЗВАНИЕ;
    private final FxPropertyImpl<String> URI;

    FxЗона( DbЗона элемент ) 
    {
        super( элемент );
        //ВАРИАНТЫ = new FxPropertyImpl<>( элемент, "варианты", КЛЮЧ_А_ВАРИАНТЫ, () -> элемент.варианты(), t -> элемент.варианты( t ) );
        ВАРИАНТЫ = new FxReadOnlyPropertyImpl<>( элемент, "варианты", КЛЮЧ_А_ВАРИАНТЫ, () -> элемент.варианты() );
        НАЗВАНИЕ = new FxPropertyImpl<>( элемент, "название", КЛЮЧ_А_НАЗВАНИЕ, () -> элемент.название(), t -> элемент.название( t ) );
        URI      = new FxPropertyImpl<>( элемент, "uri",      КЛЮЧ_А_ЗОНА,     () -> элемент.uri(),      t -> элемент.uri( t )      );
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
