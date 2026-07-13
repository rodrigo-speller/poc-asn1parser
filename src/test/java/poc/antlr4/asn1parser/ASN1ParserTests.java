package poc.antlr4.asn1parser;

import static org.junit.jupiter.api.Assertions.*;

import java.util.*;

import org.bouncycastle.asn1.*;
import org.bouncycastle.asn1.x509.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import org.slf4j.*;

import poc.asn1parser.*;

public class ASN1ParserTests {

    Logger log = org.slf4j.LoggerFactory.getLogger(ASN1ParserTests.class);

    @ParameterizedTest()
    @CsvSource({
        " TRUE                      , ASN1Boolean           , BOOL:true                             ",
        " FALSE                     , ASN1Boolean           , BOOLEAN:false                         ",
        " FALSE                     , ASN1Boolean           , ASN1:BOOL:false                       ",
        " TRUE                      , ASN1Boolean           , ASN1:BOOLEAN:true                     ",
        " 0                         , ASN1Integer           , INT:0                                 ",
        " 10                        , ASN1Integer           , INT:10                                ",
        " 1000                      , ASN1Integer           , INT:1000                              ",
        " 0                         , ASN1Integer           , INTEGER:0                             ",
        " 12345                     , ASN1Integer           , INTEGER:12345                         ",
        " 12345                     , ASN1Integer           , ASN1:INT:12345                        ",
        " 25                        , ASN1Integer           , ASN1:INTEGER:25                       ",
        " 1.2.3                     , ASN1ObjectIdentifier  , OID:1.2.3                             ",
        " 2.456.789.1011.121314     , ASN1ObjectIdentifier  , ASN1:OID:2.456.789.1011.121314        ",
        " []                        , ASN1Set               , SET:[]                                ",
        " [TRUE]                    , ASN1Set               , SET:[BOOL:true]                       ",
        " [FALSE]                   , ASN1Set               , ASN1:SET:[BOOL:false]                 ",
    })
    void should_parse_multiple_types(String expected, String expectedType, String input) throws Exception {
        var parser = new ASN1EncodableParser();
        var parsed = parser.parseValue(input);

        var type = Class.forName("org.bouncycastle.asn1." + expectedType);
        assertInstanceOf(type, parsed);
        assertEquals(expected, parsed.toString());
    }

    @ParameterizedTest()
    @CsvSource({
        " TRUE      , BOOL:true             ",
        " TRUE      , BOOL:yes              ",
        " TRUE      , BOOL:on               ",
        " TRUE      , BOOL:high             ",
        " TRUE      , BOOL:up               ",
        " TRUE      , BOOL:enable           ",
        " TRUE      , BOOL:enabled          ",
        " FALSE     , BOOL:false            ",
        " FALSE     , BOOL:no               ",
        " FALSE     , BOOL:off              ",
        " FALSE     , BOOL:low              ",
        " FALSE     , BOOL:down             ",
        " FALSE     , BOOL:disable          ",
        " FALSE     , BOOL:disabled         ",
        " TRUE      , BOOLEAN:true          ",
        " TRUE      , BOOLEAN:yes           ",
        " TRUE      , BOOLEAN:on            ",
        " TRUE      , BOOLEAN:high          ",
        " TRUE      , BOOLEAN:up            ",
        " TRUE      , BOOLEAN:enable        ",
        " TRUE      , BOOLEAN:enabled       ",
        " FALSE     , BOOLEAN:false         ",
        " FALSE     , BOOLEAN:no            ",
        " FALSE     , BOOLEAN:off           ",
        " FALSE     , BOOLEAN:low           ",
        " FALSE     , BOOLEAN:down          ",
        " FALSE     , BOOLEAN:disable       ",
        " FALSE     , BOOLEAN:disabled      ",
        " TRUE      , ASN1:BOOL:true        ",
        " FALSE     , ASN1:BOOL:false       ",
        " TRUE      , ASN1:BOOLEAN:true     ",
        " FALSE     , ASN1:BOOLEAN:false    ",
    })
    void should_parse_booleans(String expected, String input) {
        var parser = new ASN1EncodableParser();
        var value = parser.parseValue(input);

        assertInstanceOf(ASN1Boolean.class, value);
        assertEquals(expected, value.toString());
    }

