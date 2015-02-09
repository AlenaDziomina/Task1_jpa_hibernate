package com.epam.testapp.presentation.action;

import com.epam.testapp.model.News;
import com.epam.testapp.presentation.form.NewsForm;
import com.epam.testapp.service.INewsService;
import com.epam.testapp.util.converter.DataConverter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.struts.Globals;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.actions.DispatchAction;
import org.springframework.beans.factory.annotation.Autowired;

public class NewsAction extends DispatchAction {
    private static final String FORWARD_INDEX = "index";
    private static final String HEADER_REFERER = "Referer";
    private static final String FORWARD_NEWSLIST = "newslist";
    private static final String FORWARD_NEWSEDIT = "newsedit";
    private static final String FORWARD_NEWSVIEW = "newsview";
    
    @Autowired
    private INewsService newsService;
    
    /**
     * @return the newsService
     */
    public INewsService getNewsService() {
        return newsService;
    }

    /**
     * @param newsService the newsService to set
     */
    public void setNewsService(INewsService newsService) {
        this.newsService = newsService;
    }

    public ActionForward list(ActionMapping mapping, ActionForm form,
            HttpServletRequest request, HttpServletResponse responce)
            throws Exception {
        NewsForm newsForm = (NewsForm) form;
        List list = getNewsService().getList();
        newsForm.setNewsList(list);
        newsForm.setForwardName(FORWARD_NEWSLIST);
        return mapping.findForward(FORWARD_NEWSLIST);
    }
    
    public ActionForward view(ActionMapping mapping, ActionForm form,
            HttpServletRequest request, HttpServletResponse responce)
            throws Exception {
        NewsForm newsForm = (NewsForm) form;
        News news = new News(Integer.decode(newsForm.getSelectedId()));
        news = getNewsService().fetchById(news);
        newsForm.setNewsMessage(news);
        if (news != null) {
            newsForm.setStringDate(DataConverter.toFormatString(news.getDate()));
        } else {
            ActionErrors errors = new ActionErrors();
            errors.add(ActionErrors.GLOBAL_MESSAGE, 
                    new ActionMessage("errors.news.deleted"));
            saveErrors(request, errors);
        }
        newsForm.setForwardName(FORWARD_NEWSVIEW);
        return mapping.findForward(FORWARD_NEWSVIEW);
    }
    
    public ActionForward edit(ActionMapping mapping, ActionForm form,
            HttpServletRequest request, HttpServletResponse responce)
            throws Exception {
        NewsForm newsForm = (NewsForm) form;
        News news = new News(Integer.decode(newsForm.getSelectedId()));
        news = getNewsService().fetchById(news);
        newsForm.setNewsMessage(news);
        if (news != null) {
            newsForm.setStringDate(DataConverter.toFormatString(news.getDate()));
        } else {
            ActionErrors errors = new ActionErrors();
            errors.add(ActionErrors.GLOBAL_MESSAGE, 
                    new ActionMessage("errors.news.deleted"));
            saveErrors(request, errors);
        }
        return mapping.findForward(FORWARD_NEWSEDIT);
    }
    
    public ActionForward delete(ActionMapping mapping, ActionForm form,
            HttpServletRequest request, HttpServletResponse responce)
            throws Exception {
        NewsForm newsForm = (NewsForm) form;
        String[] deletedId = newsForm.getDeletedId();
        if (deletedId != null) {
            List<News> newsList = new ArrayList();
            for(String strId : deletedId) {
                newsList.add(new News(Integer.decode(strId)));
            }
            getNewsService().remove(newsList);
            newsForm.setNewsList(getNewsService().getList());
        } else {
            ActionErrors errors = new ActionErrors();
            errors.add(ActionErrors.GLOBAL_MESSAGE, 
                    new ActionMessage("errors.confirmation"));
            saveErrors(request, errors);
        }
        return mapping.findForward(FORWARD_NEWSLIST);
    }
    
    public ActionForward cancel(ActionMapping mapping, ActionForm form,
            HttpServletRequest request, HttpServletResponse responce)
            throws Exception {
        NewsForm newsForm = (NewsForm) form;
        return mapping.findForward(newsForm.getForwardName());
    }
    
    public ActionForward save(ActionMapping mapping, ActionForm form,
            HttpServletRequest request, HttpServletResponse responce)
            throws Exception {
        NewsForm newsForm = (NewsForm) form;
        ActionErrors errors = newsForm.validate(mapping, request);
        if (!errors.isEmpty()) {
            saveErrors(request, errors);
            return mapping.findForward(FORWARD_NEWSEDIT);
        }
        newsForm.getNewsMessage().setDate(DataConverter.toSqlDate(newsForm.getStringDate()));
        newsForm.getNewsMessage().setId(getNewsService().save(newsForm.getNewsMessage()));
        newsForm.setForwardName(FORWARD_NEWSVIEW);
        return mapping.findForward(FORWARD_NEWSVIEW);
    }
    
    public ActionForward add(ActionMapping mapping, ActionForm form,
            HttpServletRequest request, HttpServletResponse responce)
            throws Exception {
        NewsForm newsForm = (NewsForm) form;
        newsForm.setNewsMessage(new News());
        newsForm.getNewsMessage().setDate(DataConverter.toSqlDate());
        newsForm.setStringDate(DataConverter.toFormatString(null));
        newsForm.setForwardName(FORWARD_INDEX);
        return mapping.findForward(FORWARD_NEWSEDIT);
    }

    public ActionForward locale(ActionMapping mapping, ActionForm form,
            HttpServletRequest request, HttpServletResponse responce)
            throws Exception {
        NewsForm newsForm = (NewsForm) form;
        Locale locale = new Locale(newsForm.getLocale(), newsForm.getLocale());
        request.getSession().setAttribute(Globals.LOCALE_KEY, locale);
        String path = request.getHeader(HEADER_REFERER);
        return new ActionForward(path, true);
    }
}
