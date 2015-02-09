package com.epam.testapp.database.dao;

import com.epam.testapp.database.connection.IConnectionPool;
import com.epam.testapp.database.dao.jdbc.IGenericQuery;
import com.epam.testapp.database.dao.jdbc.RowMapper;
import com.epam.testapp.database.exception.DaoConnectException;
import com.epam.testapp.database.exception.DaoException;
import com.epam.testapp.database.exception.DaoSqlException;
import com.epam.testapp.model.News;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import org.apache.log4j.Logger;

public class JdbcNewsDao implements INewsDao{
    private static final String DB_NEWS_ID = "NEWS_ID";
    private static final String DB_NEWS_TITLE = "TITLE";
    private static final String DB_NEWS_DATE = "NEWS_DATE";
    private static final String DB_NEWS_BRIEF = "BRIEF";
    private static final String DB_NEWS_CONTENT = "CONTENT";
    private static final String SELECT_ALL_QUERY = "SELECT * FROM NEWS ORDER BY NEWS_DATE DESC";
    private static final String SELECT_BY_ID_QUERY = "SELECT * FROM NEWS WHERE NEWS_ID = ?";
    private static final String UPDATE_QUERY = "UPDATE NEWS set TITLE= ?, NEWS_DATE = ?, BRIEF = ?, CONTENT = ? where NEWS_ID = ?";
    private static final String INSERT_QUERY = "INSERT INTO NEWS (TITLE, NEWS_DATE, BRIEF, CONTENT) VALUES (?, ?, ?, ?)";
    private static final String DELETE_QUERY = "Delete from NEWS where NEWS_ID = ?";
    private static final Logger LOGGER = Logger.getLogger(JdbcNewsDao.class);
    private static final String ERROR_CONNECT_RETURN = "Return connection is not done.";
    private IConnectionPool connectionPool;
    private IGenericQuery genericQuery;
    private final RowMapper rowMapper = (RowMapper) new RowMapper() {

        public Object mapRow(ResultSet rs) throws SQLException {
            News bean = new News();
            bean.setId(rs.getInt(DB_NEWS_ID));
            bean.setTitle(rs.getString(DB_NEWS_TITLE));
            bean.setDate(rs.getDate(DB_NEWS_DATE));
            bean.setBrief(rs.getString(DB_NEWS_BRIEF));
            bean.setContent(rs.getString(DB_NEWS_CONTENT));
            return bean;
        }
    };
        
    @Override
    public Integer insert(News news) throws DaoException {
        Connection connection = null;
        Integer id = null;
        try {
            connection = getConnectionPool().getConnection();
            Object[] params = new Object[]{news.getTitle(), news.getDate(),
                news.getBrief(), news.getContent()};
            id = getGenericQuery().saveQuery(INSERT_QUERY, DB_NEWS_ID,
                    params, connection);
        } catch (DaoSqlException | DaoConnectException ex) {
            throw new DaoException(ex);
        } finally {
            if (connection != null) {
                try {
                    connectionPool.returnConnection(connection);
                } catch (DaoConnectException ex) {
                    LOGGER.error(ERROR_CONNECT_RETURN, ex);
                }
            }
        }
        return id;
    }
    
    @Override
    public void update(News news) throws DaoException {
        Connection connection = null;
        try {
            connection = getConnectionPool().getConnection();
            Object[] params = new Object[]{news.getTitle(), news.getDate(),
                    news.getBrief(), news.getContent(), news.getId()};
            getGenericQuery().updateQuery(UPDATE_QUERY, params, connection);
        } catch (DaoSqlException | DaoConnectException ex) {
            throw new DaoException(ex);
        } finally {
            if (connection != null) {
                try {
                    connectionPool.returnConnection(connection);
                } catch (DaoConnectException ex) {
                    LOGGER.error(ERROR_CONNECT_RETURN, ex);
                }
            }
        }
    }
    
    @Override
    public void delete(List<News> newsList) throws DaoException {
        Connection connection = null;
        try {
            connection = getConnectionPool().getConnection();
            Object[] params = new Object[newsList.size()];
            int i = 0;
            for (News news : newsList){
                params[i++] = news.getId();
            }
            getGenericQuery().deleteQuery(DELETE_QUERY, params, connection);       
        } catch (DaoSqlException | DaoConnectException ex) {
            throw new DaoException(ex);
        } finally {
            if (connection != null) {
                try {
                    connectionPool.returnConnection(connection);
                } catch (DaoConnectException ex) {
                    LOGGER.error(ERROR_CONNECT_RETURN, ex);
                }
            }
        }
    }
    
    @Override
    public List selectAll() throws DaoException {
        List<News> newsList;
        Connection connection = null;
        try {
            connection = getConnectionPool().getConnection();
            newsList = getGenericQuery().loadQuery(SELECT_ALL_QUERY, null, 
                    connection, rowMapper);
        } catch (DaoSqlException | DaoConnectException ex) {
            throw new DaoException(ex);
        } finally {
            if (connection != null) {
                try {
                    connectionPool.returnConnection(connection);
                } catch (DaoConnectException ex) {
                    LOGGER.error(ERROR_CONNECT_RETURN, ex);
                }
            }
        }
        return newsList;
    }

    
    

    

    @Override
    public News fetchById(News news) throws DaoException {
        Connection connection = null;
        try {
            connection = getConnectionPool().getConnection();
            List<News> newsList = getGenericQuery().loadQuery(SELECT_BY_ID_QUERY, new Object[]{news.getId()}, 
                    connection, rowMapper);
            if (newsList != null && !newsList.isEmpty()) {
                news = newsList.get(0);
            } else {
                news = null;
            }
        } catch (DaoSqlException | DaoConnectException ex) {
            throw new DaoException(ex);
        } finally {
            if (connection != null) {
                try {
                    connectionPool.returnConnection(connection);
                } catch (DaoConnectException ex) {
                    LOGGER.error(ERROR_CONNECT_RETURN, ex);
                }
            }
        }
        return news;
    }

    
    
    

    /**
     * @return the connectionPool
     */
    public IConnectionPool getConnectionPool() {
        return connectionPool;
    }

    /**
     * @param connectionPool the connectionPool to set
     */
    public void setConnectionPool(IConnectionPool connectionPool) {
        this.connectionPool = connectionPool;
    }

    /**
     * @return the genericQuery
     */
    public IGenericQuery getGenericQuery() {
        return genericQuery;
    }

    /**
     * @param genericQuery the genericQuery to set
     */
    public void setGenericQuery(IGenericQuery genericQuery) {
        this.genericQuery = genericQuery;
    }

    
}

    

