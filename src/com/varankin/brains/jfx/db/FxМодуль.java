package com.varankin.brains.jfx.db;

import com.varankin.brains.db.DbМодуль;
import javafx.beans.property.ReadOnlyListProperty;

/**
 *
 * @author Varankine
 */
public final class FxМодуль extends FxЭлемент<DbМодуль>
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
            new FxList<>( модуль.библиотеки(), e -> new FxБиблиотека( e ), e -> e.getSource() ) );
        ПРОЦЕССОРЫ = buildReadOnlyListProperty( модуль, "процессоры", 
            new FxList<>( модуль.процессоры(), e -> new FxПроцессор( e ), e -> e.getSource() ) );
        ФРАГМЕНТЫ = buildReadOnlyListProperty( модуль, "фрагменты", 
            new FxList<>( модуль.фрагменты(), e -> new FxФрагмент( e ), e -> e.getSource() ) );
        СИГНАЛЫ = buildReadOnlyListProperty( модуль, "сигналы", 
            new FxList<>( модуль.сигналы(), e -> new FxСигнал( e ), e -> e.getSource() ) );
        СОЕДИНЕНИЯ = buildReadOnlyListProperty( модуль, "соединения", 
            new FxList<>( модуль.соединения(), e -> new FxСоединение( e ), e -> e.getSource() ) );
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
