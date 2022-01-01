package com.varankin.brains.jfx.editor;

import com.varankin.brains.db.xml.МаркированныйЗонныйКлюч;
import com.varankin.util.LoggerX;
import java.util.HashMap;
import java.util.Map;
import javafx.util.StringConverter;

import static com.varankin.brains.jfx.archive.ArchiveResourceFactory.*;

/**
 *
 * @author Varankine
 */
class ConverterКлюч extends StringConverter<МаркированныйЗонныйКлюч>
{
    private static final LoggerX LOGGER = LoggerX.getLogger( ConverterКлюч.class );
    
    private final Map<String, МаркированныйЗонныйКлюч> map = new HashMap<>();

    ConverterКлюч()
    {
    }

    @Override
    public String toString( МаркированныйЗонныйКлюч ключ )
    {
        String название = /*XmlSvg.XMLNS_SVG.equalsIgnoreCase( ключ.uri() ) ? ключ.название() :*/ метка( ключ );
        map.put( название, ключ );
        return название;
    }

    @Override
    public МаркированныйЗонныйКлюч fromString( String название )
    {
        return map.get( название );
    }
    
}
