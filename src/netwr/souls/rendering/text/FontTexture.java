package netwr.souls.rendering.text;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;

import netwr.souls.rendering.Texture;

public class FontTexture
{
	private static final String IMAGE_FORMAT = "PNG";
	private Font font;
	private String charsetName;
	private Map<Character, CharInfo> charMap;
	private int width;
	private int height;
	private Texture texture;
	
	public FontTexture(Font font, String charsetName) throws Exception
	{
		this.font = font;
		this.charsetName = charsetName;
		charMap = new HashMap<Character, CharInfo>();
		
		buildTexture();
	}
	
	private String getAllAvailableChars(String charsetName)
	{
		CharsetEncoder ce = Charset.forName(charsetName).newEncoder();
		StringBuilder result = new StringBuilder();
		
		for(char c = 0; c < Character.MAX_VALUE; c++)
			if(ce.canEncode(c))
				result.append(c);
		
		return result.toString();
	}
	
	private void buildTexture() throws Exception
	{
		BufferedImage img = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2d = img.createGraphics();
		g2d.setFont(font);
		FontMetrics fontMetrics = g2d.getFontMetrics();
		
		String allChars = getAllAvailableChars(charsetName);
		width = 0;
		height = 0;
		
		for(char c : allChars.toCharArray())
		{
			CharInfo charInfo = new CharInfo(width, fontMetrics.charWidth(c));
			charMap.put(c, charInfo);
			width += charInfo.getWidth();
			height = Math.max(height, fontMetrics.getHeight());
		}
		
		g2d.dispose();
		
		img = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		g2d = img.createGraphics();
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2d.setFont(font);
		fontMetrics = g2d.getFontMetrics();
		g2d.setColor(Color.WHITE);
		g2d.drawString(allChars, 0, fontMetrics.getAscent());
		g2d.dispose();
		
		InputStream is;
		
		try(ByteArrayOutputStream out = new ByteArrayOutputStream())
		{
			ImageIO.write(img, IMAGE_FORMAT, out);
			out.flush();
			is = new ByteArrayInputStream(out.toByteArray());
		}
		
		texture = new Texture(is);
	}
	
	public static class CharInfo
	{
		private final int startX;
		private final int width;
		
		public CharInfo(int startX, int width)
		{
			this.startX = startX;
			this.width = width;
		}
		
		public int getStartX()
		{
			return startX;
		}
		
		public int getWidth()
		{
			return width;
		}
	}
	
	public Texture getTexture()
	{
		return texture;
	}
	
	public int getWidth()
	{
		return width;
	}
	
	public int getHeight()
	{
		return height;
	}
	
	public CharInfo getCharInfo(char c)
	{
		return charMap.get(c);
	}
}
