package cn.hfut.book_borrow.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import netscape.javascript.JSObject;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * @author LJH
 * @date 2019/11/11 21:46
 */

@Service
public class BookService {
    public JSONArray borrow_books(Map<String, String> cookies) {
        Connection connection = Jsoup.connect("http://opac.hfut.edu.cn:8080/reader/book_lst.php");
        connection.cookies(cookies);

        Connection.Response response = null;
        try {
            response = connection.method(Connection.Method.GET).execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println(cookies);
        System.out.println(response.body());

        /* 所以书的借阅信息 */
        JSONArray bookArray = new JSONArray();
        Document document = Jsoup.parse(response.body());
        Elements elements = document.select("#mylib_content > table > tbody > tr:not(:first-child)");
        for (Element element : elements) {
            /* 条码号 */
            String barCode = element.select("td:nth-child(1)").text();
            /* 书名和作者 */
            String[] bookInfo = element.select("td:nth-child(2)").text().split("/");
            /* 书名 */
            String bookName = bookInfo[0];
            /* 作者 */
            String author = bookInfo[1];
            /* 借阅日期 */
            String borrowDate = element.select("td:nth-child(3)").text();
            /* 归还日期 */
            String returnDate = element.select("td:nth-child(4)").text();
            /* 续借量 */
            String renew = element.select("td:nth-child(5)").text();
            /* 馆藏地 */
            String holdings = element.select("td:nth-child(6)").text();

            JSONObject bookObject = new JSONObject();
            bookObject.put("条码号", barCode);
            bookObject.put("书名", bookName);
            bookObject.put("作者", author);
            bookObject.put("借阅日期", borrowDate);
            bookObject.put("归还日期", returnDate);
            bookObject.put("续借量", renew);
            bookObject.put("馆藏地", holdings);
            bookArray.add(bookObject);
        }

        return bookArray;
    }
}
