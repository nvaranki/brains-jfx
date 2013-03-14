package com.varankin.brains.jfx;

import com.varankin.biz.action.Действие;
import com.varankin.brains.appl.УдалитьАрхивныеБиблиотеки;
import com.varankin.brains.artificial.io.svg.SvgБиблиотека;
import com.varankin.brains.db.Библиотека;
import com.varankin.brains.db.Отображаемый;
import com.varankin.brains.jfx.MenuFactory.MenuNode;
import com.varankin.util.Текст;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.logging.*;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.scene.control.*;
import javafx.scene.web.WebView;
import javafx.util.Callback;

/**
 * Каталог проектов архива.
 *
 * @author &copy; 2013 Николай Варанкин
 */
class LibraryCatalogView extends ListView<Библиотека>
{
    private final static Logger LOGGER = Logger.getLogger( LibraryCatalogView.class.getName() );
    
    private final ApplicationView.Context context;
    private final ReadOnlyStringProperty title;

    LibraryCatalogView( ApplicationView.Context context )
    {
        this.context = context;
        getSelectionModel().setSelectionMode( SelectionMode.MULTIPLE );
        setContextMenu( context.menuFactory.createContextMenu( popup() ) );
        setCellFactory( new RowBuilder() );
        itemsProperty().bind( new ObservableValueList<>( context.jfx.контекст.архив.библиотеки() ) );
        Текст словарь = Текст.ПАКЕТЫ.словарь( LibraryCatalogView.class, context.jfx.контекст.специфика );
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
                null, 
                new MenuNode( new ActionRemove() ),
                null, 
                new MenuNode( new ActionExport() ),
                null, 
                new MenuNode( new ActionProperties() )
                );
    }
    
    //<editor-fold defaultstate="collapsed" desc="классы">

    private class RowBuilder implements Callback<ListView<Библиотека>, ListCell<Библиотека>>
    {
        @Override
        public ListCell<Библиотека> call( ListView<Библиотека> view )
        {
            return new VisibleRow();
        }
    }

    static private class VisibleRow extends ListCell<Библиотека>
    {
        @Override
        public void updateItem( Библиотека item, boolean empty ) 
        {
            super.updateItem( item, empty );
            setText( empty ? null : item.название() );
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
            
            for( Библиотека проект : selectionModelProperty().getValue().getSelectedItems() )
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

        private TitledSceneGraph isShown( Библиотека проект, Iterable<TitledSceneGraph> views )
        {
            for( TitledSceneGraph tsg : views )
            {
                Object content = tsg.node.getUserData();
                if( tsg.node instanceof WebView && проект.equals( content ) )
                    return tsg;
            }
            return null;
        }

        private void show( final Библиотека библиотека, Collection<TitledSceneGraph> views )
        {
            final WebView view = new WebView();
            view.setUserData( библиотека );
            views.add( new TitledSceneGraph( view, new SimpleStringProperty( библиотека.название() ) ) );
            
            контекст.jfx.getExecutorService().submit( new Task<String>() 
            {
                @Override
                protected String call() throws Exception
                {
                    //return библиотека.getImage( Отображаемый.MIME_SVG );
                    return new SvgБиблиотека( библиотека ).generateImage( "  " );
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
                    String problem = "<html><body>Failed to obtain library image.</body></html>";
                    if( exception != null ) problem += "\n" + exception.getStackTrace();
                    view.getEngine().loadContent( problem, "text/html" );
                    LOGGER.log( Level.SEVERE, "Failed to obtain library image.", exception );
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
        final Действие<Collection<Библиотека>> действие;
        
        ActionRemove()
        {
            super( context, context.jfx.словарь( ActionRemove.class ) );
            действие = new УдалитьАрхивныеБиблиотеки( context.jfx.контекст.архив );
            disableProperty().bind( new SelectionDetector( selectionModelProperty(), false, 1 ) );
        }
        
        @Override
        public void handle( ActionEvent _ )
        {
            // собрать выделенные элементы немедленно, ибо список может затем измениться другими процессами
            List<Библиотека> ceлектор = new ArrayList<>( getSelectionModel().getSelectedItems() );
            //TODO confirmation dialog
            new ApplicationActionWorker<>( действие, ceлектор ).execute( context.jfx );
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
