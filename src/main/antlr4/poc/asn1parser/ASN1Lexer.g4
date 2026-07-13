lexer grammar ASN1Lexer;

options {
    caseInsensitive=true;
}

// -----------------------------------------------------------------------------
// Specification tags
// -----------------------------------------------------------------------------

ASN1_TAG                : ASN1_TAG_TEXT;
BOOL_TAG                : BOOL_TAG_TEXT;
INT_TAG                 : INT_TAG_TEXT;
OID_TAG                 : OID_TAG_TEXT;
SEQ_TAG                 : SEQ_TAG_TEXT;
SET_TAG                 : SET_TAG_TEXT;
STR_TAG                 : STR_TAG_TEXT;

SEQ_BEGIN               : '{';
SEQ_END                 : '}';
SET_BEGIN               : '[';
SET_END                 : ']';
VEC_SEPARATOR           : ',';

TAG_BEGIN               : '<'                   -> mode(TAG_MODE);

BOOL_TRUE               : BOOL_TRUE_VALUE;
BOOL_FALSE              : BOOL_FALSE_VALUE;
OID                     : OID_VALUE;
INT                     : '-'? UINT;

STR                     : DQUOTED_STRING;

DEFAULT_COMMENT         : COMMENT               -> skip;
DEFAULT_WS              : WHITESPACE            -> skip;

// -----------------------------------------------------------------------------
// Structure Mode
// -----------------------------------------------------------------------------

mode TAG_MODE;

// Implicit is omitted, as it is the default tagging.
TAG_EXPLICIT            : 'e'
                        | 'exp'
                        | 'explicit'
                        ;

// Universal is omitted, as it is the default class.
TAG_CLASS               : 'a'
                        | 'app'
                        | 'application'
                        | 'c'
                        | 'context'
                        | 'p'
                        | 'private'
                        ;

TAG_NUMBER              : UINT;

TAG_END                 : '>'                   -> mode(DEFAULT_MODE);

TAG_WS                  : WHITESPACE            -> skip;

// -----------------------------------------------------------------------------
// Fragment rules
// -----------------------------------------------------------------------------

// Tags
fragment ASN1_TAG_TEXT      : 'asn1:'                               ;
fragment BOOL_TAG_TEXT      : 'bool:'       | 'boolean:'            ;
fragment INT_TAG_TEXT       : 'int:'        | 'integer:'            ;
fragment OID_TAG_TEXT       : 'OID:'                                ;

// Structure tags
fragment SEQ_TAG_TEXT       : 'seq:'        | 'sequence:'           ;
fragment SET_TAG_TEXT       : 'set:'                                ;

// String tags
fragment BMP_TAG_TEXT       : 'bmp:'        | 'bmpstring:'          ;
fragment GENERAL_TAG_TEXT   : 'general:'    | 'generalstring:'      ;
fragment GRAPHIC_TAG_TEXT   : 'graphic:'    | 'graphicstring:'      ;
fragment IA5_TAG_TEXT       : 'ia5:'        | 'ia5string:'          ;
fragment ISO646_TAG_TEXT    : 'iso646:'     | 'iso646string:'       ;
fragment NUMERIC_TAG_TEXT   : 'numeric:'    | 'numericstring:'      ;
fragment PRINTABLE_TAG_TEXT : 'printable:'  | 'printablestring:'    ;
fragment T61_TAG_TEXT       : 't61:'        | 't61string:'          ;
fragment TELETEX_TAG_TEXT   : 'teletex:'    | 'teletexstring:'      ;
fragment UNIVERSAL_TAG_TEXT : 'universal:'  | 'universalstring:'    ;
fragment UTF8_TAG_TEXT      : 'utf8:'       | 'utf8string:'         ;
fragment VIDEOTEX_TAG_TEXT  : 'videotex:'   | 'videotexstring:'     ;
fragment VISIBLE_TAG_TEXT   : 'visible:'    | 'visiblestring:'      ;

fragment STR_TAG_TEXT       : BMP_TAG_TEXT
                            | GENERAL_TAG_TEXT
                            | GRAPHIC_TAG_TEXT
                            | IA5_TAG_TEXT
                            | ISO646_TAG_TEXT
                            | NUMERIC_TAG_TEXT
                            | PRINTABLE_TAG_TEXT
                            | T61_TAG_TEXT
                            | TELETEX_TAG_TEXT
                            | UNIVERSAL_TAG_TEXT
                            | UTF8_TAG_TEXT
                            | VIDEOTEX_TAG_TEXT
                            | VISIBLE_TAG_TEXT
                            ;

// Values
fragment BOOL_TRUE_VALUE    : 'true'  | 'yes' | 'on'  | 'high' | 'up'   | 'enable'  | 'enabled' ;
fragment BOOL_FALSE_VALUE   : 'false' | 'no'  | 'off' | 'low'  | 'down' | 'disable' | 'disabled';
fragment OID_ROOT           : ( ( [0-1] '.' [1-3]?[0-9] ) | ( [2] '.' UINT ) );
fragment OID_VALUE          : OID_ROOT ( '.' UINT )+; // At least one branch to avoid floating point numbers
                                                             // collision.
fragment DQUOTED_STRING     : '"' ( ~'"' | '""' )* '"';
fragment UINT               : [0] | [1-9][0-9]*;

// Skips
fragment COMMENT            : ( ( '#' | '//' ) ~[\r\n]* );
fragment WHITESPACE         : [ \t\r\n]+;

// -----------------------------------------------------------------------------
