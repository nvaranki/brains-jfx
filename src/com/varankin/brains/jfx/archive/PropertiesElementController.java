package com.varankin.brains.jfx.archive;

import com.varankin.brains.db.DbЭлемент;
import com.varankin.brains.jfx.JavaFX;
import com.varankin.util.LoggerX;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;

/**
 * FXML-контроллер панели выбора и установки параметров элемента.
 * 
 * @author &copy; 2014 Николай Варанкин
 */
public final class PropertiesElementController 
        extends TabElementController
{
    private static final LoggerX LOGGER = LoggerX.getLogger( PropertiesElementController.class );

    private volatile DbЭлемент элемент;
    
    @FXML
    public void onActionApply( ActionEvent event )
    {
        //applied.setValue( false );
        JavaFX.getInstance().execute( new ScreenToStorageTask( элемент, getAgents() ) );
    }

    @Override
    public void reset( DbЭлемент элемент )
    {
        this.элемент = элемент;
        super.reset( элемент );
        JavaFX.getInstance().execute( new StorageToScreenTask( элемент, getAgents() ) );
    }
    
}
