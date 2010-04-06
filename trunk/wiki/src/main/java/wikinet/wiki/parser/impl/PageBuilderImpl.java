package wikinet.wiki.parser.impl;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import wikinet.db.model.Locale;
import wikinet.wiki.dao.CategoryDao;
import wikinet.wiki.dao.LocalizedPageDao;
import wikinet.wiki.dao.PageDao;
import wikinet.wiki.domain.Category;
import wikinet.wiki.domain.LinkedPage;
import wikinet.wiki.domain.LocalizedPage;
import wikinet.wiki.domain.Page;
import wikinet.wiki.parser.PageBuilder;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * todo: text = text.replaceAll("[=]{2,4}.*?[=]{2,4}", "");
 *
 * http://meta.wikimedia.org/wiki/Help:Wikitext_examples
 * @author shyiko
 * @since Mar 30, 2010
 */
public class PageBuilderImpl implements PageBuilder {

    private static Logger logger = Logger.getLogger(PageBuilderImpl.class);

    private static final Pattern NOWIKI = Pattern.compile("(<nowiki>).*?(</ ?nowiki>)");
    private static final Pattern PRE = Pattern.compile("(<pre>).*?(</ ?pre>)");

    private static final String[] NAMESPACES_TO_SKIP =
            {"Media:", "Special:", "main:", "Talk:", "User:", "User talk:", "Meta:",
            "Meta talk:", "File:", "File talk:", "MediaWiki:", "MediaWiki talk:", "Template:", "Template talk:",
            "Help:", "Help talk:", "Category talk:", "Portal:", "Portal talk:", "media:", "Image:"};


    @Autowired
    private PageDao pageDao;

    @Autowired
    private CategoryDao categoryDao;

    @Autowired
    private LocalizedPageDao localizedPageDao;
    private static final Pattern PATTERN_MATH = Pattern.compile("(<math).*?(/[ ]*math>)", Pattern.DOTALL);
    private static final Pattern PATTERN_REF = Pattern.compile("(<ref).*?(/[ ]*ref>)", Pattern.DOTALL);

    public void setPageDao(PageDao pageDao) {
        this.pageDao = pageDao;
    }

    public void setCategoryDao(CategoryDao categoryDao) {
        this.categoryDao = categoryDao;
    }

    public void setLocalizedPageDao(LocalizedPageDao localizedPageDao) {
        this.localizedPageDao = localizedPageDao;
    }

    @Override
    public void importPage(String title, String text) {
        title = title.trim();

        PagePrototype pagePrototype = new PagePrototype(title);

        // check if text is just a redirect to the other page
        if (text.indexOf("#REDIRECT [[") != -1) {
            String redirectedPageTitle = text.substring(text.indexOf("[[") + 2, text.lastIndexOf("]]")).trim();
            Page thisPage = findOrCreatePage(title);
            Page redirectedPage = findOrCreatePage(redirectedPageTitle);
            redirectedPage.addRedirect(thisPage);
            pageDao.save(redirectedPage);
            return;
        }

        text = processText(pagePrototype, text);

        Page page = findOrCreatePage(title);
        for (Link link : pagePrototype.getLinkPrototypes().values()) {
            // todo: change LinkedPage class definition to <id,page,map<pos,length>
            Page linkedPage = findOrCreatePage(link.getText());
            for (Map.Entry<Integer, Integer> entry : link.getPos().entrySet()) {
                page.addLinkedPage(new LinkedPage(entry.getKey(), entry.getValue(), linkedPage));
            }
        }
        for (String category : pagePrototype.getCategories()) {
            page.addCategory(findOrCreateCategory(category));
        }
        for (Map.Entry<Locale, String> entry : pagePrototype.getLocalizedPages().entrySet()) {
            page.addLocalizedPage(findOrCreateLocalizedPage(entry.getValue(), entry.getKey()));
        }
        page.setText(text);
        pageDao.save(page);
    }

    public String processText(final PagePrototype pagePrototype, String text) {
        StringBuilder sb = new StringBuilder();
        Matcher nowikiMatcher = NOWIKI.matcher(text);
        Matcher preMatcher = PRE.matcher(text);
        int index = 0;
        do {
            int nowikipos = nowikiMatcher.find(index) ? nowikiMatcher.start() : -1;
            int prepos = preMatcher.find(index) ? preMatcher.start() : -1;
            if (nowikipos == -1 && prepos == -1) {
                if (index == 0)
                    return processTextBlock(pagePrototype, text);
                break;
            }
            int startGroup;
            int endGroup;
            String group;
            if ((nowikipos < prepos && nowikipos != -1) || (prepos == -1)) {
                startGroup = nowikipos;
                endGroup = nowikiMatcher.end();
                group = nowikiMatcher.group();
            } else {
                startGroup = prepos;
                endGroup = preMatcher.end();
                group = preMatcher.group();
            }
            if (startGroup < index) {
                throw new Error("Couldn't parse page \"" + pagePrototype.getTitle() + "\".");
            }

            String block = text.substring(index, startGroup);
            block = processTextBlock(pagePrototype, block);
            sb.append(block);
            sb.append(group.substring(group.indexOf(">") + 1,
                      group.lastIndexOf("<")));
            index = endGroup;
        } while(true);
        sb.append(processTextBlock(pagePrototype, text.substring(index)));
        return sb.toString();
    }

