package wikinet.wiki.parser.impl;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import wikinet.db.model.Locale;
import wikinet.wiki.parser.PagePrototypeBuilder;
import wikinet.wiki.parser.ParseException;
import wikinet.wiki.parser.prototype.CategoryPagePrototype;
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
public class PagePrototypeBuilderImpl implements PagePrototypeBuilder {

    private static Logger logger = Logger.getLogger(PagePrototypeBuilderImpl.class);

    private static final Pattern NOWIKI = Pattern.compile("(<nowiki>).*?(</ ?nowiki>)");
    private static final Pattern PRE = Pattern.compile("(<pre>).*?(</ ?pre>)");

    private static final Pattern[] REGEX_PATTERNS_TO_REMOVE = {
            Pattern.compile("(<math).*?(/[ ]*math>)", Pattern.DOTALL),
            Pattern.compile("[']{2,5}"),
            Pattern.compile("[~]{3,5}"),
            Pattern.compile("[-]{4}"),
            Pattern.compile("[=]{2,4}.*?[=]{2,4}"),
            Pattern.compile("(<ref([^>])*?/>)|((<ref).*?(/[ ]*ref>))", Pattern.DOTALL),
            Pattern.compile("(<).*?(>)", Pattern.DOTALL)
    };

    private static final String[] NAMESPACES_TO_SKIP =
            {"Media:", "Special:", "main:", "Talk:", "User:", "User talk:", "Meta:",
            "Meta talk:", "File:", "File talk:", "MediaWiki:", "MediaWiki talk:", "Template:", "Template talk:",
            "Help:", "Help talk:", "Category talk:", "Portal:", "Portal talk:", "media:", "Image:", "Wikipedia:"};

    @Override
    public PagePrototype build(String title, String text) {
        if (!isPageTitleValid(title)) {
            if (logger.isDebugEnabled())
                logger.debug("Page \"" + title + "\" skipped (wrong page title).");
            return null;
        }

        title = getWithFirstCharInUpperCase(title.trim());
        text = getUTF8(text);

        if (title.startsWith("Category:")) {
            CategoryPagePrototype categoryPagePrototype = new CategoryPagePrototype(title.substring(9).trim());
            try {
                parseCategoryPage(categoryPagePrototype, text);
            } catch (Exception ex) {
                if (ex instanceof ParseException)
                    logger.error("Page \"" + title + "\" : " + ex.getMessage());
                else
                    logger.error("Page \"" + title + "\"", ex);
                return null;
            }
            return categoryPagePrototype;
        }

        if (text.indexOf("#REDIRECT [[") != -1) {
            int pos = text.indexOf("[[");
            String redirectedPageTitle = text.substring(pos + 2, text.indexOf("]]", pos + 2)).trim();

            if (!isPageTitleValid(redirectedPageTitle)) {
                if (logger.isDebugEnabled())
                    logger.debug("Page \"" + title + "\" skipped " +
                            "(wrong redirected page title \"" + redirectedPageTitle + "\").");
                return null;
            }

            redirectedPageTitle = getWithFirstCharInUpperCase(redirectedPageTitle);
            return new RedirectPagePrototype(title, new PagePrototype(redirectedPageTitle));
        }

        UniquePagePrototype pagePrototype = new UniquePagePrototype(title);
        try {
            parseUniquePage(pagePrototype, text);
        } catch (Exception ex) {
            if (ex instanceof ParseException)
                logger.error("Page \"" + pagePrototype + "\" : " + ex.getMessage());
            else
                logger.error("Page \"" + pagePrototype + "\"", ex);
            return null;
        }
        return pagePrototype;
    }

    private void parseCategoryPage(final CategoryPagePrototype pagePrototype, String text) {
        if (text.isEmpty())
            return;
        StringBuilder sb = new StringBuilder(text);
        int start = 0, end;
        while ((start = sb.indexOf("[[Category:", start)) != -1) {
            end = sb.indexOf("]", start + 11);
            if (end == -1)
                break;
            String category = sb.substring(start + 11, end);
            category = cleanFromTrash(category);
            pagePrototype.addParentCategory(category);
            start = end;
        }
    }
    
