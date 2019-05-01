package com.varankin.brains.jfx.db;

import com.varankin.brains.db.DbКонтакт;
import javafx.beans.property.ReadOnlyListProperty;

import static com.varankin.brains.db.DbКонтакт.*;

/**
 *
 * @author &copy; 2019 Николай Варанкин
 */
public final class FxКонтакт extends FxЭлемент<DbКонтакт>
{
    private final ReadOnlyListProperty<FxПараметр> ПАРАМЕТРЫ;
    private final ReadOnlyListProperty<FxКлассJava> КЛАССЫ;
    private final FxPropertyImpl<Integer> ПРИОРИТЕТ;
    private final FxPropertyImpl<Short> СВОЙСТВА;
    private final FxPropertyImpl<String> СИГНАЛ;
    private final FxPropertyImpl<String> ТОЧКА;

    public FxКонтакт( DbКонтакт элемент ) 
    {
        super( элемент );
        ПАРАМЕТРЫ = buildReadOnlyListProperty( элемент, "параметры", 
            new FxList<>( элемент.параметры(), элемент, e -> new FxПараметр( e ), e -> e.getSource() ) );
        КЛАССЫ = buildReadOnlyListProperty( элемент, "классы", 
            new FxList<>( элемент.классы(), элемент, e -> new FxКлассJava( e ), e -> e.getSource() ) );
        ПРИОРИТЕТ = new FxPropertyImpl<>( элемент, "приоритет", КЛЮЧ_А_ПРИОРИТЕТ, () -> элемент.приоритет(), t -> элемент.приоритет( t ) );
        СВОЙСТВА  = new FxPropertyImpl<>( элемент, "свойства",  КЛЮЧ_А_СВОЙСТВА,  () -> элемент.свойства(),  t -> элемент.свойства( t )  );
        СИГНАЛ    = new FxPropertyImpl<>( элемент, "сигнал",    КЛЮЧ_А_СИГНАЛ,    () -> элемент.сигнал(),    t -> элемент.сигнал( t )    );
        ТОЧКА     = new FxPropertyImpl<>( элемент, "точка",     КЛЮЧ_А_ТОЧКА,     () -> элемент.точка(),     t -> элемент.точка( t )    );
    }

    public ReadOnlyListProperty<FxПараметр> параметры()
    {
        return ПАРАМЕТРЫ;
    }
    
    public ReadOnlyListProperty<FxКлассJava> классы()
    {
        return КЛАССЫ;
    }
    
    public FxProperty<Integer> приоритет()
    {
        return ПРИОРИТЕТ;
    }
    
    public FxProperty<Short> свойства()
    {
        return СВОЙСТВА;
    }

    public FxProperty<String> сигнал()
    {
        return СИГНАЛ;
    }

    public FxProperty<String> точка()
    {
        return ТОЧКА;
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
