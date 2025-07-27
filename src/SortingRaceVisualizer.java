

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class SortingRaceVisualizer extends Application {
    public static void main(String[] args) { launch(args); }

    @Override
    public void start(Stage stage) {
        int[] base = new int[100];
        for (int i = 0; i < base.length; i++) base[i] = (int)(Math.random() * 300 + 10);
        int[] arr1 = base.clone(), arr2 = base.clone();

        Visualizer panel1 = new Visualizer(arr1);
        Visualizer panel2 = new Visualizer(arr2);

        Thread bubble = new Thread(() -> bubbleSort(arr1, panel1));
        Thread merge = new Thread(() -> mergeSort(arr2, 0, arr2.length - 1, panel2));

        HBox root = new HBox(panel1.canvas, panel2.canvas);
        stage.setScene(new Scene(root, 1000, 400));
        stage.setTitle("Sorting Race: Bubble vs Merge");
        stage.show();

        bubble.start();
        merge.start();
    }

    static class Visualizer {
        int[] array;
        Canvas canvas;
        GraphicsContext gc;

        Visualizer(int[] array) {
            this.array = array;
            this.canvas = new Canvas(500, 350);
            this.gc = canvas.getGraphicsContext2D();
            draw();
        }

        void draw() {
            Platform.runLater(() -> {
                gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
                double w = canvas.getWidth() / array.length;
                for (int i = 0; i < array.length; i++) {
                    gc.setFill(Color.CORNFLOWERBLUE);
                    gc.fillRect(i * w, canvas.getHeight() - array[i], w - 2, array[i]);
                }
            });
        }

        void update(int[] newArray) {
            this.array = newArray;
            draw();
            try { Thread.sleep(10); } catch (InterruptedException ignored) {}
        }
    }

    static void bubbleSort(int[] arr, Visualizer vis) {
        for (int i = 0; i < arr.length - 1; i++)
            for (int j = 0; j < arr.length - i - 1; j++)
                if (arr[j] > arr[j + 1]) {
                    int tmp = arr[j]; arr[j] = arr[j + 1]; arr[j + 1] = tmp;
                    vis.update(arr.clone());
                }
    }

    static void mergeSort(int[] arr, int l, int r, Visualizer vis) {
        if (l < r) {
            int m = (l + r) / 2;
            mergeSort(arr, l, m, vis);
            mergeSort(arr, m + 1, r, vis);
            merge(arr, l, m, r, vis);
        }
    }

    static void merge(int[] arr, int l, int m, int r, Visualizer vis) {
        int[] L = java.util.Arrays.copyOfRange(arr, l, m + 1);
        int[] R = java.util.Arrays.copyOfRange(arr, m + 1, r + 1);
        int i = 0, j = 0, k = l;
        while (i < L.length && j < R.length)
            arr[k++] = (L[i] <= R[j]) ? L[i++] : R[j++];
        while (i < L.length) arr[k++] = L[i++];
        while (j < R.length) arr[k++] = R[j++];
        vis.update(arr.clone());
    }
}
