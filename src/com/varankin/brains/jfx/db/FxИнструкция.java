package com.varankin.brains.jfx.db;

import com.varankin.brains.db.DbИнструкция;
import javafx.beans.property.StringProperty;

/**
 *
 * @author Varankine
 */
public final class FxИнструкция extends FxАтрибутный<DbИнструкция>
{
    private final StringProperty КОД;
    private final StringProperty ПРОЦЕССОР;

    public FxИнструкция( DbИнструкция инструкция ) 
    {
        super( инструкция );
        КОД = buildStringProperty( инструкция, "код" );
        ПРОЦЕССОР = buildStringProperty( инструкция, "процессор" );
    }

    public StringProperty код()
    {
        return КОД;
    }
    
    public StringProperty процессор()
    {
        return ПРОЦЕССОР;
    }
    
    @Override
    /*default*/ public <X> X выполнить( FxОператор<X> оператор, FxАтрибутный узел )
    {
        throw new UnsupportedOperationException();
    }
    
}
