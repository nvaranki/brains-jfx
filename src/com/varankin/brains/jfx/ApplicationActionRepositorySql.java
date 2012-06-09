package com.varankin.brains.jfx;

import com.varankin.brains.jfx.ApplicationView.Context;
import com.varankin.util.Текст;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.event.ActionEvent;

/**
 * Действие JavaFX для установки источника данных о мыслительной структуре.
 * Источником служит реляционная база данных с интерфейсом SQL&tm;.
 *
 * @author &copy; 2012 Николай Варанкин
 */
class ApplicationActionRepositorySql extends Action
{
    static private final Logger LOGGER = Logger.getLogger( 
            ApplicationActionRepositoryXml.class.getName() );

    private final ApplicationView.Context context;
//    private final DataBaseChooser chooser;

    ApplicationActionRepositorySql( Context context )
    {
        super( context.jfx, Текст.ПАКЕТЫ.словарь( ApplicationActionRepositorySql.class, 
                context.jfx.контекст.специфика ) );
        this.context = context;
//        chooser = new DataBaseChooser( context );
    }

    @Override
    public void handle( ActionEvent event )
    {
        context.actions.getRepositorySql().setEnabled( false );
        context.actions.getRepositoryXml().setEnabled( false );
        context.actions.getRepositoryInThePast().setEnabled( false );
        context.actions.getRepositoryInThePast().агент( null, null );
        context.actions.getLoad().setEnabled( false );

//        Platform.runLater( new Chooser() );
    }

}
