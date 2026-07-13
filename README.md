# ASN1 Parser

This project is a proof of concept that uses ANTLR 4 and Bouncy Castle to parse lightweight ASN.1 strings into ASN.1 objects.

## Exemples

### Basic ASN.1 Example

```asn1

BasicExample ::= SEQUENCE {
    boolValue BOOLEAN,
    intValue INTEGER,
    oidValue OBJECT IDENTIFIER,
    setValue SET {
        boolValue BOOLEAN
    }
}
```

Full ASN.1 sequence representation:

```
ASN1:
    SEQ: {
        BOOL:   true,
        INT:    123,
        OID:    1.2.3,
        SET:    [
            BOOL: false
        ]
    }
```

Compact ASN.1 sequence representation:

```
{
    true,
    123,
    1.2.3,
    [
        false
    ]
}
```

### RFC 5280 CRL Distribution Points Example

Below is an example of a CRLDistributionPoints object defined in
[RFC 5280, section 4.2.1.13](https://datatracker.ietf.org/doc/html/rfc5280#section-4.2.1.13).

```asn1
# RFC 5280: PKIX Certificate and CRL Profile
# Section 4.2.1.13:  CRL Distribution Points

CRLDistributionPoints ::= SEQUENCE SIZE (1..MAX) OF DistributionPoint

DistributionPoint ::= SEQUENCE {
        distributionPoint       [0]     DistributionPointName OPTIONAL,
        reasons                 [1]     ReasonFlags OPTIONAL,
        cRLIssuer               [2]     GeneralNames OPTIONAL
}

DistributionPointName ::= CHOICE {
        fullName                [0]     GeneralNames,
        nameRelativeToCRLIssuer [1]     RelativeDistinguishedName
}

GeneralNames ::= SEQUENCE SIZE (1..MAX) OF GeneralName

GeneralName ::= CHOICE {
    otherName                 [0]  AnotherName,
    rfc822Name                [1]  IA5String,
    dNSName                   [2]  IA5String,
    x400Address               [3]  ORAddress,
    directoryName             [4]  Name,
    ediPartyName              [5]  EDIPartyName,
    uniformResourceIdentifier [6]  IA5String,
    iPAddress                 [7]  OCTET STRING,
    registeredID              [8]  OBJECT IDENTIFIER
}
```

When ASN.1 type declararion is optional, the following is a valid ASN.1 string
that represents a CRLDistributionPoints object:

```
//  CRLDistributionPoints ::= SEQUENCE SIZE (1..MAX) OF DistributionPoint
{
    // DistributionPoint ::= SEQUENCE {
    {
        // distributionPoint       [0]     DistributionPointName OPTIONAL, ... }
        // DistributionPointName ::= CHOICE {
        <EXP 0>: 
            //      fullName                [0]     GeneralNames, ... }
            //
            // GeneralNames ::= SEQUENCE SIZE (1..MAX) OF GeneralName
            <0> {
                // GeneralName ::= CHOICE { ...
                //      uniformResourceIdentifier [6]  IA5String, ... }
                6: IA5:"https://datatracker.ietf.org/doc/html/rfc5280"
            }
    }
}
```
