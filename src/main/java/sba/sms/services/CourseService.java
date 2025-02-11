package sba.sms.services;

import org.hibernate.Hibernate;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import sba.sms.dao.CourseI;
import sba.sms.models.Course;
import sba.sms.utils.HibernateUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * CourseService is a concrete class. This class implements the
 * CourseI interface, overrides all abstract service methods and
 * provides implementation for each method.
 */
public class CourseService implements CourseI {
    Transaction tx = null;

    public void createCourse(Course course) {
        try(Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            session.save(course);
            tx.commit();
        }catch (HibernateException e) {
            if (tx != null) tx.rollback();
        }
    }

    public Course getCourseById(int courseId) {
        Course course = null;
        try(Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            course = session.get(Course.class, courseId);
            if (course == null) {
                Hibernate.initialize(course.getStudents());
            }
            tx.commit();
        }catch (HibernateException e) {
            if (tx != null) tx.rollback();
        }
        return course;
    }

    public List<Course> getAllCourses() {
        List<Course> courses = new ArrayList<>();
        try(Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            courses = session.createQuery("from Course", Course.class).list();
            for (Course course : courses) {
                Hibernate.initialize(course.getStudents());
            }
            tx.commit();
        }catch (HibernateException e) {
            if (tx != null) tx.rollback();
        }
        return courses;
    }
}
