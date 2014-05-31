package com.varankin.brains.jfx.analyser;

import com.varankin.brains.artificial.rating.КаталогРанжировщиков;
import static com.varankin.brains.artificial.rating.КаталогРанжировщиков.*;
import com.varankin.brains.artificial.Ранжировщик;
import com.varankin.brains.factory.Proxy;
import com.varankin.brains.observable.*;
import com.varankin.characteristic.НаблюдаемоеСвойство;
import com.varankin.util.LoggerX;
import java.util.ArrayList;
import java.util.Collection;
import java.util.ResourceBundle;
import java.util.logging.Level;

/**
 * Наблюдаемое свойство.
 * 
 * @author &copy; 2014 Николай Варанкин
 */
class Observable<T>
{
    private static final LoggerX LOGGER = LoggerX.getLogger( ObservableConversionPaneController.class );
    private static final ResourceBundle RESOURCE_BUNDLE = LOGGER.getLogger().getResourceBundle();
    private static final Ранжировщик РАНЖИРОВЩИК = new Value.РанжировщикImpl();
    
    final НаблюдаемоеСвойство<T> СВОЙСТВО;
    final String НАЗВАНИЕ;
    final Collection<Ранжировщик> РАНЖИРОВЩИКИ;

    Observable( НаблюдаемоеСвойство<T> свойство, String index )
    {
        СВОЙСТВО = свойство;
        НАЗВАНИЕ = RESOURCE_BUNDLE.getString( index );
        Collection<Ранжировщик> варианты = КаталогРанжировщиков.getInstance().get( index );
        РАНЖИРОВЩИКИ = new ArrayList<>(); //TODO index
        if( варианты != null ) РАНЖИРОВЩИКИ.addAll( варианты );
        РАНЖИРОВЩИКИ.add( РАНЖИРОВЩИК ); // универсальный
    }
    
    /**
     * Создает список доступных параметров объекта.
     * 
     * @param object объект.
     * @return список доступных параметров. 
     */
    static Collection<Observable> observables( Object object )
    {
        Collection<Observable> items = new ArrayList<>();
        
        if( object instanceof НаблюдаемыйПроцесс )
        {
            НаблюдаемыйПроцесс наблюдаемый = (НаблюдаемыйПроцесс)object;
            items.add( new Observable( наблюдаемый.свойствоСостояние(), ПроцессСостояние ) );
        }
        if( object instanceof НаблюдаемыйПроцессор )
        {
            НаблюдаемыйПроцессор наблюдаемый = (НаблюдаемыйПроцессор)object;
            items.add( new Observable( наблюдаемый.свойствоСостояние(), ПроцессСостояние ) );
            items.add( new Observable( наблюдаемый.свойствоПауза(), ПроцессорПауза ) );
        }
        if( object instanceof НаблюдаемыйПроект )
        {
            НаблюдаемыйПроект наблюдаемый = (НаблюдаемыйПроект)object;
            items.add( new Observable( наблюдаемый.свойствоСостояние(), ПроцессСостояние ) );
        }
        else if( object instanceof НаблюдаемоеЗначение )
        {
            НаблюдаемоеЗначение наблюдаемый = (НаблюдаемоеЗначение)object;
            items.add( new Observable( наблюдаемый.свойствоВыход(), ЗначениеВыход ) );
            items.add( new Observable( наблюдаемый.свойствоЗначение(), ЗначимыйЗначение ) );
        }
        else if( object instanceof НаблюдаемаяВетвь )
        {
            НаблюдаемаяВетвь наблюдаемый = (НаблюдаемаяВетвь)object;
            items.add( new Observable( наблюдаемый.свойствоЗначение(), ЗначимыйЗначение ) );
        }
        else if( object instanceof НаблюдаемыйАргумент )
        {
            НаблюдаемыйАргумент наблюдаемый = (НаблюдаемыйАргумент)object;
            items.add( new Observable( наблюдаемый.свойствоВход(), АргументВход ) );
            items.add( new Observable( наблюдаемый.свойствоРанг(), АргументРанг ) );
            items.add( new Observable( наблюдаемый.свойствоЗначение(), ЗначимыйЗначение ) );
        }
        else if( object instanceof НаблюдаемыйЗначимый )
        {
            НаблюдаемыйЗначимый наблюдаемый = (НаблюдаемыйЗначимый)object;
            items.add( new Observable( наблюдаемый.свойствоЗначение(), ЗначимыйЗначение ) );
        }
        else if( object instanceof НаблюдаемыйРазветвитель )
        {
            НаблюдаемыйРазветвитель наблюдаемый = (НаблюдаемыйРазветвитель)object;
            items.add( new Observable( наблюдаемый.свойствоВход(), ПриемникВход ) );
            items.add( new Observable( наблюдаемый.свойствоВыход(), ИсточникВыход ) );
            items.add( new Observable( наблюдаемый.свойствоПринято(), ПриемникПринято ) );
            items.add( new Observable( наблюдаемый.свойствоПередано(), ИсточникПередано ) );
        }
        else if( object instanceof НаблюдаемыйПриемник )
        {
            НаблюдаемыйПриемник наблюдаемый = (НаблюдаемыйПриемник)object;
            items.add( new Observable( наблюдаемый.свойствоВход(), ПриемникВход ) );
            items.add( new Observable( наблюдаемый.свойствоПринято(), ПриемникПринято ) );
        }
        else if( object instanceof НаблюдаемыйИсточник )
        {
            НаблюдаемыйИсточник наблюдаемый = (НаблюдаемыйИсточник)object;
            items.add( new Observable( наблюдаемый.свойствоВыход(), ИсточникВыход ) );
            items.add( new Observable( наблюдаемый.свойствоПередано(), ИсточникПередано ) );
        }
        else if( object instanceof НаблюдаемоеПоле )
        {
            //НаблюдаемоеПоле наблюдаемый = (НаблюдаемоеПоле)object;
        }
        else if( object instanceof НаблюдаемыйСенсор )
        {
            //НаблюдаемыйСенсор наблюдаемый = (НаблюдаемыйСенсор)object;
        }
        else if( object instanceof НаблюдаемыйЭлемент )
        {
            //НаблюдаемыйЭлемент наблюдаемый = (НаблюдаемыйЭлемент)object;
        }
        else if( object instanceof Proxy )
        {
            items.addAll( observables( ((Proxy)object).оригинал() ) );
        }
        else if( object instanceof Наблюдаемый )
        {
            LOGGER.log( Level.FINE, "observable.unknown", object );
        }
        return items;
    }
    
}
