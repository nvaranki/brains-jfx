package com.varankin.brains.jfx.db;

import com.varankin.brains.db.type.DbЗаметка;
import com.varankin.brains.db.xml.ЗонныйКлюч;

import javafx.beans.property.ReadOnlyListProperty;

import static com.varankin.brains.db.xml.type.XmlЗаметка.*;

/**
 *
 * @author &copy; 2021 Николай Варанкин
 */
public final class FxЗаметка extends FxУзел<DbЗаметка>
{
    static final ЗонныйКлюч КЛЮЧ_А_ТЕКСТ = new ЗонныйКлюч( "текст", null );

    private final ReadOnlyListProperty<FxГрафика> ГРАФИКИ;
    private final FxReadOnlyPropertyImpl<String> ТЕКСТ;
    private final FxPropertyImpl<Long> ГЛУБИНА;

    public FxЗаметка( DbЗаметка элемент )
    {
        super( элемент );
        ГРАФИКИ = buildReadOnlyListProperty( элемент, "графики", 
            new FxList<>( элемент.графики(), элемент, FxГрафика::new, FxАтрибутный::getSource ) );
        ТЕКСТ = new FxReadOnlyPropertyImpl<>( элемент, "текст", КЛЮЧ_А_ТЕКСТ, () -> элемент.текст( "\n" ) );
        ГЛУБИНА = new FxPropertyImpl<>( элемент, "глубина", КЛЮЧ_А_ГЛУБИНА, элемент::глубина, элемент::глубина );
    }

    public ReadOnlyListProperty<FxГрафика> графики()
    {
        return ГРАФИКИ;
    }
    
    public FxReadOnlyProperty<String> текст()
    {
        return ТЕКСТ;
    }

    public FxProperty<Long> глубина()
    {
        return ГЛУБИНА;
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
