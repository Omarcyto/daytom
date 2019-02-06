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
import java.text.DecimalFormat;
import java.util.ArrayList;

import java.util.Date;
import java.util.List;
import java.util.stream.Stream;


public class CreateExcelDeal {

    private static final DecimalFormat FORMAT_USD = CommonActions.getInstance().getFormatUSD();
    private final SQL QUERY = SQL.getInstance();
    private double tCam;
    private int shopID;
    private Stream<ResultSet> range;
    private Stream<ResultSet> exis;
    private File source;
    private List<Product> productList;
    private List<String[]> rangeList;
    private int counter;
    private static final String SEPARATOR = System.getProperty("file.separator");

    public CreateExcelDeal() throws SQLException, ClassNotFoundException {

        this.tCam = 6.96;
        this.productList = new ArrayList<>();
        this.counter = 0;
        this.shopID = 1;
        this.exis = QUERY.getSortedTable("electr_exis","CODART");

    }

    public void excelDealGenerator() {
        //    source = CommonActions.getInstance().getFileWithJFileChooser();
        //   if (source != null) {

        try {
            System.out.println(new File(".").getCanonicalPath());
            this.source = new File(new File(".").getCanonicalPath() + SEPARATOR + "Deals.xlsx");
        } catch (IOException e) {
            e.printStackTrace();
        }

        this.createPurchaseCompareListFromExcelFile(source);
        this.completeDataWithDataBase();
        //  }
    }

    public void requestGenerator() {

        try {
            System.out.println(new File(".").getCanonicalPath());
            this.source = new File(new File(".").getCanonicalPath() + SEPARATOR + "DealsToDB.xlsx");
        } catch (IOException e) {
            e.printStackTrace();
        }

        this.createListFromExcelFile(source);
        this.insertInToDataBaseExis();

    }

    public void insertInToDataBaseExis() {
        counter = 0;
        final int[] counterNewProducs = {0};
        String currentDate = CommonActions.getInstance().getDataBaseDateOf(new Date());
        productList.forEach(product -> {
            try {
                    // String pBefore, String pmayor, String ufecha,String ganancia, String codart, String isDeal
                    QUERY.modificarItemDeal(product.getPriceBefore(), product.getPriceUSD(),currentDate, product.getGainPercent(), product.getCodProduct(),product.getIsDeal());
                    counter++;

            } catch (SQLException e) {
                e.printStackTrace();
            }


        });
        System.out.println("TOTAL MODIFIED: " + counter + " And NEWS: " + counterNewProducs[0]);
    }

    public void createListFromExcelFile(File source) {
        this.productList = new ArrayList<>();
        Workbook sourceData = this.getWorkBook(source);

        // Recover Data from Excel.
        final int[] cont = {0};
        final double[] gains = {0};
        sourceData.getSheetAt(0)
                .forEach(row -> {
                    if (cont[0] == 0) {
                        cont[0]++;
                    } else {
                        if(row.getCell(4).getNumericCellValue()!=0)
                            gains[0] =  row.getCell(4).getNumericCellValue() - row.getCell(5).getNumericCellValue();
                        else
                            gains[0] =0;

                        productList.add(GenericBuilder.of(Product::new)
                                .with(Product::setCodProduct, row.getCell(1).getStringCellValue())
                                .with(Product::setPriceBefore, FORMAT_USD.format(row.getCell(3).getNumericCellValue()/tCam))
                                .with(Product::setPriceUSD, FORMAT_USD.format(row.getCell(6).getNumericCellValue()/tCam))
                                .with(Product::setGainPercent, FORMAT_USD.format(gains[0]))
                                .with(Product::setDeal,"1")
                                .build());
                        //WORKING..!!!!!!!!!!
                        counter++;
                    }
                });
    }


    public void createPurchaseCompareListFromExcelFile(File source) {
        this.productList = new ArrayList<>();
        Workbook sourceData = this.getWorkBook(source);

        // Recover Data from Excel.
        final int[] cont = {0};
        sourceData.getSheetAt(0)
                .forEach(row -> {
                    if (cont[0] == 0) {
                        cont[0]++;
                    } else {
                        productList.add(GenericBuilder.of(Product::new)
                                .with(Product::setCodOrigin, row.getCell(0).getStringCellValue())
                                .with(Product::setGainPercent, row.getCell(1).getStringCellValue())
                                .build());
                        //WORKING..!!!!!!!!!!
                        counter++;
                    }
                });
    }


    public void completeDataWithDataBase() {
        final double[] priceWithDiscount = {0.0};
        List<Product> cache = new ArrayList<>();
        exis.forEach(rs -> productList.forEach(product -> {
            try {
                String codart = rs.getString("CODART").toUpperCase();
                if (codart.replace(" ", "").contains(product.getCodOrigin().replace(" ", "").toUpperCase())) {
                    priceWithDiscount[0] = (rs.getDouble("PMAYOR")*tCam+ rs.getDouble("PMAYOR")*tCam * (Double.parseDouble(product.getGainPercent())/100));
                    cache.add(GenericBuilder.of(Product::new)
                            .with(Product::setCodProduct, rs.getString("CODART"))
                            .with(Product::setPriceOrigin, rs.getString("PEXTERIOR"))
                            .with(Product::setGainPercentBefore, rs.getString("GANANCIA"))
                            .with(Product::setPriceBS, FORMAT_USD.format(rs.getDouble("PMAYOR")*tCam))
                            .with(Product::setQuantityCurrent, rs.getString("CANTI0" + shopID))
                            .with(Product::setCodOrigin, product.getCodOrigin())
                            .with(Product::setGainPercent, product.getGainPercent())
                            .with(Product::setDeal, product.getIsDeal())
                            .with(Product::setPriceUSD, FORMAT_USD.format(priceWithDiscount[0]))
                            .with(Product::setDeal, "1")
                            .build());
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }));
        productList = cache;
        try {
            List<JTable> tb = new ArrayList<JTable>();
            tb.add(this.fillJTableWithProducts());
            SaveExcel excelExporter = new SaveExcel(tb, new File(new File(".").getCanonicalPath() + ".xlsx"), "Deals");
            if (excelExporter.export()) {
                JOptionPane.showMessageDialog(null, "TABLAS EXPORTADOS CON EXITOS!");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }


    public JTable fillJTableWithProducts() {
        JTable table = new JTable();

        String columnNames[] = {"Code", "Daytom Code", "Price Origin","Price Bs Before","Gains", "Gains Deal", "Price Bs Now"};
        String rowData[][] = {{}, {}, {}, {}, {},{}, {}};

        DefaultTableModel model = new DefaultTableModel(rowData, columnNames) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return true;
            }
        };
        table.setModel(model);
        CommonActions.getInstance().cleanModelOfJTable(model);
        productList.forEach(product -> {
            model.addRow(new String[]{product.getCodOrigin(), product.getCodProduct(), product.getPriceOrigin(), product.getPriceBS(), product.getGainPercentBefore(), product.getGainPercent(), product.getPriceUSD()});
        });
        //   table.setModel(model);
        return table;
    }


    public Workbook getWorkBook(File file) {
        return StreamingReader.builder()
                .rowCacheSize(100)
                .bufferSize(4096)
                .open(file);
    }

}
