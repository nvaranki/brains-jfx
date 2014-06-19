package com.varankin.brains.jfx.editor;

import com.varankin.brains.artificial.io.xml.Xml;
import static com.varankin.brains.artificial.io.xml.XmlSvg.*;
import com.varankin.brains.db.*;
import static com.varankin.brains.jfx.editor.InPlaceEditorBuilder.*;
import com.varankin.util.LoggerX;
import java.util.Collection;
import java.util.logging.Level;
import javafx.event.*;
import javafx.fxml.FXML;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.input.*;
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
    
    private final Неизвестный ЭЛЕМЕНТ;
    
    private TextField editor;
    private EventHandler<? super MouseEvent> handlerMouseClick;
    
    @FXML private Text text;
    
    public SvgTextController( Неизвестный элемент ) 
    {
        ЭЛЕМЕНТ = элемент;
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
        String update = "";
        for( Неизвестный н : ЭЛЕМЕНТ.прочее() )
            if( н.тип().название() == null )
                update = н.атрибут( Xml.XML_TEXT, "?" );
            else if( н instanceof Инструкция )
                update = ((Инструкция)н).выполнить();
        if( update.trim().isEmpty() ) update = "?";
        text.setText( update );
        text.setFill( asColor( ЭЛЕМЕНТ, SVG_ATTR_FILL, null ) );
        text.setStroke( asColor( ЭЛЕМЕНТ, SVG_ATTR_STROKE, null ) );
        text.setOnMouseClicked( handlerMouseClick );
    }
    
    void setEditable( boolean значение )
    {
        handlerMouseClick = значение ? this::handleMouseClick : null;
    }

    private void handleMouseClick( MouseEvent event )
    {
        Collection<Node> children = childrenOf( text.getParent() );
        if( children == null )
        {
            LOGGER.log( Level.FINE, "No accessible children list of parent {0}", text.getParent() );
            return;
        }
        
        if( editor == null ) 
        {
            SvgTextFieldController controller = new SvgTextFieldController( ЭЛЕМЕНТ, text );
            editor = controller.build();
        }
        event.isControlDown(); //TODO change attribute or reference to attribute
        text.setVisible( false );
        editor.setTranslateX( text.getX() );
        editor.setTranslateY( text.getY() - text.getBaselineOffset() );
        children.add( editor );
        editor.requestFocus();
        
        event.consume();
    }
    
}
