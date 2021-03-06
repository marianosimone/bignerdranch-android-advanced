package com.bignerdranch.android.nerdmail.view;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.LightingColorFilter;
import android.graphics.Paint;
import android.graphics.Point;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

import com.bignerdranch.android.nerdmail.R;
import com.bignerdranch.android.nerdmail.inject.Injector;
import com.bignerdranch.android.nerdmail.model.DataManager;
import com.bignerdranch.android.nerdmailservice.Email;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import timber.log.Timber;

public class EmailListItemView extends View implements View.OnTouchListener {

    private static final int PADDING_SIZE = 16;
    private static final int BODY_PADDING_SIZE = 4;
    private static final int LARGE_TEXT_SIZE = 16;
    private static final int SMALL_TEXT_SIZE = 14;
    private static final int STAR_SIZE = 24;
    private static final int DIVIDER_SIZE = 1;
    private static final String ELLIPSIS = "...";

    @Inject
    DataManager mDataManager;

    float mScreenDensity;
    // default padding size in pixels
    float mPaddingSize;
    // star pixel size (width & height)
    float mStarPixelSize;
    // divider pixel size
    float mDividerSize;
    // text sizes in pixels
    float mLargeTextSize;
    float mSmallTextSize;

    private Paint mBackgroundPaint;
    private Paint mDividerPaint;
    private Paint mStarPaint;

    private Bitmap mImportantStar;
    private Bitmap mUnimportantStar;

    private Email mEmail;
    private static final int MAX_LINES_TO_SHOW = 2;

    float mStarTop;
    float mStarLeft;

    private TextPaint mSenderAddressTextPaint;
    private TextPaint mSubjectTextPaint;
    private TextPaint mBodyTextPaint;

    public EmailListItemView(Context context) {
        this(context, null);
    }

    public EmailListItemView(final @NonNull Context context, final @Nullable AttributeSet attrs) {
        super(context, attrs);
        Injector.obtain(getContext()).inject(this);
        setOnTouchListener(this);

        mScreenDensity = context.getResources().getDisplayMetrics().density;
        mStarPixelSize = Math.round(STAR_SIZE * mScreenDensity);
        mDividerSize = Math.round(DIVIDER_SIZE * mScreenDensity);
        mPaddingSize = Math.round(PADDING_SIZE * mScreenDensity);

        // scale text size based on screen density and accessibility settings
        final Configuration configuration = context.getResources().getConfiguration();
        float textScale = configuration.fontScale * mScreenDensity;
        mLargeTextSize = Math.round(LARGE_TEXT_SIZE * textScale);
        mSmallTextSize = Math.round(SMALL_TEXT_SIZE * textScale);

        setupPaints();
        setupStarBitmaps();
    }

    public void setEmail(Email email) {
        mEmail = email;
    }

    private void setupPaints() {
        mBackgroundPaint = new Paint();
        mBackgroundPaint.setColor(ContextCompat.getColor(getContext(), R.color.white));
        mDividerPaint = new Paint();
        mDividerPaint.setColor(ContextCompat.getColor(getContext(), R.color.divider_color));
        mDividerPaint.setStrokeWidth(mDividerSize);
        mStarPaint = new Paint();
        final int starTint = ContextCompat.getColor(getContext(), R.color.star_tint);
        final ColorFilter colorFilter = new LightingColorFilter(starTint, 1);
        mStarPaint.setColorFilter(colorFilter);

        mSenderAddressTextPaint = new TextPaint();
        mSenderAddressTextPaint.setTextSize(mLargeTextSize);
        mSenderAddressTextPaint.setTextAlign(Paint.Align.LEFT);
        mSenderAddressTextPaint.setColor(ContextCompat.getColor(getContext(), R.color.black));
        mSenderAddressTextPaint.setAntiAlias(true);
        mSubjectTextPaint = new TextPaint();
        mSubjectTextPaint.setTextSize(mSmallTextSize);
        mSubjectTextPaint.setTextAlign(Paint.Align.LEFT);
        mSubjectTextPaint.setColor(ContextCompat.getColor(getContext(), R.color.black));
        mSubjectTextPaint.setAntiAlias(true);
        mBodyTextPaint = new TextPaint();
        mBodyTextPaint.setTextSize(mSmallTextSize);
        mBodyTextPaint.setTextAlign(Paint.Align.LEFT);
        mBodyTextPaint.setColor(ContextCompat.getColor(getContext(), R.color.body_color));
        mBodyTextPaint.setAntiAlias(true);
    }

    private void setupStarBitmaps() {
        int bitmapSize = (int) (STAR_SIZE * mScreenDensity);
        Bitmap importantBitmap = BitmapFactory
                .decodeResource(getResources(), R.drawable.ic_important);
        mImportantStar = Bitmap
                .createScaledBitmap(importantBitmap, bitmapSize, bitmapSize, false);
        Bitmap unimportantBitmap = BitmapFactory
                .decodeResource(getResources(), R.drawable.ic_unimportant);
        mUnimportantStar = Bitmap
                .createScaledBitmap(unimportantBitmap, bitmapSize, bitmapSize, false);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        final int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        final int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        final int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        final int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        final int width;
        final int height;
        if (widthMode == MeasureSpec.EXACTLY || widthMode == MeasureSpec.AT_MOST) {
            width = widthSize;
        } else {
            width = calculateWidth();
        }
        if (heightMode == MeasureSpec.EXACTLY || heightMode == MeasureSpec.AT_MOST) {
            height = heightSize;
        } else {
            height = calculateHeight();
        }
        setMeasuredDimension(width, height);
    }

