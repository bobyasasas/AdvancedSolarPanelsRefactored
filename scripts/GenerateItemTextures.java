import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import javax.imageio.ImageIO;

public final class GenerateItemTextures {
    private static final int SIZE = 16;

    private GenerateItemTextures() {
    }

    public static void main(String[] args) throws IOException {
        if (args.length != 1) {
            throw new IllegalArgumentException("Expected the output texture directory");
        }
        Path output = Path.of(args[0]);
        Files.createDirectories(output);
        write(output.resolve("crushed_ancient_debris.png"), debris(false));
        write(output.resolve("purified_ancient_debris.png"), debris(true));
        write(output.resolve("crystalline_solar_lens.png"), lens());
        write(output.resolve("netherite_plate.png"), plate());
    }

    private static BufferedImage debris(boolean purified) {
        BufferedImage image = blank();
        int dark = purified ? 0xFF493E39 : 0xFF392924;
        int base = purified ? 0xFFA98763 : 0xFF8A563A;
        int light = purified ? 0xFFE2C493 : 0xFFC47A4D;
        int[][] chunks = {{3, 4, 5, 4}, {8, 3, 4, 5}, {5, 9, 5, 4}, {11, 9, 3, 3}, {2, 11, 3, 2}};
        for (int[] chunk : chunks) {
            for (int y = chunk[1]; y < chunk[1] + chunk[3]; y++) {
                for (int x = chunk[0]; x < chunk[0] + chunk[2]; x++) {
                    boolean edge = x == chunk[0] || y == chunk[1] || x == chunk[0] + chunk[2] - 1 || y == chunk[1] + chunk[3] - 1;
                    image.setRGB(x, y, edge ? dark : ((x + y) % 3 == 0 ? light : base));
                }
            }
        }
        image.setRGB(9, 4, light);
        image.setRGB(6, 10, light);
        return image;
    }

    private static BufferedImage lens() {
        BufferedImage image = blank();
        for (int y = 2; y < 14; y++) {
            for (int x = 1; x < 15; x++) {
                double dx = (x - 7.5) / 6.5;
                double dy = (y - 7.5) / 4.5;
                double distance = dx * dx + dy * dy;
                if (distance <= 1.0) {
                    int color = distance > 0.72 ? 0xFF593A89 : (x + y < 13 ? 0xFFBDEBFF : 0xFF8B67CF);
                    image.setRGB(x, y, color);
                }
            }
        }
        image.setRGB(5, 5, 0xFFFFFFFF);
        image.setRGB(6, 5, 0xFFE6FBFF);
        image.setRGB(5, 6, 0xFFE6FBFF);
        return image;
    }

    private static BufferedImage plate() {
        BufferedImage image = blank();
        for (int y = 3; y < 13; y++) {
            for (int x = 2; x < 14; x++) {
                if ((x == 2 || x == 13) && (y == 3 || y == 12)) {
                    continue;
                }
                boolean edge = x == 2 || x == 13 || y == 3 || y == 12;
                int color = edge ? 0xFF211F24 : (y < 6 ? 0xFF766E78 : (y < 10 ? 0xFF504A52 : 0xFF38343B));
                image.setRGB(x, y, color);
            }
        }
        for (int x = 4; x < 12; x++) {
            image.setRGB(x, 5, x % 2 == 0 ? 0xFF9A909D : 0xFF847A88);
        }
        image.setRGB(3, 4, 0xFFB2A9B5);
        image.setRGB(12, 11, 0xFF17151A);
        return image;
    }

    private static BufferedImage blank() {
        return new BufferedImage(SIZE, SIZE, BufferedImage.TYPE_INT_ARGB);
    }

    private static void write(Path path, BufferedImage image) throws IOException {
        ImageIO.write(image, "png", path.toFile());
    }
}
