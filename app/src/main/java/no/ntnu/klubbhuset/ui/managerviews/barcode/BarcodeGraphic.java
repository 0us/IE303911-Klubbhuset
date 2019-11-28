// Copyright 2018 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//      http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
package no.ntnu.klubbhuset.ui.managerviews.barcode;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;

import com.google.firebase.ml.vision.barcode.FirebaseVisionBarcode;

import no.ntnu.klubbhuset.util.mlkit.GraphicOverlay;

/** Graphic instance for rendering Barcode position and content information in an overlay view. */
public class BarcodeGraphic extends GraphicOverlay.Graphic {

  private static final int TEXT_COLOR_SUCCESS = Color.GREEN;
  private static final int TEXT_COLOR_FAILURE = Color.RED;
  private static final float TEXT_SIZE = 144.0f;
  private static final float STROKE_WIDTH = 16.0f;

  private final Paint rectPaint;
  private final Paint barcodePaint;
  private final FirebaseVisionBarcode barcode;
  private final String text;

  BarcodeGraphic(GraphicOverlay overlay, FirebaseVisionBarcode barcode, String text) {
    super(overlay);

    this.barcode = barcode;
    this.text = text;

    // TextColor is always TEXT_COLOR_FAILURE unless text is "has paid"
    int textColor = TEXT_COLOR_FAILURE;
    if (text.equals("Has paid")) {
        textColor = TEXT_COLOR_SUCCESS;
    }

    rectPaint = new Paint();
    rectPaint.setColor(textColor);
    rectPaint.setStyle(Paint.Style.STROKE);
    rectPaint.setStrokeWidth(STROKE_WIDTH);

    barcodePaint = new Paint();
    barcodePaint.setColor(textColor);
    barcodePaint.setTextSize(TEXT_SIZE);
    barcodePaint.setTextAlign(Paint.Align.CENTER);
    barcodePaint.setShadowLayer(5.0f, 10.0f, 10.0f, Color.BLACK);
  }

  /**
   * Draws the barcode block annotations for position, size, and raw value on the supplied canvas.
   */
  @Override
  public void draw(Canvas canvas) {
    if (barcode == null) {
      throw new IllegalStateException("Attempting to draw a null barcode.");
    }

    // Draws the bounding box around the BarcodeBlock.
    RectF rect = new RectF(barcode.getBoundingBox());
    rect.left = translateX(rect.left);
    rect.top = translateY(rect.top);
    rect.right = translateX(rect.right);
    rect.bottom = translateY(rect.bottom);
    canvas.drawRect(rect, rectPaint);


    // Renders the barcode at the center-left of the box.
    canvas.drawText(text, rect.centerX(), rect.centerY(), barcodePaint);
  }
}
