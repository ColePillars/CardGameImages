import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;

public class drawCards {
    public void drawCards() throws IOException {
        String outputDir = outputDir();
        List<Card> cards = parseCardList();

        for (Card card: cards) {
            drawCard(card, outputDir);
        }
    }

    List<Card> parseCardList() throws IOException {
        BufferedReader bufferedReader =
                new BufferedReader(new FileReader("./input/cardList.csv"));
        List<Card> cards = new ArrayList<>();
        String line;
        int name = 1;

        while ((line = bufferedReader.readLine()) != null) {
            cards.add(new Card(Integer.toString(name), line));
            name++;
        }

        return cards;
    }

    void drawCard (Card card, String outputDir) throws IOException {
        BufferedImage image = new BufferedImage(744, 1039, BufferedImage.TYPE_INT_ARGB);
        Graphics g = image.createGraphics();

        g.drawImage(ImageIO.read(new File("./input/base.png")), 0, 0, null);
        drawSlots(g, card.getSlots());
        g.drawImage(
                ImageIO.read(new File("./input/backpack.png")),
                10,
                120,
                null);
        g.dispose();

        ImageIO.write(image,"PNG", new File(outputDir, card.getName() + ".png"));
    }

    void drawSlots(Graphics g, String slots) throws IOException {
        if (slots == null || slots.length() > 6 || slots.isEmpty()) {
            throw new RuntimeException("incompatible slots in: " + slots);
        }

        char[] slotsArray = slots.toCharArray();

        int verticalOffset = 327 - 50 * slotsArray.length;
        int horizontalOffset = 20;

        for (char slot : slotsArray) {
            g.drawImage(
                    ImageIO.read(new File("./input/" + slot + "B.png")),
                    verticalOffset,
                    horizontalOffset,
                    null);

            verticalOffset += 100;
        }
    }

    String outputDir() {
        String outputDir =
                "./output/" + new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss")
                .format(new Date());

        if (new File(outputDir).mkdirs()) {
            return outputDir;
        } else {
            throw new RuntimeException("Output directory unable to be created");
        }
    }

}
