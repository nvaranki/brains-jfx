package com.varankin.brains.jfx.db;

import com.varankin.brains.db.DbАрхив;
import javafx.beans.property.ReadOnlyListProperty;

/**
 *
 * @author Varankine
 */
public final class FxАрхив extends FxАтрибутный<DbАрхив>
{
    private final ReadOnlyListProperty<FxПакет> ПАКЕТЫ;
    private final ReadOnlyListProperty<FxNameSpace> NAMESPACES;
    private final ReadOnlyListProperty<FxМусор> МУСОР;

    public FxАрхив( DbАрхив архив ) 
    {
        super( архив );
        ПАКЕТЫ = buildReadOnlyListProperty( архив, "пакеты", 
            new FxList<>( архив.пакеты(), e -> new FxПакет( e ), e -> e.getSource() ) );
        NAMESPACES = buildReadOnlyListProperty( архив, "namespaces", 
            new FxList<>( архив.namespaces(), e -> new FxNameSpace( e ), e -> e.getSource() ) );
        МУСОР = buildReadOnlyListProperty( архив, "мусор", 
            new FxList<>( архив.мусор(), e -> new FxМусор( e ), e -> e.getSource() ) );
    }

    public ReadOnlyListProperty<FxПакет> пакеты()
    {
        return ПАКЕТЫ;
    }
    
    public ReadOnlyListProperty<FxNameSpace> namespaces()
    {
        return NAMESPACES;
    }
    
    public ReadOnlyListProperty<FxМусор> мусор()
    {
        return МУСОР;
    }
    
}
