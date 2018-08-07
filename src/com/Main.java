package com;

import com.model.common.SearchProductModel;
import com.model.manager.CreateDevolution;
import com.model.manager.CreateQuotation;
import com.model.manager.ExcelManager;
import com.model.purchase.CreatePurchase;
import com.utils.SearchTextField;
import com.utils.tables.Product;

import javax.swing.*;
import java.sql.SQLException;
import java.util.List;


/**
 * Main class.
 */
public class Main {

    public static void main(String[] args) throws SQLException, ClassNotFoundException {
       // GeneratePurchaseExisSQL generatePurchaseSQL = new GeneratePurchaseExisSQL();
      //  CreateQuotation quotation = new CreateQuotation();
     //   CreateDevolution devolution = new CreateDevolution();
       // UpdateGainsPercent updateGainsPercent = new UpdateGainsPercent();
     //   ExcelManager excelManager = new ExcelManager();


        SearchProductModel productModel = new SearchProductModel(new JTable(),new SearchTextField(), new JLabel(""),1);
        List<Product> productList = productModel.getProductList();
        //productList.forEach(product -> System.out.println(product.getCodProduct()));
        CreatePurchase pruchase= new CreatePurchase(productList,1,2);


    }

}