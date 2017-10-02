package com.varankin.brains.jfx.archive;

import com.varankin.brains.artificial.io.Фабрика;
import com.varankin.brains.db.DbАтрибутный;
import com.varankin.brains.db.Транзакция;
import com.varankin.brains.jfx.JavaFX;
import com.varankin.brains.jfx.db.FxАтрибутный;
import com.varankin.util.LoggerX;
import com.varankin.util.Текст;
import java.util.logging.Level;
import javafx.application.Platform;
import javafx.beans.binding.ListExpression;
import javafx.beans.property.ReadOnlyProperty;
import javafx.concurrent.Task;
import javafx.stage.Stage;

/**
 * Задача создания элемента коллекции.
 * 
 * @author &copy; 2017 Николай Варанкин
 */
class TaskCreateАтрибутный<E extends FxАтрибутный, T extends FxАтрибутный> 
        extends Task<E>
{
    private static final LoggerX LOGGER = LoggerX.getLogger( TaskCreateАтрибутный.class );
    
    private final Фабрика<T, E> фабрика;
    private final T владелец;
    private final ListExpression<E> коллекция;

    TaskCreateАтрибутный( Фабрика<T,E> фабрика, T владелец, ListExpression<E> коллекция ) 
    {
        this.фабрика = фабрика;
        this.владелец = владелец;
        this.коллекция = коллекция;
    }

    @Override
    protected E call() throws Exception 
    {
        try( final Транзакция т = владелец.getSource().транзакция() )
        {
            т.согласовать( Транзакция.Режим.ЗАПРЕТ_ДОСТУПА, владелец.getSource() );
            E элемент = фабрика.создать( владелец );
            if( элемент == null ) 
            {
                cancel();
                т.завершить( false );
            }
            else if( коллекция.add( элемент ) )
            {
                т.завершить( true );
            }
            else
            {
                cancel();
                т.завершить( false );
            }
            return элемент;
        }
    }

    @Override
    protected void succeeded() 
    { 
        final E элемент = getValue();
        Platform.runLater( () -> 
        {
            // открыть свойства
            Stage stage = ActionProcessor.buildProperties( элемент );
            stage.show();
            stage.toFront();
        } );
    }

    @Override
    protected void failed()
    {
        Текст словарь = JavaFX.getInstance().словарь( TaskCreateАтрибутный.class );
        ReadOnlyProperty<DbАтрибутный.Ключ> тип = владелец.тип();
        String msg = словарь.текст( "failure", тип.getValue().название() );
        Throwable exception = this.getException();
        LOGGER.log( Level.SEVERE, msg, exception );
    }

    @Override
    protected void cancelled()
    {
        TaskCreateАтрибутный.this.failed();
    }

}
