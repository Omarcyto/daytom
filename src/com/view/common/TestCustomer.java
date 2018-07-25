package com.view.common;

import javax.swing.*;

public class TestCustomer extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private CustomerChooser customers;

    public TestCustomer() {
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);
    }

    public static void main(String[] args) {
        TestCustomer dialog = new TestCustomer();
        dialog.pack();
        dialog.setVisible(true);
        System.exit(0);
    }
    public CustomerChooser getCustomers(){
        return  customers;
    }
}
