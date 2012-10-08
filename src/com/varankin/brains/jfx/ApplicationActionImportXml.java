package com.varankin.brains.jfx;

import com.varankin.brains.appl.Импортировать;
import com.varankin.io.container.Provider;
import com.varankin.io.stream.FileInputStreamProvider;
import com.varankin.util.Serializator;
import com.varankin.util.Текст;
import java.io.File;
import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.*;
import java.util.prefs.Preferences;
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
    static private final Preferences PNODE = 
            Preferences.userNodeForPackage( ApplicationActionImportXml.class )
            .node( ApplicationActionImportXml.class.getSimpleName() );

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
        byte[] data = PNODE.getByteArray( Integer.toString( индекс ), null );
        Object object = Serializator.byteArrayToObject( data );
        агент = object instanceof Provider ? new Агент( (Provider<InputStream>)object ) : null;
        setEnabled( агент != null );
        setTitle( индекс, агент );
    }

    @Override
    public void handle( ActionEvent _ )
    {
        setEnabledAll( false );
        if( селектор != null )
            Platform.runLater( селектор );
        else if( агент != null )
            new Агент( агент.поставщик ).execute(); // т.к. одноразовый
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
    
    private void setTitle( int индекс, Агент агент )
    {
        String префикс = Integer.toString( индекс );
        textProperty().setValue( агент != null ? префикс + ' ' + агент.поставщик : префикс );
    }
    
    static private void updateHistory( ApplicationActionImportXml[] history, int индексСписка, Агент агент )
    {
        // скопировать историю агентов в список и почистить
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
                history[i].setTitle( i, замена );
                String key = Integer.toString( i );
                byte[] value = Serializator.objectToByteArray( замена != null ? замена.поставщик : null );
                if( value != null )
                    PNODE.putByteArray( key, value );
                else
                    PNODE.remove( key );
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
                new Агент( агент.поставщик ).execute(); // т.к. одноразовый
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
                Агент агент = new Агент( new FileInputStreamProvider( выбор ));
                return агент;
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
    private class Агент extends ApplicationActionWorker implements Serializable
    {
        final Provider<InputStream> поставщик;
        
        Агент( Provider<InputStream> поставщик )
        {
            super( new Импортировать( поставщик ), jfx.контекст, jfx );
            this.поставщик = поставщик;
            updateTitle( textProperty().getValueSafe() );
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
