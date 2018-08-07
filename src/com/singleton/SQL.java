package com.singleton;

import com.utils.ResultSetIterator;
import com.utils.tables.Product;
import com.utils.tables.Purchase;

import java.io.File;
import java.io.FileInputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.Date;
import java.util.Spliterators;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * This class si for all query's to database.
 *
 * @author Omar Limbert Huanca Sanchez - AT-[06].
 * @version 1.0.
 */
public class SQL {

    private static SQL sqlQuerys;
    /**
     * connection, Type: Connection, this is connection to replace to connection on Singleton.
     */
    private Connection connection;
    private String consulta = null;
    private Statement stm;
    private Statement stm1;
    private Statement stm2;
    private ResultSet rs;

    /**
     * Constructor for SearchQuery.
     */
    public SQL() throws SQLException, ClassNotFoundException {
        connection = DBConnection.getInstance().getConnection();
        stm = DBConnection.getInstance().getStm();
        stm1 = DBConnection.getInstance().getStm();
        stm2 = DBConnection.getInstance().getStm2();
    }

    public void setToSecondStatement() {
        stm = stm2;
    }

    public void setToFirstStatement() {
        stm = stm1;
    }

    public static SQL getInstance() throws ClassNotFoundException, SQLException {
        if (sqlQuerys == null) {
            sqlQuerys = new SQL();
        }
        return sqlQuerys;
    }

    // 2018
    public void purchaseComplete(Purchase purchase) throws SQLException {

        consulta = "UPDATE electr_sec_ventas SET TERMINADO ='1',MODIFICAR ='" + CommonActions.getInstance().getDataBaseDateOf(new Date()) + "', RESP ='" + purchase.getUserID() + "', ESTADO = '0' WHERE CODVENT ='" + purchase.getId() + "' AND cod_sucursal ='" + purchase.getShopID() + "'";
        stm.execute(consulta);
    }

    public void daytom_catalogo(String nom_cat, int lin_cat, int cod_prov_cat) throws SQLException {

        consulta = "INSERT INTO ELECTR_CATALOGO (NOM_CATALOGO,LINEA_CATALOGO,CODPROV_CATALOGO,COD_SUCURSAL) VALUES ('" + nom_cat + "','" + lin_cat + "','" + cod_prov_cat + "',1)";
        stm.execute(consulta);
    }

    public void daytom_ciudad(String nom_cat) throws SQLException {

        consulta = "INSERT INTO ELECTR_CIUDAD (NOMBRE,COD_SUCURSAL) VALUES ('" + nom_cat + "',1)";
        stm.execute(consulta);
    }

    public ResultSet getdaytom_ciudad() throws SQLException {
        consulta = "SELECT * FROM CIUDAD";
        rs = stm.executeQuery(consulta);
        return rs;
    }

    public ResultSet getUsuarios() throws SQLException {
        consulta = "SELECT * FROM ELECTR_USUARIO ORDER BY NOMBRE";
        rs = stm.executeQuery(consulta);
        return rs;
    }

    public ResultSet getdes_daytom() throws SQLException {
        consulta = "SELECT * FROM EXIS";
        rs = stm.executeQuery(consulta);
        return rs;
    }

    public ResultSet getDeudasCliente() throws SQLException {
        consulta = "SELECT DISTINCT electr_resumen.CODCLI\n" +
                "FROM electr_resumen\n" +
                "WHERE electr_resumen.ESTADO = 0\n" +
                "ORDER BY electr_resumen.CODCLI";
        rs = stm.executeQuery(consulta);
        return rs;
    }


    public void daytom_cliart(int codcli, String codart, int cantidad, Double precio, Double total, String codvent, int nro, String des, Double precio_bs, Double total_bs) throws SQLException {

        consulta = "INSERT INTO ELECTR_CLIART (CODCLI, CODART, CANTIDAD, PRECIO, TOTAL, CODVENT, NRO, DES, PRECIO_BS, TOTAL_BS, COD_SUCURSAL) VALUES ('" + codcli + "','" + codart + "','" + cantidad + "','" + precio + "','" + total + "','" + codvent + "','" + nro + "','" + des + "','" + precio_bs + "','" + total_bs + "',1)";
        //     System.out.println(consulta);
        stm.execute(consulta);
    }

    public ResultSet getdaytom_cliart() throws SQLException {
        consulta = "SELECT * FROM CLIART ORDER BY CODCLI, CODART";
        rs = stm.executeQuery(consulta);
        return rs;
    }

    public void setTC(String codvent) throws SQLException {
        consulta = "UPDATE RESUMEN SET TC =7  WHERE COD ='" + codvent + "' ";
        stm.execute(consulta);

    }

    public void setTCc(String codvent) throws SQLException {
        consulta = "UPDATE CLIART SET TC =7  WHERE COD ='" + codvent + "' ";
        stm.execute(consulta);

    }

    public void daytom_clientes(int codcli, String nomcli, String direccion, String celular, String telefono) throws SQLException {

        consulta = "INSERT INTO ELECTR_CLIENTES (CODCLI, NOMCLI, DIRECCION, CIUDAD, CELULAR, TELEFONO, DEBE, HABER, EMAIL, NIT, NOM_NIT, FULTIMO, RESP, NOMEMPRESA, COD_SUCURSAL) VALUES ('" + codcli + "','" + nomcli + "','" + direccion + "',1,'" + celular + "','" + telefono + "',0,0,'','','','" + new java.sql.Timestamp(new java.util.Date().getTime()) + "',1,'',1)";
        //    System.out.println(consulta);
        stm.execute(consulta);
    }

    public ResultSet getdaytom_clientes() throws SQLException {
        consulta = "SELECT * FROM CLIENTES ORDER BY CODCLI";
        rs = stm.executeQuery(consulta);

        return rs;
    }

    public void daytom_compart(String codcom, int nro, String codart, int cantidad, Double precio, Double total, Double preciof, int ganancia, Double preciousd, Double precioant, Double preciof_bs, Double precioant_bs, int codprov, int id_sucursal) throws SQLException {

        consulta = "INSERT INTO ELECTR_COMPART (CODCOM,NRO,CODART,CANTIDAD,PRECIO,TOTAL,PRECIOF,GANANCIA,PRECIOUSD,PRECIOANT,COD_SUCURSAL,PRECIOF_BS,PRECIOANT_BS,CODPROV) VALUES ('" + codcom + "','" + nro + "','" + codart + "','" + cantidad + "','" + precio + "','" + total + "','" + preciof + "','" + ganancia + "','" + preciousd + "','" + precioant + "','" + id_sucursal + "','" + preciof_bs + "','" + precioant_bs + "','" + codprov + "')";
        //    System.out.println(consulta);
        stm.execute(consulta);
    }

    public ResultSet getcompras_det() throws SQLException {
        consulta = "SELECT * FROM COMPRAS_DET ORDER BY CODING";
        rs = stm.executeQuery(consulta);

        return rs;
    }

    public ResultSet getcompras() throws SQLException {
        consulta = "SELECT * FROM COMPRAS ORDER BY CODING";
        rs = stm.executeQuery(consulta);

        return rs;
    }

    public void daytom_compras(String codcom, Timestamp fecha, Double tc, int codprov, int lugGar, Double total, String literal, int resp, Double tcext, Double total_usd, String literal_usd, String literal_usd_eng) throws SQLException {

        consulta = "INSERT INTO ELECTR_COMPRAS (CODCOM,FECHA,TC,CODPROV,TOTAL,LITERAL,RESP,TCEXT,PROCESO,COD_SUCURSAL,TOTAL_USD,LITERAL_USD,LITERAL_USD_ENG) VALUES ('" + codcom + "','" + fecha + "','" + tc + "','" + codprov + "','" + total + "','" + literal + "','" + resp + "','" + tcext + "',1,1,'" + total_usd + "','" + literal_usd + "','" + literal_usd_eng + "')";
        //      System.out.println(consulta);
        stm.execute(consulta);
    }

    public ResultSet get_exis() throws SQLException {
        consulta = "SELECT * FROM EXIS ORDER BY CODART,LIN";
        rs = stm.executeQuery(consulta);

        return rs;
    }

    public void daytom_exis(String codart, String des, String remplazo, int canti04, Double pcosto, Double pmayor, Double pespecial, int lin, Timestamp ufecha, Timestamp cfecha, int codcat, int rank, FileInputStream imagen, FileInputStream img2) throws SQLException {
        //   System.out.println("llega consulta"+consulta);
        consulta = "INSERT INTO ELECTR_EXIS (CODART,DES,REMPLAZO,CANTI03,CANTI01,PCOSTO,PMAYOR,PESPECIAL,CANTI02,CANTI04,LIN,UFECHA,CFECHA,CODCAT,RANK,TIENE_IMAGEN,IMG,IMAGEN,OFERTA,DES_CATALOGO,NUEVO,CODOR,COD_SUCURSAL,COD_UBICACION,CONFIRMADO,RESPONSABLE,TCAMEXT,IMGP) VALUES ('" + codart + "','" + des + "','" + remplazo + "',0,'" + canti04 + "','" + pcosto + "','" + pmayor + "','" + pespecial + "',0,0,'" + lin + "','" + ufecha + "','" + cfecha + "','" + codcat + "','" + rank + "',0,'" + imagen + "','no-img.png',0,'" + des + "',0,'" + codart + "',1,1,1,1,1.000,'" + img2 + "')";
        //    System.out.println(consulta);
        stm.execute(consulta);
    }

    public ResultSet get_facturas() throws SQLException {
        consulta = "SELECT * FROM FACTURAS ORDER BY COD";
        rs = stm.executeQuery(consulta);

        return rs;
    }

    public void daytom_facturas(String cod_factura, int codcli, int resp, Timestamp fecpago, Double v_deuda, Double v_pago, Double tc, Timestamp fec_mov) throws SQLException {
        //    System.out.println("llega consulta"+consulta);
        consulta = "INSERT INTO ELECTR_FACTURAS (COD_FACTURA,CODCLI,RESP,FECPAGO,V_DEUDA,V_PAGO,TC,FECMOV,COD_SUCURSAL,ANTIGUO) VALUES ('" + cod_factura + "','" + codcli + "','" + resp + "','" + fecpago + "','" + v_deuda + "','" + v_pago + "','" + tc + "','" + fec_mov + "',1,1)";
        //      System.out.println(consulta);
        stm.execute(consulta);
    }

    public ResultSet get_proveedor() throws SQLException {
        consulta = "SELECT * FROM PROV ORDER BY NOM";
        rs = stm.executeQuery(consulta);

        return rs;
    }

    public void daytom_proveedor(int codprov, String nom, int origen, String direc, String telef, String email, String sr, String fax, String cuenta, String nick, String otros) throws SQLException {
        // System.out.println("llega consulta"+consulta);
        consulta = "INSERT INTO ELECTR_PROVEEDOR (CODPROV, NOM, ORIGEN,DIREC,TELEF,EMAIL,SR,FAX,CUENTA,COD_SUCURSAL,ANTIGUO,NICK,OTROS) VALUES ('" + codprov + "','" + nom + "','" + origen + "','" + direc + "','" + telef + "','" + email + "','" + sr + "','" + fax + "','" + cuenta + "',1,1,'" + nick + "','" + otros + "')";
        //    System.out.println(consulta);
        stm.execute(consulta);
    }

    public ResultSet get_resumen() throws SQLException {
        consulta = "SELECT * FROM RESUMEN ORDER BY COD";
        rs = stm.executeQuery(consulta);

        return rs;
    }

    public void daytom_resumen(int codcli, String codvent, Double total_v, String literal, Timestamp fec_venta, Double imp, Double iva, Double tc, int resp, Double desc_antes, Double descuento, Double descu, Double iva_bs, Double desc_antes_bs, Double descu_bs, Double total_vbs, String literalbs, int pagado) throws SQLException {
        // System.out.println("llega consulta"+consulta);
        consulta = "INSERT INTO ELECTR_RESUMEN (CODCLI,CODVENT,TOTAL_V,LITERAL,FEC_VENTA,IMP,IVA,TC,RESP,DESC_ANTES,DESCUENTO,DESCU,IVA_BS,DESC_ANTES_BS,DESCU_BS,TOTAL_VBS,LITERALBS,ESTADO,COD_SUCURSAL,PAGADO) VALUES ('" + codcli + "','" + codvent + "','" + total_v + "','" + literal + "','" + fec_venta + "','" + imp + "','" + iva + "','" + tc + "','" + resp + "','" + desc_antes + "','" + descuento + "','" + descu + "','" + iva_bs + "','" + desc_antes_bs + "','" + descu_bs + "','" + total_vbs + "','" + literalbs + "',0,1,'" + pagado + "')";
        //   System.out.println(consulta);
        stm.execute(consulta);
    }

    public void daytom_deudas(int codcli, String codvent, Double total, Double total_bs, Double pago, Double saldo, Double pago_bs, Double saldo_bs) throws SQLException {
        // System.out.println("llega consulta"+consulta);
        consulta = "INSERT INTO ELECTR_DEUDAS (CODCLI,CODVENT,TOTAL,TOTAL_BS,PAGO,SALDO,PAGO_BS,SALDO_BS) VALUES ('" + codcli + "','" + codvent + "','" + total + "','" + total_bs + "','" + pago + "','" + saldo + "','" + pago_bs + "','" + saldo_bs + "')";
        //   System.out.println(consulta);
        stm.execute(consulta);
    }

    /*
    public ResultSet getCliart() throws SQLException{
    consulta = "SELECT * FROM daytom_cliart ORDER BY CODCLI,CODVENT";
    rs = stm.executeQuery(consulta);
    return rs;
    }
    public void setCliart(int nuevo,String codvent,String codart,int cant) throws SQLException{
        consulta = "UPDATE daytom_cliart SET codvent_num = '"+nuevo+"' WHERE CODVENT ='"+codvent+"' AND CODART = '"+codart+"' AND CANTIDAD = '"+cant+"' ";
        System.out.println(consulta);
        stm.execute(consulta);
    }*/

    //*********************
    public ResultSet consultarExis(String cod) throws SQLException {
        consulta = "SELECT ELECTR_PROVEEDOR.NICK,ELECTR_PROVEEDOR.CODPROV, ELECTR_EXIS.*\n" +
                "FROM ELECTR_EXIS \n" +
                "INNER JOIN ELECTR_PROVEEDOR ON ELECTR_PROVEEDOR.CODPROV = ELECTR_EXIS.CODPROV\n" +
                "WHERE CODART = '" + cod + "'";
        rs = stm.executeQuery(consulta);
        return rs;
    }

    public ResultSet hacerConsulta(String tabla) throws SQLException {
        consulta = "SELECT * FROM " + tabla + "";
        rs = stm.executeQuery(consulta);
        return rs;
    }

    public ResultSet hacerConsultaOrdenada(String tabla, String orden) throws SQLException {
        consulta = "SELECT * FROM " + tabla + " ORDER BY " + orden;
        rs = stm.executeQuery(consulta);
        return rs;
    }

    public boolean consulta(String consulta) throws SQLException {


        return stm.execute(consulta);
    }

    //2018
    public Stream<ResultSet> getExis() throws SQLException {
        consulta = "SELECT * FROM electr_exis ORDER BY CODART";
        return this.stream(stm.executeQuery(consulta));
    }

    //2018
    public Stream<ResultSet> getProductToSell(Product product) throws SQLException {
        consulta = "SELECT " + product.getQuantityParam() + " FROM `electr_exis` WHERE electr_exis.CODART = '" + product.getCodProduct() + "'";
        return this.stream(stm.executeQuery(consulta));

    }

    public ResultSet getCliartModificar(String codcli, String codvent, String codart) throws SQLException {
        consulta = "SELECT ELECTR_CLIART.* \n" +
                "\n" +
                "FROM ELECTR_CLIART \n" +
                "WHERE ELECTR_CLIART.CODCLI = '" + codcli + "'AND ELECTR_CLIART.CODVENT = '" + codvent + "' AND ELECTR_CLIART.CODART = '" + codart + "'  ";
        //  System.out.println(consulta);
        rs = stm.executeQuery(consulta);
        return rs;
    }

