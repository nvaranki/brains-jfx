package com.varankin.brains.jfx;

import com.varankin.io.container.Provider;
import com.varankin.util.Текст;
import java.io.File;
import java.util.logging.*;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;

/**
 * Форма выбора файла для экспорта данных.
 * 
 * @author &copy; 2016 Николай Варанкин
 */
public class ExportFileSelector implements Provider<File>
{
    static private final Logger LOGGER = Logger.getLogger( ExportFileSelector.class.getName() );

    private final Текст словарь;
    private final Stage платформа;
    private final FileChooser селектор;

    public ExportFileSelector( ExtensionFilter... фильтры )
    {
        JavaFX jfx = JavaFX.getInstance();
        словарь = Текст.ПАКЕТЫ.словарь( ExportFileSelector.class, jfx.контекст.специфика );
        платформа = jfx.платформа;
        селектор = new FileChooser();
        селектор.setInitialDirectory( jfx.getCurrentLocalDirectory() );
        селектор.getExtensionFilters().addAll( фильтры );
    }
    
    @Override
    public File newInstance()
    {
        File выбор = селектор.showSaveDialog( платформа );
        if( выбор != null )
        {
            String путь = выбор.getAbsolutePath();
            //TODO селектор.getFileFilter();
            LOGGER.log( Level.FINE, словарь.текст( "path", путь ) );
            File директория = выбор.getParentFile();
            if( директория != null )
                селектор.setInitialDirectory( директория );
        }
        return выбор;
    }
    
}
