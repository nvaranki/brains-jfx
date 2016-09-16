package com.varankin.brains.jfx.archive;

import com.varankin.brains.jfx.JavaFX;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import com.varankin.brains.db.DbКлассJava;

/**
 * FXML-контроллер панели закладок для установки параметров класса Java.
 * 
 * @author &copy; 2014 Николай Варанкин
 */
public final class PropertiesClassJavaController extends TabClassJavaController
{
    private volatile DbКлассJava класс;
    
    @FXML
    void onActionApply( ActionEvent event )
    {
        //applied.setValue( false );
        JavaFX.getInstance().execute( new ScreenToStorageTask( класс, getAgents() ) );
    }

    void reset( DbКлассJava класс )
    {
        this.класс = класс;
        super.reset( класс );
        JavaFX.getInstance().execute( new StorageToScreenTask( класс, getAgents() ) );
    }
    
}
