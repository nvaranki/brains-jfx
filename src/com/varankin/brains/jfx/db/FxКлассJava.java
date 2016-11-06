package com.varankin.brains.jfx.db;

import com.varankin.brains.db.DbКлассJava;
import javafx.beans.property.*;

/**
 *
 * @author Varankine
 */
public final class FxКлассJava extends FxЭлемент<DbКлассJava>
{
    private final ReadOnlyListProperty<FxКонвертер> КОНВЕРТЕРЫ;
    private final BooleanProperty ОСНОВНОЙ;
    private final StringProperty КОД;

    public FxКлассJava( DbКлассJava класс ) 
    {
        super( класс );
        КОНВЕРТЕРЫ = buildReadOnlyListProperty( класс, "конвертеры", 
            new FxList<>( класс.конвертеры(), e -> new FxКонвертер( e ), e -> e.getSource() ) );
        ОСНОВНОЙ = buildBooleanProperty( класс, "основной" );
        КОД = buildStringProperty( класс, "код" );
    }
    
    
    public ReadOnlyListProperty<FxКонвертер> конвертеры()
    {
        return КОНВЕРТЕРЫ;
    }
    
    public BooleanProperty основной()
    {
        return ОСНОВНОЙ;
    }
    
    public StringProperty код()
    {
        return КОД;
    }
    
}
