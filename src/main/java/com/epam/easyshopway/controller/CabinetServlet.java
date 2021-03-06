package com.epam.easyshopway.controller;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.epam.easyshopway.model.User;

/**
 * Servlet implementation class CabinetServlet
 */
public class CabinetServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public CabinetServlet() {
		super();
		// TODO Auto-generated constructor stub
	}
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		HttpSession session = request.getSession(false);
		if (session == null) {
			request.getRequestDispatcher("/WEB-INF/error.jsp").forward(request, response);
		} else {
			User user = (User) session.getAttribute("user");

			if (user != null) {
				if ("admin".equals(user.getRole())) {
					request.getRequestDispatcher("/WEB-INF/admin/cabinet.jsp").forward(request, response);
				} else {
					request.getRequestDispatcher("/WEB-INF/user/cabinet.jsp").forward(request, response);
				}
			} else {
				request.getRequestDispatcher("/WEB-INF/error.jsp").forward(request, response);
			}
		}
	}


}
