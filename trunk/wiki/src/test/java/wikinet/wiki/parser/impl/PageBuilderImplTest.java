package wikinet.wiki.parser.impl;

import org.mockito.ArgumentMatcher;
import org.testng.Assert;
import org.testng.annotations.Test;
import wikinet.db.model.Locale;
import wikinet.wiki.parser.PageBuilder;
import wikinet.wiki.parser.PagePrototypeSaver;
import wikinet.wiki.parser.prototype.CategoryPagePrototype;
import wikinet.wiki.parser.prototype.PagePrototype;
import wikinet.wiki.parser.prototype.RedirectPagePrototype;
import wikinet.wiki.parser.prototype.UniquePagePrototype;

import java.util.*;

import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.*;

/**
 * @author shyiko
 * @since Apr 4, 2010
 */
public class PageBuilderImplTest {

    public void testImportPage(String title, String text, ArgumentMatcher<PagePrototype> matcher) {
        PagePrototypeSaver prototypeSaverMock = mock(PagePrototypeSaver.class);
        PageBuilder pb = new PageBuilderImpl();
        PagePrototype prototype = pb.buildPagePrototype(title, text);
        Assert.assertTrue(matcher.matches(prototype));
    }

    @Test
    public void testCategoryPage() throws Exception {
        testImportPage("Category:Page", "sdghm[[Category:Parent1]] and [[Category:Parent2]][[Category:Parent3]]",
                             new ArgumentMatcher<PagePrototype>() {
                                 @Override
                                 public boolean matches(Object o) {
                                     CategoryPagePrototype pagePrototype = (CategoryPagePrototype) o;
                                     Assert.assertEquals(pagePrototype.toString(), "Page");
                                     Set<String> parentCategories = pagePrototype.getParentCategories();
                                     Assert.assertEquals(parentCategories.size(), 3);
                                     Assert.assertTrue(parentCategories.contains("Parent1"));
                                     Assert.assertTrue(parentCategories.contains("Parent2"));
                                     Assert.assertTrue(parentCategories.contains("Parent3"));
                                     return true;
                                 }
                             });
    }

    @Test
    public void testNowikiAndPre() throws Exception {
        testImportPage("Page", "<tag>text <nowiki>hello <tag></nowiki> text\ntext <pre>hello <tag></pre> text",
                             new ArgumentMatcher<PagePrototype>() {
                                 @Override
                                 public boolean matches(Object o) {
                                     UniquePagePrototype pagePrototype = (UniquePagePrototype) o;
                                     Assert.assertEquals(pagePrototype.getFirstParagraph(), "text hello <tag> text");
                                     Assert.assertEquals(pagePrototype.getText(), "text hello <tag> text");
                                     return true;
                                 }
                             });
        testImportPage("Page", "text <nowiki><pre>hello <tag></pre></nowiki> text\ntext <pre><nowiki>hello <tag></nowiki></pre> text\n" +
                                 "text <nowiki>hello <tag></nowiki><pre><tag2></pre> text",
                             new ArgumentMatcher<PagePrototype>() {
                                 @Override
                                 public boolean matches(Object o) {
                                     UniquePagePrototype pagePrototype = (UniquePagePrototype) o;
                                     Assert.assertEquals(pagePrototype.getFirstParagraph(), "text <pre>hello <tag></pre> text");
                                     Assert.assertEquals(pagePrototype.getText(), "text <nowiki>hello <tag></nowiki> text\n" +
                                                                                  "text hello <tag><tag2> text");
                                     return true;
                                 }
                             });
    }