    public String processTextBlock(final PagePrototype pagePrototype, String block) {
        // removing all mathematical formulas
        block = PATTERN_MATH.matcher(block).replaceAll("");
        // removing basic text formatting
        block = block.replaceAll("[']{2,5}", "");
        block = block.replaceAll("[~]{3,5}", "");
        block = block.replaceAll("[-]{4}", "");
        // replacing html entities
        block = block.replaceAll("&lt;", "<");
        block = block.replaceAll("&gt;", ">");
        block = block.replaceAll("&apos;", "'");
        block = block.replaceAll("&quot;", "\"");
        block = block.replaceAll("&amp;", "&");
        // removing unusual constructions
        block = PATTERN_REF.matcher(block).replaceAll("");
        // removing all html tags
        block = Pattern.compile("(<).*?(>)", Pattern.DOTALL).matcher(block).replaceAll("");

        // todo: templates

        block = processCurlyBrackets(pagePrototype, block);
        block = processSquareBrackets(pagePrototype, block);

        return block;
    }

    public String processCurlyBrackets(final PagePrototype pagePrototype, String text) {
        /*
        todo:
        {{for|the anthology of anarchist writings|Anarchism: A Documentary History of Libertarian Ideas}}
        {{redirect|Anarchist|the fictional character|Anarchist (comics)}}
        {{redirect|Anarchists}}
        */
        StringBuilder sb = new StringBuilder(text);
        int start = 0, end;
        while ((start = sb.indexOf("{", start)) != -1) {
            end = sb.indexOf("}", start + 1);
            if (end == -1)
                break;
            String str = sb.substring(start + 1, end + 1).trim();
            if (str.startsWith("{") && str.endsWith("}")) {
                //str = str.substring(1, str.length() - 1).trim();
                end++;
                sb.replace(start, end + 1, "");
                continue;
            }
            // todo: table
            sb.replace(start, end + 1, "");
        }
        return sb.toString();
    }

    /**
     * @param text Text outside &lt;nowiki&gt; tag
     * @return text without square brackets compounds
     */
    public String processSquareBrackets(final PagePrototype pagePrototype, String text) {
        StringBuilder sb = new StringBuilder(text);
        int start = 0, end;
        root_cycle:
        while ((start = sb.indexOf("[", start)) != -1) {
            end = sb.indexOf("]", start + 1);
            if (end == -1)
                break;
            String str = sb.substring(start + 1, end + 1).trim();
            // external link
            if (str.matches("(http://|https://|telnet://|gopher://|file://|wais://|ftp://|mailto:|news:).*")) {
                int spaceIndex = str.indexOf(" ");
                String replacement = "";
                if (spaceIndex > 0) {
                    replacement = str.substring(spaceIndex + 1, str.length() - 1).trim();
                }
                sb.replace(start, end + 1, replacement);
                start += replacement.length();
                continue;
            }
            if (!str.startsWith("[") || !str.endsWith("]")) {
                logger.warn("Page \"" + pagePrototype.getTitle() + "\". Unrecognizable square brackets structure \"[" + str + "]\"");
                start = end;
                continue;
            }
            str = str.substring(1, str.length() - 1).trim();
            end++;

            for (Locale locale : Locale.values()) {
                if (str.startsWith(locale.getText() + ":")) {
                    pagePrototype.addLocalizedPages(locale, str.substring(locale.getText().length() + 1));
                    sb.replace(start, end + 1, "");
                    continue root_cycle;
                }
            }

            String prefix = "";
            if (str.startsWith(":"))
                prefix = ":";
            for (String namespacePrefix : NAMESPACES_TO_SKIP) {
                String pr = prefix + namespacePrefix;
                if (str.startsWith(pr)) {
                    str = str.substring(pr.length());
                    String replacement = "";
                    int pipePos = str.lastIndexOf("|");
                    if (pipePos != -1) {
                        replacement = str.substring(pipePos + 1).trim();
                    } else
                        pipePos = str.length();
                    if (replacement.isEmpty()) {
                        replacement = str.substring(0, pipePos);        
                    }
                    sb.replace(start, end + 1, replacement);
                    start += replacement.length();
                    continue root_cycle;
                }
            }

            // category
            if (str.startsWith("Category:")) {
                String category = str.substring(9).trim();
                pagePrototype.addCategory(category);
                sb.replace(start, end + 1, "");
                continue;
            }

            // link on category
            if (str.startsWith(":Category:")) {
                String category = str.substring(10).trim();
                sb.replace(start, end + 1, category);
                start += category.length();
                continue;
            }

            // date
            // [[1969]]-[[07-20]]
            if (str.matches("(\\d){4}")) {
                String space = sb.substring(end + 1, end + 4);
                if (space.equals("-[[")) {
                    int dateStart = end + 2;
                    int dateEnd = sb.indexOf("]]", end + 1);
                    if (dateEnd != -1) {
                        String date = sb.substring(dateStart + 2, dateEnd);
                        if (date.matches("(\\d){2}-(\\d){2}")) {
                            String replacement = str + "-" + date;
                            sb.replace(start, dateEnd + 2, replacement);
                            start += replacement.length();
                            continue;
                        }
                    }
                }
            }
            // [[July 20]], [[1969]] and [[20 July]] [[1969]]
            String allMonthes = "(January|February|March|April| May|June|July|August|September|October|November|December)";
            if (str.matches("(" + allMonthes + " (\\d){1,2})|((\\d){1,2} " + allMonthes + ")")) {
                String space = sb.substring(end + 1, end + 5);
                if (space.equals(", [[") || space.substring(0, 3).equals(" [[")) {
                    int dateStart = sb.indexOf("[[", end + 1);
                    int dateEnd = sb.indexOf("]]", end + 1);
                    if (dateEnd != -1) {
                        String date = sb.substring(dateStart + 2, dateEnd);
                        if (date.matches("(\\d){4}")) {
                            String replacement = str + " " + date;
                            sb.replace(start, dateEnd + 2, replacement);
                            start += replacement.length();
                            continue;
                        }
                    }
                }
            }
            // [[1969-07-20]]
            if (str.matches("(\\d\\d\\d\\d)-(\\d\\d)-(\\d\\d)")) {
                sb.replace(start, end + 1, str);
                start += str.length();
                continue;
            }

            // link
            String link = str;
            String linkText = str;
            boolean pipeUsed = false;
            int pipeCharPos = str.indexOf("|");
            if (pipeCharPos > 0) {
                link = str.substring(0, pipeCharPos).trim();
                linkText = str.substring(pipeCharPos + 1).trim();
                if (linkText.isEmpty())
                    linkText = link;
                pipeUsed = true;
            }
            int sectionStart = link.indexOf("#");
            if (sectionStart != -1) {
                if (!pipeUsed)
                    linkText = link.substring(sectionStart + 1).trim();
                link = link.substring(0, sectionStart).trim();
            }

            sb.replace(start, end + 1, linkText);

            if (link.equals("")) {
                continue;
            }

            String title = normalizePageTitle(link);
            Link linkObj = pagePrototype.getLinkPrototype(title);
            if (linkObj == null) {
                linkObj = pagePrototype.addLinkPrototype(title);
            }
            linkObj.addPosition(start, linkText.length());
        }
        return sb.toString();
    }

