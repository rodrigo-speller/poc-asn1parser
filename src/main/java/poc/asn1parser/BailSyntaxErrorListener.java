package poc.asn1parser;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.misc.*;

public class BailSyntaxErrorListener extends BaseErrorListener {

    @Override
    public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol, int line, int charPositionInLine,
        String msg, RecognitionException e) {
        throw new ParseCancellationException("Syntar error (" + line + ":" + charPositionInLine + "): " + msg, e);
    }

}
