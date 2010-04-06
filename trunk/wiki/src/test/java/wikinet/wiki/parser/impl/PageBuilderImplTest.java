package wikinet.wiki.parser.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTransactionalTestNGSpringContextTests;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import wikinet.db.model.Locale;
import wikinet.wiki.dao.CategoryDao;
import wikinet.wiki.dao.LocalizedPageDao;
import wikinet.wiki.dao.PageDao;
import wikinet.wiki.domain.Category;
import wikinet.wiki.domain.LocalizedPage;
import wikinet.wiki.domain.Page;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.*;

import static wikinet.wiki.parser.impl.PageBuilderImpl.Link;
import static wikinet.wiki.parser.impl.PageBuilderImpl.PagePrototype;

/**
 * @author shyiko
 * @since Apr 4, 2010
 */
@ContextConfiguration(locations = {"classpath:spring-wiki-module-test.xml"})
public class PageBuilderImplTest extends AbstractTransactionalTestNGSpringContextTests {

    @Autowired
    private PageDao pageDao;

    @Autowired
    private LocalizedPageDao localizedPageDao;

    @Autowired
    private CategoryDao categoryDao;

    private PageBuilderImpl pageBuilder;

    @BeforeClass
    public void setUp() {
        pageBuilder = new PageBuilderImpl();
        pageBuilder.setPageDao(pageDao);
        pageBuilder.setLocalizedPageDao(localizedPageDao);
        pageBuilder.setCategoryDao(categoryDao);
    }

    @Test
    public void testProcessComplexText() throws Exception {
        PagePrototype pagePrototype = new PagePrototype("");
        String rt = pageBuilder.processText(pagePrototype, getComplexText());
        Assert.assertNotNull(rt);
    }

    @Test
    public void testProcessText() {
        PagePrototype pagePrototype = new PagePrototype("");
        Assert.assertEquals(pageBuilder.processText(pagePrototype, "<tag>text <nowiki>hello <tag></nowiki> text"),
                            "text hello <tag> text");
        Assert.assertEquals(pageBuilder.processText(pagePrototype, "text <pre>hello <tag></pre> text"),
                            "text hello <tag> text");
        Assert.assertEquals(pageBuilder.processText(pagePrototype, "text <nowiki><pre>hello <tag></pre></nowiki> text"),
                            "text <pre>hello <tag></pre> text");
        Assert.assertEquals(pageBuilder.processText(pagePrototype, "text <pre><nowiki>hello <tag></nowiki></pre> text"),
                            "text <nowiki>hello <tag></nowiki> text");
        Assert.assertEquals(pageBuilder.processText(pagePrototype, "text <nowiki>hello <tag></nowiki><pre><tag2></pre> text"),
                            "text hello <tag><tag2> text");
    }

    @Test
    public void testProcessTextBlock() {
        PagePrototype pagePrototype = new PagePrototype("");
        Assert.assertEquals(pageBuilder.processText(pagePrototype, ".&lt;ref name=&quot;definition&quot;&gt;/ref&gt;."),
                            "..");
        Assert.assertEquals(pageBuilder.processText(pagePrototype, "a<tag>b c<math>\\sin</math> ''d''"),
                            "ab c d");
    }

