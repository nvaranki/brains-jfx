package com.varankin.brains.jfx.editor;

import com.varankin.brains.db.*;
import com.varankin.brains.jfx.JavaFX;
import com.varankin.brains.jfx.db.FxГрафика;
import com.varankin.brains.jfx.db.FxИнструкция;
import com.varankin.brains.jfx.db.FxТекстовыйБлок;
import com.varankin.util.LoggerX;

import java.util.Collection;
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
    
    private final FxГрафика ЭЛЕМЕНТ;
    private final FxИнструкция инструкция;
    private final FxТекстовыйБлок блок;
    
    @FXML private Text text;
    
    SvgTextController( FxГрафика элемент, FxИнструкция инструкция ) 
    {
        this( элемент, инструкция, null );
    }
    
    SvgTextController( FxГрафика элемент, FxТекстовыйБлок блок ) 
    {
        this( элемент, null, блок );
    }
    
    private SvgTextController( FxГрафика элемент, FxИнструкция инструкция, FxТекстовыйБлок блок ) 
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
        if( newValue ) 
        {
            JavaFX.getInstance().execute( new TextUpdateTask() );
        }
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
            //try( Транзакция транзакция = ЭЛЕМЕНТ.транзакция() )
            {
                //транзакция.согласовать( Транзакция.Режим.ЧТЕНИЕ_БЕЗ_ЗАПИСИ, ЭЛЕМЕНТ );
                x = ЭЛЕМЕНТ.toSvgDouble( SVG_ATTR_X, 0d );
                y = ЭЛЕМЕНТ.toSvgDouble( SVG_ATTR_Y, 0d );
                fill = ЭЛЕМЕНТ.toSvgColor( SVG_ATTR_FILL, null );
                stroke = ЭЛЕМЕНТ.toSvgColor( SVG_ATTR_STROKE, null );
                font = Font.font( ЭЛЕМЕНТ.toSvgDouble( SVG_ATTR_FONT_SIZE, 10d ) );
                content = инструкция != null ? content( инструкция ) : блок != null ? блок.текст().getValue() : null;
            }
            return null;
        }
        
        String content( FxИнструкция инструкция )
        {
            if( инструкция == null ) return null;
            String text = инструкция.выполнить().getValue();
            if( text == null )
                text = "!!! " + инструкция.процессор().getValue() + '(' + инструкция.код().getValue() + ')' + "=null";
            else if( text.isEmpty() )
                text = "??? " + инструкция.процессор().getValue() + '(' + инструкция.код().getValue() + ')' + "=\"\"";
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
