package com.techjar.ledcm.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.hackoeur.jglm.Mat3;
import com.hackoeur.jglm.Mat4;
import com.techjar.ledcm.LEDCubeManager;
import com.techjar.ledcm.Main;
import com.techjar.ledcm.gui.GUI;
import com.techjar.ledcm.gui.GUIBox;
import com.techjar.ledcm.gui.GUICheckBox;
import com.techjar.ledcm.gui.GUIColorPicker;
import com.techjar.ledcm.gui.GUIComboBox;
import com.techjar.ledcm.gui.GUIComboButton;
import com.techjar.ledcm.gui.GUIRadioButton;
import com.techjar.ledcm.gui.GUISlider;
import com.techjar.ledcm.gui.GUISpinner;
import com.techjar.ledcm.gui.GUITextField;
import com.techjar.ledcm.gui.screen.ScreenMainControl;
import com.techjar.ledcm.hardware.animation.AnimationOption;
import com.techjar.ledcm.util.json.ShapeInfo;

import java.awt.event.KeyEvent;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.InflaterOutputStream;

import com.techjar.ledcm.util.math.Dimension3D;
import com.techjar.ledcm.util.math.PooledMutableVector3;
import com.techjar.ledcm.util.math.Quaternion;
import com.techjar.ledcm.util.math.Vector2;
import com.techjar.ledcm.util.math.Vector3;
import lombok.SneakyThrows;

import org.lwjgl.input.Controller;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.util.Color;
import org.lwjgl.util.ReadableColor;
import org.lwjgl.util.vector.Matrix3f;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;
import org.newdawn.slick.geom.Circle;
import org.newdawn.slick.geom.Ellipse;
import org.newdawn.slick.geom.Polygon;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.geom.RoundedRectangle;
import org.newdawn.slick.geom.Shape;
import org.newdawn.slick.geom.Vector2f;

/**
 *
 * @author Techjar
 */
public final class Util {
	private static final Map<String, ShapeInfo> shapeCache = new HashMap<>();
	public static final Gson GSON = new GsonBuilder().create();

	private Util() {
	}

	public static void addLibraryPath(String path) throws IOException {
		try {
			// This enables the java.library.path to be modified at runtime
			// From a Sun engineer at http://forums.sun.com/thread.jspa?threadID=707176
			//
			Field field = ClassLoader.class.getDeclaredField("usr_paths");
			field.setAccessible(true);
			String[] paths = (String[])field.get(null);
			for (int i = 0; i < paths.length; i++) {
				if (path.equals(paths[i])) {
					return;
				}
			}
			String[] tmp = new String[paths.length + 1];
			System.arraycopy(paths, 0, tmp, 0, paths.length);
			tmp[paths.length] = path;
			field.set(null, tmp);
			System.setProperty("java.library.path", System.getProperty("java.library.path") + File.pathSeparator + path);
		} catch (IllegalAccessException e) {
			throw new IOException("Failed to get permissions to set library path", e);
		} catch (NoSuchFieldException e) {
			throw new IOException("Failed to get field handle to set library path", e);
		}
	}

	public static boolean isPrintableCharacter(char ch) {
		Character.UnicodeBlock block = Character.UnicodeBlock.of(ch);
		return !Character.isISOControl(ch) && ch != KeyEvent.CHAR_UNDEFINED && block != null && block != Character.UnicodeBlock.SPECIALS;
	}

	public static String stackTraceToString(Throwable throwable) {
		StringWriter stackTrace = new StringWriter();
		throwable.printStackTrace(new PrintWriter(stackTrace));
		return stackTrace.toString();
	}

