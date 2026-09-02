package com.jsp.foodorderingapplication.service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.jsp.foodorderingapplication.dto.ResponseStructure;
import com.jsp.foodorderingapplication.entity.MenuItem;
import com.jsp.foodorderingapplication.entity.Restaurant;
import com.jsp.foodorderingapplication.exception.IdNotFoundException;
import com.jsp.foodorderingapplication.exception.InvalidFieldException;
import com.jsp.foodorderingapplication.exception.NoRecordAvailableException;
import com.jsp.foodorderingapplication.repository.MenuItemRespository;
import com.jsp.foodorderingapplication.repository.RestaurantRepository;

@Service
public class MenuItemService {

	@Autowired
	private MenuItemRespository menuItemRepository;

	@Autowired
	private RestaurantRepository restaurantRepository;

	public ResponseStructure<MenuItem> saveItems(MenuItem menuItem) {

		ResponseStructure<MenuItem> res = new ResponseStructure<MenuItem>();

		// Restaurant Validation
		Optional<Restaurant> opt = restaurantRepository.findById(menuItem.getRestaurant().getRestaurantId());
		if (opt.isEmpty())
			throw new NoRecordAvailableException("Cannot add menu item. The specified restaurant does not exist");

		// menuItem price validation
		if (menuItem.getPrice() == null || menuItem.getPrice() <= 0)
			throw new DataIntegrityViolationException("Menu Item price cannot be negative.");

		res.setData(menuItemRepository.save(menuItem));
		res.setMessage("Menu Items Saved Sucessfully");
		res.setStatusCode(HttpStatus.CREATED.value());
		return res;

	}

	public ResponseStructure<List<MenuItem>> fetchAllItems() {

		ResponseStructure<List<MenuItem>> res = new ResponseStructure<List<MenuItem>>();
		List<MenuItem> items = menuItemRepository.findAll();

		if (items.isEmpty())
			throw new NoRecordAvailableException("No Record Found");
		else {
              res.setData(items);
              res.setStatusCode(HttpStatus.OK.value());
              res.setMessage("Menu Items Fetched Successfully");
              return res;
		}
	}

	public  ResponseStructure<MenuItem> getItemById(Integer id) {
		ResponseStructure<MenuItem> res = new ResponseStructure<MenuItem>();

	
		Optional<MenuItem> opt = menuItemRepository.findById(id);
		if(opt.isPresent()) {
			res.setData(opt.get());
			res.setMessage("Menu Item With ID: "+ id +" Fetched Sucessfully");
			res.setStatusCode(HttpStatus.OK.value());
			return res;
		}
		else
			throw new IdNotFoundException("Menu Item With ID: "+id+" Not Found");
	}
	
	public ResponseStructure<String> updatePriceAndAvailability(Map<String, Object> data, Integer id) {

	    ResponseStructure<String> res = new ResponseStructure<String>();

	    Optional<MenuItem> opt = menuItemRepository.findById(id);

	    if (opt.isPresent()) {

	        MenuItem menuItem = opt.get();

	        for (Map.Entry<String, Object> entry : data.entrySet()) {

	            String key = entry.getKey();
	            Object value = entry.getValue();

	            switch (key) {

	            case "price":
	                if (!(value instanceof Number))
                    throw new InvalidFieldException("Price must be a number.");
                menuItem.setPrice(((Number) value).doubleValue());
	                break;

	            case "availability":
	                menuItem.setAvailability((Boolean) value);
	                break;

	            default:
	                throw new InvalidFieldException("Invalid field name. Please provide a valid field.");
	            }
	        }

	        menuItemRepository.save(menuItem);

	        res.setData("Success");
	        res.setMessage("MenuItem Record Updated Successfully");
	        res.setStatusCode(HttpStatus.OK.value());

	        return res;

	    } else
	        throw new IdNotFoundException("MenuItem With ID: " + id + " Not Found");
	}

	public ResponseStructure<List<MenuItem>> sortByPrice(String fieldName) {
		ResponseStructure<List<MenuItem>> res = new ResponseStructure<List<MenuItem>>();
		List<MenuItem> items = menuItemRepository.findAll(Sort.by(fieldName).ascending());
		if(items.isEmpty())
			throw new NoRecordAvailableException("No Records Found");
		else {
			res.setData(items);
			res.setMessage("Menu Items Sorted by Price in Ascending Order Successfully");
	        res.setStatusCode(HttpStatus.OK.value());

	        return res;
		}
	}
	public ResponseStructure<List<MenuItem>> getItemsByName(String itemName) {

	    ResponseStructure<List<MenuItem>> res = new ResponseStructure<List<MenuItem>>();

	    List<MenuItem> items = menuItemRepository.findByItemName(itemName);

	    if (items.isEmpty()) {
	        throw new IdNotFoundException("MenuItem With Name: " + itemName + " Not Found");
	    }

	    res.setData(items);
	    res.setMessage("MenuItems Fetched Successfully");
	    res.setStatusCode(HttpStatus.OK.value());

	    return res;
	}

	public ResponseStructure<List<MenuItem>>  getAllItemsInRestaurant(Integer restaurantId) {

	    ResponseStructure<List<MenuItem>> res = new ResponseStructure<List<MenuItem>>();
		Optional<Restaurant> opt = restaurantRepository.findById(restaurantId);
		if(opt.isPresent()) {
			res.setData(opt.get().getMenuItem());
			res.setMessage("Menu Items Available in Resturant With ID: "+restaurantId+" fetched Successfully ");
			res.setStatusCode(HttpStatus.OK.value());

		    return res;
		}
		else
	        throw new IdNotFoundException("Restaurant With ID: " + restaurantId + " Not Found");
	}
	
}
