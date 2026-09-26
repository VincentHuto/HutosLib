package com.vincenthuto.hutoslib.common.book;

import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.IntPredicate;

/** Source syntax only. Translation, font measurement and access decisions belong to the reader. */
public record BookText(List<Node> nodes, List<String> diagnostics) {
    public BookText {
        nodes = List.copyOf(nodes);
        diagnostics = List.copyOf(diagnostics);
    }

    public sealed interface Node permits Text, Span, Link, Wash, Break {}
    public record Text(String text) implements Node {}
    public record Span(boolean bold, boolean italic, List<Node> children) implements Node {}
    public record Link(ResourceLocation target, List<Node> children) implements Node {}
    public record Wash(int level, boolean malformed, List<Node> children) implements Node {}
    public record Break() implements Node {}

    public record Run(String text, boolean bold, boolean italic, ResourceLocation target,
                      Set<Integer> levels, boolean malformed) {
        public Run { levels = Set.copyOf(levels); }
        public boolean readable(IntPredicate access) {
            return !malformed && levels.stream().allMatch(level -> level > 0 && access.test(level));
        }
    }

    public static BookText parse(String source) {
        List<String> diagnostics = new ArrayList<>();
        List<Node> nodes = new Parser(source, diagnostics).sequence(null);
        Set<Integer> malformed = new HashSet<>();
        findMalformedParagraphs(nodes, false, new int[]{0}, malformed);
        return new BookText(concealParagraphs(nodes, new int[]{0}, malformed), diagnostics);
    }

    private static void findMalformedParagraphs(List<Node> nodes, boolean bad, int[] paragraph, Set<Integer> malformed) {
        for (Node node : nodes) {
            if (bad) malformed.add(paragraph[0]);
            switch (node) {
                case Break ignored -> { paragraph[0]++; if (bad) malformed.add(paragraph[0]); }
                case Span span -> findMalformedParagraphs(span.children(), bad, paragraph, malformed);
                case Link link -> findMalformedParagraphs(link.children(), bad, paragraph, malformed);
                case Wash wash -> {
                    if (wash.malformed()) malformed.add(paragraph[0]);
                    findMalformedParagraphs(wash.children(), bad || wash.malformed(), paragraph, malformed);
                }
                case Text ignored -> { }
            }
        }
    }

    private static List<Node> concealParagraphs(List<Node> nodes, int[] paragraph, Set<Integer> malformed) {
        List<Node> result = new ArrayList<>();
        for (Node node : nodes) result.add(switch (node) {
            case Text text -> malformed.contains(paragraph[0]) ? new Wash(0, true, List.of(text)) : text;
            case Break line -> { paragraph[0]++; yield line; }
            case Span span -> new Span(span.bold(), span.italic(), concealParagraphs(span.children(), paragraph, malformed));
            case Link link -> new Link(link.target(), concealParagraphs(link.children(), paragraph, malformed));
            case Wash wash -> new Wash(wash.level(), wash.malformed(), concealParagraphs(wash.children(), paragraph, malformed));
        });
        return List.copyOf(result);
    }

    public List<Run> runs() {
        List<Run> result = new ArrayList<>();
        flatten(nodes, false, false, null, Set.of(), false, result);
        return List.copyOf(result);
    }

    public String plainText() {
        StringBuilder text = new StringBuilder();
        runs().forEach(run -> text.append(run.text()));
        return text.toString();
    }

    public String sanitized(IntPredicate access) {
        StringBuilder result = new StringBuilder();
        boolean concealed = false;
        for (Run run : runs()) {
            if (run.readable(access)) {
                result.append(run.text());
                concealed = false;
            } else if (!concealed) {
                result.append("[…]");
                concealed = true;
            }
        }
        return result.toString();
    }

    private static void flatten(List<Node> nodes, boolean bold, boolean italic, ResourceLocation target,
                                Set<Integer> levels, boolean malformed, List<Run> result) {
        for (Node node : nodes) {
            switch (node) {
                case Text text -> result.add(new Run(text.text(), bold, italic, target, levels, malformed));
                case Break ignored -> result.add(new Run("\n", bold, italic, target, levels, malformed));
                case Span span -> flatten(span.children(), bold || span.bold(), italic || span.italic(), target, levels, malformed, result);
                case Link link -> flatten(link.children(), bold, italic, link.target(), levels, malformed, result);
                case Wash wash -> {
                    Set<Integer> nested = new HashSet<>(levels);
                    nested.add(wash.level());
                    flatten(wash.children(), bold, italic, target, nested, malformed || wash.malformed(), result);
                }
            }
        }
    }

