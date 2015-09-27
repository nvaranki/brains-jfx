package com.varankin.brains.jfx.archive;

import com.varankin.brains.db.DbМассив;
import com.varankin.brains.jfx.JavaFX;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;

/**
 * FXML-контроллер панели установки значения массива.
 * 
 * @author &copy; 2014 Николай Варанкин
 */
public class PropertiesArrayController extends TabArrayController
{
    private volatile DbМассив массив;
    
    @FXML
    void onActionApply( ActionEvent event )
    {
        //applied.setValue( false );
        JavaFX.getInstance().execute( new ScreenToStorageTask( массив, getAgents() ) );
    }

    @Override
    void reset( DbМассив массив )
    {
        super.reset( this.массив = массив );
        JavaFX.getInstance().execute( new StorageToScreenTask( массив, getAgents() ) );
    }
    
}
