package com.varankin.brains.jfx.db;

import com.varankin.brains.db.DbПоле;
import javafx.beans.property.ReadOnlyListProperty;

/**
 *
 * @author Varankine
 */
public final class FxПоле extends FxЭлемент<DbПоле>
{
    private final ReadOnlyListProperty<FxСоединение> СОЕДИНЕНИЯ;
    private final ReadOnlyListProperty<FxСенсор> СЕНСОРЫ;

    public FxПоле( DbПоле поле ) 
    {
        super( поле );
        СОЕДИНЕНИЯ = buildReadOnlyListProperty( поле, "соединения", 
            new FxList<>( поле.соединения(), e -> new FxСоединение( e ), e -> e.getSource() ) );
        СЕНСОРЫ = buildReadOnlyListProperty( поле, "сенсоры", 
            new FxList<>( поле.сенсоры(), e -> new FxСенсор( e ), e -> e.getSource() ) );
    }

    public ReadOnlyListProperty<FxСоединение> соединения()
    {
        return СОЕДИНЕНИЯ;
    }
    
    public ReadOnlyListProperty<FxСенсор> сенсоры()
    {
        return СЕНСОРЫ;
    }
    
}
