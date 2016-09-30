package com.varankin.brains.jfx.editor;

import com.varankin.brains.io.xml.Xml;

import static com.varankin.brains.io.xml.XmlSvg.*;

import com.varankin.brains.db.*;

import static com.varankin.brains.jfx.editor.InPlaceEditorBuilder.*;

import com.varankin.util.LoggerX;
import java.util.Collection;
import java.util.Collections;
import java.util.logging.Level;
import javafx.beans.binding.Bindings;
import javafx.event.*;
import javafx.fxml.FXML;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.input.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.util.Builder;

/**
 * FXML-контроллер текста SVG. 
 * 
 * @author &copy; 2014 Николай Варанкин
 */
public final class SvgTextController implements Builder<Text>
{
    private static final LoggerX LOGGER = LoggerX.getLogger( SvgTextController.class );
    //private static final String RESOURCE_CSS  = "/fxml/editor/SvgText.css";
    private static final String CSS_CLASS = "svg-text";
    
    private final DbАтрибутный ЭЛЕМЕНТ;
    private final EventHandler<? super MouseEvent> handlerMouseClick, handlerMouseDrag;
    
    @FXML private Text text;
    
    public SvgTextController( DbАтрибутный элемент, boolean изменяемый ) 
    {
        ЭЛЕМЕНТ = элемент;
        handlerMouseClick = изменяемый ? this::handleMouseClick : null;
        handlerMouseDrag  = изменяемый ? this::handleMouseDrag  : null;
    }

    /**
     * Создает {@linkplain Node графический элемент} для отображения текста. 
     * Применяется в конфигурации без FXML.
     * 
     * @return графический элемент.
     */
    @Override
    public Text build()
    {
        text = new Text();
        
        text.getStyleClass().add( CSS_CLASS );
        //text.getStylesheets().add( getClass().getResource( RESOURCE_CSS ).toExternalForm() );

        initialize();
        
        return text;
    }
        
    @FXML
    protected void initialize()
    {
        text.setX( asDouble( ЭЛЕМЕНТ, SVG_ATTR_X, 0d ) );
        text.setY( asDouble( ЭЛЕМЕНТ, SVG_ATTR_Y, 0d ) );
        text.setFill( asColor( ЭЛЕМЕНТ, SVG_ATTR_FILL, null ) );
        text.setStroke( asColor( ЭЛЕМЕНТ, SVG_ATTR_STROKE, null ) );
        text.setOnMouseClicked( handlerMouseClick );
        text.setOnDragDetected( handlerMouseDrag );
        text.textProperty().bind( Bindings.createStringBinding( 
                () -> getContent(), text.visibleProperty() )); //TODO (1) on set vis=true only (2) extend approach on x,y,...
    }
    
    @FXML
    private void handleMouseClick( MouseEvent event )
    {
        if( MouseButton.PRIMARY == event.getButton() )
            switch( event.getClickCount() )
            {
                case 2: 
                    raiseInPlaceEditor( event.isControlDown() ); 
                    event.consume();
                    break;
                case 1: 
                    select(); 
                    event.consume();
                    break;
            }
    }
    
    @FXML
    private void handleMouseDrag( MouseEvent event )
    {
        if( MouseButton.PRIMARY == event.getButton() )
        {
            SnapshotParameters snapParams = new SnapshotParameters();
            snapParams.setFill( Color.TRANSPARENT );
            Dragboard dndb = text.startDragAndDrop( TransferMode.MOVE );
            dndb.setDragView( text.snapshot( snapParams, null ) );
            dndb.setContent( Collections.singletonMap( DataFormat.PLAIN_TEXT, text.getText() ) );
        }
    }

    private String getContent()
    {
        String update = "";
//        for( DbАтрибутный н : ЭЛЕМЕНТ.прочее() )
//            if( н.тип().название() == null )
//                update = н.атрибут( Xml.XML_TEXT, "?" );
//            else if( н instanceof DbИнструкция )
//                update = ((DbИнструкция)н).выполнить();
        if( update.trim().isEmpty() ) update = "?";
        return update;
    }

    private void raiseInPlaceEditor( boolean controlDown )
    {
        Collection<Node> children = childrenOf( text.getParent() );
        if( children == null )
        {
            LOGGER.log( Level.FINE, "No accessible children list of parent {0}", text.getParent() );
            return;
        }
        
        Builder<TextField> controller = new SvgTextFieldController( ЭЛЕМЕНТ, text, controlDown );
        TextField editor = controller.build();
        editor.setTranslateX( text.getX() );
        editor.setTranslateY( text.getY() - text.getBaselineOffset() );
        
        text.setVisible( false );
        children.add( editor );
        editor.requestFocus();
    }

    private void select()
    {
        //throw new UnsupportedOperationException( "Not supported yet." ); //To change body of generated methods, choose Tools | Templates.
    }
    
}
