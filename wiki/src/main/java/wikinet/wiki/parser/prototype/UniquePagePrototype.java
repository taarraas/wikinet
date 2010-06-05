package wikinet.wiki.parser.prototype;

import wikinet.db.model.Locale;

import java.util.*;

/**
 * @author shyiko
 * @since Apr 16, 2010
 */
public class UniquePagePrototype extends PagePrototype {

    private Map<String, Link> links = new HashMap<String, Link>();

    private Set<String> categories = new HashSet<String>();

    private Map<Locale, String> localizedPages = new HashMap<Locale, String>();

    private String firstParagraph;

    private String text;

    public UniquePagePrototype(String title) {
        super(title);
    }

    public Link getLink(String linkTitle) {
        return links.get(linkTitle.trim());
    }

    public Link addLink(String linkTitle) {
        linkTitle = linkTitle.trim();
        Link link = new Link(linkTitle);
        links.put(linkTitle, link);
        return link;
    }

    public Collection<Link> getLinks() {
        return links.values();
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

    public void addLocalizedPage(Locale locale, String localizedPageTitle) {
        this.localizedPages.put(locale, localizedPageTitle);
    }

    public String getText() {
        return text;
    }

    public String getFirstParagraph() {
        return firstParagraph;
    }

    public void setFirstParagraph(String firstParagraph) {
        this.firstParagraph = firstParagraph;
    }

    public void setText(String text) {
        this.text = text;
    }

    public static class Link {

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
