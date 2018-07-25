package com.view.common;

import com.utils.SearchTextField;

import javax.swing.*;

public class ProductChooser extends JPanel{
    private SearchTextField productSearch;
    private JPanel panel1;
    private JTable tableResult;
    private JLabel resultLabel;

    private void createUIComponents() {
        this.productSearch = new SearchTextField();
         this.productSearch.setPlaceHolderText("Ingresa el Codigo o Desc.");
    }


    public SearchTextField getProductSearch() {
        return productSearch;
    }

    public JLabel getResultLabel() {
        return resultLabel;
    }

    public JTable getTableResult() {
        return tableResult;
    }

}