    public ResultSet getExisUpdate() throws SQLException {
        consulta = "SELECT REMPLAZO,DES,CODART,PCOSTO,CANTI01,PMAYOR,LIN,UFECHA,CFECHA,GANANCIA FROM ELECTR_EXIS ORDER BY CODART ";
        rs = stm.executeQuery(consulta);
        return rs;


    }

    public ResultSet getPagosUpdate(Timestamp f1, Timestamp f2) throws SQLException {
        consulta = "SELECT * FROM ELECTR_PAGOS WHERE FECPAGO  BETWEEN '" + f1 + "' AND '" + f2 + "' ORDER BY FECPAGO DESC";
        //  System.out.println(consulta);
        rs = stm.executeQuery(consulta);
        return rs;


    }

    public ResultSet getPagos(int codcli, String f1, String f2) throws SQLException {
        consulta = "SELECT * FROM ELECTR_PAGOS WHERE  ELECTR_PAGOS.CODCLI = '" + codcli + "' AND ELECTR_PAGOS.proceso = 1 AND (ELECTR_PAGOS.FECPAGO BETWEEN '" + f1 + "' AND '" + f2 + "' ) ORDER BY ELECTR_PAGOS.FECPAGO ASC";
        System.out.println("ERROR " + consulta);
        rs = stm.executeQuery(consulta);
        return rs;


    }

    public ResultSet getResumen(int codcli, String f1, String f2) throws SQLException {
        consulta = "SELECT * FROM electr_resumen WHERE electr_resumen.CODCLI = '" + codcli + "' AND (electr_resumen.FEC_VENTA BETWEEN '" + f1 + "'  AND '" + f2 + "') ORDER BY electr_resumen.FEC_VENTA ASC";
        //System.out.println("ERROR FECHA "+consulta);
        rs = stm.executeQuery(consulta);
        return rs;


    }

    public ResultSet getReporte(String codcli) throws SQLException {
        consulta = "SELECT * FROM electr_reporte WHERE electr_reporte.COD = '" + codcli + "'  ORDER BY electr_reporte.ORDEN ASC";
        rs = stm.executeQuery(consulta);
        return rs;


    }

    public ResultSet getDev(int codcli, String f1, String f2) throws SQLException {
        consulta = "SELECT * FROM electr_resumen_devolucion WHERE electr_resumen_devolucion.CODCLI = '" + codcli + "' AND electr_resumen_devolucion.PROCESO = 1 AND (electr_resumen_devolucion.FEC_DEV BETWEEN '" + f1 + "'  AND '" + f2 + "') ORDER BY electr_resumen_devolucion.FEC_DEV ASC";
        rs = stm.executeQuery(consulta);
        return rs;


    }

    public ResultSet getResumenUpdate(Timestamp f1, Timestamp f2) throws SQLException {
        consulta = "SELECT * FROM electr_resumen WHERE FEC_VENTA IS NULL OR FEC_VENTA BETWEEN '" + f1 + "' AND '" + f2 + "' ORDER BY CODVENT ASC ";
        //   System.out.println(consulta);
        rs = stm.executeQuery(consulta);
        return rs;


    }

    public ResultSet getExisC() throws SQLException {
        consulta = "SELECT * FROM electr_exis ORDER BY CODART ";
        rs = stm.executeQuery(consulta);
        return rs;
    }

    public ResultSet getExisCodigo() throws SQLException //OPTIMIZADO
    {
        consulta = "SELECT CODART,CODOR,CANTI01,CANTI02,CANTI03,CANTI04 FROM electr_exis ORDER BY CODART ";
        rs = stm.executeQuery(consulta);
        return rs;
    }

    public ResultSet getDeudaCliente(String codcli) throws SQLException {
        consulta = "SELECT * FROM electr_deudas WHERE CODCLI = '" + codcli + "' ORDER BY CODVENT";
        rs = stm.executeQuery(consulta);
        return rs;
    }

    public ResultSet getVentaPagar(String codcli, String codvent) throws SQLException {
        consulta = "SELECT * FROM electr_deudas WHERE CODCLI = '" + codcli + "' AND CODVENT = '" + codvent + "'";
        rs = stm.executeQuery(consulta);
        return rs;
    }

    public void pagarVenta(String codcli, String codvent) throws SQLException {
        consulta = "UPDATE electr_RESUMEN SET PAGADO = 1 WHERE CODCLI ='" + codcli + "' AND CODVENT ='" + codvent + "' ";
        stm.execute(consulta);
        consulta = "DELETE FROM electr_DEUDAS WHERE CODCLI ='" + codcli + "' AND CODVENT ='" + codvent + "' ";
        //   System.out.println("llego a consulta venta");
        stm.execute(consulta);
        // return rs;
    }

    public void pagarVentaM(String codcli, String codvent, Double pago, Double pago_bs) throws SQLException {
        consulta = "UPDATE electr_DEUDAS SET PAGO = '" + pago + "', SALDO = TOTAL- '" + pago + "', PAGO_BS = '" + pago_bs + "',SALDO_BS = TOTAL_BS - '" + pago_bs + "' WHERE CODCLI ='" + codcli + "' AND CODVENT ='" + codvent + "' ";
        //  System.out.println("llego a consulta Mventa");
        stm.execute(consulta);

        // return rs;
    }

    public ResultSet getDatos(String cod) throws SQLException {
        consulta = "SELECT * FROM electr_exis WHERE CODART = '" + cod + "'";

        rs = stm.executeQuery(consulta);


        return rs;
    }

    public ResultSet getDatosT(String cod) throws SQLException //OPTIMIZADO
    {
        consulta = " SELECT ELECTR_EXIS.*,ELECTR_PROVEEDOR.*,ELECTR_LINEA.*\n" +
                "FROM `ELECTR_EXIS` \n" +
                "INNER JOIN ELECTR_PROVEEDOR ON ELECTR_PROVEEDOR.CODPROV = ELECTR_EXIS.CODPROV  \n" +
                "INNER JOIN ELECTR_LINEA ON ELECTR_LINEA.CODLIN = ELECTR_EXIS.LIN  \n" +
                "\n" +
                "WHERE ELECTR_EXIS.CODART  = '" + cod + "' ";


        rs = stm.executeQuery(consulta);


        return rs;
    }

    public ResultSet getClienteCiudad(String codcli) throws SQLException {
        int codci = 0;
        consulta = "SELECT CIUDAD FROM electr_clientes WHERE CODCLI = '" + codcli + "'";

        rs = stm.executeQuery(consulta);
        while (rs.next()) {
            codci = rs.getInt("CIUDAD");
        }
        consulta = "SELECT NOMBRE FROM electr_ciudad WHERE CODCIU = '" + codci + "'";

        rs = stm.executeQuery(consulta);


        return rs;
    }

    public ResultSet getClienteNombre(String codcli) throws SQLException {
        consulta = "SELECT NOMCLI FROM electr_clientes WHERE CODCLI = '" + codcli + "' ORDER BY NOMCLI";

        rs = stm.executeQuery(consulta);


        return rs;
    }

    public ResultSet getClienteDatos(String codcli) throws SQLException {
        consulta = "SELECT ELECTR_USUARIO.nombre,ELECTR_USUARIO.apellido,SUM(ELECTR_RESUMEN.saldo),SUM(ELECTR_RESUMEN.saldobs),ELECTR_CLIENTES.*,ELECTR_CIUDAD.* FROM ELECTR_CLIENTES \n" +
                "INNER JOIN ELECTR_CIUDAD ON ELECTR_CIUDAD.codciu = ELECTR_CLIENTES.CIUDAD \n" +
                "INNER JOIN ELECTR_USUARIO ON ELECTR_USUARIO.cod_usuario = ELECTR_CLIENTES.RESP \n" +
                "INNER JOIN ELECTR_RESUMEN ON ELECTR_RESUMEN.ESTADO = 0 AND ELECTR_RESUMEN.CODCLI = '" + codcli + "'\n" +
                "WHERE ELECTR_CLIENTES.CODCLI = '" + codcli + "' ";

        rs = stm.executeQuery(consulta);


        return rs;
    }

    public ResultSet getCliDatos(String codcli) throws SQLException {
        consulta = "SELECT ELECTR_CLIENTES.*,ELECTR_CIUDAD.* "
                + "FROM ELECTR_CLIENTES \n" +
                "INNER JOIN ELECTR_CIUDAD ON ELECTR_CIUDAD.codciu = ELECTR_CLIENTES.CIUDAD \n" +
                "WHERE ELECTR_CLIENTES.CODCLI = '" + codcli + "' ";

        rs = stm.executeQuery(consulta);


        return rs;
    }

    public ResultSet getCliDatosFactura(String codcli) throws SQLException {
        consulta = "SELECT ELECTR_CLIENTES.* "
                + "FROM ELECTR_CLIENTES \n" +
                "WHERE ELECTR_CLIENTES.CODCLI = '" + codcli + "' ";

        rs = stm.executeQuery(consulta);


        return rs;
    }

    public ResultSet getSaldoVentasOk(String codcli) throws SQLException {
        consulta = "SELECT SUM(electr_resumen.SALDO) as SALDO_USD,SUM(electr_resumen.SALDOBS) as SALDO_BS FROM electr_resumen WHERE electr_resumen.CODCLI = '" + codcli + "'AND electr_resumen.ESTADO = 0";

        rs = stm.executeQuery(consulta);


        return rs;
    }

    public ResultSet getSaldoDevoluciones(String codcli) throws SQLException {
        consulta = "SELECT SUM(electr_resumen_devolucion.SALDO) as SALDOUSD,SUM(electr_resumen_devolucion.SALDOBS) as SALDOBS FROM electr_resumen_devolucion WHERE electr_resumen_devolucion.CODCLI = '" + codcli + "'AND electr_resumen_devolucion.PROCESO = 1 ";
//System.out.println(consulta);
        rs = stm.executeQuery(consulta);


        return rs;
    }

    public ResultSet getSaldoNotaIngreso(String codcli) throws SQLException {
        consulta = "SELECT SUM(electr_nota_ingreso.SALDO),SUM(electr_nota_ingreso.SALDOBS)\n" +
                "FROM electr_nota_ingreso \n" +
                "WHERE electr_nota_ingreso.CODCLI = '" + codcli + "'  AND electr_nota_ingreso.ESTADO = 0  ";

        rs = stm.executeQuery(consulta);


        return rs;
    }

    public ResultSet getSaldoNotaEgreso(String codcli) throws SQLException {
        consulta = "SELECT SUM(electr_nota_egreso.SALDO), SUM(electr_nota_egreso.SALDOBS) \n" +
                "FROM electr_nota_egreso \n" +
                "WHERE electr_nota_egreso.CODCLI = '" + codcli + "'  AND electr_nota_egreso.ESTADO = 0 ";

        rs = stm.executeQuery(consulta);


        return rs;
    }

    public ResultSet getClienteDatosVenta(String codvent) throws SQLException {
        consulta = "SELECT electr_resumen.TOTAL_V,electr_resumen.TOTAL_V_BS,electr_resumen.IVA_VALOR,electr_resumen.IVA_VALOR_bs,electr_resumen.DESC_VALOR,electr_resumen.DESC_VALOR_BS,electr_resumen.IVA,electr_resumen.DESCUENTO,electr_resumen.FEC_VENTA,electr_resumen.CODCLI,SUM(electr_cliart.TOTAL),SUM(electr_cliart.TOTAL_BS)\n" +
                "from electr_resumen\n" +
                "INNER JOIN electr_cliart ON electr_cliart.codvent =  '" + codvent + "'\n" +
                "where electr_resumen.CODVENT =  '" + codvent + "' ";

        rs = stm.executeQuery(consulta);


        return rs;
    }

    public ResultSet getClienteDatosSR(String codcli) throws SQLException {
        consulta = "SELECT electr_usuario.nombre,electr_usuario.apellido,electr_clientes.*,electr_ciudad.* FROM electr_clientes \n" +
                "INNER JOIN electr_ciudad ON electr_ciudad.codciu = electr_clientes.CIUDAD \n" +
                "INNER JOIN electr_usuario ON electr_usuario.cod_usuario = electr_clientes.RESP \n" +
                "WHERE electr_clientes.CODCLI = '" + codcli + "' ";
        //  System.out.println(consulta);
        rs = stm.executeQuery(consulta);


        return rs;
    }

    public ResultSet getClienteNit() throws SQLException {
        consulta = "SELECT NOMCLI,CODCLI,NIT FROM electr_clientes ";

        rs = stm.executeQuery(consulta);


        return rs;
    }

    //2018
    public Stream<ResultSet> getRange() throws SQLException {
        consulta = "SELECT * FROM electr_rango";//" WHERE (costo_ini <= '"+precio+"' AND costo_fin >= '"+precio+"') AND codlin = '"+linea+"' LIMIT 1";

        return this.stream(stm.executeQuery(consulta));
    }

    //2018
    public int getNextPurchase() throws SQLException {
        consulta = "SELECT MAX(electr_sec_ventas.CODVENT) FROM electr_sec_ventas";
        rs = stm.executeQuery(consulta);
        while (rs.next()) {
            return rs.getInt("MAX(electr_sec_ventas.CODVENT)") + 1;
        }
        return 0;
    }


    //2018
    public Stream<ResultSet> getCodart() throws SQLException {
        consulta = "SELECT * FROM electr_codart";

        return this.stream(stm.executeQuery(consulta));

    }

    public ResultSet getExisCompra() throws SQLException {
        consulta = "SELECT CODART,CANTI01,PCOSTO,PMAYOR,PPUB FROM electr_exis";

        rs = stm.executeQuery(consulta);


        return rs;
    }

    public ResultSet getRangoPub(Double precio, int linea) throws SQLException {
        consulta = "SELECT * FROM electr_rango_pub WHERE (costo_inic > '" + precio + "' OR costo_fin > '" + precio + "') AND codlin = '" + linea + "' LIMIT 1";

        rs = stm.executeQuery(consulta);


        return rs;
    }

    public ResultSet getLineas() throws SQLException {

        consulta = "SELECT * FROM electr_linea ORDER BY DES";
        rs = stm.executeQuery(consulta);

        return rs;
    }

    public ResultSet getFavoritos(String f1, String f2, String f3, String f4, String f5) throws SQLException {

        consulta = "SELECT * FROM electr_favorito WHERE cod_favorito = '" + f1 + "' OR cod_favorito = '" + f2 + "' OR cod_favorito = '" + f3 + "' OR cod_favorito = '" + f4 + "'OR cod_favorito = '" + f5 + "' ORDER BY cod_favorito";
        rs = stm.executeQuery(consulta);

        return rs;
    }

    //2018
    public Stream<ResultSet> getProviders() throws SQLException {
        consulta = "SELECT * FROM electr_proveedor ORDER BY NOMBRE";
        return this.stream(stm.executeQuery(consulta));

    }

    public ResultSet getVentas(String min, String max) throws SQLException {

        consulta = "SELECT * FROM electr_cliart WHERE CODVENT BETWEEN '" + min + "' AND '" + max + "' ORDER BY CODVENT";
        //      System.out.println("mmm "+consulta);
        rs = stm.executeQuery(consulta);

        return rs;
    }

    public ResultSet getLinea(String cod) throws SQLException { //OPTIMIZADO

        consulta = "SELECT CODLIN,DES FROM electr_linea WHERE CODLIN = '" + cod + "'";
        rs = stm.executeQuery(consulta);

        return rs;
    }

    public ResultSet getMarca(int cod) throws SQLException { //OPTIMIZADO

        consulta = "SELECT * FROM electr_marca WHERE COD_MARCA = '" + cod + "'";
        rs = stm.executeQuery(consulta);

        return rs;
    }

    public ResultSet getVenta(String cod) throws SQLException {

        consulta = "SELECT * FROM electr_RESUMEN WHERE CODVENT = '" + cod + "'";
        rs = stm.executeQuery(consulta);

        return rs;
    }

