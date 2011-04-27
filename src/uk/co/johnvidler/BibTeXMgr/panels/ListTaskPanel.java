package uk.co.johnvidler.BibTeXMgr.panels;

import uk.co.johnvidler.BibTeXMgr.ui.MainWindow;
import uk.co.johnvidler.bibtex.BibTeXEntry;
import uk.co.johnvidler.bibtex.BibTeXFile;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import javax.swing.table.AbstractTableModel;
import javax.swing.text.TableView;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.Collections;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

public class ListTaskPanel extends AbstractTaskPanel
{
    private JTable table = null;
    private JScrollPane scrollArea = null;
    private TreeMap<String, BibTeXEntry> dataSource = null;

    //private String[] columns = {"Key", "Title", "Author"};
    private String[] columns = {"Type", "Key", "Title", "Author"};

    private AbstractTableModel dataModel = new AbstractTableModel()
    {
        public String getColumnName( int col )
        {
            return columns[col];
        }

        public int getRowCount()
        {
            if( dataSource == null )
                return 0;
            return dataSource.keySet().size();
        }

        public int getColumnCount()
        {
            return columns.length;
        }

        public Object getValueAt( int row, int col )
        {
            if( dataSource == null )
                return null;

            String keys[] = dataSource.keySet().toArray(new String[0]);

            if( columns[col].equalsIgnoreCase("key") )
                return keys[row];
            else
            {
                BibTeXEntry entry = dataSource.get( keys[row] );

                if( columns[col].equalsIgnoreCase("type") )
                    return entry.getType();
                return entry.getProperty( columns[col] );
            }
        }

        public boolean isCellEditable( int row, int col )
        {
            if( dataSource == null )
                return false;

            return true;
        }

        public void setValueAt( Object value, int row, int col )
        {
            if( dataSource == null )
                return;

            String keys[] = dataSource.keySet().toArray(new String[0]);
            BibTeXEntry entry = dataSource.get( keys[row] );


            if( columns[col].equalsIgnoreCase("key") )
            {
                dataSource.remove(entry);
                entry.setKey( (String)value );
                dataSource.put( (String)value, entry );
                repaint();
                return;
            }
            else if( columns[col].equalsIgnoreCase("type") )
            {
                entry.setType( (String)value );
                return;
            }
            entry.addProperty(columns[col], (String) value);
        }

    };

    private JButton newEntryBtn = new JButton("New Entry");
    private ActionListener newEntryAction = new ActionListener()
    {
        public void actionPerformed(ActionEvent actionEvent)
        {
            dataSource.put("",new BibTeXEntry("",""));
            table.updateUI();
        }
    };

    public ListTaskPanel()
    {
        setLayout( new BorderLayout() );

        table = new JTable();
        table.setAutoResizeMode( JTable.AUTO_RESIZE_LAST_COLUMN );
        
        scrollArea = new JScrollPane( table, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED );
        scrollArea.setVisible(true);
        add(scrollArea, BorderLayout.CENTER);

        JToolBar toolbar = new JToolBar();
        toolbar.add( newEntryBtn );
        newEntryBtn.addActionListener( newEntryAction );
        add(toolbar, BorderLayout.NORTH);
    }

    public void setData( TreeMap<String, BibTeXEntry> data )
    {
        dataSource = data;
        table.setModel( dataModel );
        repaint();
    }

    @Override
    public void paint( Graphics g )
    {
        scrollArea.revalidate();
        super.paint(g);
    }

    @Override
    public void openBtnEvent( MainWindow win )
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

            int action = fileChooser.showOpenDialog(this);
            if( action == JFileChooser.APPROVE_OPTION )
            {
                File file = fileChooser.getSelectedFile();
                TreeMap<String, BibTeXEntry> entries = bibFile.read( file );
                setData( entries );
            }
        }
        catch( Throwable t )
        {
            JDialog alert = new JDialog(win);
            alert.setTitle("Error!");
            alert.add( new JLabel( t.getLocalizedMessage() ) );
            alert.setSize( new Dimension(320, 120) );
            alert.setVisible(true);
        }
    }

    @Override
    public void saveBtnEvent( MainWindow win )
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

            int action = fileChooser.showSaveDialog( this );
            if( action == JFileChooser.APPROVE_OPTION )
            {
                File file = fileChooser.getSelectedFile();

                bibFile.write( file, dataSource );

            }

        }
        catch( Throwable t )
        {
            JDialog alert = new JDialog(win);
            alert.setTitle("Error!");
            alert.add( new JLabel( t.getLocalizedMessage() ) );
            alert.setSize( new Dimension(320, 120) );
            alert.setVisible(true);
        }
    }

}
