import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import org.apache.commons.text.WordUtils;

import javax.imageio.ImageIO;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class DrawCards {
    private static final Rectangle typeRectangle = new Rectangle(10, 10, 100, 100);
    private static final int typeFontSize = 100;
    private static final Rectangle pointsRectangle = new Rectangle(634, 10, 100, 100);
    private static final int pointsFontSize = 100;
    private static final Rectangle nameRectangle = new Rectangle(122, 673, 500, 80);
    private static final int nameFontSize = 60;
    private static final Rectangle textRectangle = new Rectangle(10, 763, 724, 266);
    private static final Rectangle artRectangle = new Rectangle(10, 120, 724, 543);

    private static final int cardWidth = 744;
    private static final int cardHeight = 1039;
    private static final int sheetWidth = 2550;
    private static final int sheetHeight = 3300;
    private static final int numberOfCardsWide = 3;
    private static final int numberOfCardsHigh = 3;
    private static final int numberOfCardsSheet = numberOfCardsWide * numberOfCardsHigh;

    private static final int pixelateToWidth = 400;
    private static final int pixelateToHeight = 300;
    private static final boolean pixelateCard = true;

    public void drawCardsAndSheets() throws IOException {
        System.out.println(new Date());
        String outputDir = outputDir();
        List<Card> cards = parseCardCSV();

        BufferedImage sheetImage = new BufferedImage(sheetWidth, sheetHeight, BufferedImage.TYPE_INT_ARGB);
        Graphics sheetGraphics = sheetImage.createGraphics();
        sheetGraphics.setColor(Color.WHITE);
        sheetGraphics.fillRect(0, 0, sheetWidth, sheetHeight);

        BufferedImage cardImage = new BufferedImage(cardWidth, cardHeight, BufferedImage.TYPE_INT_ARGB);
        Graphics cardGraphics = cardImage.createGraphics();

        for (int i = 1; i <= cards.size(); i++) {
            drawCard(cards.get(i - 1), cardGraphics);
            ImageIO.write(cardImage, "PNG", new File(outputDir, i + ".png"));
            int sheetX = ((i - 1) % numberOfCardsWide) * (cardWidth + 1);
            int sheetY = (((i - 1) / numberOfCardsWide) % numberOfCardsHigh) * (cardHeight + 1);
            sheetGraphics.drawImage(cardImage, sheetX, sheetY, null);
            sheetGraphics.drawImage(ImageIO.read(new File("./input/overlay.png")), sheetX, sheetY, null);

            if (i % numberOfCardsSheet == 0) {
                ImageIO.write(sheetImage, "PNG", new File(outputDir, "cardSheet" + i / numberOfCardsSheet + ".png"));
                sheetGraphics.fillRect(0, 0, sheetWidth, sheetHeight);
            } else if (i == cards.size()) {
                ImageIO.write(sheetImage, "PNG", new File(outputDir, "cardSheet" + ((i / numberOfCardsSheet) + 1) + ".png"));
            }
        }
        sheetGraphics.dispose();
        cardGraphics.dispose();
        System.out.println(new Date());
    }

    void drawCard(Card card, Graphics cardGraphics) throws IOException {
        cardGraphics.drawImage(ImageIO.read(new File("./input/base.png")), 0, 0, null);
        basicDrawText(cardGraphics, card.getTypeString(), typeRectangle, typeFontSize);
        basicDrawText(cardGraphics, card.getPoints(), pointsRectangle, pointsFontSize);
        basicDrawText(cardGraphics, card.getCardName(), nameRectangle, nameFontSize);
        wrapDrawText(cardGraphics, card.getText(), textRectangle);
        drawSymbols(cardGraphics, card);
        drawArt(cardGraphics, card);
    }

    void wrapDrawText(Graphics g, String text, Rectangle rectangle) {
        String[] lines = WordUtils.wrap(text, 25, "\n", true).split("\n");
        if (lines.length <= 3) {
            drawText(g, lines, rectangle, 55);
        } else if (lines.length == 4) {
            drawText(g, WordUtils.wrap(text, 28, "\n", true).split("\n"), rectangle, 50);
        } else {
            drawText(g, WordUtils.wrap(text, 31, "\n", true).split("\n"), rectangle, 45);
        }
    }

    void basicDrawText(Graphics g, String text, Rectangle rectangle, int fontsize) {
        drawText(g, new String[]{text}, rectangle, fontsize);
    }

    void drawText(Graphics g, String[] lines, Rectangle rectangle, int fontsize) {
        Font font = new Font(null, Font.PLAIN, fontsize);
        FontMetrics metrics = g.getFontMetrics(font);
        g.setFont(font);

        int multiLineOffset = (lines.length - 1) * metrics.getHeight() * -1 / 2;

        for (String line : lines) {
            g.drawString(
                    line,
                    rectangle.x + (rectangle.width - metrics.stringWidth(line)) / 2,
                    rectangle.y + ((rectangle.height - metrics.getHeight()) / 2) + metrics.getAscent() + multiLineOffset);
            multiLineOffset += metrics.getHeight();
        }
    }

    void drawSymbols(Graphics g, Card card) throws IOException {
        if (card.getSymbols() == null || card.getSymbols().length() > 6 || card.getSymbols().isEmpty()) {
            throw new RuntimeException("incompatible symbols in: " + card.getSymbols());
        }

        if (card.getType().equals(Card.CardType.BAG)) {
            List<Character> symbolList = orderColors(card.getSymbols());

            int horizontalOffset = 373 - 41 * symbolList.size();
            int verticalOffset = 20;

            for (char symbol : symbolList) {
                g.drawImage(
                        ImageIO.read(new File("./input/symbol/" + symbol + "B.png")),
                        horizontalOffset,
                        verticalOffset,
                        null);

                horizontalOffset += 82;
            }
        } else if (card.getType().equals(Card.CardType.CHARM)) {
            g.drawImage(
                    ImageIO.read(new File("./input/symbol/" + card.getSymbols() + "C.png")),
                    332,
                    20,
                    null);
        }
    }

    void drawArt(Graphics g, Card card) throws IOException {
        BufferedImage art;

        if (!card.getArtName().isEmpty()) {
            art = ImageIO.read(new File("./input/art/" + card.getArtName() + ".png"));
        } else if (card.getType().equals(Card.CardType.BAG)) {
            art = ImageIO.read(new File("./input/bag.png"));
        } else {
            art = ImageIO.read(new File("./input/charm.png"));
        }

        g.drawImage(pixelateImage(art), (int) artRectangle.getX(), (int) artRectangle.getY(), null);
    }

    List<Character> orderColors(String symbols) throws IOException {
        List<Character> reorderedSymbols = new ArrayList<>();
        StringBuilder stringBuilder = new StringBuilder();

        for (char ch : symbols.toCharArray()) {
            if (ch == 'W') {
                reorderedSymbols.add(ch);
            } else if ("PUGYOR".indexOf(ch) >= 0) {
                stringBuilder.append(ch);
            }
        }

        String coloredSymbols = stringBuilder.toString();

        String colorOrder =
                Files.readAllLines(
                                new File(
                                        "input/colorOrders.txt").toPath(),
                                Charset.defaultCharset())
                        .stream()
                        .filter(string -> similarStrings(coloredSymbols, string))
                        .findFirst()
                        .orElse("");

        List<Character> charList =
                coloredSymbols.chars()
                        .mapToObj(e -> (char) e)
                        .sorted(
                                (obj1, obj2) -> {
                                    int index1 = colorOrder.indexOf(obj1);
                                    int index2 = colorOrder.indexOf(obj2);
                                    return Integer.compare(index1, index2);
                                })
                        .collect(Collectors.toList());

        reorderedSymbols.addAll(charList);
        return reorderedSymbols;
    }

    boolean similarStrings(String s1, String s2) {
        for (char ch : s1.toCharArray()) {
            if (s2.indexOf(ch) < 0) {
                return false;
            }
        }
        for (char ch : s2.toCharArray()) {
            if (s1.indexOf(ch) < 0) {
                return false;
            }
        }
        return true;
    }

    BufferedImage pixelateImage(BufferedImage image) {
        if (pixelateCard) {
            return resizeImage(
                    resizeImage(image, pixelateToWidth, pixelateToHeight),
                    artRectangle.getWidth(),
                    artRectangle.getHeight());
        } else {
            return resizeImage(image, artRectangle.getWidth(), artRectangle.getHeight());
        }
    }

    BufferedImage resizeImage(BufferedImage image, double width, double height) {
        if (image.getWidth() == width && image.getHeight() == height) {
            return image;
        }

        AffineTransform scalingTransform = new AffineTransform();
        scalingTransform.scale(width / image.getWidth(), height / image.getHeight());
        AffineTransformOp scaleOp = new AffineTransformOp(scalingTransform, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);

        return scaleOp.filter(image, new BufferedImage((int) width, (int) height, image.getType()));
    }

    String outputDir() {
        String outputDir = "./output/" + new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss").format(new Date());

        if (new File(outputDir).mkdirs()) {
            return outputDir;
        } else {
            throw new RuntimeException("Output directory unable to be created");
        }
    }

    List<Card> parseCardCSV() {
        List<Card> cards = new ArrayList<>();

        try (CSVReader reader = new CSVReader(new FileReader("./input/cardList.csv"))) {
            String[] line;
            while ((line = reader.readNext()) != null) {
                Card.CardType cardType = Objects.equals(line[0], "B") ? Card.CardType.BAG : Card.CardType.CHARM;
                cards.add(new Card(cardType, line[1], line[2], line[3], line[4], line[5]));
            }
        } catch (IOException | CsvValidationException e) {
            System.err.println(e.getMessage());
        }

        return cards;
    }
}
