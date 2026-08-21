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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public class DrawCards {
    private static final Rectangle typeRectangle = new Rectangle(10, 10, 100, 100);
    private static final int typeFontSize = 100;

    private static final Rectangle symbolsRectangle = new Rectangle(120, 10, 504, 100);
    private static final int symbolWidth = 80;
    private static final int symbolHeight = 80;
    private static final int symbolHorizontalPadding = 2;

    private static final Rectangle pointsRectangle = new Rectangle(634, 10, 100, 100);
    private static final int pointsFontSize = 100;

    private static final Rectangle artRectangle = new Rectangle(10, 120, 724, 543);

    private static final Rectangle nameRectangle = new Rectangle(122, 673, 500, 80);
    private static final int nameFontSize = 60;

    private static final Rectangle textRectangle = new Rectangle(10, 763, 724, 266);

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
        drawSymbols(cardGraphics, card);
        basicDrawText(cardGraphics, card.getPoints(), pointsRectangle, pointsFontSize);
        basicDrawText(cardGraphics, card.getCardName(), nameRectangle, nameFontSize);
        drawArt(cardGraphics, card);
        wrapDrawText(cardGraphics, card, textRectangle);
    }

    void wrapDrawText(Graphics g, Card card, Rectangle rectangle) {
        if (!card.getWrapLength().isEmpty() && !card.getFontSize().isEmpty()) {
            drawText(g, WordUtils.wrap(card.getText(), Integer.parseInt(card.getWrapLength()), "\n", true).split("\n"), rectangle, Integer.parseInt(card.getFontSize()));
        } else {
            String[] lines = WordUtils.wrap(card.getText(), 25, "\n", true).split("\n");
            if (lines.length <= 3) {
                drawText(g, lines, rectangle, 55);
            } else if (lines.length == 4) {
                drawText(g, WordUtils.wrap(card.getText(), 28, "\n", true).split("\n"), rectangle, 50);
            } else if (lines.length == 5) {
                drawText(g, WordUtils.wrap(card.getText(), 35, "\n", true).split("\n"), rectangle, 45);
            } else {
                drawText(g, WordUtils.wrap(card.getText(), 38, "\n", true).split("\n"), rectangle, 40);
            }
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

        char[] symbolArray = card.getSymbols().toCharArray();
        int symbolCount = card.getType().equals(Card.CardType.BAG) ? symbolArray.length : 1;
        int horizontalOffset =
                (int) (symbolsRectangle.getX()
                        + ((symbolsRectangle.getWidth() - symbolWidth) / 2)
                        - ((symbolCount - 1) * ((double) (symbolWidth + symbolHorizontalPadding) / 2)));
        int verticalOffset =
                (int) (symbolsRectangle.getY()
                        + (symbolsRectangle.getHeight() - symbolHeight) / 2);

        if (card.getType().equals(Card.CardType.BAG)) {
            for (char symbol : symbolArray) {
                g.drawImage(
                        ImageIO.read(new File("./input/symbol/" + symbol + "B.png")),
                        horizontalOffset,
                        verticalOffset,
                        null);

                horizontalOffset += symbolWidth + symbolHorizontalPadding;
            }
        } else if (card.getType().equals(Card.CardType.CHARM)) {
            g.drawImage(
                    ImageIO.read(new File("./input/symbol/" + card.getSymbols() + "C.png")),
                    horizontalOffset,
                    verticalOffset,
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
                if (!line[0].isEmpty() && !line[1].isEmpty() && !line[2].isEmpty()) {
                    Card.CardType cardType = Objects.equals(line[0], "B") ? Card.CardType.BAG : Card.CardType.CHARM;
                    cards.add(new Card(cardType, line[1], line[2], line[3], line[4], line[5], line[6], line[7]));
                }
            }
        } catch (IOException | CsvValidationException e) {
            System.err.println(e.getMessage());
        }

        return cards;
    }
}
