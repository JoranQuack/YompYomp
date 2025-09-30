package seng202.team5.gui.util;

import javafx.beans.binding.Bindings;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Region;
import javafx.scene.shape.Rectangle;

/**
 * Utility class for setting up background images with cover behaviour
 */
public class BackgroundImageUtil {

    /**
     * Sets up an ImageView to cover its container while preserving aspect ratio
     *
     * @param imageView The ImageView to configure
     * @param container The pane that the image should cover
     */
    public static void setupCoverBehavior(ImageView imageView, Region container) {
        if (imageView == null || container == null) {
            throw new IllegalArgumentException("ImageView and container cannot be null");
        }

        imageView.setPreserveRatio(true);

        imageView.fitWidthProperty().bind(
                Bindings.createDoubleBinding(() -> calculateCoverDimensions(imageView, container)[0],
                        container.widthProperty(), container.heightProperty(), imageView.imageProperty()));

        imageView.fitHeightProperty().bind(
                Bindings.createDoubleBinding(() -> calculateCoverDimensions(imageView, container)[1],
                        container.widthProperty(), container.heightProperty(), imageView.imageProperty()));
    }

    /**
     * Calculates the width and height needed for an image to cover its container
     * while preserving aspect ratio.
     *
     * @param imageView The ImageView containing the image
     * @param container The container that should be covered
     * @return An array with calculated width and height
     */
    private static double[] calculateCoverDimensions(ImageView imageView, Region container) {
        if (imageView.getImage() == null || container.getWidth() <= 0 || container.getHeight() <= 0) {
            return new double[] { container.getWidth(), container.getHeight() };
        }

        double containerWidth = container.getWidth();
        double containerHeight = container.getHeight();
        double imageWidth = imageView.getImage().getWidth();
        double imageHeight = imageView.getImage().getHeight();

        double containerRatio = containerWidth / containerHeight;
        double imageRatio = imageWidth / imageHeight;

        if (containerRatio > imageRatio) {
            // Container is wider relative to image - fit to width
            return new double[] { containerWidth, containerWidth / imageRatio };
        } else {
            // Container is taller relative to image - scale based on height
            return new double[] { containerHeight * imageRatio, containerHeight };
        }
    }

    /**
     * Sets up more simple cover behaviour for an ImageView to fill its container
     * without preserving aspect ratio.
     *
     * @param imageView The ImageView to configure
     * @param container The container that the image should fill
     */
    public static void setupFillBehavior(ImageView imageView, Region container) {
        if (imageView == null || container == null) {
            throw new IllegalArgumentException("ImageView and container cannot be null");
        }

        imageView.setPreserveRatio(false);

        imageView.fitWidthProperty().bind(container.widthProperty());
        imageView.fitHeightProperty().bind(container.heightProperty());
    }

    /**
     * Sets up fill behaviour with rounded corners by applying a clipping rectangle.
     *
     * @param imageView    The ImageView to configure
     * @param container    The container that the image should fill
     * @param cornerRadius The radius for the rounded corners
     */
    public static void setupFillBehaviorWithRoundedCorners(ImageView imageView, Region container, double cornerRadius) {
        if (imageView == null || container == null) {
            throw new IllegalArgumentException("ImageView and container cannot be null");
        }

        setupFillBehavior(imageView, container);

        Rectangle clip = new Rectangle();
        clip.setArcWidth(cornerRadius * 2);
        clip.setArcHeight(cornerRadius * 2);

        clip.widthProperty().bind(container.widthProperty());
        clip.heightProperty().bind(container.heightProperty());

        imageView.setClip(clip);
    }
}