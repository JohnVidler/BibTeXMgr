package uk.co.johnvidler.BibTeXMgr.ui;

import uk.co.johnvidler.BibTeXMgr.panels.AbstractTaskPanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.concurrent.Semaphore;

/**
 * Created by IntelliJ IDEA.
 * User: john
 * Date: 30/03/11
 * Time: 16:56
 * To change this template use File | Settings | File Templates.
 */
public class MainWindow extends JFrame
{
    private MainWindow self = null;
    private Semaphore eventLock = new Semaphore(1);
    private JToolBar toolBar = new JToolBar("Menu");

    private AbstractTaskPanel currentTaskPanel = null;

    private JButton openBtn = new JButton("Open");
    private JButton saveBtn = new JButton("Save");
    private JButton saveAsBtn = new JButton("Save As");

    private ActionListener buttonListener = new ActionListener()
    {
        public void actionPerformed( ActionEvent actionEvent )
        {
            if( eventLock.tryAcquire() )
            {
                if( currentTaskPanel != null )
                {
                    Object source = actionEvent.getSource();

                    if( source == openBtn )
                        currentTaskPanel.openBtnEvent( self );
                    else if( source == saveBtn )
                        currentTaskPanel.saveBtnEvent( self );
                    else if( source == saveAsBtn )
                        currentTaskPanel.saveAsBtnEvent( self );
                }
                eventLock.release();
            }
        }
    };

    public MainWindow()
    {
        super("BibTeXMgr");
        setDefaultCloseOperation( EXIT_ON_CLOSE );
        setPreferredSize( new Dimension( 640, 480 ) );
        self = this;

        // Events
        openBtn.addActionListener( buttonListener );
        saveBtn.addActionListener( buttonListener );
        saveAsBtn.addActionListener( buttonListener );

        // Toolbar
        toolBar.add( openBtn );
        toolBar.add( saveBtn );
        toolBar.add( saveAsBtn );

        // Layout
        setLayout(new BorderLayout());
        add( toolBar, BorderLayout.NORTH );

        pack();
        setVisible( true );
    }


    public void setCurrentTaskPanel( AbstractTaskPanel panel )
    {
        try
        {
            eventLock.acquire();
            
            if( currentTaskPanel != null )
                remove( currentTaskPanel );
            add( panel, BorderLayout.CENTER );
            currentTaskPanel = panel;

            eventLock.release();
        }
        catch( Throwable err )
        {
            err.printStackTrace();
        }
    }
}
