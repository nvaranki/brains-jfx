package com.varankin.brains.jfx;

import com.varankin.biz.action.Действие;
import com.varankin.biz.action.Результат;
import com.varankin.brains.appl.УдалитьАрхивныйПроект;
import com.varankin.brains.artificial.io.svg.SvgПроект;
import com.varankin.brains.db.Коллекция;
import com.varankin.brains.db.Отображаемый;
import com.varankin.brains.db.Проект;
import com.varankin.brains.jfx.MenuFactory.MenuNode;
import com.varankin.util.Текст;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.logging.*;
import javafx.application.Platform;
import javafx.beans.property.ObjectPropertyBase;
import javafx.beans.property.Property;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.beans.value.ObservableValueBase;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.scene.control.*;
import javafx.scene.web.WebView;
import javafx.util.Callback;

/**
 * Каталог проектов архива.
 *
 * @author &copy; 2012 Николай Варанкин
 */
class ProjectCatalogView extends ListView<Проект>
{
    private final static Logger LOGGER = Logger.getLogger( ProjectCatalogView.class.getName() );
    
    private final ApplicationView.Context context;
    private final ReadOnlyStringProperty title;

    ProjectCatalogView( final ApplicationView.Context context )
    {
        this.context = context;
        getSelectionModel().setSelectionMode( SelectionMode.MULTIPLE );
        setContextMenu( context.menuFactory.createContextMenu( popup() ) );
        setCellFactory( new RowBuilder() );
        itemsProperty().bind( context.jfx.проекты() );
        Текст словарь = Текст.ПАКЕТЫ.словарь( ProjectCatalogView.class, context.jfx.контекст.специфика );
        title = new ReadOnlyStringWrapper( словарь.текст( "Name" ) );
    }
    
    final ReadOnlyStringProperty titleProperty()
    {
        return title;
    }
    
    private MenuNode popup()
    {
        return new MenuNode( null,
                new MenuNode( new ActionPreview() ),
                new MenuNode( new ActionEdit() ),
                new MenuNode( new ActionRun() ),
                null, 
                new MenuNode( new ActionRemove() ),
                null, 
                new MenuNode( new ActionExport() ),
                null, 
                new MenuNode( new ActionProperties() )
                );
    }
    
    //<editor-fold defaultstate="collapsed" desc="классы">

    private class RowBuilder implements Callback<ListView<Проект>, ListCell<Проект>>
    {
        @Override
        public ListCell<Проект> call( ListView<Проект> view )
        {
            return new VisibleRow();
        }
    }

    static private class VisibleRow extends ListCell<Проект>
    {
        @Override
        public void updateItem( Проект item, boolean empty ) 
        {
            super.updateItem( item, empty );
            setText( empty ? null : item.toString() );
        }
    }
    
    private class ActionPreview extends AbstractJfxAction<ApplicationView.Context>
    {
        
        ActionPreview()
        {
            super( context, context.jfx.словарь( ActionPreview.class ) );
            disableProperty().bind( new SelectionDetector( selectionModelProperty(), false, 1, 1 ) );
        }
        
        @Override
        public void handle( ActionEvent _ )
        {
            ObservableList<TitledSceneGraph> views = контекст.jfx.getViews().getValue();
            
            for( Проект проект : selectionModelProperty().getValue().getSelectedItems() )
                if( проект != null )
                {
                    TitledSceneGraph tsg;
                    if( ( tsg = isShown( проект, views ) ) == null )
                        show( проект, views );
                    else
                        //TODO временный обходной вариант для активации view
                        views.set( views.indexOf( tsg ), new TitledSceneGraph( tsg.node, tsg.title ) );
                }
        }

        private TitledSceneGraph isShown( Проект проект, Iterable<TitledSceneGraph> views )
        {
            for( TitledSceneGraph tsg : views )
            {
                Object content = tsg.node.getUserData();
                if( tsg.node instanceof WebView && проект.equals( content ) )
                    return tsg;
            }
            return null;
        }

