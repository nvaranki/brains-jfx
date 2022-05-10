package com.varankin.brains.jfx.db;

import com.varankin.brains.db.type.DbКонтакт;
import javafx.beans.property.ReadOnlyListProperty;

import static com.varankin.brains.db.xml.type.XmlКонтакт.*;

/**
 *
 * @author &copy; 2021 Николай Варанкин
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
            new FxList<>( элемент.параметры(), элемент, FxПараметр::new, FxАтрибутный::getSource ) );
        КЛАССЫ = buildReadOnlyListProperty( элемент, "классы", 
            new FxList<>( элемент.классы(), элемент, FxКлассJava::new, FxАтрибутный::getSource ) );
        ПРИОРИТЕТ = new FxPropertyImpl<>( элемент, "приоритет", КЛЮЧ_А_ПРИОРИТЕТ, элемент::приоритет, элемент::приоритет );
        СВОЙСТВА  = new FxPropertyImpl<>( элемент, "свойства",  КЛЮЧ_А_СВОЙСТВА,  элемент::свойства,  элемент::свойства  );
        СИГНАЛ    = new FxPropertyImpl<>( элемент, "сигнал",    КЛЮЧ_А_СИГНАЛ,    элемент::сигнал,    элемент::сигнал    );
        ТОЧКА     = new FxPropertyImpl<>( элемент, "точка",     КЛЮЧ_А_ТОЧКА,     элемент::точка,     элемент::точка     );
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
