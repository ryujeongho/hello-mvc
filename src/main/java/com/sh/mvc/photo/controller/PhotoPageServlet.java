package com.sh.mvc.photo.controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sh.mvc.common.GsonConverter;
import com.sh.mvc.photo.model.entity.Photo;
import com.sh.mvc.photo.model.service.PhotoService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static com.sh.mvc.common.HelloMvcUtils.getGson;

@WebServlet("/photo/page")
public class PhotoPageServlet extends HttpServlet {
    private PhotoService photoService = new PhotoService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // 1. 사용자입력값처리
        int page = Integer.parseInt(req.getParameter("page"));
        final int limit = 5;
        Map<String, Object> param = Map.of("page", page, "limit", limit);
        System.out.println(param);
        // 2. 업무로직
        List<Photo> photos = photoService.findAll(param);
        System.out.println(photos);
        // 3. json 응답처리
        resp.setContentType("application/json; charset=utf-8");

        // HelloMvcUtils.getGson() : LocalDate, LocalDateTime 적용된 gson객체 반환
        getGson().toJson(photos, resp.getWriter());
    }
}