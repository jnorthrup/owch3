/*
 * Created by IntelliJ IDEA.
 * User: root
 * Date: May 27, 2002
 * Time: 3:20:09 PM
 * To change template for new class use
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package net.sourceforge.gui;

import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;


public class TextPanel extends JPanel {
    private JLabel label;
    private JTextComponent textField;

    public TextPanel(String name) {
        super(new FlowLayout());

        setLabel(new JLabel(name));
        setTextField(new JTextField());
        getLabel().setLabelFor(getTextField());
        this.add(getLabel());
        this.add(getTextField());
    }

    public JLabel getLabel() {
        return label;
    }

    public void setColumns(int c) {
        ((JTextField) getTextField()).setColumns(c);
    }

    public void setLabel(JLabel label) {
        this.label = label;
    }

    public JTextComponent getTextField() {
        return textField;
    }

    public void setTextField(JTextComponent textField) {
        this.textField = textField;
    }

    public String getText() {
        return getTextField().getText();
    }

    public void setText(String text) {
        getTextField().setText(text);
    }

}
