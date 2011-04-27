package uk.co.johnvidler.bibtex;

import java.io.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.DataFormatException;

public class BibTeXFile
{
    private static final Pattern property = Pattern.compile( "^\\s*([a-zA-Z]+)\\s*=\\s*(?:\\\"|\\{)([^\\\"]+)(?:\\\"|\\}),?" );
    private static final Pattern entryHeader = Pattern.compile( "^@([a-zA-Z0-9_\\-]+)\\{([a-zA-Z0-9\\-_\\.]+)," );
    private static final Pattern entryFooter = Pattern.compile( "\\}" );

    public BibTeXFile()
    {
        //Stub, init
    }

    public void write( File file, Map<String, BibTeXEntry> entries ) throws Throwable
    {
        BufferedWriter outputStream = new BufferedWriter( new FileWriter(file) );

        for( String key : entries.keySet() )
        {
            outputStream.write( entries.get(key).toString() );
        }

        outputStream.flush();
        outputStream.close();
    }


    public void writeXML( File file, Map<String, BibTeXEntry> entries ) throws Throwable
    {
        BufferedWriter outputStream = new BufferedWriter( new FileWriter(file) );

        for( String key : entries.keySet() )
        {
            outputStream.write( entries.get(key).toXML() );
        }

        outputStream.flush();
        outputStream.close();
    }

    public TreeMap<String, BibTeXEntry> read( File file ) throws Throwable
    {
        TreeMap<String, BibTeXEntry> entries = new TreeMap<String, BibTeXEntry>();

        try
        {
            BufferedReader inputStream = new BufferedReader( new FileReader( file ) );

            String buffer = inputStream.readLine();
            while( buffer != null )
            {
                buffer = buffer.trim();

                // Skip newlines
                if( !buffer.equals("") )
                {
                    // Is it a new entry?
                    if( entryHeader.matcher(buffer).matches() )
                    {
                        Matcher m = entryHeader.matcher(buffer);
                        m.find();

                        BibTeXEntry newEntry = readEntry( m.group(1), m.group(2), inputStream );

                        if( newEntry != null )
                            entries.put( newEntry.getKey(), newEntry );
                    }
                    else
                    {
                        // Try again, attaching another line to this one in case the ID is on the next line
                        buffer += inputStream.readLine().trim();

                        if( entryHeader.matcher(buffer).matches() )
                        {
                            Matcher m = entryHeader.matcher(buffer);
                            m.find();

                            BibTeXEntry newEntry = readEntry( m.group(1), m.group(2), inputStream );

                            if( newEntry != null )
                                entries.put( newEntry.getKey(), newEntry );
                            else
                                System.out.println("Internal error!");
                        }
                        else
                        {
                            throw new DataFormatException( "Expected a new entry, but got something else! '" +buffer+ "'" );
                        }
                    }
                }

                buffer = inputStream.readLine();
            }

            return entries;
        }
        catch ( Throwable t )
        {
            t.printStackTrace();
            return null;
        }
    }

    private BibTeXEntry readEntry( String type, String key, BufferedReader inputStream ) throws Throwable
    {
        BibTeXEntry newEntry = new BibTeXEntry( type, key );

        String buffer = inputStream.readLine();
        while( buffer != null )
        {
            // Skip newlines
            if( !buffer.equals("") )
            {
                // Is is a property line?
                if( property.matcher(buffer).matches() )
                {
                    Matcher m = property.matcher(buffer);
                    m.find();

                    newEntry.addProperty(m.group(1), m.group(2));
                }
                // Is is an end-of-entry marker?
                else if( buffer.matches("\\}") )
                {
                    return newEntry;
                }
                else
                {
                    System.out.println("[WW]\tUnrecognised line in '" +key+ "' -> '" +buffer+ "'");
                }
            }

            buffer = inputStream.readLine();
        }

        System.out.println( "Unexpected EOF?!" );
        return null;
    }

}
