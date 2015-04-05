import java.awt.Color;
public class SeamCarver {
    private int[][] image;
    private int[][] energy;
    private int height;
    private int width;
    private boolean status_is_horizontal;
    private static int BORDER_ENERGY = 195075;

    public SeamCarver(Picture picture) {
        if (null == picture)
            throw new java.lang.NullPointerException();
        height = picture.height();
        width = picture.width();
        image = new int[width][height];
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                image[i][j] = picture.get(i, j).getRGB();
            }
        }

        // compute the energy
        energy = new int[width][height];
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                computeEnergy(i, j);
            }
        }

        status_is_horizontal = true;
    }

    public Picture picture() {
        if (status_is_horizontal) {
            Picture pic = new Picture(width, height);
            for (int i = 0; i < width; i++) {
                for (int j = 0; j < height; j++) {
                    pic.set(i, j, new Color(image[i][j]));
                }
            }
            return pic;
        } else {
            Picture pic = new Picture(height, width);
            for (int i = 0; i < height; i++) {
                for (int j = 0; j < width; j++) {
                    pic.set(i, j, new Color(image[j][i]));
                }
            }
            return pic;
        }
    }

    public int width() {
        if (status_is_horizontal)
            return width;
        else
            return height;
    }

    public int height() {
        if (status_is_horizontal)
            return height;
        else
            return width;
    }

    public double energy(int x, int y) {
        if (!status_is_horizontal) {
            int tmp = x;
            x = y;
            y = tmp;
        }
        if (x < 0 || x >= width || y < 0 || y >= height)
            throw new java.lang.IndexOutOfBoundsException();
        return energy[x][y];
    }

    public int[] findHorizontalSeam() {
        if (!status_is_horizontal)
            flip();
        return findSeamCommon();
    }

    public int[] findVerticalSeam() {
        if (status_is_horizontal)
            flip();
        return findSeamCommon();
    }

    public void removeHorizontalSeam(int[] seam) {
        if (!status_is_horizontal)
            flip();
        removeSeamCommon(seam);
    }

    public void removeVerticalSeam(int[] seam) {
        if (status_is_horizontal)
            flip();
        removeSeamCommon(seam);
    }

    private int[] findSeamCommon() {
        // return result
        int[] path = new int[width];

        // aux variable
        int[][] path_to = new int[width][height];
        double[][] dist_to = new double[width][height];
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                dist_to[i][j] = Double.MAX_VALUE;
            }
        }

        // initialize the first column
        for (int j = 0; j < height; j++) {
            path_to[0][j] = j;
            dist_to[0][j] = energy[0][j];
        }

        // compute others
        for (int i = 0; i < width - 1; i++) {
            for (int j = 0; j < height; j++) {
                // begin 3-way compare
                double new_value;
                // first compare
                if (j >= 1) {
                    new_value = dist_to[i][j] + energy[i + 1][j - 1];
                    if (new_value < dist_to[i + 1][j - 1]) {
                        dist_to[i + 1][j - 1] = new_value;
                        path_to[i + 1][j - 1] = j;
                    }
                }

                // second compare
                new_value = dist_to[i][j] + energy[i + 1][j];
                if (new_value < dist_to[i + 1][j]) {
                    dist_to[i + 1][j] = new_value;
                    path_to[i + 1][j] = j;
                }

                //third compare
                if (j <= height - 2) {
                    new_value = dist_to[i][j] + energy[i + 1][j + 1];
                    if (new_value < dist_to[i + 1][j + 1]) {
                        dist_to[i + 1][j + 1] = new_value;
                        path_to[i + 1][j + 1] = j;
                    }
                }
            }
        }

        // find the shortest path
        int shortest_row_index = -1;
        double shortest_value = Double.MAX_VALUE;
        for (int j = 0; j < height; j++) {
            if (dist_to[width - 1][j] < shortest_value) {
                shortest_row_index = j;
                shortest_value = dist_to[width - 1][j];
            }
        }

        // construct the path
        path[width - 1] = shortest_row_index;
        for (int i = width - 2; i >= 0; i--) {
            path[i] = path_to[i + 1][path[i + 1]];
        }
        return path;
    }

    private void removeSeamCommon(int[] seam) {
        if (!check(seam)) {
            throw new java.lang.IllegalArgumentException();
        }
        if (width <= 1 || height <= 1) throw new java.lang.IllegalArgumentException();
        for (int i = 0; i < width; i++) {
            if (seam[i] == height - 1) continue;
            for (int j = seam[i]; j < height - 1; j++) {
                image[i][j] = image[i][j + 1];
                energy[i][j] = energy[i][j + 1];
            }
        }

        height--;
        // recompute the energy
        for (int i = 0; i < width; i++) {
            // 4-way recompute
            // up
            computeEnergy(i, seam[i] - 1);

            //down
            computeEnergy(i, seam[i]);

            //left
            computeEnergy(i - 1, seam[i]);

            //right
            computeEnergy(i + 1, seam[i]);
        }
    }

    private boolean check(int[] seam) {
        if (seam.length != width) return false;
        for (int i = 0; i < width - 1; i++) {
            if (seam[i] < 0 || seam[i] > height - 1) return false;
            if (Math.abs(seam[i] - seam[i + 1]) > 1) return false;
        }
        if (seam[width - 1] < 0 || seam[width - 1] > height - 1) return false;

        return true;
    }

    private void computeEnergy(int i, int j) {
        if (i < 0 || i > width - 1 || j < 0 || j > height - 1) {
            return;
        } else if (i == 0 || i == width - 1 || j == 0 || j == height - 1) {
            energy[i][j] = BORDER_ENERGY;
        } else {
            int x_energy = 0;
            int y_energy = 0;
            energy[i][j] = computeRGB(image[i - 1][j], image[i + 1][j])
                           + computeRGB(image[i][j - 1], image[i][j + 1]);
        }
    }

    private int computeRGB(int color1, int color2) {
        int energy = 0;
        int blue1 = color1 & 0xFF;
        int blue2 = color2 & 0xFF;
        int green1 = (color1 >> 8) & 0xFF;
        int green2 = (color2 >> 8) & 0xFF;
        int red1 = (color1 >> 16) & 0xFF;
        int red2 = (color2 >> 16) & 0xFF;
        energy += (blue1 - blue2) * (blue1 - blue2);
        energy += (green1 - green2) * (green1 - green2);
        energy += (red1 - red2) * (red1 - red2);
        return energy;
    }

    private void flip() {
        int[][] old_image = image;
        int[][] old_energy = energy;
        image = new int[height][width];
        energy = new int[height][width];
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                image[j][i] = old_image[i][j];
                energy[j][i] = old_energy[i][j];
            }
        }

        int tmp = height;
        height = width;
        width = tmp;
        status_is_horizontal = !status_is_horizontal;
    }
}
