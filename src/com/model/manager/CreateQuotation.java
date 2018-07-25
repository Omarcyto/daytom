package com.model.manager;

import com.monitorjbl.xlsx.StreamingReader;
import com.singleton.CommonActions;
import com.singleton.SQL;
import com.utils.GenericBuilder;
import com.utils.SaveExcel;
import com.utils.tables.Product;
import org.apache.poi.ss.usermodel.Workbook;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.io.File;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.TreeSet;
import java.util.stream.Collectors;
import java.util.stream.Stream;


public class CreateQuotation {

    private final SQL QUERY = SQL.getInstance();
    private int shopID;
    private Stream<ResultSet> providers;
    private Stream<ResultSet> range;
    private Stream<ResultSet> exis;
    private File source;
    private List<Product> productList;
    private Map<Integer, String> providerList;
    private int counter;
    private static final String SEPARATOR = System.getProperty("file.separator");

    public CreateQuotation() throws SQLException, ClassNotFoundException {

        this.productList = new ArrayList<>();
        this.providerList = new HashMap<>();
        this.counter = 0;
        this.shopID = 1;
        this.providers = QUERY.getProviders();
        this.fillProvideList();
        this.range = QUERY.getRange();
        this.exis = QUERY.getExis();

        try {
            System.out.println(new File(".").getCanonicalPath());
            this.source = new File(new File(".").getCanonicalPath() + SEPARATOR + "customerQuotation.xlsx");
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.excelQuotationGeneratorFromCustomer();
    }

    public void fillProvideList() {
        providers.forEach(resultSet -> {
            try {
                providerList.put(resultSet.getInt("CODPROV"), resultSet.getString("NICK"));
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    public void excelQuotationGeneratorFromCustomer() {

        this.createCustomerQuotationFromExcel(source);
        this.sortOrderAndSumQty();
        this.completeQuotationWithDataBase();
    }

    public void sortOrderAndSumQty() {
        List<Product> cache = productList;
        TreeSet<Product> collect = cache.stream()
                .collect(Collectors.toCollection(
                        () -> new TreeSet<>(Comparator.comparing(p -> p.getCodOrigin().replace(" ", "")))
                ));

//        TreeSet<Product> collect = cache.stream()
//                .collect(Collectors.toCollection(
//                        () -> new TreeSet<Product>((p1, p2) -> p1.getCodProduct().replace(" ","").compareTo(p2.getCodProduct().replace(" ","")))
//                ));

        collect.forEach(productDistinct -> {
            final int[] count = {0};
            productList.forEach(originalProduct -> {

                if (productDistinct.getCodOrigin().equalsIgnoreCase(originalProduct.getCodOrigin())) {

                    count[0]++;
                    int newQty;
                    if (count[0] == 1) {
                        if (Boolean.valueOf(originalProduct.getIsSpecial())) {
                            productDistinct.setQuantitySpecial(originalProduct.getQuantityOrigin());
                            productDistinct.setQuantityOrigin("0");
                        } else {
                            productDistinct.setQuantityOrigin(originalProduct.getQuantityOrigin());
                            productDistinct.setQuantitySpecial("0");
                        }
                    } else {
                        if (Boolean.valueOf(originalProduct.getIsSpecial())) {
                            newQty = Integer.valueOf(productDistinct.getQuantitySpecial()) + Integer.valueOf(originalProduct.getQuantityOrigin());
                            productDistinct.setQuantitySpecial(String.valueOf(newQty));
                        } else {
                            System.out.println(productDistinct.getCodOrigin()+" "+productDistinct.getQuantityOrigin()+" "+originalProduct.getQuantityOrigin());
                            newQty = Integer.valueOf(productDistinct.getQuantityOrigin()) + Integer.valueOf(originalProduct.getQuantityOrigin());
                            productDistinct.setQuantityOrigin(String.valueOf(newQty));
                        }

                    }
                }
            });
        });
        productList = new ArrayList<>();
        productList.addAll(collect);
        productList.forEach(product -> System.out.println("? " + product.getCodOrigin() + " " + product.getQuantityOrigin() + " " + product.getQuantitySpecial()));
        System.out.println("Total Products : " + productList.size());
    }

    public void createCustomerQuotationFromExcel(File source) {
        this.productList = new ArrayList<>();
        this.counter = 0;
        Workbook sourceData = this.getWorkBook(source);

        final int[] cont = {0};
        sourceData.getSheetAt(0)
                .forEach(row -> {
                    if (cont[0] == 0 || cont[0] == 1) {
                        cont[0]++;
                    } else {
                        String isSpecial = row.getCell(3).getStringCellValue().equalsIgnoreCase("si")
                                ? "true" : "false";
                        productList.add(GenericBuilder.of(Product::new)
                                .with(Product::setCodOrigin, row.getCell(0).getStringCellValue())  //ok .
                                .with(Product::setQuantityOrigin, row.getCell(1).getStringCellValue())
                                .with(Product::setCustomer, row.getCell(2).getStringCellValue())
                                .with(Product::setDescription, row.getCell(4).getStringCellValue())
                                .with(Product::setIsSpecial, isSpecial)
                                .with(Product::setCodProduct, "")
                                .with(Product::setQuantityCurrent, "0")
                                .with(Product::setPriceOrigin, "0.00")
                                .with(Product::setPriceUSD, "0.00")
                                .with(Product::setPriceCurrentUSD, "0.00")
                                .with(Product::setLastDate, "")
                                .with(Product::setGainPercent, "0.00")
                                .with(Product::setNotes, "")
                                .with(Product::setCodProvider, "0")
                                .with(Product::setExchangeExt, "0.00")
                                .build());

                    }
                    counter++;
                });

        System.out.println("Total rows: " + counter);
    }

    public void completeQuotationWithDataBase() {

        List<Product> cache = new ArrayList<>();
        final int[] count = {0};
        exis.forEach(rs -> productList.forEach(product -> {
            try {
                String codart = rs.getString("CODART").replace(" ", "").toUpperCase();
                String codartExcel = product.getCodOrigin().replace(" ", "").toUpperCase();
                if (codart.contains(codartExcel)) {
                    cache.add(GenericBuilder.of(Product::new)
                            .with(Product::setCodProduct, rs.getString("CODART"))
                            .with(Product::setQuantityCurrent, rs.getString("CANTI0" + shopID))
                            .with(Product::setPriceOrigin, rs.getString("PEXTERIOR"))
                            .with(Product::setPriceUSD, rs.getString("PCOSTO"))
                            .with(Product::setPriceCurrentUSD, rs.getString("PMAYOR"))
                            .with(Product::setLastDate, rs.getString("UFECHA"))
                            .with(Product::setGainPercent, rs.getString("GANANCIA"))
                            .with(Product::setNotes, rs.getString("NOTAS"))
                            .with(Product::setCodProvider, rs.getString("CODPROV"))
                            .with(Product::setExchangeExt, rs.getString("TCAMEXT"))
                            .with(Product::setCodOrigin, product.getCodOrigin())
                            .with(Product::setQuantityOrigin, product.getQuantityOrigin())
                            .with(Product::setQuantitySpecial, product.getQuantitySpecial())
                            .with(Product::setIsSpecial, product.getIsSpecial())
                            .with(Product::setDescription, product.getDescription())
                            .build());

                    count[0]++;
                }

            } catch (SQLException e) {
                e.printStackTrace();
            }
        }));


        // Filling new items
        productList.forEach(p1 -> {
            try {
                cache.stream().filter(p -> p1.getCodOrigin().replace(" ", "").equalsIgnoreCase(p.getCodOrigin().replace(" ", ""))).findAny().get();
            } catch (NoSuchElementException exception) {
                p1.setIsNew("1");
                cache.add(p1);
            }

        });
        productList = cache;

        productList.forEach(product -> System.out.println(product.getCodOrigin() + " " + product.getCodProduct() + " " + product.getQuantityCurrent() + " " + product.getQuantityOrigin()));
        System.out.println("Total Found :" + count[0]);

        try {
            List<JTable> tb = new ArrayList<>();
            tb.add(this.fillJTableWithProductsQuotation());
            //-------------------
            System.out.println(new File(".").getCanonicalPath() + ".xlsx");
            SaveExcel excelExporter = new SaveExcel(tb, new File(new File(".").getCanonicalPath() + ".xlsx"), "QuotationToShops");
            if (excelExporter.export()) {
                JOptionPane.showMessageDialog(null, "TABLAS EXPORTADOS CON EXITOS!");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    public JTable fillJTableWithProductsQuotation() {
        JTable table = new JTable();

        String columnNames[] = {"QUOTATION CODE", "DAYTOM CODE", "DESC", "QTY", "SPECIAL", "STOCK", "COST USD", "COST EXT", "PRICE NOW", "GAINS", "PROVIDER ID", "PROVIDER", "SPECIAL", "NOTES", "LAST DATE", "TC EXT"};
        String rowData[][] = {{}, {}, {}, {}, {}, {}, {}, {}, {}, {}, {}, {}, {}, {}, {}, {}};

        DefaultTableModel model = new DefaultTableModel(rowData, columnNames) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return true;
            }
        };
        table.setModel(model);
        CommonActions.getInstance().cleanModelOfJTable(model);
        productList.forEach(product -> model.addRow(new String[]{product.getCodOrigin(), product.getCodProduct(), product.getDescription(), product.getQuantityOrigin(), product.getQuantitySpecial(), product.getQuantityCurrent(), product.getPriceUSD(), product.getPriceOrigin(), product.getPriceCurrentUSD(), product.getGainPercent(), product.getCodProvider(), providerList.get(Integer.valueOf(product.getCodProvider())), product.getIsSpecial(), product.getNotes(), product.getLastDate(), product.getExchangeExt()}));
        return table;
    }

    public Workbook getWorkBook(File file) {
        return StreamingReader.builder()
                .rowCacheSize(100)
                .bufferSize(4096)
                .open(file);
    }

}
