package com.varankin.brains.jfx;

import com.varankin.brains.artificial.io.xml.ЗагрузчикImpl;
import com.varankin.brains.Контекст;
import com.varankin.io.container.Provider;
import com.varankin.io.stream.FileInputStreamProvider;
import com.varankin.util.Текст;
import java.io.*;
import java.util.logging.*;
import javafx.application.Platform;
import javafx.concurrent.*;
import javafx.event.*;
import javafx.stage.FileChooser;

/**
 * Действие JavaFX для установки источника данных о мыслительной структуре.
 * Источником служит файл формата XML.
 *
 * @author &copy; 2012 Николай Варанкин
 */
class ApplicationActionRepositoryXml extends Action
{
    static private final Logger LOGGER = Logger.getLogger( ApplicationActionRepositoryXml.class.getName() );

    private final ApplicationView.Context context;
    private final FileChooser chooser;

    ApplicationActionRepositoryXml( ApplicationView.Context context )
    {
        super( context.jfx, Текст.ПАКЕТЫ.словарь( ApplicationActionRepositoryXml.class, 
                context.jfx.контекст.специфика ) );
        this.context = context;
        this.chooser = new FileChooser();
        chooser.setInitialDirectory( context.jfx.getCurrentLocalDirectory() );
        FileChooser.ExtensionFilter filter = new FileChooser.ExtensionFilter( словарь.текст( "ext.xml" ), "*.xml" );
        chooser.getExtensionFilters().add( filter );
    }

    @Override
    public void handle( ActionEvent event )
    {
        ActionFactory actions = context.actions;
        actions.getRepositorySql().setEnabled( false );
        actions.getRepositoryXml().setEnabled( false );
        actions.getRepositoryInThePast().setEnabled( false );
        actions.getLoad().setEnabled( false );

        Platform.runLater( new FileSelector() );
    }
    
    private void finish( Агент агент )
    {
        ActionFactory actions = context.actions;
        actions.getRepositorySql().setEnabled( true );
        actions.getRepositoryXml().setEnabled( true );
        actions.getRepositoryInThePast().setEnabled( агент != null );
        actions.getLoad().setEnabled( агент != null );
    }
    
    private class FileSelector implements Runnable
    {
        @Override
        public void run()
        {
            File returnVal = chooser.showOpenDialog( context.jfx.платформа );
            if( returnVal != null ) 
            {
                String путь = returnVal.getAbsolutePath();
                LOGGER.log( Level.FINE, словарь.текст( "path", путь ) );
                File директория = returnVal.getParentFile();
                if( директория != null )
                    chooser.setInitialDirectory( директория );
                jfx.getExecutorService().execute( new Агент( 
                    new FileInputStreamProvider( returnVal ), textProperty().get() ) );
            }
            else
            {
                context.actions.getRepositoryInThePast().агент( null, null );//TODO а если загрузчик есть, но отмеенен?
                finish( null );
            }
        }
    }
    
    private class Агент extends Task<Void>
    {
        final Provider<InputStream> поставщик;
        final Контекст контекст;

        Агент( Provider<InputStream> поставщик, String title )
        {
            this.контекст = context.jfx.контекст;
            this.поставщик = поставщик;
            updateTitle( title );
        }

        @Override
        protected Void call() throws Exception
        {
            ЗагрузчикImpl загрузчик = new ЗагрузчикImpl( поставщик, контекст.специфика );
            загрузчик.read();
            контекст.загрузчик( загрузчик );
            return null;
        }
        
        @Override
        protected void succeeded()
        {
            Агент агент = new Агент( поставщик, getTitle() );
            context.actions.getRepositoryInThePast().агент( агент, поставщик.toString() );
            finish( агент );
            LOGGER.log( Level.INFO, словарь.текст( "success", поставщик.toString() ) );
        }

        @Override
        protected void failed()
        {
            finish( null );
            context.actions.getRepositoryInThePast().агент( null, null );
            Throwable exception = getException();
            if( exception != null )
                LOGGER.log( Level.SEVERE, словарь.текст( "failure", поставщик.toString() ), exception );
            else
                LOGGER.log( Level.SEVERE, словарь.текст( "failure", поставщик.toString() ) );
        }

        @Override
        protected void cancelled()
        {
            context.actions.getRepositoryInThePast().агент( null, null );
            finish( null );
            LOGGER.log( Level.SEVERE, словарь.текст( "failure", поставщик.toString() ) );
        }

    }
    
}
