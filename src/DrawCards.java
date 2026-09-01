import org.apache.commons.text.WordUtils;
import org.imgscalr.Scalr;

import javax.imageio.ImageIO;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class DrawCards {
    public DrawCards(String templateFileName, Rectangle typeRectangle, int typeFontSize, Rectangle symbolsRectangle, int symbolWidth, int symbolHeight, int symbolHorizontalPadding, Rectangle pointsRectangle, int pointsFontSize, Rectangle artRectangle, Rectangle nameRectangle, int nameFontSize, Rectangle textRectangle, int cardWidth, int cardHeight, int sheetWidth, int sheetHeight, int numberOfCardsWide, int numberOfCardsHigh, int numberOfCardsSheet, boolean createImageFiles, boolean createSheetFiles, int pixelateToWidth, int pixelateToHeight, boolean pixelateCard) {
        this.templateFileName = templateFileName;
        this.typeRectangle = typeRectangle;
        this.typeFontSize = typeFontSize;
        this.symbolsRectangle = symbolsRectangle;
        this.symbolWidth = symbolWidth;
        this.symbolHeight = symbolHeight;
        this.symbolHorizontalPadding = symbolHorizontalPadding;
        this.pointsRectangle = pointsRectangle;
        this.pointsFontSize = pointsFontSize;
        this.artRectangle = artRectangle;
        this.nameRectangle = nameRectangle;
        this.nameFontSize = nameFontSize;
        this.textRectangle = textRectangle;
        this.cardWidth = cardWidth;
        this.cardHeight = cardHeight;
        this.sheetWidth = sheetWidth;
        this.sheetHeight = sheetHeight;
        this.numberOfCardsWide = numberOfCardsWide;
        this.numberOfCardsHigh = numberOfCardsHigh;
        this.numberOfCardsSheet = numberOfCardsSheet;
        this.createImageFiles = createImageFiles;
        this.createSheetFiles = createSheetFiles;
        this.pixelateToWidth = pixelateToWidth;
        this.pixelateToHeight = pixelateToHeight;
        this.pixelateCard = pixelateCard;
    }

    private final String templateFileName;
    private final Rectangle typeRectangle;
    private final int typeFontSize;
    private final Rectangle symbolsRectangle;
    private final int symbolWidth;
    private final int symbolHeight;
    private final int symbolHorizontalPadding;
    private final Rectangle pointsRectangle;
    private final int pointsFontSize;
    private final Rectangle artRectangle;
    private final Rectangle nameRectangle;
    private final int nameFontSize;
    private final Rectangle textRectangle;
    private final int cardWidth;
    private final int cardHeight;
    private final int sheetWidth;
    private final int sheetHeight;
    private final int numberOfCardsWide;
    private final int numberOfCardsHigh;
    private final int numberOfCardsSheet;
    private final boolean createImageFiles;
    private final boolean createSheetFiles;
    private final int pixelateToWidth;
    private final int pixelateToHeight;
    private final boolean pixelateCard;

    public void drawCardsAndSheets(List<Card> cards) throws IOException {
        System.out.println(new Date() + " Start of drawing cards.");

        String outputDir = outputDir();

        BufferedImage sheetImage = new BufferedImage(sheetWidth, sheetHeight, BufferedImage.TYPE_INT_ARGB);
        Graphics sheetGraphics = sheetImage.createGraphics();
        sheetGraphics.setColor(Color.WHITE);
        sheetGraphics.fillRect(0, 0, sheetWidth, sheetHeight);

        BufferedImage cardImage = new BufferedImage(cardWidth, cardHeight, BufferedImage.TYPE_INT_ARGB);
        Graphics cardGraphics = cardImage.createGraphics();

        for (int i = 1; i <= cards.size(); i++) {
            drawCard(cards.get(i - 1), cardGraphics);
            if (createImageFiles) {
                ImageIO.write(cardImage, "PNG", new File(outputDir, i + ".png"));
            }
            if (createSheetFiles) {
                int sheetX = ((i - 1) % numberOfCardsWide) * (cardWidth + 1);
                int sheetY = (((i - 1) / numberOfCardsWide) % numberOfCardsHigh) * (cardHeight + 1);
                sheetGraphics.drawImage(cardImage, sheetX, sheetY, null);

                if (i % numberOfCardsSheet == 0) {
                    ImageIO.write(sheetImage, "PNG", new File(outputDir, "cardSheet" + i / numberOfCardsSheet + ".png"));
                    sheetGraphics.fillRect(0, 0, sheetWidth, sheetHeight);
                } else if (i == cards.size()) {
                    ImageIO.write(sheetImage, "PNG", new File(outputDir, "cardSheet" + ((i / numberOfCardsSheet) + 1) + ".png"));
                }
            }
        }

        sheetGraphics.dispose();
        cardGraphics.dispose();

        System.out.println(new Date() + " End of drawing cards.");
    }

    void drawCard(Card card, Graphics cardGraphics) throws IOException {
        cardGraphics.drawImage(ImageIO.read(new File("./input/" + templateFileName)), 0, 0, null);
        basicDrawText(cardGraphics, card.getTypeString(), typeRectangle, typeFontSize);
        drawSymbols(cardGraphics, card);
        basicDrawText(cardGraphics, card.getPoints(), pointsRectangle, pointsFontSize);
        basicDrawText(cardGraphics, card.getCardName(), nameRectangle, nameFontSize);
        drawArt(cardGraphics, card);
        wrapDrawText(cardGraphics, card, textRectangle);
    }

    void wrapDrawText(Graphics g, Card card, Rectangle rectangle) {
        if (card.getText().isEmpty()) {
            return;
        }
        if (!card.getWrapLength().isEmpty() && !card.getFontSize().isEmpty()) {
            String[] lines =
                    Arrays.stream(card.getText().split("\n"))
                            .flatMap(str -> Arrays.stream(WordUtils.wrap(str, Integer.parseInt(card.getWrapLength()), "\n", true).split("\n")))
                            .toArray(String[]::new);
            drawText(g, lines, rectangle, Integer.parseInt(card.getFontSize()));
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
                    (int) artRectangle.getWidth(),
                    (int) artRectangle.getHeight());
        } else {
            return resizeImage(image, (int) artRectangle.getWidth(), (int) artRectangle.getHeight());
        }
    }

    BufferedImage resizeImage(BufferedImage image, int width, int height) {
        if (image.getWidth() == width && image.getHeight() == height) {
            return image;
        }
        return Scalr.resize(image, width, height);
    }

    String outputDir() {
        String outputDir = "./output/" + new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss").format(new Date());

        if (new File(outputDir).mkdirs()) {
            return outputDir;
        } else {
            throw new RuntimeException("Output directory unable to be created");
        }
    }
}
