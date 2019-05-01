package com.varankin.brains.jfx.db;

import com.varankin.brains.db.DbИнструкция;
import com.varankin.brains.db.КороткийКлюч;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.WeakChangeListener;

import static com.varankin.brains.db.DbИнструкция.*;

/**
 *
 *  
 * @author &copy; 2019 Николай Варанкин
 */
public final class FxИнструкция extends FxАтрибутный<DbИнструкция>
{
    static final КороткийКлюч КЛЮЧ_А_ВЫПОЛНИТЬ = new КороткийКлюч( "выполнить", null );

    private final FxPropertyImpl<String> КОД;
    private final FxPropertyImpl<String> ПРОЦЕССОР;
    private final FxReadOnlyPropertyImpl<String> ВЫПОЛНИТЬ;
    private final ChangeListener<String> CL_КОД;
    private final ChangeListener<String> CL_ПРОЦЕССОР;

    public FxИнструкция( DbИнструкция элемент ) 
    {
        super( элемент );
        КОД       = new FxPropertyImpl<>(         элемент, "код",       КЛЮЧ_А_КОД,       () -> элемент.код(),       t -> элемент.код( t )       );
        ПРОЦЕССОР = new FxPropertyImpl<>(         элемент, "процессор", КЛЮЧ_А_ПРОЦЕССОР, () -> элемент.процессор(), t -> элемент.процессор( t ) );
        ВЫПОЛНИТЬ = new FxReadOnlyPropertyImpl<>( элемент, "выполнить", КЛЮЧ_А_ВЫПОЛНИТЬ, () -> элемент.выполнить() );
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
