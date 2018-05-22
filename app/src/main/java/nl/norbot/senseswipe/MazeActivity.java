package nl.norbot.senseswipe;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class MazeActivity extends AppCompatActivity {
    boolean initialized = false;

    private Canvas canvas;
    private Paint paint = new Paint();
    private Bitmap bitmap;

    private Point currentPosition = new Point();
    private int gridsize = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maze);

        ImageView screen = findViewById(R.id.maze_imageviewer);
        Log.d("Screenwidth", String.valueOf(screen.getWidth()));

        Rect vierkantje = new Rect();
        //draw(screen);
    }

    public void onWindowFocusChanged(boolean hasFocus) {
        // Gets called when window changes focus
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            ImageView img = (ImageView) findViewById(R.id.maze_imageviewer);
            Log.d("Screenwidth", "width : " + img.getWidth());

            int screenwidth = img.getWidth();
            int screenheight = img.getHeight();

            if (initialized == false) {
                currentPosition.x = screenwidth / 2;
                currentPosition.y = screenheight / 2;
            }
            draw(img);
            initialized = true;
        }
    }

    public void moveright(View view){
        ImageView image = (ImageView) view;
        currentPosition.x += image.getWidth() / 5;
        draw(image);
    }

    public void draw(ImageView view) {
        int screenWidth = view.getWidth();
        int screenHeight = view.getHeight();

        Log.d("Screenwidth", String.valueOf(screenWidth));

        bitmap = Bitmap.createBitmap(screenWidth, screenHeight, Bitmap.Config.ARGB_8888);

        view.setImageBitmap(bitmap);
        canvas = new Canvas(bitmap);

        //canvas.drawColor(Color.RED); //Background color
        canvas.drawCircle(currentPosition.x, currentPosition.y, 50, paint);

        view.invalidate();

    }


}