	public static org.newdawn.slick.Color convertColor(org.lwjgl.util.Color color) {
		return new org.newdawn.slick.Color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha());
	}

	public static org.lwjgl.util.Color convertColor(org.newdawn.slick.Color color) {
		return new org.lwjgl.util.Color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha());
	}

	public static Vector2f convertVector(Vector2 vector) {
		return new Vector2f(vector.getX(), vector.getY());
	}

	public static Vector2 convertVector(Vector2f vector) {
		return new Vector2(vector.getX(), vector.getY());
	}

	public static Vector3f convertVector(Vector3 vector) {
		return new Vector3f(vector.getX(), vector.getY(), vector.getZ());
	}

	public static Vector3 convertVector(Vector3f vector) {
		return new Vector3(vector.getX(), vector.getY(), vector.getZ());
	}

	public static PooledMutableVector3 convertVectorPooled(Vector3f vector) {
		return PooledMutableVector3.get(vector.getX(), vector.getY(), vector.getZ());
	}

	public static org.lwjgl.util.vector.Quaternion convertQuaternion(Quaternion quat) {
		return new org.lwjgl.util.vector.Quaternion(quat.getX(), quat.getY(), quat.getZ(), quat.getW());
	}

	public static Matrix3f convertMatrix(Mat3 matrix) {
		Matrix3f out = new Matrix3f();
		out.m00 = matrix.m00; out.m01 = matrix.m01; out.m02 = matrix.m02;
		out.m10 = matrix.m10; out.m11 = matrix.m11; out.m12 = matrix.m12;
		out.m20 = matrix.m20; out.m21 = matrix.m21; out.m22 = matrix.m22;
		return out;
	}

	public static Matrix4f convertMatrix(Mat4 matrix) {
		Matrix4f out = new Matrix4f();
		out.m00 = matrix.m00; out.m01 = matrix.m01; out.m02 = matrix.m02; out.m03 = matrix.m03;
		out.m10 = matrix.m10; out.m11 = matrix.m11; out.m12 = matrix.m12; out.m13 = matrix.m13;
		out.m20 = matrix.m20; out.m21 = matrix.m21; out.m22 = matrix.m22; out.m23 = matrix.m23;
		out.m30 = matrix.m30; out.m31 = matrix.m31; out.m32 = matrix.m32; out.m33 = matrix.m33;
		return out;
	}

	public static float[] matrixToArray(Matrix3f matrix) {
		return new float[] {
				matrix.m00, matrix.m01, matrix.m02,
				matrix.m10, matrix.m11, matrix.m12,
				matrix.m20, matrix.m21, matrix.m22
		};
	}

	public static float[] matrixToArray(Matrix4f matrix) {
		return new float[] {
				matrix.m00, matrix.m01, matrix.m02, matrix.m03,
				matrix.m10, matrix.m11, matrix.m12, matrix.m13,
				matrix.m20, matrix.m21, matrix.m22, matrix.m23,
				matrix.m30, matrix.m31, matrix.m32, matrix.m33
		};
	}

	public static float[] matrixToArray(Mat3 matrix) {
		return new float[] {
				matrix.m00, matrix.m01, matrix.m02,
				matrix.m10, matrix.m11, matrix.m12,
				matrix.m20, matrix.m21, matrix.m22
		};
	}

	public static float[] matrixToArray(Mat4 matrix) {
		return new float[] {
				matrix.m00, matrix.m01, matrix.m02, matrix.m03,
				matrix.m10, matrix.m11, matrix.m12, matrix.m13,
				matrix.m20, matrix.m21, matrix.m22, matrix.m23,
				matrix.m30, matrix.m31, matrix.m32, matrix.m33
		};
	}

	public static void storeColorInBuffer(org.lwjgl.util.Color color, ByteBuffer buffer) {
		buffer.putFloat(color.getRed() / 255F);
		buffer.putFloat(color.getGreen() / 255F);
		buffer.putFloat(color.getBlue() / 255F);
		buffer.putFloat(color.getAlpha() / 255F);
	}

	public static void storeColorInBuffer(org.lwjgl.util.Color color, FloatBuffer buffer) {
		buffer.put(color.getRed() / 255F);
		buffer.put(color.getGreen() / 255F);
		buffer.put(color.getBlue() / 255F);
		buffer.put(color.getAlpha() / 255F);
	}

	public static void storeMatrixInBuffer(Matrix3f matrix, ByteBuffer buffer) {
		buffer.putFloat(matrix.m00); buffer.putFloat(matrix.m01); buffer.putFloat(matrix.m02);
		buffer.putFloat(matrix.m10); buffer.putFloat(matrix.m11); buffer.putFloat(matrix.m12);
		buffer.putFloat(matrix.m20); buffer.putFloat(matrix.m21); buffer.putFloat(matrix.m22);
	}

	public static void storeMatrixInBuffer(Matrix4f matrix, ByteBuffer buffer) {
		buffer.putFloat(matrix.m00); buffer.putFloat(matrix.m01); buffer.putFloat(matrix.m02); buffer.putFloat(matrix.m03);
		buffer.putFloat(matrix.m10); buffer.putFloat(matrix.m11); buffer.putFloat(matrix.m12); buffer.putFloat(matrix.m13);
		buffer.putFloat(matrix.m20); buffer.putFloat(matrix.m21); buffer.putFloat(matrix.m22); buffer.putFloat(matrix.m23);
		buffer.putFloat(matrix.m30); buffer.putFloat(matrix.m31); buffer.putFloat(matrix.m32); buffer.putFloat(matrix.m33);
	}

	public static void storeMatrixInBuffer(Mat3 matrix, ByteBuffer buffer) {
		buffer.putFloat(matrix.m00); buffer.putFloat(matrix.m01); buffer.putFloat(matrix.m02);
		buffer.putFloat(matrix.m10); buffer.putFloat(matrix.m11); buffer.putFloat(matrix.m12);
		buffer.putFloat(matrix.m20); buffer.putFloat(matrix.m21); buffer.putFloat(matrix.m22);
	}

	public static void storeMatrixInBuffer(Mat4 matrix, ByteBuffer buffer) {
		buffer.putFloat(matrix.m00); buffer.putFloat(matrix.m01); buffer.putFloat(matrix.m02); buffer.putFloat(matrix.m03);
		buffer.putFloat(matrix.m10); buffer.putFloat(matrix.m11); buffer.putFloat(matrix.m12); buffer.putFloat(matrix.m13);
		buffer.putFloat(matrix.m20); buffer.putFloat(matrix.m21); buffer.putFloat(matrix.m22); buffer.putFloat(matrix.m23);
		buffer.putFloat(matrix.m30); buffer.putFloat(matrix.m31); buffer.putFloat(matrix.m32); buffer.putFloat(matrix.m33);
	}

	public static void storeMatrixInBuffer(Mat3 matrix, FloatBuffer buffer) {
		buffer.put(matrix.m00); buffer.put(matrix.m01); buffer.put(matrix.m02);
		buffer.put(matrix.m10); buffer.put(matrix.m11); buffer.put(matrix.m12);
		buffer.put(matrix.m20); buffer.put(matrix.m21); buffer.put(matrix.m22);
	}

	public static void storeMatrixInBuffer(Mat4 matrix, FloatBuffer buffer) {
		buffer.put(matrix.m00); buffer.put(matrix.m01); buffer.put(matrix.m02); buffer.put(matrix.m03);
		buffer.put(matrix.m10); buffer.put(matrix.m11); buffer.put(matrix.m12); buffer.put(matrix.m13);
		buffer.put(matrix.m20); buffer.put(matrix.m21); buffer.put(matrix.m22); buffer.put(matrix.m23);
		buffer.put(matrix.m30); buffer.put(matrix.m31); buffer.put(matrix.m32); buffer.put(matrix.m33);
	}

	public static org.lwjgl.util.Color addColors(org.lwjgl.util.Color color1, org.lwjgl.util.Color color2) {
		return new org.lwjgl.util.Color(MathHelper.clamp(color1.getRed() + color2.getRed(), 0, 255), MathHelper.clamp(color1.getGreen() + color2.getGreen(), 0, 255), MathHelper.clamp(color1.getBlue() + color2.getBlue(), 0, 255));
	}

	public static org.lwjgl.util.Color subtractColors(org.lwjgl.util.Color color1, org.lwjgl.util.Color color2) {
		return new org.lwjgl.util.Color(MathHelper.clamp(color1.getRed() - color2.getRed(), 0, 255), MathHelper.clamp(color1.getGreen() - color2.getGreen(), 0, 255), MathHelper.clamp(color1.getBlue() - color2.getBlue(), 0, 255));
	}

	public static org.lwjgl.util.Color multiplyColors(org.lwjgl.util.Color color1, org.lwjgl.util.Color color2) {
		float[] floats1 = colorToFloats(color1);
		float[] floats2 = colorToFloats(color2);
		return floatsToColor(new float[]{floats1[0] * floats2[0], floats1[1] * floats2[1], floats1[2] * floats2[2], floats1[3] * floats2[3]});
	}

	public static org.lwjgl.util.Color divideColors(org.lwjgl.util.Color color1, org.lwjgl.util.Color color2) {
		float[] floats1 = colorToFloats(color1);
		float[] floats2 = colorToFloats(color2);
		return floatsToColor(new float[]{floats1[0] / floats2[0], floats1[1] / floats2[1], floats1[2] / floats2[2], floats1[3] / floats2[3]});
	}

	public static org.lwjgl.util.Color multiplyColor(org.lwjgl.util.Color color1, double mult) {
		return new org.lwjgl.util.Color(MathHelper.clamp((int)Math.round(color1.getRed() * mult), 0, 255), MathHelper.clamp((int)Math.round(color1.getGreen() * mult), 0, 255), MathHelper.clamp((int)Math.round(color1.getBlue() * mult), 0, 255));
	}

	public static float[] colorToFloats(org.lwjgl.util.Color color) {
		return new float[]{color.getRed() / 255F, color.getGreen() / 255F, color.getBlue() / 255F, color.getAlpha() / 255F};
	}

	public static org.lwjgl.util.Color floatsToColor(float[] floats) {
		if (floats.length < 4) {
			return new Color(Math.round(floats[0] * 255), Math.round(floats[1] * 255), Math.round(floats[2] * 255));
		} else {
			return new Color(Math.round(floats[0] * 255), Math.round(floats[1] * 255), Math.round(floats[2] * 255), Math.round(floats[3] * 255));
		}
	}

	public static Vector3 transformVector(Vector3 vector, Matrix4f matrix, boolean round) {
		Vector4f vec = Matrix4f.transform(matrix, new Vector4f(vector.getX(), vector.getY(), vector.getZ(), 1), null);
		return round ? new Vector3(Math.round(vec.x), Math.round(vec.y), Math.round(vec.z)) : new Vector3(vec.x, vec.y, vec.z);
	}

	/**
	 * @deprecated use {@link com.techjar.ledcm.hardware.manager.LEDManager#encodeVector(Vector3)} instead
	 */
	@Deprecated
	public static int encodeCubeVector(Vector3 vector) {
		return LEDCubeManager.getLEDManager().encodeVector(vector);
	}

	/**
	 * @deprecated use {@link com.techjar.ledcm.hardware.manager.LEDManager#encodeVector(int, int, int)} instead
	 */
	@Deprecated
	public static int encodeCubeVector(int x, int y, int z) {
		return LEDCubeManager.getLEDManager().encodeVector(x, y, z);
	}

	/**
	 * @deprecated use {@link com.techjar.ledcm.hardware.manager.LEDManager#decodeVector(int)} instead
	 */
	@Deprecated
	public static Vector3 decodeCubeVector(int number) {
		return LEDCubeManager.getLEDManager().decodeVector(number);
	}

	public static boolean isInsideCube(int x, int y, int z) {
		Dimension3D dim = LEDCubeManager.getLEDCube().getLEDManager().getDimensions();
		return x >= 0 && x < dim.x && y >= 0 && y < dim.y && z >= 0 && z < dim.z;
	}

	public static boolean isInsideCube(Vector3 vector) {
		return isInsideCube((int)vector.getX(), (int)vector.getY(), (int)vector.getZ());
	}

	/**
	 * Sets an animation option using the GUI component rather than directly.
	 *
	 * @param option The ID of the option
	 * @param value The value to set (internal or display value)
	 */
	public static void setOptionInGUI(AnimationOption option, String value) {
		if (LEDCubeManager.getLEDCube().getCommThread().getCurrentAnimation() == null) return;
		if (option.getType() == AnimationOption.OptionType.COMBOBOX || option.getType() == AnimationOption.OptionType.COMBOBUTTON || option.getType() == AnimationOption.OptionType.RADIOGROUP) {
			for (int i = 1; i < option.getParams().length; i += 2) {
				if (option.getParams()[i].toString().equals(value)) {
					value = option.getParams()[i + 1].toString();
					break;
				}
			}
		}
		ScreenMainControl screen = LEDCubeManager.getInstance().getScreenMainControl();
		List<GUI> components = screen.animOptionsScrollBox.findComponentsByName(option.getId());
		if (components.size() > 0) {
			GUI component = components.get(0);
			if (component instanceof GUITextField) {
				((GUITextField)component).setText(value);
			} else if (component instanceof GUISlider) {
				((GUISlider)component).setValue(Float.parseFloat(value));
			} else if (component instanceof GUIComboBox) {
				((GUIComboBox)component).setSelectedItem(value);
			} else if (component instanceof GUIComboButton) {
				((GUIComboButton)component).setSelectedItem(value);
			} else if (component instanceof GUIBox) {
				GUIBox box = (GUIBox)component;
				for (GUI gui : box.getAllComponents()) {
					if (gui instanceof GUIRadioButton) {
						GUIRadioButton radioButton = (GUIRadioButton)gui;
						if (radioButton.getLabel().getText().equals(value)) {
							radioButton.setSelected(true);
						}
					}
				}
			} else if (component instanceof GUICheckBox) {
				((GUICheckBox)component).setChecked(Boolean.parseBoolean(value));
			} else if (component instanceof GUISpinner) {
				((GUISpinner)component).setValue(Float.parseFloat(value));
			} else if (component instanceof GUIColorPicker) {
				((GUIColorPicker)component).setValue(Util.stringToColor(value));
			} else {
				LEDCubeManager.getLEDCube().getCommThread().getCurrentAnimation().setOption(option.getId(), value);
			}
		}
	}

	/*public static int getRequiredBits(long value) {
        int i = 0;
        for (; i < 64; i++) {
            if (value == 0) break;
            value >>= 1;
        }
        return i;
    }

    public static Dimension3D getRequiredBits(Dimension3D dimension) {
        return new Dimension3D(getRequiredBits(dimension.x - 1), getRequiredBits(dimension.y - 1), getRequiredBits(dimension.z - 1));
    }*/

	public static float getAxisValue(Controller con, String name) {
		if (name == null) return 0;
		for (int i = 0; i < con.getAxisCount(); i++) {
			if (name.equals(con.getAxisName(i))) return con.getAxisValue(i);
		}
		return 0;
	}

	public static <T> List<T> arrayAsListCopy(T... array) {
		List<T> list = new ArrayList<>();
		list.addAll(Arrays.asList(array));
		return list;
	}

	public static void shuffleArray(Object[] array, Random random) {
		for (int i = array.length - 1; i > 0; i--) {
			int index = random.nextInt(i + 1);
			Object obj = array[index];
			array[index] = array[i];
			array[i] = obj;
		}
	}

	public static void shuffleArray(int[] array, Random random) {
		for (int i = array.length - 1; i > 0; i--) {
			int index = random.nextInt(i + 1);
			int obj = array[index];
			array[index] = array[i];
			array[i] = obj;
		}
	}

	public static void shuffleArray(Object[] array) {
		shuffleArray(array, new Random());
	}

	public static int getMouseX() {
		//return (int)(Mouse.getX() / ((double)canvas.getWidth() / (double)displayMode.getWidth()));
		if (LEDCubeManager.getInstance().getMouseOverride() != null)
			return (int)LEDCubeManager.getInstance().getMouseOverride().getX();
		return Mouse.getX();
	}

	public static int getMouseY() {
		//return (int)((canvas.getHeight() - Mouse.getY()) / ((double)canvas.getHeight() / (double)displayMode.getHeight()));
		if (LEDCubeManager.getInstance().getMouseOverride() != null)
			return (int)LEDCubeManager.getInstance().getMouseOverride().getY();
		return LEDCubeManager.getHeight() - Mouse.getY() - 1;
	}

	public static Vector2 getMousePos() {
		return new Vector2(getMouseX(), getMouseY());
	}

	public static Vector2 getMouseCenterOffset() {
		return new Vector2(getMouseX() - LEDCubeManager.getWidth() / 2, getMouseY() - LEDCubeManager.getHeight() / 2 + 1);
	}

	public static Shape getMouseHitbox() {
		if (LEDCubeManager.getInstance().getMouseOverride() != null && Main.isVrMode())
			return new Rectangle(getMouseX() - 5, getMouseY() - 5, 10, 10);
		return new Rectangle(getMouseX(), getMouseY(), 1, 1);
	}

	public static String[] parseArgumentString(String argStr) {
		boolean quote = false;
		List<String> params = new ArrayList<>();
		List<String> temp = new ArrayList<>();
		String[] split = argStr.split(" ");
		for (int i = 0; i < split.length; i++) {
			String str = split[i];
			char first = str.isEmpty() ? '\u0000' : str.charAt(0);
			char last = str.isEmpty() ? '\u0000' : str.charAt(str.length() - 1);
			char beforeLast = str.length() < 2 ? '\u0000' : str.charAt(str.length() - 2);
			if (first == '"' && last == '"' && beforeLast != '\\') {
				if (quote) throw new IllegalArgumentException("Erroneous quote in word " + i + ": " + str);
				params.add(str.replaceFirst("^\"(.*)\"$", "$1"));
			} else if (first == '"') {
				if (quote) throw new IllegalArgumentException("Erroneous quote in word " + i + ": " + str);
				quote = true;
				temp.add(str.replaceFirst("^\"", ""));
			} else if (last == '"' && beforeLast != '\\') {
				if (!quote) throw new IllegalArgumentException("Erroneous quote in word " + i + ": " + str);
				quote = false;
				temp.add(str.replaceFirst("\"$", ""));
				StringBuilder sb = new StringBuilder();
				for (int j = 0; j < temp.size(); j++) {
					sb.append(temp.get(j));
					if (j < temp.size() - 1) sb.append(' ');
				}
				params.add(sb.toString());
				temp.clear();
			} else if (quote) {
				temp.add(str);
			} else {
				params.add(str);
			}
		}
		if (quote) throw new IllegalArgumentException("Unclosed quotes in string: " + argStr);
		List<String> params2 = new ArrayList<>();
		for (String param : params) {
			if (!param.isEmpty()) params2.add(param.replaceAll("\\\\\"", "\""));
		}
		return params2.toArray(new String[params2.size()]);
	}

	/**
	 * Will parse a valid IPv4/IPv6 address and port, may return garbage for invalid address formats. If no port was parsed it will be -1.
	 */
	public static IPInfo parseIPAddress(String str) throws UnknownHostException {
		String ip;
		int port = -1;
		boolean ipv6 = false;
		if (str.indexOf(':') != -1) {
			if (str.indexOf('[') != -1 && str.indexOf(']') != -1) {
				ip = str.substring(1, str.indexOf(']'));
				port = Integer.parseInt(str.substring(str.indexOf(']') + 2));
				ipv6 = true;
			} else if (str.indexOf(':') == str.lastIndexOf(':')) {
				ip = str.substring(0, str.indexOf(':'));
				port = Integer.parseInt(str.substring(str.indexOf(':') + 1));
			} else ip = str;
		} else ip = str;
		return new IPInfo(InetAddress.getByName(ip), port, ipv6);
	}

	public static String getChecksum(String method, byte[] bytes) throws NoSuchAlgorithmException {
		MessageDigest md = MessageDigest.getInstance(method);
		md.update(bytes);
		byte[] digest = md.digest();
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < digest.length; i++) {
			sb.append(String.format("%02x", digest[i] & 0xFF));
		}
		return sb.toString();
	}

	public static String getChecksum(String method, String str) throws NoSuchAlgorithmException, UnsupportedEncodingException {
		return getChecksum(method, str.getBytes("UTF-8"));
	}

	public static String getFileChecksum(String method, File file) throws NoSuchAlgorithmException, IOException {
		try (FileInputStream fis = new FileInputStream(file)) {
			byte[] bytes = new byte[(int)file.length()];
			fis.read(bytes);
			return getChecksum(method, bytes);
		}
	}

	@SneakyThrows(FileNotFoundException.class)
	public static Shape loadShape(String file) {
		ShapeInfo info = shapeCache.get(file);
		if (info == null) {
			info = Util.GSON.fromJson(new FileReader(new File("resources/shapes/" + file + ".shape")), ShapeInfo.class);
			shapeCache.put(file, info);
		}

		switch (info.type.toLowerCase()) {
			case "circle":
				return new Circle(0, 0, info.radius);
			case "ellipse":
				return new Ellipse(0, 0, info.radius1, info.radius2);
			case "polygon":
				if (info.points.length % 2 != 0) throw new IllegalArgumentException("Invalid point array, must have even number of elements");
				float[] points = new float[info.points.length];
				for (int i = 0; i < points.length; i += 2) {
					points[i] = info.points[i] + info.pointOffsetX;
					points[i + 1] = info.points[i + 1] + info.pointOffsetY;
				}
				Vector2 pos = findMinimumPoint(points);
				Polygon poly = new Polygon(points);
				poly.setX(pos.getX());
				poly.setY(pos.getY());
				return poly;
			case "rectangle":
				Rectangle rect = new Rectangle(0, 0, info.width, info.height);
				rect.setCenterX(0);
				rect.setCenterY(0);
				return rect;
			case "roundedrectangle":
				if (info.cornerFlags != null) {
					int flags = 0;
					for (String flag : info.cornerFlags) {
						switch (flag.toUpperCase()) {
							case "TOP_LEFT":
								flags |= RoundedRectangle.TOP_LEFT;
								break;
							case "TOP_RIGHT":
								flags |= RoundedRectangle.TOP_RIGHT;
								break;
							case "BOTTOM_LEFT":
								flags |= RoundedRectangle.BOTTOM_LEFT;
								break;
							case "BOTTOM_RIGHT":
								flags |= RoundedRectangle.BOTTOM_RIGHT;
								break;
							case "ALL":
								flags = RoundedRectangle.ALL;
								break;
							default:
								throw new IllegalArgumentException("Invalid corner flag: " + flag);
						}
					}
					rect = new RoundedRectangle(0, 0, info.width, info.height, info.cornerRadius, 25, flags);
				} else {
					rect = new RoundedRectangle(0, 0, info.width, info.height, info.cornerRadius);
				}
				rect.setCenterX(0);
				rect.setCenterY(0);
				return rect;
			default:
				throw new IllegalArgumentException("Invalid shape type: " + info.type);
		}
	}

	private static Vector2 findMinimumPoint(float[] points) {
		if (points.length == 0) return new Vector2();
		float minX = points[0];
		float minY = points[1];
		for (int i = 0; i < points.length; i += 2) {
			minX = Math.min(points[i], minX);
			minY = Math.min(points[i + 1], minY);
		}
		return new Vector2(minX, minY);
	}

	/**
	 * Compresses the byte array using deflate algorithm.
	 */
	public static byte[] compresssBytes(byte[] bytes) throws IOException {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		try (DeflaterOutputStream dos = new DeflaterOutputStream(out)) {
			dos.write(bytes);
		}
		return out.toByteArray();
	}

	/**
	 * Decompresses the byte array using deflate algorithm.
	 */
	public static byte[] decompresssBytes(byte[] bytes) throws IOException {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		try (InflaterOutputStream dos = new InflaterOutputStream(out)) {
			dos.write(bytes);
		}
		return out.toByteArray();
	}

	public static float[] floatListToArray(List<Float> list) {
		float[] array = new float[list.size()];
		for (int i = 0; i < array.length; i++) {
			array[i] = list.get(i);
		}
		return array;
	}

	public static short floatToShortBits(float fval) {
		int fbits = Float.floatToIntBits(fval);
		int sign = fbits >>> 16 & 0x8000;
		int val = (fbits & 0x7fffffff) + 0x1000;

		if(val >= 0x47800000) {
			if((fbits & 0x7fffffff) >= 0x47800000) {
				if(val < 0x7f800000) return (short)(sign | 0x7c00);
				return (short)(sign | 0x7c00 | (fbits & 0x007fffff) >>> 13);
			}
			return (short)(sign | 0x7bff);
		}
		if(val >= 0x38800000) return (short)(sign | val - 0x38000000 >>> 13);
		if(val < 0x33000000) return (short)(sign);
		val = (fbits & 0x7fffffff) >>> 23;
		return (short)(sign | ((fbits & 0x7fffff | 0x800000) + (0x800000 >>> val - 102) >>> 126 - val));
	}

	public static float shortBitsToFloat(short hbits) {
		int mant = hbits & 0x03ff;
		int exp =  hbits & 0x7c00;
		if(exp == 0x7c00) exp = 0x3fc00;
		else if(exp != 0) {
			exp += 0x1c000;
			if(mant == 0 && exp > 0x1c400) return Float.intBitsToFloat((hbits & 0x8000) << 16 | exp << 13 | 0x3ff);
		}
		else if(mant != 0) {
			exp = 0x1c400;
			do {
				mant <<= 1;
				exp -= 0x400;
			} while((mant & 0x400) == 0);
			mant &= 0x3ff;
		}
		return Float.intBitsToFloat((hbits & 0x8000) << 16 | (exp | mant) << 13);
	}

	public static byte[] readFully(InputStream in) throws IOException {
		try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
			byte[] bytes = new byte[4096]; int count;
			while ((count = in.read(bytes, 0, bytes.length)) != -1) {
				out.write(bytes, 0, count);
			}
			return out.toByteArray();
		}
	}

	public static String readFile(File file) throws FileNotFoundException, IOException {
		try (FileInputStream in = new FileInputStream(file)) {
			byte[] bytes = readFully(in);
			return new String(bytes, "UTF-8");
		}
	}

	public static int characterToLWJGLKeyCode(char ch) {
		switch (ch) {
			case 'a':
			case 'A':
				return Keyboard.KEY_A;
			case 'b':
			case 'B':
				return Keyboard.KEY_B;
			case 'c':
			case 'C':
				return Keyboard.KEY_C;
			case 'd':
			case 'D':
				return Keyboard.KEY_D;
			case 'e':
			case 'E':
				return Keyboard.KEY_E;
			case 'f':
			case 'F':
				return Keyboard.KEY_F;
			case 'g':
			case 'G':
				return Keyboard.KEY_G;
			case 'h':
			case 'H':
				return Keyboard.KEY_H;
			case 'i':
			case 'I':
				return Keyboard.KEY_I;
			case 'j':
			case 'J':
				return Keyboard.KEY_J;
			case 'k':
			case 'K':
				return Keyboard.KEY_K;
			case 'l':
			case 'L':
				return Keyboard.KEY_L;
			case 'm':
			case 'M':
				return Keyboard.KEY_M;
			case 'n':
			case 'N':
				return Keyboard.KEY_N;
			case 'o':
			case 'O':
				return Keyboard.KEY_O;
			case 'p':
			case 'P':
				return Keyboard.KEY_P;
			case 'q':
			case 'Q':
				return Keyboard.KEY_Q;
			case 'r':
			case 'R':
				return Keyboard.KEY_R;
			case 's':
			case 'S':
				return Keyboard.KEY_S;
			case 't':
			case 'T':
				return Keyboard.KEY_T;
			case 'u':
			case 'U':
				return Keyboard.KEY_U;
			case 'v':
			case 'V':
				return Keyboard.KEY_V;
			case 'w':
			case 'W':
				return Keyboard.KEY_W;
			case 'x':
			case 'X':
				return Keyboard.KEY_X;
			case 'y':
			case 'Y':
				return Keyboard.KEY_Y;
			case 'z':
			case 'Z':
				return Keyboard.KEY_Z;
			case '0':
				return Keyboard.KEY_0;
			case '1':
				return Keyboard.KEY_1;
			case '2':
				return Keyboard.KEY_2;
			case '3':
				return Keyboard.KEY_3;
			case '4':
				return Keyboard.KEY_4;
			case '5':
				return Keyboard.KEY_5;
			case '6':
				return Keyboard.KEY_6;
			case '7':
				return Keyboard.KEY_7;
			case '8':
				return Keyboard.KEY_8;
			case '9':
				return Keyboard.KEY_9;
			case '`':
				return Keyboard.KEY_GRAVE;
			case '-':
				return Keyboard.KEY_MINUS;
			case '=':
				return Keyboard.KEY_EQUALS;
			case '\b':
				return Keyboard.KEY_BACK;
			case '[':
				return Keyboard.KEY_LBRACKET;
			case ']':
				return Keyboard.KEY_RBRACKET;
			case ';':
				return Keyboard.KEY_SEMICOLON;
			case '\'':
				return Keyboard.KEY_APOSTROPHE;
			case ',':
				return Keyboard.KEY_COMMA;
			case '.':
				return Keyboard.KEY_PERIOD;
			case '/':
				return Keyboard.KEY_SLASH;
			case '*':
				return Keyboard.KEY_MULTIPLY;
			case '+':
				return Keyboard.KEY_ADD;
		}
		return 0;
	}

	public static long microTime() {
		return System.nanoTime() / 1000L;
	}

	public static long milliTime() {
		return System.nanoTime() / 1000000L;
	}

	public static Rectangle clipRectangle(Rectangle toClip, Rectangle clipTo) {
		if (!toClip.intersects(clipTo)) return new Rectangle(0, 0, 0, 0);
		float newX = MathHelper.clamp(toClip.getX(), clipTo.getX(), clipTo.getMaxX());
		float newY = MathHelper.clamp(toClip.getY(), clipTo.getY(), clipTo.getMaxY());
		float newMaxX = MathHelper.clamp(toClip.getMaxX(), clipTo.getX(), clipTo.getMaxX());
		float newMaxY = MathHelper.clamp(toClip.getMaxY(), clipTo.getY(), clipTo.getMaxY());
		return new Rectangle(newX, newY, newMaxX - newX, newMaxY - newY);
	}

	public static long bytesToMB(long bytes) {
		return bytes / 1048576;
	}

	public static String bytesToMBString(long bytes) {
		return bytesToMB(bytes) + " MB";
	}

	public static String colorToString(ReadableColor color, boolean alpha) {
		return color.getRed() + "," + color.getGreen() + "," + color.getBlue() + (alpha ? "," + color.getAlpha() : "");
	}

	public static Color stringToColor(String str) {
		String[] split = str.split(",");
		if (split.length < 3) throw new IllegalArgumentException("Too few color components or wrong delimiter");
		Color color = new Color(Integer.parseInt(split[0]), Integer.parseInt(split[1]), Integer.parseInt(split[2]));
		if (split.length >= 4) color.setAlpha(Integer.parseInt(split[3]));
		return color;
	}

	public static int getNextPowerOfTwo(int number) {
		int ret = Integer.highestOneBit(number);
		return ret < number ? ret << 1 : ret;
	}

	public static boolean isPowerOfTwo(int number) {
		return (number != 0) && (number & (number - 1)) == 0;
	}

	public static final class IPInfo {
		private InetAddress address;
		private int port;
		private boolean ipv6;

		private IPInfo(InetAddress address, int port, boolean ipv6) {
			this.address = address;
			this.port = port;
			this.ipv6 = ipv6;
		}

		public InetAddress getAddress() {
			return address;
		}

		public int getPort() {
			return port;
		}

		public boolean isIPv6() {
			return ipv6;
		}

		@Override
		public boolean equals(Object obj) {
			if (obj == null) {
				return false;
			}
			if (getClass() != obj.getClass()) {
				return false;
			}
			final IPInfo other = (IPInfo)obj;
			if (this.address != other.address && (this.address == null || !this.address.equals(other.address))) {
				return false;
			}
			if (this.port != other.port) {
				return false;
			}
			return true;
		}

		@Override
		public int hashCode() {
			int hash = 5;
			hash = 67 * hash + (this.address != null ? this.address.hashCode() : 0);
			hash = 67 * hash + this.port;
			return hash;
		}

		@Override
		public String toString() {
			return port < 0 ? address.getHostAddress() : ipv6 ? '[' + address.getHostAddress() + "]:" + port : address.getHostAddress() + ':' + port;
		}
	}
}
