package com.varankin.brains.jfx.db;

import com.varankin.brains.db.type.DbКлассJava;
import com.varankin.brains.db.xml.ЗонныйКлюч;
import static com.varankin.brains.db.type.DbКлассJava.*;

import static com.varankin.brains.db.xml.type.XmlКлассJava.*;

/**
 *
 * @author &copy; 2021 Николай Варанкин
 */
public final class FxКлассJava extends FxЭлемент<DbКлассJava>
{
    static final ЗонныйКлюч КЛЮЧ_А_КОД = new ЗонныйКлюч( "код", null );

    private final FxPropertyImpl<Назначение> НАЗНАЧЕНИЕ;
    private final FxPropertyImpl<String> КОД;

    public FxКлассJava( DbКлассJava элемент ) 
    {
        super( элемент );
        НАЗНАЧЕНИЕ = new FxPropertyImpl<>( элемент, "назначение", КЛЮЧ_А_НАЗНАЧЕНИЕ, элемент::назначение, элемент::назначение );
        КОД        = new FxPropertyImpl<>( элемент, "код",        КЛЮЧ_А_КОД,        элемент::код,        элемент::код        );
    }
    
    
    public FxProperty<Назначение> назначение()
    {
        return НАЗНАЧЕНИЕ;
    }
    
    public FxProperty<String> код()
    {
        return КОД;
    }

    @Override
    /*default*/ public <X> X выполнить( FxОператор<X> оператор, FxАтрибутный узел )
    {
        return /*FxЭлемент.*/super.выполнить( оператор, узел );
    }
    
}
