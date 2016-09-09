package com.epam.easyshopway.controller.admin;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;


import com.alibaba.fastjson.JSONArray;
import com.epam.easyshopway.model.Cupboard;
import com.epam.easyshopway.model.CupboardInformation;
import com.epam.easyshopway.model.CupboardPlacement;
import com.epam.easyshopway.model.Map;
import com.epam.easyshopway.model.Placement;
import com.epam.easyshopway.service.CupboardInformationService;
import com.epam.easyshopway.service.CupboardPlacementService;
import com.epam.easyshopway.service.CupboardService;
import com.epam.easyshopway.service.MapService;
import com.epam.easyshopway.service.PlacementService;
import com.sun.xml.internal.ws.wsdl.writer.document.Service;

public class AdminMapServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	private String type;
       
    public AdminMapServlet() {
        super();
    }

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setCharacterEncoding("utf-8");
		type = request.getParameter("type");
		
		switch (type) {
			case "mapsName":{
				JSONArray responseJSON = doForMapsName();
				response.getWriter().write(responseJSON.toString());
			}
				break;
	
			case "map":{
				Integer mapId = Integer.valueOf(request.getParameter("id"));
				JSONObject responseJSON = doForMap(mapId);
				responseJSON = doForMap(mapId);
				response.getWriter().write(responseJSON.toString());
			}	
				break;
				
			case "saveMap":{
				String data = request.getParameter("data");
				int status = doForSaveMap(data);
				response.getWriter().write(status);
			}
				break;
		}
			
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}
	
	
	
	@Override
	protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String type = req.getParameter("type");
		
		switch (type) {
		case "cupboard": {
			String data = req.getParameter("values");
			int bCount = Integer.parseInt(req.getParameter("b_count"));
			int status = doForCupboard(data, bCount);
		}
			break;

		default:
			break;
		}
		
		
	}

	@SuppressWarnings("unchecked")
	private JSONArray doForMapsName(){
		JSONArray mapNameArray = new JSONArray();
		List<Map> maps = MapService.getAll();
		if (maps != null){
			for (Map i : maps){
				JSONObject map = new JSONObject();
				map.put("id", i.getId());
				map.put("nameEn", i.getNameEn());
				map.put("nameUk", i.getNameUk());
				mapNameArray.add(map);
			}
		}
		return mapNameArray;
	}

	@SuppressWarnings("unchecked")
	private JSONObject doForMap (Integer mapId){
		JSONObject response = new JSONObject();
	
		Map map = MapService.getById(mapId);
		if (map != null){
			JSONObject m = new JSONObject();
			m.put("id", map.getId());
			m.put("weight", map.getWeight());
			m.put("height", map.getHeight());
			m.put("nameEn", map.getNameEn());
			m.put("nameUk", map.getNameUk());
			response.put("map", m);
		
			JSONArray enters = getPlaces(PlacementService.getEntersByMapId(mapId));
			JSONArray paydesks = getPlaces(PlacementService.getPayDesksByMapId(mapId));
			JSONArray walls = getPlaces(PlacementService.getWallsByMapId(mapId));
			JSONArray cupboards = cupboardsToJSON(CupboardInformationService.getCupboardsByMapId(mapId));

			response.put("enters", enters);
			response.put("walls", walls);
			response.put("paydesks", paydesks);
			response.put("cupboards", cupboards);
		}
		return response;
	}
			
	private JSONArray getPlaces(List<Placement> placements){
		JSONArray places = new JSONArray();
		if (placements != null){
			for (Placement placement : placements)
				places.add(placement.getPlace());
		}
		return places;
	}
	
	@SuppressWarnings("unchecked")
	private JSONArray cupboardsToJSON (List<CupboardInformation> cupboards){
		JSONArray result = new JSONArray();
		if (cupboards != null){
			int size = cupboards.size();
			for (int i=0; i<size; i++){
				JSONObject cupboard = new JSONObject();
				JSONArray values = new JSONArray();
				Integer id = cupboards.get(i).getCupboardId();
				cupboard.put("id", id);
				do{
					values.add(cupboards.get(i++).getPlace());
				}while (i<size && id.equals(cupboards.get(i).getCupboardId()));
				i--;
				cupboard.put("values", values);
				cupboard.put("board_count", cupboards.get(i).getBoardAmount());
				result.add(cupboard);
			}
		}
		return result;
	}	
	
	@SuppressWarnings("unchecked")
	private int doForCupboard(String jsonData, int bCount){
		try{
			JSONParser parser = new JSONParser();
			JSONObject obj = (JSONObject)parser.parse(jsonData);
			Long id = (Long) obj.get("mapId");
			List<Long> values = (List<Long>)obj.get("values");
			Cupboard cupboard = new Cupboard(bCount, "", "", true);
			CupboardService.insert(cupboard);
			int cupboardId = CupboardService.getLastInserted().getId();
			for (Long value : values){
				Placement placement = new Placement(id.intValue(), value.intValue(), "cupboard");
				PlacementService.insert(placement);
				CupboardPlacement cupboardPlacement = new CupboardPlacement(cupboardId, PlacementService.getLastInserted().getId());
				CupboardPlacementService.insert(cupboardPlacement);
			}
			return 1;
		}catch(org.json.simple.parser.ParseException e){
			e.printStackTrace();
		}
		return 0;
	}
	
	@SuppressWarnings("unchecked")
	private int doForSaveMap (String jsonData){
		try{
			JSONParser parser = new JSONParser();
			JSONObject obj = (JSONObject)parser.parse(jsonData);
			Long id = (Long) obj.get("mapId");
			List<Long> walls = (List<Long>)obj.get("walls");
			List<Long> enters = (List<Long>)obj.get("enters");
			List<Long> paydesks = (List<Long>)obj.get("paydesks");
			insertPlacements(id, walls, "wall");
			insertPlacements(id, enters, "enter");
			insertPlacements(id, paydesks, "paydesk");
			return 1;
		}catch(org.json.simple.parser.ParseException e){
			e.printStackTrace();
		}
		return 0;
	}
	
	private void insertPlacements(Long mapId, List<Long> values, String type){
		if(values !=null){
			for (Long value : values){
				Placement placement = new Placement(mapId.intValue(), value.intValue(), type);
				PlacementService.insert(placement);
			}
		}
	}
	
}


