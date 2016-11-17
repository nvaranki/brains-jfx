package com.varankin.brains.jfx.db;

import com.varankin.brains.db.DbТочка;
import javafx.beans.property.Property;
import javafx.beans.property.ReadOnlyListProperty;

/**
 *
 * @author Varankine
 */
public final class FxТочка extends FxЭлемент<DbТочка>
{
    private final ReadOnlyListProperty<FxТочка> ТОЧКИ;
    private final ReadOnlyListProperty<FxПараметр> ПАРАМЕТРЫ;
    private final ReadOnlyListProperty<FxКлассJava> КЛАССЫ;
    private final Property<Integer> ИНДЕКС;
    private final Property<Boolean> ДАТЧИК;
    private final Property<Float> ПОРОГ;

    public FxТочка( DbТочка точка ) 
    {
        super( точка );
        ТОЧКИ = buildReadOnlyListProperty( точка, "точки", 
            new FxList<>( точка.точки(), e -> new FxТочка( e ), e -> e.getSource() ) );
        ПАРАМЕТРЫ = buildReadOnlyListProperty( точка, "параметры", 
            new FxList<>( точка.параметры(), e -> new FxПараметр( e ), e -> e.getSource() ) );
        КЛАССЫ = buildReadOnlyListProperty( точка, "классы", 
            new FxList<>( точка.классы(), e -> new FxКлассJava( e ), e -> e.getSource() ) );
        ИНДЕКС = new FxProperty<>( точка, "индекс" );
        ДАТЧИК = new FxProperty<>( точка, "датчик" );
        ПОРОГ = new FxProperty<>( точка, "порог" );
    }

    public ReadOnlyListProperty<FxТочка> точки()
    {
        return ТОЧКИ;
    }
    
    public ReadOnlyListProperty<FxПараметр> параметры()
    {
        return ПАРАМЕТРЫ;
    }
    
    public ReadOnlyListProperty<FxКлассJava> классы()
    {
        return КЛАССЫ;
    }
    
    public Property<Integer> индекс()
    {
        return ИНДЕКС;
    }
    
    public Property<Boolean> датчик()
    {
        return ДАТЧИК;
    }
    
    public Property<Float> порог()
    {
        return ПОРОГ;
    }
    
    @Override
    /*default*/ public <X> X выполнить( FxОператор<X> оператор, FxАтрибутный узел )
    {
        X результат;
        if( узел instanceof FxКлассJava )
            результат = оператор.выполнить( (FxКлассJava)узел, классы() );
        else if( узел instanceof FxПараметр )
            результат = оператор.выполнить( (FxПараметр)узел, параметры() );
        else if( узел instanceof FxТочка )
            результат = оператор.выполнить( (FxТочка)узел, точки() );
        else 
            результат = /*FxЭлемент.*/super.выполнить( оператор, узел );
        return результат;
    }
    
}
