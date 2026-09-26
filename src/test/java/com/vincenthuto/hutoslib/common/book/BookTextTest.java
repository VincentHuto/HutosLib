package com.vincenthuto.hutoslib.common.book;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class BookTextTest {
    @Test
    void nestedLinksAndFormattingRetainTheSurroundingGate() {
        var text = BookText.parse("Public {wash:3|secret {b}bold{/b} {link:test:guide/intro/pages/a|witness}} end");
        assertTrue(text.diagnostics().isEmpty());
        assertEquals("Public […] end", text.sanitized(level -> false));
        assertEquals("Public secret bold witness end", text.sanitized(level -> level <= 3));
        var link = text.runs().stream().filter(run -> run.target() != null).findFirst().orElseThrow();
        assertFalse(link.readable(level -> false));
        assertTrue(link.readable(level -> level == 3));
        assertTrue(text.runs().stream().anyMatch(run -> run.bold() && run.text().equals("bold")));
    }

    @Test
    void malformedWashHidesTheAffectedParagraphEvenForAnAllAccessPredicate() {
        var text = BookText.parse("Safe paragraph{n}before {wash:abc|do not leak} after{n}Still safe");
        assertFalse(text.diagnostics().isEmpty());
        assertEquals("Safe paragraph\n[…]\nStill safe", text.sanitized(level -> true));
        assertFalse(BookText.parse("Broken {wash:2|secret").sanitized(level -> true).contains("secret"));
    }

    @Test
    void escapedBracesAndUnknownMarkupRemainLiteral() {
        var text = BookText.parse("{{wash:4|literal} and {unknown} and {i}slanted{/i}");
        assertEquals("{wash:4|literal} and {unknown} and slanted", text.sanitized(level -> false));
    }

    @Test
    void paragraphBreaksRetainEnclosingWashAndLinkGates() {
        for (String separator : new String[]{"{n}", "\n\n"}) {
            var text = BookText.parse("Public{n}{wash:3|first secret" + separator
                    + "{link:test:guide/intro/pages/a|second secret}}{n}After");
            assertTrue(text.diagnostics().isEmpty());
            assertEquals("Public\n[…]\nAfter", text.sanitized(level -> false));
            assertEquals("Public\nfirst secret\nsecond secret\nAfter", text.sanitized(level -> true));
            assertTrue(text.runs().stream().filter(run -> run.target() != null)
                    .noneMatch(run -> run.readable(level -> false)));
        }
    }

    @Test
    void unclosedWashConcealsEveryAffectedParagraphAndEscapedBreakIsLiteral() {
        assertEquals("{n} literal", BookText.parse("{{n} literal").plainText());
        var broken = BookText.parse("Safe{n}before {wash:3|first{n}second");
        assertEquals("Safe\n[…]", broken.sanitized(level -> true));
        assertFalse(BookText.parse("{b}before {wash:3|secret}").sanitized(level -> false).contains("secret"));
    }

    @Test
    void anUnclosedLinkContainingClosedFormattingIsLiteralAndNotInteractive() {
        String source = "{link:test:book/intro/pages/entry|a {b}label{/b}";
        var text = BookText.parse(source);
        assertEquals(source, text.sanitized(level -> true));
        assertTrue(text.runs().stream().noneMatch(run -> run.target() != null));
    }
}
