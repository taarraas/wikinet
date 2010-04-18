package wikinet.wiki.parser.impl;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import wikinet.db.model.Locale;
import wikinet.wiki.parser.PageBuilder;
import wikinet.wiki.parser.PagePrototypeSaver;
import wikinet.wiki.parser.ParseException;
import wikinet.wiki.parser.prototype.PagePrototype;
import wikinet.wiki.parser.prototype.RedirectPagePrototype;
import wikinet.wiki.parser.prototype.UniquePagePrototype;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * http://meta.wikimedia.org/wiki/Help:Wikitext_examples
 * @author shyiko
 * @since Mar 30, 2010
 */
public class PageBuilderImpl implements PageBuilder {

    private static Logger logger = Logger.getLogger(PageBuilderImpl.class);

    private static final Pattern NOWIKI = Pattern.compile("(<nowiki>).*?(</ ?nowiki>)");
    private static final Pattern PRE = Pattern.compile("(<pre>).*?(</ ?pre>)");
    private static final Pattern PATTERN_MATH = Pattern.compile("(<math).*?(/[ ]*math>)", Pattern.DOTALL);
    private static final Pattern PATTERN_REF = Pattern.compile("(<ref([^>])*?/>)|((<ref).*?(/[ ]*ref>))", Pattern.DOTALL);
    private static final Pattern PATTERN_HTML_TAGS = Pattern.compile("(<).*?(>)", Pattern.DOTALL);

    private static final String[] NAMESPACES_TO_SKIP =
            {"Media:", "Special:", "main:", "Talk:", "User:", "User talk:", "Meta:",
            "Meta talk:", "File:", "File talk:", "MediaWiki:", "MediaWiki talk:", "Template:", "Template talk:",
            "Help:", "Help talk:", "Category talk:", "Portal:", "Portal talk:", "media:", "Image:"};

    private boolean endLineHasBeenMet = false;

    private PagePrototypeSaver pagePrototypeSaver;

    @Autowired
    public PageBuilderImpl(PagePrototypeSaver pagePrototypeSaver) {
        this.pagePrototypeSaver = pagePrototypeSaver;
    }

    @Override
    public void importPage(String title, String text) {
        endLineHasBeenMet = false;
        if (text.indexOf("#REDIRECT [[") != -1) {
            int pos = text.indexOf("[[");
            String redirectedPageTitle = text.substring(pos + 2, text.indexOf("]]", pos + 2)).trim();
            pagePrototypeSaver.save(new RedirectPagePrototype(title, new PagePrototype(redirectedPageTitle)));
            return;
        }

        UniquePagePrototype pagePrototype = new UniquePagePrototype(title);
        try {
            processText(pagePrototype, text);
        } catch (Exception ex) {
            if (ex instanceof ParseException)
                logger.error("Page \"" + pagePrototype + "\" : " + ex.getMessage());
            else
                logger.error("Page \"" + pagePrototype + "\"", ex);
            return;
        }
        pagePrototypeSaver.save(pagePrototype);
    }

    private void processText(final UniquePagePrototype pagePrototype, String text) {
        //StringBuilder sb = new StringBuilder();
        Matcher nowikiMatcher = NOWIKI.matcher(text);
        Matcher preMatcher = PRE.matcher(text);
        int index = 0;
        do {
            int nowikipos = nowikiMatcher.find(index) ? nowikiMatcher.start() : -1;
            int prepos = preMatcher.find(index) ? preMatcher.start() : -1;
            if (nowikipos == -1 && prepos == -1) {
                if (index == 0) {
                    processTextBlock(pagePrototype, text, true);
                    return;
                }
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
                throw new ParseException("Unrecognizable <nowiki> / <pre> structure.");
            }

            processTextBlock(pagePrototype, text.substring(index, startGroup), false);
            String between = group.substring(group.indexOf(">") + 1, group.lastIndexOf("<"));
            if (!endLineHasBeenMet)
                pagePrototype.appendFirstParagraph(between);
            else
                pagePrototype.appendText(between);
            index = endGroup;
        } while(true);
        processTextBlock(pagePrototype, text.substring(index), true);
    }

    private void processTextBlock(final UniquePagePrototype pagePrototype, String block, boolean trimEnd) {
        // removing all mathematical formulas
        block = PATTERN_MATH.matcher(block).replaceAll("");
        // removing basic text formatting
        block = block.replaceAll("[']{2,5}", "");
        block = block.replaceAll("[~]{3,5}", "");
        block = block.replaceAll("[-]{4}", "");
        block = block.replaceAll("[=]{2,4}.*?[=]{2,4}", "");
        // replacing html entities
        block = block.replace("&lt;", "<");
        block = block.replace("&gt;", ">");
        block = block.replace("&apos;", "'");
        block = block.replace("&quot;", "\"");
        block = block.replace("&amp;", "&");
        // removing unusual constructions
        block = PATTERN_REF.matcher(block).replaceAll("");
        // removing all html tags
        block = PATTERN_HTML_TAGS.matcher(block).replaceAll("");

        // todo: templates

        block = processCurlyBrackets(pagePrototype, block);
        
        // remove spaces and new lines repeat
        StringBuilder sb = new StringBuilder(block);
        int i = 0, j;
        while ((i = sb.indexOf("\n", i)) != -1) {
            j = sb.indexOf("\n", i+1);
            if (j == -1)
                break;
            if (sb.substring(i + 1, j).trim().isEmpty())
                sb.replace(i, j, "");
            else
                i = j;
        }
        if (sb.length() == 0)
            return;
        if (sb.charAt(0) == '\n')
            sb.replace(0, 1, "");
        block = sb.toString();

        if (!endLineHasBeenMet) {
            i = block.indexOf("\n");
            if (i == -1)
                i = block.length();
            else
                endLineHasBeenMet = true;
            String fp = block.substring(0, i);
            if (!fp.trim().isEmpty()) {
                String pp = processSquareBrackets(pagePrototype, fp, true);
                if (trimEnd)
                    pp = trimEnd(pp);
                pagePrototype.appendFirstParagraph(pp);
            }
            if (i == block.length())
                return;
            block = block.substring(i + 1);
        }

        block = processSquareBrackets(pagePrototype, block, false);
        if (trimEnd)
            block = trimEnd(block);
        pagePrototype.appendText(block);
    }