    private static final class Parser {
        private final String source;
        private final List<String> diagnostics;
        private int position;
        private static final java.util.regex.Pattern PARAGRAPH = java.util.regex.Pattern.compile("\\n\\s*\\n");
        private boolean lastClosed;
        private int depth;

        private Parser(String source, List<String> diagnostics) {
            this.source = source;
            this.diagnostics = diagnostics;
        }

        private List<Node> sequence(String end) {
            List<Node> result = new ArrayList<>();
            StringBuilder literal = new StringBuilder();
            while (position < source.length()) {
                if (end != null && source.startsWith(end, position)) {
                    position += end.length();
                    flush(literal, result);
                    lastClosed = true;
                    return List.copyOf(result);
                }
                if (source.startsWith("{{", position)) {
                    literal.append('{');
                    position += 2;
                    continue;
                }
                var paragraph = PARAGRAPH.matcher(source).region(position, source.length());
                if (source.startsWith("{n}", position) || (source.charAt(position) == '\n' && paragraph.lookingAt())) {
                    flush(literal, result);
                    position = source.startsWith("{n}", position) ? position + 3 : paragraph.end();
                    result.add(new Break());
                    continue;
                }
                if (source.charAt(position) != '{') {
                    literal.append(source.charAt(position++));
                    continue;
                }
                int start = position;
                flush(literal, result);
                if (++depth > 64) {
                    diagnostics.add("Markup nesting exceeds 64 levels");
                    result.add(fallback(source.substring(position)));
                    position = source.length();
                    depth--;
                    break;
                }
                if (source.startsWith("{b}", position) || source.startsWith("{i}", position)) {
                    boolean bold = source.charAt(position + 1) == 'b';
                    position += 3;
                    List<Node> children = sequence(bold ? "{/b}" : "{/i}");
                    if (!lastClosed) {
                        result.add(fallback(source.substring(start, position)));
                    } else result.add(new Span(bold, !bold, children));
                } else if (source.startsWith("{wash", position) || source.startsWith("{link:", position)) {
                    boolean wash = source.startsWith("{wash", position);
                    int pipe = source.indexOf('|', position);
                    int close = source.indexOf('}', position);
                    if (pipe < 0 || (close >= 0 && close < pipe)) {
                        diagnostics.add("Malformed " + (wash ? "wash" : "link") + " at " + start);
                        position = close < 0 ? source.length() : close + 1;
                        result.add(wash ? new Wash(0, true, List.of(new Text(source.substring(start, position))))
                                : fallback(source.substring(start, position)));
                    } else {
                        String header = source.substring(position + 1, pipe);
                        position = pipe + 1;
                        List<Node> children = sequence("}");
                        boolean unclosed = !lastClosed;
                        if (wash) {
                            int level = 0;
                            try {
                                if (!header.startsWith("wash:")) throw new NumberFormatException();
                                level = Integer.parseInt(header.substring(5));
                            } catch (NumberFormatException ignored) { /* Invalid gates always conceal. */ }
                            boolean invalid = unclosed || level <= 0;
                            if (invalid) diagnostics.add("Invalid wash gate at " + start);
                            result.add(new Wash(level, invalid, children));
                        } else {
                            ResourceLocation target = ResourceLocation.tryParse(header.substring(5));
                            if (target == null || unclosed) {
                                diagnostics.add("Invalid link at " + start);
                                result.add(fallback(source.substring(start, position)));
                            } else result.add(new Link(target, children));
                        }
                    }
                } else {
                    int close = source.indexOf('}', position);
                    position = close < 0 ? source.length() : close + 1;
                    diagnostics.add("Unknown markup at " + start);
                    result.add(new Text(source.substring(start, position)));
                }
                depth--;
            }
            flush(literal, result);
            if (end != null) {
                diagnostics.add("Missing closing " + end);
            }
            lastClosed = end == null;
            return List.copyOf(result);
        }

        private static Node fallback(String literal) {
            Node text = new Text(literal);
            // Invalid enclosing markup must never turn a nested gate back into readable source.
            return literal.contains("{wash") ? new Wash(0, true, List.of(text)) : text;
        }

        private static void flush(StringBuilder literal, List<Node> nodes) {
            if (!literal.isEmpty()) {
                nodes.add(new Text(literal.toString()));
                literal.setLength(0);
            }
        }
    }
}
