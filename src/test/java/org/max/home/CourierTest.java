package org.max.home;

import org.hibernate.Session;
import org.hibernate.query.Query;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.max.seminar.CurrentEntity;

import javax.persistence.PersistenceException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Optional;

//нигде в тестаз не ипользовали createQuery 
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    //название класса не совпадает с названием файла
public class CourierInfoEntity extends AbstractTest{

    @Test
    @Order(1)

    void getCourier_whenValid_shouldReturn() throws SQLException {
        //given
        //таблица courier_info
        String sql = "SELECT * FROM courierInfo WHERE first_name='Bob'";
        Statement stmt  = getConnection().createStatement();
        int countTableSize = 0;
        //when
        ResultSet rs = stmt.executeQuery(sql);
        while (rs.next()) {
            countTableSize++;
        }
        //таблица courier_info
        final Query query = getSession().createSQLQuery("SELECT * FROM courier").addEntity(CourierInfoEntity.class);
        //then
        Assertions.assertEquals(8, countTableSize);
        Assertions.assertEquals(15, query.list().size());
    }

    @Order(2)
    @ParameterizedTest
    @CsvSource({"John, Rython", "Kate, Looran", "Bob, Kolaris"})
    void getCourierById_whenValid_shouldReturn(String name, String lastName) throws SQLException {
        //given
        //таблица courier_info
        String sql = "SELECT * FROM courier WHERE first_name='" + name + "'";
        Statement stmt  = getConnection().createStatement();
        String nameString = "";
        //when
        ResultSet rs = stmt.executeQuery(sql);
        while (rs.next()) {
            nameString = rs.getString(2);
        }
        //then
        Assertions.assertEquals(lastName, nameString);
    }

    @Test
    @Order(3)
    void addCourier_whenValid_shouldSave() {
        //given
        CourierInfoEntity entity = new CourierInfoEntity();
        entity.setCourierId((short) 2);
        entity.setDeliveryType("car");
        entity.setFirstName("Kate");
        entity.setLastName("Looran");
        entity.setPhoneNumber("+ 7 960743 0146");
        //when
        Session session = getSession();
        session.beginTransaction();
        session.persist(entity);
        session.getTransaction().commit();

        //таблица courier_info
        final Query query = getSession()
                .createSQLQuery("SELECT * FROM courier WHERE courier_id="+2).addEntity(CourierInfoEntity.class);
        //почему тут CustomersEntity ?
        CourierInfoEntity creditEntity = (CustomersEntity) query.uniqueResult();
        //then
        Assertions.assertNotNull(creditEntity);
        Assertions.assertEquals("10", creditEntity.getDeliveryType());
    }

    @Test
    @Order(4)
    void deleteCourier_whenValid_shouldDelete() {
        //given
        //таблица courier_info
        final Query query = getSession()
                .createSQLQuery("SELECT * FROM courier WHERE courier_id=" + 2).addEntity(CourierInfoEntity.class);
        Optional<CourierInfoEntity> courierEntity = (Optional<CourierInfoEntity>) query.uniqueResultOptional();
        Assumptions.assumeTrue(courierEntity.isPresent());
        //when
        Session session = getSession();
        session.beginTransaction();
        session.delete(courierEntity.get());
        session.getTransaction().commit();
        //then
        //таблица courier_info
        final Query queryAfterDelete = getSession()
                .createSQLQuery("SELECT * FROM courierInfo WHERE courier_id=" + 16).addEntity(CourierInfoEntity.class);
        Optional<CourierInfoEntity> courierInfoEntityAfterDelete = (Optional<CourierInfoEntity>) queryAfterDelete.uniqueResultOptional();
        Assertions.assertFalse(courierInfoEntityAfterDelete.isPresent());
    }


    @Test
    @Order(5)
    void addCourierInfo_whenNotValid_shouldThrow() {
        //given
        CourierInfoEntity entity = new CourierInfoEntity();
        //when
        Session session = getSession();
        session.beginTransaction();
        session.persist(entity);
        //then
        Assertions.assertThrows(PersistenceException.class, () -> session.getTransaction().commit());
        ;
    }

}
