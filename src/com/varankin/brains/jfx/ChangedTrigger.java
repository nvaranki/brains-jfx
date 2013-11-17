package com.varankin.brains.jfx;

import java.util.concurrent.Callable;

/**
 * Триггер однократно возвращает предустановленное значение,
 * а затем только {@link Boolean#TRUE}, до следующей установки.
 * 
 * @author &copy; 2013 Николай Варанкин
 */
public final class ChangedTrigger implements Callable<Boolean>
{
    boolean value = false;

    @Override
    public Boolean call() throws Exception
    {
        boolean rv = value;
        value = true;
        return rv;
    }

    public void setValue( boolean v )
    {
        value = v;
    }
    
}
