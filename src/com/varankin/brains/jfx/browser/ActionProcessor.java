package com.varankin.brains.jfx.browser;

import com.varankin.biz.action.Действие;
import com.varankin.brains.appl.ВыгрузитьПроект;
import com.varankin.brains.appl.ДействияПоПорядку;
import com.varankin.brains.appl.УправлениеПроцессом;
import com.varankin.brains.artificial.async.Процесс;
import com.varankin.brains.artificial.Проект;
import com.varankin.brains.artificial.Элемент;
import com.varankin.brains.jfx.ApplicationActionWorker;
import com.varankin.brains.jfx.JavaFX;
import com.varankin.brains.jfx.SelectionListBinding;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javafx.beans.binding.BooleanBinding;
import javafx.event.ActionEvent;

import static javafx.beans.binding.Bindings.createBooleanBinding;

/**
 *
 * @author Varankine
 */
class ActionProcessor //TODO split
{
    private static final Logger LOGGER = Logger.getLogger(
            ActionProcessor.class.getName(),
            ActionProcessor.class.getPackage().getName() + ".text" );

    private final SelectionListBinding<Элемент> selection;

    private final BooleanBinding
        disableStart, disablePause, 
        disableStop, disableRemove, 
        disableProperties; 

    ActionProcessor( SelectionListBinding<Элемент> selectionBinding )
    {
        selection = selectionBinding;
        
        disableStart = createBooleanBinding( () -> !enableActionStart(), selection );
        disablePause = createBooleanBinding( () -> !enableActionPause(), selection );
        disableStop = createBooleanBinding( () -> !enableActionStop(), selection );
        disableRemove = createBooleanBinding( () -> !enableActionRemove(), selection );
        disableProperties = createBooleanBinding( () -> !enableActionProperties(), selection );
    }
    
    private void onChangeProcesses( УправлениеПроцессом.Команда команда )
    {
        Действие<List<Процесс>> действие = new ДействияПоПорядку( ДействияПоПорядку.Приоритет.КОНТЕКСТ, 
                new УправлениеПроцессом( JavaFX.getInstance().контекст, команда ) );
        List<Процесс> ceлектор = selection.stream()
            .filter(  ( Элемент i ) -> i instanceof Процесс )
            .flatMap( ( Элемент i ) -> Stream.of( (Процесс)i ) )
            .collect( Collectors.toList() );
        new ApplicationActionWorker<>( действие, ceлектор ).execute( JavaFX.getInstance() );
    }

    void onActionStart( ActionEvent event ) 
    {
        onChangeProcesses( УправлениеПроцессом.Команда.СТАРТ );
    }

    void onActionPause( ActionEvent event ) 
    {
        onChangeProcesses( УправлениеПроцессом.Команда.ПАУЗА );
    }

    void onActionStop( ActionEvent event ) 
    {      
        onChangeProcesses( УправлениеПроцессом.Команда.СТОП );
    }

    void onActionRemove( ActionEvent event ) 
    {
        Действие<List<Проект>> действие = new ДействияПоПорядку( ДействияПоПорядку.Приоритет.КОНТЕКСТ, 
                new УправлениеПроцессом( JavaFX.getInstance().контекст, УправлениеПроцессом.Команда.СТОП ),
                new ВыгрузитьПроект( JavaFX.getInstance().контекст ) );
        List<Проект> ceлектор = selection.stream()
            .filter(  ( Элемент i ) -> i instanceof Проект )
            .flatMap( ( Элемент i ) -> Stream.of( (Проект)i ) )
            .collect( Collectors.toList() );
        new ApplicationActionWorker<>( действие, ceлектор ).execute( JavaFX.getInstance() );
    }

    void onActionProperties( ActionEvent event ) 
    {
        Действие<List<Элемент>> действие = null; //TODO NOT IMPL.
        List<Элемент> ceлектор = selection;/*.stream()
            .filter(  ( Элемент i ) -> i instanceof Проект )
            .flatMap( ( Элемент i ) -> Stream.of( (Проект)i ) )
            .collect( Collectors.toList() );*/
        LOGGER.info( "Sorry, the command is not implemented." );//TODO not impl.
    }
    
    BooleanBinding disableStartProperty() { return disableStart; }
    BooleanBinding disablePauseProperty() { return disablePause; }
    BooleanBinding disableStopProperty() { return disableStop; }
    BooleanBinding disableRemoveProperty() { return disableRemove; }
    BooleanBinding disablePropertiesProperty() { return disableProperties; }

    private boolean enableActionStart() 
    {
        return !selection.isEmpty() && selection.stream()
                .allMatch( ( Элемент i ) -> i instanceof Процесс );
    }

    private boolean enableActionPause() 
    {
        return !selection.isEmpty() && selection.stream()
                .allMatch( ( Элемент i ) -> i instanceof Процесс );
    }

    private boolean enableActionStop() 
    {
        return !selection.isEmpty() && selection.stream()
                .allMatch( ( Элемент i ) -> i instanceof Процесс );
    }

    private boolean enableActionRemove() 
    {
        return !selection.isEmpty() && selection.stream()
                .allMatch( ( Элемент i ) -> i instanceof Проект );
    }

    private boolean enableActionProperties() 
    {
        return selection.size() == 1;
    }

}