    @Test
    public void testSquereBracketsAtTheBeginning() throws Exception {
        testImportPage("PageSB", "{{Redirect|Mapmaker}}\n" +
                "{{Redirect|Cartographer|the album by [[E.S. Posthumus]]|Cartographer (album)}}\n" +
                "\n\n\n" +
                "[[File:Mediterranean chart fourteenth century2.jpg|right|thumb|250px|The oldest original cartographic artifact in the [[Library of Congress]]: a [[nautical chart]] of the [[Mediterranean Sea]].  Second quarter of the fourteenth century.]]\n" +
                "\n" +
                "'''Cartography''' (in [[Greek language|Greek]] ''chartis'' = map and ''graphein'' = write) is the study and practice of making [[map]]s (also can be called mapping). Combining science, [[aesthetics]], and technique, cartography builds on the premise that reality can be modeled in ways that communicate spatial information effectively. ",
                             new ArgumentMatcher<PagePrototype>() {
                                 @Override
                                 public boolean matches(Object o) {
                                     UniquePagePrototype pagePrototype = (UniquePagePrototype) o;
                                     Assert.assertEquals(pagePrototype.getFirstParagraph(), "Cartography (in Greek chartis = map and graphein = write) is the study and practice of making maps (also can be called mapping). Combining science, aesthetics, and technique, cartography builds on the premise that reality can be modeled in ways that communicate spatial information effectively.");
                                     Assert.assertNull(pagePrototype.getText());
                                     Assert.assertEquals(pagePrototype.getLinks().size(), 3);
                                     return true;
                                 }
                             });
    }

    @Test
    public void testRefAndMath() throws Exception {
        testImportPage("Page", ".&lt;ref name=&quot;definition&quot;&gt;/ref&gt;.\na<tag>b c<math>\\sin</math> ''d''",
                             new ArgumentMatcher<PagePrototype>() {
                                 @Override
                                 public boolean matches(Object o) {
                                     UniquePagePrototype pagePrototype = (UniquePagePrototype) o;
                                     Assert.assertEquals(pagePrototype.getFirstParagraph(), "..");
                                     Assert.assertEquals(pagePrototype.getText(), "ab c d");
                                     return true;
                                 }
                             });
    }

    @Test
    public void testCurlyBrackets() {
        testImportPage("Page", "This template takes two parameters, and\n" +
                                 "creates underlined text with a hover box\n" +
                                 "for many modern browsers supporting CSS:\n" +
                                 "\n" +
                                 "{{H:title|This is the hover text|\n" +
                                 "Hover your mouse over this text}}\n" +
                                 "\n" +
                                 "Go to this page to see the H:title template\n" +
                                 "itself: {{tl|H:title}}",
                new ArgumentMatcher<PagePrototype>() {
                    @Override
                    public boolean matches(Object o) {
                        UniquePagePrototype pagePrototype = (UniquePagePrototype) o;
                        Assert.assertEquals(pagePrototype.getFirstParagraph(), "This template takes two parameters, and");
                        Assert.assertEquals(pagePrototype.getText(), "creates underlined text with a hover box\n" +
                                                                     "for many modern browsers supporting CSS:\n" +
                                                                     "Go to this page to see the H:title template\n" +
                                                                     "itself:");
                        return true;
                    }
                });
    }

    @Test
    public void testSquareBracketsSimple() throws Exception {
        testImportPage("Page", "Here's a link to a page named [[Official position]] with [[official position]].\n" +
                                 "You can even say [[xxx]]s\n" +
                                 "and the link will show up correctly.",
                             new ArgumentMatcher<PagePrototype>() {
                                 @Override
                                 public boolean matches(Object o) {
                                     UniquePagePrototype pagePrototype = (UniquePagePrototype) o;
                                     Assert.assertEquals(pagePrototype.getFirstParagraph(), "Here's a link to a page named Official position with official position.");
                                     Assert.assertEquals(pagePrototype.getText(), "You can even say xxxs\n" +
                                                                                  "and the link will show up correctly.");
                                     Collection<UniquePagePrototype.Link> linkCollection = pagePrototype.getLinks();
                                     Assert.assertEquals(linkCollection.size(), 1);
                                     Assert.assertEquals(linkCollection.iterator().next().getText(), "Official position");
                                     return true;
                                 }
                             });
    }

