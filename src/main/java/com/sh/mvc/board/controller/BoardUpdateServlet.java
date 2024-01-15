package com.sh.mvc.board.controller;

import com.sh.mvc.board.model.entity.Attachment;
import com.sh.mvc.board.model.service.BoardService;
import com.sh.mvc.board.model.vo.BoardVo;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@WebServlet("/board/boardUpdate")
public class BoardUpdateServlet extends HttpServlet {
    private BoardService boardService = new BoardService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // 1.사용자 입력값 처리
        long id = Long.parseLong(req.getParameter("id"));
        System.out.println(id);
        // 2. 업무로직
        BoardVo board = boardService.findById(id);
        System.out.println(board);
        req.setAttribute("board", board);
        // 3. forwarding
        req.getRequestDispatcher("/WEB-INF/views/board/boardUpdate.jsp").forward(req, resp);
    }

    /**
     * 파일업로드처리
     * 1. commons-io, commons-fileupload 의존 추가
     * 2. form[method=post][enctype=multipart/form-data] 설정
     * 3. DiskFileItemFactory / ServletFileUpload / FileItem요청처리
     *  - 저장경로
     *  - 파일최대크기
     * @param req
     * @param resp
     * @throws ServletException
     * @throws IOException
     */
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // DiskFileItemFactory - ServletFileUpload
        File repository = new File("C:\\Workspaces\\web_server_workspace\\hello-mvc\\src\\main\\webapp\\upload\\board");
        DiskFileItemFactory factory = new DiskFileItemFactory();
        factory.setRepository(repository);
        factory.setSizeThreshold(10 * 1024 * 1024); // 10mb
        ServletFileUpload servletFileUpload = new ServletFileUpload(factory);
        BoardVo board = new BoardVo();
        try {
            // 1. 사용자입력값 처리
            List<FileItem> fileItemList = servletFileUpload.parseRequest(req);
            for (FileItem fileItem : fileItemList) {
                String name = fileItem.getFieldName();
                if (fileItem.isFormField()) {
                    // form field
                    String value = fileItem.getString("utf-8");
                    board.setValue(name, value);
                } else {
                    // file
                    if (fileItem.getSize() > 0) {
                        String originalFilename = fileItem.getName();
                        int dotIndex = originalFilename.lastIndexOf(".");
                        String ext = dotIndex > -1 ? originalFilename.substring(dotIndex) : "";
                        String renamedFilename = UUID.randomUUID() + ext;
                        Attachment attach = new Attachment();
                        attach.setOriginalFilename(originalFilename);
                        attach.setRenamedFilename(renamedFilename);
                        board.addAttachment(attach);

                        File upFile = new File(repository, renamedFilename);
                        fileItem.write(upFile); // 파일 출력
                    }
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        System.out.println(board);

        // 2. 업무로직
        int result = boardService.updateBoard(board);

        req.getSession().setAttribute("msg", "게시물을 성공적으로 수정했습니다.😁");
        // 3. redirect
        resp.sendRedirect(req.getContextPath() + "/board/boardDetail?id=" + board.getId());

    }
}