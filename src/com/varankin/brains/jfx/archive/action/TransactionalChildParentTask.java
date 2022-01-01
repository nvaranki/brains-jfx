package com.varankin.brains.jfx.archive.action;

import com.varankin.brains.db.type.DbАтрибутный;
import com.varankin.brains.db.Транзакция;
import com.varankin.brains.jfx.db.FxАтрибутный;
import com.varankin.brains.jfx.db.FxОператор;
import com.varankin.util.LoggerX;

import java.util.logging.Level;
import java.util.logging.LogRecord;
import javafx.concurrent.Task;
import javafx.scene.control.TreeItem;

/**
 * {@linkplain Task Задача} выполнения операции над элементом 
 * коллекции в рамках отдельной {@linkplain Транзакция транзакции}.
 * 
 * @author &copy; 2021 Николай Варанкин
 */
class TransactionalChildParentTask extends Task<Boolean>
{
    private static final LoggerX LOGGER = LoggerX.getLogger( ActionProcessor.class );
    
    private final String НАЗВАНИЕ;
    private final FxАтрибутный ОБЪЕКТ;
    private final FxАтрибутный ВЛАДЕЛЕЦ;
    private final FxОператор<Boolean> ОПЕРАТОР;
    private final String НЕОЖИДАННОСТЬ;
    private final String УСПЕХ;
    private final String НЕУДАЧА;

    TransactionalChildParentTask( TreeItem<FxАтрибутный> ti, FxОператор<Boolean> оператор,
            String succeeded, String surprise, String failed )
    {
        НАЗВАНИЕ = ti.toString();
        ОБЪЕКТ = ti.getValue();
        ВЛАДЕЛЕЦ = ti.getParent().getValue();
        ОПЕРАТОР = оператор;
        this.УСПЕХ = succeeded;
        this.НЕОЖИДАННОСТЬ = surprise;
        this.НЕУДАЧА = failed;
    }

    @Override
    protected Boolean call() throws Exception
    {
        DbАтрибутный а = ОБЪЕКТ.getSource();
        try( final Транзакция т = а.транзакция() )
        {
            т.согласовать( Транзакция.Режим.ЗАПРЕТ_ДОСТУПА, а.архив() );
            boolean итог = (Boolean)ВЛАДЕЛЕЦ.выполнить( ОПЕРАТОР, ОБЪЕКТ );
            т.завершить( итог );
            return итог;
        }
    }

    @Override
    protected void succeeded()
    {
        if( getValue() )
            LOGGER.getLogger().log( Level.INFO, УСПЕХ, НАЗВАНИЕ );
        else
            LOGGER.getLogger().log( Level.WARNING, НЕОЖИДАННОСТЬ, НАЗВАНИЕ );
    }

    @Override
    protected void failed()
    {
        LogRecord lr = new LogRecord( Level.SEVERE, НЕУДАЧА );
        lr.setParameters( new Object[]{ НАЗВАНИЕ } );
        lr.setResourceBundle( LOGGER.getLogger().getResourceBundle() );
        lr.setThrown( getException() );
        LOGGER.getLogger().log( lr );
    }

}
