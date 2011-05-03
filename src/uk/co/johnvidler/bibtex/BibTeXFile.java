package uk.co.johnvidler.bibtex;

import java.io.*;
import java.nio.BufferOverflowException;
import java.util.ArrayList;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BibTeXFile
{
    /*private static final Pattern property = Pattern.compile( "^\\s*([a-zA-Z_\\-]+)\\s*=\\s*(?:\\\"|\\{)([^\\\"]+)(?:\\\"|\\}),?" );
    private static final Pattern entryHeader = Pattern.compile( "^@([a-zA-Z0-9_\\-]+)\\{([a-zA-Z0-9:\\-_\\.]+)," );
    private static final Pattern entryFooter = Pattern.compile( "\\},?" );*/

    private static final Pattern separator = Pattern.compile( "(,)" );
    private static final Pattern openEntry = Pattern.compile( "^\\s*@([a-zA-Z_\\-]+)\\s*\\{\\s*([a-zA-Z0-9_\\-:]+),$" );
    private static final Pattern closeEntry = Pattern.compile( "^(\\s*\\},)" );
    private static final Pattern property = Pattern.compile( "^\\s*([a-zA-Z0-9:_\\-]+)\\s*=\\s*" );

    private static final Pattern comment = Pattern.compile( "^(%+)" );


    private ArrayList<String> fields = new ArrayList<String>();
    private String buffer = "";

    public BibTeXFile()
    {
        //Stub, init
    }

    public ArrayList<String> getFields() { return fields; }

    public void write( File file, TreeSet<BibTeXEntry> entries ) throws Throwable
    {
        BufferedWriter outputStream = new BufferedWriter( new FileWriter(file) );

        for( BibTeXEntry e : entries )
            outputStream.write( e.toString() );

        outputStream.flush();
        outputStream.close();
    }


    public void writeXML( File file, TreeSet<BibTeXEntry> entries ) throws Throwable
    {
        BufferedWriter outputStream = new BufferedWriter( new FileWriter(file) );

        for( BibTeXEntry e : entries  )
            outputStream.write( e.toXML() );

        outputStream.flush();
        outputStream.close();
    }

    public TreeSet<BibTeXEntry> read(File file) throws Throwable
    {
        TreeSet<BibTeXEntry> entries = new TreeSet<BibTeXEntry>();

        try
        {
            BufferedReader inputStream = new BufferedReader( new FileReader( file ) );

            int input = 0;
            buffer = "";
            while( input != -1 )
            {
                input = inputStream.read();
                buffer += (char)input;

                if( comment.matcher(buffer.trim()).find() )
                {
                    Matcher m = comment.matcher(buffer);
                    m.find();

                    // Pop from the start of the buffer...
                    buffer = "";
                    System.out.println( "Skipped: " + inputStream.readLine() );
                }

                if( openEntry.matcher( buffer ).find() )
                {
                    BibTeXEntry newEntry = readBibTeXEntry( inputStream );
                    if( newEntry != null )
                        entries.add( newEntry );
                }

                if( buffer.length() > 1024 )
                {
                    System.err.println( "Input: " + buffer );
                    throw new BufferOverflowException();
                }
            }

            System.out.println( "OK!" );

            return entries;
        }
        catch ( Throwable t )
        {
            t.printStackTrace();
            return null;
        }
    }

    public BibTeXEntry readBibTeXEntry( BufferedReader inputStream ) throws Throwable
    {
        Matcher titleMatcher = openEntry.matcher(buffer);
        titleMatcher.find();

        BibTeXEntry entry = new BibTeXEntry( titleMatcher.group(1), titleMatcher.group(2) );

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

                if( !fields.contains(prop.group(1)) )
                    fields.add(prop.group(1));

                String value = readUntilClosingBrace( inputStream );

                entry.addProperty( prop.group(1), value );

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

        System.err.println("Warning! Did not see the end of an entry! Attempting to continue...");
        return entry;
    }


    private String readUntilClosingBrace( BufferedReader inputReader ) throws Throwable
    {
        int depth = 0;
        boolean inQuote = false;
        String output = "";
        int input = inputReader.read();

        while( input != -1 )
        {
            if( (char)input == '\\' )
                output += "" + (char)input + (char)inputReader.read();

            if( (char)input == '\"' )
                inQuote = !inQuote;

            if( (char)input == '{' )
                depth++;

            if( depth == 0 && !inQuote )
            {
                if( (char)input == ',' || (char)input == '}' )
                        return output;
                else if( (char)input != ' ' && (char)input != '\t' && (char)input != '\n' && (char)input != '\r' )
                    output += (char)input;
            }
            else
                output += (char)input;


            if( (char)input == '}' )
            {
                depth--;

                if( depth < 0 )
                    throw new Exception("Extra closing brace! Are you missing something somewhere? (" +output+ ")");
            }

            input = inputReader.read();
        }

        throw new Exception("Mis-matched braces! Are you missing something somewhere? (" +output+ ")");
    }

}
