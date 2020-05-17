package com.shang.springsecurity.util;


import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Random;

/**
 * <p>生成验证码的工具</p>
 *
 * @Author: shangjp
 * @Email: shangjp@163.com
 * @Date: 2020/4/25 16:24
 */
public class VerifyCodeUtil {

    private int width = 100; //生成验证码图片的宽度
    private int height = 50; //生成验证码图片的高度
    private String[] fontNames = {"宋体", "楷体", "隶书", "微软雅黑"};
    //定义验证码图片的背景色为白色
    private Color bgColor = new Color(255, 255, 255);
    private Random random = new Random();
    private String text;   //记录随机字符串


    /**
     * 获取一个随机的颜色
     */
    private Color randomColor() {
        //设置随机RGB值
        int red = random.nextInt(150);
        int green = random.nextInt(150);
        int blue = random.nextInt(150);
        return new Color(red, green, blue);
    }

    /**
     * 获取一个随机的字体
     */
    private Font randomFont() {
        String fontName = fontNames[random.nextInt(fontNames.length)];
        int style = random.nextInt(4); //随机样式
        int size = random.nextInt(5) + 24;//字体在24-29号之间
        return new Font(fontName, style, size);
    }

    /**
     * 获取一个随机字符
     */
    private char randomChar() {
        //候选字符
        String codes = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
        return codes.charAt(random.nextInt(codes.length()));
    }

    /**
     * 创建一个空白的bufferedImage对象
     */
    private BufferedImage bufferedImage() {
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        //强转为2D图形
        Graphics2D graphics = (Graphics2D) image.getGraphics();
        graphics.setColor(bgColor);//设置验证码图片的背景色
        graphics.fillRect(0, 0, width, height);
        return image;
    }

    /**
     * 在空白的BufferedImage 对象上面绘制验证码数字
     */
    public BufferedImage createImage() {
        //调用私有方法,生成空白对象
        BufferedImage image = this.bufferedImage();
        Graphics2D graphics = (Graphics2D) image.getGraphics();
        //下面开始绘制验证码
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < 4; i++) {
            String item = randomChar() + "";
            sb.append(item);
            graphics.setColor(this.randomColor()); //每个验证码的颜色随机
            graphics.setFont(this.randomFont());//每个验证码的字体随机
            float x = i * width * 1.0f / 4;  //计算每个字符的其实起始位置横坐标
            graphics.drawString(item, x, height - 15);
        }
        this.text = sb.toString();//拼接完整的4位验证码字符串
        //汇总干扰线
        this.drawRandomLine(image);
        return image;
    }

    /**
     * 绘制干扰线
     */
    private void drawRandomLine(BufferedImage image) {
        Graphics2D graphics = (Graphics2D) image.getGraphics();
        int num = 5;//干扰线的条数
        for (int i = 0; i < num; i++) {
            int x1 = random.nextInt(width); //起点的横坐标
            int y1 = random.nextInt(height); //起点的纵坐标
            int x2 = random.nextInt(width); //终点的横坐标
            int y2 = random.nextInt(height); //终点的纵坐标
            graphics.setColor(randomColor());//每条干扰线的颜色随机
            graphics.setStroke(new BasicStroke(1.5f));//设置行距
            graphics.drawLine(x1, y1, x2, y2);//画线
        }
    }


    /**
     * 获取生成之后的随机码字符串
     * @return
     */
    public String getText() {
        return text;
    }


    /**
     * 把生成好的验证码图片信息放到输出流中去
     * @param image
     * @param out
     * @throws IOException
     */
    public static void output(BufferedImage image, OutputStream out) throws IOException {
        ImageIO.write(image,"JPEG",out);
    }


}
