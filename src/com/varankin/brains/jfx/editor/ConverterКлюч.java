package com.varankin.brains.jfx.editor;

import com.varankin.brains.db.xml.ЗонныйКлюч;
import com.varankin.util.LoggerX;
import java.util.HashMap;
import java.util.Map;
import javafx.util.StringConverter;

import static com.varankin.brains.jfx.archive.ArchiveResourceFactory.*;

/**
 *
 * @author &copy; 2022 Николай Варанкин
 */
class ConverterКлюч extends StringConverter<ЗонныйКлюч>
{
    private static final LoggerX LOGGER = LoggerX.getLogger( ConverterКлюч.class );
    
    private final Map<String, ЗонныйКлюч> map = new HashMap<>();

    ConverterКлюч()
    {
    }

    @Override
    public String toString( ЗонныйКлюч ключ )
    {
        String название = /*XmlSvg.XMLNS_SVG.equalsIgnoreCase( ключ.uri() ) ? ключ.название() :*/ метка( ключ );
        map.put( название, ключ );
        return название;
    }

    @Override
    public ЗонныйКлюч fromString( String название )
    {
        return map.get( название );
    }
    
}