    public ResultSet getVentaFactura(String cod) throws SQLException {

        consulta = "SELECT electr_resumen.TOTAL_V_BS,electr_resumen.IVA,electr_resumen.IVA_VALOR_BS, electr_clientes.NOM_NIT,electr_clientes.NIT,COUNT(electr_cliart.CODART)AS items\n" +
                "FROM electr_RESUMEN \n" +
                "INNER JOIN electr_clientes ON electr_clientes.CODCLI = electr_resumen.CODCLI\n" +
                "INNER JOIN electr_cliart ON electr_cliart.CODVENT =  '" + cod + "'\n" +
                "WHERE electr_resumen.CODVENT =  '" + cod + "' ";
        rs = stm.executeQuery(consulta);

        return rs;
    }

    public ResultSet getVenta_mod(String cod) throws SQLException {

        consulta = "SELECT RESP FROM electr_RESUMEN WHERE CODVENT = '" + cod + "'";
        rs = stm.executeQuery(consulta);

        return rs;
    }

    public ResultSet getItemPedido(String cod) throws SQLException { //OPTIMIZADO

        consulta = "SELECT sum(electr_comtemp.CANTIDAD) FROM electr_comtemp WHERE CODART = '" + cod + "'";
        rs = stm.executeQuery(consulta);

        return rs;
    }

    public ResultSet getItem(String cod) throws SQLException { //OPTIMIZADO

        consulta = "SELECT * FROM electr_exis WHERE CODART = '" + cod + "'";
        rs = stm.executeQuery(consulta);

        return rs;
    }

    public ResultSet getDatoCliente(String cod) throws SQLException {

        consulta = "SELECT * FROM electr_CLIENTES WHERE CODCLI = '" + cod + "'";
        rs = stm.executeQuery(consulta);

        return rs;
    }

    public ResultSet getClientes() throws SQLException {

        consulta = "SELECT * FROM electr_CLIENTES ORDER BY NOMCLI";
        rs = stm.executeQuery(consulta);

        return rs;

    }

    //2018
    public Stream<ResultSet> getCustomers() throws SQLException {

        consulta = "SELECT * \n" +
                "FROM electr_clientes \n" +
                "INNER JOIN electr_ciudad ON electr_ciudad.codciu = electr_clientes.CIUDAD\n" +
                "ORDER BY NOMCLI";
        return stream(stm.executeQuery(consulta));


    }

    public ResultSet getDeuda(String codcli) throws SQLException {

        consulta = "SELECT SUM( electr_resumen.SALDO),SUM( electr_resumen.SALDOBS) FROM electr_resumen WHERE electr_resumen.`CODCLI` = '" + codcli + "' AND electr_resumen.`ESTADO` = '0' ORDER BY electr_resumen.CODVENT ASC";

        rs = stm.executeQuery(consulta);
        return rs;

    }

    public ResultSet getClientesJ() throws SQLException {

        consulta = "SELECT NOMCLI,CODCLI FROM electr_CLIENTES ORDER BY NOMCLI";
        rs = stm.executeQuery(consulta);

        return rs;

    }

    public ResultSet getProveedor() throws SQLException {

        consulta = "SELECT * FROM electr_PROVEEDOR ORDER BY NOM";
        rs = stm.executeQuery(consulta);

        return rs;

    }

    public ResultSet getProveedorIndice(int codprov) throws SQLException { //OPTIMIZADO

        consulta = "SELECT * FROM electr_PROVEEDOR WHERE CODPROV = '" + codprov + "'";
        rs = stm.executeQuery(consulta);

        return rs;

    }

    public ResultSet getCatalogo() throws SQLException {

        consulta = "SELECT * FROM electr_CATALOGO ORDER BY NOM_CATALOGO";
        rs = stm.executeQuery(consulta);

        return rs;

    }

    public ResultSet getCatalogoEsp(int codcat) throws SQLException {

        consulta = "SELECT * FROM electr_CATALOGO WHERE ID_CATALOGO = '" + codcat + "'";
        rs = stm.executeQuery(consulta);

        return rs;

    }

    public ResultSet getLinea() throws SQLException {

        consulta = "SELECT * FROM electr_linea ORDER BY DES";
        rs = stm.executeQuery(consulta);
        //    System.out.println("llegue aca 123");
        return rs;

    }

    public ResultSet getDatoProveedor(String cod) throws SQLException {

        consulta = "SELECT * FROM electr_PROVEEDOR WHERE CODPROV = '" + cod + "'";
        rs = stm.executeQuery(consulta);

        return rs;

    }

    public ResultSet getPagosT(String cod) throws SQLException {

        consulta = "SELECT V_PAGO FROM electr_FACTURAS WHERE CODCLI = '" + cod + "' ";
        rs = stm.executeQuery(consulta);

        return rs;

    }

    public ResultSet getPagosTop(String cod) throws SQLException {

        consulta = "SELECT FECPAGO,V_PAGO FROM electr_pagos WHERE CODCLI = '" + cod + "' ORDER BY FECPAGO DESC  LIMIT 0, 3 ";  //
        rs = stm.executeQuery(consulta);

        return rs;

    }

    public ResultSet getPagosTop10(String cod) throws SQLException {

        consulta = "SELECT FECPAGO,V_PAGO FROM electr_pagos WHERE CODCLI = '" + cod + "' ORDER BY FECPAGO DESC  LIMIT 0, 10 ";  //
        rs = stm.executeQuery(consulta);

        return rs;

    }

    public ResultSet getVentasTop(String cod) throws SQLException {

        consulta = "SELECT FEC_VENTA,CODVENT,IVA FROM electr_resumen WHERE CODCLI = '" + cod + "' ORDER BY fec_venta DESC  LIMIT 0, 4 ";  //
        rs = stm.executeQuery(consulta);

        return rs;

    }

    public ResultSet getDeudaT(String cod) throws SQLException {

        consulta = "SELECT TOTAL_V FROM electr_RESUMEN WHERE CODCLI = '" + cod + "' ";
        rs = stm.executeQuery(consulta);

        return rs;

    }

    public ResultSet getTopVentas() throws SQLException {

        consulta = "SELECT * FROM(SELECT * FROM electr_RESUMEN ORDER BY FEC_VENTA DESC) AS T LIMIT 25";
        rs = stm.executeQuery(consulta);

        return rs;

    }

    public ResultSet getUltimoPago(String cod) throws SQLException {

        consulta = "SELECT * FROM electr_FACTURAS WHERE CODCLI = '" + cod + "' ORDER BY FECPAGO DESC LIMIT 1";
        rs = stm.executeQuery(consulta);
        return rs;
    }


    public ResultSet getResponsable(int cod) throws SQLException { //OPTIMIZADO

        consulta = "SELECT  *\n" +
                "FROM electr_usuario\n" +
                "\n" +
                "INNER JOIN electr_cargo ON electr_cargo.cod_cargo = electr_usuario.cargo\n" +
                "\n" +
                "WHERE electr_usuario.cod_usuario = '" + cod + "'";
        //   System.out.println(consulta);
        rs = stm.executeQuery(consulta);
        return rs;
    }

    public ResultSet getNivel(int cod) throws SQLException {

        consulta = "SELECT * FROM electr_nivel WHERE COD_NIVEL = '" + cod + "'";
        rs = stm.executeQuery(consulta);
        return rs;
    }

    public void llenar_sec_compras(int codcom) throws SQLException {

        consulta = "INSERT INTO electr_SEC_COMPRAS (CODCOM) VALUES ('" + codcom + "')";
        //INSERT INTO `electr_stock` (`cod`, `resp`, `codart`, `cantidad_ant`, `cantidad`, `fecha`) VALUES (NULL, '1', 'cedar', '4', '5', CURRENT_TIMESTAMP);
        stm.execute(consulta);

    }

    public void insertarStock(String resp, String codart, String canti_ant, String canti, Timestamp fecha, String canti_string, String desc) throws SQLException {

        consulta = "INSERT INTO `electr_stock` (`cod`, `resp`, `codart`, `cantidad_ant`, `cantidad`, `fecha`, `obs`) VALUES (NULL, '" + resp + "', '" + codart + "', '" + canti_ant + "', '" + canti + "', '" + fecha + "', '" + desc + "');";
        System.out.println(consulta);
        stm.execute(consulta);


        consulta = "UPDATE `electr_exis` SET `CANTI01` =  '" + canti + "' WHERE `electr_exis`.`codart` = '" + codart + "' AND `electr_exis`.`" + canti_string + "` = '" + canti_ant + "'  ;";
        stm.execute(consulta);
    }

    public void insertarReporte(String fecha, String tipo, String documento, Double v_pago, Double v_venta, Double saldo_ini, Double saldo_fin, Double v_pago_bs, Double v_venta_bs, Double saldo_ini_bs, Double saldo_fin_bs, String codcli, String cod, String orden, String iva, String descuento, String estado) throws SQLException {


        if (iva.equalsIgnoreCase("true"))
            iva = "1";
        else
            iva = "0";

        if (descuento.equalsIgnoreCase("true"))
            descuento = "1";
        else
            descuento = "0";

        if (estado.equalsIgnoreCase("true"))
            estado = "1";
        else
            estado = "0";

        consulta = "INSERT INTO `electr_reporte` (`fecha`, `tipo`, `documento`, `v_pago`, `v_venta`, `saldo_ini`, `saldo_fin`, `v_pago_bs`, `v_venta_bs`, `saldo_ini_bs`, `saldo_fin_bs`, `CODCLI`, `COD`, `ORDEN`, `IVA`, `DESCUENTO`, `ESTADO`) VALUES ( '" + fecha + "', '" + tipo + "', '" + documento + "', '" + v_pago + "', '" + v_venta + "', '" + saldo_ini + "', '" + saldo_fin + "', '" + v_pago_bs + "', '" + v_venta_bs + "', '" + saldo_ini_bs + "', '" + saldo_fin_bs + "', '" + codcli + "', '" + cod + "', '" + orden + "', '" + iva + "', '" + descuento + "', '" + estado + "')";
        //  System.out.println(consulta);

        stm.execute(consulta);

    }

    public void insertarClienteServer(int codcli, String nomcli, String direccion, String celular, String telefono) throws SQLException {

        consulta = "INSERT INTO `electr_clientes` (`CODCLI`, `NOMCLI`, `DIRECCION`, `CIUDAD`, `CELULAR`, `TELEFONO`, `DEBE`, `HABER`, `EMAIL`, `NOM_NIT`, `FULTIMO`, `RESP`, `NOMEMPRESA`, `cod_sucursal`, `NIT`, `SENDING`) VALUES ('" + codcli + "', '" + nomcli + "', '" + direccion + "', '1', '" + celular + "', '" + telefono + "', '0.00', '0.00', '', '', '0000-00-00 00:00:00', '2', '', '1', '', '');";
        stm.execute(consulta);
//INSERT INTO `electr_fraude` (`cod`, `codart`, `comprado`, `vendido`, `existencia`) VALUES (NULL, 'aasdfasdf', '1', '2', '3');
    }

    public void insertarStock(String codart, String comprado, String vendido, String stock) throws SQLException {

        consulta = "INSERT INTO `electr_fraude` (`codart`, `comprado`, `vendido`, `existencia`) VALUES ('" + codart + "', '" + comprado + "', '" + vendido + "', '" + stock + "');";
        //   System.out.println(consulta);
        stm.execute(consulta);
//INSERT INTO `electr_fraude` (`cod`, `codart`, `comprado`, `vendido`, `existencia`) VALUES (NULL, 'aasdfasdf', '1', '2', '3');
    }

    public void insertarLineaServer(int codlin, String des) throws SQLException {
        consulta = "INSERT INTO `electr_linea` (`CODLIN`, `DES`, `rep`) VALUES ('" + codlin + "', '" + des + "', '0')";
        stm.execute(consulta);
    }

    public void insertarExisProvServer(String codcom, String codprov, String fec, String total, String total_usd, String literal, String literal_usd) throws SQLException {
        consulta = "INSERT INTO `electr_compras` (`CODCOM`, `FECHA`, `TC`, `CODPROV`, `TOTAL`, `LITERAL`, `RESP`, `TCEXT`, `PROCESO`, `cod_sucursal`, `TOTAL_USD`, `LITERAL_USD`, `ANTIGUO`) VALUES ('" + codcom + "', ''" + fec + "'', '6.96', '" + codprov + "', '" + (total) + "', '" + literal + "', '1', '1.39', '1', '1', '" + total_usd + "', '" + literal_usd + "', '1');";
        stm.execute(consulta);
    }

    public void insertarProveedorServer(Object codlin, String des) throws SQLException {
        consulta = "INSERT INTO `electr_proveedor` (`CODPROV`, `NICK`) VALUES ('" + codlin + "', '" + des + "')";
        // INSERT INTO `electr_cliart` (`cod`, `CODCLI`, `CODART`, `CANTIDAD`, `PRECIO`, `TOTAL`, `CODVENT`, `NRO`, `DES`, `cod_sucursal`) VALUES (NULL, '1', '2', '3', '0', '0', '5', '6', '7', '1');
        stm.execute(consulta);
        //   System.out.println(consulta);
    }

    public void insertarVentasServer(String codvent, String codcli, String codart, String cantidad, String total, String des, String precio, String precio_bs, String total_bs) throws SQLException {
        consulta = "INSERT INTO `electr_cliart` ( `CODCLI`, `CODART`, `CANTIDAD`, `PRECIO`, `TOTAL`, `CODVENT`,  `DES`, `cod_sucursal`, `precio_bs`, `total_bs`) VALUES ( '" + codcli + "', '" + codart + "', '" + cantidad + "', '" + precio + "', '" + total + "', '" + codvent + "',  '" + des + "', '1', '" + precio_bs + "', '" + total_bs + "')";
        // UPDATE `electr_cliart` SET `CANTIDAD` = '501', `PRECIO` = '0.11', `TOTAL` = '51', `NRO` = '101', `DES` = '11' WHERE `electr_cliart`.`cod` = 201474;
        stm.execute(consulta);


        //System.out.println(consulta);
    }

    public void modificarVentasServer(String precio, String total, String des, String codvent, String codcli, String codart, String precio_bs, String total_bs) throws SQLException {
        consulta = "UPDATE `electr_cliart` SET `PRECIO` = '" + precio + "', `TOTAL` = '" + total + "', `DES` = '" + des + "' , `PRECIO_BS` = '" + precio_bs + "', `TOTAL_BS` = '" + total_bs + "' WHERE `electr_cliart`.`codvent` = '" + codvent + "' AND  `electr_cliart`.`codcli` = '" + codcli + "' AND  `electr_cliart`.`codart` = '" + codart + "' ";

        stm.execute(consulta);
        //    System.out.println(consulta);
    }

    public void modificarVentasServerIva(String des, String codvent, String codcli, String codart) throws SQLException {
        consulta = "UPDATE `electr_cliart` SET `DES` = '" + des + "' ,  WHERE `electr_cliart`.`codvent` = '" + codvent + "' AND  `electr_cliart`.`codcli` = '" + codcli + "' AND  `electr_cliart`.`codart` = '" + codart + "' ";
        //   System.out.println(consulta);
        stm.execute(consulta);

    }

    public void modificarDebeCliente(String valor, String codcli) throws SQLException {
        consulta = "UPDATE `electr_clientes` SET `DEBE` = '" + valor + "' WHERE `electr_clientes`.`CODCLI` = '" + codcli + "' ";
        stm.execute(consulta);
        //    System.out.println(consulta);
    }

    public void borrarVentasServer(String codvent, String codart, String cantidad, String codcli) throws SQLException {
        consulta = "DELETE FROM electr_cliart WHERE electr_cliart.CODVENT ='" + codvent + "'  ";
        //  System.out.println(consulta);
        stm.execute(consulta);

        int aux = Integer.valueOf(codvent.substring(1, codvent.length()));
        consulta = "DELETE FROM electr_sec_ventas WHERE electr_sec_ventas.CODVENT ='" + aux + "'  ";
        //   System.out.println(consulta);
        stm.execute(consulta);
        //   System.out.println(consulta);
    }

    public void borrarLineasServer(String codlin) throws SQLException {
        consulta = "DELETE FROM electr_linea WHERE electr_linea.CODLIN ='" + codlin + "'  ";

        stm.execute(consulta);
        //     System.out.println(consulta);
    }

