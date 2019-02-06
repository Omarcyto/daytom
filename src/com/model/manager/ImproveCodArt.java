package com.model.manager;

import com.singleton.SQL;
import com.utils.GenericBuilder;
import com.utils.tables.Product;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;


public class ImproveCodArt {

    private final SQL QUERY = SQL.getInstance();
    private Stream<ResultSet> exis;
    private Stream<ResultSet> codart;
    private static final String SEPARATOR = System.getProperty("file.separator");
    private List<Product> productList;

    public ImproveCodArt() throws SQLException, ClassNotFoundException {


        this.productList = new ArrayList<>();
        // FROM ELECTR_EXIS
//        this.exis = QUERY.getSortedTable("electr_exis","CODART");
//        this.readFromDB("COD_EXIS",exis);
//        this.lookingForDuplicates();
////        this.updateCodArt("electr_exis","COD_EXIS");

        // FROM ELECTR_CODART
//        this.codart = QUERY.getSortedTable("electr_cliart","CODART");
//        this.readFromDB("COD", codart);
//        this.updateCodArt("electr_cliart","COD");

        // FROM ELECTR_DEVART
        this.codart = QUERY.getSortedTable("electr_compart","CODART");
        this.readFromDB("COD", codart);
        this.updateCodArt("electr_compart","COD");
    }

    public void readFromDB(String table,Stream<ResultSet> data) {
        data.forEach(rs -> {
            try {

                String code = rs.getString(table);
                String newCode = rs.getString("CODART").replaceAll(" ","");
                newCode = newCode.replaceAll(",",".");
                newCode = newCode.trim();

                // Fixing CODES
                if (newCode.startsWith("ARD") )
                    newCode = newCode.replace("ARD","ARD-");
//
//                if (newCode.equals("LED1056-7000"))
//                    newCode = newCode.replace("LED1056-","LED1056/");
//
//                if (newCode.equals("LED1031-7000"))
//                    newCode = newCode.replace("LED1031-","LED1031/");



                productList.add(GenericBuilder.of(Product::new)
                        .with(Product::setCodOrigin, code)
                        .with(Product::setCodProduct,newCode)
                        .build());

            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }
    public void lookingForDuplicates(){
        productList.stream()
                .collect(Collectors.groupingBy(Product::getCodProduct))
                .forEach((id, peopleWithSameId) -> {
                    if (peopleWithSameId.size() > 1) {
                        System.out.println("Product with same CODE: "+ id);
                    }
                });
    }
    public void updateCodArt(String table,String condition){
        productList.forEach(product -> {
            try {
                QUERY.updateCodeArt(product.getCodOrigin(), product.getCodProduct(),table,condition);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

}
