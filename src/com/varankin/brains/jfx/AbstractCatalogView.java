package com.varankin.brains.jfx;

import com.varankin.biz.action.Действие;
import com.varankin.brains.appl.ЭкспортироватьSvg;
import com.varankin.brains.db.Сборка;
import com.varankin.brains.db.Элемент;
import com.varankin.brains.jfx.ApplicationView.Context;
import com.varankin.io.container.Provider;
import com.varankin.util.Текст;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.control.ListView;
import javafx.scene.web.WebView;

/**
 *
 * @author Николай
 */
abstract class AbstractCatalogView<T extends Элемент> extends ListView<T>
{
    private static final Logger LOGGER = Logger.getLogger( AbstractCatalogView.class.getName() );
    
    private final ReadOnlyStringProperty title;

    protected final ApplicationView.Context context;
    protected final Текст словарь;

    protected AbstractCatalogView( Context context )
    {
        this.context = context;
        словарь = Текст.ПАКЕТЫ.словарь( getClass(), context.jfx.контекст.специфика );
        title = new ReadOnlyStringWrapper( словарь.текст( "Name" ) );
    }
    
    final ReadOnlyStringProperty titleProperty()
    {
        return title;
    }

    //<editor-fold defaultstate="collapsed" desc="classes">
    
    protected class ActionPreview extends AbstractContextJfxAction<ApplicationView.Context>
    {
        private final SvgService<T> service;
        
        ActionPreview( SvgService<T> service, ObservableValue<Boolean> blocker )
        {
            super( context, context.jfx.словарь( ActionPreview.class ) );
            this.service = service;
            disableProperty().bind( blocker );
        }
        
        @Override
        public void handle( ActionEvent _ )
        {
            ObservableList<TitledSceneGraph> views = контекст.jfx.getViews().getValue();
            
            for( T элемент : selectionModelProperty().getValue().getSelectedItems() )
                if( элемент != null )
                {
                    TitledSceneGraph tsg = isShown( элемент, views );
                    if( tsg == null )
                        views.add( show( элемент ) );
                    else
                        //TODO временный обходной вариант для активации view
                        views.set( views.indexOf( tsg ), new TitledSceneGraph( tsg.node, tsg.title ) );
                }
        }
        
        private TitledSceneGraph isShown( T элемент, Iterable<TitledSceneGraph> views )
        {
            for( TitledSceneGraph tsg : views )
            {
                Object content = tsg.node.getUserData(); // see show(элемент)
                if( tsg.node instanceof WebView && элемент.equals( content ) )
                    return tsg;
            }
            return null;
        }
        
        private TitledSceneGraph show( T элемент )
        {
            WebView view = new WebView();
            view.setUserData( элемент );
            String название = элемент.название();
            контекст.jfx.getExecutorService().submit( new WebViewLoaderTask(
                    service.getPainter( элемент ), view.getEngine(), название, словарь ) );
            return new TitledSceneGraph( view, new SimpleStringProperty( название ) );
        }

    }
    
    protected class ActionEdit extends AbstractContextJfxAction<ApplicationView.Context>
    {
        
        ActionEdit( ObservableValue<Boolean> blocker )
        {
            super( context, context.jfx.словарь( ActionEdit.class ) );
            disableProperty().bind( blocker );
        }

        @Override
        public void handle( ActionEvent _ )
        {
            LOGGER.info( "Sorry, the command is not implemented." );//TODO not impl.
        }
    }
    
    protected class ActionRemove extends AbstractContextJfxAction<ApplicationView.Context>
    {
        private final Действие<Collection<T>> действие;
        
        ActionRemove( Действие<Collection<T>> действие, ObservableValue<Boolean> blocker )
        {
            super( context, context.jfx.словарь( ActionRemove.class ) );
            this.действие = действие;
            disableProperty().bind( blocker );
        }
        
        @Override
        public void handle( ActionEvent _ )
        {
            // собрать выделенные элементы немедленно, ибо список может затем измениться другими процессами
            List<T> ceлектор = new ArrayList<>( getSelectionModel().getSelectedItems() );
            //TODO confirmation dialog
            new ApplicationActionWorker<>( действие, ceлектор ).execute( context.jfx );
        }
    }
    
    protected class ActionExport extends AbstractContextJfxAction<ApplicationView.Context>
    {
        final ЭкспортироватьSvg действие;
        
        ActionExport( ЭкспортироватьSvg действие, ObservableValue<Boolean> blocker )
        {
            super( context, context.jfx.словарь( ActionExport.class ) );
            this.действие = действие;
            disableProperty().bind( blocker );
        }
        
        @Override
        public void handle( ActionEvent _ )
        {
            // собрать выделенные элементы немедленно, ибо список может затем измениться другими процессами
            List<T> сeлектор = new ArrayList<>( getSelectionModel().getSelectedItems() );
            File file = context.exporter.newInstance();
            if( file != null )
                if( сeлектор.size() == 1 )
                {
                    T элемент = сeлектор.get( 0 );
                    ЭкспортироватьSvg.Контекст к = new ЭкспортироватьSvg.Контекст(
                            контекст.jfx.контекст, элемент, new Сборка( элемент ), file );
                    new ApplicationActionWorker<>( действие, к ) // новый, т.к. одноразовый
                            .execute( context.jfx );
                }
                else
                    LOGGER.log( Level.SEVERE, "Cannot save multiple {0} elements in single file.", сeлектор.size() );
        }
    }
    
    interface SvgService<T extends Элемент >
    {
        Provider<String> getPainter( T element );
    }
    
    //</editor-fold>
}
