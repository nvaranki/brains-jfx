package com.varankin.brains.jfx.db;

import com.varankin.brains.db.DbТочка;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.FloatProperty;
import javafx.beans.property.IntegerProperty;
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
    private final IntegerProperty ИНДЕКС;
    private final BooleanProperty ДАТЧИК;
    private final FloatProperty ПОРОГ;

    public FxТочка( DbТочка точка ) 
    {
        super( точка );
        ТОЧКИ = buildReadOnlyListProperty( точка, "точки", 
            new FxList<>( точка.точки(), e -> new FxТочка( e ), e -> e.getSource() ) );
        ПАРАМЕТРЫ = buildReadOnlyListProperty( точка, "параметры", 
            new FxList<>( точка.параметры(), e -> new FxПараметр( e ), e -> e.getSource() ) );
        КЛАССЫ = buildReadOnlyListProperty( точка, "классы", 
            new FxList<>( точка.классы(), e -> new FxКлассJava( e ), e -> e.getSource() ) );
        ИНДЕКС = buildIntegerProperty( точка, "индекс" );
        ДАТЧИК = buildBooleanProperty( точка, "датчик" );
        ПОРОГ = buildFloatProperty( точка, "порог" );
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
    
    public IntegerProperty индекс()
    {
        return ИНДЕКС;
    }
    
    public BooleanProperty датчик()
    {
        return ДАТЧИК;
    }
    
    public FloatProperty порог()
    {
        return ПОРОГ;
    }
    
}
