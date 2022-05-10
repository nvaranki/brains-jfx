package com.varankin.brains.jfx.db;

import com.varankin.brains.db.type.DbПроект;
import javafx.beans.property.ReadOnlyListProperty;

/**
 *
 * @author &copy; 2021 Николай Варанкин
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
            new FxList<>( проект.библиотеки(), проект, FxБиблиотека::new, FxАтрибутный::getSource ) );
        ПРОЦЕССОРЫ = buildReadOnlyListProperty( проект, "процессоры", 
            new FxList<>( проект.процессоры(), проект, FxПроцессор::new, FxАтрибутный::getSource ) );
        ФРАГМЕНТЫ = buildReadOnlyListProperty( проект, "фрагменты", 
            new FxList<>( проект.фрагменты(), проект, FxФрагмент::new, FxАтрибутный::getSource ) );
        СИГНАЛЫ = buildReadOnlyListProperty( проект, "сигналы", 
            new FxList<>( проект.сигналы(), проект, FxСигнал::new, FxАтрибутный::getSource ) );
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

    @Override
    /*default*/ public <X> X выполнить( FxОператор<X> оператор, FxАтрибутный узел )
    {
        X результат;
        if( узел instanceof FxБиблиотека )
            результат = оператор.выполнить( (FxБиблиотека)узел, библиотеки() );
        else if( узел instanceof FxПроцессор )
            результат = оператор.выполнить( (FxПроцессор)узел, процессоры() );
        else if( узел instanceof FxФрагмент )
            результат = оператор.выполнить( (FxФрагмент)узел, фрагменты() );
        else if( узел instanceof FxСигнал )
            результат = оператор.выполнить( (FxСигнал)узел, сигналы() );
        else 
            результат = /*FxЭлемент.*/super.выполнить( оператор, узел );
        return результат;
    }
    
}
