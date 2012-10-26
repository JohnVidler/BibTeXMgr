package uk.co.johnvidler.biblio.bibtex;

import uk.co.johnvidler.biblio.Entry;
import uk.co.johnvidler.biblio.EntryReader;

import java.io.*;
import java.nio.BufferOverflowException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by IntelliJ IDEA.
 * User: john
 * Date: 25/05/11
 * Time: 20:57
 * To change this template use File | Settings | File Templates.
 */
public class BibTeXReader implements EntryReader
{
    protected static final Pattern openEntry = Pattern.compile( "^,?\\s*@([a-zA-Z_\\-]+)\\s*\\{\\s*([^,\\s]+),$" );
    protected static final Pattern closeEntry = Pattern.compile( "^,?\\s*(\\}[,]?\\s*)" );
    protected static final Pattern property = Pattern.compile( "^,?\\s*([a-zA-Z0-9:_\\-]+)\\s*=\\s*([\\\"\\{0-9])");
    protected static final Pattern comment = Pattern.compile( "^(%+)" );

    protected PushbackReader stream = null;
    protected String buffer = "";

    public BibTeXReader( InputStream inStream )
    {
        stream = new PushbackReader( new BufferedReader( new InputStreamReader( inStream ) ) );
    }

    public BibTeXReader( BufferedReader bufferedReader )
    {
        stream = new PushbackReader( bufferedReader );
    }

    public Entry read()
    {
        try
        {
            int input = 0;
            buffer = "";
            while( input != -1 )
            {
                input = stream.read();
                buffer += (char)input;

                if( comment.matcher(buffer.trim()).find() )
                {
                    Matcher m = comment.matcher(buffer);
                    m.find();

                    // Pop from the start of the buffer...
                    buffer = "";
                }

                if( openEntry.matcher( buffer ).find() )
                {
                    BibTeXEntry newEntry = readBibTeXEntry( stream );

                    if( newEntry != null )
                        return newEntry;
                }

                if( buffer.length() > 1024 )
                {
                    System.err.println( "Buffer=" + buffer );
                    throw new BufferOverflowException();
                }
            }

            return null;
        }
        catch ( Throwable t )
        {
            t.printStackTrace();
            return null;
        }
    }

    public BibTeXEntry readBibTeXEntry( PushbackReader inputStream ) throws Throwable
    {
        Matcher titleMatcher = openEntry.matcher(buffer);
        titleMatcher.find();

        String type = titleMatcher.group(1);
        String key = titleMatcher.group(2);

        BibTeXEntry entry = new BibTeXEntry( type, key );

        // Pop from the start of the buffer...
        buffer = buffer.substring( titleMatcher.group(0).length() );

        int input = inputStream.read();
        while( input != -1 )
        {
            buffer += (char)input;

            if( property.matcher(buffer).find() )
            {
                Matcher prop = property.matcher(buffer);
                prop.find();

                //Property matching causes a character at the start of the expression to be read as well - push this back!
                inputStream.unread( prop.group(2).charAt(0) );

                String value = readPropertyValue( inputStream, 0, ',' );

                if( value != null )
                {
                    if( value.endsWith(",") )
                        value = value.trim().substring( 0, value.length()-1 );

                    if( value.endsWith("\"") && value.startsWith("\"") )
                        value = value.trim().substring( 1, value.length()-1 );

                    entry.setProperty(prop.group(1), value);
                }
                else
                    System.err.println( "Could not read property '" +prop.group(1)+ "' in '" +key+ "'!" );

                // Pop from the start of the buffer...
                buffer = buffer.substring( prop.group(0).length() );
            }
            else if( closeEntry.matcher(buffer).find() )
            {
                Matcher eoe = closeEntry.matcher(buffer);
                eoe.find();

                // Pop from the start of the buffer...
                buffer = buffer.substring( eoe.group(0).length() );

                return entry;
            }

            input = inputStream.read();
        }

        System.err.println( "Warning! Did not see the end of an entry (" +key+ ")! Attempting to continue..." );
        purgeBuffer();
        return entry;
    }

    protected String readPropertyValue( PushbackReader inputReader, int depth, char expectant ) throws Throwable
    {
        String ret = "";
        int input = 0;

        while( input != -1 )
        {
            input = inputReader.read();
            ret += (char)input;

            /* An edge case for entries with no ending comma, as would be the case at the end of entries */
            if( depth == 0 )
            {
                if( ((char)input == '}' || (char)input == '\n' || (char)input == ',' ) )
                {
                    inputReader.unread( input );
                    return ret.trim();
                }
            }

            if( (char)input == expectant )
                return ret.trim();
            else
            {
                if( (char)input == '\\' )
                    ret += (char)inputReader.read();
                else
                {
                    if( (char)input == '\"' )
                        ret += readPropertyValue( inputReader, depth+1, '\"' );

                    else if( (char)input == '{' )
                        ret += readPropertyValue( inputReader, depth+1, '}' );
                }
            }
        }

        return null;
    }

    protected void purgeBuffer()
    {
        buffer = "";
    }
}
