package com.varankin.brains.jfx.selector;

import com.varankin.brains.jfx.JavaFX;
import com.varankin.io.container.Provider;
import java.io.File;
import javafx.stage.DirectoryChooser;

/**
 * Интерактивный поставщик папок локальной файловой системы.
 * 
 * @author &copy; 2016 Николай Варанкин
 */
public class LocalFolderSelector implements Provider<File>
{
    private final DirectoryChooser селектор;

    public LocalFolderSelector() 
    {
        селектор = new DirectoryChooser();
        селектор.setInitialDirectory( JavaFX.getInstance().getCurrentLocalDirectory() );
    }

    @Override
    public File newInstance() 
    {
        File выбор = селектор.showDialog( JavaFX.getInstance().платформа );
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
    
    public void setTitle( String text )
    {
        селектор.setTitle( text );
    }
    
}
