package com.varankin.brains.jfx.editor;

import com.varankin.brains.db.*;
import com.varankin.brains.jfx.JavaFX;
import com.varankin.util.LoggerX;

import java.util.Collection;
import java.util.Collections;
import java.util.logging.Level;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.*;
import javafx.scene.input.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.util.Builder;

import static com.varankin.brains.io.xml.XmlSvg.*;
import static com.varankin.brains.jfx.editor.InPlaceEditorBuilder.*;

/**
 * FXML-контроллер текста SVG. 
 * 
 * @author &copy; 2016 Николай Варанкин
 */
final class SvgTextController implements Builder<Text>
{
    private static final LoggerX LOGGER = LoggerX.getLogger( SvgTextController.class );
    //private static final String RESOURCE_CSS  = "/fxml/editor/SvgText.css";
    private static final String CSS_CLASS = "svg-text";
    
    private final DbАтрибутный ЭЛЕМЕНТ;
    private final DbИнструкция инструкция;
    private final DbТекстовыйБлок блок;
    
    @FXML private Text text;
    
    SvgTextController( DbАтрибутный элемент, DbИнструкция инструкция ) 
    {
        this( элемент, инструкция, null );
    }
    
    SvgTextController( DbАтрибутный элемент, DbТекстовыйБлок блок ) 
    {
        this( элемент, null, блок );
    }
    
    private SvgTextController( DbАтрибутный элемент, DbИнструкция инструкция, DbТекстовыйБлок блок ) 
    {
        ЭЛЕМЕНТ = элемент;
        this.инструкция = инструкция;
        this.блок = блок;
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
        text.setOnMouseClicked( this::onMouseClicked );
        text.visibleProperty().addListener( this::onVisibleChanged );
        text.setVisible( false );
        text.setVisible( true );
        text.setText( "Loading..." );
    }
    
    private void onVisibleChanged( ObservableValue<? extends Boolean> o, Boolean oldValue, Boolean newValue )
    {
        if( newValue ) JavaFX.getInstance().execute( new TextUpdateTask() );
    }
    
    @FXML
    private void onMouseClicked( MouseEvent event )
    {
        if( MouseButton.PRIMARY == event.getButton() )
            switch( event.getClickCount() )
            {
                case 2: 
                    raiseInPlaceEditor( ); 
                    event.consume();
                    break;
//                case 1: 
//                    select(); 
//                    event.consume();
//                    break;
            }
    }
    
    private void raiseInPlaceEditor()
    {
        Collection<Node> children = childrenOf( text.getParent() );
        if( children == null )
        {
            LOGGER.log( Level.FINE, "No accessible children list of parent {0}", text.getParent() );
            return;
        }
        Node editor = инструкция != null ? new InPlaceText2Controller( инструкция ).build() : 
                блок != null ? new InPlaceTextController( блок ).build() : null;
        if( editor == null ) return; //TODO
        editor.setTranslateX( text.getTranslateX() );
        editor.setTranslateY( text.getTranslateY() );
        editor.visibleProperty().addListener( (x,o,n) -> 
            { if( !n ) { children.remove( editor ); text.setVisible( true ); } } );
        
        text.setVisible( false );
        children.add( editor );
        editor.requestFocus();
    }

    private void select()
    {
        //throw new UnsupportedOperationException( "Not supported yet." ); //To change body of generated methods, choose Tools | Templates.
    }
    
    private class TextUpdateTask extends Task<Void>
    {
        volatile double y, x;
        volatile Font font;
        volatile Color stroke, fill;
        volatile String content;

        @Override
        protected Void call() throws Exception
        {
            try( Транзакция транзакция = ЭЛЕМЕНТ.транзакция() )
            {
                транзакция.согласовать( Транзакция.Режим.ЧТЕНИЕ_БЕЗ_ЗАПИСИ, ЭЛЕМЕНТ );
                x = asDouble( ЭЛЕМЕНТ, SVG_ATTR_X, 0d );
                y = asDouble( ЭЛЕМЕНТ, SVG_ATTR_Y, 0d );
                fill = asColor( ЭЛЕМЕНТ, SVG_ATTR_FILL, null );
                stroke = asColor( ЭЛЕМЕНТ, SVG_ATTR_STROKE, null );
                font = Font.font( asDouble( ЭЛЕМЕНТ, SVG_ATTR_FONT_SIZE, 10d ) );
                content = инструкция != null ? content( инструкция ) : блок != null ? блок.текст() : null;
            }
            return null;
        }
        
        String content( DbИнструкция инструкция )
        {
            if( инструкция == null ) return null;
            String text = инструкция.выполнить();
            if( text == null )
                text = "!!! " + инструкция.процессор() + '(' + инструкция.код() + ')' + "=null";
            else if( text.isEmpty() )
                text = "??? " + инструкция.процессор() + '(' + инструкция.код() + ')' + "=\"\"";
            return text;
        }
        
        @Override
        protected void succeeded()
        {
            if( font != null ) text.setFont( font );
            text.setTranslateX( x );
            text.setTranslateY( y /*- text.getBaselineOffset()*/ );
            if( fill != null ) text.setFill( fill );
            if( stroke != null ) text.setStroke( stroke );
            text.setText( content );
        }
        
        @Override
        protected void failed()
        {
            Throwable exception = getException();
            String msg = exception == null ? null : exception.getMessage() != null ? exception.getMessage() : 
                    exception.getClass().getSimpleName();
            LOGGER.getLogger().log( Level.SEVERE, "Failure to update visible text{0}.", 
                    msg != null ? ": " + msg : "" );
        }
        
    }
    
}