    @ParameterizedTest()
    @CsvSource({
        " 0         , INT:0             ",
        " 1         , INT:1             ",
        " 5         , INT:5             ",
        " 10        , INT:10            ",
        " 25        , INT:25            ",
        " 1000      , INT:1000          ",
        " 12345     , INT:12345         ",
        " 0         , INTEGER:0         ",
        " 1         , INTEGER:1         ",
        " 5         , INTEGER:5         ",
        " 10        , INTEGER:10        ",
        " 25        , INTEGER:25        ",
        " 1000      , INTEGER:1000      ",
        " 12345     , INTEGER:12345     ",
        " 12345     , ASN1:INT:12345    ",
        " 25        , ASN1:INTEGER:25   ",
    })
    void should_parse_integers(String expected, String input) {
        var parser = new ASN1EncodableParser();
        var value = parser.parseValue(input);

        assertInstanceOf(ASN1Integer.class, value);
        assertEquals(expected, value.toString());
    }

    @ParameterizedTest()
    @CsvSource({
        "123456789012345678901234567890123456789012345678901234567890",
        "12345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890"
    })
    void should_parse_large_integers(String expected) {
        var parser = new ASN1EncodableParser();
        var value = parser.parseValue("INT:" + expected);

        assertInstanceOf(ASN1Integer.class, value);
        assertEquals(expected, value.toString());
    }

    @ParameterizedTest()
    @CsvSource({
        " 1.2.3                     , OID:1.2.3                         ",
        " 1.2.3.4                   , OID:1.2.3.4                       ",
        " 1.2.3.4.5                 , OID:1.2.3.4.5                     ",
        " 1.39.789.1011.121314      , OID:1.39.789.1011.121314          ",
        " 2.456.789.1011.121314     , OID:2.456.789.1011.121314         ",
        " 2.456.789.1011.121314     , ASN1:OID:2.456.789.1011.121314    ",
    })
    void should_parse_oids(String expected, String input) {
        var parser = new ASN1EncodableParser();
        var oid = parser.parseValue(input);

        assertInstanceOf(ASN1ObjectIdentifier.class, oid);
        assertEquals(expected, oid.toString());
    }

    @ParameterizedTest()
    @CsvSource({
        " '[]'          , 0     , 'SET:[]'          ",
        " '[TRUE]'      , 1     , 'SET:[BOOL:true]' ",
        " '[123]'       , 1     , 'SET:[INT:123]'   ",
        " '[1.2.3]'     , 1     , 'SET:[OID:1.2.3]' ",
    })
    void should_parse_sets(String expected, int expectedSize, String input) {
        var parser = new ASN1EncodableParser();
        var parsed = parser.parseValue(input, ASN1Set.class);

        assertInstanceOf(ASN1Set.class, parsed);
        assertEquals(expectedSize, parsed.size());
        assertEquals(expected, parsed.toString());
    }

    @Test()
    void should_parse_sets_with_multiple_values() {
        var parser = new ASN1EncodableParser();
        var parsed = parser.parseValue(
            "SET:[BOOL:true, INT:123, OID:1.2.3, SET:[BOOL:false]]",
            ASN1Set.class
        );

        var items = Arrays.asList(parsed.toArray());

        assertInstanceOf(ASN1Set.class, parsed);
        assertEquals(4, parsed.size());

        // Should contain
        assertTrue(items.contains(ASN1Boolean.TRUE));
        assertTrue(items.contains(new ASN1Integer(123)));
        assertTrue(items.contains(new ASN1ObjectIdentifier("1.2.3")));
        assertTrue(items.contains(new DERSet(ASN1Boolean.FALSE)));

        // Should not contain
        assertFalse(items.contains(ASN1Boolean.FALSE));
        assertFalse(items.contains(new ASN1Integer(321)));
        assertFalse(items.contains(new ASN1ObjectIdentifier("1.2.4")));
        assertFalse(items.contains(new DERSet(ASN1Boolean.TRUE)));
    }

