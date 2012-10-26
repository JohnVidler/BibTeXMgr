package uk.co.johnvidler.util;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;

/**
 * Created by IntelliJ IDEA.
 * User: john
 * Date: 19/05/11
 * Time: 04:26
 * To change this template use File | Settings | File Templates.
 */
public final class DialogUtils
{

    public static ProgressDialog createProgressDialog( Context context, String text )
    {
        ProgressDialog dialog = new ProgressDialog( context );
        dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        dialog.setMessage(text);
        dialog.setCancelable(false);

        return dialog;
    }

    public static AlertDialog createAlertDialog( Context context, String message )
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(message).setCancelable(true);
        AlertDialog alert = builder.create();

        return alert;
    }

}
