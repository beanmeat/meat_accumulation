package pay.alipay.utils;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
 
import javax.imageio.ImageIO;
 
 
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import io.micrometer.common.util.StringUtils;


public class WriteWordsBottomQrCode {
	private static final int QRCOLOR = 0xFF000000; // 二维码颜色 默认是黑色
	   private static final int BGWHITE = 0xFFFFFFFF; // 背景颜色
	 
	   private static final int WIDTH = 300; // 二维码宽
	   private static final int HEIGHT = 300; // 二维码高
	 
	   private static final int WORDHEIGHT = 345; // 加文字二维码高
	 
	 
	   /**
	    * 用于设置QR二维码参数
	    */
	   private static Map<EncodeHintType, Object> hints = new HashMap<EncodeHintType, Object>() {
	      private static final long serialVersionUID = 1L;
	      {
	         // 设置QR二维码的纠错级别（H为最高级别）具体级别信息
	         put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
	         // 设置编码方式
	         put(EncodeHintType.CHARACTER_SET, "utf-8");
	         put(EncodeHintType.MARGIN, 0);
	      }
	   };
	 
	 
	   /**
	    * 设置 Graphics2D 属性  （抗锯齿）
	    * @param graphics2D
	    */
	   private static void setGraphics2D(Graphics2D graphics2D){
	      graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
	      graphics2D.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_DEFAULT);
	      Stroke s = new BasicStroke(1, BasicStroke.CAP_ROUND, BasicStroke.JOIN_MITER);
	      graphics2D.setStroke(s);
	   }
	 
	 
	   /**
	    * 生成二维码图片存储到filePath中
	    * 
	    * @param content
	    *            二维码内容
	    * @param filePath
	    *            成二维码图片保存路径
	    * @return
	    */
	   public static boolean createImg(String content, String filePath) {
	      boolean flag = false;
	      try {
	         MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
	         BitMatrix bitMatrix = multiFormatWriter.encode(content, BarcodeFormat.QR_CODE, WIDTH, HEIGHT, hints);
	 
	         // 图片输出路径
	         String code = content.split("=")[1]; // 设备编号
	 
	         File file = new File(filePath + "//PSD" + code + ".jpg");
	         if (!file.exists()) {
	            // 如果文件夹不存在则创建
	            file.mkdirs();
	         }
	 
	         // 输出二维码图片到文件夹
	         MatrixToImageWriter.writeToFile(bitMatrix, "jpg", file);
	         flag = true;
	 
	      } catch (IOException e) {
	         e.printStackTrace();
	      } catch (Exception e) {
	         e.printStackTrace();
	      }
	      return flag;
	   }
	 
	 
	   /**
	    * 把带logo的二维码下面加上文字
	    * @param image
	    * @param words
	    * @return
	    */
	   public static BufferedImage insertWords(BufferedImage image,String words){
	      // 新的图片，把带logo的二维码下面加上文字
	      if (StringUtils.isNotEmpty(words)) {
	 
	         //创建一个带透明色的BufferedImage对象
	         BufferedImage outImage = new BufferedImage(WIDTH, WORDHEIGHT, BufferedImage.TYPE_INT_ARGB);
	         Graphics2D outg = outImage.createGraphics();
	         setGraphics2D(outg);
	 
	         // 画二维码到新的面板
	         outg.drawImage(image, 0, 0, image.getWidth(), image.getHeight(), null);
	         // 画文字到新的面板
	         Color color=new Color(0,0,0);
	         outg.setColor(color);
	         // 字体、字型、字号
	         if(words != null && words.length() > 8) {
	        	 outg.setFont(new Font("微软雅黑", Font.PLAIN, 25));
	         } else {
	        	 outg.setFont(new Font("微软雅黑", Font.PLAIN, 30));
	         }
	         
	         //文字长度
	         int strWidth = outg.getFontMetrics().stringWidth(words);
	         //总长度减去文字长度的一半  （居中显示）
	         int wordStartX=(WIDTH - strWidth) / 2;
	         //height + (outImage.getHeight() - height) / 2 + 12
	         int wordStartY=HEIGHT+30;
	         // 画文字
	         outg.drawString(words, wordStartX, wordStartY);
	         outg.dispose();
	         outImage.flush();
	         return outImage;
	      }
	      return null;
	   }
	 
	 
	   /**
	    * @description 生成带logo的二维码图片 二维码下面带文字
	    * @param logoFile loge图片的路径
	    * @param bgFile 背景图片的路径
	    * @param codeFile 图片输出路径
	    * @param qrUrl 二维码内容
	    * @param words 二维码下面的文字
	    */
	   public static void drawLogoQRCode(File logoFile,File bgFile, File codeFile, String qrUrl, String words) {
	        try {
	            MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
	            // 参数顺序分别为：编码内容，编码类型，生成图片宽度，生成图片高度，设置参数
	            BitMatrix bm = multiFormatWriter.encode(qrUrl, BarcodeFormat.QR_CODE, WIDTH, HEIGHT, hints);
	            BufferedImage image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
	 
	            // 开始利用二维码数据创建Bitmap图片，分别设为黑（0xFFFFFFFF）白（0xFF000000）两色
	            for (int x = 0; x < WIDTH; x++) {
	                for (int y = 0; y < HEIGHT; y++) {
	                    image.setRGB(x, y, bm.get(x, y) ? QRCOLOR : BGWHITE);
	                }
	            }
	 
	            //把logo画到二维码上面
	            if (logoFile != null && logoFile.exists()) {
	                // 构建绘图对象
	                Graphics2D g = image.createGraphics();
	            setGraphics2D(g);
	                // 读取Logo图片
	                BufferedImage logo = ImageIO.read(logoFile);
	                // 开始绘制logo图片 等比缩放：（width * 2 / 10 height * 2 / 10）不需缩放直接使用图片宽高
	            //width * 2 / 5 height * 2 / 5  logo绘制在中心点位置
	                g.drawImage(logo, WIDTH * 2 / 5, HEIGHT * 2 / 5, logo.getWidth(), logo.getHeight(), null);
	                g.dispose();
	                logo.flush();
	            }
	 
	            // 新的图片，把带logo的二维码下面加上文字
	            image=insertWords(image,words);
	 
	            image.flush();
	            ImageIO.write(image, "png", codeFile);
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
	   }
	   
	   public static void main(String[] args) {
	      //logo
	      File logoFile = new File("d:/QRCodeDoc/qrLogo.jpg");
	      //背景图片
	     // File bgFile = new File("G://picture/01.png");
	      //生成图片
	        File qrCodeFile = new File("d:/QRCodeDoc/qrLogo111.jpg");
	        //二维码内容
	        String url = "https://w.url.cn/s/AYmfAV3";
	        //二维码下面的文字
	        String words = "PSD000001";
	        drawLogoQRCode(logoFile,null, qrCodeFile, url, words);
	   }
	
}