package com.varankin.brains.jfx.db;

import com.varankin.brains.db.type.DbМодуль;
import javafx.beans.property.ReadOnlyListProperty;

/**
 *
 * @author &copy; 2021 Николай Варанкин
 */
public final class FxМодуль extends FxЭлемент<DbМодуль> implements FxКоммутируемый
{
    private final ReadOnlyListProperty<FxБиблиотека> БИБЛИОТЕКИ;
    private final ReadOnlyListProperty<FxПроцессор> ПРОЦЕССОРЫ;
    private final ReadOnlyListProperty<FxФрагмент> ФРАГМЕНТЫ;
    private final ReadOnlyListProperty<FxСигнал> СИГНАЛЫ;
    private final ReadOnlyListProperty<FxСоединение> СОЕДИНЕНИЯ;

    public FxМодуль( DbМодуль модуль ) 
    {
        super( модуль );
        БИБЛИОТЕКИ = buildReadOnlyListProperty( модуль, "библиотеки", 
            new FxList<>( модуль.библиотеки(), модуль, FxБиблиотека::new, FxАтрибутный::getSource ) );
        ПРОЦЕССОРЫ = buildReadOnlyListProperty( модуль, "процессоры", 
            new FxList<>( модуль.процессоры(), модуль, FxПроцессор::new, FxАтрибутный::getSource ) );
        ФРАГМЕНТЫ = buildReadOnlyListProperty( модуль, "фрагменты", 
            new FxList<>( модуль.фрагменты(), модуль, FxФрагмент::new, FxАтрибутный::getSource ) );
        СИГНАЛЫ = buildReadOnlyListProperty( модуль, "сигналы", 
            new FxList<>( модуль.сигналы(), модуль, FxСигнал::new, FxАтрибутный::getSource ) );
        СОЕДИНЕНИЯ = buildReadOnlyListProperty( модуль, "соединения", 
            new FxList<>( модуль.соединения(), модуль, FxСоединение::new, FxАтрибутный::getSource ) );
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
    public ReadOnlyListProperty<FxСоединение> соединения()
    {
        return СОЕДИНЕНИЯ;
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
        else if( узел instanceof FxСоединение )
            результат = оператор.выполнить( (FxСоединение)узел, соединения() );
        else 
            результат = /*FxЭлемент.*/super.выполнить( оператор, узел );
        return результат;
    }
    
}
