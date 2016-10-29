package com.varankin.brains.jfx.editor;

import com.varankin.brains.db.DbАрхив;
import com.varankin.brains.db.DbАтрибутный;
import com.varankin.brains.db.DbОператор;
import com.varankin.brains.db.DbЭлемент;
import com.varankin.brains.db.Транзакция;
import com.varankin.brains.io.xml.XmlSvg;
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
    final DbАтрибутный.Ключ type;
    final Queue<int[]> path;
    final Group group;
    final DbЭлемент узел;

    AddTask( DbАтрибутный.Ключ type, Queue<int[]> path, Group group )
    {
        this.type = type;
        this.path = new LinkedList<>( path );
        this.group = group;
        Object userDataGroup = group.getUserData();
        this.узел = userDataGroup instanceof DbЭлемент ? (DbЭлемент)userDataGroup : null;
    }

    @Override
    protected Node call() throws Exception
    {
        if( type == null | узел == null )
        {
            return null;
        }
        try( final Транзакция транзакция = узел.транзакция() )
        {
            транзакция.согласовать( Транзакция.Режим.ЗАПРЕТ_ДОСТУПА, узел );
            DbАрхив архив = узел.пакет().архив();
            DbАтрибутный атрибутный = архив.создатьНовыйЭлемент( type.название(), type.uri(), null );
            boolean добавлено = (Boolean)узел.выполнить( оператор, атрибутный );
            Node node = добавлено ? EdtФабрика.getInstance().создать( атрибутный ).загрузить( XmlSvg.XMLNS_SVG.equals( type.uri() ), path ) : null;
            транзакция.завершить( добавлено );
            return node;
        }
    }

    @Override
    protected void succeeded()
    {
        Node edt = getValue();
        if( edt != null )
        {
            group.getChildren().add( edt );
            path.clear();
        }
        else
        {
            path.clear();
        }
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
