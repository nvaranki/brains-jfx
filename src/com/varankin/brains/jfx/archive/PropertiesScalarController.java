package com.varankin.brains.jfx.archive;

import com.varankin.brains.jfx.JavaFX;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;

/**
 * FXML-контроллер панели установки скалярного значения.
 * 
 * @author &copy; 2014 Николай Варанкин
 */
public class PropertiesScalarController extends TabScalarController
{
    private volatile Object скаляр;
    
    @FXML
    void onActionApply( ActionEvent event )
    {
//        //applied.setValue( false );
//        JavaFX.getInstance().execute( new ScreenToStorageTask( скаляр, getAgents() ) );
    }

    @Override
    void reset( Object скаляр )
    {
//        super.reset( this.скаляр = скаляр );
//        JavaFX.getInstance().execute( new StorageToScreenTask( скаляр, getAgents() ) );
    }
    
}
