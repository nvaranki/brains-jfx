package com.varankin.brains.jfx.db;

import com.varankin.brains.db.type.DbПараметр;
import javafx.beans.property.ReadOnlyListProperty;

import static com.varankin.brains.db.xml.type.XmlПараметр.*;

/**
 *
 * @author &copy; 2021 Николай Варанкин
 */
public final class FxПараметр extends FxЭлемент<DbПараметр>
{
    private final ReadOnlyListProperty<FxПараметр> ПАРАМЕТРЫ;
    private final ReadOnlyListProperty<FxКлассJava> КЛАССЫ;
    private final FxPropertyImpl<String> ИНДЕКС;
    private final FxPropertyImpl<Integer> ПРИОРИТЕТ;

    public FxПараметр( DbПараметр элемент ) 
    {
        super( элемент );
        ПАРАМЕТРЫ = buildReadOnlyListProperty( элемент, "параметры", 
            new FxList<>( элемент.параметры(), элемент, FxПараметр::new, FxАтрибутный::getSource ) );
        КЛАССЫ = buildReadOnlyListProperty( элемент, "классы", 
            new FxList<>( элемент.классы(), элемент, FxКлассJava::new, FxАтрибутный::getSource ) );
        ИНДЕКС = new FxPropertyImpl<>( элемент, "индекс", КЛЮЧ_А_ИНДЕКС, элемент::индекс, элемент::индекс );
        ПРИОРИТЕТ = new FxPropertyImpl<>( элемент, "приоритет", КЛЮЧ_А_ПРИОРИТЕТ, элемент::приоритет, элемент::приоритет );
    }
    
    public ReadOnlyListProperty<FxПараметр> параметры()
    {
        return ПАРАМЕТРЫ;
    }
    
    public ReadOnlyListProperty<FxКлассJava> классы()
    {
        return КЛАССЫ;
    }
    
    public FxProperty<String> индекс()
    {
        return ИНДЕКС;
    }

    public FxProperty<Integer> приоритет()
    {
        return ПРИОРИТЕТ;
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
