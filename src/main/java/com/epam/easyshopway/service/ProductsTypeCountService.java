package com.epam.easyshopway.service;

import java.sql.Date;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import com.epam.easyshopway.dto.ProductsTypeCountDTO;
import com.epam.easyshopway.model.ProductsTypeCount;

public class ProductsTypeCountService {
	@SuppressWarnings("finally")
	public static List<ProductsTypeCount> getUserProductTypesUser(
			Integer userId, Date startDate, Date endDate) {
		List<ProductsTypeCount> userProducts = new ArrayList<>();
		try (ProductsTypeCountDTO dto = new ProductsTypeCountDTO()) {
			userProducts = dto.getUserProductTypesUser(userId, startDate,
					endDate);
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (IllegalAccessException | InstantiationException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			return userProducts;
		}
	}

	@SuppressWarnings("finally")
	public static List<ProductsTypeCount> getUserProductTypesAdmin(Date startDate, Date endDate) {
		List<ProductsTypeCount> userProducts = new ArrayList<>();
		try (ProductsTypeCountDTO dto = new ProductsTypeCountDTO()) {
			userProducts = dto.getUserProductTypesAdmin(startDate, endDate);
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (IllegalAccessException | InstantiationException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			return userProducts;
		}
	}
}
