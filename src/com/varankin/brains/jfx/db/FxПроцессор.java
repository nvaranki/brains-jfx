package com.varankin.brains.jfx.db;

import com.varankin.brains.artificial.ПроцессорРасчета;
import com.varankin.brains.db.DbПроцессор;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.LongProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyListProperty;

/**
 *
 * @author Varankine
 */
public final class FxПроцессор extends FxЭлемент<DbПроцессор>
{
    private final ReadOnlyListProperty<FxПараметр> ПАРАМЕТРЫ;
    private final ReadOnlyListProperty<FxКлассJava> КЛАССЫ;
    private final LongProperty ЗАДЕРЖКА;
    private final IntegerProperty НАКОПЛЕНИЕ;
    private final LongProperty ПАУЗА;
    private final BooleanProperty РЕСТАРТ;
    private final BooleanProperty СЖАТИЕ;
    private final BooleanProperty ОЧИСТКА;
    private final ObjectProperty<ПроцессорРасчета.Стратегия> СТРАТЕГИЯ;

    public FxПроцессор( DbПроцессор элемент ) 
    {
        super( элемент );
        ПАРАМЕТРЫ = buildReadOnlyListProperty( элемент, "параметры", 
            new FxList<>( элемент.параметры(), e -> new FxПараметр( e ), e -> e.getSource() ) );
        КЛАССЫ = buildReadOnlyListProperty( элемент, "классы", 
            new FxList<>( элемент.классы(), e -> new FxКлассJava( e ), e -> e.getSource() ) );
        ЗАДЕРЖКА = buildLongProperty( элемент, "задержка" );
        НАКОПЛЕНИЕ = buildIntegerProperty( элемент, "накопление" );
        ПАУЗА = buildLongProperty( элемент, "пауза" );
        РЕСТАРТ = buildBooleanProperty( элемент, "рестарт" );
        СЖАТИЕ = buildBooleanProperty( элемент, "сжатие" );
        ОЧИСТКА = buildBooleanProperty( элемент, "очистка" );
        СТРАТЕГИЯ = buildObjectProperty( элемент, "стратегия" );
    }

    public ReadOnlyListProperty<FxПараметр> параметры()
    {
        return ПАРАМЕТРЫ;
    }
    
    public ReadOnlyListProperty<FxКлассJava> классы()
    {
        return КЛАССЫ;
    }
    
    public LongProperty задержка()
    {
        return ЗАДЕРЖКА;
    }
    
    public IntegerProperty накопление()
    {
        return НАКОПЛЕНИЕ;
    }
    
    public LongProperty пауза()
    {
        return ПАУЗА;
    }
    
    public BooleanProperty рестарт()
    {
        return РЕСТАРТ;
    }
    
    public BooleanProperty сжатие()
    {
        return СЖАТИЕ;
    }
    
    public BooleanProperty очистка()
    {
        return ОЧИСТКА;
    }
    
    public ObjectProperty<ПроцессорРасчета.Стратегия> стратегия()
    {
        return СТРАТЕГИЯ;
    }
    
}
