
package com.techjar.ledcm.gui;

import static org.lwjgl.opengl.GL11.*;

import com.google.common.base.Strings;
import com.techjar.ledcm.LEDCubeManager;
import com.techjar.ledcm.render.RenderHelper;
import com.techjar.ledcm.util.MathHelper;
import com.techjar.ledcm.util.Util;

import java.math.RoundingMode;
import java.text.DecimalFormat;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.util.Color;
import org.lwjgl.util.Dimension;
import org.newdawn.slick.UnicodeFont;
import org.newdawn.slick.geom.Rectangle;

/**
 *
 * @author Techjar
 */
public class GUISpinner extends GUI {
	protected UnicodeFont font;
	protected Color color;
	protected GUIBackground guiBg;
	protected GUITextField textField;
	protected float value;
	protected boolean focused;
	protected boolean upHovered;
	protected boolean downHovered;
	protected GUICallback changeHandler;

	protected int buttonWidth = 30;
	protected float minValue = 0;
	protected float maxValue = 100;
	protected int decimalPlaces = 0;
	protected float increment = 1;

	public GUISpinner(UnicodeFont font, Color color, GUIBackground guiBg) {
		this.font = font;
		this.color = color;
		this.guiBg = guiBg;
		guiBg.setParent(this);
		textField = new GUITextField(font, color, guiBg);
		textField.setParent(this);
		textField.setCanLoseFocus(true);
		textField.setText("0");
		textField.setChangeHandler(component -> {
			try {
				value = MathHelper.clamp(Float.parseFloat(formatDecimal(Float.parseFloat(textField.getText()))), minValue, maxValue);
				if (changeHandler != null) {
					changeHandler.run(this);
				}
			} catch (NumberFormatException ex) {
			}
		});
		textField.setValidationFunction(str -> {
			try {
				if (str.isEmpty()) return true;
				if (str.toLowerCase().contains("f") || str.toLowerCase().contains("d")) return false;
				float f = Float.parseFloat(str);
				if (f >= minValue && f <= maxValue) {
					if (decimalPlaces > 0) {
						return str.indexOf('.') == -1 || str.substring(str.indexOf('.') + 1).length() <= decimalPlaces;
					}
					return str.indexOf('.') == -1 && Math.floor(f) == f;
				}
				return false;
			} catch (NumberFormatException ex) {
				return false;
			}
		});
	}

	@Override
	protected boolean keyboardEvent(int key, boolean state, char character) {
		boolean ret = true;
		if (focused) {
			if (state) {
				if (key == Keyboard.KEY_UP) {
					updateValue(value + increment);
					ret = false;
				} else if (key == Keyboard.KEY_DOWN) {
					updateValue(value - increment);
					ret = false;
				}
			}
		}
		if (!textField.keyboardEvent(key, state, character)) ret = false;
		return ret;
	}

	@Override
	protected boolean mouseEvent(int button, boolean state, int dwheel) {
		boolean ret = true;
		if (state && button == 0) {
			Rectangle btnUp = new Rectangle(getPosition().getX() + (dimension.getWidth() - buttonWidth), getPosition().getY(), buttonWidth, (dimension.getHeight() / 2) - 1);
			Rectangle btnDown = new Rectangle(getPosition().getX() + (dimension.getWidth() - buttonWidth), getPosition().getY() + (dimension.getHeight() / 2) + (dimension.getHeight() % 2 == 0 ? 1 : 2), buttonWidth, (dimension.getHeight() / 2) - 1);
			if (checkMouseIntersect(btnUp)) {
				LEDCubeManager.getSoundManager().playEffect("ui/click.wav", false);
				updateValue(value + increment);
				ret = false;
			} else if (checkMouseIntersect(btnDown)) {
				LEDCubeManager.getSoundManager().playEffect("ui/click.wav", false);
				updateValue(value - increment);
				ret = false;
			}
		}
		if (!textField.processMouseEvent()) ret = false;
		return ret;
	}

	@Override
	public void update(float delta) {
		textField.update(delta);
		if (!focused && textField.isFocused()) {
			focused = true;
		} else if (focused && !textField.isFocused()) {
			focused = false;
			setTextField(value);
		}
		Rectangle btnUp = new Rectangle(getPosition().getX() + (dimension.getWidth() - buttonWidth), getPosition().getY(), buttonWidth, (dimension.getHeight() / 2) - 1);
		Rectangle btnDown = new Rectangle(getPosition().getX() + (dimension.getWidth() - buttonWidth), getPosition().getY() + (dimension.getHeight() / 2) + (dimension.getHeight() % 2 == 0 ? 1 : 2), buttonWidth, (dimension.getHeight() / 2) - 1);
		if (checkMouseIntersect(btnUp)) {
			if (!upHovered) LEDCubeManager.getSoundManager().playEffect("ui/rollover.wav", false);
			upHovered = true;
		} else upHovered = false;
		if (checkMouseIntersect(btnDown)) {
			if (!downHovered) LEDCubeManager.getSoundManager().playEffect("ui/rollover.wav", false);
			downHovered = true;
		} else downHovered = false;
	}

