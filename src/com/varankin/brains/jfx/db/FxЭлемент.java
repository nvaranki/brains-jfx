package com.varankin.brains.jfx.db;

import com.varankin.brains.db.DbЭлемент;
import javafx.beans.property.ReadOnlyListProperty;
import javafx.beans.property.StringProperty;

/**
 *
 * @author Varankine
 * @param <T>
 */
public class FxЭлемент<T extends DbЭлемент> extends FxУзел<T>
{
    private final ReadOnlyListProperty<FxЗаметка> ЗАМЕТКИ;
    private final ReadOnlyListProperty<FxГрафика> ГРАФИКИ;
    private final StringProperty НАЗВАНИЕ;

    public FxЭлемент( T элемент ) 
    {
        super( элемент );
        ЗАМЕТКИ = buildReadOnlyListProperty( элемент, "заметки", 
            new FxList<>( элемент.заметки(), e -> new FxЗаметка( e ), e -> e.getSource() ) );
        ГРАФИКИ = buildReadOnlyListProperty( элемент, "графики", 
            new FxList<>( элемент.графики(), e -> new FxГрафика( e ), e -> e.getSource() ) );
        НАЗВАНИЕ = buildStringProperty( элемент, "название" );
    }

    public final ReadOnlyListProperty<FxЗаметка> заметки()
    {
        return ЗАМЕТКИ;
    }
    
    public final ReadOnlyListProperty<FxГрафика> графики()
    {
        return ГРАФИКИ;
    }
    
    public final StringProperty название()
    {
        return НАЗВАНИЕ;
    }
    
}
