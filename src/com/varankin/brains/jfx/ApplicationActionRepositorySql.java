package com.varankin.brains.jfx;

import com.varankin.brains.jfx.ApplicationView.Context;
import java.util.logging.Logger;
import javafx.event.ActionEvent;

/**
 * Действие JavaFX для установки источника данных о мыслительной структуре.
 * Источником служит реляционная база данных с интерфейсом SQL&tm;.
 *
 * @author &copy; 2012 Николай Варанкин
 */
class ApplicationActionRepositorySql extends AbstractContextJfxAction<ApplicationView.Context>
{
    static private final Logger LOGGER = Logger.getLogger( 
            ApplicationActionRepositoryXml.class.getName() );

//    private final DataBaseChooser chooser;

    ApplicationActionRepositorySql( Context context )
    {
        super( context, context.jfx.словарь( ApplicationActionRepositorySql.class ) );
//        chooser = new DataBaseChooser( context );
    }

    @Override
    public void handle( ActionEvent event )
    {
//        контекст.actions.getRepositorySql().setEnabled( false );
//        контекст.actions.getRepositoryXml().setEnabled( false );
//        контекст.actions.getRepositoryInThePast().setEnabled( false );
//        контекст.actions.getRepositoryInThePast().агент( null, null );
//        контекст.actions.getLoad().setEnabled( false );

//        Platform.runLater( new Chooser() );
    }
    
    private ApplicationActionRepositoryInThePast getRepositoryInThePast()
    {
        return null;//TODO контекст.actions.getRepositoryInThePast();
    }

}
