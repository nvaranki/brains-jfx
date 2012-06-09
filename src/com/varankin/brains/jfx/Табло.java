package com.varankin.brains.jfx;

import javafx.application.Platform;
import javafx.scene.control.TextArea;

/**
 *
 *
 * @author &copy; 2011 Николай
 */
class Табло extends  TextArea 
{
    private final int limit;

    Табло( int limit )
    {
        this.limit = limit;
        setEditable( false );
    }

    @Override
    public void appendText( String text )
    {
        assert Platform.isFxApplicationThread();
        super.appendText( text );
        int excess = text != null ? getExcessCharCount( text.split( "\\n" ).length ) : 0;
        if( excess > 0 )
            deleteText( 0, excess );
    }
    
    protected int getExcessCharCount( int numLinesAppended )
    {
        int cnt = 0;
        String[] lines = getText().split( "\\n" ); // TODO needs performance improvement!
        for( int i = 0, max = Math.max( 0, lines.length - limit ); i < max; i++ )
            cnt += lines[i].length() + 1; // + '\n'
        return cnt;
//        char[] chars = getText().toCharArray();
//        int cnt = chars.length > 0 ? 1 : 0;
//        System.out.print( "getExcessCharCount: " );
//        System.out.print( chars.length );
//        System.out.print( " " );
//        for( int i = chars.length - 1; i >= 0; i-- )
//        {
//            if( chars[i] == '\n' )
//            {
//                cnt++;
//                if( cnt > limit )
//                {
//                    System.out.println( i + 1 );
//                    return i + 1;
//                }
//            }
//        }
//        System.out.println( 0 );
//        return 0;
    }
}
