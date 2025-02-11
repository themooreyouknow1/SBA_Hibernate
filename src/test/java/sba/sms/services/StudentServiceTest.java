package sba.sms.services;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.junit.jupiter.api.*;
import sba.sms.models.Student;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class StudentServiceTest {
    private SessionFactory sessionFactory;
    private Session session;
    private Transaction transaction;
    private StudentService studentService;


    @BeforeEach
    public void setUp() {
        sessionFactory = new Configuration().configure().buildSessionFactory();
        session = sessionFactory.openSession();
        transaction = session.beginTransaction();
        studentService = new StudentService();
    }

    @AfterEach
    public void tearDown() {
        if (transaction != null){
            transaction.rollback();
        }
        if (session != null){
            session.close();
        }
        if (sessionFactory != null){
            sessionFactory.close();
        }
    }
    @Test
    public void testCreateStudent() {
        Student student = new Student();
        student.setEmail("starman@gmail.com");
        student.setName("David Bowie");
        student.setPassword("password");
        studentService.createStudent(student);

        Student fetchedStudent = studentService.getStudentByEmail(student.getEmail());
        System.out.println(fetchedStudent);
        assertEquals("starman@gmail.com", fetchedStudent.getEmail(), "Emails should match");
        assertEquals("David Bowie", fetchedStudent.getName(), "Name should match");
        assertEquals("password", fetchedStudent.getPassword(), "Password should match");

    }



}