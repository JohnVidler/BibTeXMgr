package uk.co.johnvidler.storage;

/**
 * Created by IntelliJ IDEA.
 * User: john
 * Date: 31/03/11
 * Time: 14:37
 * To change this template use File | Settings | File Templates.
 */
public class Registry
{

    private static Registry reference = null;

    protected Registry()
    {

    }

    public static Registry getInstance()
    {
        if( reference != null )
            reference = new Registry();
        return reference;
    }

}
