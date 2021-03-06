package com.epam.easyshopway.controller.admin;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.epam.easyshopway.model.Product;
import com.epam.easyshopway.model.ProductType;
import com.epam.easyshopway.model.User;
import com.epam.easyshopway.service.ProductService;
import com.epam.easyshopway.service.ProductTypeService;
import com.epam.easyshopway.service.UserService;


import sun.net.www.content.image.jpeg;

public class TypeProductControlServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final int MAX_MEMORY_SIZE = 1024 * 1024 * 5;
	private static final int MAX_REQUEST_SIZE = 1024 * 1024;

	private JSONObject o;
	private User user;
	private ProductType productType;

	public TypeProductControlServlet() {
		super();
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		o = new JSONObject();
		user = (User) request.getSession(false).getAttribute("user");
		o.put("types", setJsonArrayType(ProductTypeService.getAll()));
		System.out.println(o.toJSONString());
		response.setCharacterEncoding("UTF-8");
		response.getWriter().write(o.toJSONString());
	}

	@Override
	protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		int id = Integer.parseInt(req.getParameter("id"));
		System.out.println("Delete type " + id);
		ProductTypeService.delete(id);
	}

	private JSONArray setJsonArrayType(Collection<ProductType> list) {
		JSONArray jsonArray = new JSONArray();
		JSONObject object;
		for (ProductType u : list) {
			object = new JSONObject();
			object.put("id", u.getId());
			object.put("img", u.getImageUrl());
			object.put("nen", u.getNameEn());
			object.put("nuk", u.getNameUk());
			jsonArray.add(object);
		}
		return jsonArray;
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

		req.setCharacterEncoding("UTF-8");
		resp.setCharacterEncoding("UTF-8");
		
		JSONObject message = new JSONObject(); 

		boolean isMultipart = ServletFileUpload.isMultipartContent(req);
		if (!isMultipart) {
			return;
		}
		System.out.println("DoPost type prod");
		DiskFileItemFactory factory = new DiskFileItemFactory();
		factory.setSizeThreshold(MAX_MEMORY_SIZE);
		factory.setRepository(new File(System.getProperty("java.io.tmpdir")));
		ServletFileUpload upload = new ServletFileUpload(factory);
		String fName = "images/prod/def.gif";
		try {
			List items = upload.parseRequest(req);
			int id = Integer.parseInt(((FileItem) items.get(0)).getString());
			System.out.println(id + " id");
			String name_en = ((FileItem) items.get(1)).getString();
			System.out.println(name_en + " name_en");
			String name_uk = new String(((FileItem) items.get(2)).getString().getBytes("ISO-8859-1"), "UTF-8");
			System.out.println(name_uk + " name_uk");
			FileItem fileItem = (FileItem) items.get(3);

			String type = "";
			try {
				type = "" + fileItem.getName().substring(fileItem.getName().lastIndexOf('.') + 1);
			} catch (NullPointerException e) {
				e.printStackTrace();
			}
			if (!"".equals(type)) {
				fName = "images/prod/" + new Random().nextInt(Integer.MAX_VALUE) + "." + type;
				String absoluteDiskPath = getServletContext().getRealPath("/" + fName);
				System.out.println(absoluteDiskPath);
				File uploadedFile = new File(absoluteDiskPath);
				try (InputStream input = fileItem.getInputStream()) {
				    // It's an image (only BMP, GIF, JPG and PNG are recognized).
				     ImageIO.read(input).toString();
				     fileItem.write(uploadedFile);
				     System.out.println("Image download successful");
				     req.getSession(false).setAttribute("messageProd", "Image loaded successfully");
				} catch (Exception e) {
				      // It's not an image.
					System.out.println("Image download failed");
					req.getSession(false).setAttribute("messageProd", "Image loading failed");
					throw e;
				}
					
			}

			if (id != 0) {
				productType = ProductTypeService.getById(id);
				productType.setNameEn(name_en);
				productType.setNameUk(name_uk);
				if (!"".equals(type))
					productType.setImageUrl(fName);
				if (ProductTypeService.update(id, productType) > 0) {
					System.out.println("OK put");
				} else {
					System.out.println("Bad put");
				}
			} else {
				productType = new ProductType();
				productType.setNameEn(name_en);
				productType.setNameUk(name_uk);
				productType.setImageUrl(fName);
				System.out.println(fName + "file name in else");
				productType.setActive(true);
				if (ProductTypeService.hasNameEn(name_en)){
					message.put("msg", "This english name already exists");
					System.out.println("This english name already exists");
				}else if(ProductTypeService.hasNameUk(name_uk)) {
					System.out.println(name_uk);
					message.put("msg", "This ukrainian name already exists");
					System.out.println("This ukrainian name already exists");
				}else if(ProductTypeService.insert(productType) > 0){
					message.put("msg", "Product type added successfully");
					System.out.println("Product type added successfully");
				}else{
					message.put("msg", "Product type adding failed");
				}
			}
		} catch (FileUploadException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		System.out.println("Before redirect");
//		resp.getWriter().write(message.toString());
		resp.sendRedirect("/EasyShopWayNew/cabinet#/types");
	}

}