    private String processCurlyBrackets(final PagePrototype pagePrototype, String text) {
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
    private String processSquareBrackets(final UniquePagePrototype pagePrototype, String text, boolean addLinks) {
        if (text.isEmpty())
            return "";
        StringBuilder sb = new StringBuilder(text);
        int start = 0, end;
        root_cycle:
        while ((start = sb.indexOf("[", start)) != -1) {
            end = sb.indexOf("]", start + 1);
            if (end == -1)
                break;
            // [ [][] ], [ ], [ [] ]
            int includedBracketStart = start;
            while ((includedBracketStart = sb.indexOf("[", includedBracketStart + 1)) < end) {
                if (includedBracketStart == -1)
                    break;
                end = sb.indexOf("]", end + 1);
            }
            if (end == -1) {
                throw new ParseException("Unrecognizable square brackets structure.");
            }
            String str = sb.substring(start + 1, end).trim();
            // external link
            if (str.matches("(http://|https://|telnet://|gopher://|file://|wais://|ftp://|mailto:|news:).*")) {
                int spaceIndex = str.indexOf(" ");
                String replacement = "";
                if (spaceIndex > 0) {
                    replacement = str.substring(spaceIndex + 1, str.length()/* - 1*/).trim();
                }
                sb.replace(start, end + 1, replacement);
                start += replacement.length();
                continue;
            }
            if (!str.startsWith("[") || !str.endsWith("]")) {
//                logger.warn("Page \"" + pagePrototype.getTitle() + "\". Unrecognizable square brackets structure \"[" + str + "]\"");
                sb.replace(start, end + 1, str);
                start = end;
                continue;
            }
            str = str.substring(1, str.length() - 1).trim();
//            end++;

            for (Locale locale : Locale.values()) {
                if (str.startsWith(locale.getText() + ":")) {
                    pagePrototype.addLocalizedPage(locale, str.substring(locale.getText().length() + 1));
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
                    // skip parsing for now, just replace with empty string
                    str = str.substring(pr.length());
                    String replacement = "";
                    int pipePos, odd = 0, pp = str.length();
                    do {
                        odd = 0;
                        pipePos = str.lastIndexOf("|", pp);
                        for (int i = pipePos + 1, n = str.length(); i < n; i++) {
                            if (str.charAt(i) == '[')
                                odd++;
                            else
                                if (str.charAt(i) == ']')
                                    odd--;
                        }
                        pp = pipePos - 1;
                    } while (odd != 0);
                    if (pipePos != -1) {
                        replacement = str.substring(pipePos + 1).trim();
                    } else
                        pipePos = str.length();
                    if (replacement.isEmpty()) {
                        replacement = str.substring(0, pipePos);
                    }
                    sb.replace(start, end + 1, replacement);
                    //start += replacement.length(); // here shouldn't be any increment,
                    // otherwise nested costructions may occure (like [File:a|b[]])
                    //sb.replace(start, end + 1, "");
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

            // remove language definition
            if (str.matches(".*[:].*")) {
                for (Locale locale : Locale.values()) {
                    if (str.startsWith(locale.getText() + ":")) {
                        String s = str.substring(locale.getText().length() + 1);
                        if (!s.isEmpty())
                            pagePrototype.addLocalizedPage(locale, s);
                        break;
                    }
                }
                sb.replace(start, end + 1, "");
                continue;
            }

            // date
            // [[1969]]-[[07-20]]
            if (str.matches("(\\d){4}") && sb.length() > end + 3) {
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
            if (str.matches("(" + allMonthes + " (\\d){1,2})|((\\d){1,2} " + allMonthes + ")") && sb.length() > end + 4) {
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

            if (!addLinks)
                continue;

            String title = normalizePageTitle(link);
            UniquePagePrototype.Link linkObj = pagePrototype.getLink(title);
            if (linkObj == null) {
                linkObj = pagePrototype.addLink(title);
            }
            linkObj.addPosition(start, linkText.length());
        }
        return sb.toString();
    }

    private String normalizePageTitle(String title) {
        title = title.trim();
        return Character.toUpperCase(title.charAt(0)) + title.substring(1);
    }

    private String trimBegin(String text) {
        int len = text.length(), i = 0;
        while ((i < len) && (text.charAt(i) <= ' ')) {
            i++;
        }
        return (i < len) ? text.substring(i) : text;
    }

    private String trimEnd(String text) {
        int len = text.length() - 1;
        while ((len > -1) && (text.charAt(len) <= ' ')) {
            len--;
        }
        return (len > -1) ? text.substring(0, len + 1) : text;
    }

}
