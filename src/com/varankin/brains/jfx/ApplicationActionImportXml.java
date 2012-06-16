package com.varankin.brains.jfx;

import com.varankin.brains.appl.Импортировать;
import com.varankin.io.container.Provider;
import com.varankin.io.stream.FileInputStreamProvider;
import com.varankin.util.Текст;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.*;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.stage.FileChooser;

/**
 * Действие JavaFX для импорта данных о мыслительной структуре в локальный архив.
 * Источником служит файл формата XML.
 *
 * @author &copy; 2012 Николай Варанкин
 */
class ApplicationActionImportXml extends Action 
{
    static private final Logger LOGGER = Logger.getLogger( ApplicationActionImportXml.class.getName() );

    private final ActionFactory actions;
    private final Runnable селектор;
    private Агент агент;

    ApplicationActionImportXml( ApplicationView.Context context, FileChooser chooser )
    {
        super( context.jfx, Текст.ПАКЕТЫ.словарь( ApplicationActionImportXml.class, 
                context.jfx.контекст.специфика ) );
        actions = context.actions;
        селектор = new Селектор( chooser );
        setEnabled( true );
    }

    ApplicationActionImportXml( ApplicationView.Context context, int индекс )
    {
        super( context.jfx, Текст.ПАКЕТЫ.словарь( ApplicationActionImportXml.class, 
                context.jfx.контекст.специфика ) );
        actions = context.actions;
        селектор = null;
        setEnabled( false );
        setText( Integer.toString( индекс ) );
    }

    @Override
    public void handle( ActionEvent _ )
    {
        setEnabledAll( false );
        if( селектор != null )
            Platform.runLater( селектор );
        else if( агент != null )
            new Агент( агент ).execute(); // т.к. одноразовый SwingWorker
        else
            setEnabledAll( true );
    }

    @Override
    final void setEnabled( boolean статус )
    {
        super.setEnabled( статус && ( селектор != null || агент != null ) );
    }

    private void setEnabledAll( boolean статус )
    {
        for( Action action : actions.getImportXml() )
            action.setEnabled( статус );
    }
    
    static private void updateHistory( ApplicationActionImportXml[] history, int индексСписка, Агент агент )
    {
        // скопировать историю в список и почистить
        List<Агент> порядок = new ArrayList<>( history.length );
        for( int i = 0; i < history.length; i++ )
            порядок.add( history[i].агент );
        порядок.subList( индексСписка, порядок.size() ).removeAll( Collections.singleton( агент ) );
        
        // вставить агента на приоритетное место
        порядок.add( индексСписка, агент );
        while( порядок.size() < history.length ) порядок.add( null );
        
        // восстановить историю
        for( int i = 0; i < history.length; i++ )
        {
            Агент замена = порядок.get( i );
            history[i].агент = замена;
            if( i >= индексСписка ) 
            {
                String префикс = Integer.toString( i ) + ' ';
                history[i].setText( замена != null ? префикс + замена.поставщик : префикс );
            }
        }
    }
    
    //<editor-fold defaultstate="collapsed" desc="classes">
    
    /**
     * Агент интерактивного запроса файла для загрузки.
     */
    private class Селектор implements Runnable
    {
        final FileChooser chooser;
        
        Селектор( FileChooser chooser )
        {
            this.chooser = chooser;
            chooser.setInitialDirectory( jfx.getCurrentLocalDirectory() );
            FileChooser.ExtensionFilter filter = new FileChooser.ExtensionFilter(
                    словарь.текст( "ext.xml" ), "*.xml" );
            chooser.getExtensionFilters().add( filter );
        }
        
        @Override
        public void run()
        {
            Агент агент = showOpenDialog();
            if( агент != null )
            {
                updateHistory( (ApplicationActionImportXml[])actions.getImportXml(), 1, агент );
                new Агент( агент ).execute(); // т.к. одноразовый Task
            }
            else
            {
                setEnabledAll( true );
            }
        }
        
        Агент showOpenDialog()
        {
            File выбор = chooser.showOpenDialog( jfx.платформа );
            if( выбор != null )
            {
                String путь = выбор.getAbsolutePath();
                LOGGER.log( Level.FINE, словарь.текст( "path", путь ) );
                File директория = выбор.getParentFile();
                if( директория != null )
                    chooser.setInitialDirectory( директория );
                return new Агент( new FileInputStreamProvider( выбор ), getText() );
            }
            else
            {
                return null;
            }
        }
        
    }
    
    /**
     * Одноразовый агент исполнения действия по импорту XML-файла.
     */
    private class Агент extends ApplicationActionWorker
    {
        final Provider<InputStream> поставщик;
        
        Агент( Provider<InputStream> поставщик, String заголовок )
        {
            super( new Импортировать( поставщик ), jfx.контекст, jfx );
            this.поставщик = поставщик;
            updateTitle( заголовок );
        }
        
        Агент( Агент прототип )
        {
            super( new Импортировать( прототип.поставщик ), jfx.контекст, jfx );
            this.поставщик = прототип.поставщик;
            updateTitle( прототип.getTitle() );
        }
        
        @Override
        protected void finished()
        {
            setEnabledAll( true );
        }
        
        @Override
        public boolean equals( Object o )
        {
            return o instanceof Агент && поставщик.equals( ((Агент)o).поставщик );
        }

    }
    
    //</editor-fold>
    
}
