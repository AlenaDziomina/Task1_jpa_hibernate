package com.epam.testapp.database.dao;

import com.epam.testapp.database.exception.DaoException;
import com.epam.testapp.model.News;
import java.util.List;
import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

@Repository
public class HibernateNewsDao  implements INewsDao {
    private static final Logger LOGGER = Logger.getLogger(HibernateNewsDao.class);
    private static final String NEWS_ID = "id";
    private static final String NEWS_DATE = "date";
    private SessionFactory sessionFactory;
    
    public HibernateNewsDao() {}
    
    public void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }
  
    @Override
    public Integer insert(News news) throws DaoException {
        Session session = sessionFactory.openSession();
        Integer id = (Integer) session.save(news);
        return id;
    }

    @Override
    public void update(News news) throws DaoException {
        Session session = sessionFactory.openSession();
        session.update(news);
    }

    

    @Override
    public void delete(List<News> newsList) throws DaoException {
        Session session = sessionFactory.openSession();
        Criteria criteria = session.createCriteria(News.class);
        for (News news : newsList) {
            criteria.add(Restrictions.eq(NEWS_ID, news.getId()));
        }
        List<News> list = (List<News>) criteria.list();
        for (News news : list) {
            session.delete(news);
        }
    }

    
    @Override
    public List selectAll() throws DaoException {
      
        Session session = sessionFactory.openSession();
        List<News> newsList;
        Criteria criteria = session.createCriteria(News.class);
        criteria.addOrder(Order.desc(NEWS_DATE));
        newsList = (List<News>) criteria.list();
        return newsList;
    }
 
    @Override
    public News fetchById(News news) throws DaoException {
        Session session = sessionFactory.openSession();
        news = (News) session.get(News.class, news.getId());
        return news;
    }
}

