package com.varankin.brains.jfx.db;

import com.varankin.brains.db.DbКлассJava;
import javafx.beans.property.*;

/**
 *
 * @author Varankine
 */
public final class FxКлассJava extends FxЭлемент<DbКлассJava>
{
    private final ReadOnlyListProperty<FxКонвертер> КОНВЕРТЕРЫ;
    private final Property<Boolean> ОСНОВНОЙ;
    private final Property<String> КОД;

    public FxКлассJava( DbКлассJava элемент ) 
    {
        super( элемент );
        КОНВЕРТЕРЫ = buildReadOnlyListProperty( элемент, "конвертеры", 
            new FxList<>( элемент.конвертеры(), элемент, e -> new FxКонвертер( e ), e -> e.getSource() ) );
        ОСНОВНОЙ = new FxProperty<>( элемент, "основной", () -> элемент.основной(), (t) -> элемент.основной( t ) );
        КОД = new FxProperty<>( элемент, "код", () -> элемент.код(), (t) -> элемент.код( t ) );
    }
    
    
    public ReadOnlyListProperty<FxКонвертер> конвертеры()
    {
        return КОНВЕРТЕРЫ;
    }
    
    public Property<Boolean> основной()
    {
        return ОСНОВНОЙ;
    }
    
    public Property<String> код()
    {
        return КОД;
    }

    @Override
    /*default*/ public <X> X выполнить( FxОператор<X> оператор, FxАтрибутный узел )
    {
        X результат;
        if( узел instanceof FxКонвертер )
            результат = оператор.выполнить( (FxКонвертер)узел, конвертеры() );
        else 
            результат = /*FxЭлемент.*/super.выполнить( оператор, узел );
        return результат;
    }
    
}
