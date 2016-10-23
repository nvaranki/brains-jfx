package com.varankin.brains.jfx.history;

import com.varankin.util.LoggerX;
import java.io.*;
import java.util.Objects;

/**
 * Поставщик локальных {@linkplain InputStream входных потоков}, пригодный для 
 * использования в {@linkplain com.varankin.history.HistoryList списке хранения истории}.
 *
 * @author &copy; 2016 Николай Варанкин
 */
public final class LocalInputStreamProvider implements SerializableProvider<InputStream>
{
    private static final LoggerX LOGGER = LoggerX.getLogger( LocalInputStreamProvider.class );
    private final File file;

    public LocalInputStreamProvider( File file )
    {
        this.file = file;
    }

    @Override
    public InputStream newInstance()
    {
        try
        {
            return new FileInputStream( file );
        }
        catch( NullPointerException | IOException ex )
        {
            throw new RuntimeException( LOGGER.text( "is.local.failed", toString() ), ex );
        }
    }

    @Override
    public boolean equals( Object o )
    {
        return o instanceof LocalInputStreamProvider &&
            Objects.equals( file, ((LocalInputStreamProvider)o).file );
    }

    @Override
    public int hashCode()
    {
        int hash = LocalInputStreamProvider.class.hashCode() ^ Objects.hashCode( file );
        return hash;
    }
    
    @Override
    public String toString()
    {
        return file != null ? file.toString() : "";
    }
    
}