    private String normalizePageTitle(String title) {
        title = title.trim();
        return Character.toUpperCase(title.charAt(0)) + title.substring(1);
    }

    private Page findOrCreatePage(String title) {
        Page page = pageDao.findById(title);
        if (page == null) {
            page = new Page(title);
            pageDao.save(page);
        }
        return page;
    }

    private Category findOrCreateCategory(String name) {
        Category category = categoryDao.findById(name);
        if (category == null) {
            category = new Category(name);
            categoryDao.save(category);
        }
        return category;
    }

    private LocalizedPage findOrCreateLocalizedPage(String title, Locale locale) {
        LocalizedPage localizedPage = localizedPageDao.findById(title);
        if (localizedPage == null) {
            localizedPage = new LocalizedPage(title, locale);
            localizedPageDao.save(localizedPage);
        }
        return localizedPage;
    }

    static class PagePrototype {

        private String title;
        private Map<String, Link> linkPrototypes = new HashMap<String, Link>();
        private Set<String> categories = new HashSet<String>();
        private Map<Locale, String> localizedPages = new HashMap<Locale, String>();

        public PagePrototype(String title) {
            this.title = title;
        }

        public String getTitle() {
            return title;
        }

        public Link getLinkPrototype(String linkTitle) {
            return linkPrototypes.get(linkTitle);
        }

        public Link addLinkPrototype(String linkTitle) {
            Link link = new Link(linkTitle);
            linkPrototypes.put(linkTitle, link);
            return link;
        }

        public Map<String, Link> getLinkPrototypes() {
            return linkPrototypes;
        }

        public void addCategory(String category) {
            categories.add(category);
        }

        public Set<String> getCategories() {
            return categories;
        }

        public Map<Locale, String> getLocalizedPages() {
            return localizedPages;
        }

        public String getLocalizedPages(Locale locale) {
            return localizedPages.get(locale);
        }

        public void addLocalizedPages(Locale locale, String localizedPageTitle) {
            this.localizedPages.put(locale, localizedPageTitle);
        }
    }

    static class Link {

        private String text;

        /*
        key = start position, value = length
        */
        private Map<Integer, Integer> pos = new HashMap<Integer, Integer>();

        public Link(String text) {
            this.text = text;
        }

        public void addPosition(Integer startPos, Integer length) {
            pos.put(startPos, length);
        }

        public String getText() {
            return text;
        }

        public Map<Integer, Integer> getPos() {
            return pos;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Link link = (Link) o;

            if (!text.equals(link.text)) return false;

            return true;
        }

        @Override
        public int hashCode() {
            return text.hashCode();
        }
    }


}
