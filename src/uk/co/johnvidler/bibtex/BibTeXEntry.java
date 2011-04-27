package uk.co.johnvidler.bibtex;

import java.util.*;

public class BibTeXEntry implements Comparable<String>
{
    private String type = "???";
    private String key = "???";
    private SortedMap<String, String> properties = Collections.synchronizedSortedMap(new TreeMap<String, String>());

    public BibTeXEntry( String type, String key )
    {
        this.type = type;
        this.key = key;
    }

    public String getType(){ return this.type; }
    public String getKey(){ return this.key; }

    public void setKey( String newKey ){ this.key = newKey; }
    public void setType( String newType ){ this.type = newType; }

    public void addProperty( String property, String data )
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

    public int getPropertyCount()
    {
        return properties.size();
    }

    public void removeProperty( String property )
    {
        if( properties.containsKey(property.toLowerCase()) )
            properties.remove( property.toLowerCase() );
    }

    @Override
    public String toString()
    {
        String buffer = "@" +type+ "{\n\t" +key+ "";

        for( String key : properties.keySet() )
        {
            String property = properties.get(key).toString();

            if( property.startsWith("{") )
                buffer += ",\n\t" +key+ " = " +property;
            else
                buffer += ",\n\t" +key+ " = \"" +property+ "\"";
        }

        return buffer+"\n}\n";
    }

    public String toXML()
    {
        String buffer = "<" +type+ " key=\"" +key+ "\">\n";

        for( String key : properties.keySet() )
        {
            String value = properties.get(key);
            if( value == null )
                value = "???";
            buffer += "\t<" +key+ ">" +value.toString()+ "</" +key+ ">\n";
        }

        return buffer + "</" +type+ ">\n";
    }

    public int compareTo(String s){ return key.compareTo(s); }
}
