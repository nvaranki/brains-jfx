package com.varankin.brains.jfx.db;

import com.varankin.brains.db.DbУзел;
import javafx.beans.property.ReadOnlyListProperty;

/**
 *
 * @author Varankine
 * @param <T>
 */
public class FxУзел<T extends DbУзел> extends FxАтрибутный<T>
{
    private final ReadOnlyListProperty<FxИнструкция> ИНСТРУКЦИИ;
    private final ReadOnlyListProperty<FxТекстовыйБлок> ТЕКСТЫ;
    private final ReadOnlyListProperty<FxАтрибутный> ПРОЧЕЕ;

    public FxУзел( T элемент )
    {
        super( элемент );
        ИНСТРУКЦИИ = buildReadOnlyListProperty( элемент, "инструкции", 
            new FxList<>( элемент.инструкции(), e -> new FxИнструкция( e ), e -> e.getSource() ) );
        ТЕКСТЫ = buildReadOnlyListProperty( элемент, "тексты", 
            new FxList<>( элемент.тексты(), e -> new FxТекстовыйБлок( e ), e -> e.getSource() ) );
        ПРОЧЕЕ = buildReadOnlyListProperty( элемент, "прочее", 
            new FxList<>( элемент.прочее(), e -> new FxАтрибутный( e ), e -> e.getSource() ) );
    }
    
    public final ReadOnlyListProperty<FxИнструкция> инструкции()
    {
        return ИНСТРУКЦИИ;
    }
    
    public final ReadOnlyListProperty<FxТекстовыйБлок> тексты()
    {
        return ТЕКСТЫ;
    }
    
    public final ReadOnlyListProperty<FxАтрибутный> прочее()
    {
        return ПРОЧЕЕ;
    }
    
}
