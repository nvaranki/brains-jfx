package com.varankin.brains.jfx.browser;

import com.varankin.brains.artificial.async.Процесс;
import com.varankin.characteristic.Изменение;
import com.varankin.characteristic.Наблюдатель;
import java.util.function.Consumer;
import javafx.application.Platform;

/**
 * Инструмент заливки фона марки процесса в зависимости от состояния.
 * 
 * @author &copy; 2016 Николай Варанкин
 */
final class Маляр implements Наблюдатель<Процесс.Состояние>
{
    private final Consumer<? super Процесс.Состояние> ЗАЛИВКА;

    Маляр( Consumer<? super Процесс.Состояние> заливка )
    {
        ЗАЛИВКА = заливка;
    }

    @Override
    public void отклик( Изменение<Процесс.Состояние> изменение )
    {
        Platform.runLater( () -> ЗАЛИВКА.accept( изменение.АКТУАЛЬНОЕ ) );
    }
    
}
