import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;

import javax.imageio.ImageIO;
import java.awt.Graphics;
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
        drawSlots(g, card);
        g.drawImage(ImageIO.read(new File("./input/backpack.png")), 10, 120, null);
        g.dispose();

        ImageIO.write(image, "PNG", new File(outputDir, card.getName() + ".png"));
    }

    void drawSlots(Graphics g, Card card) throws IOException {
        if (card.getSlots() == null || card.getSlots().length() > 6 || card.getSlots().isEmpty()) {
            throw new RuntimeException("incompatible slots in: " + card.getSlots());
        }

        if (card.getType().equals(Card.CardType.BAG)) {
            char[] slotsArray = card.getSlots().toCharArray();

            int horizontalOffset = 327 - 50 * slotsArray.length;
            int verticalOffset = 20;

            for (char slot : slotsArray) {
                g.drawImage(ImageIO.read(new File("./input/" + slot + "B.png")), horizontalOffset, verticalOffset, null);

                horizontalOffset += 100;
            }
        } else if (card.getType().equals(Card.CardType.CHARM)) {
            g.drawImage(ImageIO.read(new File("./input/" + card.getSlots().replaceAll("[/]+", "") + "C.png")), 277, 20, null);

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