    private void parseUniquePage(UniquePagePrototype pagePrototype, String text) {
        StringBuilder buffer = new StringBuilder();
        Matcher nowikiMatcher = NOWIKI.matcher(text);
        Matcher preMatcher = PRE.matcher(text);
        int index = 0;
        do {
            int nowikipos = nowikiMatcher.find(index) ? nowikiMatcher.start() : -1;
            int prepos = preMatcher.find(index) ? preMatcher.start() : -1;
            if (nowikipos == -1 && prepos == -1) {
                if (index == 0) {
                    processTextBlock(pagePrototype, buffer, text);
                    processPageData(pagePrototype, buffer);
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

            processTextBlock(pagePrototype, buffer, text.substring(index, startGroup));
            String between = group.substring(group.indexOf(">") + 1, group.lastIndexOf("<"));
            buffer.append(between);
            index = endGroup;
        } while(true);
        processTextBlock(pagePrototype, buffer, text.substring(index));
        processPageData(pagePrototype, buffer);
    }

    private void processPageData(UniquePagePrototype pagePrototype, StringBuilder buffer) {
        String data = trim(buffer);
        int nlp = data.indexOf("\n");
        if (nlp == -1) {
            pagePrototype.setFirstParagraph(data);
        } else {
            pagePrototype.setFirstParagraph(data.substring(0, nlp));
            pagePrototype.setText(data.substring(nlp + 1));
        }
    }

    private void processTextBlock(UniquePagePrototype pagePrototype, StringBuilder buffer, String block) {
        block = block.replace("&lt;", "<");
        block = block.replace("&gt;", ">");
        block = block.replace("&apos;", "'");
        block = block.replace("&quot;", "\"");
        block = block.replace("&amp;", "&");
/*
        StringUtils.replaceEach(block, new String[] {"'''''", "''''", "'''", "''", "~~~~~", "~~~~", "~~~", "----"},
                new String[] {"", "", "", "", "", "", "", ""});
*/
        for (Pattern pattern : REGEX_PATTERNS_TO_REMOVE) {
            block = pattern.matcher(block).replaceAll("");
        }
        block = processCurlyBrackets(pagePrototype, block);
        block = removeDuplicatedWhitespaces(block);
        block = processSquareBrackets(pagePrototype, block);
        buffer.append(block);
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
    private String processSquareBrackets(final UniquePagePrototype pagePrototype, String text) {
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
                sb.replace(start, end + 1, str);
                start = end;
                continue;
            }
            str = str.substring(1, str.length() - 1).trim();

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
/*
                    str = str.substring(pr.length());
                    String replacement = "";
                    int pipePos, odd, pp = str.length();
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
*/
                    sb.replace(start, end + 1, "");
                    continue root_cycle;
                }
            }

            // category
            if (str.startsWith("Category:")) {
                String category = str.substring(9).trim();
                category = cleanFromTrash(category);
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

            if (start > getFirstParagraphEndPosition(sb))
                continue;

            String title = getWithFirstCharInUpperCase(link);
            UniquePagePrototype.Link linkObj = pagePrototype.getLink(title);
            if (linkObj == null) {
                linkObj = pagePrototype.addLink(title);
            }
            linkObj.addPosition(start, linkText.length());
        }
        return sb.toString();
    }

    private int getFirstParagraphEndPosition(StringBuilder sb) {
        return getFirstParagraphEndPosition(sb, 0);
    }

    private int getFirstParagraphEndPosition(StringBuilder sb, int startIndex) {
        int pos = sb.indexOf("\n", startIndex);
        if (pos == -1)
            return sb.length();
        for (int i = startIndex; i < pos; i++) {
            if (!Character.isWhitespace(sb.charAt(i))) {
                return pos;
            }
        }
        return getFirstParagraphEndPosition(sb, pos + 1);
    }

    public String trim(StringBuilder sb) {
        int len = sb.length();
        int st = 0;
        while ((st < len) && (sb.charAt(st) <= ' ')) {
            st++;
        }
        while ((st < len) && (sb.charAt(len - 1) <= ' ')) {
            len--;
        }
        return ((st > 0) || (len < sb.length())) ? sb.substring(st, len) : sb.toString();
    }


    private String removeDuplicatedWhitespaces(String text) {
        StringBuilder result = new StringBuilder();
        char c, prev = (char) -1;
        for (int i = 0; i < text.length(); i++) {
            c = text.charAt(i);
            if ((c == '\n' && prev != '\n') || !Character.isWhitespace(c) || !Character.isWhitespace(prev)) {
                result.append(c);
                prev = c;
            }
        }
        return result.toString();
    }

    private String cleanFromTrash(String title) {
        String result = removePipe(title);
        result = removeSharp(result);
        return result.trim();
    }

    private String removePipe(String title) {
        String result = title;
        int pos = title.indexOf("|");
        if (pos != -1)
            result = result.substring(0, pos);
        return result;
    }

    private String removeSharp(String title) {
        String result = title;
        int pos = title.indexOf("#");
        if (pos != -1)
            result = result.substring(0, pos);
        return result;
    }

    private String getWithFirstCharInUpperCase(String title) {
        title = title.trim();
        return Character.toUpperCase(title.charAt(0)) + title.substring(1);
    }

    private boolean isPageTitleValid(String title) {
        for (String s : NAMESPACES_TO_SKIP) {
            if (title.startsWith(s))
                return false;
        }
        int p = title.lastIndexOf(")");
        if (p != -1) {
            if (!title.substring(p + 1).trim().isEmpty())
                return false;
        }
        if (title.trim().isEmpty())
            return false;
        return true;
    }

    public static String getUTF8(String str) {
        if (str == null)
            return null;

        StringBuilder sb = new StringBuilder();
        char ch;

        for (int i = 0, end = str.length(); i < end; i++) {
            ch = str.charAt(i);
            if ((ch >= 0x0020 && ch <= 0xD7FF) ||
                (ch >= 0xE000 && ch <= 0xFFFD) ||
                 ch == 0x0009 ||
                 ch == 0x000A ||
                 ch == 0x000D) {
                sb.append(ch);
            } 
        }
        return sb.toString();
    }

}
