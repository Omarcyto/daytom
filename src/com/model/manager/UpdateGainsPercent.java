package com.model.manager;

import com.monitorjbl.xlsx.StreamingReader;
import com.singleton.CommonActions;
import com.singleton.SQL;
import com.utils.GenericBuilder;
import com.utils.tables.Product;
import org.apache.poi.ss.usermodel.Workbook;

import java.io.File;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.ArrayList;

import java.util.Date;
import java.util.List;
import java.util.stream.Stream;


public class UpdateGainsPercent {

    private static final DecimalFormat FORMAT_USD = CommonActions.getInstance().getFormatUSD();
    private static final DecimalFormat FORMAT_BS = CommonActions.getInstance().getFormatBS();
    private final SQL QUERY = SQL.getInstance();
    private int shopID;
    private Stream<ResultSet> range;
    private Stream<ResultSet> exis;
    private File source;
    private List<Product> productList;
    private static final String SEPARATOR = System.getProperty("file.separator");
    private int counter;

    public UpdateGainsPercent() throws SQLException, ClassNotFoundException {


        this.productList = new ArrayList<>();
        this.shopID = 1;
        this.range = QUERY.getRange();
        this.exis = QUERY.getExis();

        try {
            this.source = new File(new File(".").getCanonicalPath() + SEPARATOR + "updateGainsPercent.xlsx");
        } catch (IOException e) {
            e.printStackTrace();
        }

        this.updateGainsPercent(); // Gains Percent Manager.
    }


    public void updateGainsPercent() {
        this.createGainsPercentFromExcelFile(source);
        this.completeGainsPercentDataWithDataBase();
        this.insertInToExisGains();
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
