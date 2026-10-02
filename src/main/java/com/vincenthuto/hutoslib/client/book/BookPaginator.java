package com.vincenthuto.hutoslib.client.book;

import com.vincenthuto.hutoslib.common.book.BookText;
import net.minecraft.resources.ResourceLocation;
import java.util.ArrayList;
import java.util.List;

/** Measures complete entries into leaves without changing their identity or omitting overflow. */
public final class BookPaginator {
    private BookPaginator() {}
    public enum Kind { BODY, RECORD, MARGIN, FOOTNOTE, SEE_ALSO }
    @FunctionalInterface public interface Measurer {
        int width(BookText.Run run);
        default int width(BookText.Run run, Kind kind) { return width(run); }
    }
    public record Block(String id, BookText text, Kind kind, int lineHeight, String label, ResourceLocation target) {}
    public record Line(String blockId, int start, int end, List<BookText.Run> runs, int y, int height, int width,
                       Kind kind, boolean label, ResourceLocation target) {
        public Line { runs = List.copyOf(runs); }
        public String sourceText() {
            StringBuilder text = new StringBuilder();
            runs.forEach(run -> text.append(run.text()));
            return text.toString();
        }
    }
    public record Leaf(List<Line> lines) {
        public Leaf { lines = List.copyOf(lines); }
        public boolean blank() { return lines.isEmpty(); }
    }
    private record Unit(BookText.Run run, int width, boolean space, boolean newline) {}
    private record Wrapped(int start, int end, List<BookText.Run> runs, int width) {}

    public static List<Leaf> paginate(List<Block> main, List<Block> companion, boolean record, boolean spread,
                                      int width, int bodyHeight, Measurer measurer, String continued) {
        return paginate(main,companion,record,spread,width,bodyHeight,bodyHeight,measurer,continued);
    }

    public static List<Leaf> paginate(List<Block> main, List<Block> companion, boolean record, boolean spread,
                                      int width, int bodyHeight,int companionHeight, Measurer measurer, String continued) {
        return paginate(main,companion,record,spread,width,bodyHeight,companionHeight,bodyHeight,measurer,continued);
    }

    public static List<Leaf> paginate(List<Block> main, List<Block> companion, boolean record, boolean spread,
                                      int width, int bodyHeight,int companionHeight,int continuationHeight,
                                      Measurer measurer, String continued) {
        if (width < 1 || bodyHeight < 1 || companionHeight < 1 || continuationHeight < 1)
            throw new IllegalArgumentException("Book body must have positive dimensions");
        List<Block> notes = new ArrayList<>();
        main.stream().filter(block -> block.kind() == Kind.FOOTNOTE).forEach(notes::add);
        companion.stream().filter(block -> block.kind() == Kind.FOOTNOTE).forEach(notes::add);
        main = main.stream().filter(block -> block.kind() != Kind.FOOTNOTE).toList();
        companion = companion.stream().filter(block -> block.kind() != Kind.FOOTNOTE).toList();
        if (record && spread) {
            List<Leaf> left = flow(main, width, bodyHeight, continuationHeight, measurer, continued);
            List<Leaf> right = flow(companion, width, companionHeight, continuationHeight, measurer, continued);
            if(right.size()==1)right=List.of(bottomLinks(right.getFirst(),companionHeight));
            List<Leaf> result = new ArrayList<>();
            result.add(left.getFirst());
            result.add(right.getFirst());
            result.addAll(left.subList(1, left.size()));
            result.addAll(right.subList(1, right.size()));
            return appendNotes(result, notes, 1, width, companionHeight, continuationHeight, measurer, continued);
        }
        List<Block> blocks = new ArrayList<>(main);
        blocks.addAll(companion);
        return appendNotes(flow(blocks, width, bodyHeight, continuationHeight, measurer, continued), notes, 0, width, bodyHeight, continuationHeight, measurer, continued);
    }

    private static Leaf bottomLinks(Leaf leaf,int height) {
        int bottom=leaf.lines().stream().mapToInt(line->line.y()+line.height()).max().orElse(height);
        int shift=Math.max(0,height-bottom);
        return new Leaf(leaf.lines().stream().map(line->line.kind()==Kind.SEE_ALSO?
                new Line(line.blockId(),line.start(),line.end(),line.runs(),line.y()+shift,line.height(),line.width(),line.kind(),line.label(),line.target()):line).toList());
    }

    private static List<Leaf> appendNotes(List<Leaf> body, List<Block> notes, int owner, int width,
                                          int height, int continuationHeight, Measurer measurer, String continued) {
        if (notes.isEmpty()) return body;
        List<Leaf> result = new ArrayList<>(body);
        List<Leaf> noteLeaves = flow(notes, width, height, measurer, continued);
        List<Line> first = noteLeaves.getFirst().lines();
        int noteHeight = first.stream().mapToInt(line -> line.y() + line.height()).max().orElse(0);
        List<Line> owning = result.get(owner).lines();
        int bodyBottom = owning.stream().mapToInt(line -> line.y() + line.height()).max().orElse(0);
        if (noteLeaves.size() == 1 && bodyBottom + 8 + noteHeight <= height) {
            List<Line> combined = new ArrayList<>(owning);
            for (Line line : first) combined.add(new Line(line.blockId(), line.start(), line.end(), line.runs(),
                    height - noteHeight + line.y(), line.height(), line.width(), line.kind(), line.label(), line.target()));
            result.set(owner, new Leaf(combined));
        } else result.addAll(flow(notes, width, continuationHeight, measurer, continued));
        return List.copyOf(result);
    }

