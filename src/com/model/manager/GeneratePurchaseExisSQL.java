package com.model.manager;

import com.monitorjbl.xlsx.StreamingReader;
import com.singleton.CommonActions;
import com.singleton.SQL;
import com.utils.GenericBuilder;
import com.utils.SaveExcel;
import com.utils.tables.Product;
import com.utils.tables.ResumePurchase;
import com.utils.tables.ResumeReturn;
import com.view.manager.PurchaseAdvantage;
import org.apache.poi.ss.usermodel.Workbook;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.io.File;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.ArrayList;

import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.TreeSet;
import java.util.stream.Collectors;
import java.util.stream.Stream;


public class GeneratePurchaseExisSQL {

    private static final DecimalFormat FORMAT_USD = CommonActions.getInstance().getFormatUSD();
    private static final DecimalFormat FORMAT_BS = CommonActions.getInstance().getFormatBS();
    private final SQL QUERY = SQL.getInstance();
    private double tcExt;
    private double tcLocal;
    private int userID;
    private int shopID;
    private ArrayList<String[]> provider;
    private Stream<ResultSet> providers;
    private Stream<ResultSet> range;
    private Stream<ResultSet> gainsPercent;
    private Stream<ResultSet> exis;
    private File source;
    private List<Product> productList;
    private List<ResumePurchase> resumePurchaseList;
    private List<ResumeReturn> resumeDevList;
    private List<String[]> rangeList;
    private String codPurchase;
    private Map<Integer, String[]> resumeMap;
    private Map<Integer, String> providerList;
    private int counter;
    private static final String SEPARATOR = System.getProperty("file.separator");

