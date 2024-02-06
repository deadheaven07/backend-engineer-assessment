package com.midas.app.workflows;

import com.midas.app.providers.external.stripe.StripePaymentProvider;
import com.midas.app.providers.payment.CreateAccount;
import io.temporal.workflow.WorkflowMethod;

public class StripeIntegrationWorkflowImpl extends StripeIntegrationWorkflow {

  private final StripePaymentProvider stripePaymentProvider; // Inject StripePaymentProvider.java

  public StripeIntegrationWorkflowImpl(StripePaymentProvider stripePaymentProvider) {
    this.stripePaymentProvider = stripePaymentProvider;
  }

  @Override
  @WorkflowMethod
  public void createStripeCustomer(String email) {
    // Create a CreateAccount object with the email
    CreateAccount createAccount = new CreateAccount();
    createAccount.setEmail(email);

    // Your logic to create a customer using StripePaymentProvider.java
    stripePaymentProvider.createAccount(createAccount);
  }
}
