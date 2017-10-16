package com.varankin.brains.jfx.db;

import com.varankin.brains.db.DbПараметр;
import javafx.beans.property.ReadOnlyListProperty;

import static com.varankin.brains.db.DbПараметр.*;

/**
 *
 * @author Varankine
 */
public final class FxПараметр extends FxЭлемент<DbПараметр>
{
    private final ReadOnlyListProperty<FxПараметр> ПАРАМЕТРЫ;
    private final ReadOnlyListProperty<FxКлассJava> КЛАССЫ;
    private final FxProperty<String> ИНДЕКС;

    public FxПараметр( DbПараметр элемент ) 
    {
        super( элемент );
        ПАРАМЕТРЫ = buildReadOnlyListProperty( элемент, "параметры", 
            new FxList<>( элемент.параметры(), элемент, e -> new FxПараметр( e ), e -> e.getSource() ) );
        КЛАССЫ = buildReadOnlyListProperty( элемент, "классы", 
            new FxList<>( элемент.классы(), элемент, e -> new FxКлассJava( e ), e -> e.getSource() ) );
        ИНДЕКС = new FxProperty<>( элемент, "индекс", КЛЮЧ_А_ИНДЕКС, () -> элемент.индекс(), (t) -> элемент.индекс( t ) );
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