    public GeneratePurchaseExisSQL() throws SQLException, ClassNotFoundException {


        this.productList = new ArrayList<>();
        this.resumePurchaseList = new ArrayList<>();
        this.resumeDevList = new ArrayList<>();
        this.resumeMap = new HashMap<>();
        this.providerList = new HashMap<>();
        this.counter = 0;
        this.shopID = 1;
        this.tcExt = 1;
        this.tcLocal = 6.96;
        this.userID = 2;
        this.codPurchase = "C0004";
        // Filling Data from DataBase.
        this.gainsPercent = QUERY.getCodart();
        this.providers = QUERY.getProviders();
        this.fillProvideList();
        this.range = QUERY.getRange();
        this.rangeList = this.getMapOfPercents();
        this.exis = QUERY.getExis();

        try {
            System.out.println(new File(".").getCanonicalPath());
            this.source = new File(new File(".").getCanonicalPath() + SEPARATOR + "sourceToDB.xlsx");
        } catch (IOException e) {
            e.printStackTrace();
        }

        this.sqlExisGenerator(); //PREPARE EXIS TO INSERT INTO DATA BASE

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

    public void updateGainsPercent() {
        this.createGainsPercentFromExcelFile(source);
        this.completeGainsPercentDataWithDataBase();
        this.insertInToExisGains();
    }

    public void sqlDevGenerator() {
        this.createDevArtFromExcelFile(source);
        this.completeDevResume();
        this.insertInToDataBaseDev();
    }

    public void excelPurchaseGenerator() {
        //    source = CommonActions.getInstance().getFileWithJFileChooser();
        //   if (source != null) {
        this.createPurchaseCompareListFromExcelFile();
        this.completeDataWithDataBase();
        //  }
    }

    public void sqlPurchaseGenerator() {
        //   source = CommonActions.getInstance().getFileWithJFileChooser();
        //     if (source != null) {
        this.createPurchaseFromExcelFile(source);
        this.completePurchaseResume();
        this.insertInToDataBasePurchases(); // return compart and compras tables.
        //   }
    }

    public void sqlExisGenerator() {
        //    source = CommonActions.getInstance().getFileWithJFileChooser();
        //    if (source != null) {
        this.createPurchaseFromExcelFile(source);
        this.completePurchaseResume();
        this.insertInToDataBaseExis(); // updating and adding produtcs
        //  }
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

    public void insertInToExisGains() {

        System.out.println("Update Gains Percent: ");
        productList.forEach(p -> {
            try {
                QUERY.updateGainsPercent(p.getPriceUSD(), CommonActions.getInstance().getDataBaseDateOf(new Date()), p.getGainPercent(), p.getCodProduct());

            } catch (SQLException e) {
                e.printStackTrace();
            }
        });


    }

    public void insertInToDataBaseDev() {
        final boolean[] response = {false};
        final int[] passed = {0};
        final int[] failed = {0};
        productList.forEach(d -> {
            try {
                response[0] = QUERY.returnDevArt(d.getCodCli(), d.getCodProduct(), d.getQuantityCurrent(), d.getPriceUSD(), d.getPreTotalUSD(), d.getCodDev(), d.getDescription(), d.getShopID(), d.getPriceBS(), d.getPreTotalBS(), d.getCodVent());
                if (response[0]) passed[0]++;
                else failed[0]++;

                response[0] = QUERY.returnProduct(d.getShopID(), d.getQuantityCurrent(), d.getCodProduct(), d.getQuantityCurrent() + " = " + d.getDescription());
                if (response[0]) passed[0]++;
                else failed[0]++;
            } catch (SQLException e) {
                e.printStackTrace();
            }

        });
        System.out.println("TOTAL Insert DevArt and Exis: " + passed[0] + " passed, " + failed[0] + " failed.");

        final int[] passed1 = {0};
        final int[] failed1 = {0};
        resumeDevList.forEach(resumeReturn -> {
            try {
                response[0] = QUERY.returnResume(resumeReturn.getCodCli(), resumeReturn.getCodDev(), resumeReturn.getTotalUSD(), resumeReturn.getLiteralUSD(), resumeReturn.getDate(), resumeReturn.getTcLocal(), resumeReturn.getUserID(), resumeReturn.getShopID(), resumeReturn.getLiteralBS(), resumeReturn.getProcess(), resumeReturn.getTotalBS());
                if (response[0]) passed1[0]++;
                else failed1[0]++;
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
        System.out.println("TOTAL Insert Dev Resume: " + passed1[0] + " passed, " + failed1[0] + " failed.");
    }

    public void insertInToDataBasePurchases() {
        System.out.println("Insert compart: ");
        productList.forEach(p -> {
            try {
                QUERY.insertPurchaseItem(p.getCodProvider(), p.getCodOrigin(), p.getCodProduct(), p.getDescription(), p.getLine(), p.getPriceBefore(), p.getPriceOrigin(), p.getPriceUSD(), p.getGainPercent(), p.getPriceAfter(), p.getPriceCurrentUSD(), p.getPreTotal(), p.getQuantityOrigin(), p.getShopID(), resumeMap.get(Integer.valueOf(p.getCodProvider()))[0]);

            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
        System.out.println("Insert compras: ");
        resumePurchaseList.forEach(resumePurchase -> {
            QUERY.insertPurchaseResume(resumePurchase.getProvideID(), resumePurchase.getPurchaseID(), resumePurchase.getDate(), resumePurchase.getTcLocal(), resumePurchase.getTotal(), resumePurchase.getLiteral(), resumePurchase.getUserID(), resumePurchase.getTcExt(), resumePurchase.getState(), resumePurchase.getShopID());
        });

    }

    public void insertInToDataBaseExis() {
        counter = 0;
        final int[] counterNewProducs = {0};
        String currentDate = CommonActions.getInstance().getDataBaseDateOf(new Date());

        productList.forEach(product -> {
            try {
                if (product.getIsNew().equalsIgnoreCase("0")) {
                    // Current product
                    // String cantidad, String pcosto, String pmayor, String ufecha, String codor, String tcamext, String pexterior, String ganancia, String codprov, String codart

                    QUERY.modificarItem(product.getQuantityOrigin(), product.getPriceUSD(), product.getPriceAfter(), currentDate, product.getCodOrigin(), String.valueOf(tcExt), product.getPriceOrigin(), product.getGainPercent(), product.getCodProvider(), product.getCodProduct());

                    counter++;
                } else {
                    // new product
                    // String codart,String desc, String cantidad, String pcosto, String pmayor, String fec, String codor, String pexterior, String ganancia, String codprov
                    QUERY.insertarItemNuevoCompra(product.getCodProduct(), product.getDescription(), product.getQuantityOrigin(), product.getPriceUSD(), product.getPriceAfter(), currentDate, product.getCodOrigin(), product.getPriceOrigin(), product.getGainPercent(), product.getCodProvider(), product.getLine());
                    counterNewProducs[0]++;
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }


        });
        System.out.println("TOTAL MODIFIED: " + counter + " And NEWS: " + counterNewProducs[0]);
    }

    private void completeDevResume() {
        this.resumePurchaseList = new ArrayList<>();

        resumeMap.forEach((customerID, data) -> {
            resumeDevList.add(GenericBuilder.of(ResumeReturn::new)
                    .with(ResumeReturn::setCodCli, customerID.toString())
                    .with(ResumeReturn::setCodDev, data[0])
                    .with(ResumeReturn::setTotalUSD, data[2])
                    .with(ResumeReturn::setLiteralUSD, CommonActions.getInstance().literalOf(data[2], "USD Dolares."))
                    .with(ResumeReturn::setDate, CommonActions.getInstance().getDataBaseDateOf(new Date()))
                    .with(ResumeReturn::setTcLocal, String.valueOf(tcLocal))
                    .with(ResumeReturn::setUserID, String.valueOf(userID))
                    .with(ResumeReturn::setShopID, String.valueOf(this.shopID))
                    .with(ResumeReturn::setLiteralBS, CommonActions.getInstance().literalOf(data[1], "BS Bolivianos."))
                    .with(ResumeReturn::setProcess, "1")
                    .with(ResumeReturn::setTotalBS, data[1])
                    .build());
        });
    }

    private void completePurchaseResume() {
        this.resumePurchaseList = new ArrayList<>();

        final int[] lastPurchase = new int[1];
        final String[] purchaseKey = new String[1];

        try {
            QUERY.getLastPurchase(shopID).forEach(last -> {
                try {
                    lastPurchase[0] = last.getInt(1);
                    purchaseKey[0] = last.getString(2);

                } catch (SQLException e) {
                    e.printStackTrace();
                }
            });
        } catch (SQLException e) {
            e.printStackTrace();
        }

        resumeMap.forEach((provideID, data) -> {
            lastPurchase[0]++;
            data[0] = CommonActions.getInstance().getCode(purchaseKey[0], lastPurchase[0]);
            resumePurchaseList.add(GenericBuilder.of(ResumePurchase::new)
                    .with(ResumePurchase::setProvideID, provideID.toString())
                    .with(ResumePurchase::setPurchaseID, data[0])
                    .with(ResumePurchase::setDate, CommonActions.getInstance().getDataBaseDateOf(new Date()))
                    .with(ResumePurchase::setTcLocal, String.valueOf(tcLocal))
                    .with(ResumePurchase::setTotal, data[1])
                    .with(ResumePurchase::setLiteral, CommonActions.getInstance().literalOf(data[1], "USD American Dollar."))
                    .with(ResumePurchase::setUserID, String.valueOf(userID))
                    .with(ResumePurchase::setTcExt, String.valueOf(tcExt))
                    .with(ResumePurchase::setState, "1")
                    .with(ResumePurchase::setShopID, String.valueOf(shopID))
                    .build());

        });
    }

    public void createGainsPercentFromExcelFile(File source) {
        this.productList = new ArrayList<>();
        this.counter = 0;
        Workbook sourceData = this.getWorkBook(source);
        // Recover Data from Excel.
        final int[] cont = {0};
        sourceData.getSheetAt(0)
                .forEach(row -> {
                    if (cont[0] == 0) {
                        cont[0]++;
                    } else {
                        productList.add(GenericBuilder.of(Product::new)
                                .with(Product::setCodProduct, row.getCell(0).getStringCellValue()) //ok
                                .with(Product::setGainPercent, row.getCell(1).getStringCellValue().replace(",", ""))
                                .with(Product::setShopID, String.valueOf(this.shopID))
                                .build());
                        System.out.println(row.getCell(0).getStringCellValue() + " " + row.getCell(1).getStringCellValue());
                        counter++;
                    }
                });

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
                                .with(Product::setIsSpecial, isSpecial)
                                .build());

                    }
                    counter++;
                });

        System.out.println("Total rows: " + counter);
    }


    public void createDevArtFromExcelFile(File source) {
        this.productList = new ArrayList<>();
        this.counter = 0;
        Workbook sourceData = this.getWorkBook(source);
        resumeMap = new HashMap<>();
        final double[] preTotalBS = new double[1];
        final double[] preTotalUSD = new double[1];
        final Integer[] customer = new Integer[1];
        // Recover Data from Excel.
        final int[] cont = {0};
        sourceData.getSheetAt(0)
                .forEach(row -> {
                    if (cont[0] == 0 || cont[0] == 1) {
                        cont[0]++;
                    } else {
                        preTotalBS[0] = Double.valueOf(FORMAT_BS.format(row.getCell(2).getNumericCellValue() * row.getCell(3).getNumericCellValue())); //ok);
                        preTotalUSD[0] = Double.valueOf(FORMAT_USD.format(row.getCell(2).getNumericCellValue() * row.getCell(3).getNumericCellValue() / tcLocal)); //ok);
                        productList.add(GenericBuilder.of(Product::new)
                                .with(Product::setCodCli, row.getCell(0).getStringCellValue())  //ok .
                                .with(Product::setCodProduct, row.getCell(1).getStringCellValue()) //ok
                                .with(Product::setQuantityCurrent, row.getCell(2).getStringCellValue().replace(",", ""))
                                .with(Product::setPriceBS, row.getCell(3).getStringCellValue())
                                .with(Product::setPreTotalBS, String.valueOf(preTotalBS[0])) //ok)
                                .with(Product::setPriceUSD, FORMAT_USD.format(row.getCell(3).getNumericCellValue() / tcLocal)) //ok)
                                .with(Product::setPreTotalUSD, String.valueOf(preTotalUSD[0])) //ok)
                                .with(Product::setCodDev, row.getCell(4).getStringCellValue())
                                .with(Product::setDescription, row.getCell(5).getStringCellValue())
                                .with(Product::setShopID, row.getCell(6).getStringCellValue())
                                .with(Product::setCodVent, row.getCell(7).getStringCellValue())
                                .build());
                        //WORKING..!!!!!!!!!!
                        System.out.println(row.getCell(0).getStringCellValue() + " " + row.getCell(1).getStringCellValue());

                        customer[0] = Integer.valueOf(row.getCell(0).getStringCellValue());
                        String[] data = new String[4];

                        if (resumeMap.get(customer[0]) == null) {
                            data[0] = row.getCell(4).getStringCellValue();
                            data[1] = String.valueOf(preTotalBS[0]);
                            data[2] = String.valueOf(preTotalUSD[0]);
                            resumeMap.put(customer[0], data);
                        } else {
                            data[0] = row.getCell(4).getStringCellValue();
                            data[1] = FORMAT_BS.format(Double.valueOf(resumeMap.get(customer[0])[1]) + preTotalBS[0]);
                            data[2] = FORMAT_USD.format(Double.valueOf(resumeMap.get(customer[0])[2]) + preTotalUSD[0]);
                            resumeMap.put(customer[0], data);
                        }
                        counter++;
                    }
                });

        System.out.println("Total rows: " + counter);
    }

    public void completeGainsPercentDataWithDataBase() {

        System.out.println("=============================");
        List<Product> cache = new ArrayList<>();
        exis.forEach(rs -> productList.forEach(product -> {
            try {
                String codart = rs.getString("CODART").toUpperCase();
                if (codart.replace(" ", "").equalsIgnoreCase(product.getCodProduct().replace(" ", "").toUpperCase())) {
                    Double costPrice = rs.getDouble("PCOSTO");
                    String finalPrice = FORMAT_USD.format(costPrice + (costPrice * (Double.parseDouble(product.getGainPercent()) / 100)));
                    cache.add(GenericBuilder.of(Product::new)
                            .with(Product::setCodProduct, rs.getString("CODART"))
                            .with(Product::setPriceCurrentUSD, rs.getString("PMAYOR"))
                            .with(Product::setPriceOrigin, rs.getString("PCOSTO"))
                            .with(Product::setGainPercent, product.getGainPercent())
                            .with(Product::setPriceUSD, finalPrice)
                            .with(Product::setShopID, String.valueOf(this.shopID))
                            .build());
                }

            } catch (SQLException e) {
                e.printStackTrace();
            }
        }));

        productList = cache;

        productList.forEach(p -> System.out.println(p.getCodProduct() + " " + p.getGainPercent() + " " + p.getPriceOrigin() + " " + p.getPriceUSD()));
    }


    public void createPurchaseFromExcelFile(File source) {
        resumeMap = new HashMap<>();
        this.productList = new ArrayList<>();
        this.counter = 0;
        Workbook sourceData = this.getWorkBook(source);

        final double[] preTotal = new double[1];
        final Integer[] provider = new Integer[1];
        // Recover Data from Excel.
        final int[] cont = {0};
        sourceData.getSheetAt(0)
                .forEach(row -> {
                    if (cont[0] == 0) {
                        cont[0]++;
                    } else {
                        productList.add(GenericBuilder.of(Product::new)
                                .with(Product::setCodProvider, row.getCell(0).getStringCellValue())  //ok .
                                .with(Product::setCodOrigin, row.getCell(1).getStringCellValue()) //ok .
                                .with(Product::setCodProduct, row.getCell(2).getStringCellValue()) //ok .
                                .with(Product::setDescription, row.getCell(3).getStringCellValue()) //ok .
                                .with(Product::setPriceOrigin, String.valueOf(FORMAT_USD.format(row.getCell(6).getNumericCellValue()))) //ok .
                                .with(Product::setPriceBefore, String.valueOf(FORMAT_USD.format(row.getCell(5).getNumericCellValue()))) //ok .
                                .with(Product::setPriceUSD, String.valueOf(FORMAT_USD.format(row.getCell(9).getNumericCellValue() / tcExt))) //ok .
                                .with(Product::setGainPercent, String.valueOf(FORMAT_USD.format(row.getCell(8).getNumericCellValue()))) //ok .
                                .with(Product::setGainPercentBefore, String.valueOf(FORMAT_USD.format(row.getCell(7).getNumericCellValue()))) //ok .
                                .with(Product::setPriceAfter, String.valueOf(FORMAT_USD.format(row.getCell(10).getNumericCellValue()))) //ok .
                                .with(Product::setPriceCurrentUSD, String.valueOf(FORMAT_USD.format(row.getCell(11).getNumericCellValue()))) //ok .
                                .with(Product::setPreTotal, FORMAT_USD.format(row.getCell(6).getNumericCellValue() * row.getCell(12).getNumericCellValue())) //ok
                                .with(Product::setQuantityOrigin, row.getCell(12).getStringCellValue().replace(",", "")) // ok  .
                                .with(Product::setQuantityCurrent, row.getCell(13).getStringCellValue().replace(",", "")) // ok  .
                                .with(Product::setShopID, String.valueOf(this.shopID))
                                .with(Product::setIsNew, row.getCell(14).getStringCellValue()) // .
                                .with(Product::setLine, row.getCell(4).getStringCellValue()) //.
                                // .with(Product::setCodPurchase, row.getCell(15).getStringCellValue())
                                .build());
                        //WORKING..!!!!!!!!!!
                        System.out.println(row.getCell(0).getStringCellValue() + " " + row.getCell(1).getStringCellValue());
                        preTotal[0] = Double.valueOf(FORMAT_USD.format(row.getCell(6).getNumericCellValue() * row.getCell(12).getNumericCellValue())); //ok);

                        provider[0] = Integer.valueOf(row.getCell(0).getStringCellValue());
                        String[] data = new String[4];

                        if (resumeMap.get(provider[0]) == null) {
                            data[0] = row.getCell(15).getStringCellValue();
                            data[1] = String.valueOf(preTotal[0]);
                            resumeMap.put(provider[0], data);
                        } else {
                            data[0] = row.getCell(15).getStringCellValue();
                            data[1] = FORMAT_USD.format(Double.valueOf(resumeMap.get(provider[0])[1]) + preTotal[0]);
                            resumeMap.put(provider[0], data);
                        }
                        counter++;
                    }
                });

        System.out.println("Total rows: " + counter);
    }


    public void createPurchaseCompareListFromExcelFile() {
        this.productList = new ArrayList<>();
        Workbook sourceData = this.getWorkBook(source);

        final double[] preTotal = new double[1];
        final Integer[] provider = new Integer[1];
        // Recover Data from Excel.
        final int[] cont = {0};
        sourceData.getSheetAt(0)
                .forEach(row -> {
                    if (cont[0] == 0) {
                        cont[0]++;
                    } else {
                        productList.add(GenericBuilder.of(Product::new)
                                .with(Product::setCodOrigin, row.getCell(1).getStringCellValue())
                                .with(Product::setQuantityOrigin, row.getCell(2).getStringCellValue().replace(",", ""))
                                .with(Product::setCodProvider, row.getCell(0).getStringCellValue())
                                .with(Product::setPriceOrigin, String.valueOf(FORMAT_USD.format(row.getCell(4).getNumericCellValue())))
                                .with(Product::setDescription, row.getCell(3).getStringCellValue())
                                .with(Product::setPriceUSD, String.valueOf(FORMAT_USD.format(row.getCell(4).getNumericCellValue() / tcExt)))
                                .with(Product::setPreTotal, FORMAT_USD.format(row.getCell(4).getNumericCellValue() * row.getCell(2).getNumericCellValue()))
                                .with(Product::setShopID, "1")
                                .build());
                        //WORKING..!!!!!!!!!!
                        counter++;
                    }
                });
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
                            .build());

                    count[0]++;
                }

            } catch (SQLException e) {
                e.printStackTrace();
            }
        }));
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

