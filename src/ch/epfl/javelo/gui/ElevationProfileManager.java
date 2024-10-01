package ch.epfl.javelo.gui;

import ch.epfl.javelo.routing.ElevationProfile;
import javafx.beans.binding.Bindings;
import javafx.beans.property.*;
import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
import javafx.geometry.VPos;
import javafx.scene.Group;
import javafx.scene.layout.*;
import javafx.scene.shape.*;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.transform.Affine;
import javafx.scene.transform.Transform;
import javafx.geometry.Insets;

/**
 *  ElevationProfileManager displays the elevations of route and the grid and manages the interactions with the profile
 *
 * @author Temi Romeo Messmer (345250)
 * @author Thomas Marin Zamblera (344438)
 */
public final class ElevationProfileManager {

    private static final int[] POS_STEPS = new int[]{1000, 2000, 5000, 10_000, 25_000, 50_000, 100_000};
    private static final int[] ELE_STEPS = new int[]{5, 10, 20, 25, 50, 100, 200, 250, 500, 1_000};
    private static final int MIN_PIXEL_LENGTH = 25;
    private static final int MIN_PIXEL_ELEV = 50;
    private static final int KM_METER_RATIO = 1000;
    private final ReadOnlyObjectProperty<ElevationProfile> elevationProfile;
    private final DoubleProperty highlightedPosition;
    private final BorderPane borderPane;
    private final Pane pane;
    private final Text vBoxText;
    private final Group group;
    private final Polygon profile;
    private final Path grid;
    private final Line line;
    private final Insets insets;
    private final ObjectProperty<Rectangle2D> rectangle;
    private final ObjectProperty<Transform> screenToWorld;
    private final ObjectProperty<Transform> worldToScreen;
    private final SimpleDoubleProperty mousePosition;

    /**
     * Constructor of ElevationProfileManager
     *
     * @param elevationProfile elevations of the route
     * @param highlightedPosition highlighted position of the route
     */
    public ElevationProfileManager(ReadOnlyObjectProperty<ElevationProfile> elevationProfile,
                                   DoubleProperty highlightedPosition){

        this.elevationProfile = elevationProfile;
        this.highlightedPosition = highlightedPosition;

        mousePosition = new SimpleDoubleProperty();

        borderPane = new BorderPane();
        borderPane.getStylesheets().add("elevation_profile.css");
        borderPane.setMinHeight(100);

        pane = new Pane();

        VBox vBox = new VBox();
        vBox.setId("profile_data");

        vBoxText = new Text();
        vBox.getChildren().add(vBoxText);

        profile = new Polygon();
        profile.setId("profile");
        pane.getChildren().add(profile);

        group = new Group();
        pane.getChildren().add(group);

        grid = new Path();
        grid.setId("grid");
        pane.getChildren().add(grid);

        line = new Line();
        pane.getChildren().add(line);

        insets = new Insets(10, 10, 20, 40);

        borderPane.setCenter(pane);
        borderPane.setBottom(vBox);

        rectangle = new SimpleObjectProperty<>();

        screenToWorld = new SimpleObjectProperty<>();
        worldToScreen = new SimpleObjectProperty<>();

        setScreenToWorld();
        setWorldToScreen();

        addListeners();
        addBindings();
    }

    /**
     * Propriety of the position of the mouse on the profile
     *
     * @return propriety position of the mouse on the profile
     */
    public ReadOnlyDoubleProperty mousePositionOnProfileProperty(){
        return mousePosition;
    }

    /**
     * Pane of the elevation profile
     *
     * @return pane of the elevation profile
     */
    public Pane pane(){
        return borderPane;
    }

    private void setScreenToWorld(){
        ElevationProfile ele = elevationProfile.get();

        Affine affine = new Affine();
        if (ele != null){
            Rectangle2D rect = rectangle.get();

            affine.prependTranslation(-insets.getLeft(), -rect.getMaxY());
            affine.prependScale(1d/rect.getWidth(), -1d/rect.getHeight());
            affine.prependScale(ele.length(), ele.maxElevation() - ele.minElevation());
            affine.prependTranslation(0, ele.minElevation());
        }
        screenToWorld.set(affine);
    }

    private void setWorldToScreen(){
        try{
            worldToScreen.set(screenToWorld.get().createInverse());
        }catch (Exception ignored){}
    }

    private void updateRectangle(){
        if (pane.getWidth() - (insets.getLeft() + insets.getRight()) >= 0 && pane.getHeight() - (insets.getTop() + insets.getBottom()) >= 0){
            rectangle.set(new Rectangle2D(insets.getLeft(), insets.getTop(),
                    pane.getWidth() - (insets.getLeft() + insets.getRight()),
                    pane.getHeight() - (insets.getTop() + insets.getBottom())));
        }
    }

    private void updateVBox(){
        ElevationProfile ele = elevationProfile.get();

        vBoxText.setFont(Font.font("Avenir", 10));
        vBoxText.setTextOrigin(VPos.CENTER);
        vBoxText.setText(String.format(
                "Longueur : %.1f km" +
                        "     Montée : %.0f m" +
                        "     Descente : %.0f m" +
                        "     Altitude : de %.0f m à %.0f m",
                ele.length()/KM_METER_RATIO, ele.totalAscent(), ele.totalDescent(), ele.minElevation(), ele.maxElevation()
        ));
    }