    @Test
    public void testProcessTextWithLongRef() {
        PagePrototype pagePrototype = new PagePrototype("");
        String s = pageBuilder.processText(pagePrototype,
                "{{for|the anthology of anarchist writings|Anarchism: A Documentary History of Libertarian Ideas}}\n" +
                        "{{redirect|Anarchist|the fictional character|Anarchist (comics)}}\n" +
                        "{{redirect|Anarchists}}\n" +
                        "{{pp-move-indef}}\n" +
                        "{{Anarchism sidebar}}\n" +
                        "Anarchism is a [[political philosophy]] which considers the [[Sovereign state|state]] undesirable, unnecessary and harmful, and instead promotes a stateless society, or [[anarchy]].<ref name=\"definition\">\n" +
                        "{{cite journal|last=Malatesta|first=Errico|title=Towards Anarchism|journal=MAN!|publisher=International Group of San Francisco|location=Los Angeles|oclc=3930443|url=http://www.marxists.org/archive/malatesta/1930s/xx/toanarchy.htm|authorlink=Errico Malatesta}}\n" +
                        "{{cite journal |url=http://www.theglobeandmail.com/servlet/story/RTGAM.20070514.wxlanarchist14/BNStory/lifeWork/home/\n" +
                        "|title=Working for The Man |journal=[[The Globe and Mail]] |accessdate=2008-04-14 |last=Agrell |first=Siri |date=2007-05-14}}\n" +
                        "{{cite web|url=http://www.britannica.com/eb/article-9117285|title=Anarchism|date=2006|work=Encyclopædia Britannica|publisher=Encyclopædia Britannica Premium Service|accessdate=2006-08-29}}\n" +
                        "{{cite journal|date=2005|title=Anarchism|journal=The Shorter [[Routledge Encyclopedia of Philosophy]]|pages=14|quote=Anarchism is the view that a society without the state, or government, is both possible and desirable.}}\n" +
                        "The following sources cite anarchism as a political philosophy:\n" +
                        "{{cite book | last = Mclaughlin | first = Paul | title = Anarchism and Authority | publisher = Ashgate | location = Aldershot | year = 2007 | isbn = 0754661962 |page=59}}\n" +
                        "{{cite book | last = Johnston | first = R. | title = The Dictionary of Human Geography | publisher = Blackwell Publishers | location = Cambridge | year = 2000 | isbn = 0631205616 |page=24}}</ref> It seeks to diminish or even abolish authority in the conduct of human relations. Anarchists may widely disagree on what additional criteria are required in anarchism. [[The Oxford Companion to Philosophy]] says, \"there is no single defining position that all anarchists hold, and those considered anarchists at best share a certain family resemblance.\"");
        Assert.assertNotNull(s);
        Assert.assertFalse(s.contains("following sources cite anarchism"));
    }

    @Test
    public void testProcessCurlyBrackets() {
        PagePrototype pagePrototype = new PagePrototype("");
        Assert.assertEquals(pageBuilder.processCurlyBrackets(pagePrototype,
                "This template takes two parameters, and\n" +
                "creates underlined text with a hover box\n" +
                "for many modern browsers supporting CSS:\n" +
                "\n" +
                "{{H:title|This is the hover text|\n" +
                "Hover your mouse over this text}}\n" +
                "\n" +
                "Go to this page to see the H:title template\n" +
                "itself: {{tl|H:title}}"),
                "This template takes two parameters, and\n" +
                "creates underlined text with a hover box\n" +
                "for many modern browsers supporting CSS:\n" +
                "\n" +
                "\n" +
                "\n" +
                "Go to this page to see the H:title template\n" +
                "itself: ");
    }

    @Test
    public void testProcessSquareBracketsSimple() throws Exception {
        testSB("Here's a link to a page named [[Official position]].\n" +
               "You can even say [[official position]]s\n" +
               "and the link will show up correctly.",
               "Here's a link to a page named Official position.\n" +
               "You can even say official positions\n" +
               "and the link will show up correctly.",
               new HashMap<String, Integer>() {{
                   put("Official position", 2);
               }});
    }

    @Test
    public void testProcessSquareBracketsWithSections() throws Exception {
        testSB("You can link to a page section by its title:\n" +
               "* [[Doxygen#Doxygen Examples]].\n" +
               "If multiple sections have the same title, add\n" +
               "a number. [[#Example section 3]] goes to the\n" +
               "third section named \"Example section\".",
               "You can link to a page section by its title:\n" +
               "* Doxygen Examples.\n" +
               "If multiple sections have the same title, add\n" +
               "a number. Example section 3 goes to the\n" +
               "third section named \"Example section\".",
               new HashMap<String, Integer>() {{
                   put("Doxygen", 1);
               }},
               new HashMap<String, String>() {{
                   put("Doxygen", "Doxygen Examples");
               }});
    }

    @Test
    public void testProcessSquareBracketsWithPipes() throws Exception {
        testSB("You can make a link point to a different place\n" +
               "with a [[Help:Piped link|piped link]]. Put the link\n" +
               "target first, then the pipe character \"|\", then\n" +
               "the link text.\n" +
               "* [[Help:Link|About Links]]\n" +
               "* [[List of cities by country#Morocco|Cities in Morocco]]\n" +
               "Or you can use the \"pipe trick\" so that a title that\n" +
               "contains disambiguation text will appear with more concise\n" +
               "link text.\n" +
               "* [[Spinning (textiles)|]]\n" +
               "* [[Boston, Massachusetts|]]",
               "You can make a link point to a different place\n" +
               "with a piped link. Put the link\n" +
               "target first, then the pipe character \"|\", then\n" +
               "the link text.\n" +
               "* About Links\n" +
               "* Cities in Morocco\n" +
               "Or you can use the \"pipe trick\" so that a title that\n" +
               "contains disambiguation text will appear with more concise\n" +
               "link text.\n" +
               "* Spinning (textiles)\n" +
               "* Boston, Massachusetts",
               new HashMap<String, Integer>() {{
                   put("List of cities by country", 1);
                   put("Spinning (textiles)", 1);
                   put("Boston, Massachusetts", 1);
               }},
               new HashMap<String, String>() {{
                   put("List of cities by country", "Cities in Morocco");
               }});
    }

