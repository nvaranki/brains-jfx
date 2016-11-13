package com.varankin.brains.jfx.editor;

import com.varankin.brains.db.Транзакция;
import com.varankin.brains.jfx.db.*;
import com.varankin.util.LoggerX;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import javafx.concurrent.Task;
import javafx.scene.Group;
import javafx.scene.Node;

/**
 * {@linkplain Task Задача} по удалению элемента из {@linkplain Group группы}.
 * 
 * @author &copy; 2016 Николай Варанкин
 */
class DeleteTask extends Task<Boolean>
{
    private static final LoggerX LOGGER = LoggerX.getLogger( DeleteTask.class );
    private static final FxОператор оператор = (o, c) -> c.remove( o );
    
    private final FxАтрибутный выбор;
    private final FxУзел узел;
    private final Node node;
    private final Node parent;

    DeleteTask( Node node )
    {
        this.node = node;
        Object userDataNode = node.getUserData();
        this.выбор = userDataNode instanceof FxАтрибутный ? (FxАтрибутный)userDataNode : null;
        this.parent = node.getParent();
        Object userDataParent = parent.getUserData();
        this.узел = userDataParent instanceof FxУзел ? (FxУзел)userDataParent : null;
    }

    @Override
    protected Boolean call() throws Exception
    {
        if( выбор == null | узел == null )
        {
            return false;
        }
        try( final Транзакция транзакция = узел.getSource().транзакция() )
        {
            транзакция.согласовать( Транзакция.Режим.ЗАПРЕТ_ДОСТУПА, узел.getSource() );
            boolean удалено = (Boolean)узел.выполнить( оператор, выбор );
            транзакция.завершить( удалено );
            return удалено;
        }
    }

    @Override
    protected void succeeded()
    {
        if( getValue() )
        {
            if( parent instanceof Group ) 
            {
                boolean removed = ((Group)parent).getChildren().remove( node );
                LOGGER.getLogger().info( "deletion of " + узел + "." + выбор + " result=" + (removed ? "true" : "true but GUI") );
            }
            else
                LOGGER.getLogger().info( "deletion of " + узел + "." + выбор + " result=" + "true but GUI" );
        }
        else
        {
            LOGGER.getLogger().info( "deletion of " + узел + "." + выбор + " result=" + false );
        }
    }

    @Override
    protected void failed()
    {
        LogRecord lr = new LogRecord( Level.SEVERE, "task.delete.failed" );
        //lr.setParameters( new Object[]{ поставщик } );
        lr.setResourceBundle( LOGGER.getLogger().getResourceBundle() );
        lr.setThrown( getException() );
        LOGGER.getLogger().log( lr );
    }
    
}
