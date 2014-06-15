package com.varankin.brains.jfx;

import java.util.List;
import javafx.collections.ListChangeListener;
import javafx.scene.control.SingleSelectionModel;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.image.ImageView;

/**
 * TabPane content manager.
 *
 * @author &copy; 2012 Николай Варанкин
 */
class TabPaneContentManager implements ListChangeListener<TitledSceneGraph>
{
    private final TabPane tabPane;

    TabPaneContentManager( TabPane tabPane )
    {
        this.tabPane = tabPane;
    }

    @Override
    public void onChanged( Change<? extends TitledSceneGraph> change )
    {
        List<Tab> tabs = tabPane.getTabs();
        SingleSelectionModel<Tab> selector = tabPane.getSelectionModel();
        
        while( change.next() )
            if( change.wasReplaced() ) // перед change.wasRemoved() и change.wasAdded() !!!
            {
                //TODO временный обходной вариант для активации view, вместо change.wasUpdated()
                for( int i = change.getFrom(), max = change.getTo(); i < max; i++ )
                    selector.select( i );
            }   
            else if( change.wasRemoved() )
                for( TitledSceneGraph tsg : change.getRemoved() )
                {
                    for( Tab tab : tabs )
                        if( tab.getContent().equals( tsg.node ) )
                            tabs.remove( tab );
                }
            else if( change.wasAdded() )
                for( TitledSceneGraph tsg : change.getAddedSubList() )
                {
                    Tab tab = new Tab();
                    tab.setContent( tsg.node );
                    if( tsg.icon != null ) tab.setGraphic( new ImageView( tsg.icon ) );
                    tab.textProperty().bind( tsg.title );
                    tabs.add( tab );
                    selector.select( tab );
                }
//            else
//            {
//                continue; //TODO
//            }
    }

}
