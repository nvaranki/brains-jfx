package com.varankin.brains.jfx.db;

import com.varankin.brains.db.DbПакет;
import javafx.beans.property.ReadOnlyListProperty;

/**
 *
 * @author Varankine
 */
public final class FxПакет extends FxУзел<DbПакет>
{
    private final ReadOnlyListProperty<FxПроект> ПРОЕКТЫ;
    private final ReadOnlyListProperty<FxБиблиотека> БИБЛИОТЕКИ;

    public FxПакет( DbПакет пакет ) 
    {
        super( пакет );
        ПРОЕКТЫ = buildReadOnlyListProperty( пакет, "проекты", 
            new FxList<>( пакет.проекты(), e -> new FxПроект( e ), e -> e.getSource() ) );
        БИБЛИОТЕКИ = buildReadOnlyListProperty( пакет, "библиотеки", 
            new FxList<>( пакет.библиотеки(), e -> new FxБиблиотека( e ), e -> e.getSource() ) );
    }

    public ReadOnlyListProperty<FxПроект> проекты()
    {
        return ПРОЕКТЫ;
    }
    
    public ReadOnlyListProperty<FxБиблиотека> библиотеки()
    {
        return БИБЛИОТЕКИ;
    }
    
    @Override
    /*default*/ public <X> X выполнить( FxОператор<X> оператор, FxАтрибутный узел )
    {
        X результат;
        if( узел instanceof FxПроект )
            результат = оператор.выполнить( (FxПроект)узел, проекты() );
        else if( узел instanceof FxБиблиотека )
            результат = оператор.выполнить( (FxБиблиотека)узел, библиотеки() );
        else 
            результат = /*FxУзел.*/super.выполнить( оператор, узел );
        return результат;
    }
    
}
