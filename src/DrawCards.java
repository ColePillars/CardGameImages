import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import org.apache.commons.text.WordUtils;

import javax.imageio.ImageIO;
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
    public void drawCards() throws IOException {
        String outputDir = outputDir();
        List<Card> cards = parseCardCSV();

        for (Card card : cards) {
            drawCard(card, outputDir);
        }
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

    void drawCard(Card card, String outputDir) throws IOException {
        BufferedImage image = new BufferedImage(744, 1039, BufferedImage.TYPE_INT_ARGB);
        Graphics g = image.createGraphics();

        g.drawImage(ImageIO.read(new File("./input/base.png")), 0, 0, null);
        drawText(g, card.getTypeString(), new Rectangle(10, 10, 100, 100), 100);
        drawText(g, card.getPoints(), new Rectangle(634, 10, 100, 100), 100);
        drawText(g, card.getText(), new Rectangle(10, 673, 724, 356), 55);
        drawSlots(g, card);
        g.drawImage(ImageIO.read(new File("./input/backpack.png")), 10, 120, null);
        g.dispose();

        ImageIO.write(image, "PNG", new File(outputDir, card.getName() + ".png"));
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
                    ImageIO.read(new File("./input/" + card.getSlots().replaceAll("/+", "") + "C.png")),
                    332,
                    20,
                    null);
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
