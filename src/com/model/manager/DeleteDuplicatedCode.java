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


public class DeleteDuplicatedCode {

    private final SQL QUERY = SQL.getInstance();
    private int shopID;
    private Stream<ResultSet> exis;
    private File source;
    private List<Product> productList;
    private int counter;
    private static final String SEPARATOR = System.getProperty("file.separator");

    public DeleteDuplicatedCode() throws SQLException, ClassNotFoundException {
        this.productList = new ArrayList<>();
        this.counter = 0;
        this.shopID = 1;
        this.exis = QUERY.getSortedTable("electr_exis","CODART");
    }

    public void excelDealGenerator() {

        try {
            System.out.println(new File(".").getCanonicalPath());
            this.source = new File(new File(".").getCanonicalPath() + SEPARATOR + "Duplicates.xlsx");
        } catch (IOException e) {
            e.printStackTrace();
        }

        this.createEntityFromExcel(source);
        this.completeDataWithDataBase();
        this.insertInToDataBaseExis();
        //  }
    }

    public void insertInToDataBaseExis() {
        counter = 0;
        final int[] counterNewProducs = {0};
        productList.forEach(product -> {
            try {

                if(Integer.parseInt(product.getQuantityCurrent())==0 && product.getIsDelete().equalsIgnoreCase("1")){
                    QUERY.deleteDuplicatedCode(product.getCodOrigin(),String.valueOf(shopID));
                    counter++;
                }

            } catch (SQLException e) {
                e.printStackTrace();
            }


        });

        productList.forEach(product -> {
            try {

                if(product.getIsDelete().equalsIgnoreCase("0")){
                    QUERY.modifyDuplicatedCode(product.getCodProduct(),product.getCodOrigin(),String.valueOf(shopID), product.getQuantityCurrent());
                    counterNewProducs[0]++;
                }

            } catch (SQLException e) {
                e.printStackTrace();
            }


        });


        System.out.println("TOTAL DELETED: " + counter + " And MODIFIED: " + counterNewProducs[0]);
    }


    public void createEntityFromExcel(File source) {
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
                                .with(Product::setIsDelete,row.getCell(1).getStringCellValue())
                                .with(Product::setCodProduct, row.getCell(2).getStringCellValue())
                                .build());
                        counter++;
                    }
                });
    }

    public void completeDataWithDataBase() {
        List<Product> cache = new ArrayList<>();
        exis.forEach(rs -> productList.forEach(product -> {
            try {
                String codart = rs.getString("CODART").toUpperCase();
                if (codart.replace(" ", "").equals(product.getCodOrigin().replace(" ", "").toUpperCase())) {
                    cache.add(GenericBuilder.of(Product::new)
                            .with(Product::setCodOrigin, rs.getString("CODART"))
                            .with(Product::setQuantityCurrent, rs.getString("CANTI0" + shopID))
                            .with(Product::setCodProduct, product.getCodProduct())
                            .with(Product::setIsDelete, product.getIsDelete())
                            .with(Product::setDeal, product.getIsDeal())
                            .build());
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }));
        productList = cache;
    }

    public Workbook getWorkBook(File file) {
        return StreamingReader.builder()
                .rowCacheSize(100)
                .bufferSize(4096)
                .open(file);
    }

}
