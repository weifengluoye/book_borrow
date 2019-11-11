package cn.hfut.book_borrow.service;

import net.sourceforge.tess4j.Tesseract;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * @author LJH
 * @date 2019/11/11 20:11
 */

@Service
public class LoginService {

    /* 过程中所有的cookies */
    public Map<String, String> cookies = new HashMap<>();
    /* 验证码值 */
    String captcha = "";

    /**
     * 登录到图书馆页面的一系列操作
     * @param username 用户名
     * @param password 密码
     * @return cookies集合
     */
    public Map<String, String> login(String username, String password) {
        login_1();
        while (!login_2(username, password))
            login_1();
        login_3();
        return cookies;
    }

    /**
     * 获取验证码页面返回的cookie
     * @return 验证码
     */
    private void login_1() {
        /* 加载验证码图片，获取cookie，并解析验证码 */
        Connection connection = Jsoup.connect("http://my.hfut.edu.cn/captchaGenerate.portal?s=0.2679940000310119");

        Connection.Response response = null;

        try {
            /* 获取验证码图片必须忽略请求内容 */
            response = connection.ignoreContentType(true).method(Connection.Method.GET).execute();
            /* 将获取的cookie储存在上面定义的哈希表中 */
            cookies.putAll(response.cookies());
            /* 将图片进行转换成bufferedImage，利用tesseract进行图片的解析 */
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(response.bodyAsBytes());
            BufferedImage bufferedImage = ImageIO.read(byteArrayInputStream);
            Tesseract tesseract = new Tesseract();
            captcha = tesseract.doOCR(bufferedImage).substring(0, 4);
        } catch (Exception e) {
            e.printStackTrace();
        }
        cookies.putAll(response.cookies());
    }

    /**
     * 验证密码是否正确的页面
     * @param username 用户名
     * @param password 密码
     */
    private boolean login_2(String username, String password) {
        /* 数据 */
        Map<String, String> datas = new HashMap<>();
        datas.put("Login.Token1", username);
        datas.put("Login.Token2", password);
        datas.put("captchaField", captcha);
        datas.put("goto", "http://my.hfut.edu.cn/loginSuccess.portal");
        datas.put("gotoOnFail", "http://my.hfut.edu.cn/loginFailure.portal");

        /* 访问验证用户名和密码的界面 */
        Connection connection = Jsoup.connect("http://my.hfut.edu.cn/userPasswordValidate.portal");

        /* 携带cookie */
        connection.cookies(cookies);
        /* 携带数据 */
        connection.data(datas);

        Connection.Response response = null;
        try {
            response = connection.method(Connection.Method.POST).execute();
            cookies.putAll(response.cookies());
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (response == null) return false;
        cookies.putAll(response.cookies());
        return true;
    }

    /**
     * 登录图书馆页面
     */
    private void login_3() {
        Connection connection = Jsoup.connect("http://opac.hfut.edu.cn:8080/reader/hwthau.php");
        connection.cookies(cookies);

        Connection.Response response = null;
        try {
            response = connection.method(Connection.Method.GET).execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
        cookies.putAll(response.cookies());
    }
}
