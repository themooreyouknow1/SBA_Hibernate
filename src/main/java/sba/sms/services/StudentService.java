package sba.sms.services;

import lombok.extern.java.Log;
import org.hibernate.Hibernate;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.NativeQuery;
import org.hibernate.query.Query;
import sba.sms.dao.StudentI;
import sba.sms.models.Course;
import sba.sms.models.Student;
import sba.sms.utils.HibernateUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * StudentService is a concrete class. This class implements the
 * StudentI interface, overrides all abstract service methods and
 * provides implementation for each method. Lombok @Log used to
 * generate a logger file.
 */

public class StudentService implements StudentI {
    Transaction tx = null;

    public List<Student> getAllStudents() {
        List<Student> students = new ArrayList<>();
        try(Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            students = session.createQuery("from Student",Student.class).list();
            for (Student s : students) {
                Hibernate.initialize(s.getCourses());
            }
            tx.commit();
        } catch (HibernateException e) {
            if (tx != null) tx.rollback();
        }
        return students;
    }

    public void createStudent(Student student) {
        try(Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            session.persist(student);
            tx.commit();
        }catch (HibernateException e) {
            if (tx != null) tx.rollback();
        }
    }

    public Student getStudentByEmail(String email) {
        Student student = null;
        try(Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            student = session.createQuery("from Student where email = :email", Student.class).setParameter("email", email).uniqueResult();
            if (student != null) {
                Hibernate.initialize(student.getCourses());
            }
            tx.commit();
        }catch (HibernateException e) {
            if (tx != null) tx.rollback();
        }
        return student;
    }

    public boolean validateStudent(String email, String password) {
        boolean valid = false;
        try(Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            Student student = session.createQuery("from Student where email = :email", Student.class).setParameter("email", email).uniqueResult();
            if (student != null && student.getPassword().equals(password)) {
                valid = true;
            }
            tx.commit();
        }catch (HibernateException e) {
            if (tx != null) tx.rollback();
        }
        return valid;
    }

    //register a course to a student (collection to prevent duplication),
    // also handle commit,rollback, and exceptions
    public void registerStudentToCourse(String email, int courseId) {
        try(Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            Student student = session.createQuery("from Student where email = :email", Student.class).setParameter("email", email).uniqueResult();
            Course course = session.get(Course.class, courseId);
            if (student != null && course != null) {
                student.getCourses().add(course);
                session.update(student);
                tx.commit();
            }else{
                if (student == null) {
                    System.out.println("Student not found");
                }
                if (course == null) {
                    System.out.println("Course not found");
                }
            }
        }catch (HibernateException e) {
            if (tx != null) tx.rollback();
        }

    }

    public List<Course> getStudentCourses(String email) {
        List<Course> courses = new ArrayList<>();
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            courses = session.createQuery(
                    "SELECT c FROM Course c " + "JOIN FETCH c.students s " + "WHERE s.email = :email", Course.class)
                    .setParameter("email", email)
                    .getResultList();
            tx.commit();
        } catch (HibernateException e) {
            if (tx != null) tx.rollback();
        }
        return courses;
    }
}
