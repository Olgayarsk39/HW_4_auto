package org.max.home;

import org.hibernate.Session;
import org.hibernate.query.Query;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import javax.persistence.PersistenceException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Optional;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class CourierTest extends AbstractTest{

    @Test
    @Order(1)
    void getCourier_whenValid_shouldReturn() throws SQLException {
        //given
        String sql = "SELECT * FROM courier_info WHERE delivery_type='car'";
        Statement stmt  = getConnection().createStatement();
        int countTableSize = 0;
        //when
        ResultSet rs = stmt.executeQuery(sql);
        while (rs.next()) {
            countTableSize++;
        }
        final Query query = getSession().createSQLQuery("SELECT * FROM courier_info").addEntity(CourierInfoEntity.class);
        //then
        Assertions.assertEquals(3, countTableSize);
        Assertions.assertEquals(4, query.list().size());
    }

    @Order(2)
    @ParameterizedTest
    @CsvSource({"John, Rython", "Kate, Looran"})
    void getCourier_whenValid_shouldReturn(String firstName, String lastName) throws SQLException {
        //given
        String sql = "SELECT * FROM courier_info WHERE first_name='" + firstName + "'";
        Statement stmt  = getConnection().createStatement();
        String nameString = "";
        String name ="";
        //when
        ResultSet rs = stmt.executeQuery(sql);
        while (rs.next()) {
            nameString = rs.getString(3);
            name = rs.getString("last_name");

        }
        //then
        Assertions.assertEquals(lastName, nameString);
        Assertions.assertEquals(lastName, name);

    }

    @Test
    @Order(3)
    void addCourier_whenValid_shouldSave() {
        //given
        CourierInfoEntity entity = new CourierInfoEntity();
        entity.setCourierId((short) 5);
        entity.setFirstName("John");
        entity.setLastName("Smit");
        entity.setPhoneNumber("+ 7 960 655 0000");
        entity.setDeliveryType("foot");
        //when
        Session session = getSession();
        session.beginTransaction();
        session.persist(entity);
        session.getTransaction().commit();

        final Query query = getSession()
                .createSQLQuery("SELECT * FROM courier_info WHERE courier_id=" + 5).addEntity(CourierInfoEntity.class);
        CourierInfoEntity courierInfo = (CourierInfoEntity) query.uniqueResult();
        //then
        Assertions.assertNotNull(courierInfo);
        Assertions.assertEquals("Smit", courierInfo.getLastName());
    }

    @Test
    @Order(4)
    void deleteCourier_whenValid_shouldDelete() {
        //given
        final Query query = getSession()
                .createSQLQuery("SELECT * FROM courier_info WHERE courier_id=" + 5).addEntity(CourierInfoEntity.class);

        Optional<CourierInfoEntity> courierInfoEntity = (Optional<CourierInfoEntity>) query.uniqueResultOptional();
        Assumptions.assumeTrue(courierInfoEntity.isPresent());
        //when
        Session session = getSession();
        session.beginTransaction();
        session.delete(courierInfoEntity.get());
        session.getTransaction().commit();
        //then
        final Query queryAfterDelete = getSession()
                .createSQLQuery("SELECT * FROM courier_info WHERE courier_id=" + 5).addEntity(CourierInfoEntity.class);
        Optional<CourierInfoEntity> courierEntityAfterDelete = (Optional<CourierInfoEntity>) queryAfterDelete.uniqueResultOptional();
        Assertions.assertFalse(courierEntityAfterDelete.isPresent());
    }

    @Test
    @Order(5)
    void addCourier_whenNotValid_shouldThrow() {
        //given
        CourierInfoEntity entity = new CourierInfoEntity();
        entity.setCourierId((short) 5);
        entity.setFirstName("John");
        //when
        Session session = getSession();
        session.beginTransaction();
        session.persist(entity);
        //then
        Assertions.assertThrows(PersistenceException.class, () -> session.getTransaction().commit());
        ;
    }

}
