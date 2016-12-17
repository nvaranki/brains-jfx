package com.varankin.brains.jfx.db;

import com.varankin.brains.db.DbЗаметка;
import javafx.beans.property.ReadOnlyListProperty;
import javafx.beans.property.ReadOnlyProperty;

/**
 *
 * @author Varankine
 */
public final class FxЗаметка extends FxУзел<DbЗаметка>
{
    private final ReadOnlyListProperty<FxГрафика> ГРАФИКИ;
    private final ReadOnlyProperty<String> ТЕКСТ;

    public FxЗаметка( DbЗаметка элемент )
    {
        super( элемент );
        ГРАФИКИ = buildReadOnlyListProperty( элемент, "графики", 
            new FxList<>( элемент.графики(), элемент, e -> new FxГрафика( e ), e -> e.getSource() ) );
        ТЕКСТ = new FxReadOnlyProperty<>( элемент, "текст", () -> элемент.текст( "\n" ) );
    }

    public ReadOnlyListProperty<FxГрафика> графики()
    {
        return ГРАФИКИ;
    }
    
    public ReadOnlyProperty<String> текст()
    {
        return ТЕКСТ;
    }

    @Override
    /*default*/ public <X> X выполнить( FxОператор<X> оператор, FxАтрибутный узел )
    {
        X результат;
        if( узел instanceof FxГрафика )
            результат = оператор.выполнить( (FxГрафика)узел, графики() );
        else 
            результат = /*FxУзел.*/super.выполнить( оператор, узел );
        return результат;
    }

}