    public void borrarPago(String codlin) throws SQLException {
        consulta = "DELETE FROM electr_pagos WHERE electr_pagos.COD ='" + codlin + "'  ";

        stm.execute(consulta);
        //    System.out.println(consulta);
    }

    public void borrarProveedorServer(String codprov) throws SQLException {
        consulta = "DELETE FROM electr_proveedor WHERE electr_proveedor.CODPROV ='" + codprov + "'  ";

        stm.execute(consulta);
        //     System.out.println(consulta);
    }

    public void borrarPagosServer(String codpago, String codcli) throws SQLException {
        consulta = "DELETE FROM electr_pagos WHERE electr_pagos.CODPAGO ='" + codpago + "' AND electr_pagos.CODCLI ='" + codcli + "'  ";

        stm.execute(consulta);
        //    System.out.println(consulta);
    }

    public void borrarExisServer(String codart, String cantidad) throws SQLException {
        consulta = "DELETE FROM electr_exis WHERE electr_exis.CODART ='" + codart + "' AND electr_exis.CANTI01 ='" + cantidad + "'  ";

        stm.execute(consulta);
        //    System.out.println(consulta);
    }

    public void borrarClientesServer(String codcli) throws SQLException {

        consulta = "DELETE FROM electr_clientes WHERE electr_clientes.CODCLI ='" + codcli + "'  ";

        stm.execute(consulta);
        //   System.out.println(consulta);
    }

    public void borrarResumenServer(String codvent, String codcli) throws SQLException {
        String aux = codvent.substring(1);
        int aux2 = Integer.valueOf(aux);

        consulta = "DELETE FROM electr_resumen WHERE electr_resumen.CODVENT ='" + codvent + "'  ";
        stm.execute(consulta);
        //    System.out.println(consulta);
        consulta = "DELETE FROM electr_sec_ventas WHERE electr_sec_ventas.CODVENT ='" + aux2 + "' ";
        stm.execute(consulta);
        //     System.out.println(consulta);

    }

    public ResultSet getPersonal() throws SQLException {

        consulta = "SELECT * FROM electr_USUARIO ORDER BY NOMBRE";
        rs = stm.executeQuery(consulta);
        return rs;
    }

    public ResultSet getCiudades() throws SQLException {

        consulta = "SELECT * FROM electr_CIUDAD ORDER BY NOMBRE";
        rs = stm.executeQuery(consulta);
        return rs;


    }

    public ResultSet getCodcli() throws SQLException {

        consulta = "SELECT MAX(CODCLI) FROM electr_CLIENTES";
        rs = stm.executeQuery(consulta);
        return rs;
    }

    public ResultSet getSucursales() throws SQLException {

        consulta = "SELECT * FROM electr_SUCURSAL";
        rs = stm.executeQuery(consulta);
        return rs;
    }

    public ResultSet getSucursalesP() throws SQLException { //OPTIMIZADO

        consulta = "SELECT NOM_SUCURSAL FROM electr_SUCURSAL";
        rs = stm.executeQuery(consulta);
        return rs;
    }


    public ResultSet getCodigos(int cod) throws SQLException {

        consulta = "SELECT * FROM electr_CODIGO WHERE ID_SUCURSAL = '" + cod + "'";
        rs = stm.executeQuery(consulta);
        return rs;
    }

    //2018
    public Stream<ResultSet> getLastSales(int shopID) throws SQLException {
        consulta = "SELECT MAX(CODVENT),electr_codigo.ventas from electr_sec_ventas INNER JOIN electr_codigo WHERE electr_codigo.id_sucursal = '" + shopID + "'";
        return stream(stm.executeQuery(consulta.toLowerCase()));
    }

    //2018
    public Stream<ResultSet> getLastPurchase(int shopID) throws SQLException {
        consulta = "SELECT MAX(CODCOM),electr_codigo.compras from electr_sec_compras INNER JOIN electr_codigo WHERE electr_codigo.id_sucursal = '" + shopID + "'";
        return stream(stm.executeQuery(consulta.toLowerCase()));
    }

    //2018
    public Stream<ResultSet> getLastReturn(int shopID) throws SQLException {
        consulta = "SELECT MAX(CODDEV),electr_codigo.devolucion from electr_sec_devoluciones INNER JOIN electr_codigo WHERE electr_codigo.id_sucursal = '" + shopID + "'";
        return stream(stm.executeQuery(consulta.toLowerCase()));
    }

    public ResultSet getCodigosP() throws SQLException { // OPTIMIZADO

        consulta = "SELECT ID_SUCURSAL,VENTAS,PAGOS,COMPRAS FROM electr_CODIGO";
        rs = stm.executeQuery(consulta);
        return rs;
    }

    public ResultSet getDatosIni(String mac) throws SQLException {

        consulta = "SELECT * FROM ELECTR_INI \n" +
                "INNER JOIN ELECTR_LICENCIA ON ELECTR_LICENCIA.serial ='" + mac + "'\n" +
                "INNER JOIN ELECTR_SUCURSAL ON ELECTR_SUCURSAL.cod_sucursal = ELECTR_INI.cod_sucursal ";
        //System.out.println("LOS DATOS" +consulta);
        rs = stm.executeQuery(consulta);
        return rs;
    }

    public ResultSet getVentaProceso(String codcli, String codvent, String canti) throws SQLException {

        consulta = "SELECT  ELECTR_CLIART.*,ELECTR_EXIS." + canti + "\n" +
                "FROM ELECTR_CLIART \n" +
                "\n" +
                "INNER JOIN ELECTR_EXIS ON ELECTR_EXIS.CODART = ELECTR_CLIART.CODART \n" +
                "\n" +
                "WHERE CODCLI = '" + codcli + "' AND CODVENT = '" + codvent + "'\n" +
                "ORDER BY ELECTR_CLIART.CODART";
        //   System.out.println(consulta);

        rs = stm.executeQuery(consulta);
        return rs;
    }

    public ResultSet getVentaProducto(String codvent) throws SQLException {

        consulta = "SELECT * FROM ELECTR_CLIART WHERE ELECTR_CLIART.CODVENT = '" + codvent + "' ";
        rs = stm.executeQuery(consulta);
        return rs;
    }

    public ResultSet getVentaProcesoImprimir(String codcli, String codvent) throws SQLException {

        consulta = "SELECT ELECTR_CLIART.*,ELECTR_RESUMEN.*\n" +
                "FROM ELECTR_CLIART \n" +
                "INNER JOIN ELECTR_RESUMEN ON ELECTR_RESUMEN.CODVENT = '" + codvent + "'\n" +
                "WHERE ELECTR_CLIART.CODCLI = '" + codcli + "' AND ELECTR_CLIART.CODVENT = '" + codvent + "' ORDER BY ELECTR_CLIART.CODART";
//System.out.println(consulta);
        rs = stm.executeQuery(consulta);
        return rs;

    }

    public ResultSet getVentaProcesoT(String codcli, String codvent) throws SQLException {//optimizado

        consulta = "SELECT CODART,DES,CANTIDAD,PRECIO,TOTAL FROM electr_CLIART WHERE CODCLI = '" + codcli + "' AND CODVENT = '" + codvent + "' ORDER BY CODART";
        rs = stm.executeQuery(consulta);
        return rs;
    }

    public ResultSet getPagoProceso(String codpago) throws SQLException {

        consulta = "SELECT * FROM electr_pagos WHERE CODPAGO = '" + codpago + "' ORDER BY FECPAGO ASC";
        rs = stm.executeQuery(consulta);
        return rs;
    }

    public ResultSet getCompraProceso(String codprov, String codcom) throws SQLException {

        consulta = "SELECT * FROM electr_COMPART WHERE CODPROV = '" + codprov + "' AND CODCOM = '" + codcom + "' ORDER BY CODART";
        rs = stm.executeQuery(consulta);
        return rs;
    }


    public void venderItem(String codcli, String codart, int cantidad, Double precio, Double total, String cod, String des, int cod_sucursal, String canti_sucursal, Double precio_bs, Double total_bs, int codprov) throws SQLException {


        consulta = "UPDATE ELECTR_EXIS SET " + canti_sucursal + " =" + canti_sucursal + " - '" + cantidad + "' WHERE CODART ='" + codart + "' ";
        stm.execute(consulta);
        consulta = "INSERT INTO ELECTR_CLIART (CODCLI, CODART, CANTIDAD, PRECIO, TOTAL, CODVENT, DES,COD_SUCURSAL,PRECIO_BS,TOTAL_BS,CODPROV) VALUES ('" + codcli + "','" + codart + "','" + cantidad + "','" + precio + "','" + total + "','" + cod + "','" + des + "','" + cod_sucursal + "','" + precio_bs + "','" + total_bs + "','" + codprov + "'  )";
        stm.execute(consulta);
        System.out.println(" salio" + consulta);

        // //out.println("salio 1");

    }

    //2018
    public void insertPurchaseItem(String codprov, String codor, String codart, String des, String lin, String precio_ant, String precio, String precio_usd, String ganancia, String preciof, String preciof_ant, String total, String cantidad, String cod_sucursal, String codcom) throws SQLException {

        consulta = "INSERT INTO `electr_compart` (`CODPROV`, `CODOR`, `CODART`, `DES`, `LIN`, `PRECIO_ANT`, `PRECIO`, `PRECIOUSD`, `GANANCIA`, `PRECIOF`, `PRECIOF_ANT`, `TOTAL`, `CANTIDAD`, `cod_sucursal`, `CODCOM`, `COD`) VALUES ('" + (int) Double.parseDouble(codprov) + "', '" + codor + "', '" + codart + "', '" + des + "', '" + (int) Double.parseDouble(lin) + "', '" + precio_ant + "', '" + precio + "', '" + precio_usd + "', '" + ganancia + "', '" + preciof + "', '" + preciof_ant + "', '" + total + "', '" + (int) Double.parseDouble(cantidad) + "', '" + (int) Double.parseDouble(cod_sucursal) + "', '" + codcom + "',NULL ); ";
        System.out.println(consulta);
        //  stm.execute(consulta);

    }

    //2018
    public void insertPurchaseResume(String codProvider, String codPruchase, String datePurchase, String tc, String total, String literal, String userID, String tcExt, String process, String shopID) {
        consulta = "INSERT INTO `electr_compras` (`CODPROV`, `CODCOM`, `FECHA`, `TC`, `TOTAL`, `LITERAL`, `RESP`, `TCEXT`, `PROCESO`, `cod_sucursal`, `cod`) VALUES ('" + codProvider + "', '" + codPruchase + "', '" + datePurchase + "',  '" + tc + "',  '" + total + "', '" + literal + "', '" + userID + "', '" + tcExt + "', '" + process + "', '" + shopID + "', NULL);";
        System.out.println(consulta);
        //  stm.execute(consulta);
    }

    //2018
    public void sellProduct(Product product) throws SQLException {
        consulta = "INSERT INTO `electr_cliart` (`cod`, `CODCLI`, `CODART`, `CANTIDAD`, `PRECIO`, `TOTAL`, `CODVENT`, `DES`, `cod_sucursal`, `PRECIO_BS`, `TOTAL_BS`, `CODPROV`) VALUES (NULL, '" + product.getCustomer() + "', '" + product.getCodProduct() + "', '" + product.getQuantityToSell() + "', '" + product.getPriceUSD() + "', '" + product.getPreTotalUSD() + "', '" + product.getCodPurchase() + "', '" + product.getDescription() + "', '" + product.getShopID() + "', '" + product.getPriceBS() + "', '" + product.getPreTotalBS() + "', '" + product.getCodProvider() + "');";
        stm.execute(consulta);

        consulta = "UPDATE ELECTR_EXIS SET " + product.getQuantityParam() + " =" + product.getQuantityParam() + " - '" + product.getQuantityToSell() + "' WHERE CODART ='" + product.getCodProduct() + "' ";
        stm.execute(consulta);

    }

    //2018
    public void removeProduct(Product product) throws SQLException {
        consulta = "DELETE FROM `electr_cliart` WHERE CODART = '" + product.getCodProduct() + "' AND CODCLI = '" + product.getCustomer() + "' AND CANTIDAD = '" + product.getQuantityToSell() + "' ";
        stm.execute(consulta);

        consulta = "UPDATE ELECTR_EXIS SET " + product.getQuantityParam() + " =" + product.getQuantityParam() + " + '" + product.getQuantityToSell() + "' WHERE CODART ='" + product.getCodProduct() + "' ";
        stm.execute(consulta);

    }

    //2018
    public void insertPurchaseResume(Purchase purchase) throws SQLException {
        consulta = "INSERT INTO `electr_resumen` (`cod`, `CODCLI`, `CODVENT`, `TOTAL_V`, `LITERAL`, `FEC_VENTA`, `IVA`, `IVA_VALOR`, `TC`, `RESP`, `DESC_ANTERIOR`, `DESCUENTO`, `DESC_VALOR`, `ESTADO`, `PAGO`, `SALDO`, `LITERAL_BS`, `PROCESO`,`TOTAL_V_BS`, `DESC_ANTERIOR_BS`, `IVA_VALOR_BS`, `DESC_VALOR_BS`, `PAGOBS`, `SALDOBS`, `SUCURSAL`) VALUES (NULL, '" + purchase.getCustomerID() + "'\n" +
                ", '" + purchase.getPurchaseCod() + "','" + purchase.getTotalUSD() + "', '" + purchase.getLiteralUSD() + "', '" + purchase.getDate() + "', '" + purchase.getIvaPercent() + "', '" + purchase.getIvaAmountUSD() + "', '" + purchase.getExchange() + "', '" + purchase.getUserID() + "', '" + purchase.getDiscountBeforeUSD() + "', '" + purchase.getDiscountPercent() + "', '" + purchase.getDiscountAmountUSD() + "', '" + purchase.getState() + "', '" + purchase.getCreditUSD() + "', '" + purchase.getDebitUSD() + "', '" + purchase.getLiteralBS() + "',  '" + purchase.getProgress() + "', '" + purchase.getTotalBS() + "', '" + purchase.getDiscountBeforeBS() + "', '" + purchase.getIvaAmountBS() + "', '" + purchase.getDiscountAmountBS() + "' , '" + purchase.getCreditBS() + "', '" + purchase.getDebitBS() + "', '" + purchase.getShopID() + "');";
        System.out.println(consulta);
        stm.execute(consulta);
    }


    public void insertarReporte(String cod, String cod_vent, String fecha, String codart, int cantidad, Double precio, Double total, Double precio_bs, Double total_bs, String des, String codprov, String codcli) throws SQLException {

        //   //out.println(des);
        consulta = "INSERT INTO `electr_reporte_item` (`cod_reporte`, `cod`, `cod_vent`, `fec_venta`, `codart`, `cantidad`, `precio`, `total`, `precio_bs`, `total_bs`, `des`, `codprov`, `cod_cli`) VALUES (NULL, '" + cod + "', '" + cod_vent + "', '" + fecha + "', '" + codart + "', '" + cantidad + "', '" + precio + "', '" + total + "', '" + precio_bs + "', '" + total_bs + "', '" + des + "', '" + codprov + "', '" + codcli + "');";
        //   //out.println(consulta);

        stm.execute(consulta);


    }

    public void insertarLicencia(String serial, Timestamp fecha, String detalle, String version, String desc) throws SQLException {

        consulta = "INSERT INTO `electr_licencia` ( `serial`, `fini`,  `detalle`, `version`, `version_desc`) VALUES ( '" + serial + "', '" + fecha + "', '" + detalle + "','" + version + "','" + desc + "');";

        stm.execute(consulta);

    }

    public void pagoCliente(String codpago, String codcli, int resp, Timestamp fecpago, Double v_deuda, Double v_pago, Double tc, Timestamp fecmov, int cod_sucursal, String nro_recibo, Double v_pago_bs, Double v_deuda_bs) throws SQLException {

        // //out.println(des);
        consulta = "INSERT INTO electr_FACTURAS (CODPAGO,CODCLI,RESP,FECPAGO,V_DEUDA,V_PAGO,TC,FECMOV,cod_sucursal,ANTIGUO,NRO_RECIBO,PROCESO,V_PAGO_BS,V_DEUDA_BS) VALUES ('" + codpago + "','" + codcli + "','" + resp + "','" + fecpago + "','" + v_deuda + "','" + v_pago + "','" + tc + "','" + fecmov + "','" + cod_sucursal + "','0','" + nro_recibo + "',0,'" + v_pago_bs + "','" + v_deuda_bs + "')";
        //
        //   //out.println(consulta);
        stm.execute(consulta);
    }