    @Test
    public void testSquareBracketsWithSections() throws Exception {
        testImportPage("Page", "You can link to a page section by its title:" +
                                 "* [[Doxygen#Doxygen Examples]].\n" +
                                 "If multiple sections have the same title, add\n" +
                                 "a number. [[#Example section 3]] goes to the\n" +
                                 "third section named \"Example section\".",
                             new ArgumentMatcher<PagePrototype>() {
                                 @Override
                                 public boolean matches(Object o) {
                                     UniquePagePrototype pagePrototype = (UniquePagePrototype) o;
                                     Assert.assertEquals(pagePrototype.getFirstParagraph(),
                                              "You can link to a page section by its title:" +
                                              "* Doxygen Examples.");
                                     Assert.assertEquals(pagePrototype.getText(),
                                             "If multiple sections have the same title, add\n" +
                                             "a number. Example section 3 goes to the\n" +
                                             "third section named \"Example section\".");
                                     Collection<UniquePagePrototype.Link> linkCollection = pagePrototype.getLinks();
                                     Assert.assertEquals(linkCollection.size(), 1);
                                     UniquePagePrototype.Link link = linkCollection.iterator().next();
                                     Assert.assertEquals(link.getText(), "Doxygen");
                                     Map.Entry<Integer, Integer> linkPos = link.getPos().entrySet().iterator().next();
                                     Assert.assertEquals(pagePrototype.getFirstParagraph()
                                             .substring(linkPos.getKey(), linkPos.getKey() + linkPos.getValue()), "Doxygen Examples");
                                     return true;
                                 }
                             });
    }

    @Test
    public void testSquareBracketsWithPipes() throws Exception {
        testImportPage("Page", "You can make a link point to a different place " +
                                 "with a [[Help:Piped link|piped link]]. Put the link " +
                                 "target first, then the pipe character \"|\", then " +
                                 "the link text. [[Boston, Massachusetts|]] " +
                                 "* [[List of cities by country#Morocco|Cities in Morocco]]\n" +
                                 "Or you can use the \"pipe trick\" so that a title that\n" +
                                 "contains disambiguation text will appear with more concise\n" +
                                 "link text.\n" +
                                 "* [[Spinning (textiles)|]]\n",
                             new ArgumentMatcher<PagePrototype>() {
                                 @Override
                                 public boolean matches(Object o) {
                                     UniquePagePrototype pagePrototype = (UniquePagePrototype) o;
                                     Assert.assertEquals(pagePrototype.getFirstParagraph(),
                                              "You can make a link point to a different place " +
                                              "with a . Put the link " +
                                              "target first, then the pipe character \"|\", then " +
                                              "the link text. Boston, Massachusetts " +
                                              "* Cities in Morocco");
                                     Assert.assertEquals(pagePrototype.getText(),
                                              "Or you can use the \"pipe trick\" so that a title that\n" +
                                              "contains disambiguation text will appear with more concise\n" +
                                              "link text.\n" +
                                              "* Spinning (textiles)");
                                     Collection<UniquePagePrototype.Link> linkCollection = pagePrototype.getLinks();
                                     Set<String> links = new HashSet<String>();
                                     links.add("List of cities by country");
                                     links.add("Boston, Massachusetts");
                                     Map<String, String> linksOverride = new HashMap<String, String>();
                                     linksOverride.put("List of cities by country", "Cities in Morocco");
                                     Assert.assertEquals(links.size(), linkCollection.size());
                                     for (UniquePagePrototype.Link link : linkCollection) {
                                         Assert.assertTrue(links.contains(link.getText()));
                                         String linkText = linksOverride.get(link.getText());
                                         if (linkText == null)
                                             linkText = link.getText();
                                         for (Map.Entry<Integer, Integer> linkPos : link.getPos().entrySet()) {
                                             Assert.assertEquals(pagePrototype.getFirstParagraph()
                                                     .substring(linkPos.getKey(), linkPos.getKey() + linkPos.getValue()), linkText);
                                         }

                                     }
                                     return true;
                                 }
                             });
    }

