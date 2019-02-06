package com.model.manager;

import com.monitorjbl.xlsx.StreamingReader;
import com.singleton.CommonActions;
import com.singleton.SQL;
import com.utils.GenericBuilder;
import com.utils.tables.Product;
import com.utils.tables.ResumeReturn;
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


public class CreateDevolution {

    private static final DecimalFormat FORMAT_USD = CommonActions.getInstance().getFormatUSD();
    private static final DecimalFormat FORMAT_BS = CommonActions.getInstance().getFormatBS();
    private final SQL QUERY = SQL.getInstance();
    private double tcLocal;
    private int userID;
    private int shopID;
    private File source;
    private List<Product> productList;
    private List<ResumeReturn> resumeDevList;
    private Map<Integer, String[]> resumeMap;
    private int counter;
    private static final String SEPARATOR = System.getProperty("file.separator");
    private ArrayList<ResumeReturn> resumePurchaseList;

    public CreateDevolution() throws SQLException, ClassNotFoundException {
        this.productList = new ArrayList<>();
        this.resumeDevList = new ArrayList<>();
        this.resumeMap = new HashMap<>();
        this.counter = 0;
        this.shopID = 1;
        this.tcLocal = 6.96;
        this.userID = 2;
        try {
            this.source = new File(new File(".").getCanonicalPath() + SEPARATOR + "devolucionesDaytom.xlsx");
        } catch (IOException e) {
            e.printStackTrace();
        }

        this.sqlDevGenerator(); // Dev Manager
    }

    public void sqlDevGenerator() {
        this.createDevArtFromExcelFile(source);
        this.completeDevResume();
        this.insertInToDataBaseDev();
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

    public Workbook getWorkBook(File file) {
        return StreamingReader.builder()
                .rowCacheSize(100)
                .bufferSize(4096)
                .open(file);
    }

}