    public void agregarFavoritoLinea(String nom) throws SQLException {

        // //out.println(des);
        consulta = "ALTER TABLE `electr_favoritos` ADD `" + nom + "` TINYINT NOT NULL DEFAULT '0'";
        //
        //   //out.println(consulta);
        stm.execute(consulta);
    }

    public void insertarComTemp(String codart, String desc, int cantidad, Double pcosto, Double pmayor, Timestamp fecha, int cod_su, Double tcamext, Double pexterior, Double ppub, Double ganancia, Double ganancia_pub) throws SQLException {

        Double mayor = 0.00;
        Double menor = 0.00;
        consulta = "SELECT * FROM electr_COMTEMP WHERE CODART = '" + codart + "'";
        rs = stm.executeQuery(consulta);
        //  //out.print(rs.getRow());
        if (rs.first() == false) {
            consulta = "INSERT INTO electr_COMTEMP (CODART,DES,CANTIDAD, PCOSTO,PMAYOR, UFECHA, COD_SUCURSAL, TCAMEXT,PEXTERIOR,PPUB,GANANCIA,GANANCIA_PUB) VALUES ('" + codart + "','" + desc + "','" + cantidad + "','" + pcosto + "','" + pmayor + "','" + fecha + "','" + cod_su + "','" + tcamext + "','" + pexterior + "','" + ppub + "','" + ganancia + "','" + ganancia_pub + "')";
            //  //out.println(consulta);
            stm.execute(consulta);
        } else {
            while (rs.next()) {
                mayor = rs.getDouble("PMAYOR");
                menor = rs.getDouble("PPUB");
            }
            //   //out.println(mayor+"<="+pmayor+" "+menor+"<="+ppub);
            if (mayor <= pmayor || menor <= ppub) {
                consulta = "UPDATE electr_COMTEMP SET  DES = '" + desc + "',CANTIDAD= CANTIDAD+'" + cantidad + "',PCOSTO = '" + pcosto + "',PMAYOR = '" + pmayor + "', UFECHA = '" + fecha + "', TCAMEXT = '" + tcamext + "',PEXTERIOR = '" + pexterior + "',PPUB = '" + ppub + "',GANANCIA = '" + ganancia + "',GANANCIA_PUB = '" + ganancia_pub + "' WHERE CODART ='" + codart + "'";
                //       //out.println(consulta);
                stm.execute(consulta);
            } else {
                if (mayor > pmayor || menor > ppub) {
                    consulta = "UPDATE electr_COMTEMP SET  DES = '" + desc + "',CANTIDAD= CANTIDAD+'" + cantidad + "', UFECHA = '" + fecha + "', WHERE CODART ='" + codart + "'";
                    //      System.out.println(consulta);
                    stm.execute(consulta);
                }

            }

        }

    }

    public void modificarIndice(int nuevo, String codart) throws SQLException {


        consulta = "UPDATE electr_EXIS SET cod_exis = '" + nuevo + "' WHERE CODART ='" + codart + "' ";
        stm.execute(consulta);

        // //out.println("salio 1");
    }

    //2018
    public void updateGainsPercent(String finalPrice, String date, String gainsPercent, String codart) throws SQLException {


        consulta = "UPDATE `electr_exis` SET `PMAYOR` = '" + finalPrice + "', `UFECHA` = '" + date + "', `GANANCIA` = '" + gainsPercent + "' WHERE `electr_exis`.`codart` = '" + codart + "' ;";
        System.out.println(consulta);
        // stm.execute(consulta);
    }

    //2018
    public void modificarItem(String cantidad, String pcosto, String pmayor, String ufecha, String codor, String tcamext, String pexterior, String ganancia, String codprov, String codart) throws SQLException {


        consulta = "UPDATE `electr_exis` SET `CANTI01` = `CANTI01` + '" + (int) Double.parseDouble(cantidad) + "', `PCOSTO` = '" + pcosto + "', `PMAYOR` = '" + pmayor + "', `UFECHA` = '" + ufecha + "' , `CODOR` = '" + codor + "' , `TCAMEXT` = '" + tcamext + "'  , `PEXTERIOR` = '" + pexterior + "' , `GANANCIA` = '" + ganancia + "' , `CODPROV` = '" + (int) Double.parseDouble(codprov) + "'  WHERE `electr_exis`.`codart` = '" + codart + "' ";
        System.out.println(consulta + ";");
        //stm.execute(consulta);


        // //out.println("salio 1");
    }

    public void modificarPago(String cod, String estado, String pago, String saldo, String pagobs, String saldobs) throws SQLException {

        //System.out.println("termina false = "+resultados_datos[0]+" 1000 = "+resultados_datos[1]+" 52.524 = "+resultados_datos[2]+" ??? =  "+resultados_datos[3]+" "+resultados_datos[4]+" "+resultados_datos[5]);

        consulta = "UPDATE electr_resumen SET ESTADO = '" + estado + "',PAGO = '" + pago + "' ,SALDO = '" + saldo + "' ,PAGOBS = '" + pagobs + "' ,SALDOBS = '" + saldobs + "' WHERE COD ='" + cod + "' ";
        stm.execute(consulta);

        // //out.println("salio 1");
    }

    public void updateCliente(String a1, String a2, String a3, String a4, String a5, String a6, String a7, String a8, String a9, String a10, String a11, String codcli, String moneda) throws SQLException {


        consulta = "UPDATE electr_clientes SET NOMCLI = '" + a1 + "', DIRECCION = '" + a2 + "', CIUDAD = '" + a3 + "', CELULAR = '" + a4 + "', TELEFONO = '" + a5 + "', EMAIL = '" + a6 + "', NOM_NIT = '" + a7 + "', RESP = '" + a8 + "', NOMEMPRESA = '" + a9 + "', NIT = '" + a10 + "', SENDING = '" + a11 + "', MONEDA = '" + moneda + "' WHERE CODCLI = '" + codcli + "' ";
        //    System.out.println(stm.execute(consulta));
        stm.execute(consulta);


    }

    public void updatePassword(String pass, String codu) throws SQLException {
        consulta = " UPDATE `electr_usuario` SET `password` = '" + pass + "' WHERE `electr_usuario`.`cod_usuario` = '" + codu + "' ";
        // System.out.println(stm.execute(consulta));
        stm.execute(consulta);

    }

    public void modificarItemCatalogo(String linea, String notas, String remplazo, int nuevo, FileInputStream imagen, File f, String img, String des, String des_catalogo, String codart, int oferta) throws SQLException {

        consulta = "UPDATE electr_EXIS SET LIN = '" + linea + "',NOTAS = '" + notas + "',REMPLAZO = '" + remplazo + "',NUEVO = '" + nuevo + "',TIENE_IMAGEN = 1, IMG = ? ,IMAGEN = '" + img + "',DES = '" + des + "',DES_CATALOGO = '" + des_catalogo + "' ,OFERTA = '" + oferta + "' WHERE CODART ='" + codart + "' ";

        PreparedStatement stmt = this.connection.prepareStatement(consulta);
        stmt.setBinaryStream(1, imagen, (int) f.length());
        stmt.execute();


        // System.out.println("salio 1");
    }

    public void modificarExis(String cantidad, String pcosto, String pmayor, String ufecha, String cfecha, String ganancia, String codart) throws SQLException {

        consulta = "UPDATE `electr_exis` SET `CANTI01` = `CANTI01`+'" + cantidad + "', `PCOSTO` = '" + pcosto + "', `PMAYOR` = '" + pmayor + "', `UFECHA` = '" + ufecha + "', `CFECHA` = '" + cfecha + "', `GANANCIA` = '" + ganancia + "'  WHERE CODART ='" + codart + "' ";
        System.out.println(consulta);
        //   stm.execute(consulta.toLowerCase());


    }

    public void modificarPago(String codcli, Timestamp fecpago, Timestamp fecmov, double v_deuda, double v_pago, String literal, String literal_bs, String cod, Double v_deuda_bs, Double v_pago_bs) throws SQLException {

        consulta = "UPDATE `electr_pagos` SET `CODCLI` = '" + codcli + "', `FECPAGO` = '" + fecpago + "', `V_DEUDA` = '" + v_deuda + "', `V_PAGO` = '" + v_pago + "', `FECMOV` = '" + fecmov + "', `LITERAL` = '" + literal + "', `LITERAL_BS` = '" + literal_bs + "', `V_DEUDA_BS` = '" + v_deuda_bs + "', `V_PAGO_BS` = '" + v_pago_bs + "' WHERE CODPAGO ='" + cod + "' ";
        // System.out.println(consulta);
        stm.execute(consulta);


    }

    public void modificarResumen(String codcli, String codvent, Double total, String literal, String fecha, Double iva, Double iva_valor, Double tc, int resp, Double desc_anterior, Double descuento, Double desc_valor, int estado, Double pago, Double saldo, String literal_bs, double total_v_bs, double desc_anterior_bs, double iva_valor_bs, double desc_valor_bs, Double pagobs, Double saldobs) throws SQLException {

        consulta = "UPDATE `electr_resumen` SET `CODCLI` = '" + codcli + "', `CODVENT` = '" + codvent + "', `TOTAL_V` = '" + total + "', `LITERAL` = '" + literal + "', `FEC_VENTA` = '" + fecha + "', `IVA` = '" + iva + "', `IVA_VALOR` = '" + iva_valor + "', `TC` = '" + tc + "', `RESP` = '" + resp + "', `DESC_ANTERIOR` = '" + desc_anterior + "', `DESCUENTO` = '" + descuento + "', `DESC_VALOR` = '" + desc_valor + "', `ESTADO` = '" + estado + "', `PAGO` = '" + pago + "', `SALDO` = '" + saldo + "', `LITERAL_BS` = '" + literal_bs + "' , `TOTAL_V_BS` = '" + total_v_bs + "', `DESC_ANTERIOR_BS` = '" + desc_anterior_bs + "', `IVA_VALOR_BS` = '" + iva_valor_bs + "', `DESC_VALOR_BS` = '" + desc_valor_bs + "' , `PAGOBS` = '" + pagobs + "', `SALDOBS` = '" + saldobs + "' WHERE `electr_resumen`.`codvent` = '" + codvent + "' ";
        //String codcli,String codvent,Double total,String literal,String fecha,Double iva,Double iva_valor,Double tc,int resp,Double desc_anterior,Double descuento,Double desc_valor,int estado,Double pago,Double saldo,String literal_bs
        //   System.out.println(consulta);
//System.out.println(consulta);
        stm.execute(consulta);


    }

    public void modificarResumenCliente(String codcli, String codvent) throws SQLException {

        //   System.out.println(""+codcli+" - "+codvent);
        consulta = "UPDATE `electr_resumen` SET `CODCLI` = '" + codcli + "' WHERE `electr_resumen`.`codvent` = '" + codvent + "' ";
        //String codcli,String codvent,Double total,String literal,String fecha,Double iva,Double iva_valor,Double tc,int resp,Double desc_anterior,Double descuento,Double desc_valor,int estado,Double pago,Double saldo,String literal_bs
        //   System.out.println(consulta);
//System.out.println(consulta);
        stm.execute(consulta);


    }

    public void modificarItemCatalogo1(String linea, String notas, String remplazo, int nuevo, int estado, String img, String des, String des_catalogo, String codart, int oferta) throws SQLException {

        consulta = "UPDATE electr_EXIS SET  LIN = '" + linea + "',NOTAS = '" + notas + "', REMPLAZO = '" + remplazo + "',NUEVO = '" + nuevo + "',TIENE_IMAGEN = '" + estado + "', IMAGEN = '" + img + "',DES = '" + des + "',DES_CATALOGO = '" + des_catalogo + "',OFERTA = '" + oferta + "' WHERE CODART ='" + codart + "' ";
        stm.execute(consulta);
        // System.out.println("salio 1");
    }

    public void setLineaReporte(boolean estado, String codlin) throws SQLException {

        // consulta ="";
        if (estado)
            consulta = "UPDATE `electr_linea` SET `rep` = '1' WHERE `electr_linea`.`DES` = '" + codlin + "'";
        else
            consulta = "UPDATE `electr_linea` SET `rep` = '0' WHERE `electr_linea`.`DES` = '" + codlin + "'";
        //  System.out.println(consulta);
        stm.execute(consulta);
    }

    public void setLineaReporteNombre(String nombre, String codlin) throws SQLException {


        consulta = "UPDATE `electr_favorito` SET `nom_favorito` = '" + nombre + "' WHERE `electr_favorito`.`cod_favorito` = '" + codlin + "'";

        stm.execute(consulta);
    }

    public void setLineaReporte(boolean estado, String codlin, int favorito) throws SQLException {

        // consulta ="";
        if (estado) {
            //   consulta = "UPDATE `electr_linea` SET `rep` = '1' WHERE `electr_linea`.`DES` = '"+codlin+"'";
            if (favorito == 1) {
                consulta = "UPDATE `electr_linea` SET `f1` = '1' WHERE `electr_linea`.`DES` = '" + codlin + "'";
            }
            if (favorito == 2) {
                consulta = "UPDATE `electr_linea` SET `f2` = '1' WHERE `electr_linea`.`DES` = '" + codlin + "'";
            }
            if (favorito == 3) {
                consulta = "UPDATE `electr_linea` SET `f3` = '1' WHERE `electr_linea`.`DES` = '" + codlin + "'";
            }
            if (favorito == 4) {
                consulta = "UPDATE `electr_linea` SET `f4` = '1' WHERE `electr_linea`.`DES` = '" + codlin + "'";
            }
            if (favorito == 5) {
                consulta = "UPDATE `electr_linea` SET `f5` = '1' WHERE `electr_linea`.`DES` = '" + codlin + "'";
            }
            if (favorito == 6) {
                consulta = "UPDATE `electr_linea` SET `f6` = '1' WHERE `electr_linea`.`DES` = '" + codlin + "'";
            }
            if (favorito == 7) {
                consulta = "UPDATE `electr_linea` SET `f7` = '1' WHERE `electr_linea`.`DES` = '" + codlin + "'";
            }
            if (favorito == 8) {
                consulta = "UPDATE `electr_linea` SET `f8` = '1' WHERE `electr_linea`.`DES` = '" + codlin + "'";
            }
            if (favorito == 9) {
                consulta = "UPDATE `electr_linea` SET `f9` = '1' WHERE `electr_linea`.`DES` = '" + codlin + "'";
            }
            if (favorito == 10) {
                consulta = "UPDATE `electr_linea` SET `f10` = '1' WHERE `electr_linea`.`DES` = '" + codlin + "'";
            }
            if (favorito == 11) {
                consulta = "UPDATE `electr_linea` SET `f11` = '1' WHERE `electr_linea`.`DES` = '" + codlin + "'";
            }
            if (favorito == 12) {
                consulta = "UPDATE `electr_linea` SET `f12` = '1' WHERE `electr_linea`.`DES` = '" + codlin + "'";
            }
            if (favorito == 13) {
                consulta = "UPDATE `electr_linea` SET `f13` = '1' WHERE `electr_linea`.`DES` = '" + codlin + "'";
            }
            if (favorito == 14) {
                consulta = "UPDATE `electr_linea` SET `f14` = '1' WHERE `electr_linea`.`DES` = '" + codlin + "'";
            }
            if (favorito == 15) {
                consulta = "UPDATE `electr_linea` SET `f15` = '1' WHERE `electr_linea`.`DES` = '" + codlin + "'";
            }


        } else {

            if (favorito == 1) {
                consulta = "UPDATE `electr_linea` SET `f1` = '0' WHERE `electr_linea`.`DES` = '" + codlin + "'";
            }
            if (favorito == 2) {
                consulta = "UPDATE `electr_linea` SET `f2` = '0' WHERE `electr_linea`.`DES` = '" + codlin + "'";
            }
            if (favorito == 3) {
                consulta = "UPDATE `electr_linea` SET `f3` = '0' WHERE `electr_linea`.`DES` = '" + codlin + "'";
            }
            if (favorito == 4) {
                consulta = "UPDATE `electr_linea` SET `f4` = '0' WHERE `electr_linea`.`DES` = '" + codlin + "'";
            }
            if (favorito == 5) {
                consulta = "UPDATE `electr_linea` SET `f5` = '0' WHERE `electr_linea`.`DES` = '" + codlin + "'";
            }
            if (favorito == 6) {
                consulta = "UPDATE `electr_linea` SET `f6` = '0' WHERE `electr_linea`.`DES` = '" + codlin + "'";
            }
            if (favorito == 7) {
                consulta = "UPDATE `electr_linea` SET `f7` = '0' WHERE `electr_linea`.`DES` = '" + codlin + "'";
            }
            if (favorito == 8) {
                consulta = "UPDATE `electr_linea` SET `f8` = '0' WHERE `electr_linea`.`DES` = '" + codlin + "'";
            }
            if (favorito == 9) {
                consulta = "UPDATE `electr_linea` SET `f9` = '0' WHERE `electr_linea`.`DES` = '" + codlin + "'";
            }
            if (favorito == 10) {
                consulta = "UPDATE `electr_linea` SET `f10` = '0' WHERE `electr_linea`.`DES` = '" + codlin + "'";
            }
            if (favorito == 11) {
                consulta = "UPDATE `electr_linea` SET `f11` = '0' WHERE `electr_linea`.`DES` = '" + codlin + "'";
            }
            if (favorito == 12) {
                consulta = "UPDATE `electr_linea` SET `f12` = '0' WHERE `electr_linea`.`DES` = '" + codlin + "'";
            }
            if (favorito == 13) {
                consulta = "UPDATE `electr_linea` SET `f13` = '0' WHERE `electr_linea`.`DES` = '" + codlin + "'";
            }
            if (favorito == 14) {
                consulta = "UPDATE `electr_linea` SET `f14` = '0' WHERE `electr_linea`.`DES` = '" + codlin + "'";
            }
            if (favorito == 15) {
                consulta = "UPDATE `electr_linea` SET `f15` = '0' WHERE `electr_linea`.`DES` = '" + codlin + "'";
            }
        }

        // System.out.println(consulta);
        stm.execute(consulta);
    }

