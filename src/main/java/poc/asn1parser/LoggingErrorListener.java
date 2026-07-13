package poc.asn1parser;

import java.util.*;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.*;
import org.slf4j.*;

public class LoggingErrorListener implements ANTLRErrorListener {

    private final Logger log;

    public LoggingErrorListener(Logger log) {
        if (log == null) {
            throw new IllegalArgumentException("log is null");
        }
        this.log = log;
    }

    @Override
    public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol, int line, int charPositionInLine,
            String msg, RecognitionException e) {
        var recognizerClazz = recognizer.getClass();
        log.debug("ANTLR: Syntar error {}({}:{}): {}", recognizerClazz.getName(), line, charPositionInLine, msg);
    }

    @Override
    public void reportAmbiguity(Parser recognizer, DFA dfa, int startIndex, int stopIndex, boolean exact,
            BitSet ambigAlts, ATNConfigSet configs) {
        var context = recognizer.getRuleContext();
        var contextClazz = context.getClass();
        log.debug("ANTLR: Ambiguity report: {}({}:{}), exact={}", contextClazz.getName(), startIndex, stopIndex, exact);
    }

    @Override
    public void reportAttemptingFullContext(Parser recognizer, DFA dfa, int startIndex, int stopIndex,
            BitSet conflictingAlts, ATNConfigSet configs) {
        var context = recognizer.getRuleContext();
        var contextClazz = context.getClass();
        log.debug("ANTLR: Attempting full context report: {}({}:{})", contextClazz.getName(), startIndex, stopIndex);
    }

    @Override
    public void reportContextSensitivity(Parser recognizer, DFA dfa, int startIndex, int stopIndex, int prediction,
            ATNConfigSet configs) {
        var context = recognizer.getRuleContext();
        var contextClazz = context.getClass();
        log.debug("ANTLR: Context sensitivity report: " + contextClazz + "(" + startIndex + ":" + stopIndex + "), prediction=" + prediction);
    }

}
