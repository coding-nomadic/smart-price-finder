package com.example.product.price.controllers;

import com.example.product.price.models.ContactForm;
import com.example.product.price.models.ProductPriceDetail;
import com.example.product.price.services.EmailService;
import com.example.product.price.services.ProductPriceService;
import com.example.product.price.utils.JsonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

@Controller
public class ProductPriceMvcController {

    private static final Logger logger = LoggerFactory.getLogger(ProductPriceMvcController.class);

    private final ProductPriceService productPriceService;
    private final EmailService emailService;


    public ProductPriceMvcController(final ProductPriceService productPriceService, EmailService emailService) {
        this.productPriceService = productPriceService;
        this.emailService = emailService;
    }

    @GetMapping("/")
    public String index() {
        return "home";
    }

    @PostMapping("/generate")
    public String fetchProductPriceLists(@RequestParam String query, @RequestParam String province, @RequestParam String city, Model model) {
        try {
            logger.info("Query: " + query + ", Province: " + province + ", City: " + city);
            final List<ProductPriceDetail> stores = productPriceService.fetchStores(query, city, province);
            logger.info("Fetched {} stores for query '{}'", stores.size(), JsonUtils.toString(stores));
            model.addAttribute("query", query);
            model.addAttribute("stores", stores);
            if (stores.isEmpty()) {
                model.addAttribute("noData", "No Product Found in this location, try some other locations !");
            }
        } catch (Exception exception) {
            logger.error("Error fetching stores for query '{}'", query, exception);
            model.addAttribute("error", "Unable to fetch stores. Please try again later!");
            model.addAttribute("stores", Collections.emptyList());
        }
        return "home";
    }

    @GetMapping("/about")
    public String aboutPage() {
        return "about";
    }

    @GetMapping("/contact")
    public String contactPage() {
        return "contact";
    }

    @PostMapping("/contact-submit")
    public String submitContactForm(ContactForm form, Model model) {
        try {
            emailService.sendContactEmail(form);
            model.addAttribute("success", "Message sent successfully!");
        } catch (Exception e) {
            model.addAttribute("error", "Failed to send message. Please try again later.");
        }
        return "contact";
    }
}