    public void estableserContador(int cont) throws SQLException {

        for (int i = 0; i < cont; i++) {
            consulta = "INSERT INTO electr_SEC_VENTAS (CODVENT) VALUES ('" + i + "')";
            //   System.out.println(consulta);
            stm.execute(consulta);
        }

    }

    public ResultSet ventaSiguiente(int cod) throws SQLException {

        consulta = "SELECT MAX(ELECTR_SEC_VENTAS.CODVENT), SUM(ELECTR_SEC_VENTAS.terminado=0)\n" +
                "FROM ELECTR_SEC_VENTAS \n" +
                "WHERE cod_sucursal ='" + cod + "'";
        rs = stm.executeQuery(consulta);


        return rs;
    }

    public ResultSet pagoSiguiente(int cod) throws SQLException {

        consulta = "SELECT MAX(ELECTR_SEC_PAGOS.CODPAGO)\n" +
                "FROM ELECTR_SEC_PAGOS \n" +
                "WHERE cod_sucursal ='" + cod + "'";
        rs = stm.executeQuery(consulta);


        return rs;
    }

    public ResultSet ventaError(int cod) throws SQLException {

        consulta = "SELECT *\n" +
                "FROM electr_SEC_VENTAS\n" +
                "WHERE cod_sucursal ='" + cod + "' AND terminado ='0' ORDER BY CODVENT DESC";
        rs = stm.executeQuery(consulta);


        return rs;
    }

    public ResultSet compraSiguiente(int cod) throws SQLException {

        consulta = "SELECT MAX(CODCOM) FROM electr_SEC_COMPRAS WHERE cod_sucursal ='" + cod + "'";
        rs = stm.executeQuery(consulta);


        return rs;
    }

    public ResultSet sucursalActual(int cod) throws SQLException { //OPTIMIZADO

        consulta = "SELECT * FROM electr_SUCURSAL WHERE COD_SUCURSAL ='" + cod + "' ";
        rs = stm.executeQuery(consulta);


        return rs;
    }

    public ResultSet getActualizarDB(int cod) throws SQLException {

        consulta = "SELECT * FROM electr_UPDATEDB WHERE ID_SUCURSAL ='" + cod + "'";
        rs = stm.executeQuery(consulta);


        return rs;
    }

    public ResultSet getPrintVenta(int codcli, Timestamp f1, Timestamp f2) throws SQLException {

        // consulta = "SELECT * FROM electr_resumen WHERE CODCLI = '"+codcli+"' (FEC_VENTA BETWEEN '"+f1+"' AND '"+f2+"') ";
        consulta = "SELECT * FROM electr_resumen WHERE  CODCLI = '" + codcli + "' AND PAGO = 0 AND (FEC_VENTA BETWEEN '" + f1 + "' AND '" + f2 + "') ORDER BY FEC_VENTA DESC";

        // System.out.println(consulta);
        rs = stm.executeQuery(consulta);


        return rs;
    }

    public ResultSet getVentasItem(String f1, String f2) throws SQLException {

        // consulta = "SELECT * FROM electr_resumen WHERE CODCLI = '"+codcli+"' (FEC_VENTA BETWEEN '"+f1+"' AND '"+f2+"') ";
        consulta = "SELECT * FROM electr_resumen WHERE  (FEC_VENTA BETWEEN '" + f1 + "' AND '" + f2 + "') ORDER BY FEC_VENTA DESC";

        // System.out.println(consulta);
        rs = stm.executeQuery(consulta);


        return rs;
    }

    public ResultSet getPrintVentaTodo(int codcli, Timestamp f1, Timestamp f2) throws SQLException {

        // consulta = "SELECT * FROM electr_resumen WHERE CODCLI = '"+codcli+"' (FEC_VENTA BETWEEN '"+f1+"' AND '"+f2+"') ";
        consulta = "SELECT * FROM electr_resumen WHERE  CODCLI = '" + codcli + "' AND (FEC_VENTA BETWEEN '" + f1 + "' AND '" + f2 + "') ORDER BY FEC_VENTA DESC";

        // System.out.println(consulta);
        rs = stm.executeQuery(consulta);


        return rs;
    }

    public ResultSet getPrintVentaFactura(int codcli) throws SQLException {

        // consulta = "SELECT * FROM electr_resumen WHERE CODCLI = '"+codcli+"' (FEC_VENTA BETWEEN '"+f1+"' AND '"+f2+"') ";
        consulta = "SELECT * FROM electr_resumen WHERE  CODCLI = '" + codcli + "' AND ESTADO = 0 ORDER BY FEC_VENTA DESC ";

        // System.out.println(consulta);
        rs = stm.executeQuery(consulta);


        return rs;
    }

    public void ventaInsertar(int cod, int resp, int su) throws SQLException {

        consulta = "INSERT INTO electr_sec_ventas (CODVENT,ESTADO, RESP,cod_sucursal,TERMINADO) VALUES ('" + cod + "',1,'" + resp + "','" + su + "','0')";
        //   System.out.println(consulta);
        stm.execute(consulta);

    }

    public void pagoInsertar(int cod, int resp, int su) throws SQLException {

        consulta = "INSERT INTO electr_sec_pagos (CODPAGO,ESTADO, RESP,cod_sucursal) VALUES ('" + cod + "',1,'" + resp + "','" + su + "')";
        //   //out.println(consulta);
        stm.execute(consulta);

    }

    public void compraInsertar(int cod, int resp, int su) throws SQLException {

        consulta = "INSERT INTO electr_sec_compras (CODCOM, RESP,cod_sucursal) VALUES ('" + cod + "','" + resp + "','" + su + "')";
        //  System.out.println(consulta);
        stm.execute(consulta);

    }

    public void ventaDesocuparCero(int codvent, int su) throws SQLException {

        consulta = "UPDATE electr_sec_ventas SET TERMINADO ='1',ESTADO = '0' WHERE CODVENT ='" + codvent + "' AND cod_sucursal ='" + su + "'";
        stm.execute(consulta);
    }

    public void ventaDesocuparCeroM(int codvent, int su, Timestamp fecha, int resp) throws SQLException {

        consulta = "UPDATE electr_sec_ventas SET TERMINADO ='1',ESTADO = '0' ,MODIFY = '0' , MODIFICAR = '" + fecha + "',RESP = '" + resp + "'  WHERE CODVENT ='" + codvent + "' AND cod_sucursal ='" + su + "'";
        stm.execute(consulta);
    }

    public void pagoDesocuparCero(int codvent, int su) throws SQLException {

        consulta = "UPDATE electr_sec_pagos SET ESTADO = '0' ,TERMINADO ='1' WHERE CODPAGO ='" + codvent + "' AND cod_sucursal ='" + su + "'";
        stm.execute(consulta);
    }

    public void compraDesocuparCeroC(int codcom, int su) throws SQLException {

        consulta = "UPDATE electr_sec_compras SET ESTADO = '0' WHERE CODCOM ='" + codcom + "' AND cod_sucursal ='" + su + "'";
        stm.execute(consulta);
    }

    public void ventaOcupar(int codvent, int su, int resp) throws SQLException {

        consulta = "UPDATE electr_sec_ventas SET ESTADO = '1', RESP = '" + resp + "' WHERE CODVENT ='" + codvent + "' AND cod_sucursal ='" + su + "'";
        //  System.out.println(consulta);
        stm.execute(consulta);
    }

    public void pagoOcupar(int codvent, int su) throws SQLException {

        consulta = "UPDATE electr_sec_pagos SET ESTADO = '1' WHERE CODPAGO ='" + codvent + "' AND cod_sucursal ='" + su + "'";
        stm.execute(consulta);
    }

    public void compraOcupar(int codvent, int su) throws SQLException {

        consulta = "UPDATE electr_sec_compras SET ESTADO = '1' WHERE CODCOM ='" + codvent + "' AND cod_sucursal ='" + su + "'";
        stm.execute(consulta);
    }

    public void ventaAnular(int cod, int su, String codvent, String ts, int resp) throws SQLException {

        consulta = "UPDATE electr_sec_ventas SET VALIDO = '0' , TERMINADO = '1', MODIFICAR = '" + ts + "' , RESP = '" + resp + "'  WHERE CODVENT ='" + cod + "' AND cod_sucursal ='" + su + "'";
        stm.execute(consulta);
        // System.out.println(consulta) ;
        consulta = "DELETE FROM electr_resumen WHERE CODVENT ='" + codvent + "'";
        stm.execute(consulta);
        //  System.out.println(consulta) ;

    }

    public void ventaBorrar(int cod, int su, String codvent) throws SQLException {


        consulta = "DELETE FROM electr_sec_ventas WHERE CODVENT ='" + cod + "' AND cod_sucursal ='" + su + "'";
        stm.execute(consulta);

        consulta = "DELETE FROM electr_resumen WHERE CODVENT ='" + codvent + "'";
        stm.execute(consulta);


    }

    public void pagoBorrar(int cod, int su, String codvent) throws SQLException {


        consulta = "DELETE FROM electr_sec_pagos WHERE CODPAGO ='" + cod + "' AND cod_sucursal ='" + su + "'";
        stm.execute(consulta);

        consulta = "DELETE FROM electr_resumen_pagos WHERE CODPAGO ='" + codvent + "'";
        stm.execute(consulta);

    }

    public void compraBorrar(int cod, int su, String codvent) throws SQLException {

        consulta = "DELETE FROM electr_sec_compras WHERE CODCOM ='" + cod + "' AND cod_sucursal ='" + su + "'";
        stm.execute(consulta);

        consulta = "DELETE FROM electr_compras WHERE CODCOM ='" + codvent + "'";
        stm.execute(consulta);
    }

    public ResultSet ventaObtener(int cod, int su) throws SQLException {

        consulta = "SELECT * FROM electr_sec_ventas WHERE CODVENT ='" + cod + "' AND cod_sucursal ='" + su + "'";
        rs = stm.executeQuery(consulta);
        return rs;
    }

    //2018
    public boolean setNewPurchase(int purchaseID, int userID, int shopID) throws SQLException {
        consulta = "INSERT INTO `electr_sec_ventas` (`CODVENT`, `ESTADO`, `modificar`, `resp`, `cod_sucursal`, `VALIDO`, `cod`, `terminado`, `modify`) VALUES ('" + purchaseID + "', '1', '0000-00-00 00:00:00.000000', '" + userID + "', '" + shopID + "', '1', NULL, '0', '0');";
        return stm.execute(consulta);

    }


    //2018
    public boolean returnDevArt(String codcli, String codart, String qty, String priceUSD, String totalUSD, String codDev, String desc, String shopID, String priceBS, String totalBS, String codVent) throws SQLException {
        consulta = "INSERT INTO `electr_devart` (`CODCLI`, `CODART`, `CANTIDAD`, `PRECIO`, `TOTAL`, `CODDEV`, `DES`, `cod_sucursal`, `PRECIO_BS`, `TOTAL_BS`, `CODVENT`, `COD`) VALUES ('" + codcli + "', '" + codart + "', '" + qty + "', '" + priceUSD + "', '" + totalUSD + "', '" + codDev + "', '" + desc + "', '" + shopID + "', '" + priceBS + "', '" + totalBS + "', '" + codVent + "', NULL);";
        System.out.println(consulta);
        //  return stm.execute(consulta);
        return true;
    }

    //2018
    public boolean returnProduct(String shopID, String qty, String codArt, String note) throws SQLException {
        consulta = "UPDATE `electr_exis` SET `CANTI0" + shopID + "` =  `CANTI0" + shopID + "` + '" + qty + "' , `NOTAS` = '" + note + "' WHERE `electr_exis`.`codart` = '" + codArt + "';";
        System.out.println(consulta);
        //  return stm.execute(consulta);
        return true;
    }

    //2018
    public boolean returnResume(String codcli, String codDev, String totalUSD, String literalUSD, String date, String tcLocal, String userID, String shopID, String literalBS, String process, String totalBS) throws SQLException {
        consulta = "INSERT INTO `electr_resumen_devolucion` (`cod`, `CODCLI`, `CODDEV`, `TOTAL_D`, `LITERAL`, `FEC_DEV`, `TC`, `RESP`, `SUCURSAL`, `LITERAL_BS`, `PROCESO`, `TOTAL_D_BS`) VALUES (NULL, '" + codcli + "', '" + codDev + "', '" + totalUSD + "', '" + literalUSD + "', '" + date + "', '" + tcLocal + "', '" + userID + "', '" + shopID + "', '" + literalBS + "', '" + process + "', '" + totalBS + "');";
        System.out.println(consulta);
        //  return stm.execute(consulta);
        return true;
    }

    public void devolverItem(String codcli, String codart, int cantidad, String cod, String sucursal) throws SQLException {

        consulta = "DELETE FROM electr_CLIART WHERE CODART ='" + codart + "' AND CODVENT ='" + cod + "' AND CODCLI ='" + codcli + "' AND CANTIDAD ='" + cantidad + "' ";
        //   System.out.println(consulta);
        stm.execute(consulta);
        consulta = "UPDATE electr_EXIS SET " + sucursal + " = " + sucursal + " + '" + cantidad + "' WHERE CODART ='" + codart + "' ";
        stm.execute(consulta);
    }

    public void devolverPago(String codpago, String cod, String recibo, int id) throws SQLException {

        consulta = "DELETE FROM electr_FACTURAS WHERE CODPAGO ='" + codpago + "' AND CODCLI ='" + cod + "' AND NRO_RECIBO ='" + recibo + "' AND COD_SUCURSAL ='" + id + "' ";
        stm.execute(consulta);

    }

