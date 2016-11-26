package com.varankin.brains.jfx.editor;

import com.varankin.brains.db.DbАтрибутный;
import com.varankin.brains.db.Транзакция;
import com.varankin.brains.jfx.db.FxАтрибутный;
import com.varankin.brains.jfx.db.FxОператор;
import com.varankin.brains.jfx.db.FxФабрика;
import com.varankin.brains.jfx.db.FxЭлемент;
import com.varankin.util.LoggerX;

import java.util.LinkedList;
import java.util.Queue;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import javafx.concurrent.Task;
import javafx.scene.Group;
import javafx.scene.Node;

/**
 * {@linkplain Task Задача} по созданию нового элемента и добавлению его в {@linkplain Group группу}.
 * 
 * @author &copy; 2016 Николай Варанкин
 */
class AddTask extends Task<Node>
{
    private static final LoggerX LOGGER = LoggerX.getLogger( AddTask.class );
    
    static final FxОператор оператор = (o, c) -> c.add( o );
    final DbАтрибутный.Ключ ключ;
    final Queue<int[]> path;
    final Group group;
    final FxЭлемент узел;

    AddTask( DbАтрибутный.Ключ ключ, Queue<int[]> path, Group group )
    {
        this.ключ = ключ;
        this.path = new LinkedList<>( path );
        this.group = group;
        Object userDataGroup = group.getUserData();
        this.узел = userDataGroup instanceof FxЭлемент ? (FxЭлемент)userDataGroup : null;
    }

    @Override
    protected Node call() throws Exception
    {
        if( ключ == null | узел == null ) return null;

        boolean добавлено;
        FxАтрибутный fxАтрибутный;
        DbАтрибутный source = узел.getSource();
        try( final Транзакция транзакция = source.транзакция() )
        {
            транзакция.согласовать( Транзакция.Режим.ЗАПРЕТ_ДОСТУПА, source.архив() );
            fxАтрибутный = узел.архив().создатьНовыйЭлемент( ключ.название(), ключ.uri() );
            добавлено = fxАтрибутный != null && (Boolean)узел.выполнить( оператор, fxАтрибутный );
            Node edt = null;
            if( добавлено )
            {
                NodeBuilder создатель = fxАтрибутный != null ? EdtФабрика.getInstance().создать( fxАтрибутный ) : null;
                edt = создатель != null && создатель.составить( path ) ? создатель.загрузить( false ) : null;
            }
            транзакция.завершить( edt != null );
            return edt;
        }
        
    }

    @Override
    protected void succeeded()
    {
        Node edt = getValue();
        if( edt != null )
            group.getChildren().add( edt );
        else
            LOGGER.getLogger().severe( "Failed to create new element." );
    }

    @Override
    protected void failed()
    {
        LogRecord lr = new LogRecord( Level.SEVERE, "task.add.failed" );
        //lr.setParameters( new Object[]{ поставщик } );
        lr.setResourceBundle( LOGGER.getLogger().getResourceBundle() );
        lr.setThrown( getException() );
        LOGGER.getLogger().log( lr );
        path.clear();
    }
    
}
