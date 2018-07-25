package com.view.common;

import com.utils.SearchTextField;

import javax.swing.*;

public class CustomerChooser extends JPanel{
    private SearchTextField customerSearch;
    private JTable tableResult;
    private JLabel resultLabel;
    private JPanel panel1;

    private void createUIComponents() {
        this.customerSearch = new SearchTextField();
        this.customerSearch.setPlaceHolderText("Ingrese el Cliente");
    }
    public SearchTextField getCustomerSearch() {
        return customerSearch;
    }

    public JLabel getResultLabel() {
        return resultLabel;
    }

    public JTable getTableResult() {
        return tableResult;
    }
}
