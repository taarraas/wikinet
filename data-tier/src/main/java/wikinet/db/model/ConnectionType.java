package wikinet.db.model;

/**
 * @author shyiko
 * @since Feb 27, 2010
 */
public enum ConnectionType {

    ANTONYM("!"), HYPERNYM("@"), INSTANCE_HYPERNYM("@i"), HYPONYM("~"), INSTANCE_HYPONYM("~i"),
    MEMBER_HOLONYM("#m"), SUBSTANCE_HOLONYM("#s"), PART_HOLONYM("#p"), MEMBER_MERONYM("%m"),
    SUBSTANCE_MERONYM("%s"), PART_MERONYM("%p"), ATTRIBUTE("="), DERIVATIONALLY_RELATED_FORM("+"),
    DOMAIN_OF_SYNSET_TOPIC(";c"), MEMBER_OF_THIS_DOMAIN_TOPIC("-c"), DOMAIN_OF_SYNSET_REGION(";r"),
    MEMBER_OF_THIS_DOMAIN_REGION("-r"), DOMAIN_OF_SYNSET_USAGE(";u"), MEMBER_OF_THIS_DOMAIN_USAGE("-u"),
    ENTAILMENT("*"), CAUSE(">"), ALSO_SEE("^"), VERB_GROUP("$"), SIMILAR_TO("&"), PARTICIPLE_OF_VERB("<"),
    PERTAINYM_OR_DERIVED_FROM_ADJECTIVE("\\");

    private String text;

    ConnectionType(String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return text;
    }

    public static ConnectionType parse(String text) {
        for (ConnectionType connectionType : values()) {
            if (connectionType.text.equals(text))
                return connectionType;
        }
        return null;
    }
}
