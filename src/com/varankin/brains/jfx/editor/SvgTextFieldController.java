package com.varankin.brains.jfx.editor;

import com.varankin.brains.artificial.io.xml.Xml;
import static com.varankin.brains.artificial.io.xml.XmlSvg.SVG_ATTR_FILL;
import static com.varankin.brains.artificial.io.xml.XmlSvg.SVG_ATTR_STROKE;
import static com.varankin.brains.artificial.io.xml.XmlSvg.SVG_ATTR_X;
import static com.varankin.brains.artificial.io.xml.XmlSvg.SVG_ATTR_Y;
import com.varankin.brains.db.Инструкция;
import com.varankin.brains.db.Неизвестный;
import static com.varankin.brains.jfx.editor.InPlaceEditorBuilder.asColor;
import static com.varankin.brains.jfx.editor.InPlaceEditorBuilder.asDouble;
import com.varankin.util.LoggerX;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.util.Builder;

/**
 * FXML-контроллер редактируемого текста SVG. 
 * 
 * @author &copy; 2014 Николай Варанкин
 */
public class SvgTextFieldController implements Builder<TextField>
{
    private static final LoggerX LOGGER = LoggerX.getLogger( SvgTextFieldController.class );
    //private static final String RESOURCE_CSS  = "/fxml/editor/SvgTextField.css";
    private static final String CSS_CLASS = "svg-text-field";
    
    private final Неизвестный ЭЛЕМЕНТ;
    
    @FXML private TextField editor;
    
    public SvgTextFieldController( Неизвестный элемент ) 
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
    public TextField build()
    {
        editor = new TextField();
        
        editor.getStyleClass().add( CSS_CLASS );
        //text.getStylesheets().add( getClass().getResource( RESOURCE_CSS ).toExternalForm() );

        initialize();
        
        return editor;
    }
        
    @FXML
    protected void initialize()
    {
        for( Неизвестный н : ЭЛЕМЕНТ.прочее() )
            if( н.тип().название() == null )
                editor.setText( н.атрибут( Xml.XML_TEXT, "?" ) );
            else if( н instanceof Инструкция )
                editor.setText( н.атрибут( Xml.PI_ATTR_INSTRUCTION, "{?}" ) );
        if( editor.getText().isEmpty() )
            editor.setText( "?" );
    }
    
}
