package com.varankin.brains.jfx.selector;

import com.varankin.brains.jfx.JavaFX;
import com.varankin.io.container.Provider;
import java.io.File;
import javafx.stage.FileChooser;

/**
 * Интерактивный поставщик папок локальной файловой системы.
 * 
 * @author &copy; 2016 Николай Варанкин
 */
public class LocalFileSelector implements Provider<File>
{
    private final FileChooser селектор;

    public LocalFileSelector() 
    {
        селектор = new FileChooser();
        селектор.setInitialDirectory( JavaFX.getInstance().getCurrentLocalDirectory() );
    }

    @Override
    public File newInstance() 
    {
        File выбор = селектор.showOpenDialog( JavaFX.getInstance().платформа );
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
    
    public FileChooser.ExtensionFilter getActiveFilter()
    {
        return селектор.getSelectedExtensionFilter();
    }
    
    public void setFilters( FileChooser.ExtensionFilter... filters )
    {
        селектор.getExtensionFilters().setAll( filters );
    }
    
    public void setTitle( String text )
    {
        селектор.setTitle( text );
    }
    
}