    private void createPolygon(){
        profile.getPoints().clear();
        Rectangle2D rect = rectangle.get();
        ElevationProfile ele = elevationProfile.get();

        profile.getPoints().add(rect.getMinX());
        profile.getPoints().add(rect.getMaxY());

        for (double i = rect.getMinX(); i < rect.getMaxX(); i++) {
           profile.getPoints().add(i);
           profile.getPoints().add(worldToScreen.get().transform(0,
                   ele.elevationAt(screenToWorld.get().transform(i, 0).getX())).getY());
        }

        profile.getPoints().add(rect.getMaxX());
        profile.getPoints().add(rect.getMaxY());
    }

    private void createGrid(){
        grid.getElements().clear();
        Rectangle2D rect = rectangle.get();
        ElevationProfile ele = elevationProfile.get();
        group.getChildren().clear();

        int posStep = 0;
        int eleStep = 0;

        for(Integer i : POS_STEPS){
            if (i*rect.getWidth()/ele.length() >= MIN_PIXEL_LENGTH){
                posStep = i;
                break;
            }
        }
        if (posStep == 0){
            posStep = POS_STEPS[POS_STEPS.length - 1];
        }

        for(Integer i : ELE_STEPS){
            if (i*rect.getHeight()/(ele.maxElevation() - ele.minElevation()) >= MIN_PIXEL_ELEV){
                eleStep = i;
                break;
            }
        }
        if (eleStep == 0){
            eleStep = ELE_STEPS[ELE_STEPS.length - 1];
        }

        for (int i = 0; i <= (int)(ele.length()/posStep); i++) {

            MoveTo start = new MoveTo(rect.getMinX() + i*posStep*rectangle.get().getWidth()/ele.length(), rect.getMaxY());
            LineTo end = new LineTo(rect.getMinX() + i*posStep*rectangle.get().getWidth()/ele.length(), rect.getMinY());
            grid.getElements().add(start);
            grid.getElements().add(end);

            Text textHorizontal = new Text();

            textHorizontal.getStyleClass().add("grid_label");
            textHorizontal.getStyleClass().add("horizontal");
            textHorizontal.setTextOrigin(VPos.TOP);
            textHorizontal.setFont(Font.font("Avenir", 10));
            textHorizontal.setText(String.valueOf(i*posStep/KM_METER_RATIO));
            textHorizontal.setLayoutX(start.getX() - textHorizontal.prefWidth(0)/2);
            textHorizontal.setLayoutY(start.getY());
            group.getChildren().add(textHorizontal);
        }

        for (int i = 0; i <= (int)((ele.maxElevation() - ele.minElevation())/eleStep); i++) {
            double yOfFirstLine = ele.minElevation() - ele.minElevation()%eleStep + eleStep;

            MoveTo start = new MoveTo(rect.getMinX(),worldToScreen.get().transform(0, yOfFirstLine + i*eleStep).getY());
            LineTo end = new LineTo(rect.getMaxX(),worldToScreen.get().transform(0, yOfFirstLine + i*eleStep).getY());
            grid.getElements().add(start);
            grid.getElements().add(end);

            Text textVertical = new Text();

            textVertical.getStyleClass().add("grid_label");
            textVertical.getStyleClass().add("vertical");
            textVertical.setTextOrigin(VPos.CENTER);
            textVertical.setFont(Font.font("Avenir", 10));
            textVertical.setText(String.valueOf((int)(yOfFirstLine) + i*eleStep));
            textVertical.setLayoutX(start.getX() - (textVertical.prefWidth(0) + 2));
            textVertical.setLayoutY(start.getY());
            group.getChildren().add(textVertical);
        }
    }

    private void addBindings(){
        line.layoutXProperty().bind(Bindings.createDoubleBinding(() -> worldToScreen.get().transform(highlightedPosition.get(), 0).getX(), rectangle, highlightedPosition));
        line.startYProperty().bind(Bindings.select(rectangle, "minY"));
        line.endYProperty().bind(Bindings.select(rectangle, "maxY"));
        line.visibleProperty().bind(highlightedPosition.greaterThanOrEqualTo(0));
    }

    private void addListeners(){
        elevationProfile.addListener(e -> {
            if (elevationProfile.get() != null){
                updateRectangle();
                updateVBox();
            }
        });

        pane.widthProperty().addListener(e -> updateRectangle());
        pane.heightProperty().addListener(e -> updateRectangle());

        rectangle.addListener(e -> {
            setScreenToWorld();
            setWorldToScreen();
            createPolygon();
            createGrid();
        });

        pane.setOnMouseMoved(e -> {
            if (rectangle.get().contains(new Point2D(e.getX(), e.getY()))){
                mousePosition.set(screenToWorld.get().transform(e.getX(), 0).getX());
            }else {
                mousePosition.set(Double.NaN);
            }
        });

        pane.setOnMouseExited(e -> mousePosition.set(Double.NaN));
    }
}



