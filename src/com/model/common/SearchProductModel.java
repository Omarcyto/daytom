package com.model.common;

import com.singleton.CommonActions;
import com.singleton.SQL;
import com.utils.GenericBuilder;
import com.utils.SearchTextField;
import com.utils.tables.Product;

import javax.naming.ldap.PagedResultsControl;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.KeyEvent;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class SearchProductModel {

    private final SQL QUERY = SQL.getInstance();
    private int shopID;
    private Stream<ResultSet> exis;

    public List<Product> getProductList() {
        return productList;
    }

    private List<Product> productList;
    private JTable tableResult;
    private SearchTextField searchProduct;
    private DefaultTableModel modelResult;
    private JLabel resultLabel;

    public SearchProductModel(JTable tableResult, SearchTextField searchProduct, JLabel resultLabel, int shopID) throws SQLException, ClassNotFoundException {
        this.shopID = shopID;
        System.out.println("1");
        this.exis = QUERY.getExis();
        System.out.println("2");
        this.productList = new ArrayList<>();
        this.tableResult = tableResult;


        this.resultLabel = resultLabel;
        this.searchProduct = searchProduct;
        this.searchProduct.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(KeyEvent evt) {
                searchProductPressed(evt);
            }

            public void keyReleased(KeyEvent evt) {
                searchProductReleased(evt);
            }
        });
        CommonActions.getInstance().cleanModelOfJTable(this.modelResult);
        this.fillProductList();
        this.prepareTable();
    }

    private void prepareTable() {
        String columnNames[] = {"CodArt", "Cantidad"};
        String rowData[][] = {{}, {}};

        this.modelResult = new DefaultTableModel(rowData, columnNames) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return true;
            }
        };
        this.tableResult.setModel(modelResult);
        this.tableResult.getColumnModel().getColumn(0).setPreferredWidth(200);
        this.tableResult.getColumnModel().getColumn(1).setPreferredWidth(58);
        CommonActions.getInstance().cleanModelOfJTable(modelResult);
    }

    private void fillProductList() {
        exis.forEach(rs -> {
            try {
                productList.add(GenericBuilder.of(Product::new)
                        .with(Product::setCodProduct, rs.getString("CODART"))
                        .with(Product::setQuantityCurrent, rs.getString("CANTI0" + shopID))
                        .with(Product::setDescription, rs.getString("DES"))
                        .with(Product::setQuantityShop1, rs.getString("CANTI01"))
                        .with(Product::setQuantityShop1, rs.getString("CANTI02"))
                        .with(Product::setQuantityShop1, rs.getString("CANTI03"))
                        .with(Product::setQuantityShop1, rs.getString("CANTI04"))
                        .with(Product::setPriceOrigin, rs.getString("PCOSTO"))
                        .with(Product::setPriceCurrentUSD, rs.getString("PMAYOR"))
                        .with(Product::setImage, rs.getString("IMAGEN"))
                        .with(Product::setIsOffer, rs.getString("OFERTA"))
                        .with(Product::setIsNew, rs.getString("NUEVO"))
                        .with(Product::setGainPercent, rs.getString("GANANCIA"))
                        .with(Product::setNotes, rs.getString("NOTAS"))
                        .with(Product::setCodProvider, rs.getString("CODPROV"))

                        .build());
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    private void searchProductReleased(KeyEvent evt) {

        final int[] index = {0};
        CommonActions.getInstance().cleanModelOfJTable(this.modelResult);
        String selection = this.searchProduct.getText().toLowerCase().replace(" ", "");
        productList.stream()
                .filter(product -> {
                    String codProduct = product.getCodProduct().toLowerCase().replace(" ", "");
                    String codDesc = product.getDescription().toLowerCase().replace(" ", "");
                    return codProduct.contains(selection) || codDesc.contains(selection);
                })
                .forEach(p -> {
                    index[0]++;
                    this.fillTableResult(p.getCodProduct(), p.getQuantityCurrent());
                });

        resultLabel.setText("Econtrado(s): " + index[0] + " Item(s)");
    }

    private void fillTableResult(String codProduct, String quantityCurrent) {
        this.modelResult.addRow(new String[]{codProduct, quantityCurrent});
    }

    private void searchProductPressed(KeyEvent evt) {
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