	@Override
	public void render() {
		textField.render();
		RenderHelper.drawSquare(getPosition().getX() + (dimension.getWidth() - buttonWidth), getPosition().getY(), buttonWidth, (dimension.getHeight() / 2) - 1, upHovered ? Util.addColors(guiBg.getBorderColor(), new Color(50, 50, 50)) : guiBg.getBorderColor());
		RenderHelper.drawSquare(getPosition().getX() + (dimension.getWidth() - buttonWidth), getPosition().getY() + (dimension.getHeight() / 2) + (dimension.getHeight() % 2 == 0 ? 1 : 2), buttonWidth, (dimension.getHeight() / 2) - 1, downHovered ? Util.addColors(guiBg.getBorderColor(), new Color(50, 50, 50)) : guiBg.getBorderColor());
		glDisable(GL_TEXTURE_2D);
		RenderHelper.setGlColor(upHovered ? Util.addColors(guiBg.getBackgroundColor(), new Color(50, 50, 50)) : guiBg.getBackgroundColor());
		glBegin(GL_TRIANGLES);
		glVertex2f(getPosition().getX() + ((dimension.getWidth() - buttonWidth) + 1), getPosition().getY() + (dimension.getHeight() / 2) - 2);
		glVertex2f(getPosition().getX() + (dimension.getWidth() - 1), getPosition().getY() + (dimension.getHeight() / 2) - 2);
		glVertex2f(getPosition().getX() + (dimension.getWidth() - (buttonWidth / 2)), getPosition().getY() + 1);
		glEnd();
		RenderHelper.setGlColor(downHovered ? Util.addColors(guiBg.getBackgroundColor(), new Color(50, 50, 50)) : guiBg.getBackgroundColor());
		glBegin(GL_TRIANGLES);
		float yOffset = (dimension.getHeight() / 2) + (dimension.getHeight() % 2 == 0 ? 1 : 2);
		glVertex2f(getPosition().getX() + ((dimension.getWidth() - buttonWidth) + 1), getPosition().getY() + yOffset + 1);
		glVertex2f(getPosition().getX() + (dimension.getWidth() - (buttonWidth / 2)), getPosition().getY() + dimension.getHeight() - 1);
		glVertex2f(getPosition().getX() + (dimension.getWidth() - 1), getPosition().getY() + yOffset + 1);
		glEnd();
		glEnable(GL_TEXTURE_2D);
	}

	@Override
	public void setDimension(Dimension dimension) {
		super.setDimension(dimension);
		textField.setDimension(dimension.getWidth() - buttonWidth - 2, dimension.getHeight());
	}

	protected void updateValue(float value) {
		this.value = MathHelper.clamp(Float.parseFloat(formatDecimal(value)), minValue, maxValue);
		setTextField(this.value);
	}

	protected void setTextField(float value) {
		GUICallback cb = textField.getChangeHandler();
		textField.setChangeHandler(null);
		textField.setText(formatDecimal(value));
		textField.setChangeHandler(cb);
		if (changeHandler != null) {
			changeHandler.run(this);
		}
	}

	protected String formatDecimal(float value) {
		DecimalFormat df;
		if (decimalPlaces > 0) df = new DecimalFormat("#." + Strings.repeat("#", decimalPlaces));
		else df = new DecimalFormat("#");
		df.setRoundingMode(RoundingMode.HALF_UP);
		return df.format(value);
	}

	public float getValue() {
		return value;
	}

	public void setValue(float value) {
		updateValue(value);
	}

	public GUICallback getChangeHandler() {
		return changeHandler;
	}

	public void setChangeHandler(GUICallback changeHandler) {
		this.changeHandler = changeHandler;
	}

	public int getButtonWidth() {
		return buttonWidth;
	}

	public void setButtonWidth(int buttonWidth) {
		this.buttonWidth = buttonWidth;
	}

	public float getMinValue() {
		return minValue;
	}

	public void setMinValue(float minValue) {
		this.minValue = minValue;
		updateValue(value);
	}

	public float getMaxValue() {
		return maxValue;
	}

	public void setMaxValue(float maxValue) {
		this.maxValue = maxValue;
		updateValue(value);
	}

	public int getDecimalPlaces() {
		return decimalPlaces;
	}

	public void setDecimalPlaces(int decimalPlaces) {
		this.decimalPlaces = decimalPlaces;
		updateValue(value);
	}

	public float getIncrement() {
		return increment;
	}

	public void setIncrement(float increment) {
		this.increment = increment;
	}
}