    @Test
    public void testProcessSquareBracketsWithUrls() throws Exception {
        testSB("You can make an external link just by typing a URL:\n" +
               "http://www.nupedia.com\n" +
               "You can give it a title:\n" +
               "[http://www.nupedia.com Nupedia]\n" +
               "Or leave the title blank:\n" +
               "[http://www.nupedia.com]\n" +
               "External link can be used to link to a wiki page that\n" +
               "http://meta.wikimedia.org/w/index.php?title=Fotonotes\n" +
               "&oldid=482030#Installation",
               "You can make an external link just by typing a URL:\n" +
               "http://www.nupedia.com\n" +
               "You can give it a title:\n" +
               "Nupedia\n" +
               "Or leave the title blank:\n" +
               "\n" +
               "External link can be used to link to a wiki page that\n" +
               "http://meta.wikimedia.org/w/index.php?title=Fotonotes\n" +
               "&oldid=482030#Installation");
    }

    @Test
    public void testProcessSquareBracketsWithMailto() throws Exception {
        testSB("Linking to an e-mail address works the same way:\n" +
               "mailto:someone@example.com or [mailto:someone@example.com someone]",
               "Linking to an e-mail address works the same way:\n" +
               "mailto:someone@example.com or someone");
    }

    @Test
    public void testProcessSquareBracketsWithCategories() throws Exception {
        testSB("[[Help:Category|Category links]] do not show up in line\n" +
                "but instead at page bottom\n" +
                "''and cause the page to be listed in the category.''\n" +
                "[[Category:English documentation]]",
               "Category links do not show up in line\n" +
                "but instead at page bottom\n" +
                "''and cause the page to be listed in the category.''\n",
               new HashSet<String>() {{
                   add("English documentation");
               }});
    }

    @Test
    public void testProcessSquareBracketsWithCategoriesLinks() throws Exception {
        testSB("Add an extra colon to ''link'' to a category in line\n" +
                "without causing the page to be listed in the category:\n" +
                "[[:Category:English documentation]]",
               "Add an extra colon to ''link'' to a category in line\n" +
                "without causing the page to be listed in the category:\n" +
                "English documentation");
    }

    @Test
    public void testProcessSquareBracketsWithDates() throws Exception {
        testSB("The Wiki reformats linked dates to match the reader's\n" +
                "date preferences. These three dates will show up the\n" +
                "same if you choose a format in your\n" +
                "[[Special:Preferences|]]:\n" +
                "* [[1969-07-20]]\n" +
                "* [[July 20]], [[1969]]\n" +
                "* [[20 July]] [[1969]]\n" +
                "* [[1969]]-[[07-20]]\n",
               "The Wiki reformats linked dates to match the reader's\n" +
                "date preferences. These three dates will show up the\n" +
                "same if you choose a format in your\n" +
                "Preferences:\n" +
                "* 1969-07-20\n" +
                "* July 20 1969\n" +
                "* 20 July 1969\n" +
                "* 1969-07-20\n");
    }

    @Test
    public void testProcessSquareBracketsWithLocales() throws Exception {
        testSB("The Wiki reformats linked dates to match the reader's\n" +
               "date [[pl:Anarchizm]] [[uk:Анархізм]]",
               "The Wiki reformats linked dates to match the reader's\n" +
               "date  ",
               Collections.<String, Integer>emptyMap(),
               Collections.<String, String>emptyMap(),
               Collections.<String>emptySet(),
               new HashMap<Locale, String>() {{
                   put(Locale.POL, "Anarchizm");
                   put(Locale.UKR, "Анархізм");
               }});
    }


    @Test
    public void testImportRedirectPage() throws Exception {
        PageBuilderImpl pageBuilder = new PageBuilderImpl();
        pageBuilder.setPageDao(pageDao);
        pageBuilder.setLocalizedPageDao(localizedPageDao);
        pageBuilder.setCategoryDao(categoryDao);
        try {
            pageBuilder.importPage("AccessibleComputing", "#REDIRECT [[Computer accessibility]] {{R from CamelCase}}");
            Assert.assertEquals(pageDao.findAll().size(), 2);
            Page page = pageDao.findById("Computer accessibility");
            Assert.assertNotNull(page);
            Assert.assertEquals(page.getRedirects().iterator().next().getTitle(), "AccessibleComputing");
        } finally {
            cleanDB();
        }
    }