    @ParameterizedTest()
    @CsvSource({
        " TRUE                      , ASN1Boolean           , true                  ",
        " TRUE                      , ASN1Boolean           , true                  ",
        " FALSE                     , ASN1Boolean           , false                 ",
        " TRUE                      , ASN1Boolean           , yes                   ",
        " FALSE                     , ASN1Boolean           , off                   ",
        " 0                         , ASN1Integer           , 0                     ",
        " 10                        , ASN1Integer           , 10                    ",
        " 25                        , ASN1Integer           , 25                    ",
        " 1000                      , ASN1Integer           , 1000                  ",
        " 12345                     , ASN1Integer           , 12345                 ",
        " 1.2.3                     , ASN1ObjectIdentifier  , 1.2.3                 ",
        " 2.456.789.1011.121314     , ASN1ObjectIdentifier  , 2.456.789.1011.121314 ",
    })
    void should_parse_literal_values(String expected, String expectedType, String input) throws Exception {
        var parser = new ASN1EncodableParser();
        var parsed = parser.parseValue(input);

        var type = Class.forName("org.bouncycastle.asn1." + expectedType);
        assertInstanceOf(type, parsed);
        assertEquals(expected, parsed.toString());
    }

    @Test
    void should_parse_complex_structure() throws Exception {
        var expected =
            new CRLDistPoint(
                new DistributionPoint[] {
                    new DistributionPoint(
                        new DistributionPointName(
                            new GeneralNames(
                                new GeneralName[] {
                                    new GeneralName(
                                        GeneralName.uniformResourceIdentifier,
                                        "https://datatracker.ietf.org/doc/html/rfc5280"
                                    )
                                }
                            )
                        ),
                        null,
                        null
                    )
                }
            );

        var input = """
            ASN1:
                //  CRLDistributionPoints ::= SEQUENCE SIZE (1..MAX) OF DistributionPoint
                SEQ: {
                    // DistributionPoint ::= SEQUENCE {
                    SEQ: {
                        // distributionPoint       [0]     DistributionPointName OPTIONAL, ... }
                        // DistributionPointName ::= CHOICE {
                        <EXP 0>
                            //      fullName                [0]     GeneralNames, ... }
                            //
                            // GeneralNames ::= SEQUENCE SIZE (1..MAX) OF GeneralName
                            <0> SEQ: {
                                    // GeneralName ::= CHOICE { ...
                                    //      uniformResourceIdentifier [6]  IA5String, ... }
                                    <6> IA5:"https://datatracker.ietf.org/doc/html/rfc5280"
                                }
                    }
                }
            """;

        var parser = new ASN1EncodableParser();
        var parsed = parser.parse(input);

        assertEquals(expected, parsed);
        assertTrue(Arrays.equals(expected.getEncoded(), parsed.toASN1Primitive().getEncoded()));
    }

    @Test
    void should_parse_deeply_nested_structure() throws Exception {
        var expected =
            new DERSequence(new ASN1Encodable[] {
                new ASN1Integer(1),
                new DERSequence(new ASN1Encodable[] {
                    ASN1Boolean.FALSE,
                    new ASN1Integer(2),
                    new DERIA5String("level-2"),
                    new DERSequence(new ASN1Encodable[] {
                        new ASN1ObjectIdentifier("1.2.3.4"),
                        new DERUTF8String("level-3"),
                        new DERSet(new ASN1Encodable[] {
                            // SET does not require ordering.
                            new ASN1Integer(4),
                            new ASN1ObjectIdentifier("2.999.3"),
                            ASN1Boolean.TRUE
                        })
                    })
                }),
                new ASN1ObjectIdentifier("1.2.840.113549"),
                new DERIA5String("root-sibling")
            });

        var input = """
                SEQ: {
                    1,
                    {
                        BOOL:false,
                        2,
                        IA5: "level-2",
                        SEQ: {
                            OID: 1.2.3.4,
                            UTF8: "level-3",
                            [
                                BOOL: true,
                                INT: 4,
                                2.999.3
                            ]
                        }
                    },
                    1.2.840.113549,
                    "root-sibling"
                }
            """;

        var parser = new ASN1EncodableParser();
        var parsed = parser.parse(input);

        assertEquals(expected, parsed);
        assertTrue(Arrays.equals(expected.getEncoded(), parsed.toASN1Primitive().getEncoded()));
    }

}
