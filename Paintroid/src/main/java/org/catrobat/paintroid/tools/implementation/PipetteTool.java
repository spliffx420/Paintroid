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

package org.catrobat.paintroid.tools.implementation;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.PointF;

import org.catrobat.paintroid.dialog.colorpicker.ColorPickerDialog;
import org.catrobat.paintroid.listener.LayerListener;
import org.catrobat.paintroid.tools.ToolType;

public class PipetteTool extends BaseTool {

	private Bitmap mSurfaceBitmap;

	public PipetteTool(Context context, ToolType toolType) {
		super(context, toolType);

		updateSurfaceBitmap();
	}

	@Override
	public void draw(Canvas canvas) {
	}

	@Override
	public boolean handleDown(PointF coordinate) {
		return setColor(coordinate);
	}

	@Override
	public boolean handleMove(PointF coordinate) {
		return setColor(coordinate);
	}

	@Override
	public boolean handleUp(PointF coordinate) {
		return setColor(coordinate);
	}

	protected boolean setColor(PointF coordinate) {
		if (coordinate == null) {
			return false;
		}

		if (coordinate.x < 0 || coordinate.y < 0 ||
				coordinate.x >= mSurfaceBitmap.getWidth() || coordinate.y >= mSurfaceBitmap.getHeight()) {
			return false;
		}

		int color = mSurfaceBitmap.getPixel((int) coordinate.x, (int) coordinate.y);

		ColorPickerDialog.getInstance().setInitialColor(color);
		changePaintColor(color);
		return true;
	}

	public void updateSurfaceBitmap() {
		mSurfaceBitmap = LayerListener.getInstance().getBitmapOfAllLayersToSave();
	}

	@Override
	public void resetInternalState() {
	}

	@Override
	public void setupToolOptions() {
	}

}
