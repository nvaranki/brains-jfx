package com.varankin.brains.jfx;

import com.varankin.io.container.Provider;
import java.io.File;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

/**
 * Интерактивный поставщик папок локальной файловой системы.
 * 
 * @author &copy; 2016 Николай Варанкин
 */
public class LocalFolderSelector implements Provider<File>
{
    private final Stage платформа;
    private final DirectoryChooser селектор;

    LocalFolderSelector() 
    {
        JavaFX jfx = JavaFX.getInstance();
        платформа = jfx.платформа;
        селектор = new DirectoryChooser();
        селектор.setInitialDirectory( jfx.getCurrentLocalDirectory() );
    }

    @Override
    public File newInstance() 
    {
        File выбор = селектор.showDialog( платформа );
        if( выбор == null )
            return null;
        else
        {
            File директория = выбор.getParentFile();
            if( директория != null )
                селектор.setInitialDirectory( директория );
            return выбор;
        }
    }
    
}