    @Test
    public void testSquareBracketsWithUrls() throws Exception {
        testImportPage("Page",
                "You can make an external link just by typing a URL:\n" +
                "http://www.nupedia.com\n" +
                "You can give it a title:\n" +
                "[http://www.nupedia.com Nupedia]\n" +
                "Or leave the title blank:\n" +
                "[http://www.nupedia.com]\n" +
                "External link can be used to link to a wiki page that\n" +
                "http://meta.wikimedia.org/w/index.php?title=Fotonotes\n" +
                "&oldid=482030#Installation",
                new ArgumentMatcher<PagePrototype>() {
                    @Override
                    public boolean matches(Object o) {
                        UniquePagePrototype pagePrototype = (UniquePagePrototype) o;
                        Assert.assertEquals(pagePrototype.getFirstParagraph(), "You can make an external link just by typing a URL:");
                        Assert.assertEquals(pagePrototype.getText(),
                                "http://www.nupedia.com\n" +
                                "You can give it a title:\n" +
                                "Nupedia\n" +
                                "Or leave the title blank:\n" +
                                "\n" +
                                "External link can be used to link to a wiki page that\n" +
                                "http://meta.wikimedia.org/w/index.php?title=Fotonotes\n" +
                                "&oldid=482030#Installation");
                        return true;
                    }
                });
    }

    @Test
    public void testSquareBracketsWithMailto() throws Exception {
        testImportPage("Page",
                "mailto:someone@example.com or [mailto:someone@example.com someone]",
                new ArgumentMatcher<PagePrototype>() {
                    @Override
                    public boolean matches(Object o) {
                        UniquePagePrototype pagePrototype = (UniquePagePrototype) o;
                        Assert.assertEquals(pagePrototype.getFirstParagraph(), "mailto:someone@example.com or someone");
                        Assert.assertEquals(pagePrototype.getText(), null);
                        return true;
                    }
                });
    }

    @Test
    public void testSquareBracketsWithCategories() throws Exception {
        testImportPage("PageSBWC",
                "[[Help:Category|Category links]] do not show up in line " +
                "but instead at page bottom\n" +
                "''and [[cause]] the page to be listed in the category.''\n" +
                "[[Category:English documentation]]",
                new ArgumentMatcher<PagePrototype>() {
                    @Override
                    public boolean matches(Object o) {
                        UniquePagePrototype pagePrototype = (UniquePagePrototype) o;
                        Assert.assertEquals(pagePrototype.getFirstParagraph(), "do not show up in line " +
                                                                               "but instead at page bottom");
                        Assert.assertEquals(pagePrototype.getText(), "and cause the page to be listed in the category.");
                        Assert.assertEquals(pagePrototype.getCategories().size(), 1);
                        Assert.assertEquals(pagePrototype.getCategories().iterator().next(), "English documentation");
                        Assert.assertEquals(pagePrototype.getLinks().size(), 0);
                        return true;
                    }
                });
    }

    @Test
    public void testSquareBracketsWithCategoriesLinks() throws Exception {
        testImportPage("Page",
                "Add an extra colon to ''link'' to a category in line " +
                "without causing the page to be listed in the category: " +
                "[[:Category:English documentation]]",
                new ArgumentMatcher<PagePrototype>() {
                    @Override
                    public boolean matches(Object o) {
                        UniquePagePrototype pagePrototype = (UniquePagePrototype) o;
                        Assert.assertEquals(pagePrototype.getFirstParagraph(),
                                "Add an extra colon to link to a category in line " +
                                "without causing the page to be listed in the category: " +
                                "English documentation");
                        Assert.assertEquals(pagePrototype.getText(), null);
                        Assert.assertEquals(pagePrototype.getCategories().size(), 0);
                        return true;
                    }
                });
    }

