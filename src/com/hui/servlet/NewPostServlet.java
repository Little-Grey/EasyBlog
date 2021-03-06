package com.hui.servlet;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.hui.dao.PostDAO;
import com.hui.javabean.Post;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;


public class NewPostServlet extends HttpServlet {

    private PostDAO postDAO;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
            req.getRequestDispatcher("/newpost.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String title = req.getParameter("title");
        String body = req.getParameter("body");
        List<String> tags = Arrays.asList(req.getParameter("tags").split("\\s*,\\s*"));
        String author = (String) req.getSession().getAttribute("username");

        Post post = new Post(title, body, tags, author);

        if (author == null || author.isEmpty()) {

            //user without login
            resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            resp.sendRedirect(resp.encodeRedirectURL("/Blog/login"));
        } else if (!post.validate()) {

            req.setAttribute("error_message", post.getMessage());
            req.setAttribute("title", title);
            req.setAttribute("body", body);
            req.setAttribute("tags", tags);

            RequestDispatcher view = req.getRequestDispatcher("/newpost.jsp");
            view.forward(req, resp);

        } else {

            String permalink = postDAO.addPost(post);

            if (permalink == null) {             
                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                resp.sendRedirect(resp.encodeRedirectURL("/Blog"));
            }

            resp.sendRedirect(resp.encodeRedirectURL("/Blog/post/" + permalink));
        }
    }

    @Override
    public void init() throws ServletException {
        postDAO = new PostDAO();
    }
}
