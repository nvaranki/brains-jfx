package com.varankin.brains.jfx.db;

import com.varankin.brains.artificial.ПроцессорРасчета;
import com.varankin.brains.db.DbПроцессор;
import javafx.beans.property.ReadOnlyListProperty;

import static com.varankin.brains.db.DbПроцессор.*;

/**
 *
 * @author &copy; 2019 Николай Варанкин
 */
public final class FxПроцессор extends FxЭлемент<DbПроцессор>
{
    private final ReadOnlyListProperty<FxПараметр> ПАРАМЕТРЫ;
    private final ReadOnlyListProperty<FxКлассJava> КЛАССЫ;
    private final FxPropertyImpl<Long> ЗАДЕРЖКА;
    private final FxPropertyImpl<Integer> НАКОПЛЕНИЕ;
    private final FxPropertyImpl<Long> ПАУЗА;
    private final FxPropertyImpl<Boolean> РЕСТАРТ;
    private final FxPropertyImpl<Boolean> СЖАТИЕ;
    private final FxPropertyImpl<Boolean> ОЧИСТКА;
    private final FxPropertyImpl<ПроцессорРасчета.Стратегия> СТРАТЕГИЯ;

    public FxПроцессор( DbПроцессор элемент ) 
    {
        super( элемент );
        ПАРАМЕТРЫ = buildReadOnlyListProperty( элемент, "параметры", 
            new FxList<>( элемент.параметры(), элемент, e -> new FxПараметр( e ), e -> e.getSource() ) );
        КЛАССЫ = buildReadOnlyListProperty( элемент, "классы", 
            new FxList<>( элемент.классы(), элемент, e -> new FxКлассJava( e ), e -> e.getSource() ) );
        ЗАДЕРЖКА   = new FxPropertyImpl<>( элемент, "задержка",   КЛЮЧ_А_ЗАДЕРЖКА,   () -> элемент.задержка(),   t -> элемент.задержка( t )   );
        НАКОПЛЕНИЕ = new FxPropertyImpl<>( элемент, "накопление", КЛЮЧ_А_НАКОПЛЕНИЕ, () -> элемент.накопление(), t -> элемент.накопление( t ) );
        ПАУЗА      = new FxPropertyImpl<>( элемент, "пауза",      КЛЮЧ_А_ПАУЗА,      () -> элемент.пауза(),      t -> элемент.пауза( t )      );
        РЕСТАРТ    = new FxPropertyImpl<>( элемент, "рестарт",    КЛЮЧ_А_РЕСТАРТ,    () -> элемент.рестарт(),    t -> элемент.рестарт( t )    );
        СЖАТИЕ     = new FxPropertyImpl<>( элемент, "сжатие",     КЛЮЧ_А_СЖАТИЕ,     () -> элемент.сжатие(),     t -> элемент.сжатие( t )     );
        ОЧИСТКА    = new FxPropertyImpl<>( элемент, "очистка",    КЛЮЧ_А_ОЧИСТКА,    () -> элемент.очистка(),    t -> элемент.очистка( t )    );
        СТРАТЕГИЯ  = new FxPropertyImpl<>( элемент, "стратегия",  КЛЮЧ_А_СТРАТЕГИЯ,  () -> элемент.стратегия(),  t -> элемент.стратегия( t )  );
    }

    public ReadOnlyListProperty<FxПараметр> параметры()
    {
        return ПАРАМЕТРЫ;
    }
    
    public ReadOnlyListProperty<FxКлассJava> классы()
    {
        return КЛАССЫ;
    }
    
    public FxProperty<Long> задержка()
    {
        return ЗАДЕРЖКА;
    }
    
    public FxProperty<Integer> накопление()
    {
        return НАКОПЛЕНИЕ;
    }
    
    public FxProperty<Long> пауза()
    {
        return ПАУЗА;
    }
    
    public FxProperty<Boolean> рестарт()
    {
        return РЕСТАРТ;
    }
    
    public FxProperty<Boolean> сжатие()
    {
        return СЖАТИЕ;
    }
    
    public FxProperty<Boolean> очистка()
    {
        return ОЧИСТКА;
    }
    
    public FxProperty<ПроцессорРасчета.Стратегия> стратегия()
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
