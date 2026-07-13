package poc.asn1parser;

import java.math.*;
import java.nio.charset.*;

import org.bouncycastle.asn1.*;

import poc.asn1parser.ASN1Parser.*;

public class ASN1EncodableVisitor extends ASN1ParserBaseVisitor<ASN1Encodable> {

    private static Charset UTF_32BE;

    private static Charset getUtf32beCharset() {
        if (UTF_32BE == null) {
            UTF_32BE = Charset.forName("UTF-32BE");
        }
        return UTF_32BE;
    }

    @Override
    public ASN1Encodable visitBoolValue(BoolValueContext ctx) {
        if (ctx.BOOL_TRUE() != null) {
            return ASN1Boolean.TRUE;
        }

        return ASN1Boolean.FALSE;
    }

    @Override
    public ASN1Encodable visitIntValue(IntValueContext ctx) {
        return new ASN1Integer(new BigInteger(ctx.getText()));
    }

    @Override
    public ASN1Encodable visitOidValue(OidValueContext ctx) {
        return new ASN1ObjectIdentifier(ctx.getText());
    }

    @Override
    public ASN1Encodable visitSeqValue(SeqValueContext ctx) {
        var vector = new ASN1EncodableVector();
        for (var item : ctx.vectorItems().vectorItem()) {
            vector.add(visit(item));
        }

        return new DERSequence(vector);
    }

    @Override
    public ASN1Encodable visitSetValue(SetValueContext ctx) {
        var vector = new ASN1EncodableVector();
        for (var item : ctx.vectorItems().vectorItem()) {
            vector.add(visit(item));
        }

        return new DERSet(vector);
    }

    @Override
    public ASN1Encodable visitStrValue(StrValueContext ctx) {
        var value = ctx.STR().getText();

        value = value
            .substring(1, value.length() - 1)
            .replace("\"\"", "\"");

        return new DERIA5String(value, true);
    }

    @Override
    public ASN1Encodable visitStrSpec(StrSpecContext ctx) {
        var value = ctx.strValue().getText();

        value = value
            .substring(1, value.length() - 1)
            .replace("\"\"", "\"");

        /***********************************************************************
         * Implementation note:
         * ---------------------------------------------------------------------
         * Only a subset of string types is supported, as the BouncyCastle
         * library does not provide validation for all string types. Extending
         * the support for additional string types would require implementing
         * custom validation logic for each string type.
         **********************************************************************/
        return switch (ctx.STR_TAG().getText().toLowerCase()) {
            case "bmp:", "bmpstring:"
                -> new DERBMPString(value);
            case "general:", "generalstring:"
                -> throw new UnsupportedOperationException("Unsupported string type");
            case "graphic:", "graphicstring:"
                -> throw new UnsupportedOperationException("Unsupported string type");
            case "iso646:", "iso646string:"
                -> throw new UnsupportedOperationException("Unsupported string type");
            case "ia5:", "ia5string:"
                -> new DERIA5String(value, true);
            case "numeric:", "numericstring:"
                -> new DERNumericString(value, true);
            case "printable:", "printablestring:"
                -> new DERPrintableString(value, true);
            case "t61:", "t61string:"
                -> throw new UnsupportedOperationException("Unsupported string type");
            case "teletex:", "teletexstring:"
                -> throw new UnsupportedOperationException("Unsupported string type");
            case "universal:", "universalstring:"
                -> new DERUniversalString(value.getBytes(getUtf32beCharset()));
            case "utf8:", "utf8string:"
                -> new DERUTF8String(value);
            case "videotex:", "videotexstring:"
                -> throw new UnsupportedOperationException("Unsupported string type");
            case "visible:", "visiblestring:"
                -> throw new UnsupportedOperationException("Unsupported string type");
            default -> throw new IllegalStateException("Unsupported tag type");
        };
    }

    @Override
    public ASN1Encodable visitVectorItem(VectorItemContext ctx) {
        var value = visit(ctx.value());

        // Revert the order to compose the tagged object from the innermost to
        // the outermost tag.
        for (var i = ctx.tagModifier().size() - 1; i >= 0; i--) {
            var modifier = ctx.tagModifier(i);
            var explicit = modifier.explicit != null;
            var number = Integer.parseInt(modifier.number.getText());
            var tagClass = modifier.class_ == null
                ? BERTags.CONTEXT_SPECIFIC
                : switch(modifier.class_.getText().toLowerCase()) {
                    case "a", "app", "application" -> BERTags.APPLICATION;
                    case "c", "context" -> BERTags.CONTEXT_SPECIFIC;
                    case "p", "private" -> BERTags.PRIVATE;
                    default -> throw new IllegalStateException("Unsupported tag class");
                };

            value = new DERTaggedObject(explicit, tagClass, number, value);
        }

        return value;
    }

}
