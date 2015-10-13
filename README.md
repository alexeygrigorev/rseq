[![Build Status](https://travis-ci.org/alexeygrigorev/rseq.svg)](https://travis-ci.org/alexeygrigorev/rseq)
[![MavenCentral](https://maven-badges.herokuapp.com/maven-central/com.alexeygrigorev/rseq/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.alexeygrigorev/rseq/)
[![Coverage Status](https://coveralls.io/repos/alexeygrigorev/rseq/badge.svg)](https://coveralls.io/r/alexeygrigorev/rseq)


# **rseq**: Pattern matching made easier 

**rseq** is a Regular-Expression-like language for operating on sequences (Lists) of any Java objects

    Pattern pattern = Pattern.create(oneLetterRegexp.captureAs("ID"),
                                     eq("is"), eq("the").or(eq("a")).optional(), 
                                     anything.captureAs("DEF"));

It is available on maven central:

    <dependency>
      <groupId>com.alexeygrigorev</groupId>
      <artifactId>rseq</artifactId>
      <version>0.0.1</version>
    </dependency>

### Features 

- Variable and group capturing, optional, the Kneele star
- Deals with Java Beans out of the box
- Extensible interface for writing your own matchers

See a [tutorial](https://github.com/alexeygrigorev/rseq/wiki/Tutorial) for more details