    public static int locate(List<Leaf> leaves, String blockId, int offset) {
        int last = 0;
        for (int i = 0; i < leaves.size(); i++) {
            for (Line line : leaves.get(i).lines()) {
                if (line.label() || !line.blockId().equals(blockId)) continue;
                last = i;
                if (offset < line.end() || (line.start() == line.end() && offset == line.start())) return i;
            }
        }
        return last;
    }

    private static List<Leaf> flow(List<Block> blocks, int width, int bodyHeight, Measurer measurer, String continued) {
        return flow(blocks, width, bodyHeight, bodyHeight, measurer, continued);
    }

    private static List<Leaf> flow(List<Block> blocks, int width, int bodyHeight, int continuationHeight, Measurer measurer, String continued) {
        List<Leaf> leaves = new ArrayList<>();
        List<Line> lines = new ArrayList<>();
        int y = 0;
        for (Block block : blocks) {
            int lineHeight = Math.clamp(block.lineHeight(), 1, Math.min(bodyHeight, continuationHeight));
            List<Wrapped> wrapped = wrap(block.text(), block.kind()==Kind.MARGIN?Math.max(1,width-26):width, run -> measurer.width(run, block.kind()));
            if (wrapped.isEmpty() && block.kind() == Kind.SEE_ALSO) wrapped = List.of(new Wrapped(0, 0, List.of(), 0));
            int row = 0;
            boolean labelPending = !block.label().isBlank();
            for (Wrapped line : wrapped) {
                int labelHeight = labelPending && bodyHeight >= lineHeight + 11 ? 11 : 0;
                if (y + labelHeight + lineHeight > bodyHeight && !lines.isEmpty()) {
                    leaves.add(new Leaf(lines));
                    bodyHeight = continuationHeight;
                    lines = new ArrayList<>();
                    y = 0;
                    labelPending = !block.label().isBlank();
                    labelHeight = labelPending && bodyHeight >= lineHeight + 11 ? 11 : 0;
                }
                if (labelHeight > 0) {
                    String label = block.label() + (row == 0 ? "" : " (" + continued + ")");
                    List<BookText.Run> labelRuns = BookText.parse(label).runs();
                    lines.add(new Line(block.id(), line.start(), line.start(), labelRuns, y, labelHeight,
                            Math.min(width, labelRuns.stream().mapToInt(measurer::width).sum()), block.kind(), true, null));
                    y += labelHeight;
                }
                labelPending = false;
                lines.add(new Line(block.id(), line.start(), line.end(), line.runs(), y, lineHeight, line.width(), block.kind(), false, block.target()));
                y += lineHeight;
                if (line.runs().stream().anyMatch(run -> run.text().endsWith("\n"))) y += 4;
                row++;
            }
            y += block.kind()==Kind.RECORD?8:4;
        }
        if (!lines.isEmpty() || leaves.isEmpty()) leaves.add(new Leaf(lines));
        return List.copyOf(leaves);
    }

    private static List<Wrapped> wrap(BookText text, int width, Measurer measurer) {
        List<Unit> units = new ArrayList<>();
        for (BookText.Run run : text.runs()) {
            run.text().codePoints().forEach(codePoint -> {
                String character = new String(Character.toChars(codePoint));
                var glyph = new BookText.Run(character, run.bold(), run.italic(), run.target(), run.levels(), run.malformed());
                units.add(new Unit(glyph, codePoint == '\n' ? 0 : Math.max(0, measurer.width(glyph)),
                        Character.isWhitespace(codePoint), codePoint == '\n'));
            });
        }
        List<Wrapped> lines = new ArrayList<>();
        int start = 0;
        while (start < units.size()) {
            int end = start, measured = 0, lastSpace = -1;
            while (end < units.size()) {
                Unit unit = units.get(end);
                if (unit.newline()) { end++; break; }
                if (measured + unit.width() > width && end > start) {
                    if (lastSpace >= start) end = lastSpace + 1;
                    break;
                }
                measured += unit.width();
                if (unit.space()) lastSpace = end;
                end++;
                if (measured > width) break; // A single oversized glyph must still make progress.
            }
            List<BookText.Run> runs = new ArrayList<>();
            int actualWidth = 0;
            for (int i = start; i < end; i++) {
                Unit unit = units.get(i);
                actualWidth += unit.width();
                if (!runs.isEmpty() && sameStyle(runs.getLast(), unit.run())) {
                    BookText.Run previous = runs.removeLast();
                    runs.add(new BookText.Run(previous.text() + unit.run().text(), previous.bold(), previous.italic(),
                            previous.target(), previous.levels(), previous.malformed()));
                } else runs.add(unit.run());
            }
            lines.add(new Wrapped(start, end, runs, actualWidth));
            start = end;
        }
        return lines;
    }

    private static boolean sameStyle(BookText.Run first, BookText.Run second) {
        return first.bold() == second.bold() && first.italic() == second.italic()
                && java.util.Objects.equals(first.target(), second.target()) && first.levels().equals(second.levels())
                && first.malformed() == second.malformed();
    }
}
