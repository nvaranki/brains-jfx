package com.varankin.brains.jfx.db;

import com.varankin.brains.db.DbТочка;
import javafx.beans.property.ReadOnlyListProperty;

import static com.varankin.brains.db.DbТочка.*;

/**
 *
 * @author Varankine
 */
public final class FxТочка extends FxЭлемент<DbТочка>
{
    private final ReadOnlyListProperty<FxТочка> ТОЧКИ;
    private final ReadOnlyListProperty<FxПараметр> ПАРАМЕТРЫ;
    private final ReadOnlyListProperty<FxКлассJava> КЛАССЫ;
    private final FxProperty<Integer> ИНДЕКС;
    private final FxProperty<Boolean> ДАТЧИК;
    private final FxProperty<Float> ПОРОГ;

    public FxТочка( DbТочка элемент ) 
    {
        super( элемент );
        ТОЧКИ = buildReadOnlyListProperty( элемент, "точки", 
            new FxList<>( элемент.точки(), элемент, e -> new FxТочка( e ), e -> e.getSource() ) );
        ПАРАМЕТРЫ = buildReadOnlyListProperty( элемент, "параметры", 
            new FxList<>( элемент.параметры(), элемент, e -> new FxПараметр( e ), e -> e.getSource() ) );
        КЛАССЫ = buildReadOnlyListProperty( элемент, "классы", 
            new FxList<>( элемент.классы(), элемент, e -> new FxКлассJava( e ), e -> e.getSource() ) );
        ИНДЕКС = new FxProperty<>( элемент, "индекс", КЛЮЧ_А_ИНДЕКС, () -> элемент.индекс(), t -> элемент.индекс( t ) );
        ДАТЧИК = new FxProperty<>( элемент, "датчик", КЛЮЧ_А_ДАТЧИК, () -> элемент.датчик(), t -> элемент.датчик( t ) );
        ПОРОГ  = new FxProperty<>( элемент, "порог",  КЛЮЧ_А_ПОРОГ,  () -> элемент.порог(),  t -> элемент.порог( t )  );
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
    
    public FxProperty<Integer> индекс()
    {
        return ИНДЕКС;
    }
    
    public FxProperty<Boolean> датчик()
    {
        return ДАТЧИК;
    }
    
    public FxProperty<Float> порог()
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
