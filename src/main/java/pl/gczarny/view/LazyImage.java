package pl.gczarny.view;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class LazyImage {
    private String path;
    private ImageView imageView;

    public LazyImage(String path) {
        this.path = path;
    }

    public ImageView getImageView() {
        if (imageView == null) {
            Image image = new Image(path);
            imageView = new ImageView(image);
        }
        return imageView;
    }
}
