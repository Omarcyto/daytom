package com.model.purchase;

import com.singleton.CommonActions;
import com.singleton.SQL;
import com.utils.GenericBuilder;
import com.utils.tables.Product;
import com.utils.tables.Purchase;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class CreatePurchase {

    private static final DecimalFormat FORMAT_USD = CommonActions.getInstance().getFormatUSD();
    private static final DecimalFormat FORMAT_BS = CommonActions.getInstance().getFormatBS();
    private final SQL QUERY = SQL.getInstance();
    private double tcLocal;
    private int userID;
    private int shopID;
    private String purchaseCOD;
    private Purchase purchase;
    private List<Product> productList;
    private String customerID;
    private List<Product> productsSoldList;
    private int purchaseID;
    private static final String SEPARATOR = System.getProperty("file.separator");

    public CreatePurchase(List<Product> products, int shopID, int userID) throws SQLException, ClassNotFoundException {

        this.tcLocal = 6.96;
        this.shopID = shopID;
        this.userID = userID;
        this.productsSoldList = new ArrayList<>();
        this.productList = products;
        this.purchase = new Purchase();
        this.customerID = "0";
        this.setPurchaseSec();
        Product product = new Product();
        product.setCustomer("1479");
        product.setCodProduct("MF 9011");
        product.setQuantityToSell("10");
        product.setPriceUSD("40");
        product.setPreTotalUSD("40");
        product.setCodPurchase("V0002");
        product.setDescription("description");
        product.setShopID("1");
        product.setPriceBS("10");
        product.setPreTotalBS("10");
        product.setCodProvider("000");
        sellProduct(product);
        Product original = new Product();


        original.setCustomer("1479");
        original.setCodProduct("DLED 32P 7L SAMSUNG");
        original.setQuantityToSell("10");
        original.setPriceUSD("20");
        original.setPreTotalUSD("20");
        original.setCodPurchase("11V0002");
        original.setDescription("11description");
        original.setShopID("30");
        original.setPriceBS("30");
        original.setPreTotalBS("30");
        original.setCodProvider("11000");
        sellProduct(original);

        finishPurchase();

        JTable table = this.showPurchase(false);

        JFrame out = new JFrame();
        out.setSize(800,500);
        out.add(table);
        out.add(new JScrollPane(table));
        out.setVisible(true);


    }

    public void setPurchase(Purchase purchase) {
        this.purchase = purchase;
    }

    public void setProductList(List<Product> productList) {
        this.productList = productList;
    }

    public void setProductsSoldList(List<Product> productsSoldList) {
        this.productsSoldList = productsSoldList;
    }

    public void setPurchaseSec() {
        purchaseID = CommonActions.getInstance().getNextIndex("V");
        try {
            QUERY.setNewPurchase(purchaseID, userID, shopID);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        this.purchaseCOD = CommonActions.getInstance().getCode("V", purchaseID);
        this.purchase = GenericBuilder.of(Purchase::new)
                .with(Purchase::setPurchaseCod, purchaseCOD)
                .with(Purchase::setTotalUSD, "0")
                .with(Purchase::setTotalBS, "0")
                .with(Purchase::setIvaPercent, "10")
                .with(Purchase::setDiscountPercent, "10")
                .with(Purchase::setCreditUSD, "0")
                .with(Purchase::setCreditBS, "0")
                .with(Purchase::setDebitUSD, "0")
                .with(Purchase::setDebitBS, "0")
                .with(Purchase::setState, "0")
                .with(Purchase::setProgress, "0")
                .with(Purchase::setCustomerID, "0")
                .with(Purchase::setExchange, String.valueOf(tcLocal))
                .with(Purchase::setDate, CommonActions.getInstance().getDataBaseDateOf(new Date()))
                .with(Purchase::setUserID, String.valueOf(userID))
                .with(Purchase::setShopID, String.valueOf(shopID))
                .with(Purchase::setId, String.valueOf(purchaseID))
                .build();
    }

    public boolean sellProduct(Product productToSell) throws SQLException {
        productToSell.setQuantityParam("CANTI0" + shopID);
        if (CommonActions.getInstance().isValidProductToSell(productToSell)) {

            QUERY.sellProduct(productToSell);
            productsSoldList.add(productToSell);
            this.updatePurchase();
            return true;
        } else {
            return false;
        }
    }

    public void deleteProduct(Product productToDelete) {
        try {
            QUERY.removeProduct(productToDelete);
            productsSoldList.removeIf(product ->
                    product.getCodProduct().equalsIgnoreCase(productToDelete.getCodProduct())
                            && product.getCustomer().equalsIgnoreCase(productToDelete.getCustomer())
                            && product.getQuantityToSell().equalsIgnoreCase(productToDelete.getQuantityToSell())
            );
            this.updatePurchase();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void modifyProduct(Product productActual, Product productToModify) {

        try {
            this.deleteProduct(productActual);
            this.sellProduct(productToModify);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updatePurchase() {
        Double sumUSD = Double.valueOf(FORMAT_USD.format(productsSoldList.stream()
                .mapToDouble(o -> Double.valueOf(o.getPreTotalUSD()))
                .sum()));
        Double sumBS = Double.valueOf(FORMAT_BS.format(productsSoldList.stream()
                .mapToDouble(o -> Double.valueOf(o.getPreTotalBS()))
                .sum()));

        Double discountPercent = Double.valueOf(purchase.discountPercent());
        Double ivaPercent = Double.valueOf(purchase.getIvaPercent());

        String discountBeforeUSD = String.valueOf(sumUSD);
        String discountBeforeBS = String.valueOf(sumBS);

        Double discountAmountUSD = Double.valueOf(FORMAT_USD.format(sumUSD * discountPercent / 100));
        Double discountAmountBS = sumBS * discountPercent / 100;

        sumUSD = Double.valueOf(FORMAT_USD.format(sumUSD - discountAmountUSD));
        sumBS = Double.valueOf(FORMAT_BS.format(sumBS - discountAmountBS));

        Double ivaAmountUSD = Double.valueOf(FORMAT_USD.format(sumUSD * ivaPercent / 100));
        Double ivaAmountBS = Double.valueOf(FORMAT_BS.format(sumBS * ivaPercent / 100));

        sumUSD = Double.valueOf(FORMAT_USD.format(sumUSD + ivaAmountUSD));
        sumBS = Double.valueOf(FORMAT_BS.format(sumBS + ivaAmountBS));

        String literalUSD = CommonActions.getInstance().literalOf(String.valueOf(sumUSD), "USD Dolares.");
        String literalBS = CommonActions.getInstance().literalOf(String.valueOf(sumBS), "BS Bolivianos.");

        purchase.setTotalUSD(String.valueOf(sumUSD));
        purchase.setLiteralUSD(literalUSD);
        purchase.setIvaAmountUSD(String.valueOf(ivaAmountUSD));
        purchase.setDiscountBeforeUSD(discountBeforeUSD);
        purchase.setDiscountAmountUSD(String.valueOf(discountAmountUSD));
        purchase.setDebitUSD(String.valueOf(sumUSD));
        purchase.setLiteralBS(literalBS);
        purchase.setTotalBS(String.valueOf(sumBS));
        purchase.setDiscountBeforeBS(discountBeforeBS);
        purchase.setIvaAmountBS(String.valueOf(ivaAmountBS));
        purchase.setDiscountAmountBS(String.valueOf(discountAmountBS));
        purchase.setDebitBS(String.valueOf(sumBS));

    }

    public void finishPurchase() {
        try {
            QUERY.insertPurchaseResume(purchase);
            QUERY.purchaseComplete(purchase);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public JTable showPurchase(boolean exchange) {

        JTable table = new JTable();

        String columnNames[] = {"Codigo", "Descripcion", "Cantidad", "$Precio Usd", "$Total Usd", "$Precio Bs", "$Total Bs"};
        String rowData[][] = {{}, {}, {}, {}, {}, {}, {}};

        DefaultTableModel model = new DefaultTableModel(rowData, columnNames) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return true;
            }
        };
        table.setModel(model);




        CommonActions.getInstance().cleanModelOfJTable(model);
        productsSoldList.forEach(product -> model.addRow(new String[]{product.getCodProduct()
                , product.getDescription()
                , product.getQuantityToSell()
                , product.getPriceUSD()
                , product.getPreTotalUSD()
                , product.getPriceBS()
                , product.getPreTotalBS()}));

        this.configTable(table,exchange);

        return table;

    }

    private void configTable(JTable table, boolean exchange) {
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        table.getColumnModel().getColumn(0).setPreferredWidth(150);
        table.getColumnModel().getColumn(1).setPreferredWidth(250);
        table.getColumnModel().getColumn(2).setPreferredWidth(70);
        table.getColumnModel().getColumn(3).setPreferredWidth(70);
        table.getColumnModel().getColumn(4).setPreferredWidth(70);
        table.getColumnModel().getColumn(5).setPreferredWidth(70);
        table.getColumnModel().getColumn(6).setPreferredWidth(70);

        TableColumnModel m=table.getColumnModel();
        if(exchange){
            m.removeColumn(table.getColumnModel().getColumn(5));
            m.removeColumn(table.getColumnModel().getColumn(5));
        }
        else{
            m.removeColumn(table.getColumnModel().getColumn(3));
            m.removeColumn(table.getColumnModel().getColumn(3));
        }

        table.setAutoCreateRowSorter(true);
        table.repaint();

    }

}

