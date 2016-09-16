package com.varankin.brains.jfx.archive;

import com.varankin.brains.jfx.JavaFX;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import com.varankin.brains.db.DbКонвертер;

/**
 * FXML-контроллер панели установки способа конвертирования значения.
 * 
 * @author &copy; 2014 Николай Варанкин
 */
public class PropertiesConverterController extends TabConverterController
{
    private volatile DbКонвертер конвертер;
    
    @FXML
    void onActionApply( ActionEvent event )
    {
        //applied.setValue( false );
        JavaFX.getInstance().execute( new ScreenToStorageTask( конвертер, getAgents() ) );
    }

    @Override
    void reset( DbКонвертер конвертер )
    {
        super.reset( this.конвертер = конвертер );
        JavaFX.getInstance().execute( new StorageToScreenTask( конвертер, getAgents() ) );
    }
    
}
