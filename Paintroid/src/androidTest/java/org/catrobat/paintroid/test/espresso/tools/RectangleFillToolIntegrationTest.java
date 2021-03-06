/**
 *  Paintroid: An image manipulation application for Android.
 *  Copyright (C) 2010-2015 The Catrobat Team
 *  (<http://developer.catrobat.org/credits>)
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as
 *  published by the Free Software Foundation, either version 3 of the
 *  License, or (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  GNU Affero General Public License for more details.
 *
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.catrobat.paintroid.test.espresso.tools;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.PointF;
import android.support.test.espresso.Espresso;
import android.support.test.espresso.IdlingResource;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.catrobat.paintroid.MainActivity;
import org.catrobat.paintroid.PaintroidApplication;
import org.catrobat.paintroid.R;
import org.catrobat.paintroid.dialog.IndeterminateProgressDialog;
import org.catrobat.paintroid.test.espresso.util.DialogHiddenIdlingResource;
import org.catrobat.paintroid.test.utils.PrivateAccess;
import org.catrobat.paintroid.test.utils.SystemAnimationsRule;
import org.catrobat.paintroid.tools.Tool;
import org.catrobat.paintroid.tools.ToolType;
import org.catrobat.paintroid.tools.implementation.BaseToolWithRectangleShape;
import org.catrobat.paintroid.tools.implementation.BaseToolWithShape;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.pressBack;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.matcher.ViewMatchers.isRoot;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static org.catrobat.paintroid.test.espresso.util.EspressoUtils.BLACK_COLOR_PICKER_BUTTON_POSITION;
import static org.catrobat.paintroid.test.espresso.util.EspressoUtils.FIELD_NAME_BOX_HEIGHT;
import static org.catrobat.paintroid.test.espresso.util.EspressoUtils.FIELD_NAME_BOX_WIDTH;
import static org.catrobat.paintroid.test.espresso.util.EspressoUtils.FIELD_NAME_DRAWING_BITMAP;
import static org.catrobat.paintroid.test.espresso.util.EspressoUtils.FIELD_NAME_TOOL_POSITION;
import static org.catrobat.paintroid.test.espresso.util.EspressoUtils.TRANSPARENT_COLOR_PICKER_BUTTON_POSITION;
import static org.catrobat.paintroid.test.espresso.util.EspressoUtils.clickSelectedToolButton;
import static org.catrobat.paintroid.test.espresso.util.EspressoUtils.getWorkingBitmap;
import static org.catrobat.paintroid.test.espresso.util.EspressoUtils.resetColorPicker;
import static org.catrobat.paintroid.test.espresso.util.EspressoUtils.resetDrawPaintAndBrushPickerView;
import static org.catrobat.paintroid.test.espresso.util.EspressoUtils.selectColorPickerPresetSelectorColor;
import static org.catrobat.paintroid.test.espresso.util.EspressoUtils.selectTool;
import static org.catrobat.paintroid.test.espresso.util.UiInteractions.touchAt;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
public class RectangleFillToolIntegrationTest {

	@Rule
	public ActivityTestRule<MainActivity> launchActivityRule = new ActivityTestRule<>(MainActivity.class);

	@Rule
	public SystemAnimationsRule systemAnimationsRule = new SystemAnimationsRule();

	private IdlingResource dialogWait;

	private Bitmap workingBitmap;

	@Before
	public void setUp() throws NoSuchFieldException, IllegalAccessException {
		dialogWait = new DialogHiddenIdlingResource(IndeterminateProgressDialog.getInstance());
		Espresso.registerIdlingResources(dialogWait);

		PaintroidApplication.drawingSurface.destroyDrawingCache();

		workingBitmap = getWorkingBitmap();

		selectTool(ToolType.BRUSH);
		resetColorPicker();
		resetDrawPaintAndBrushPickerView();
	}

	@After
	public void tearDown() {
		Espresso.unregisterIdlingResources(dialogWait);

		if(workingBitmap != null && !workingBitmap.isRecycled()) {
			workingBitmap.recycle();
		}
	}

	@Test
	public void testFilledRectIsCreated() throws SecurityException, IllegalArgumentException, NoSuchFieldException, IllegalAccessException {
		selectTool(ToolType.SHAPE);

		Tool mRectangleFillTool = PaintroidApplication.currentTool;
		float rectWidth = (Float) PrivateAccess.getMemberValue(BaseToolWithRectangleShape.class, mRectangleFillTool, FIELD_NAME_BOX_WIDTH);
		float rectHeight = (Float) PrivateAccess.getMemberValue(BaseToolWithRectangleShape.class, mRectangleFillTool, FIELD_NAME_BOX_HEIGHT);
		PointF rectPosition = (PointF) PrivateAccess.getMemberValue(BaseToolWithShape.class, mRectangleFillTool, FIELD_NAME_TOOL_POSITION);

		assertTrue("Width should not be zero", rectWidth != 0.0f);
		assertTrue("Width should not be zero", rectHeight != 0.0f);
		assertNotNull("Position should not be NULL", rectPosition);
	}

	/**
	 * Fails if whole espresso tests run, there lives an artifact in drawing surface:
	 * AssertionError: expected:<0> but was:<-16777216>
	 */
	@Ignore
	@Test
	public void testEllipseIsDrawnOnBitmap() throws SecurityException, IllegalArgumentException, NoSuchFieldException, IllegalAccessException {

		PaintroidApplication.perspective.setScale(1.0f);

		selectTool(ToolType.SHAPE);

		onView(withId(R.id.shapes_circle_btn)).perform(click());

		Tool ellipseTool = PaintroidApplication.currentTool;
		PointF centerPointTool = (PointF) PrivateAccess.getMemberValue(BaseToolWithShape.class, ellipseTool, FIELD_NAME_TOOL_POSITION);
		float rectHeight = (Float) PrivateAccess.getMemberValue(BaseToolWithRectangleShape.class, ellipseTool, FIELD_NAME_BOX_HEIGHT);

		PointF pointUnderTest = new PointF(centerPointTool.x, centerPointTool.y);
		int colorBeforeDrawing = PaintroidApplication.drawingSurface.getPixel(pointUnderTest);

		clickSelectedToolButton();

		onView(isRoot()).perform(touchAt(centerPointTool.x - 1, centerPointTool.y - 1));

		pressBack();

		int colorPickerColor = PaintroidApplication.currentTool.getDrawPaint().getColor();

		int colorAfterDrawing = PaintroidApplication.drawingSurface.getPixel(pointUnderTest);

		assertEquals("Pixel should have the same color as currently in color picker", colorPickerColor, colorAfterDrawing);

		onView(withId(R.id.btn_top_undo)).perform(click());

		int colorAfterUndo = PaintroidApplication.drawingSurface.getPixel(pointUnderTest);
		assertEquals(colorBeforeDrawing, colorAfterUndo);

		onView(withId(R.id.btn_top_redo)).perform(click());

		int colorAfterRedo = PaintroidApplication.drawingSurface.getPixel(pointUnderTest);
		assertEquals(colorPickerColor, colorAfterRedo);

		pointUnderTest.x = centerPointTool.x + (rectHeight / 2.5f);
		colorAfterDrawing = PaintroidApplication.drawingSurface.getPixel(pointUnderTest);
		assertEquals("Pixel should have the same color as currently in color picker", colorPickerColor, colorAfterDrawing);

		pointUnderTest.y = centerPointTool.y + (rectHeight / 2.5f);
		// now the point under test is diagonal from the center -> if its a circle there should be no color
		colorAfterDrawing = PaintroidApplication.drawingSurface.getPixel(pointUnderTest);
		assertTrue("Pixel should not have been filled for a circle", (colorPickerColor != colorAfterDrawing));
		PaintroidApplication.commandManager.resetAndClear(true);
	}

	@Test
	public void testRectOnBitmapHasSameColorAsInColorPickerAfterColorChange() throws SecurityException, IllegalArgumentException, NoSuchFieldException, IllegalAccessException {
		int colorPickerColorBeforeChange = PaintroidApplication.currentTool.getDrawPaint().getColor();

		final int colorButtonPosition = 5;
		selectColorPickerPresetSelectorColor(colorButtonPosition);

		int colorPickerColorAfterChange = PaintroidApplication.currentTool.getDrawPaint().getColor();
		assertTrue("Colors should not be the same", colorPickerColorAfterChange != colorPickerColorBeforeChange);

		selectTool(ToolType.SHAPE);

		int colorInRectangleTool = PaintroidApplication.currentTool.getDrawPaint().getColor();
		assertEquals("Colors should be the same", colorPickerColorAfterChange, colorInRectangleTool);

		Tool mRectangleFillTool = PaintroidApplication.currentTool;

		float rectWidth = (Float) PrivateAccess.getMemberValue(BaseToolWithRectangleShape.class, mRectangleFillTool, FIELD_NAME_BOX_WIDTH);
		float rectHeight = (Float) PrivateAccess.getMemberValue(BaseToolWithRectangleShape.class, mRectangleFillTool, FIELD_NAME_BOX_HEIGHT);
		Bitmap drawingBitmap = (Bitmap) PrivateAccess.getMemberValue(BaseToolWithRectangleShape.class, mRectangleFillTool, FIELD_NAME_DRAWING_BITMAP);

		int colorInRectangle = drawingBitmap.getPixel((int) (rectWidth / 2), (int) (rectHeight / 2));
		assertEquals("Colors should be the same", colorPickerColorAfterChange, colorInRectangle);
	}

	@Test
	public void testFilledRectChangesColor() throws SecurityException, IllegalArgumentException, NoSuchFieldException, IllegalAccessException {
		selectTool(ToolType.SHAPE);

		Tool mRectangleFillTool = PaintroidApplication.currentTool;

		int colorInRectangleTool = mRectangleFillTool.getDrawPaint().getColor();

		float rectWidth = (Float) PrivateAccess.getMemberValue(BaseToolWithRectangleShape.class, mRectangleFillTool, FIELD_NAME_BOX_WIDTH);
		float rectHeight = (Float) PrivateAccess.getMemberValue(BaseToolWithRectangleShape.class, mRectangleFillTool, FIELD_NAME_BOX_HEIGHT);
		Bitmap drawingBitmap = (Bitmap) PrivateAccess.getMemberValue(BaseToolWithRectangleShape.class, mRectangleFillTool, FIELD_NAME_DRAWING_BITMAP);

		int colorInRectangle = drawingBitmap.getPixel((int) (rectWidth / 2), (int) (rectHeight / 2));
		assertEquals("Colors should be equal", colorInRectangleTool, colorInRectangle);

		final int colorButtonPosition = 5;
		selectColorPickerPresetSelectorColor(colorButtonPosition);

		int colorInRectangleToolAfter = mRectangleFillTool.getDrawPaint().getColor();

		Bitmap drawingBitmapAfter = (Bitmap) PrivateAccess.getMemberValue(BaseToolWithRectangleShape.class, mRectangleFillTool, FIELD_NAME_DRAWING_BITMAP);

		int colorInRectangleAfter = drawingBitmapAfter.getPixel((int) (rectWidth / 2), (int) (rectHeight / 2));

		assertTrue("Colors should have changed", colorInRectangle != colorInRectangleAfter);
		assertTrue("Colors should have changed", colorInRectangleTool != colorInRectangleToolAfter);
		assertEquals("Colors should be equal", colorInRectangleTool, colorInRectangle);
	}

	@Test
	public void testEraseWithEllipse() throws NoSuchFieldException, IllegalAccessException {
		selectTool(ToolType.SHAPE);
		selectShapeTypeAndDraw(R.id.shapes_square_btn, false, TRANSPARENT_COLOR_PICKER_BUTTON_POSITION);

		clickSelectedToolButton();

		selectShapeTypeAndDraw(R.id.shapes_circle_btn, true, TRANSPARENT_COLOR_PICKER_BUTTON_POSITION);
	}

	@Test
	public void testDrawWithDrawableShape() throws NoSuchFieldException, IllegalAccessException {
		selectTool(ToolType.SHAPE);
		selectShapeTypeAndDraw(R.id.shapes_heart_btn, false, BLACK_COLOR_PICKER_BUTTON_POSITION);
	}

	@Test
	public void testCheckeredBackgroundWhenTransparentColorSelected() throws NoSuchFieldException, IllegalAccessException {
		selectTool(ToolType.SHAPE);

		onView(withId(R.id.shapes_heart_btn)).perform(click());

		clickSelectedToolButton();

		selectColorPickerPresetSelectorColor(TRANSPARENT_COLOR_PICKER_BUTTON_POSITION);

		Tool tool = PaintroidApplication.currentTool;
		Bitmap drawingBitmap = (Bitmap) PrivateAccess.getMemberValue(BaseToolWithRectangleShape.class, tool, FIELD_NAME_DRAWING_BITMAP);
		int width = drawingBitmap.getWidth();
		int height = drawingBitmap.getHeight();
		Point upperLeftQuarter = new Point((int)(width*0.25), (int)(height*0.25));
		Point upperRightQuarter = new Point((int)(width*0.75), (int)(height*0.25));
		Point lowerRightQuarter = new Point((int)(width*0.75), (int)(height*0.75));
		Point lowerLeftQuarter = new Point((int)(width*0.25), (int)(height*0.75));

		int checkeredWhite = Color.WHITE;
		int checkeredGray = 0xFFC0C0C0;

		int pixelColor = drawingBitmap.getPixel(upperLeftQuarter.x, upperLeftQuarter.y);
		assertTrue("Color should correspond to checkered pattern", pixelColor == checkeredGray || pixelColor == checkeredWhite);

		pixelColor = drawingBitmap.getPixel(upperRightQuarter.x, upperRightQuarter.y);
		assertTrue("Color should correspond to checkered pattern", pixelColor == checkeredGray || pixelColor == checkeredWhite);

		pixelColor = drawingBitmap.getPixel(lowerRightQuarter.x, lowerRightQuarter.y);
		assertEquals("Pixel should be transparent", Color.TRANSPARENT, pixelColor);

		pixelColor = drawingBitmap.getPixel(lowerLeftQuarter.x, lowerLeftQuarter.y);
		assertEquals("Pixel should be transparent", Color.TRANSPARENT, pixelColor);
	}

	@Test
	public void testEraseWithHeartShape() throws NoSuchFieldException, IllegalAccessException {
		PaintroidApplication.perspective.setScale(1.0f);

		selectTool(ToolType.SHAPE);
		Tool tool = PaintroidApplication.currentTool;
		selectShapeTypeAndDraw(R.id.shapes_square_btn, true, BLACK_COLOR_PICKER_BUTTON_POSITION);
		int backgroundColor = tool.getDrawPaint().getColor();

		clickSelectedToolButton();
		selectShapeTypeAndDraw(R.id.shapes_heart_btn, true, TRANSPARENT_COLOR_PICKER_BUTTON_POSITION);


		Bitmap drawingBitmap = (Bitmap) PrivateAccess.getMemberValue(BaseToolWithRectangleShape.class, tool, FIELD_NAME_DRAWING_BITMAP);
		int boxWidth = drawingBitmap.getWidth();
		int boxHeight = drawingBitmap.getHeight();
		PointF toolPosition = (PointF) PrivateAccess.getMemberValue(BaseToolWithShape.class, tool, FIELD_NAME_TOOL_POSITION);

		Point upperLeftPixel = new Point((int)(toolPosition.x - boxWidth/4), (int)(toolPosition.y - boxHeight/4));
		Point upperRightPixel = new Point((int)(toolPosition.x + boxWidth/4), (int)(toolPosition.y - boxHeight/4));
		Point lowerRightPixel = new Point((int)(toolPosition.x + boxWidth/4), (int)(toolPosition.y + boxHeight/4));
		Point lowerLeftPixel = new Point((int)(toolPosition.x - boxWidth/4), (int)(toolPosition.y + boxHeight/4));

		Bitmap bitmap = PaintroidApplication.drawingSurface.getBitmapCopy();

		int pixelColor = bitmap.getPixel(upperLeftPixel.x, upperLeftPixel.y);
		assertEquals("Pixel should have been erased", Color.TRANSPARENT, pixelColor);

		pixelColor = bitmap.getPixel(upperRightPixel.x, upperRightPixel.y);
		assertEquals("Pixel should have been erased", Color.TRANSPARENT, pixelColor);

		pixelColor = bitmap.getPixel(lowerRightPixel.x, lowerRightPixel.y);
		assertEquals("Pixel should not have been erased", backgroundColor, pixelColor);

		pixelColor = bitmap.getPixel(lowerLeftPixel.x, lowerLeftPixel.y);
		assertEquals("Pixel should not have been erased", backgroundColor, pixelColor);
	}


	public void selectShapeTypeAndDraw(int shapeBtnId, boolean changeColor, int colorButtonPosition) throws NoSuchFieldException, IllegalAccessException {
		onView(withId(shapeBtnId)).perform(click());

		Tool tool = PaintroidApplication.currentTool;
		PointF centerPointTool = (PointF) PrivateAccess.getMemberValue(BaseToolWithShape.class, tool, FIELD_NAME_TOOL_POSITION);

		PointF pointUnderTest = new PointF(centerPointTool.x, centerPointTool.y);

		clickSelectedToolButton();

		if (changeColor) {
			selectColorPickerPresetSelectorColor(colorButtonPosition);
		}

		float rectWidth = (Float) PrivateAccess.getMemberValue(BaseToolWithRectangleShape.class, tool, FIELD_NAME_BOX_WIDTH);
		float rectHeight = (Float) PrivateAccess.getMemberValue(BaseToolWithRectangleShape.class, tool, FIELD_NAME_BOX_HEIGHT);
		Bitmap drawingBitmap = (Bitmap) PrivateAccess.getMemberValue(BaseToolWithRectangleShape.class, tool, FIELD_NAME_DRAWING_BITMAP);

		int colorInRectangleTool = tool.getDrawPaint().getColor();
		int colorInRectangle = drawingBitmap.getPixel((int) (rectWidth / 2), (int) (rectHeight / 2));
		if (Color.alpha(colorInRectangleTool) == 0x00) {
			int checkeredWhite = Color.WHITE;
			int checkeredGray = 0xFFC0C0C0;
			assertTrue("Color should correspond to checkered pattern", colorInRectangle == checkeredGray || colorInRectangle == checkeredWhite);
		} else {
			assertEquals("Colors should be equal", colorInRectangleTool, colorInRectangle);
		}

		onView(isRoot()).perform(touchAt(centerPointTool.x - 1, centerPointTool.y - 1));

		int colorPickerColor = PaintroidApplication.currentTool.getDrawPaint().getColor();
		int colorAfterDrawing = PaintroidApplication.drawingSurface.getPixel(pointUnderTest);
		assertEquals("Pixel should have the same color as currently in color picker", colorPickerColor, colorAfterDrawing);
	}

}
