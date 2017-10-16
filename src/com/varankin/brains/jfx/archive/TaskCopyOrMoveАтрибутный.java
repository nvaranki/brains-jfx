package com.varankin.brains.jfx.archive;

import com.varankin.brains.db.Транзакция;
import com.varankin.brains.jfx.JavaFX;
import com.varankin.brains.jfx.db.FxАтрибутный;
import com.varankin.brains.jfx.db.FxОператор;
import com.varankin.util.LoggerX;
import com.varankin.util.Текст;
import java.util.logging.Level;
import javafx.concurrent.Task;

/**
 * Задача копирования или перемеения элемента в другую коллекцию.
 * 
 * @author &copy; 2017 Николай Варанкин
 */
final class TaskCopyOrMoveАтрибутный extends Task<Void>
{
    private static final LoggerX LOGGER = LoggerX.getLogger( TaskCopyOrMoveАтрибутный.class );
    private static final FxОператор ВЛОЖИТЬ = ( о, к ) -> к.add( о );
    private static final FxОператор УДАЛИТЬ = ( о, к ) -> к.remove( о );
    
    private final FxАтрибутный образец, target, parent;

    TaskCopyOrMoveАтрибутный( FxАтрибутный source, FxАтрибутный target, FxАтрибутный parent )
    {
        this.образец = source;
        this.target = target;
        this.parent = parent;
    }

    @Override
    protected Void call() throws Exception
    {
        Транзакция т0 = null;
        Транзакция т1 = null;
        try
        {
            т0 = target.getSource().транзакция();
            т0.согласовать( Транзакция.Режим.ЗАПРЕТ_ДОСТУПА, target.архив().getSource() );

            т1 = образец.getSource().транзакция();
            т1.согласовать( Транзакция.Режим.ЧТЕНИЕ_БЕЗ_ЗАПИСИ, образец.архив().getSource() );

            FxАтрибутный дубликат = FxАтрибутный.дублировать( образец, target.архив() );
            target.выполнить( ВЛОЖИТЬ, дубликат );
            if( parent != null )
                parent.выполнить( УДАЛИТЬ, образец );
            LOGGER.log( Level.INFO, parent != null ? "Moved" : "Copied" );
            
            т1.завершить( true );
            т0.завершить( true );
        }
        finally
        {
            if( т1 != null ) т1.close();
            if( т0 != null ) т0.close();
        }
        return null;
    }

    @Override
    protected void failed()
    {
        Текст словарь = JavaFX.getInstance().словарь( TaskCreateАтрибутный.class );
//        ReadOnlyProperty<DbАтрибутный.Ключ> тип = владелец.тип();
        String msg = "failed";//TODO словарь.текст( "failure", тип.getValue().название() );
        Throwable exception = this.getException();
        LOGGER.log( Level.SEVERE, msg, exception );
    }

}
