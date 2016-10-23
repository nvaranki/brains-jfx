package com.varankin.brains.jfx.history;

import com.varankin.util.LoggerX;
import java.io.*;
import java.net.*;
import java.util.Objects;

/**
 * Поставщик удаленных {@linkplain InputStream входных потоков}, 
 * пригодный для использования в 
 * {@linkplain com.varankin.history.HistoryList списке хранения истории}.
 *
 * @author &copy; 2016 Николай Варанкин
 */
public final class RemoteInputStreamProvider implements SerializableProvider<InputStream>
{
    private static final LoggerX LOGGER = LoggerX.getLogger( RemoteInputStreamProvider.class );
    private final URL url;

    public RemoteInputStreamProvider( URL url )
    {
        this.url = url;
    }

    @Override
    public InputStream newInstance()
    {
        try
        {
            //URLConnection connection = url.openConnection();
            //connection.setRequestProperty( "Accept", "text/xml" );
            return url.openStream(); //connection.getInputStream();
        } 
        catch( NullPointerException | IOException ex )
        {
            throw new RuntimeException( LOGGER.text( "is.remote.failed", toString() ), ex );
        }
    }

    @Override
    public boolean equals( Object o )
    {
        return o instanceof RemoteInputStreamProvider &&
            Objects.equals( url, ((RemoteInputStreamProvider)o).url );
    }

    @Override
    public int hashCode()
    {
        return RemoteInputStreamProvider.class.hashCode() ^ Objects.hashCode( url );
    }
    
    @Override
    public String toString()
    {
        return url != null ? url.toExternalForm() : "";
    }
    
}
