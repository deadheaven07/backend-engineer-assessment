package com.midas.app.providers.external.stripe;

import com.midas.app.models.Account;
import com.midas.app.providers.payment.CreateAccount;
import com.midas.app.providers.payment.PaymentProvider;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.Customer;
import com.stripe.param.CustomerCreateParams;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Getter
public class StripePaymentProvider implements PaymentProvider {
    private final Logger logger = LoggerFactory.getLogger(StripePaymentProvider.class);

    private final StripeConfiguration configuration;

    /** providerName is the name of the payment provider */
    @Override
    public String providerName() {

        return "stripe";
    }

    /**
     * createAccount creates a new account in the payment provider.
     *
     * @param details is the details of the account to be created.
     * @return Account
     */
    @Override
    public Account createAccount(CreateAccount details) {

        // throw new UnsupportedOperationException("Not implemented");
        try {
            // Initialize Stripe with your secret key
            Stripe.apiKey = configuration.getApiKey();

            // Use Stripe SDK to create a new customer
            CustomerCreateParams.Builder customerParamsBuilder =
                    new CustomerCreateParams.Builder()
                            .setEmail(details.getEmail()) // Set the email from CreateAccount details
                            .setName(details.getFirstName() + " " + details.getLastName()); // Set customer name

            Customer customer = Customer.create(customerParamsBuilder.build());

            // Construct an Account object based on the Stripe response
            return Account.builder()
                    .firstName(details.getFirstName())
                    .lastName(details.getLastName())
                    .email(details.getEmail())
                    .providerType(ProviderType.STRIPE)
                    .providerId(customer.getId())
                    .build();

        } catch (StripeException e) {
            logger.error("Failed to create Stripe customer", e);
            throw new RuntimeException("Failed to create Stripe customer", e);
        }
    }
}
/**
 * Creates a new customer in Stripe.
 *
 * @param email The email address of the customer.
 * @return The created Customer object.
 * @throws StripeException If an error occurs during the creation process.
 */
public Customer createCustomer(String email) throws StripeException {
    Stripe.apiKey = configuration.getApiKey();

    // Use Stripe SDK to create a new customer
    CustomerCreateParams params = CustomerCreateParams.builder()
            .setEmail(email)
            .build();

    return Customer.create(params);
}
