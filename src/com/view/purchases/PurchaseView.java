package com.view.purchases;

import com.model.common.SearchCustomerModel;
import com.model.common.SearchProductModel;
import com.utils.SearchTextField;
import com.view.common.CustomerChooser;
import com.view.common.ProductChooser;

import javax.swing.*;
import java.awt.event.*;
import java.sql.SQLException;

public class PurchaseView extends JDialog {
    private JPanel contentPane;
    private JPanel resume;
    private JPanel param;
    private JPanel search;
    private JPanel chooser;
    private CustomerChooser customerChooser;
    private ProductChooser productChooser;
    private JLabel tcLabel;
    private SearchTextField exchangeTextField;
    private JLabel ivaLabel;
    private SearchTextField ivaTExtField;
    private JLabel discountLabel;
    private SearchTextField discountTextField;
    private JPanel paramPanel;
    private JPanel codePanel;
    private JLabel codVentaLabel;
    private SearchTextField codVentaTextField;
    private JPanel productData;
    private JPanel productImagePanel;
    private JPanel customerData;
    private JPanel productImage;
    private JPanel cantidades;
    private JLabel shop1Label;
    private JLabel shop2Label;
    private JLabel shop3Label;
    private JLabel shop4Label;
    private SearchTextField shop1TextField;
    private SearchTextField shop2TextField;
    private SearchTextField shop3TextField;
    private SearchTextField shop4TextField;
    private JPanel productDataManager;
    private JPanel shop1Panel;
    private JPanel shop2Panel;
    private JPanel shop3Panel;
    private JPanel shop4Panel;
    private JPanel providerPanel;
    private JPanel customerInformation;
    private JPanel nameCustomer;
    private JLabel monedaLabel;
    private SearchTextField customerSaldoTextField;
    private SearchTextField customerNameTextField;
    private JLabel customerNameLabel;
    private JLabel saldoLabel;
    private JPanel otherCustomer;
    private SearchTextField customerCiudadTextField;

    public PurchaseView() {
        setContentPane(contentPane);
        setModal(true);
        // call onCancel() when cross is clicked
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });

        // call onCancel() on ESCAPE
        contentPane.registerKeyboardAction(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);

        try {
            SearchProductModel productModel = new SearchProductModel(productChooser.getTableResult(),productChooser.getProductSearch(),productChooser.getResultLabel(),1);
            SearchCustomerModel customerModel = new SearchCustomerModel(customerChooser.getTableResult(),customerChooser.getCustomerSearch(),customerChooser.getResultLabel());
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        this.exchangeTextField.setText("6.96");
        this.exchangeTextField.setSizeOfSearchTextFienld(50,27);

        this.discountTextField.setPlaceHolderText("%");
        this.discountTextField.setSizeOfSearchTextFienld(50,27);

        this.ivaTExtField.setPlaceHolderText("%");
        this.ivaTExtField.setSizeOfSearchTextFienld(50,27);

        this.codVentaTextField.setText("V0000000");
        this.codVentaTextField.setSizeOfSearchTextFienld(120,27);
    }

    private void onOK() {
        // add your code here
        dispose();
    }

    private void onCancel() {
        // add your code here if necessary
        dispose();
    }

    public static void main(String[] args) {
        PurchaseView dialog = new PurchaseView();
        dialog.pack();
        dialog.setVisible(true);
        System.exit(0);
    }

}
