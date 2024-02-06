package com.midas.app.controllers;


import com.midas.app.providers.external.stripe.StripePaymentProvider;

import com.midas.app.mappers.Mapper;
import com.midas.app.models.Account;
import com.midas.app.providers.external.stripe.ProviderType;
import com.midas.app.services.AccountService;
import com.midas.app.services.TemporalWorkflowService;
import com.midas.generated.api.AccountsApi;
import com.midas.generated.model.AccountDto;
import com.midas.generated.model.CreateAccountDto;
import com.stripe.exception.StripeException;
import com.stripe.model.Customer;
import java.util.List;

import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;

@Controller
//@RequiredArgsConstructor
public class AccountController implements AccountsApi {
  private final AccountService accountService;
     public final StripePaymentProvider stripePaymentProvider; // Inject StripePaymentProvider.java
    private final TemporalWorkflowService temporalWorkflowService;// Inject TemporalWorkflowService
  private final Logger logger = LoggerFactory.getLogger(AccountController.class);
    public AccountController(AccountService accountService, StripePaymentProvider stripePaymentProvider, TemporalWorkflowService temporalWorkflowService) {
        this.accountService = accountService;
        this.stripePaymentProvider = stripePaymentProvider;
        this.temporalWorkflowService = temporalWorkflowService;
    }

    /**
   * POST /accounts : Create a new user account Creates a new user account with the given details
   * and attaches a supported payment provider such as &#39;stripe&#39;.
   *
   * @param createAccountDto User account details (required)
   * @return User account created (status code 201)
   */
  @Override
  public ResponseEntity<AccountDto> createUserAccount(CreateAccountDto createAccountDto) {
    logger.info("Creating account for user with email: {}", createAccountDto.getEmail());

      // var account =
      //     accountService.createAccount(
      //         Account.builder()
      //             .firstName(createAccountDto.getFirstName())
      //             .lastName(createAccountDto.getLastName())
      //             .email(createAccountDto.getEmail())
      //             .build());

      try {
      // Integrate Stripe SDK for customer creation
      Customer customer = stripePaymentProvider.createCustomer(createAccountDto.getEmail());
      // Process user signup request
      Account account =
          accountService.createAccount(
              Account.builder()
                  .firstName(createAccountDto.getFirstName())
                  .lastName(createAccountDto.getLastName())
                  .email(createAccountDto.getEmail())
                  .providerType(ProviderType.STRIPE)
                  .providerId(customer.getId()) // Set the Stripe customer ID
                  .build());

      // Trigger Temporal workflow for Stripe integration
      temporalWorkflowService.triggerStripeIntegration(account.getId());

      return new ResponseEntity<>(Mapper.toAccountDto(account), HttpStatus.CREATED);

    } catch (StripeException e) {
      // Handle Stripe integration failure
      logger.error("Failed to create Stripe customer", e);
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
    }
  }

  /**
   * GET /accounts : Get list of user accounts Returns a list of user accounts.
   *
   * @return List of user accounts (status code 200)
   */
  @Override
  public ResponseEntity<List<AccountDto>> getUserAccounts() {
    logger.info("Retrieving all accounts");

    try {
      // Call a method from the AccountService to retrieve user accounts
      List<Account> accounts = accountService.getAccounts();

      // Convert accounts to DTOs using Mapper
      List<AccountDto> accountsDto = accounts.stream().map(Mapper::toAccountDto).toList();

      // Return the list of DTOs in the response entity
      return new ResponseEntity<>(accountsDto, HttpStatus.OK);

    } catch (Exception e) {
      // Handle exceptions and return an error response if necessary
      logger.error("Failed to retrieve user accounts", e);
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
    }
  }
}
