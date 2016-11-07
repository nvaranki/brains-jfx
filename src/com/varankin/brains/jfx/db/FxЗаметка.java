package com.varankin.brains.jfx.db;

import com.varankin.brains.db.DbЗаметка;
import javafx.beans.property.ReadOnlyListProperty;

/**
 *
 * @author Varankine
 */
public final class FxЗаметка extends FxУзел<DbЗаметка>
{
    private final ReadOnlyListProperty<FxГрафика> ГРАФИКИ;

    public FxЗаметка( DbЗаметка конвертер )
    {
        super( конвертер );
        ГРАФИКИ = buildReadOnlyListProperty( конвертер, "графики", 
            new FxList<>( конвертер.графики(), e -> new FxГрафика( e ), e -> e.getSource() ) );
    }

    public ReadOnlyListProperty<FxГрафика> графики()
    {
        return ГРАФИКИ;
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
