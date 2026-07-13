package poc.asn1parser;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.InputMismatchException;
import org.antlr.v4.runtime.misc.*;
import org.bouncycastle.asn1.*;
import org.slf4j.*;

public class ASN1EncodableParser {

    private final Logger log = LoggerFactory.getLogger(ASN1EncodableParser.class);

    record Context<TLexer extends Lexer, TParser extends Parser>(
        TLexer lexer,
        TParser parser
    ) { }

    public ASN1Encodable parse(String payload) {
        if (payload == null || payload.isBlank()) {
            throw new IllegalArgumentException("Empty payload");
        }

        var context = createParserFor(payload);
        var parser = context.parser;
        var tree = parser.asn1Spec();
        expectEOF(parser);

        return createVisitor().visit(tree);
    }

    public ASN1Encodable parseValue(String payload) {
        if (payload == null || payload.isBlank()) {
            throw new IllegalArgumentException("Empty payload");
        }

        var context = createParserFor(payload);
        var parser = context.parser;
        var tree = parser.asn1Value();
        expectEOF(parser);

        return createVisitor().visit(tree);
    }

    public <T extends ASN1Encodable> T parseValue(String payload, Class<T> expectedType) {
        var parsed = parseValue(payload);

        if (!expectedType.isInstance(parsed)) {
            throw new IllegalArgumentException(
                String.format(
                    "Expected type %s but got %s",
                    expectedType.getName(),
                    parsed.getClass().getName()
                )
            );
        }

        return expectedType.cast(parsed);
    }

    private Context<ASN1Lexer, ASN1Parser> createParserFor(String input) {
        var lexer = new ASN1Lexer(CharStreams.fromString(input));
        lexer.removeErrorListeners();
        lexer.addErrorListener(new LoggingErrorListener(log));
        lexer.addErrorListener(new BailSyntaxErrorListener());

        var parser = new ASN1Parser(new CommonTokenStream(lexer));
        parser.setErrorHandler(new BailErrorStrategy());
        parser.removeErrorListeners();
        parser.addErrorListener(new LoggingErrorListener(log));
        parser.addErrorListener(new BailSyntaxErrorListener());

        return new Context<>(lexer, parser);
    }

    private ASN1EncodableVisitor createVisitor() {
        return new ASN1EncodableVisitor();
    }

    private void expectEOF(Parser parser) {
        if (parser.getTokenStream().LA(1) != Token.EOF) {
            throw new ParseCancellationException(new InputMismatchException(parser));
        }
    }

}
