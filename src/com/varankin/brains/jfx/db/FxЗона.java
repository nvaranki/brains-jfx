package com.varankin.brains.jfx.db;

import com.varankin.brains.db.type.DbЗона;
import com.varankin.brains.db.xml.BrainsКлюч;

import java.util.List;

import static com.varankin.brains.db.xml.type.XmlЗона.*;

/**
 *
 * @author &copy; 2021 Николай Варанкин
 */
public final class FxЗона extends FxАтрибутный<DbЗона>
{
    static final BrainsКлюч КЛЮЧ_А_НАЗВАНИЕ = new BrainsКлюч( "название" ); // виртуальный атрибут по вариантам

    private final FxReadOnlyPropertyImpl<List<String>> ВАРИАНТЫ;
    private final FxPropertyImpl<String> НАЗВАНИЕ;
    private final FxPropertyImpl<String> URI;

    FxЗона( DbЗона элемент ) 
    {
        super( элемент );
        //ВАРИАНТЫ = new FxPropertyImpl<>( элемент, "варианты", КЛЮЧ_А_ВАРИАНТЫ, элемент::варианты, элемент::варианты );
        ВАРИАНТЫ = new FxReadOnlyPropertyImpl<>( элемент, "варианты", КЛЮЧ_А_ВАРИАНТЫ, элемент::варианты );
        НАЗВАНИЕ = new FxPropertyImpl<>( элемент, "название", КЛЮЧ_А_НАЗВАНИЕ, элемент::название, элемент::название );
        URI      = new FxPropertyImpl<>( элемент, "uri",      КЛЮЧ_А_ЗОНА,     элемент::uri,      элемент::uri      );
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
