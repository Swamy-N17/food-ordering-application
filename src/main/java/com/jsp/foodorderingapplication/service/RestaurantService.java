package com.jsp.foodorderingapplication.service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.jsp.foodorderingapplication.dto.ResponseStructure;
import com.jsp.foodorderingapplication.entity.MenuItem;
import com.jsp.foodorderingapplication.entity.Restaurant;
import com.jsp.foodorderingapplication.exception.IdNotFoundException;
import com.jsp.foodorderingapplication.exception.InvalidDataException;
import com.jsp.foodorderingapplication.exception.InvalidFieldException;
import com.jsp.foodorderingapplication.exception.NoRecordAvailableException;
import com.jsp.foodorderingapplication.repository.RestaurantRepository;

@Service
public class RestaurantService {

    @Autowired
    private RestaurantRepository restaurantRepository;

    public ResponseStructure<Restaurant> saveRestaurant(Restaurant restaurant) {

        ResponseStructure<Restaurant> res = new ResponseStructure<>();

        if (restaurant.getRating() == null
                || restaurant.getRating() < 1
                || restaurant.getRating() > 5)
            throw new InvalidDataException(
                    "Ratings Should be Between 1 to 5");

        res.setData(restaurantRepository.save(restaurant));
        res.setMessage("Restaurant Records Saved Successfully");
        res.setStatusCode(HttpStatus.CREATED.value());

        return res;
    }

    public ResponseStructure<List<Restaurant>> fetchAllRestaurants() {

        ResponseStructure<List<Restaurant>> res = new ResponseStructure<>();
        List<Restaurant> restaurants = restaurantRepository.findAll();

        if (restaurants.isEmpty())
            throw new NoRecordAvailableException("No Records Found");

        res.setData(restaurants);
        res.setMessage("Restaurant Records Fetched Successfully");
        res.setStatusCode(HttpStatus.OK.value());

        return res;
    }

    public ResponseStructure<Restaurant> findRestaurantId(Integer id) {

        ResponseStructure<Restaurant> res = new ResponseStructure<>();

        Optional<Restaurant> opt = restaurantRepository.findById(id);

        if (opt.isEmpty())
            throw new IdNotFoundException(
                    "Restaurant With ID: " + id + " Not Found");

        res.setData(opt.get());
        res.setMessage(
                "Restaurant Record With ID: " + id
                        + " Fetched Successfully");
        res.setStatusCode(HttpStatus.OK.value());

        return res;
    }

    public ResponseStructure<String> updateRestaurant(
            Map<String, Object> data, Integer id) {

        ResponseStructure<String> res = new ResponseStructure<>();
        Optional<Restaurant> opt = restaurantRepository.findById(id);

        if (opt.isEmpty())
            throw new IdNotFoundException(
                    "Restaurant With ID: " + id + " Not Found");

        Restaurant restaurant = opt.get();

        if (data == null || data.isEmpty())
            throw new InvalidDataException("No update data provided");

        for (Map.Entry<String, Object> entry : data.entrySet()) {

            String key = entry.getKey();
            Object value = entry.getValue();

            switch (key) {
            case "name":
                restaurant.setName((String) value);
                break;

            case "location":
                restaurant.setLocation((String) value);
                break;

            case "rating":
                if (!(value instanceof Number))
                    throw new InvalidDataException(
                            "Rating must be a number");

                int rating = ((Number) value).intValue();

                if (rating < 1 || rating > 5)
                    throw new InvalidDataException(
                            "Ratings Should be Between 1 to 5");

                restaurant.setRating(rating);
                break;

            case "email":
                restaurant.setEmail((String) value);
                break;

            default:
                throw new InvalidFieldException(
                        "Invalid field name: " + key);
            }
        }

        restaurantRepository.save(restaurant);

        res.setData("Success");
        res.setMessage("Restaurant Record Updated Successfully");
        res.setStatusCode(HttpStatus.OK.value());

        return res;
    }

    public ResponseStructure<String> deleteRestaurantByID(Integer id) {

        ResponseStructure<String> res = new ResponseStructure<>();
        Optional<Restaurant> opt = restaurantRepository.findById(id);

        if (opt.isEmpty())
            throw new IdNotFoundException(
                    "Restaurant With ID: " + id + " Not Found");

        restaurantRepository.deleteById(id);

        res.setMessage(
                "Restaurant Record With ID: " + id
                        + " Deleted Successfully");
        res.setStatusCode(HttpStatus.OK.value());
        res.setData("Success");

        return res;
    }

    public ResponseStructure<List<Restaurant>> fetchRestaurantByLocation(
            String location) {

        ResponseStructure<List<Restaurant>> res = new ResponseStructure<>();
        List<Restaurant> restaurants =
                restaurantRepository.findRestaurantByLocation(location);

        if (restaurants.isEmpty())
            throw new NoRecordAvailableException("No Records Found");

        res.setData(restaurants);
        res.setMessage(
                "Restaurant Records With Location "
                        + location + " Fetched Successfully");
        res.setStatusCode(HttpStatus.OK.value());

        return res;
    }

    public ResponseStructure<List<Restaurant>> fetchRestaurantByName(
            String name) {

        ResponseStructure<List<Restaurant>> res = new ResponseStructure<>();
        List<Restaurant> restaurants =
                restaurantRepository.findRestaurantByName(name);

        if (restaurants.isEmpty())
            throw new NoRecordAvailableException("No Records Found");

        res.setData(restaurants);
        res.setMessage(
                "Restaurant Records With Name "
                        + name + " Fetched Successfully");
        res.setStatusCode(HttpStatus.OK.value());

        return res;
    }

    public ResponseStructure<List<Restaurant>>
    getRestaurantsByRatingGreaterThan(Integer rating) {

        ResponseStructure<List<Restaurant>> res = new ResponseStructure<>();
        List<Restaurant> restaurants =
                restaurantRepository.findRestaurantsByRatingGreaterThan(rating);

        if (restaurants.isEmpty())
            throw new NoRecordAvailableException("No Records Found");

        res.setData(restaurants);
        res.setMessage(
                "Restaurant Records With Rating "
                        + rating + " Fetched Successfully");
        res.setStatusCode(HttpStatus.OK.value());

        return res;
    }

    public ResponseStructure<List<MenuItem>> getMenuOfRestaurant(
            Integer id) {

        ResponseStructure<List<MenuItem>> res = new ResponseStructure<>();
        Optional<Restaurant> opt = restaurantRepository.findById(id);

        if (opt.isEmpty())
            throw new IdNotFoundException(
                    "Restaurant With ID: " + id + " Not Found");

        res.setMessage(
                "Menu Fetched Successfully for Restaurant ID: " + id);
        res.setStatusCode(HttpStatus.OK.value());
        res.setData(opt.get().getMenuItem());

        return res;
    }
}
