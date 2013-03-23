package com.varankin.brains.jfx;

import com.varankin.biz.action.Действие;
import com.varankin.biz.action.Результат;
import com.varankin.brains.appl.УдалитьАрхивныеПроекты;
import com.varankin.brains.artificial.io.svg.SvgПроект;
import com.varankin.brains.db.Отображаемый;
import com.varankin.brains.db.Проект;
import com.varankin.brains.jfx.MenuFactory.MenuNode;
import com.varankin.util.Текст;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
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
 * @author &copy; 2012 Николай Варанкин
 */
class ProjectCatalogView extends ListView<Проект>
{
    private final static Logger LOGGER = Logger.getLogger( ProjectCatalogView.class.getName() );
    
    private final ApplicationView.Context context;
    private final ReadOnlyStringProperty title;

    ProjectCatalogView( ApplicationView.Context context )
    {
        this.context = context;
        getSelectionModel().setSelectionMode( SelectionMode.MULTIPLE );
        setContextMenu( context.menuFactory.createContextMenu( popup() ) );
        setCellFactory( new RowBuilder() );
        itemsProperty().bind( new ObservableValueList<>( context.jfx.контекст.архив.проекты() ) );
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
            views.add( new TitledSceneGraph( view, new SimpleStringProperty( проект.название() ) ) );
            
            контекст.jfx.getExecutorService().submit( new Task<String>() 
            {
                final Проект элемент = проект;
                
                @Override
                protected String call() throws Exception
                {
                    return new SvgПроект( элемент ).generateImage( "  " ); //TODO Отображаемый.MIME_SVG
                }

                @Override
                protected void succeeded()
                {
                    view.getEngine().loadContent( getValue(), Отображаемый.MIME_SVG );
                }
                
                @Override
                protected void failed()
                {
                    String msg = словарь.текст( "failure", элемент.название() );
                    Throwable exception = this.getException();
                    view.getEngine().loadContent( HtmlGenerator.toHtml( msg, exception ), Отображаемый.MIME_TEXT );
                    LOGGER.log( Level.SEVERE, msg, exception );
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
            действие = new УдалитьАрхивныеПроекты( context.jfx.контекст.архив );
            disableProperty().bind( new SelectionDetector( selectionModelProperty(), false, 1 ) );
        }
        
        @Override
        public void handle( ActionEvent _ )
        {
            // собрать выделенные элементы немедленно, ибо список может затем измениться другими процессами
            List<Проект> ceлектор = new ArrayList<>( getSelectionModel().getSelectedItems() );
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
