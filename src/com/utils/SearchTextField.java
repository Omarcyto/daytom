package com.utils;

import javax.swing.JTextField;
import java.awt.Dimension;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Insets;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.util.logging.Logger;

/**
 * This class is for create JTexField with place holder.
 *
 * @author Omar Limbert Huanca Sanchez - AT-[06].
 * @version 1.0.
 */
public class SearchTextField extends JTextField {

    /**
     * WIDTH_OF_JTEXTFIELD, Type: Integer, this is width for JTextField.
     */
    public static final int WIDTH_OF_JTEXTFIELD = 270;

    /**
     * HEIGHT_OF_JTEXTFIELD, Type: Integer, this is height for JTextField.
     */
    public static final int HEIGHT_OF_JTEXTFIELD = 32;

    /**
     * placeHolderText, Type: String, text for place holder.
     */
    private String placeHolderText;

    /**
     * placeHolderColor, Type: Color, color for text on place holder.
     */
    private Color placeHolderColor; // maybe you will use this in the future.

    /**
     * band, Type: boolean, this is for controller changes on place holder.
     */
    private boolean band;

    /**
     * Constructor for SearchTextField.
     *
     */
    public SearchTextField() {
        super();
        this.initComponents("Default");
        //Mapping changes on textField.
        getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void removeUpdate(DocumentEvent e) {
                band = getText().length() <= 0;
            }

            @Override
            public void insertUpdate(DocumentEvent e) {
                band = false;
            }

            @Override
            public void changedUpdate(DocumentEvent de) {
            }
        });
    }
    /**
     * This method is for update size of TextField.
     *
     * @param width This is a width for place holder.
     * @param height This is a height for place holder.
     */
    public void setSizeOfSearchTextFienld(int width, int height){
        // Dimension for JTextField.
        Dimension dimension = new Dimension(width, height);
        this.setSize(dimension);
        this.setPreferredSize(dimension);
        this.repaint();
    }
    /**
     * This method is for initialize all variables.
     *
     * @param placeHolderText This is a text for place holder.
     */
    private void initComponents(String placeHolderText) {
        // Dimension for JTextField.
        Dimension dimension = new Dimension(WIDTH_OF_JTEXTFIELD, HEIGHT_OF_JTEXTFIELD);
        // Text for place holder.
        this.placeHolderText = placeHolderText;
        // Color for place holder.
        this.placeHolderColor = new Color(0, 0, 0);
        // Flag controller for place holder.
        this.band = true;
        // Setting JTextField.
        this.setSize(dimension);
        this.setPreferredSize(dimension);
        this.setVisible(true);
        this.setMargin(new Insets(3, 6, 3, 6));
        this.setPlaceHolderText(placeHolderText);
    }

    /**
     * This method is for initialize all variables.
     *
     * @param placeHolderText This is a text for place holder.
     */
    public void setPlaceHolderText(String placeHolderText) {
        this.placeHolderText = placeHolderText;
    }

    /**
     * This method is for initialize all variables.
     *
     * @param placeHolderColor This is a text for place holder.
     */
    public void setPlaceHolderColor(Color placeHolderColor) {
        this.placeHolderColor = placeHolderColor;
    }

    @Override
    public void paintComponent(Graphics g) {
        final int number90 = 90;
        final int number2 = 2;
        super.paintComponent(g);
        // Color for place holder.
        g.setColor(
                new Color(placeHolderColor.getRed(),
                        placeHolderColor.getGreen(),
                        placeHolderColor.getBlue(), number90));
        // Drawing text.
        g.drawString((band) ? placeHolderText : "", getMargin().left,
                (getSize().height) / number2 + getFont().getSize() / number2);
    }

}