        private void show( final Проект проект, Collection<TitledSceneGraph> views )
        {
            final WebView view = new WebView();
            view.setUserData( проект );
            views.add( new TitledSceneGraph( view, new SimpleStringProperty( проект.toString() ) ) );
            
            контекст.jfx.getExecutorService().submit( new Task<String>() 
            {
                @Override
                protected String call() throws Exception
                {
                    //return проект.getImage( Отображаемый.MIME_SVG );
                    return new SvgПроект( проект ).generateImage( "  " );
                }

                @Override
                protected void succeeded()
                {
                    view.getEngine().loadContent( getValue(), Отображаемый.MIME_SVG );
                }
                
                @Override
                protected void failed()
                {
                    Throwable exception = this.getException();
                    String problem = "<html><body>Failed to obtain project image.</body></html>";
                    if( exception != null ) problem += "\n" + exception.getStackTrace();
                    view.getEngine().loadContent( problem, "text/html" );
                    LOGGER.log( Level.SEVERE, "Failed to obtain project image.", exception );
                }
            } );
        }

    }
    
    private class ActionEdit extends AbstractJfxAction<ApplicationView.Context>
    {
        
        ActionEdit()
        {
            super( context, context.jfx.словарь( ActionEdit.class ) );
            disableProperty().bind( new SelectionDetector( selectionModelProperty(), false, 1, 1 ) );
        }
        
        @Override
        public void handle( ActionEvent _ )
        {
            LOGGER.info( "Sorry, the command is not implemented." );//TODO not impl.
        }
    }
    
    private class ActionRun extends AbstractJfxAction<ApplicationView.Context>
    {
        
        ActionRun()
        {
            super( context, context.jfx.словарь( ActionRun.class ) );
            disableProperty().bind( new SelectionDetector( selectionModelProperty(), false, 1, 1 ) );
        }
        
        @Override
        public void handle( ActionEvent _ )
        {
            LOGGER.info( "Sorry, the command is not implemented." );//TODO not impl.
        }
    }
    
    private class ActionExport extends AbstractJfxAction<ApplicationView.Context>
    {
        
        ActionExport()
        {
            super( context, context.jfx.словарь( ActionExport.class ) );
            disableProperty().bind( new SelectionDetector( selectionModelProperty(), false, 1, 1 ) );
        }
        
        @Override
        public void handle( ActionEvent _ )
        {
            LOGGER.info( "Sorry, the command is not implemented." );//TODO not impl.
        }
    }
    
    private class ActionRemove extends AbstractJfxAction<ApplicationView.Context>
    {
        final Действие<Collection<Проект>> действие;
        
        ActionRemove()
        {
            super( context, context.jfx.словарь( ActionRemove.class ) );
            действие = new УдалитьАрхивныйПроект( context.jfx.контекст.архив );
            disableProperty().bind( new SelectionDetector( selectionModelProperty(), false, 1 ) );
        }
        
        @Override
        public void handle( ActionEvent _ )
        {
            //TODO confirmation dialog
            
            // собрать выделенные проекты немедленно, ибо список может затем измениться другими процессами
            List<Проект> ceлектор = new ArrayList<>( getSelectionModel().getSelectedItems() );
            
            new ApplicationActionWorker<Collection<Проект>>( действие, ceлектор )
            {
                @Override
                protected void succeeded()
                {
                    super.succeeded();
                    Результат результат = getValue();
                    if( результат != null && результат.код() == Результат.НОРМА )
                        контекст.jfx.getDataBaseProjectMonitor().getValue().removeAll( контекст() );
                }
            }.execute( context.jfx );
        }
    }
    
    private class ActionProperties extends AbstractJfxAction<ApplicationView.Context>
    {
        
        ActionProperties()
        {
            super( context, context.jfx.словарь( ActionProperties.class ) );
            disableProperty().bind( new SelectionDetector( selectionModelProperty(), false, 1, 1 ) );
        }
        
        @Override
        public void handle( ActionEvent _ )
        {
            LOGGER.info( "Sorry, the command is not implemented." );//TODO not impl.
        }
    }
    
    //</editor-fold>
    
}
