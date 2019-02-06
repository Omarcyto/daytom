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
import javax.xml.validation.Schema;
import java.io.File;
import java.io.IOException;
import java.security.Provider;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.ArrayList;

import java.util.HashMap;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;


public class CreateExcelPurchase {

    private static final DecimalFormat FORMAT_USD = CommonActions.getInstance().getFormatUSD();
    private final SQL QUERY = SQL.getInstance();
    private double tcExt;
    private int shopID;
    private Stream<ResultSet> range;
    private Stream<ResultSet> exis;
    private File source;
    private List<Product> productList;
    private List<String[]> rangeList;
    //private HashMap providerList;
   // private Stream<ResultSet> providers;
    private int counter;
    private static final String SEPARATOR = System.getProperty("file.separator");

    public CreateExcelPurchase() throws SQLException, ClassNotFoundException {

      //  this.providerList = new HashMap();
        this.productList = new ArrayList<>();
        this.counter = 0;
        this.shopID = 1;
        this.tcExt = 1;
        this.range = QUERY.getRange();
        this.rangeList = this.getMapOfPercents();
        this.exis = QUERY.getSortedTable("electr_exis","CODART");
       // this.providers = QUERY.getProviders();

        try {
            System.out.println(new File(".").getCanonicalPath());
            this.source = new File(new File(".").getCanonicalPath() + SEPARATOR + "source.xlsx");
        } catch (IOException e) {
            e.printStackTrace();
        }

        this.excelPurchaseGenerator();// Prepare PRE PURCHASES

    }

    public void excelPurchaseGenerator() {
        //    source = CommonActions.getInstance().getFileWithJFileChooser();
        //   if (source != null) {
        this.createPurchaseCompareListFromExcelFile();
        this.completeDataWithDataBase();
        //  }
    }

    public void createPurchaseCompareListFromExcelFile() {
        this.productList = new ArrayList<>();
        Workbook sourceData = this.getWorkBook(source);
        // Recover Data from Excel.
        final int[] cont = {0};
        sourceData.getSheetAt(0)
                .forEach(row -> {
                    if (cont[0] == 0) {
                        cont[0]++;
                    } else {
                        System.out.println(row.getCell(1).getStringCellValue()+" - "+row.getCell(3).getStringCellValue());
                        String codOrig = row.getCell(1).getStringCellValue().replace(" ", "").toUpperCase();
                        if( codOrig.startsWith("ARD"))
                            codOrig = codOrig.replace("ARD","ARD-");

                        productList.add(GenericBuilder.of(Product::new)
                                .with(Product::setCodOrigin, codOrig)
                                .with(Product::setQuantityOrigin, row.getCell(2).getStringCellValue().replace(",", ""))
                                .with(Product::setCodProvider, row.getCell(0).getStringCellValue())
                                .with(Product::setPriceOrigin, String.valueOf(FORMAT_USD.format(row.getCell(5).getNumericCellValue())))
                                .with(Product::setTermination, row.getCell(3).getStringCellValue())
                                .with(Product::setPriceUSD, String.valueOf(FORMAT_USD.format(row.getCell(5).getNumericCellValue() / tcExt)))
                                .with(Product::setPreTotal, FORMAT_USD.format(row.getCell(5).getNumericCellValue() * row.getCell(2).getNumericCellValue()))
                                .with(Product::setShopID, "1")
                                .with(Product::setDescription,row.getCell(4).getStringCellValue())
                                .build());
                        //WORKING..!!!!!!!!!!
                        counter++;
                    }
                });
    }


    public void completeDataWithDataBase() {

        List<Product> cache = new ArrayList<>();
        exis.forEach(rs -> productList.forEach(product -> {
            try {

                String codart = rs.getString("CODART").toUpperCase();
                System.out.println(codart);
                if (codart.replace(" ", "").equalsIgnoreCase(product.getCodOrigin().replace(" ", "").toUpperCase())) {
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
                            .with(Product::setDescription, product.getDescription())
                            .with(Product::setTermination, product.getTermination())
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
            SaveExcel excelExporter = new SaveExcel(tb, new File(new File(".").getCanonicalPath() + ".xls"), "output");
            if (excelExporter.export()) {
                JOptionPane.showMessageDialog(null, "TABLAS EXPORTADOS CON EXITOS!");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }


    public JTable fillJTableWithProducts() {
        JTable table = new JTable();

        String columnNames[] = {"Provider", "CodeExt", "Code","Termination", "Desc", "Line", "Cost Before", "Cost Now", "Gains Before", "Gains Now", "Price USD", "Price Now", "Price Before", "Qty Now", "Qty Before", "Is New?"};
        String rowData[][] = {{}, {}, {}, {}, {}, {}, {}, {}, {}, {}, {}, {}, {}, {}, {}};

        DefaultTableModel model = new DefaultTableModel(rowData, columnNames) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return true;
            }
        };
        table.setModel(model);
        CommonActions.getInstance().cleanModelOfJTable(model);
        productList.forEach(product -> {
            model.addRow(new String[]{product.getCodProvider(), product.getCodOrigin(), product.getCodProduct(), product.getTermination(), product.getDescription(), product.getLine(), product.getPriceBefore(), product.getPriceOrigin(), product.getGainPercentBefore(), product.getGainPercent(), product.getPriceUSD(), product.getPriceAfter(), product.getPriceCurrentUSD(), product.getQuantityOrigin(), product.getQuantityCurrent(), product.getIsNew()});
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
