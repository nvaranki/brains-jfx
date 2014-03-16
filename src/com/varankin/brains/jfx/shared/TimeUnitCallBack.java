package com.varankin.brains.jfx.shared;

import java.util.concurrent.TimeUnit;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.util.Callback;

/**
 * Локализованная фабрика ячеек класса {@link TimeUnit}.
 * 
 * @author &copy; 2014 Николай Варанкин
 */
public class TimeUnitCallBack implements Callback<ListView<TimeUnit>, ListCell<TimeUnit>>
{
   
    @Override
    public ListCell<TimeUnit> call( ListView<TimeUnit> view )
    {
        return new TimeUnitCell();
    }

}