    public void completeDataWithDataBase() {

        List<Product> cache = new ArrayList<>();
        exis.forEach(rs -> productList.forEach(product -> {
            try {
                String codart = rs.getString("CODART").toUpperCase();
                if (codart.replace(" ", "").contains(product.getCodOrigin().replace(" ", "").toUpperCase())) {
                    cache.add(GenericBuilder.of(Product::new)
                            .with(Product::setCodProduct, rs.getString("CODART"))
                            .with(Product::setLine, rs.getString("LIN"))
                            .with(Product::setPriceBefore, rs.getString("PEXTERIOR"))
                            .with(Product::setGainPercentBefore, rs.getString("GANANCIA"))
                            .with(Product::setPriceCurrentUSD, rs.getString("PMAYOR"))
                            .with(Product::setQuantityCurrent, rs.getString("CANTI0" + shopID))
                            .with(Product::setCodOrigin, product.getCodOrigin())
                            .with(Product::setQuantityOrigin, product.getQuantityOrigin())
                            .with(Product::setCodProvider, product.getCodProvider())
                            .with(Product::setPriceOrigin, product.getPriceOrigin())
                            .with(Product::setDescription, product.getDescription())
                            .with(Product::setPriceUSD, product.getPriceUSD())
                            .with(Product::setPreTotal, product.getPreTotal())
                            .with(Product::setShopID, "1")
                            .with(Product::setIsNew, "0")
                            .build());
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
        this.completeGainsPercent();
        try {
            List<JTable> tb = new ArrayList<JTable>();
            tb.add(this.fillJTableWithProducts());
            PurchaseAdvantage ui = new PurchaseAdvantage();
            ui.getTableResult().setModel(this.fillJTableWithProducts().getModel());
            ui.pack();
            ui.setVisible(true);
            //-------------------
            SaveExcel excelExporter = new SaveExcel(tb, new File(new File(".").getCanonicalPath() + ".xlsx"), "output");
            if (excelExporter.export()) {
                JOptionPane.showMessageDialog(null, "TABLAS EXPORTADOS CON EXITOS!");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public JTable fillJTableWithProductsQuotation() {
        JTable table = new JTable();

        String columnNames[] = {"QUOTATION CODE", "DAYTOM CODE", "QTY", "SPECIAL", "STOCK", "COST USD", "COST EXT", "PRICE NOW", "GAINS", "PROVIDER ID", "PROVIDER", "SPECIAL", "NOTES", "LAST DATE", "TC EXT"};
        String rowData[][] = {{}, {}, {}, {}, {}, {}, {}, {}, {}, {}, {}, {}, {}, {}, {}};

        DefaultTableModel model = new DefaultTableModel(rowData, columnNames) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return true;
            }
        };
        table.setModel(model);
        CommonActions.getInstance().cleanModelOfJTable(model);
        productList.forEach(product -> model.addRow(new String[]{product.getCodOrigin(), product.getCodProduct(), product.getQuantityOrigin(), product.getQuantitySpecial(), product.getQuantityCurrent(), product.getPriceUSD(), product.getPriceOrigin(), product.getPriceCurrentUSD(), product.getGainPercent(), product.getCodProvider(), providerList.get(Integer.valueOf(product.getCodProvider())), product.getIsSpecial(), product.getNotes(), product.getLastDate(), product.getExchangeExt()}));
        return table;
    }

    public JTable fillJTableWithProducts() {
        JTable table = new JTable();

        String columnNames[] = {"Provider", "CodeExt", "Code", "Desc", "Line", "Cost Before", "Cost Now", "Gains Before", "Gains Now", "Price USD", "Price Now", "Price Before", "Qty Now", "Qty Before", "Is New?"};
        String rowData[][] = {{}, {}, {}, {}, {}, {}, {}, {}, {}, {}, {}, {}, {}};

        DefaultTableModel model = new DefaultTableModel(rowData, columnNames) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return true;
            }
        };
        table.setModel(model);
        CommonActions.getInstance().cleanModelOfJTable(model);
        productList.forEach(product -> {
            model.addRow(new String[]{product.getCodProvider(), product.getCodOrigin(), product.getCodProduct(), product.getDescription(), product.getLine(), product.getPriceBefore(), product.getPriceOrigin(), product.getGainPercentBefore(), product.getGainPercent(), product.getPriceUSD(), product.getPriceAfter(), product.getPriceCurrentUSD(), product.getQuantityOrigin(), product.getQuantityCurrent(), product.getIsNew()});
        });
        //   table.setModel(model);
        return table;
    }

    public void completeGainsPercent() {

        final double[] priceAfter = {0.00};
        final double[] percent = {0.00};
        productList.forEach(product -> {
            product.setGainPercent(this.getGainsPercentOf(product.getPriceUSD(), product.getLine()));
            priceAfter[0] = Double.valueOf(product.getPriceUSD());
            percent[0] = 1 + (Double.valueOf(product.getGainPercent()) / 100);
            product.setPriceAfter(FORMAT_USD.format(priceAfter[0] * percent[0]));
        });

    }

    public String getGainsPercentOf(String price, String line) {

        Double priceOrig = Double.parseDouble(price);
        final Double[] costInit = {0.00};
        final Double[] costEnd = {0.00};
        final String[] lin = {""};
        final String[] result = {""};

        this.rangeList
                .forEach(rs -> {
                    lin[0] = rs[0];
                    costInit[0] = Double.valueOf(rs[1]);
                    costEnd[0] = Double.valueOf(rs[2]);
                    if ((costInit[0] <= priceOrig) && (costEnd[0] >= priceOrig) && (lin[0].equalsIgnoreCase(line))) {
                        result[0] = rs[3];
                    }
                });

        return result[0].isEmpty() ? "0.00" : result[0];

    }

    public ArrayList<String[]> getMapOfPercents() {

        List<String[]> result = new ArrayList<>();

        range.forEach(resultSet -> {
            String[] data = new String[6];
            try {
                data[0] = resultSet.getString("CODLIN");
                data[1] = resultSet.getString("COSTO_INI");
                data[2] = resultSet.getString("COSTO_FIN");
                data[3] = resultSet.getString("GANANCIA");
                data[4] = resultSet.getString("COD_SUCURSAL");
                result.add(data);

            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
        return (ArrayList<String[]>) result;

    }

    public Workbook getWorkBook(File file) {
        return StreamingReader.builder()
                .rowCacheSize(100)
                .bufferSize(4096)
                .open(file);
    }

}
