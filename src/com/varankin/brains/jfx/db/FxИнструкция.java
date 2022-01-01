package com.varankin.brains.jfx.db;

import com.varankin.brains.db.type.DbИнструкция;
import com.varankin.brains.db.xml.ЗонныйКлюч;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.WeakChangeListener;

import static com.varankin.brains.db.xml.type.XmlИнструкция.*;

/**
 *
 *  
 * @author &copy; 2021 Николай Варанкин
 */
public final class FxИнструкция extends FxАтрибутный<DbИнструкция>
{
    static final ЗонныйКлюч КЛЮЧ_А_ВЫПОЛНИТЬ = new ЗонныйКлюч( "выполнить", null );

    private final FxPropertyImpl<String> КОД;
    private final FxPropertyImpl<String> ПРОЦЕССОР;
    private final FxReadOnlyPropertyImpl<String> ВЫПОЛНИТЬ;
    private final ChangeListener<String> CL_КОД;
    private final ChangeListener<String> CL_ПРОЦЕССОР;

    public FxИнструкция( DbИнструкция элемент ) 
    {
        super( элемент );
        КОД       = new FxPropertyImpl<>(         элемент, "код",       КЛЮЧ_А_КОД,       элемент::код,       элемент::код       );
        ПРОЦЕССОР = new FxPropertyImpl<>(         элемент, "процессор", КЛЮЧ_А_ПРОЦЕССОР, элемент::процессор, элемент::процессор );
        ВЫПОЛНИТЬ = new FxReadOnlyPropertyImpl<>( элемент, "выполнить", КЛЮЧ_А_ВЫПОЛНИТЬ, элемент::выполнить );
        CL_КОД = (v,o,n) -> пересчитать();
        CL_ПРОЦЕССОР = (v,o,n) -> пересчитать();
        КОД.addListener( new WeakChangeListener( CL_КОД ) );
        ПРОЦЕССОР.addListener( new WeakChangeListener( CL_ПРОЦЕССОР ) );
    }

    public FxProperty<String> код()
    {
        return КОД;
    }
    
    public FxProperty<String> процессор()
    {
        return ПРОЦЕССОР;
    }
    
    public FxReadOnlyProperty<String> выполнить()
    {
        return ВЫПОЛНИТЬ;
    }
    
    public void пересчитать()
    {
        ВЫПОЛНИТЬ.fireValueChangedEvent();
    }
    
    @Override
    /*default*/ public <X> X выполнить( FxОператор<X> оператор, FxАтрибутный узел )
    {
        throw new UnsupportedOperationException();
    }
    
}
