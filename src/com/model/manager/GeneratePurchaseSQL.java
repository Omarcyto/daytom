package com.model.manager;

import com.monitorjbl.xlsx.StreamingReader;
import com.singleton.CommonActions;
import com.singleton.SQL;
import com.utils.GenericBuilder;
import com.utils.tables.Product;
import com.utils.tables.ResumePurchase;
import org.apache.poi.ss.usermodel.Workbook;

import java.io.File;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.ArrayList;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;


public class GeneratePurchaseSQL {

    private static final DecimalFormat FORMAT_USD = CommonActions.getInstance().getFormatUSD();
    private final SQL QUERY = SQL.getInstance();
    private double tcExt;
    private double tcLocal;
    private int userID;
    private int shopID;
    private Stream<ResultSet> providers;
    private Stream<ResultSet> range;
    private File source;
    private List<Product> productList;
    private List<ResumePurchase> resumePurchaseList;
    private Map<Integer, String[]> resumeMap;
    private Map<Integer, String> providerList;
    private int counter;
    private static final String SEPARATOR = System.getProperty("file.separator");

    public GeneratePurchaseSQL() throws SQLException, ClassNotFoundException {

        this.productList = new ArrayList<>();
        this.resumePurchaseList = new ArrayList<>();
        this.resumeMap = new HashMap<>();
        this.providerList = new HashMap<>();
        this.counter = 0;
        this.shopID = 1;
        this.tcExt = 1;
        this.tcLocal = 6.96;
        this.userID = 2;
        this.providers = QUERY.getProviders();
        this.fillProvideList();
        this.range = QUERY.getRange();

        try {
            this.source = new File(new File(".").getCanonicalPath() + SEPARATOR + "sourceToDB.xlsx");
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.sqlPurchaseGenerator(); //PREPARE TO PURCHASE INSERT INTO DATA BASE
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

    public void sqlPurchaseGenerator() {
        //   source = CommonActions.getInstance().getFileWithJFileChooser();
        //     if (source != null) {
        this.createPurchaseFromExcelFile(source);
        this.completePurchaseResume();
        this.insertInToDataBasePurchases(); // return compart and compras tables.
        //   }
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
