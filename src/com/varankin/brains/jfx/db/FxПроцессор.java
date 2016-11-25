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
            new FxList<>( элемент.параметры(), элемент, e -> new FxПараметр( e ), e -> e.getSource() ) );
        КЛАССЫ = buildReadOnlyListProperty( элемент, "классы", 
            new FxList<>( элемент.классы(), элемент, e -> new FxКлассJava( e ), e -> e.getSource() ) );
        ЗАДЕРЖКА = new FxProperty<>( элемент, "задержка", () -> элемент.задержка(), (t) -> элемент.задержка( t ) );
        НАКОПЛЕНИЕ = new FxProperty<>( элемент, "накопление", () -> элемент.накопление(), (t) -> элемент.накопление( t ) );
        ПАУЗА = new FxProperty<>( элемент, "пауза", () -> элемент.пауза(), (t) -> элемент.пауза( t ) );
        РЕСТАРТ = new FxProperty<>( элемент, "рестарт", () -> элемент.рестарт(), (t) -> элемент.рестарт( t ) );
        СЖАТИЕ = new FxProperty<>( элемент, "сжатие", () -> элемент.сжатие(), (t) -> элемент.сжатие( t ) );
        ОЧИСТКА = new FxProperty<>( элемент, "очистка", () -> элемент.очистка(), (t) -> элемент.очистка( t ) );
        СТРАТЕГИЯ = new FxProperty<>( элемент, "стратегия", () -> элемент.стратегия(), (t) -> элемент.стратегия( t ) );
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
