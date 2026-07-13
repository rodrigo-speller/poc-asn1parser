parser grammar ASN1Parser;

options {
    tokenVocab=ASN1Lexer;
}

// -----------------------------------------------------------------------------
// Main rules
// -----------------------------------------------------------------------------

asn1Spec                    : ( ASN1_TAG? anySpec )
                            | ( ASN1_TAG value )
                            ;

asn1Value                   : ASN1_TAG? value;

// -----------------------------------------------------------------------------
// Specification rules
// -----------------------------------------------------------------------------

boolSpec                    : BOOL_TAG      boolValue;
intSpec                     : INT_TAG       intValue;
oidSpec                     : OID_TAG       oidValue;
seqSpec                     : SEQ_TAG       seqValue;
setSpec                     : SET_TAG       setValue;
strSpec                     : STR_TAG       strValue;

// -----------------------------------------------------------------------------
// Value rules
// -----------------------------------------------------------------------------

boolValue                   : BOOL_FALSE | BOOL_TRUE;
intValue                    : INT;
oidValue                    : OID;
strValue                    : STR;
seqValue                    : SEQ_BEGIN vectorItems SEQ_END;
setValue                    : SET_BEGIN vectorItems SET_END;

// -----------------------------------------------------------------------------
// Aggregate rules
// -----------------------------------------------------------------------------

anySpec                     : boolSpec
                            | intSpec
                            | oidSpec
                            | seqSpec
                            | setSpec
                            | strSpec
                            ;

anyValue                    : boolValue
                            | intValue
                            | oidValue
                            | seqValue
                            | setValue
                            | strValue
                            ;

value                       : anySpec
                            | anyValue
                            ;

vectorItems                 : ( vectorItem ( VEC_SEPARATOR vectorItem )* )?;

vectorItem                  : tagModifier* value;

tagModifier                 : (
                                TAG_BEGIN
                                    explicit    = TAG_EXPLICIT?
                                    class       = TAG_CLASS?
                                    number      = TAG_NUMBER
                                TAG_END
                            );

// -----------------------------------------------------------------------------
