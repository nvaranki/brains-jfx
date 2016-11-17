package com.varankin.brains.jfx.db;

import com.varankin.brains.artificial.ПроцессорРасчета;
import com.varankin.brains.db.DbПроцессор;
import javafx.beans.property.Property;
import javafx.beans.property.ReadOnlyListProperty;

/**
 *
 * @author Varankine
 */
public final class FxПроцессор extends FxЭлемент<DbПроцессор>
{
    private final ReadOnlyListProperty<FxПараметр> ПАРАМЕТРЫ;
    private final ReadOnlyListProperty<FxКлассJava> КЛАССЫ;
    private final Property<Long> ЗАДЕРЖКА;
    private final Property<Integer> НАКОПЛЕНИЕ;
    private final Property<Long> ПАУЗА;
    private final Property<Boolean> РЕСТАРТ;
    private final Property<Boolean> СЖАТИЕ;
    private final Property<Boolean> ОЧИСТКА;
    private final Property<ПроцессорРасчета.Стратегия> СТРАТЕГИЯ;

    public FxПроцессор( DbПроцессор элемент ) 
    {
        super( элемент );
        ПАРАМЕТРЫ = buildReadOnlyListProperty( элемент, "параметры", 
            new FxList<>( элемент.параметры(), e -> new FxПараметр( e ), e -> e.getSource() ) );
        КЛАССЫ = buildReadOnlyListProperty( элемент, "классы", 
            new FxList<>( элемент.классы(), e -> new FxКлассJava( e ), e -> e.getSource() ) );
        ЗАДЕРЖКА = new FxProperty<>( элемент, "задержка" );
        НАКОПЛЕНИЕ = new FxProperty<>( элемент, "накопление" );
        ПАУЗА = new FxProperty<>( элемент, "пауза" );
        РЕСТАРТ = new FxProperty<>( элемент, "рестарт" );
        СЖАТИЕ = new FxProperty<>( элемент, "сжатие" );
        ОЧИСТКА = new FxProperty<>( элемент, "очистка" );
        СТРАТЕГИЯ = new FxProperty<>( элемент, "стратегия" );
    }

    public ReadOnlyListProperty<FxПараметр> параметры()
    {
        return ПАРАМЕТРЫ;
    }
    
    public ReadOnlyListProperty<FxКлассJava> классы()
    {
        return КЛАССЫ;
    }
    
    public Property<Long> задержка()
    {
        return ЗАДЕРЖКА;
    }
    
    public Property<Integer> накопление()
    {
        return НАКОПЛЕНИЕ;
    }
    
    public Property<Long> пауза()
    {
        return ПАУЗА;
    }
    
    public Property<Boolean> рестарт()
    {
        return РЕСТАРТ;
    }
    
    public Property<Boolean> сжатие()
    {
        return СЖАТИЕ;
    }
    
    public Property<Boolean> очистка()
    {
        return ОЧИСТКА;
    }
    
    public Property<ПроцессорРасчета.Стратегия> стратегия()
    {
        return СТРАТЕГИЯ;
    }

    @Override
    /*default*/ public <X> X выполнить( FxОператор<X> оператор, FxАтрибутный узел )
    {
        X результат;
        if( узел instanceof FxКлассJava )
            результат = оператор.выполнить( (FxКлассJava)узел, классы() );
        else if( узел instanceof FxПараметр )
            результат = оператор.выполнить( (FxПараметр)узел, параметры() );
        else 
            результат = /*FxЭлемент.*/super.выполнить( оператор, узел );
        return результат;
    }
    
}
