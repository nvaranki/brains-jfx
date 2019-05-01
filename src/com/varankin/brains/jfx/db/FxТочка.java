package com.varankin.brains.jfx.db;

import com.varankin.brains.db.DbТочка;
import javafx.beans.property.ReadOnlyListProperty;

import static com.varankin.brains.db.DbТочка.*;

/**
 *
 *  
 * @author &copy; 2019 Николай Варанкин
 */
public final class FxТочка extends FxЭлемент<DbТочка>
{
    private final ReadOnlyListProperty<FxТочка> ТОЧКИ;
    private final ReadOnlyListProperty<FxПараметр> ПАРАМЕТРЫ;
    private final ReadOnlyListProperty<FxКлассJava> КЛАССЫ;
    private final FxPropertyImpl<Integer> ИНДЕКС;
    private final FxPropertyImpl<Boolean> ДАТЧИК;
    private final FxPropertyImpl<Float> ПОРОГ;
    private final FxPropertyImpl<String> КОНТАКТ;

    public FxТочка( DbТочка элемент ) 
    {
        super( элемент );
        ТОЧКИ = buildReadOnlyListProperty( элемент, "точки", 
            new FxList<>( элемент.точки(), элемент, e -> new FxТочка( e ), e -> e.getSource() ) );
        ПАРАМЕТРЫ = buildReadOnlyListProperty( элемент, "параметры", 
            new FxList<>( элемент.параметры(), элемент, e -> new FxПараметр( e ), e -> e.getSource() ) );
        КЛАССЫ = buildReadOnlyListProperty( элемент, "классы", 
            new FxList<>( элемент.классы(), элемент, e -> new FxКлассJava( e ), e -> e.getSource() ) );
        ИНДЕКС  = new FxPropertyImpl<>( элемент, "индекс",  КЛЮЧ_А_ИНДЕКС,  () -> элемент.индекс(),  t -> элемент.индекс( t )  );
        ДАТЧИК  = new FxPropertyImpl<>( элемент, "датчик",  КЛЮЧ_А_ДАТЧИК,  () -> элемент.датчик(),  t -> элемент.датчик( t )  );
        ПОРОГ   = new FxPropertyImpl<>( элемент, "порог",   КЛЮЧ_А_ПОРОГ,   () -> элемент.порог(),   t -> элемент.порог( t )   );
        КОНТАКТ = new FxPropertyImpl<>( элемент, "контакт", КЛЮЧ_А_КОНТАКТ, () -> элемент.контакт(), t -> элемент.контакт( t ) );
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
    
    public FxProperty<String> контакт()
    {
        return КОНТАКТ;
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
