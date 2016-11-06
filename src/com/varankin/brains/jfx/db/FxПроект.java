package com.varankin.brains.jfx.db;

import com.varankin.brains.db.DbПроект;
import javafx.beans.property.ReadOnlyListProperty;
import javafx.collections.FXCollections;

/**
 *
 * @author Varankine
 */
public final class FxПроект extends FxЭлемент<DbПроект>
{
    private final ReadOnlyListProperty<FxБиблиотека> БИБЛИОТЕКИ;
    private final ReadOnlyListProperty<FxПроцессор> ПРОЦЕССОРЫ;
    private final ReadOnlyListProperty<FxФрагмент> ФРАГМЕНТЫ;
    private final ReadOnlyListProperty<FxСигнал> СИГНАЛЫ;

    public FxПроект( DbПроект проект ) 
    {
        super( проект );
        БИБЛИОТЕКИ = buildReadOnlyListProperty( проект, "библиотеки", 
            new FxList<>( проект.библиотеки(), e -> new FxБиблиотека( e ), e -> e.getSource() ) );
        ПРОЦЕССОРЫ = buildReadOnlyListProperty( проект, "процессоры", 
            new FxList<>( проект.процессоры(), e -> new FxПроцессор( e ), e -> e.getSource() ) );
        ФРАГМЕНТЫ = buildReadOnlyListProperty( проект, "фрагменты", 
            new FxList<>( проект.фрагменты(), e -> new FxФрагмент( e ), e -> e.getSource() ) );
        СИГНАЛЫ = buildReadOnlyListProperty( проект, "сигналы", 
            FXCollections.observableList( new FxList<>( проект.сигналы(), 
                e -> new FxСигнал( e ), e -> e.getSource() ) ) );
    }

    public ReadOnlyListProperty<FxБиблиотека> библиотеки()
    {
        return БИБЛИОТЕКИ;
    }
    
    public ReadOnlyListProperty<FxПроцессор> процессоры()
    {
        return ПРОЦЕССОРЫ;
    }
    
    public ReadOnlyListProperty<FxФрагмент> фрагменты()
    {
        return ФРАГМЕНТЫ;
    }
    
    public ReadOnlyListProperty<FxСигнал> сигналы()
    {
        return СИГНАЛЫ;
    }
    
}
