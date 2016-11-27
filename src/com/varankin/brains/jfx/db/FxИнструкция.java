package com.varankin.brains.jfx.db;

import com.varankin.brains.db.DbИнструкция;
import javafx.beans.property.Property;
import javafx.beans.property.ReadOnlyProperty;

/**
 *
 * @author Varankine
 */
public final class FxИнструкция extends FxАтрибутный<DbИнструкция>
{
    private final Property<String> КОД;
    private final Property<String> ПРОЦЕССОР;
    private final ReadOnlyProperty<String> ВЫПОЛНИТЬ;

    public FxИнструкция( DbИнструкция элемент ) 
    {
        super( элемент );
        КОД = new FxProperty<>( элемент, "код", null, () -> элемент.код(), (t) -> элемент.код( t ) );
        ПРОЦЕССОР = new FxProperty<>( элемент, "процессор", null, () -> элемент.процессор(), (t) -> элемент.процессор( t ) );
        ВЫПОЛНИТЬ = new FxReadOnlyProperty<>( элемент, "выполнить", null, () -> элемент.выполнить() );
    }

    public Property<String> код()
    {
        return КОД;
    }
    
    public Property<String> процессор()
    {
        return ПРОЦЕССОР;
    }
    
    public ReadOnlyProperty<String> выполнить()
    {
        return ВЫПОЛНИТЬ;
    }
    
    @Override
    /*default*/ public <X> X выполнить( FxОператор<X> оператор, FxАтрибутный узел )
    {
        throw new UnsupportedOperationException();
    }
    
}
