package uk.co.johnvidler.BibTeXMgr.ui;

import uk.co.johnvidler.BibTeXMgr.panels.AbstractTaskPanel;
import uk.co.johnvidler.BibTeXMgr.panels.ListTaskPanel;
import uk.co.johnvidler.bibtex.BibTeXEntry;
import uk.co.johnvidler.bibtex.BibTeXFile;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.util.TreeSet;
import java.util.concurrent.Semaphore;

public class MainWindow extends JFrame implements KeyListener
{
    private static MainWindow self = null;
    
    private Semaphore eventLock = new Semaphore(1);
    private JToolBar toolBar = new JToolBar("Menu");

    private AbstractTaskPanel currentTaskPanel = null;

    // The current BibTex file
    private TreeSet<BibTeXEntry> dataSource = null;

    private JButton newBtn = new JButton("New");
    private ActionListener newBtnEvent = new ActionListener()
    {
        public void actionPerformed(ActionEvent actionEvent)
        {
            dataSource = new TreeSet<BibTeXEntry>();
            dataSource.add(new BibTeXEntry("Article", "new"));

            if( currentTaskPanel != null )
                currentTaskPanel.setDataSource( dataSource );
        }
    };

    private JButton openBtn = new JButton("Open");
    private ActionListener openBtnEvent = new ActionListener()
    {
        public void actionPerformed(ActionEvent actionEvent)
        {
            BibTeXFile bibFile = new BibTeXFile();
            try
            {
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setMultiSelectionEnabled(false);
                fileChooser.setFileFilter( new FileFilter() {
                    @Override
                    public boolean accept(File file) {
                        return file.isDirectory() || file.getAbsolutePath().endsWith(".bib");
                    }

                    @Override
                    public String getDescription() {
                        return "BibTex files";
                    }
                } );

                int action = fileChooser.showOpenDialog(self);
                if( action == JFileChooser.APPROVE_OPTION )
                {
                    File file = fileChooser.getSelectedFile();
                    dataSource = bibFile.read( file );

                    if( currentTaskPanel != null )
                        currentTaskPanel.setDataSource( dataSource );

                    if( currentTaskPanel instanceof ListTaskPanel )
                    {
                        ListTaskPanel panel = (ListTaskPanel)currentTaskPanel;
                    }
                }
            }
            catch( Throwable t )
            {
                JDialog alert = new JDialog( self, "Error!", true );
                alert.setTitle("Error!");
                alert.add(new JLabel(t.getLocalizedMessage()));
                alert.pack();
                alert.setVisible(true);
            }
        }
    };

    private JButton saveBtn = new JButton("Save");
    

    private JButton saveAsBtn = new JButton("Save As");
    private ActionListener saveAsEvent = new ActionListener()
    {
        public void actionPerformed(ActionEvent actionEvent)
        {
            BibTeXFile bibFile = new BibTeXFile();
            try
            {
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setMultiSelectionEnabled(false);
                fileChooser.setFileFilter( new FileFilter() {
                    @Override
                    public boolean accept(File file) {
                        return file.isDirectory() || file.getAbsolutePath().endsWith(".bib");
                    }

                    @Override
                    public String getDescription() {
                        return "BibTex files";
                    }
                } );

                int action = fileChooser.showSaveDialog( self );
                if( action == JFileChooser.APPROVE_OPTION )
                {
                    File file = fileChooser.getSelectedFile();

                    bibFile.write( file, dataSource );

                }

            }
            catch( Throwable t )
            {
                JDialog alert = new JDialog( self, "Error!", true );
                alert.add( new JLabel( t.getLocalizedMessage() ) );
                alert.pack();
                alert.setVisible(true);
            }
        }
    };

    public MainWindow()
    {
        super("BibTeXMgr");
        setDefaultCloseOperation( EXIT_ON_CLOSE );
        setPreferredSize( new Dimension( 800, 600 ) );
        self = this;

        // Events
        newBtn.addActionListener( newBtnEvent );
        openBtn.addActionListener( openBtnEvent );
        //saveBtn.addActionListener( saveAsEvent );
        saveAsBtn.addActionListener( saveAsEvent );

        // Listen for key events - hand them over to inner views!
        this.addKeyListener( this );

        // Toolbar
        toolBar.add( newBtn );
        toolBar.add( openBtn );
        toolBar.add( saveBtn );
        toolBar.add( saveAsBtn );

        // Layout
        setLayout(new BorderLayout());
        add( toolBar, BorderLayout.NORTH );

        pack();
        setVisible( true );
    }

    /**
     * Provides a reference to the application root window, mainly for application-wide modal dialogs.
     *
     * @return A reference to the current application window.
     */
    public static MainWindow getRootWindow() { return self; }


    public void setCurrentTaskPanel( AbstractTaskPanel panel )
    {
        try
        {
            eventLock.acquire();
            
            if( currentTaskPanel != null )
                remove( currentTaskPanel );
            add( panel, BorderLayout.CENTER );
            panel.setDataSource( dataSource );
            currentTaskPanel = panel;

            eventLock.release();
        }
        catch( Throwable err )
        {
            err.printStackTrace();
        }
    }

    public void showDialog( String title, String message, boolean modal )
    {
        final JDialog alert = new JDialog( self, title, modal );
        alert.setMinimumSize( new Dimension(320, 120) );

        JLabel label = new JLabel( message );
        label.setBorder( BorderFactory.createEmptyBorder(10,10,10,10) );

        JButton okBtn = new JButton("Ok");
        okBtn.setBorder( BorderFactory.createEmptyBorder(10,10,10,10) );
        okBtn.addActionListener( new ActionListener() {
            public void actionPerformed(ActionEvent actionEvent) {
                alert.setVisible(false);
            }
        } );

        alert.setLayout( new BorderLayout() );
        alert.add( label, BorderLayout.CENTER );
        alert.add( okBtn, BorderLayout.SOUTH );
        
        alert.setModalityType(Dialog.ModalityType.DOCUMENT_MODAL);
        alert.setAlwaysOnTop(true);
        alert.pack();
        alert.setVisible(true);
        alert.setResizable(false);
        alert.transferFocus();
    }

    public void keyTyped(KeyEvent keyEvent)
    {
        if( currentTaskPanel != null )
            currentTaskPanel.keyTyped( keyEvent );
    }

    public void keyPressed(KeyEvent keyEvent)
    {
        if( currentTaskPanel != null )
            currentTaskPanel.keyPressed(keyEvent);
    }

    public void keyReleased(KeyEvent keyEvent)
    {
        if( currentTaskPanel != null )
            currentTaskPanel.keyReleased(keyEvent);
    }
}
