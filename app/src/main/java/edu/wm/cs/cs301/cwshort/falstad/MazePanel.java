package edu.wm.cs.cs301.cwshort.falstad;

//import java.awt.Color;
//import java.awt.Font;
//import java.awt.FontMetrics;
//import java.awt.Graphics;
//import java.awt.Graphics2D;
//import java.awt.Image;
//import java.awt.Panel;
//import java.awt.RenderingHints;
//import javax.swing.JComboBox;

import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.*;
import android.graphics.*;
import android.content.Context;

import edu.wm.cs.cs301.cwshort.R;

/**
 * Created by cshort on 12/2/17.
 */

public class MazePanel extends View {

    private Paint paint;
    private Canvas canvas;
    private Bitmap bitmap;
    private Drawable myImage;

//    public MazePanel(Context context) {
//        super(context);
//        bitmap = Bitmap.createBitmap(Constants.VIEW_WIDTH, Constants.VIEW_HEIGHT, Bitmap.Config.ARGB_8888);
//        canvas = new Canvas(bitmap);
//        paint = new Paint();
//        paint.setStyle(Paint.Style.FILL);
//
//    }

    public MazePanel(Context context) {
        super(context);
        init();

    }

    public void init() {
        paint = new Paint();
        paint.setStyle(Paint.Style.FILL);
        bitmap = Bitmap.createBitmap(Constants.VIEW_WIDTH, Constants.VIEW_HEIGHT, Bitmap.Config.ARGB_8888);
        canvas = new Canvas(bitmap);
        //myImage = getContext().getResources().getDrawable(R.drawable.maze);

    }
    /**
     * Constructor with two parameters: context and attributes.
     * @param context
     * @param app
     */
    public MazePanel(Context context, AttributeSet app) {
        super(context, app);
    }

    @Override
    public void onDraw(Canvas androidCanvas) {
        super.onDraw(androidCanvas);
        androidCanvas.drawBitmap(bitmap, 0, 0, paint);
    }

    public Object getBufferGraphics() {
        return canvas;
    }

    @Override
    public void onMeasure(int width, int height) {
        super.onMeasure(width, height);
        setMeasuredDimension(Constants.VIEW_WIDTH, Constants.VIEW_HEIGHT);
    }

    public void update() {
        canvas.drawBitmap(bitmap, 0, 0, paint);
        postInvalidate();
    }

    public Canvas getCanvas() {
        return this.canvas;
    }

    public void paint(Paint paint) {
        if (null == paint) {
            System.out.println("MazePanel.paint: no graphics object, skipping drawImage operation");
        }
        else {
            this.canvas.drawBitmap(bitmap,0,0,null);
        }
    }

    public void initBufferImage() {
        bitmap = Bitmap.createBitmap(1300, 1300, Bitmap.Config.ARGB_8888);
        if (null == bitmap) {
            System.out.println("Error: creation of bitmap failed, presumedly container not displayable");
        }
    }

    public Paint getPaint() { return this.paint; }

    public void setFont(String font) {
        this.paint.setFontFeatureSettings(font);
    }

    /**
     * Takes in polygon params, fills polygon in canvas based on these.
     * Paint is always that for corn.
     * @param xPoints
     * @param yPoints
     * @param nPoints
     * */
    public void fillPolygon(int[] xPoints, int[] yPoints, int nPoints) {
        Path path = new Path();
        path.reset();
        for (int i = 0; i < nPoints; i++) {
            path.lineTo(xPoints[i], yPoints[i]);
        }
        path.lineTo(xPoints[0], yPoints[0]);

        canvas.drawPath(path, paint);
    }

    public void drawImage(int[] xPoints, int[] yPoints, int nPoints) {
        Path path = new Path();
        path.reset();
        for (int i = 0; i < nPoints; i++) {
            path.lineTo(xPoints[i], yPoints[i]);
        }
        path.lineTo(xPoints[0], yPoints[0]);
        canvas.drawRect(xPoints[0], yPoints[0], xPoints[1], yPoints[1], paint);
        myImage.setBounds(new Rect(xPoints[0], yPoints[0], xPoints[1], yPoints[1]));
        myImage.draw(canvas);

    }

    public void fillRect(int x, int y, int width, int height) {
        canvas.drawRect(x, y, x + width, y + height, paint);
    }

    /**
     * Takes in line params, draws line in canvas based on these.
     * @param x1
     * @param y1
     * @param x2
     * @param y2
     */
    public void drawLine(int x1, int y1, int x2, int y2) {
        canvas.drawLine(x1, y1, x2, y2, paint);
    }

    /**
     * Takes in oval params, fills oval in canvas based on these.
     * @param x
     * @param y
     * @param width
     * @param height
     */
    public void fillOval(int x, int y, int width, int height) {
        this.paint.setStyle(Paint.Style.FILL_AND_STROKE);
        int left = x;
        int top = y;
        int right = x + width;
        int bottom = y + height;
        canvas.drawOval(new RectF(left, top, right, bottom), paint);
    }

    public void drawString(String str, int x, int y, Paint paint) {
        canvas.drawText(str, x, y, paint);
    }

    public Bitmap getBitMap(){
        return bitmap;
    }

    public void setColor(int color) {
        switch (color) {
            case Color.BLACK:
                paint.setColor(Color.BLACK);
                break;
            case Color.BLUE:
                paint.setColor(Color.BLUE);
                break;
            case Color.DKGRAY:
                paint.setColor(Color.DKGRAY);
                break;
            case Color.GRAY:
                paint.setColor(Color.GRAY);
                break;
            case Color.CYAN:
                paint.setColor(Color.CYAN);
                break;
            case Color.RED:
                paint.setColor(Color.RED);
                break;
            case Color.WHITE:
                paint.setColor(Color.WHITE);
                break;
            case Color.YELLOW:
                paint.setColor(Color.YELLOW);
                break;
        }
    }
    public void setColor(int r, int g, int b) {
        paint.setColor(Color.rgb(r, g, b));
    }

    public static int getRGB(int red, int green, int blue) {
        return Color.rgb(red, green, blue);
    }

    public static int[] getRGBArray(int colorInt) {
        return new int[] {Color.red(colorInt), Color.green(colorInt), Color.blue(colorInt)};
    }
}