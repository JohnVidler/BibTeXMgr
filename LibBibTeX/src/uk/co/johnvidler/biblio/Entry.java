package uk.co.johnvidler.biblio;

import java.util.Collections;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * Created by IntelliJ IDEA.
 * User: john
 * Date: 25/05/11
 * Time: 20:53
 * To change this template use File | Settings | File Templates.
 */
public class Entry
{
    protected SortedMap<String, String> properties = Collections.synchronizedSortedMap(new TreeMap<String, String>());

    public void setProperty(String property, String data)
    {
        if( properties.containsKey(property.toLowerCase()) )
            properties.remove( property.toLowerCase() );
        properties.put( property.toLowerCase(), data );
    }

    public String getProperty( String property )
    {
        if( properties.containsKey(property.toLowerCase()) )
            return properties.get( property.toLowerCase() );
        return null;
    }

    public String getPropertyById( int id )
    {
        String[] keys = properties.keySet().toArray(new String[0]);
        return keys[id];
    }

    public boolean hasProperty( String key )
    {
        return properties.containsKey(key);
    }

    public int getPropertyCount()
    {
        return properties.size();
    }

    public void removeProperty( String property )
    {
        if( properties.containsKey(property.toLowerCase()) )
            properties.remove( property.toLowerCase() );
    }

    public Set<String> getProperties()
    {
        return properties.keySet();
    }


    public String toString()
    {
        StringBuffer buffer = new StringBuffer( "Basic entry:\n" );

        for( String key : properties.keySet() )
        {
            buffer.append( "\t" ).append(key).append( ": " ).append( properties.get(key) ).append('\n');
        }

        return buffer.toString();
    }

}
