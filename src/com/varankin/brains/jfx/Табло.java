package com.varankin.brains.jfx;

import javafx.application.Platform;
import javafx.scene.control.TextArea;

/**
 * Окно для вывода информационных сообщений.
 *
 * @author &copy; 2015 Николай Варанкин
 */
class Табло extends  TextArea //TODO TextArea recreates buffer on every edit - waste of memory!!! try javafx.scene.text.TextFlow
{
    private final int limit;

    private int lineCounter;
    
    Табло( int limit )
    {
        this.limit = limit;
        this.lineCounter = 1;
        setEditable( false );
        end();
    }

    @Override
    public void appendText( String text )
    {
        assert Platform.isFxApplicationThread();
        super.appendText( text );
        lineCounter += lc( text ) - 1;
        int excess = getExcessCharCount();
        if( excess > 0 )
            deleteText( 0, excess );
    }
    
    protected int getExcessCharCount()
    {
        String chars = getText();
        int cnt = 0, length = chars.length(), excess = lineCounter - limit;
        lineCounter -= Math.max( 0, excess );
        while( cnt < length && excess > 0 )
            if( chars.charAt( cnt++ ) == '\n' ) excess--;
        return cnt;
    }
    
    private static int lc( CharSequence chars )
    {
        int cnt = 1;
        for( int i = 0, m = chars.length(); i < m; i++ )
            if( chars.charAt( i ) == '\n' )
                cnt++;
        return cnt;
    }
            
}
