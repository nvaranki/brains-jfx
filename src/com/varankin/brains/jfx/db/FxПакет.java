package com.varankin.brains.jfx.db;

import com.varankin.brains.db.DbПакет;
import javafx.beans.property.ReadOnlyListProperty;

import static com.varankin.brains.db.DbПакет.*;

/**
 *
 * @author Varankine
 */
public final class FxПакет extends FxУзел<DbПакет>
{
    private final ReadOnlyListProperty<FxПроект> ПРОЕКТЫ;
    private final ReadOnlyListProperty<FxБиблиотека> БИБЛИОТЕКИ;
    private final FxReadOnlyProperty<String> ВЕРСИЯ;
    private final FxProperty<String> НАЗВАНИЕ;

    public FxПакет( DbПакет элемент ) 
    {
        super( элемент );
        ПРОЕКТЫ = buildReadOnlyListProperty( элемент, "проекты", 
            new FxList<>( элемент.проекты(), элемент, e -> new FxПроект( e ), e -> e.getSource() ) );
        БИБЛИОТЕКИ = buildReadOnlyListProperty( элемент, "библиотеки", 
            new FxList<>( элемент.библиотеки(), элемент, e -> new FxБиблиотека( e ), e -> e.getSource() ) );
        ВЕРСИЯ   = new FxReadOnlyProperty<>( элемент, "версия", КЛЮЧ_А_ВЕРСИЯ, () -> элемент.версия() );
        НАЗВАНИЕ = new FxProperty<>( элемент, "название", КЛЮЧ_А_НАЗВАНИЕ, () -> элемент.название(), t -> элемент.название( t ) );
    }

    public ReadOnlyListProperty<FxПроект> проекты()
    {
        return ПРОЕКТЫ;
    }
    
    public ReadOnlyListProperty<FxБиблиотека> библиотеки()
    {
        return БИБЛИОТЕКИ;
    }
    
    public FxReadOnlyProperty<String> версия()
    {
        return ВЕРСИЯ;
    }

    public FxProperty<String> название()
    {
        return НАЗВАНИЕ;
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
