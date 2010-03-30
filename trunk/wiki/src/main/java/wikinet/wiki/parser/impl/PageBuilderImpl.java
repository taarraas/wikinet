package wikinet.wiki.parser.impl;

import org.hibernate.lob.ClobImpl;
import org.springframework.beans.factory.annotation.Autowired;
import wikinet.wiki.dao.CategoryDao;
import wikinet.wiki.dao.LocalizedPageDao;
import wikinet.wiki.dao.PageDao;
import wikinet.wiki.domain.Category;
import wikinet.wiki.domain.LocalizedPage;
import wikinet.wiki.domain.Page;
import wikinet.wiki.parser.PageBuilder;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author shyiko
 * @since Mar 30, 2010
 */
public class PageBuilderImpl implements PageBuilder {

    private PageDao pageDao;
    private CategoryDao categoryDao;
    private LocalizedPageDao localizedPageDao;

    private static final Pattern patternRed = Pattern.compile("[{][{](for|redirect).*?[}][}]");

    @Autowired
    public PageBuilderImpl(PageDao pageDao, CategoryDao categoryDao) {
        this.pageDao = pageDao;
    }

    @Override
    public void importPage(String title, String text) {
        title = title.trim();

        Page page = findOrCreatePage(title);
        for (String redirect : findRedirections(text)) {
            page.addRedirect(findOrCreatePage(redirect));
        }

        for (String footer : findFooters(text)) {
            page.addRedirect(findOrCreatePage(footer));
        }

        for (String category : findCategories(text)) {
            page.addCategory(findOrCreateCategory(category));
        }

        //TODO:
/*
        for (String redirect : findRedirections(text)) {
            page.addRedirect(findOrCreate(redirect));
        }
*/

        page.setText(new ClobImpl(getClean(text)));
        pageDao.save(page);
    }

    private Page findOrCreatePage(String title) {
        Page page = pageDao.findById(title);
        if (page == null) {
            page = new Page(title);
            pageDao.save(page);
        }
        return page;
    }

    private Category findOrCreateCategory(String title) {
        Category category = categoryDao.findById(title);
        if (category == null) {
            category = new Category(title);
            categoryDao.save(category);
        }
        return category;
    }

/*
    private LocalizedPage findOrCreateLocalizedPage(String title) {
        LocalizedPage localizedPage = localizedPageDao.findById(title);
        if (localizedPage == null) {
            localizedPage = new LocalizedPage(title);
            categoryDao.save(localizedPage);
        }
        return localizedPage;
    }
*/

    private Set<String> findRedirections(String text) {
        Set<String> set = new HashSet<String>();
        Matcher matcher = patternRed.matcher(text);
        while (matcher.find()) {
            String match = matcher.group();
            match = match.substring(match.lastIndexOf("|"));
            match = match.substring(1, match.indexOf("}}"));
            match = match.trim();
            set.add(match);
        }
        return set;
    }

    private Set<String> findFooters(String text) {
        throw new UnsupportedOperationException();
    }

    private Set<String> findCategories(String text) {
        throw new UnsupportedOperationException();
    }

    private Set<String> findLocalizedPages(String text) {
        throw new UnsupportedOperationException();
    }

    private String getClean(String text) {
        // remove all {{...}}
        text = text.replaceAll("[{][{].*?[}][}]", "");
        // remove all &lt;ref.../ref&gt;
        text = text.replaceAll("[&lt;ref].*?[/ref&gt;]", "");
        // == paragraph title ==
        text = text.replaceAll("[=][=].*?[=][=]", "");
        // <!-- comment -->
        text = text.replaceAll("[&lt;!--].*?[--&gt;]", "");
        
        return text;
    }

}
