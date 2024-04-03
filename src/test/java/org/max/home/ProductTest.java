package org.max.home;

import org.hibernate.query.Query;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class ProductTest extends AbstractTest{

    @Test
    void getProducts_whenValid_shouldReturn() throws SQLException {
        //given
        String sql = "SELECT * FROM products";
        Statement stmt  = getConnection().createStatement();
        int countTableSize = 0;
        //when
        ResultSet rs = stmt.executeQuery(sql);
        while (rs.next()) {
            countTableSize++;
        }
        final Query query = getSession().createSQLQuery(sql).addEntity(ProductsEntity.class);
        //then
        Assertions.assertEquals(10, countTableSize);
        Assertions.assertEquals(10, query.list().size());
    }

    @ParameterizedTest
    @CsvSource({"GOJIRA ROLL", "VIVA LAS VEGAS ROLL"})
    void getProducts_whenValid_shouldReturn(String menuName) throws SQLException {
        //given
        String sql = "SELECT * FROM products WHERE menu_name='" + menuName + "'";
        Statement stmt  = getConnection().createStatement();
        String nameString = "";
        //when
        ResultSet rs = stmt.executeQuery(sql);
        while (rs.next()) {
            nameString = rs.getString(2);
        }
        //then
        Assertions.assertEquals(menuName, nameString);
    }



}