    @Override
    protected void onDraw(final @NonNull Canvas canvas) {
        int height = canvas.getHeight();
        int width = canvas.getWidth();

        // draw paint to clear canvas
        canvas.drawPaint(mBackgroundPaint);
        // Draw the divider across the bottom of the canvas
        float dividerY = height - (mDividerSize / 2);
        canvas.drawLine(0, dividerY, width, dividerY, mDividerPaint);

        // Draw the sender address
        final Paint.FontMetrics fm = mSenderAddressTextPaint.getFontMetrics();
        float senderX = mPaddingSize;
        float senderTop = (float) Math.ceil(Math.abs(fm.top));
        float senderBottom = (float) Math.ceil(Math.abs(fm.bottom));
        float senderBaseline = mPaddingSize + senderTop;
        float senderY = senderBaseline + senderBottom;
        canvas.drawText(
                mEmail.getSenderAddress(), senderX, senderBaseline, mSenderAddressTextPaint);

        // Draw the subject
        final Paint.FontMetrics subjectFm = mSubjectTextPaint.getFontMetrics();
        float subjectX = PADDING_SIZE * mScreenDensity;
        float subjectTop = (float) Math.ceil(Math.abs(subjectFm.top));
        float subjectBottom = (float) Math.ceil(Math.abs(subjectFm.bottom));
        float subjectBaseline = senderY + subjectTop;
        float subjectY = subjectBaseline + subjectBottom;
        canvas.drawText(mEmail.getSubject(), subjectX, subjectBaseline, mSubjectTextPaint);

        // Draw the body
        final Paint.FontMetrics bodyFm = mBodyTextPaint.getFontMetrics();
        float bodyX = mPaddingSize;
        float bodyBottom = (float) Math.ceil(Math.abs(bodyFm.bottom));
        float extraBodySpacing = 12 * mScreenDensity;

        final Collection<String> bodyLines = getBodyLines();
        float lastBottom = subjectY;
        for (final String line : bodyLines) {
            float baseLine = lastBottom + bodyBottom + extraBodySpacing;
            canvas.drawText(line, bodyX, baseLine, mBodyTextPaint);
            lastBottom = baseLine + bodyBottom;
        }

        // Draw the star
        mStarLeft = getWidth() - mPaddingSize - mStarPixelSize;
        mStarTop = senderTop;
        if (mEmail.isImportant()) {
            canvas.drawBitmap(mImportantStar, mStarLeft, mStarTop, mStarPaint);
        } else {
            canvas.drawBitmap(mUnimportantStar, mStarLeft, mStarTop, mStarPaint);
        }
    }

    private int calculateWidth() {
        final Point size = new Point();
        final WindowManager windowManager = (WindowManager) getContext()
                .getSystemService(Context.WINDOW_SERVICE);
        windowManager.getDefaultDisplay().getSize(size);
        // use window width if unspecified
        return size.x;
    }

    private int calculateHeight() {
        int layoutPadding = getPaddingTop() + getPaddingBottom();
        Paint.FontMetrics senderFm = mSenderAddressTextPaint.getFontMetrics();
        float senderHeight = getFontHeight(senderFm);
        Paint.FontMetrics subjectFm = mSubjectTextPaint.getFontMetrics();
        float subjectHeight = getFontHeight(subjectFm);
        Paint.FontMetrics bodyFm = mBodyTextPaint.getFontMetrics();
        float bodyHeight = getFontHeight(bodyFm);
        float bodyPadding = BODY_PADDING_SIZE * mScreenDensity;
        float totalHeight = layoutPadding + mPaddingSize + senderHeight
                + subjectHeight + (bodyHeight * MAX_LINES_TO_SHOW) + bodyPadding
                + mPaddingSize + mDividerSize;
        return (int) totalHeight;
    }

    private float getFontHeight(Paint.FontMetrics metrics) {
        return (float) (Math.ceil(Math.abs(metrics.top)) +
                Math.ceil(Math.abs(metrics.bottom)));
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (isStarClick(event)) {
                    mEmail.setImportant(!mEmail.isImportant());
                    mDataManager.updateEmail(mEmail);
                    invalidate();
                    return true;
                }
                Timber.d("Star was not clicked");
                return true;
        }
        return false;
    }

    private boolean isStarClick(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();
        float starRight = mStarLeft + mStarPixelSize;
        float starBottom = mStarTop + mStarPixelSize;
        boolean isXInStarRange = (x >= mStarLeft) && (x <= starRight);
        boolean isYInStarRange = (y >= mStarTop) && (y <= starBottom);
        return isXInStarRange && isYInStarRange;
    }

    public Collection<String> getBodyLines() {
        final float bodyWidth = getWidth() - (2 * mPaddingSize) - mStarPixelSize - mPaddingSize;
        if (mBodyTextPaint.measureText(mEmail.getBody()) < bodyWidth) {
            return Collections.singleton(mEmail.getBody());
        }

        final List<String> lines = new ArrayList<>(MAX_LINES_TO_SHOW);
        final String[] words = mEmail.getBody().split(" ");
        String currentLine = words[0];
        int currentWord = 1;
        while (lines.size() < MAX_LINES_TO_SHOW && currentWord < words.length) {
            while (currentWord < words.length && mBodyTextPaint.measureText(currentLine + " " + words[currentWord]) < bodyWidth) {
                currentLine += " " + words[currentWord];
                currentWord += 1;
            }
            lines.add(currentLine);
            currentLine = currentWord < words.length ? words[currentWord] : "";
            currentWord += 1;
        }
        if (currentWord < words.length) {
            final String lastLine = lines.remove(lines.size() - 1);
            lines.add(lastLine.substring(0, lastLine.length() - ELLIPSIS.length()) + ELLIPSIS);
        }
        return lines;
    }
}
