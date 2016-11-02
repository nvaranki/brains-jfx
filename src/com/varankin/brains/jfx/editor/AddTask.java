package com.varankin.brains.jfx.editor;

import com.varankin.brains.db.DbАтрибутный;
import com.varankin.brains.db.DbОператор;
import com.varankin.brains.db.DbЭлемент;
import com.varankin.brains.db.Транзакция;
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
    
    static final DbОператор оператор = (o, c) -> c.add( o );
    final DbАтрибутный.Ключ ключ;
    final Queue<int[]> path;
    final Group group;
    final DbЭлемент узел;

    AddTask( DbАтрибутный.Ключ ключ, Queue<int[]> path, Group group )
    {
        this.ключ = ключ;
        this.path = new LinkedList<>( path );
        this.group = group;
        Object userDataGroup = group.getUserData();
        this.узел = userDataGroup instanceof DbЭлемент ? (DbЭлемент)userDataGroup : null;
    }

    @Override
    protected Node call() throws Exception
    {
        if( ключ == null | узел == null ) return null;

        try( final Транзакция транзакция = узел.транзакция() )
        {
            транзакция.согласовать( Транзакция.Режим.ЗАПРЕТ_ДОСТУПА, узел );
            DbАтрибутный атрибутный = узел.архив().создатьНовыйЭлемент( ключ.название(), ключ.uri() );
            NodeBuilder создатель = атрибутный != null ? EdtФабрика.getInstance().создать( атрибутный ) : null;
            Node edt = создатель != null && создатель.составить( path ) ? создатель.загрузить( false ) : null;
            boolean добавлено = edt != null && (Boolean)узел.выполнить( оператор, атрибутный );
            транзакция.завершить( добавлено );
            return добавлено ? edt : null;
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