    @Test
    public void testImportPage() throws Exception {
        PageBuilderImpl pageBuilder = new PageBuilderImpl();
        pageBuilder.setPageDao(pageDao);
        pageBuilder.setLocalizedPageDao(localizedPageDao);
        pageBuilder.setCategoryDao(categoryDao);
        try {
            pageBuilder.importPage("Anarchism", getComplexText());
            Page page = pageDao.findById("Anarchism");
            Assert.assertNotNull(page);
        } finally {
            for (Page page : pageDao.findAll()) {
                pageDao.delete(page);
            }
            for (LocalizedPage page : localizedPageDao.findAll()) {
                localizedPageDao.delete(page);
            }
            for (Category category : categoryDao.findAll()) {
                categoryDao.delete(category);
            }
        }
    }

    private void testSB(String text, String resultText) {
        testSB(text, resultText,
                Collections.<String, Integer>emptyMap(),
                Collections.<String, String>emptyMap(),
                Collections.<String>emptySet(),
                Collections.<Locale, String>emptyMap());
    }

    private void testSB(String text, String resultText, Map<String, Integer> resultLinks) {
        testSB(text, resultText, resultLinks,
                Collections.<String, String>emptyMap(),
                Collections.<String>emptySet(),
                Collections.<Locale, String>emptyMap());
    }

    private void testSB(String text, String resultText, Map<String, Integer> resultLinks,
                        Map<String, String> linkLinkTextMap) {
        testSB(text, resultText, resultLinks, linkLinkTextMap,
                Collections.<String>emptySet(),
                Collections.<Locale, String>emptyMap());
    }

    private void testSB(String text, String resultText, Set<String> categories) {
        testSB(text, resultText,
                Collections.<String, Integer>emptyMap(),
                Collections.<String, String>emptyMap(),
                categories,
                Collections.<Locale, String>emptyMap());
    }

    private void testSB(String text, String resultText, Map<String, Integer> resultLinks,
                        Map<String, String> linkLinkTextMap, Set<String> categories, Map<Locale, String> localized) {
        PagePrototype pagePrototype = new PagePrototype("");
        String processed = pageBuilder.processSquareBrackets(pagePrototype, text);
        Assert.assertEquals(processed, resultText);
        Assert.assertEquals(pagePrototype.getLinkPrototypes().size(), resultLinks.size());
        for (Map.Entry<String, Integer> entry : resultLinks.entrySet()) {
            Link link = pagePrototype.getLinkPrototype(entry.getKey());
            Assert.assertNotNull(link, entry.getKey());
            Assert.assertEquals(link.getPos().size(), entry.getValue().intValue());
            for (Map.Entry<Integer, Integer> posEntry : link.getPos().entrySet()) {
                String searchFor = link.getText();
                if (linkLinkTextMap.containsKey(link.getText()))
                    searchFor = linkLinkTextMap.get(link.getText());
                String search = searchFor.substring(1);
                String sub = resultText.substring(posEntry.getKey() + 1, posEntry.getKey() + posEntry.getValue());
                Assert.assertEquals(sub, search);
            }
        }
        Assert.assertEquals(pagePrototype.getCategories().size(), categories.size());
        for (String category : categories) {
            Assert.assertTrue(pagePrototype.getCategories().contains(category));
        }
        Assert.assertEquals(pagePrototype.getLocalizedPages().size(), localized.size());
        for (Map.Entry<Locale, String> lentry : localized.entrySet()) {
            String title = pagePrototype.getLocalizedPages(lentry.getKey());
            Assert.assertEquals(title, lentry.getValue());
        }
    }

    private String getComplexText() throws Exception {
        BufferedReader reader = new BufferedReader(new FileReader("src/test/resources/wiki-complex-text.log"));
        StringBuilder sb = new StringBuilder();
        try {
            String s;
            while ((s = reader.readLine()) != null) {
                sb.append(s + "\n");
            }
        } finally {
            reader.close();
        }
        return sb.toString();
    }

    private void cleanDB() {
        for (Page page : pageDao.findAll()) {
            pageDao.delete(page);
        }
        for (LocalizedPage page : localizedPageDao.findAll()) {
            localizedPageDao.delete(page);
        }
        for (Category category : categoryDao.findAll()) {
            categoryDao.delete(category);
        }
    }

}
