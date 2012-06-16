package com.varankin.brains.jfx;

import com.varankin.biz.action.Действие;
import com.varankin.brains.appl.УдалитьАрхивныйМодуль;
import com.varankin.brains.db.Модуль;
import com.varankin.brains.Контекст;
import com.varankin.util.Текст;
import java.util.Collection;
import java.util.Collections;
import javafx.event.ActionEvent;

/**
 * Действие JavaFX для удаления модуля из архива.
 *
 * @author &copy; 2012 Николай Варанкин
 */
class ApplicationActionDbModuleRemove extends Action
{
    private final ApplicationView.Context context;
    private final УдалитьАрхивныйМодуль действие;

    ApplicationActionDbModuleRemove( ApplicationView.Context context )
    {
        super( context.jfx, Текст.ПАКЕТЫ.словарь( ApplicationActionDbModuleRemove.class, 
                context.jfx.контекст.специфика ) );
        this.действие = new УдалитьАрхивныйМодуль( context.jfx.контекст.склад );
        this.context = context;
    }

    @Override
    public void handle( ActionEvent _ )
    {
        Collection<Модуль> модули = Collections.emptyList(); // Arrays.asList( ....toArray() );
        //TODO = context.jfx.контекст.ceлектор( Модуль.class ).get();
        //TODO = CatalogView.Context.ceлектор.get();
        new ApplicationActionWorker<>( действие, модули, context.jfx, 
                this //TODO concept context.actions.getDbRemoveModule() 
                ).execute();
    }

}
