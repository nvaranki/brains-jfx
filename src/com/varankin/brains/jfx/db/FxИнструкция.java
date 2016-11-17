package com.varankin.brains.jfx.db;

import com.varankin.brains.db.DbИнструкция;
import javafx.beans.property.Property;

/**
 *
 * @author Varankine
 */
public final class FxИнструкция extends FxАтрибутный<DbИнструкция>
{
    private final Property<String> КОД;
    private final Property<String> ПРОЦЕССОР;

    public FxИнструкция( DbИнструкция инструкция ) 
    {
        super( инструкция );
        КОД = new FxProperty<>( инструкция, "код" );
        ПРОЦЕССОР = new FxProperty<>( инструкция, "процессор" );
    }

    public Property<String> код()
    {
        return КОД;
    }
    
    public Property<String> процессор()
    {
        return ПРОЦЕССОР;
    }
    
    @Override
    /*default*/ public <X> X выполнить( FxОператор<X> оператор, FxАтрибутный узел )
    {
        throw new UnsupportedOperationException();
    }
    
}
