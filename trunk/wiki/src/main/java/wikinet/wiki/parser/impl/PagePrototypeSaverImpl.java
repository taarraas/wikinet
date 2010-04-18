package wikinet.wiki.parser.impl;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import wikinet.db.model.Locale;
import wikinet.wiki.dao.CategoryDao;
import wikinet.wiki.dao.LinkedPageDao;
import wikinet.wiki.dao.LocalizedPageDao;
import wikinet.wiki.dao.PageDao;
import wikinet.wiki.domain.Category;
import wikinet.wiki.domain.LinkedPage;
import wikinet.wiki.domain.LocalizedPage;
import wikinet.wiki.domain.Page;
import wikinet.wiki.parser.PagePrototypeSaver;
import wikinet.wiki.parser.prototype.PagePrototype;
import wikinet.wiki.parser.prototype.RedirectPagePrototype;
import wikinet.wiki.parser.prototype.UniquePagePrototype;

import java.util.Map;

/**
 * @author shyiko
 * @since Apr 16, 2010
 */
public class PagePrototypeSaverImpl implements PagePrototypeSaver {

    private static Logger logger = Logger.getLogger(PagePrototypeSaverImpl.class);

    @Autowired
    private PageDao pageDao;

    @Autowired
    private CategoryDao categoryDao;

    @Autowired
    private LocalizedPageDao localizedPageDao;

    @Autowired
    private LinkedPageDao linkedPageDao;

    public void setPageDao(PageDao pageDao) {
        this.pageDao = pageDao;
    }

    public void setCategoryDao(CategoryDao categoryDao) {
        this.categoryDao = categoryDao;
    }

    public void setLocalizedPageDao(LocalizedPageDao localizedPageDao) {
        this.localizedPageDao = localizedPageDao;
    }

    public void setLinkedPageDao(LinkedPageDao linkedPageDao) {
        this.linkedPageDao = linkedPageDao;
    }

    @Override
    public void save(PagePrototype pagePrototype) {
        if (pagePrototype instanceof RedirectPagePrototype) {
            RedirectPagePrototype pp = (RedirectPagePrototype) pagePrototype;
            PagePrototype rp = pp.getRedirectedPage();
            if (!validate(pp) || !validate(rp))
                return;
            Page thisPage = pageDao.createIfNotExist(pp.getWord(), pp.getDisambiguation());
            Page redirectedPage = pageDao.createIfNotExist(rp.getWord(), rp.getDisambiguation());
            pageDao.addRedirect(redirectedPage, thisPage);
        } else
            if (pagePrototype instanceof UniquePagePrototype) {
                UniquePagePrototype pp = (UniquePagePrototype) pagePrototype;
                if (!validate(pp))
                    return;
                Page page = pageDao.createIfNotExist(pp.getWord(), pp.getDisambiguation());

                for (UniquePagePrototype.Link link : pp.getLinks()) {
                    PagePrototype p = new PagePrototype(link.getText());
                    if (!validate(p))
                        continue;
                    Page linkedPage = pageDao.createIfNotExist(p.getWord(), p.getDisambiguation());
                    for (Map.Entry<Integer, Integer> entry : link.getPos().entrySet()) {
                        LinkedPage linkedPageEntity = new LinkedPage(entry.getKey(), entry.getValue(), linkedPage);
                        linkedPageDao.save(linkedPageEntity);
                        pageDao.addLinkedPage(page, linkedPageEntity);
                    }
                }

                for (String categoryName : pp.getCategories()) {
                    Category category = categoryDao.createIfNotExist(categoryName);
                    pageDao.addCategory(page, category);
                }
                for (Map.Entry<Locale, String> entry : pp.getLocalizedPages().entrySet()) {
                    LocalizedPage localizedPage = localizedPageDao.createIfNotExist(entry.getValue(), entry.getKey());
                    pageDao.addLocalizedPage(page, localizedPage);
                }

                page.setFirstParagraph(pp.getFirstParagraph());
                page.setText(pp.getText());
                pageDao.save(page);
            } else
                throw new UnsupportedOperationException();
    }

    private boolean validate(PagePrototype pagePrototype) {
        if (!(pagePrototype.getWord() != null && pagePrototype.getWord().length() < 121
                && (pagePrototype.getDisambiguation() == null || pagePrototype.getDisambiguation().length() < 121))) {
            logger.warn("Page title \"" + pagePrototype + "\" is too long.");
            return false;
        }
        return true;
    }

}
