package cn.hfut.book_borrow.controller;

import cn.hfut.book_borrow.service.BookService;
import cn.hfut.book_borrow.service.LoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * @author LJH
 * @date 2019/11/11 20:08
 */

@RestController
public class UserController {

    @Autowired
    private LoginService loginService;
    @Autowired
    private BookService bookService;

    @RequestMapping("/getInfo")
    public String getInfo(@RequestParam("username") String username, @RequestParam("password") String password) {
        Map<String, String> cookies = loginService.login(username, password);
        return bookService.borrow_books(cookies).toJSONString();
    }
}
