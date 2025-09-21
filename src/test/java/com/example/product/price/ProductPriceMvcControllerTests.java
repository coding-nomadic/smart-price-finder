package com.example.product.price;

import com.example.product.price.controllers.ProductPriceMvcController;
import com.example.product.price.models.ContactForm;
import com.example.product.price.models.ProductPriceDetail;
import com.example.product.price.services.EmailService;
import com.example.product.price.services.ProductPriceService;
import jakarta.mail.MessagingException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.ui.ConcurrentModel;
import org.springframework.ui.Model;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.*;

public class ProductPriceMvcControllerTests {
    private ProductPriceMvcController productPriceMvcController;
    private ProductPriceService productPriceService;
    private EmailService emailService;

    @BeforeEach
    public void setUp() {
        productPriceService = mock(ProductPriceService.class);
        emailService = mock(EmailService.class);
        productPriceMvcController = new ProductPriceMvcController(productPriceService, emailService);
    }

    @Test
    public void testIndex() {
        assertEquals("home", productPriceMvcController.index());
    }

    @Test
    public void testAbout() {
        assertEquals("about", productPriceMvcController.aboutPage());
    }

    @Test
    public void testContact() {
        assertEquals("contact", productPriceMvcController.contactPage());
    }

    @Test
    public void testFetchProductPriceListsWhenDataIsAvailable() {
        ProductPriceDetail detail = new ProductPriceDetail();
        List<ProductPriceDetail> stores = List.of(detail);
        when(productPriceService.fetchStores("laptop", "Toronto", "Ontario")).thenReturn(CompletableFuture.completedFuture(stores));
        Model model = new ConcurrentModel();
        String view = productPriceMvcController.fetchProductPriceLists("laptop", "Ontario", "Toronto", model).join();
        assertEquals("home", view);
        assertEquals("laptop", model.getAttribute("query"));
        assertEquals(stores, model.getAttribute("stores"));
        assertNull(model.getAttribute("noData"));
        assertNull(model.getAttribute("error"));
    }

    @Test
    void testFetchProductPriceListsSuccessNoData() {
        when(productPriceService.fetchStores("laptop", "Toronto", "Ontario")).thenReturn(CompletableFuture.completedFuture(Collections.emptyList()));
        Model model = new ConcurrentModel();
        String view = productPriceMvcController.fetchProductPriceLists("laptop", "Ontario", "Toronto", model).join();
        assertEquals("home", view);
        assertEquals(Collections.emptyList(), model.getAttribute("stores"));
        assertEquals("No Product Found in this location, try some other locations!", model.getAttribute("noData"));
    }

    @Test
    public void testFetchProductPriceListsWhenExceptionIsThrown() {
        when(productPriceService.fetchStores(anyString(), anyString(), anyString())).thenReturn(CompletableFuture.failedFuture(new RuntimeException("API error")));
        Model model = new ConcurrentModel();
        String view = productPriceMvcController.fetchProductPriceLists("laptop", "Ontario", "Toronto", model).join();
        assertEquals("home", view);
        assertEquals("Unable to fetch stores. Please try again later!", model.getAttribute("error"));
        assertEquals(Collections.emptyList(), model.getAttribute("stores"));
    }

    @Test
    public void testSubmitContactFormWhenNoExceptionThrown() throws Exception {
        Model model = new ConcurrentModel();
        ContactForm contactForm = createContactForm();
        CompletableFuture<String> view = productPriceMvcController.submitContactForm(contactForm, model);
        assertEquals("contact", view.get());
        assertEquals("Message sent successfully!", model.getAttribute("success"));
        verify(emailService, times(1)).sendContactEmail(contactForm);
    }

    @Test
    public void testSubmitContactFormWhenExceptionThrown() throws Exception {
        Model model = new ConcurrentModel();
        ContactForm contactForm = createContactForm();
        doThrow(RuntimeException.class).when(emailService).sendContactEmail(ArgumentMatchers.any(ContactForm.class));
        CompletableFuture<String> view = productPriceMvcController.submitContactForm(contactForm, model);
        assertEquals("contact", view.get());
        assertEquals("Failed to send message. Please try again later.", model.getAttribute("error"));
        verify(emailService, times(1)).sendContactEmail(contactForm);
    }

    private ContactForm createContactForm() {
        ContactForm contactForm = new ContactForm();
        contactForm.setName("John Doe");
        contactForm.setEmail("john.doe@example.com");
        contactForm.setMessage("Test Message");
        contactForm.setEmail("john.doe@example.com");
        return contactForm;
    }
}
