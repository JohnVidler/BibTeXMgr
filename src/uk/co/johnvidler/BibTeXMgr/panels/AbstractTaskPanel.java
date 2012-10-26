package uk.co.johnvidler.BibTeXMgr.panels;

import uk.co.johnvidler.BibTeXMgr.ui.MainWindow;
import uk.co.johnvidler.biblio.bibtex.BibTeXEntry;

import javax.swing.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.TreeSet;

public abstract class AbstractTaskPanel extends JPanel implements KeyListener
{
    protected MainWindow rootWin = null;

    public AbstractTaskPanel()
    {
        rootWin = MainWindow.getRootWindow();
    }

    /**
     * Sets the data source for the current window - may change at any time!
     * @param dataSource The new data source!
     */
    public abstract void setDataSource( TreeSet<BibTeXEntry> dataSource );

    /**
     * Stub key typed event for panels to hook
     * @param keyEvent The key event handed from the root window
     */
    public void keyTyped(KeyEvent keyEvent) { }

    /**
     * Stub key pressed event for panels to hook
     * @param keyEvent The key event handed from the root window
     */
    public void keyPressed(KeyEvent keyEvent) { }

    /**
     * Stub key released event for panels to hook
     * @param keyEvent The key event handed from the root window
     */
    public void keyReleased(KeyEvent keyEvent) { }
}
