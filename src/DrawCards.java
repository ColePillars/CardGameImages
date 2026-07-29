import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import org.apache.commons.text.WordUtils;

import javax.imageio.ImageIO;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Rectangle;
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
    public void drawCardsAndSheets() throws IOException {
        String outputDir = outputDir();
        List<Card> cards = parseCardCSV();

        BufferedImage sheetImage = new BufferedImage(3600, 3600, BufferedImage.TYPE_INT_ARGB);
        Graphics sheetGraphics = sheetImage.createGraphics();
        sheetGraphics.setColor(Color.WHITE);
        sheetGraphics.fillRect(0, 0, 3600, 3600);

        for (int i = 1; i <= cards.size(); i++) {
            drawCard(cards.get(i - 1), outputDir, sheetGraphics);
            if (i % 12 == 0) {
                ImageIO.write(sheetImage, "PNG", new File(outputDir, "cardSheet" + i / 12 + ".png"));
                sheetGraphics.fillRect(0, 0, 3600, 3600);
            } else if (i == cards.size()) {
                ImageIO.write(sheetImage, "PNG", new File(outputDir, "cardSheet" + ((i / 12) + 1) + ".png"));
            }
        }
        sheetGraphics.dispose();
    }

    List<Card> parseCardCSV() {
        List<Card> cards = new ArrayList<>();

        try (CSVReader reader = new CSVReader(new FileReader("./input/cardList.csv"))) {
            int name = 1;
            String[] line;
            while ((line = reader.readNext()) != null) {
                Card.CardType cardType = Objects.equals(line[0], "C") ? Card.CardType.CHARM : Card.CardType.BAG;
                cards.add(new Card(cardType, Integer.toString(name), line[1], line[2], line[3]));
                name++;
            }
        } catch (IOException | CsvValidationException e) {
            System.err.println(e.getMessage());
        }

        return cards;
    }

    void drawCard(Card card, String outputDir, Graphics sheetGraphics) throws IOException {
        BufferedImage cardImage = new BufferedImage(744, 1039, BufferedImage.TYPE_INT_ARGB);
        Graphics cardGraphics = cardImage.createGraphics();

        cardGraphics.drawImage(ImageIO.read(new File("./input/base.png")), 0, 0, null);
        drawText(cardGraphics, card.getTypeString(), new Rectangle(10, 10, 100, 100), 100);
        drawText(cardGraphics, card.getPoints(), new Rectangle(634, 10, 100, 100), 100);
        drawText(cardGraphics, card.getText(), new Rectangle(10, 673, 724, 356), 55);
        drawSlots(cardGraphics, card);
        drawPicture(cardGraphics, card);

        ImageIO.write(cardImage, "PNG", new File(outputDir, card.getName() + ".png"));
        cardGraphics.dispose();

        int sheetX = ((Integer.parseInt(card.getName()) - 1) % 4) * 745;
        int sheetY = (((Integer.parseInt(card.getName()) - 1) / 4) % 3) * 1040;
        sheetGraphics.drawImage(cardImage, sheetX, sheetY, null);
        sheetGraphics.drawImage(ImageIO.read(new File("./input/overlay.png")), sheetX, sheetY, null);
    }

    void drawText(Graphics g, String text, Rectangle rectangle, int fontsize) {
        Font font = new Font(null, Font.PLAIN, fontsize);
        FontMetrics metrics = g.getFontMetrics(font);
        g.setFont(font);

        String[] lines = WordUtils.wrap(text, 25, "\n", true).split("\n");
        int multiLineOffset = (lines.length - 1) * metrics.getHeight() * -1 / 2;

        for (String line : lines) {
            g.drawString(
                    line,
                    rectangle.x + (rectangle.width - metrics.stringWidth(line)) / 2,
                    rectangle.y + ((rectangle.height - metrics.getHeight()) / 2) + metrics.getAscent() + multiLineOffset);
            multiLineOffset += metrics.getHeight();
        }
    }

    void drawSlots(Graphics g, Card card) throws IOException {
        if (card.getSlots() == null || card.getSlots().length() > 6 || card.getSlots().isEmpty()) {
            throw new RuntimeException("incompatible slots in: " + card.getSlots());
        }

        if (card.getType().equals(Card.CardType.BAG)) {
            char[] slotsArray = card.getSlots().toCharArray();

            int horizontalOffset = 373 - 41 * slotsArray.length;
            int verticalOffset = 20;

            for (char slot : slotsArray) {
                g.drawImage(
                        ImageIO.read(new File("./input/" + slot + "B.png")),
                        horizontalOffset,
                        verticalOffset,
                        null);

                horizontalOffset += 82;
            }
        } else if (card.getType().equals(Card.CardType.CHARM)) {
            g.drawImage(
                    ImageIO.read(new File("./input/" + card.getSlots() + "C.png")),
                    332,
                    20,
                    null);
        }
    }

    void drawPicture(Graphics g, Card card) throws IOException {
        //TODO draw different pictures for each card
        if (card.getType().equals(Card.CardType.BAG)) {
            g.drawImage(ImageIO.read(new File("./input/bag.png")), 10, 120, null);
        } else if (card.getType().equals(Card.CardType.CHARM)) {
            g.drawImage(ImageIO.read(new File("./input/charm.png")), 10, 120, null);
        }
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
