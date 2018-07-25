package com.model.common;

import com.singleton.CommonActions;
import com.singleton.SQL;
import com.utils.GenericBuilder;
import com.utils.SearchTextField;
import com.utils.tables.Customer;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.KeyEvent;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class SearchCustomerModel {

    private final SQL QUERY = SQL.getInstance();
    private Stream<ResultSet> customers;
    private List<Customer> customerList;
    private JTable tableResult;
    private SearchTextField searchCustomer;
    private DefaultTableModel modelResult;
    private JLabel resultLabel;

    public SearchCustomerModel(JTable tableResult, SearchTextField searchCustomer, JLabel resultLabel) throws SQLException, ClassNotFoundException {
        this.customers = QUERY.getCustomers();
        this.customerList = new ArrayList<>();
        this.tableResult = tableResult;
        this.resultLabel = resultLabel;
        this.searchCustomer = searchCustomer;
        this.searchCustomer.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                searchCustomerPressed(evt);
            }

            public void keyReleased(java.awt.event.KeyEvent evt) {
                searchCustomerReleased(evt);
            }
        });
        CommonActions.getInstance().cleanModelOfJTable(this.modelResult);
        this.fillCustomerList();
        this.prepareTable();
    }

    private void prepareTable() {
        String columnNames[] = {"Nombre Cliente"};
        String rowData[][] = {{}};

        this.modelResult = new DefaultTableModel(rowData, columnNames) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return true;
            }
        };
        this.tableResult.setModel(modelResult);
        CommonActions.getInstance().cleanModelOfJTable(modelResult);
    }

    private void fillCustomerList() {
        customers.forEach(rs -> {
            try {
                customerList.add(GenericBuilder.of(Customer::new)
                        .with(Customer::setCustomerCod, rs.getString("CODCLI"))
                        .with(Customer::setName, rs.getString("NOMCLI"))
                        .with(Customer::setAddress, rs.getString("DIRECCION"))
                        .with(Customer::setCountryID, rs.getString("CIUDAD"))
                        .with(Customer::setCountry, rs.getString("NOMBRE"))
                        .with(Customer::setMobilePhone, rs.getString("CELULAR"))
                        .with(Customer::setLocalPhone, rs.getString("TELEFONO"))
                        .with(Customer::setEmail, rs.getString("EMAIL"))
                        .with(Customer::setNomNIT, rs.getString("NOM_NIT"))
                        .with(Customer::setLastDate, rs.getString("FULTIMO"))
                        .with(Customer::setUserID, rs.getString("RESP"))
                        .with(Customer::setCompany, rs.getString("NOMEMPRESA"))
                        .with(Customer::setShopID, rs.getString("COD_SUCURSAL"))
                        .with(Customer::setNitNumber, rs.getString("NIT"))
                        .with(Customer::setSending, rs.getString("SENDING"))
                        .with(Customer::setExchange, rs.getString("MONEDA"))
                        .with(Customer::setState, rs.getString("DEBE"))
                        .build());
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    private void searchCustomerReleased(KeyEvent evt) {

        final int[] index = {0};
        CommonActions.getInstance().cleanModelOfJTable(this.modelResult);
        String selection = this.searchCustomer.getText().toLowerCase().replace(" ", "");
        customerList.stream()
                .filter(customer -> {
                    String customerName = customer.getName().toLowerCase().replace(" ", "");
                    return customerName.contains(selection);
                })
                .forEach(customer -> {
                    index[0]++;
                    this.fillTableResult(customer.getName());
                });

        resultLabel.setText("Econtrado(s): " + index[0] + " Cliente(s)");
    }

    private void fillTableResult(String codProduct) {
        this.modelResult.addRow(new String[]{codProduct});
    }

    private void searchCustomerPressed(KeyEvent evt) {
        if (evt.getKeyCode() == evt.VK_ENTER) {
            tableResult.getInputMap(JTable.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0, false), "selectColumnCell");
            if (tableResult.getRowCount() != 0) {
                tableResult.setRowSelectionInterval(0, 0);
                tableResult.setRowSelectionAllowed(true);
                tableResult.requestFocus();
            }
        }
    }


}