    public void devolverItemCompra(String codprov, String codart, int cantidad, String cod, String sucursal) throws SQLException {

        consulta = "DELETE FROM electr_COMPART WHERE CODART ='" + codart + "' AND CODCOM ='" + cod + "' AND CODPROV ='" + codprov + "' AND CANTIDAD ='" + cantidad + "' ";
        stm.execute(consulta);
        //   consulta = "UPDATE DAYTOM_EXIS SET "+sucursal+" = "+sucursal+" - '"+cantidad+"' WHERE CODART ='"+codart+"' ";
        //     stm.execute(consulta);
    }

    public void updateResumenPago(String resp, Double total, double total_bs, double tc, String fecmov, String literal, String literal_bs, String cod) throws SQLException {


        consulta = "UPDATE electr_RESUMEN_PAGOS SET RESP='" + resp + "',TOTAL='" + total + "',TOTAL_BS='" + total_bs + "',TC='" + tc + "',FECMOV='" + fecmov + "',PROCESO='1',LITERAL='" + literal + "',LITERAL_BS='" + literal_bs + "' WHERE CODPAGO ='" + cod + "' ";

        //     System.out.println(consulta);
        stm.execute(consulta);


    }

    public void updateResumen(int codcli, String codvent, Double total, String literal, String fec, int lugGar, Double iva, Double iva_valor, Double tc, int resp, Double desc_valor, Double desc_antes, Double descuento, Double ivabs, Double descbs, Double desc_antesbs, Double totalbs, String literalbs) throws SQLException {


        consulta = "UPDATE electr_RESUMEN SET CODCLI='" + codcli + "',TOTAL_V='" + total + "',SALDO='" + total + "',LITERAL='" + literal + "',FEC_VENTA='" + fec + "',IVA='" + iva + "',IVA_VALOR='" + iva_valor + "',TC='" + tc + "',RESP='" + resp + "',DESC_ANTERIOR='" + desc_antes + "',DESCUENTO='" + descuento + "',DESC_VALOR='" + desc_valor + "',ESTADO='0',IVA_VALOR_BS='" + ivabs + "',DESC_ANTERIOR_BS='" + desc_antesbs + "',DESC_VALOR_BS='" + descbs + "',TOTAL_V_BS='" + totalbs + "',LITERAL_BS='" + literalbs + "',SALDOBS='" + totalbs + "' WHERE CODVENT ='" + codvent + "' ";

        //     System.out.println(consulta);
        stm.execute(consulta);


    }

    public void updateResumenM(int codcli, String codvent, Double total, String literal, String fec, int lugGar, Double iva, Double iva_valor, Double tc, Double desc_valor, Double desc_antes, Double descuento, Double ivabs, Double descbs, Double desc_antesbs, Double totalbs, String literalbs) throws SQLException {


        consulta = "UPDATE electr_RESUMEN SET CODCLI='" + codcli + "',TOTAL_V='" + total + "',SALDO='" + total + "'-PAGO,LITERAL='" + literal + "',FEC_VENTA='" + fec + "',IVA='" + iva + "',IVA_VALOR='" + iva_valor + "',TC='" + tc + "',DESC_ANTERIOR='" + desc_antes + "',DESCUENTO='" + descuento + "',DESC_VALOR='" + desc_valor + "',ESTADO='0',IVA_VALOR_BS='" + ivabs + "',DESC_ANTERIOR_BS='" + desc_antesbs + "',DESC_VALOR_BS='" + descbs + "',TOTAL_V_BS='" + totalbs + "',LITERAL_BS='" + literalbs + "',SALDOBS='" + totalbs + "' - PAGOBS WHERE CODVENT ='" + codvent + "' ";

        //     System.out.println(consulta);
        stm.execute(consulta);


    }

    public void updateResumenCompra(Timestamp fec, Double tc, int codprov, int luGgar, Double total, Double total_usd, String literal, String literal_usd, int resp, Double tcext, int cod_sucursal, String codcom) throws SQLException {


        consulta = "UPDATE electr_COMPRAS SET FECHA='" + fec + "',TC='" + tc + "',CODPROV='" + codprov + "',TOTAL='" + total + "',TOTAL_USD='" + total_usd + "',LITERAL='" + literal + "',LITERAL_USD='" + literal_usd + "',RESP='" + resp + "',TCEXT='" + tcext + "',PROCESO='0',COD_SUCURSAL ='" + cod_sucursal + "' WHERE CODCOM ='" + codcom + "' ";

        //   System.out.println(consulta);
        stm.execute(consulta);

    }

    public void updateResumenPago(Timestamp fec, Double total, Double total_usd, String literal, String literal_usd, int resp, int cod_sucursal, String codpago) throws SQLException {


        consulta = "UPDATE electr_PAGOS SET FECHA='" + fec + "',TOTAL='" + total + "',TOTAL_BS='" + total_usd + "',LITERAL='" + literal + "',LITERAL_BS='" + literal_usd + "',RESP='" + resp + "',PROCESO='0',COD_SUCURSAL ='" + cod_sucursal + "' WHERE CODPAGO ='" + codpago + "' ";

        //   System.out.println(consulta);
        stm.execute(consulta);

    }

    public void updateResumenM(int codcli, String codvent, Double total, String literal, int luGgar, Double imp, Double iva, Double tc, Double desc, Double desc_antes, Double descuento, Double ivabs, Double descbs, Double desc_antesbs, Double totalbs, String literalbs) throws SQLException {


        consulta = "UPDATE electr_RESUMEN SET CODCLI='" + codcli + "',TOTAL_V='" + total + "',LITERAL='" + literal + "',IMP='" + imp + "',IVA='" + iva + "',TC='" + tc + "',DESC_ANTES='" + desc_antes + "',DESCUENTO='" + descuento + "',DESCU='" + desc + "',IVA_BS='" + ivabs + "',DESC_ANTES_BS='" + desc_antesbs + "',DESCU_BS='" + descbs + "',TOTAL_VBS='" + totalbs + "',LITERALBS='" + literalbs + "',PAGADO='0' WHERE CODVENT ='" + codvent + "' ";

        //    System.out.println(consulta);
        stm.execute(consulta);
        consulta = "UPDATE electr_deudas SET CODCLI = '" + codcli + "', TOTAL ='" + total + "', TOTAL_BS = '" + total * tc + "', SALDO = '" + total + "', SALDO_BS = '" + total * tc + "'WHERE CODVENT ='" + codvent + "' ";
        stm.execute(consulta);

    }

    public void insertarResumen(int codcli, String codvent, Double total, String literal, Timestamp fec, int lugGar, Double imp, Double iva, Double tc, int resp, Double desc, Double desc_antes, Double descuento, Double ivabs, Double descbs, Double desc_antesbs, Double totalbs, String literalbs) throws SQLException {

        consulta = "INSERT INTO electr_resumen (CODCLI,CODVENT, TOTAL_V, LITERAL, FEC_VENTA,IMP, IVA, TC, RESP, DESC_ANTES, DESCUENTO, DESCU, IVA_BS, DESC_ANTES_BS, DESCU_BS, TOTAL_VBS, LITERALBS,PAGADO) VALUES ('" + codcli + "','" + codvent + "','" + total + "','" + literal + "','" + fec + "','" + imp + "','" + iva + "','" + tc + "','" + resp + "','" + desc_antes + "','" + descuento + "','" + desc + "','" + ivabs + "','" + desc_antesbs + "','" + descbs + "','" + totalbs + "','" + literalbs + "','0')";
        //   System.out.println(consulta);
        stm.execute(consulta);
        consulta = "INSERT INTO electr_deudas (CODCLI, CODVENT, TOTAL, TOTAL_BS, PAGO, SALDO, PAGO_BS, SALDO_BS) VALUES ('" + codcli + "','" + codvent + "','" + total + "','" + total * tc + "',0.00,'" + total + "',0.00,'" + total * tc + "')";
        stm.execute(consulta);

    }

    public void insertarPreResumen(String codvent, int resp) throws SQLException {

        consulta = "INSERT INTO electr_resumen (CODVENT,RESP) VALUES ( '" + codvent + "','" + resp + "')";
        //    System.out.println(consulta);
        stm.execute(consulta);

    }

    public void insertarPrePago(String codvent, String cod) throws SQLException {

        consulta = "INSERT INTO electr_resumen_pagos (CODPAGO,COD_SUCURSAL) VALUES ( '" + codvent + "','" + cod + "')";
        //   System.out.println(consulta);
        stm.execute(consulta);


    }

    public void insertarPreResumenCompra(String codvent) throws SQLException {

        consulta = "INSERT INTO electr_compras (CODCOM) VALUES ( '" + codvent + "')";
        //   System.out.println(consulta);
        stm.execute(consulta);

    }

    public void insertarResumenC(String codcom, String codprov, Timestamp fecha, Double tc, Double text, int lugGar, Double total, String literal, int resp, String literalusd) throws SQLException {

        consulta = "DELETE FROM electr_compras WHERE CODCOM ='" + codcom + "' AND CODPROV='" + codprov + "' ";
        //    System.out.println(consulta);
        stm.execute(consulta);
        consulta = "INSERT INTO electr_compras (CODCOM,CODPROV, FECHA, TC, TCEXT, TOTAL, LITERAL, RESP, PROCESO,TOTAL_USD,LITERAL_USD,LITERAL_USD_ENG) VALUES ('" + codcom + "','" + codprov + "','" + fecha + "','" + tc + "','" + text + "','" + total + "','" + literal + "','" + resp + "','0','" + total * text + "','" + literalusd + "','nothing')";
        //   System.out.println(consulta);
        stm.execute(consulta);

    }

    public void insertarAnulador(int resp, String codvent, Timestamp fecha, Double total) throws SQLException {

        consulta = "INSERT INTO electr_anulador (RESPONSABLE,ANULADO, FECHA, TOTAL) VALUES ('" + resp + "','" + codvent + "','" + fecha + "','" + total + "')";
        //    System.out.println(consulta);
        stm.execute(consulta);

    }

    public void insertarItemNuevo(String CODART, String DES, int CANTI01, Double PCOSTO, Double PMAYOR, int LIN, Timestamp UFECHA, Timestamp CFECHA, FileInputStream imagen, Double GANANCIA, File f) throws SQLException {

        consulta = "INSERT INTO `electr_exis` ( `CODART`, `DES`, `REMPLAZO`, `CANTI04`, `CANTI01`, `PCOSTO`, `PMAYOR`, `PESPECIAL`, `CANTI02`, `CANTI03`, `LIN`, `UFECHA`, `CFECHA`, `CODCAT`, `RANK`, `TIENE_IMAGEN`, `IMG`, `OFERTA`, `DES_CATALOGO`, `NUEVO`, `CODOR`, `cod_sucursal`, `cod_ubicacion`, `CONFIRMADO`, `RESPONSABLE`, `TCAMEXT`, `PEXTERIOR`, `PPUB`, `pespecial_pub`, `GANANCIA`, `GANANCIA_PUB`, `CODPROV`, `NOTAS`, `MOQ`, `TCAM`, `IMAGEN`, `MARCA`) VALUES ( '" + CODART + "', '" + DES + "', '', '0', '" + CANTI01 + "', '" + PCOSTO + "', '" + PMAYOR + "', '" + PMAYOR + "', '0', '0', '" + LIN + "', '" + UFECHA + "', '" + CFECHA + "', '0', '0', '0',?, '0', '', '0', '" + CODART + "', '1', '0', '1', '2', '1.000', '0.00','0.00','0.00', '" + GANANCIA + "', '0.000', '0', '', '0', '7', 'no-img.png', '1')";
        PreparedStatement stmt = this.connection.prepareStatement(consulta);
        stmt.setBinaryStream(1, imagen, (int) f.length());
        stmt.execute();
        //INSERT INTO `electr_exis` (`cod_exis`, `CODART`, `DES`, `CANTI04`, `CANTI01`, `PCOSTO`, `PMAYOR`, `CANTI02`, `CANTI03`, `LIN`, `UFECHA`, `CFECHA`, `CODCAT`, `RANK`, `TIENE_IMAGEN`, `IMG`, `OFERTA`, `DES_CATALOGO`, `NUEVO`, `CODOR`, `cod_sucursal`, `cod_ubicacion`, `CONFIRMADO`, `RESPONSABLE`, `TCAMEXT`, `PEXTERIOR`, `PPUB`, `pespecial_pub`, `GANANCIA`, `GANANCIA_PUB`, `NOTAS`, `MOQ`, `IMAGEN`, `MARCA`, `CODPROV`, `REMPLAZO`, `TCAM`, `PESPECIAL`) VALUES (NULL, '1.5KE200', '', '0', '1111', '1111', '1111', '0', '0', NULL, '2018-03-06 00:00:00', '2018-03-06 00:00:00', NULL, '0', '0', NULL, '0', '', '0', ' 1111', '11', '0', '1', '1', '11111', '11111', NULL, NULL, '1111', '0.000', ' ', '0', 'no-img.png', '1', '35', '', '6.960', '1111');
    }

    //    public void insertarItem(String CODART,String DES,int CANTI01,Double PCOSTO,Double PMAYOR,int LIN,Timestamp UFECHA,Timestamp CFECHA,FileInputStream imagen,Double GANANCIA,File f) throws SQLException{
//
//        consulta = "INSERT INTO `electr_exis` (`cod_exis`, `CODART`, `DES`, `CANTI04`, `CANTI01`, `PCOSTO`, `PMAYOR`, `CANTI02`, `CANTI03`, `LIN`, `UFECHA`, `CFECHA`, `CODCAT`, `RANK`, `TIENE_IMAGEN`, `IMG`, `OFERTA`, `DES_CATALOGO`, `NUEVO`, `CODOR`, `cod_sucursal`, `cod_ubicacion`, `CONFIRMADO`, `RESPONSABLE`, `TCAMEXT`, `PEXTERIOR`, `PPUB`, `pespecial_pub`, `GANANCIA`, `GANANCIA_PUB`, `NOTAS`, `MOQ`, `IMAGEN`, `MARCA`, `CODPROV`, `REMPLAZO`, `TCAM`, `PESPECIAL`) VALUES (NULL, '1.5KE200', '', '0', '1111', '1111', '1111', '0', '0', NULL, '2018-03-06 00:00:00', '2018-03-06 00:00:00', NULL, '0', '0', NULL, '0', '', '0', ' 1111', '11', '0', '1', '1', '11111', '11111', NULL, NULL, '1111', '0.000', ' ', '0', 'no-img.png', '1', '35', '', '6.960', '1111');";
//        PreparedStatement stmt = this.connection.prepareStatement(consulta);
//        stmt.setBinaryStream(1, imagen, (int) f.length());
//        stmt.execute();
//
//    }
    //2018
    public void insertarItemNuevoCompra(String codart, String desc, String cantidad, String pcosto, String pmayor, String fec, String codor, String pexterior, String ganancia, String codprov, String line) throws SQLException {
//System.out.println("????????");
        consulta = "INSERT INTO `electr_exis` (`cod_exis`, `CODART`, `DES`, `CANTI04`, `CANTI01`,`PCOSTO`, `PMAYOR`, `CANTI02`, `CANTI03`, `LIN`, `UFECHA`, `CFECHA`, `CODCAT`, `RANK`, `TIENE_IMAGEN`, `IMG`, `OFERTA`, `DES_CATALOGO`, `NUEVO`, `CODOR`, `cod_sucursal`, `cod_ubicacion`, `CONFIRMADO`, `RESPONSABLE`, `TCAMEXT`, `PEXTERIOR`, `PPUB`, `pespecial_pub`, `GANANCIA`, `GANANCIA_PUB`, `NOTAS`, `MOQ`, `IMAGEN`, `MARCA`, `CODPROV`, `REMPLAZO`, `TCAM`, `PESPECIAL`) VALUES (NULL, '" + codart + "', '" + desc + "', '0', '" + (int) Double.parseDouble(cantidad) + "', '" + pcosto + "', '" + (pmayor) + "', '0', '0', '" + line + "', '" + fec + "', '" + fec + "', NULL, '0', '0', NULL, '0', '" + desc + "', '1', '" + codor + "', '1', '0', '1', '2', '1.3', '" + (pexterior) + "', NULL, NULL, '" + (ganancia) + "', '0.000', ' ', '0', 'no-img.png', '1', '" + (int) Double.parseDouble(codprov) + "', '', '6.960', '" + (pmayor) + "');";
        System.out.println(consulta);
        // stm.execute(consulta);

    }