    @Test
    public void testSquareBracketsWithDates() throws Exception {
        testImportPage("Page",
                "The Wiki reformats linked dates to match the reader's\n" +
                "date preferences. These three dates will show up the\n" +
                "same if you choose a format in your\n" +
                "[[Special:Preferences|]]:\n" +
                "* [[1969-07-20]]\n" +
                "* [[July 20]], [[1969]]\n" +
                "* [[20 July]] [[1969]]\n" +
                "* [[1969]]-[[07-20]]\n",
                new ArgumentMatcher<PagePrototype>() {
                    @Override
                    public boolean matches(Object o) {
                        UniquePagePrototype pagePrototype = (UniquePagePrototype) o;
                        Assert.assertEquals(pagePrototype.getFirstParagraph(), "The Wiki reformats linked dates to match the reader's");
                        Assert.assertEquals(pagePrototype.getText(),
                                "date preferences. These three dates will show up the\n" +
                                        "same if you choose a format in your\n" +
                                        ":\n" +
                                        "* 1969-07-20\n" +
                                        "* July 20 1969\n" +
                                        "* 20 July 1969\n" +
                                        "* 1969-07-20");
                        return true;
                    }
                });
        testImportPage("Page", "[[1969]]",
                new ArgumentMatcher<PagePrototype>() {
                    @Override
                    public boolean matches(Object o) {
                        UniquePagePrototype pagePrototype = (UniquePagePrototype) o;
                        Assert.assertEquals(pagePrototype.getFirstParagraph(), "1969");
                        Assert.assertEquals(pagePrototype.getText(), null);
                        Assert.assertEquals(pagePrototype.getLinks().size(), 1);
                        return true;
                    }
                });
    }

    @Test
    public void testSquareBracketsWithLocales() throws Exception {
        testImportPage("Page",
                "The Wiki reformats linked dates to match the reader's " +
                "date [[pl:Anarchizm]] [[uk:Анархізм]]",
                new ArgumentMatcher<PagePrototype>() {
                    @Override
                    public boolean matches(Object o) {
                        UniquePagePrototype pagePrototype = (UniquePagePrototype) o;
                        Assert.assertEquals(pagePrototype.getFirstParagraph(),
                                "The Wiki reformats linked dates to match the reader's date");
                        Assert.assertEquals(pagePrototype.getText(), null);
                        Map<wikinet.db.model.Locale, String> map = pagePrototype.getLocalizedPages();
                        Assert.assertEquals(map.entrySet().size(), 2);
                        Assert.assertEquals(map.get(Locale.POL), "Anarchizm");
                        Assert.assertEquals(map.get(Locale.UKR), "Анархізм");
                        return true;
                    }
                });
    }

    @Test
    public void testImportRedirectPage() throws Exception {
        testImportPage("AccessibleComputing (x)",
                       "#REDIRECT [[Computer accessibility]] {{R from CamelCase}}",
                       new ArgumentMatcher<PagePrototype>() {
                           @Override
                           public boolean matches(Object o) {
                               RedirectPagePrototype pp = (RedirectPagePrototype) o;
                               Assert.assertEquals(pp.getWord(), "AccessibleComputing");
                               Assert.assertEquals(pp.getDisambiguation(), "x");
                               Assert.assertEquals(pp.getRedirectedPage().getWord(), "Computer accessibility");
                               Assert.assertEquals(pp.getRedirectedPage().getDisambiguation(), null);
                               return true;
                           }
                       });
    }

    @Test
    public void testProcessTextWithLongRef() throws Exception {
        testImportPage("Page",
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
                        "{{cite book | last = Johnston | first = R. | title = The Dictionary of Human Geography | publisher = Blackwell Publishers | location = Cambridge | year = 2000 | isbn = 0631205616 |page=24}}</ref> It seeks to diminish or even abolish authority in the conduct of human relations. Anarchists may widely disagree on what additional criteria are required in anarchism. [[The Oxford Companion to Philosophy]] says, \"there is no single defining position that all anarchists hold, and those considered anarchists at best share a certain family resemblance.\"",
                       new ArgumentMatcher<PagePrototype>() {
                           @Override
                           public boolean matches(Object o) {
                               UniquePagePrototype pp = (UniquePagePrototype) o;
                               Assert.assertEquals(pp.getFirstParagraph(), "Anarchism is a political philosophy which " +
                                       "considers the state undesirable, unnecessary and harmful, and instead promotes " +
                                       "a stateless society, or anarchy. It seeks to diminish or even abolish authority " +
                                       "in the conduct of human relations. Anarchists may widely disagree on what additional " +
                                       "criteria are required in anarchism. The Oxford Companion to Philosophy says, " +
                                       "\"there is no single defining position that all anarchists hold, and those " +
                                       "considered anarchists at best share a certain family resemblance.\"");
                               Assert.assertEquals(pp.getText(), null);
                               return true;
                           }
                       });
    }

}
