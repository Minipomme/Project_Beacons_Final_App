package beacon.projetco.dii.polytech.tours.univ.beaconsfinder;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;
import android.util.Log;

/**
 * Created by Minipomme.
 */

public class Thermometer extends View {

    private float outerCircleRadius, outerRectRadius;
    private Paint outerPaint;

    private float middleCircleRadius, middleRectRadius;
    private Paint middlePaint;

    private float innerCircleRadius, innerRectRadius;
    private Paint innerPaint;

    private Paint degreePaint, graduationPaint;

    // Static Variable Temperature
    private static final int GRADUATION_TEXT_SIZE = 16; // in sp
    private static float DEGREE_WIDTH = 30;
    private static final int NB_GRADUATIONS = 8;
    public static final float MAX_DIST = 5, MIN_DIST = 0;
    private static final float RANGE_DIST = MAX_DIST - MIN_DIST;

    // Local Variable Temperature
    private int nbGraduations = NB_GRADUATIONS;
    private float maxDist = MAX_DIST;
    private float minDist = MIN_DIST;
    private float rangeDist = RANGE_DIST;
    private float currentDist = MIN_DIST;
    private int currentInnerColor = Color.RED;

    private Rect rect = new Rect();

    public Thermometer(Context context) {
        super(context);
        init(context, null);
    }

    public Thermometer(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public Thermometer(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    public void setCurrentInnerColor(int newColor) {
        this.currentInnerColor = newColor;
    }

    public void setCurrentDist(float currentDist){
        Log.d("STATE","Salut !!!");
        if (currentDist > maxDist) {
            this.currentDist = maxDist;
        } else if (currentDist < minDist) {
            this.currentDist = minDist;
        } else {
            this.currentDist = currentDist;
        }

        invalidate();
    }

    public float getMinDist() {
        return minDist;
    }

    public void init(Context context, AttributeSet attrs) {
        Log.d("STATE","INIT !!!");
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.Thermometer);
        outerCircleRadius = typedArray.getDimension(R.styleable.Thermometer_radius, 20f);
        int outerColor = typedArray.getColor(R.styleable.Thermometer_outerColor, Color.GRAY);
        int middleColor = typedArray.getColor(R.styleable.Thermometer_middleColor, Color.WHITE);
        int innerColor = typedArray.getColor(R.styleable.Thermometer_innerColor, Color.RED);

        typedArray.recycle();

        outerRectRadius = outerCircleRadius / 2;
        outerPaint = new Paint();

        outerPaint.setColor(outerColor);
        outerPaint.setStyle(Paint.Style.FILL);

        middleCircleRadius = outerCircleRadius - 5;
        middleRectRadius = outerRectRadius - 5;
        middlePaint = new Paint();
        middlePaint.setColor(middleColor);
        middlePaint.setStyle(Paint.Style.FILL);

        innerCircleRadius = middleCircleRadius - middleCircleRadius / 6;
        innerRectRadius = middleRectRadius - middleRectRadius / 6;
        innerPaint = new Paint();
        innerPaint.setColor(innerColor);
        innerPaint.setStyle(Paint.Style.FILL);

        DEGREE_WIDTH = middleCircleRadius / 8;

        degreePaint = new Paint();
        degreePaint.setStrokeWidth(middleCircleRadius / 16);
        degreePaint.setStyle(Paint.Style.FILL);

        graduationPaint = new Paint();
        graduationPaint.setColor(outerColor);
        graduationPaint.setStyle(Paint.Style.FILL);
        graduationPaint.setAntiAlias(true);
        graduationPaint.setTextSize(Utils.convertDpToPixel(GRADUATION_TEXT_SIZE, getContext()));
        Log.d("STATE","FIN INIT !!!");
    }

    @Override
    protected void onDraw(Canvas canvas) {
        Log.d("STATE","ON DRAW !!!");
        super.onDraw(canvas);

        innerPaint.setColor(currentInnerColor);

        int height = getHeight();
        int width = getWidth();

        int circleCenterX = width / 2;
        float circleCenterY = height - outerCircleRadius;
        float outerStartY = 0;
        float middleStartY = outerStartY + 5;

        float innerEffectStartY = middleStartY + middleRectRadius + 10;
        float innerEffectEndY = circleCenterY - outerCircleRadius - 10;
        float innerRectHeight = innerEffectEndY - innerEffectStartY;
        float innerStartY = innerEffectEndY + (maxDist - currentDist) / rangeDist * (-innerRectHeight);

        RectF outerRect = new RectF();
        outerRect.left  = circleCenterX - outerRectRadius;
        outerRect.top = outerStartY;
        outerRect.right = circleCenterX + outerRectRadius;
        outerRect.bottom = circleCenterY;

        canvas.drawRoundRect(outerRect, outerRectRadius, outerRectRadius, outerPaint);
        canvas.drawCircle(circleCenterX, circleCenterY, outerCircleRadius, outerPaint);

        RectF middleRect = new RectF();
        middleRect.left  = circleCenterX - middleRectRadius;
        middleRect.top = outerStartY;
        middleRect.right = circleCenterX + middleRectRadius;
        middleRect.bottom = circleCenterY;

        canvas.drawRoundRect(middleRect, middleRectRadius, middleRectRadius, middlePaint);
        canvas.drawCircle(circleCenterX, circleCenterY, middleCircleRadius, middlePaint);

        canvas.drawRect(circleCenterX - innerRectRadius, innerStartY, circleCenterX + innerRectRadius, circleCenterY, innerPaint);
        canvas.drawCircle(circleCenterX, circleCenterY, innerCircleRadius, innerPaint);

        float tmp = innerEffectStartY;
        //float startGraduation = minDist;
        //float inc = rangeDist / nbGraduations;
        String[] txt_field = {"Allumez ! Le feu !", "Tu le vois !", "Il est où ??", "Tu y es presque !", "Tiède", "Il fait froid !", "Tu est gelé", "Tabernak", "Il fait -8000"};

        for (int i = 0; tmp <= innerEffectEndY; i++) {
            canvas.drawLine(circleCenterX - outerRectRadius - DEGREE_WIDTH, tmp, circleCenterX - outerRectRadius, tmp, degreePaint);
            graduationPaint.getTextBounds(txt_field[i], 0, txt_field[i].length(), rect);
            float textWidth = rect.width();
            float textHeight = rect.height();

            canvas.drawText(txt_field[i], circleCenterX - outerRectRadius - DEGREE_WIDTH - textWidth - DEGREE_WIDTH * 1.5f,
                    tmp + textHeight / 2, graduationPaint);
            tmp += (innerEffectEndY - innerEffectStartY) / nbGraduations;
            //startGraduation += inc;
        }
        Log.d("STATE","fin ON DRAW !!!");
    }
}