    public void insertarPagoNuevo(String codpago, String codcli, Timestamp fecpago, Double v_deuda, Double v_pago, Double tc, Timestamp fecmov, String literal, String literal_bs, Double v_deuda_bs, Double v_pago_bs) throws SQLException {

        consulta = "INSERT INTO `electr_pagos` ( `CODPAGO`, `CODCLI`, `RESP`, `FECPAGO`, `V_DEUDA`, `V_PAGO`, `TC`, `FECMOV`, `COD_SUCURSAL`, `PROCESO`, `LITERAL`, `LITERAL_BS`, `V_DEUDA_BS`, `V_PAGO_BS`) VALUES ( '" + codpago + "', '" + codcli + "', '1', '" + fecpago + "', '" + v_deuda + "', '" + v_pago + "', '" + tc + "', '" + fecmov + "', '1', '1', '" + literal + "', '" + literal_bs + "', '" + v_deuda_bs + "', '" + v_pago_bs + "')";
        //` System.out.println(consulta);
        stm.execute(consulta);

        int pago = Integer.valueOf(codpago.substring(1, codpago.length()));
        consulta = " INSERT INTO `electr_sec_pagos` (`CODPAGO`, `ESTADO`, `VALIDO`) VALUES ('" + pago + "', '0', '1'); ";
        // UPDATE `electr_cliart` SET `CANTIDAD` = '501', `PRECIO` = '0.11', `TOTAL` = '51', `NRO` = '101', `DES` = '11' WHERE `electr_cliart`.`cod` = 201474;
        stm.execute(consulta);


    }

    public void insertarPagoNuevoPro(String codpago, String codcli, String resp, String fecpago, Double v_pago, Double tc, String sucursal, String literal, String literal_bs, Double v_pago_bs, String recibo) throws SQLException {

        consulta = "INSERT INTO `electr_pagos` ( `CODPAGO`, `CODCLI`, `RESP`, `FECPAGO`, `V_PAGO`, `TC`, `COD_SUCURSAL`, `LITERAL`, `LITERAL_BS`, `V_PAGO_BS`, `RECIBO`) VALUES ( '" + codpago + "', '" + codcli + "', '" + resp + "', '" + fecpago + "',  '" + v_pago + "', '" + tc + "', '" + sucursal + "', '" + literal + "', '" + literal_bs + "', '" + v_pago_bs + "', '" + recibo + "')";
        //  System.out.println(consulta);
        stm.execute(consulta);

    }

    public void insertarResumenNuevo(String codcli, String codvent, Double total, String literal, String fecha, Double iva, Double valor_iva, Double tc, int resp, Double desc_anterior, Double descuento, Double desc_valor, int estado, Double pago, Double saldo, String literal_bs, Double total_v_bs, Double desc_anterior_bs, Double iva_valor_bs, Double desc_valor_bs, Double pagobs, Double saldobs) throws SQLException {


        consulta = "INSERT INTO `electr_resumen` ( `CODCLI`, `CODVENT`, `TOTAL_V`, `LITERAL`, `FEC_VENTA`, `IVA`, `IVA_VALOR`, `TC`, `RESP`, `DESC_ANTERIOR`, `DESCUENTO`, `DESC_VALOR`, `ESTADO`, `PAGO`, `SALDO`, `LITERAL_BS`, `TOTAL_V_BS`, `DESC_ANTERIOR_BS`, `IVA_VALOR_BS`, `DESC_VALOR_BS`,`PAGOBS`,`SALDOBS`) VALUES ( '" + codcli + "', '" + codvent + "', '" + total + "', '" + literal + "', '" + fecha + "', '" + iva + "', '" + valor_iva + "', '" + tc + "', '" + resp + "','" + desc_anterior + "', '" + descuento + "', '" + desc_valor + "', '" + estado + "', '" + pago + "', '" + saldo + "', '" + literal_bs + "', '" + total_v_bs + "', '" + desc_anterior_bs + "', '" + iva_valor_bs + "', '" + desc_valor_bs + "', '" + pagobs + "', '" + saldobs + "');";
        //  System.out.println(consulta);
        stm.execute(consulta);

        int venta = Integer.valueOf(codvent.substring(1, codvent.length()));
        consulta = " INSERT INTO `electr_sec_ventas` (`CODVENT`) VALUES ('" + venta + "') ";
        // UPDATE `electr_cliart` SET `CANTIDAD` = '501', `PRECIO` = '0.11', `TOTAL` = '51', `NRO` = '101', `DES` = '11' WHERE `electr_cliart`.`cod` = 201474;
        stm.execute(consulta);


    }

    public void insertarSecVentas(String codvent, String fecha) throws SQLException {

        String aux = codvent.substring(1);
        int aux2 = Integer.valueOf(aux);
        //   System.out.println(aux2);
        consulta = "INSERT INTO `electr_sec_ventas` (`CODVENT`, `ESTADO`, `modificar`, `resp`, `cod_sucursal`, `VALIDO`, `TERMINADO`) VALUES ('" + aux2 + "', '0', '" + fecha + "', '1', '1', '1', '1')";
        //` System.out.println(consulta);
        stm.execute(consulta);
        //     String codcli,Stringcod_vent,Double total,String literal,String fecha,Double iva,Double valor_iva,Double tc,Double desc_anterior,Double descuento,Double desc_valor,int estado,Double pago,Double saldo,String literal_bs

    }

    public void borrarResumen(int cod, String codvent) throws SQLException {

        consulta = "DELETE FROM electr_resumen WHERE CODCLI ='" + cod + "' AND CODVENT='" + codvent + "' ";
        //    System.out.println(consulta);
        stm.execute(consulta);
        consulta = "DELETE FROM electr_deudas WHERE CODCLI ='" + cod + "' AND CODVENT='" + codvent + "' ";
        //     System.out.println(consulta);
        stm.execute(consulta);

    }

    public void borrarItems(int cod, String codvent) throws SQLException {

        consulta = "DELETE FROM electr_resumen WHERE CODCLI ='" + cod + "' AND CODVENT='" + codvent + "' ";
        //   System.out.println(consulta);
        stm.execute(consulta);
        consulta = "DELETE FROM electr_deudas WHERE CODCLI ='" + cod + "' AND CODVENT='" + codvent + "' ";
        //    System.out.println(consulta);
        stm.execute(consulta);

    }

    public void modificarResumen(int codcli, String codvent, Double total, String literal, Timestamp fec, Double imp, Double iva, Double tc, Double desc, Double desc_antes, Double descuento, Double ivabs, Double descbs, Double desc_antesbs, Double totalbs, String literalbs) throws SQLException {

        consulta = "UPDATE electr_resumen SET TOTAL_V= '" + total + "', LITERAL = '" + literal + "', FEC_VENTA = '" + fec + "', IMP = '" + imp + "', IVA = '" + iva + "', TC = '" + tc + "', DESC_ANTES = '" + desc_antes + "' , DESCUENTO= '" + descuento + "', DESCU = '" + desc + "', IVA_BS = '" + ivabs + "', DESC_ANTES_BS = '" + desc_antesbs + "', DESCU_BS = '" + descbs + "', TOTAL_VBS = '" + totalbs + "', LITERALBS = '" + literalbs + "' WHERE CODCLI ='" + codcli + "' AND CODVENT ='" + codvent + "'";
        //   System.out.println(consulta);
        stm.execute(consulta);
        consulta = "UPDATE electr_deudas SET CODCLI = '" + codcli + "', TOTAL ='" + total + "', TOTAL_BS = '" + total * tc + "', SALDO = '" + total + "', SALDO_BS = '" + total * tc + "'WHERE CODVENT ='" + codvent + "' ";
        stm.execute(consulta);

    }

    public void modificarResumenM(int codcli, String codvent, Double total, String literal, Double imp, Double iva, Double tc, Double desc, Double desc_antes, Double descuento, Double ivabs, Double descbs, Double desc_antesbs, Double totalbs, String literalbs) throws SQLException {

        consulta = "UPDATE electr_resumen SET TOTAL_V= '" + total + "', LITERAL = '" + literal + "', IMP = '" + imp + "', IVA = '" + iva + "', TC = '" + tc + "', DESC_ANTES = '" + desc_antes + "' , DESCUENTO= '" + descuento + "', DESCU = '" + desc + "', IVA_BS = '" + ivabs + "', DESC_ANTES_BS = '" + desc_antesbs + "', DESCU_BS = '" + descbs + "', TOTAL_VBS = '" + totalbs + "', LITERALBS = '" + literalbs + "' WHERE CODCLI ='" + codcli + "' AND CODVENT ='" + codvent + "'";
        //    System.out.println(consulta);
        stm.execute(consulta);
        consulta = "UPDATE electr_deudas SET CODCLI = '" + codcli + "', TOTAL ='" + total + "', TOTAL_BS = '" + total * tc + "', SALDO = '" + total + "', SALDO_BS = '" + total * tc + "'WHERE CODVENT ='" + codvent + "' ";
        stm.execute(consulta);

    }

    public void estadoResumen(int codcli, String codvent, int estado) throws SQLException {

        consulta = "UPDATE electr_resumen SET ESTADO = '" + estado + "' WHERE CODCLI ='" + codcli + "' AND CODVENT ='" + codvent + "'";
        //    System.out.println(consulta);
        stm.execute(consulta);

    }

    public void insertarIndice(int indice, String codart, String codvent, int codcli) throws SQLException {

        consulta = "UPDATE electr_CLIART SET NRO = '" + indice + "' WHERE CODART ='" + codart + "' AND CODVENT ='" + codvent + "' AND CODCLI ='" + codcli + "' ";
        stm.execute(consulta);
    }

    public void insertarIndiceC(int indice, String codart, String codcom, String codprov) throws SQLException {

        consulta = "UPDATE electr_COMPART SET NRO = '" + indice + "' WHERE CODART ='" + codart + "' AND CODCOM ='" + codcom + "' AND CODPROV ='" + codprov + "' ";
        stm.execute(consulta);
    }


    public ResultSet consultarParalelo(String codart) throws SQLException {

        consulta = "SELECT * FROM electr_compart WHERE CODART ='" + codart + "' AND PROCESO ='0' ORDER BY PRECIO";
        rs = stm.executeQuery(consulta);

        return rs;
    }

    public ResultSet ordenarItems(int codcli, String codvent) throws SQLException {

        consulta = "SELECT * FROM electr_CLIART WHERE CODCLI ='" + codcli + "' AND CODVENT ='" + codvent + "' ORDER BY CODART";
        rs = stm.executeQuery(consulta);

        return rs;
    }

    public ResultSet ordenarItemsC(String codprov, String codcom) throws SQLException {

        consulta = "SELECT * FROM electr_compart WHERE CODPROV ='" + codprov + "' AND CODCOM ='" + codcom + "' ORDER BY CODART";
        rs = stm.executeQuery(consulta);

        return rs;
    }

    public ResultSet getClientesTodo() throws SQLException {

        consulta = "SELECT * FROM electr_CLIENTES ORDER BY NOMCLI";
        rs = stm.executeQuery(consulta);

        return rs;
    }

    public ResultSet getClientesT() throws SQLException { // OPTIMIZADO

        consulta = "SELECT CODCLI FROM electr_CLIENTES ORDER BY NOMCLI";
        rs = stm.executeQuery(consulta);

        return rs;
    }

    public ResultSet getVendidosRango() throws SQLException {

        consulta = "SELECT * FROM electr_CLIART  ORDER BY CODVENT";
        rs = stm.executeQuery(consulta);

        return rs;
    }

    public ResultSet getventaFechaRango() throws SQLException {

        consulta = "SELECT CODVENT FROM electr_RESUMEN WHERE FEC_VENTA BETWEEN '2016-01-01' AND '2017-09-09'";
        rs = stm.executeQuery(consulta);
        //  System.out.println(consulta);

        return rs;
    }

    public ResultSet getventaValidoPRO(String codvent) throws SQLException {

        int aux = Integer.valueOf(codvent.substring(1, codvent.length()));


        consulta = "SELECT electr_sec_ventas.ESTADO,electr_sec_ventas.VALIDO,electr_sec_ventas.terminado, electr_resumen.ESTADO , electr_resumen.PAGO , electr_resumen.PROCESO FROM electr_SEC_VENTAs INNER JOIN electr_resumen ON electr_resumen.CODVENT ='" + codvent + "' WHERE electr_sec_ventas.CODVENT = '" + aux + "' ";
        rs = stm.executeQuery(consulta);
        // System.out.println(consulta);

        return rs;
    }

    public ResultSet getventaValido(String codvent) throws SQLException {

        consulta = "SELECT ESTADO,CODCLI FROM electr_resumen WHERE  CODVENT ='" + codvent + "' ";
        rs = stm.executeQuery(consulta);
        //  System.out.println(consulta);

        return rs;
    }

    public void actualizarSaldos(String codcli, Double debe, Double haber) throws SQLException {
        consulta = "UPDATE electr_CLIENTES SET DEBE = '" + debe + "' , HABER = '" + haber + "' WHERE CODCLI ='" + codcli + "' ";
        stm.execute(consulta);
    }

    public void actualizarUsuario(String nombre, String apellido, String telefono, String email, String fentrada, String ci, String celular, String fnacimiento, String direccion, String sangre, String sexo, String nacionalidad, String afp, String seguro, String foto) throws SQLException {
        consulta = " UPDATE `electr_usuario` SET `nombre` = '" + nombre + "', `apellido` = '" + apellido + "', `telefono` = '" + telefono + "', `email` = '" + email + "', `fecha_entrada` = '" + fentrada + "', `CI` = '" + ci + "', `celular` = '" + celular + "', `fecha_nacimiento` = '" + fnacimiento + "', `direccion` = '" + direccion + "',  `tipo_sangre` = '" + sangre + "', `sexo` = '" + sexo + "', `nacionalidad` = '" + nacionalidad + "', `afp` = '" + afp + "', `seguro` = '" + seguro + "', `foto` = '" + foto + "' WHERE `electr_usuario`.`cod_usuario` = 2 ";
        stm.execute(consulta);
    }


    public void actualizarClienteServer(String nomcli, String direccion, String celular, String telefono, int codcli) throws SQLException {
        consulta = "UPDATE `electr_clientes` SET `NOMCLI` = '" + nomcli + "', `DIRECCION` = '" + direccion + "', `CELULAR` = '" + celular + "', `TELEFONO` = '" + telefono + "' WHERE `electr_clientes`.`CODCLI` = '" + codcli + "'";
        stm.execute(consulta);

    }

    public void actualizarRanking(String codart, int rank) throws SQLException {
        consulta = "UPDATE electr_EXIS SET RANK = '" + rank + "' WHERE CODART ='" + codart + "' ";
        stm.execute(consulta);

    }

    public ResultSet getGastos() throws SQLException {

        consulta = "SELECT * FROM electr_gastos ORDER BY nombre";
        rs = stm.executeQuery(consulta);

        return rs;
    }

    public ResultSet buscarVar(String tabla, String cod) throws SQLException {
        consulta = "SELECT * FROM " + tabla + " WHERE CODART = '" + cod + "'";

        rs = stm.executeQuery(consulta);


        return rs;
    }

    public String buscarUsuario(int id) throws SQLException {
        String res = "";
        consulta = "SELECT * FROM electr_usuario WHERE cod_usuario = '" + id + "'";
        rs = stm.executeQuery(consulta);
        while (rs.next())
            res = rs.getString("nombre") + " " + rs.getString("apellido");

        return res;
    }

    public void insertarResumen(String codCompra, String prov_buscado, Timestamp date, Double cambio, Double camExt, int lugGar, double num_total, String num_literal_bs, int responsable) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    public String test() {
        return "All Ok..!!!";
    }

    public static Stream<ResultSet> stream(ResultSet rs) {
        //    Parameters.checkNull(rs, "rs");
//
        return StreamSupport.stream(Spliterators.spliteratorUnknownSize(new ResultSetIterator(rs), 0), false);
    }

}
