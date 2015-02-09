package com.epam.testapp.service;

import com.epam.testapp.database.dao.INewsDao;
import com.epam.testapp.database.exception.DaoException;
import com.epam.testapp.model.News;
import java.util.List;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class NewsService implements INewsService {
    private INewsDao newsDao;
    
    @Override
    public Integer save(News news) throws DaoException {
        Integer id = news.getId();
        if (id != null && id > 0){
            getNewsDao().update(news);
        } else {
            id = getNewsDao().insert(news);
        }
        return id;
    }
    
    @Override
    public void remove(List<News> news) throws DaoException {
        getNewsDao().delete(news);
    }
    
    
    @Override
    public List getList() throws DaoException {
        return getNewsDao().selectAll();
    }

    @Override
    public News fetchById(News news) throws DaoException {
        return getNewsDao().fetchById(news);
    }

    /**
     * @return the newsDao
     */
    public INewsDao getNewsDao() {
        return newsDao;
    }

    /**
     * @param newsDao the newsDao to set
     */
    public void setNewsDao(INewsDao newsDao) {
        this.newsDao = newsDao;
    }
    
}
