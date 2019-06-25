package com.mmi.tauProjekt.Listing;


import com.mmi.tauProjekt.Entity.Ad;
import com.mmi.tauProjekt.Entity.Customer;
import com.mmi.tauProjekt.Repository.AdRepository;
import com.mmi.tauProjekt.Repository.CustomerRepository;
import io.jsonwebtoken.Jwts;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;

import static com.mmi.tauProjekt.Security.SecurityConstants.SECRET;
import static com.mmi.tauProjekt.Security.SecurityConstants.TOKEN_PREFIX;

@RestController
@RequestMapping("/ads")
@Api(value="ads", description="İlan vermek ve yönetme için kullanılır")
public class AdController {
    @Autowired
    AdRepository adRepository;
    CustomerRepository customerRepository;



    public AdController(AdRepository adRepository, CustomerRepository customerRepository){
        this.adRepository = adRepository;
        this.customerRepository = customerRepository;
    }




    @RequestMapping(value="/put-ad", method = RequestMethod.POST)
    public String putAd(@RequestHeader("Authorization") String token, @RequestBody AdInfo adInfo){

        String customerId = tokenToCustomerIdParser(token);
        Customer customer = customerRepository.findById(customerId).orElse(new Customer());

        Collection<Integer> collection =  adRepository.findCustomerAd(customerId);



        if(collection.isEmpty()) {

            Ad newAd = new Ad(customerId,
                    customer.getName(),
                    adInfo.title,
                    adInfo.description,
                    adInfo.price,
                    adInfo.location,
                    adInfo.mail,
                    adInfo.phone, new Date());


            adRepository.save(newAd);
            return "successfully added";
        }
        else {
            return "ad already exists";
        }

    }


    @RequestMapping(value= "/get-ads",method = RequestMethod.POST)
    public ArrayList<Ad> getAllAds(){
        ArrayList<Ad> allAds = new ArrayList<>();
        adRepository.findAll().forEach(allAds::add);
        return allAds;
    }



    @RequestMapping(value="/delete-ad",method = RequestMethod.POST)
    public String deleteAd(@RequestHeader("Authorization") String token){
        String customerId = tokenToCustomerIdParser(token);
        Collection<Integer> collection =  adRepository.findCustomerAd(customerId);

        int adId = -1;

        for(Integer i: collection){
            adId = i;
        }

        if(adId==-1){
            return "ad not found";
        }

        adRepository.deleteById(adId);
        return "deleted successfully";
    }




    public static <T> ArrayList<T>
    getListFromIterator(Iterator<T> iterator)
    {

        // Create an empty list
        ArrayList<T> list = new ArrayList<>();

        // Add each element of iterator to the List
        iterator.forEachRemaining(list::add);

        // Return the List
        return list;
    }

    private String tokenToCustomerIdParser(String token){
        String Customer = Jwts.parser()
                .setSigningKey(SECRET.getBytes())
                .parseClaimsJws(token.replace(TOKEN_PREFIX, ""))
                .getBody()
                .getSubject();
        return Customer;
    }

}

class AdInfo{
    String title;
    String description;
    String phone;
    String mail;
    Integer price;
    String location;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public Integer getPrice() {
        return price;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }
}

